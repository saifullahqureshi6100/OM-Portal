package com.example.data

import android.util.Log
import com.example.model.M3Part
import com.example.model.McqQuestion
import com.example.model.QuestionDifficulty
import com.example.model.SyllabusTopic
import com.example.model.TeachingMode
import com.example.model.TutorLanguage
import com.example.model.TutorMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

class AiTutorRepository {
    companion object {
        private const val TAG = "AiTutorRepository"
        private const val MODEL_NAME = "gemini-3.8-flash"
        private const val AI_MCQ_LABEL = "AI-Generated Practice (Gemini)"
    }

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = content {
                text(
                    """
                    You are Tuto-Jarvis, an expert ICMA Pakistan Managerial Level-1 (Paper M3: Organizational Management, 2025 Scheme) study tutor.
                    Official Syllabus & Weightages ONLY:
                    - Part A: Operations Management (35%): Enterprise management, PESTEL, Legal/ethical, Digital transformation, ESG, Mintzberg's Effective Organization, Strategic operations, Job/batch/flow production, 3D printing, Plant maintenance, Resource transformation, MRP, MRP II, ERP, OPT, Capacity management, TQM, Kaizen, Lean Production, TPM, TQMEX model, AI-driven quality control, Service quality, ISO standards (incl. ISO 56005).
                    - Part B: Human Resource Management (20%): Human capital theories, Employee motivation, Remote/hybrid work policies, Codes of conduct, Algorithmic recruitment bias, HR planning, Selection, Appraisals, Compensation, Training, Decruitment, EX platforms, Mental health analytics.
                    - Part C: Marketing Management (25%): Marketing philosophies, Marketing environment, Corporate vs. marketing strategy, Metaverse & virtual branding, Micro-influencer partnerships, Brand management, IMC, Consumer behavior, NPO marketing, Internal marketing, Social marketing, Web3/NFT campaigns.
                    - Part D: Performance Management (20%): Control systems, CSFs vs KPIs, Real-time IoT dashboards, Budgetary control, Financial & non-financial tools, Balanced Scorecard, Predictive analytics.

                    Strict Integrity Rules:
                    1. NEVER invent official chapters or claim unverified exam predictions.
                    2. NEVER label AI-generated MCQs as past-paper questions; always label generated MCQs as "AI-generated practice".
                    3. Explain clearly, concisely, and accurately with structured headings and exam-ready takeaways.
                    """.trimIndent()
                )
            }
        )
    }

    private val mcqSchema = Schema.obj(
        properties = mapOf(
            "questions" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "question" to Schema.string(description = "Clear exam-style MCQ stem"),
                        "options" to Schema.array(
                            items = Schema.string(),
                            description = "Exactly 4 distinct answer choices"
                        ),
                        "correctIndex" to Schema.integer(description = "0-based index (0 to 3) of the right option"),
                        "explanation" to Schema.string(description = "Concise conceptual explanation referencing ICMA M3 theory"),
                        "topicTitle" to Schema.string(description = "Matching official M3 topic title")
                    )
                ),
                description = "List of generated practice MCQs"
            )
        )
    )

    private val mcqModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = mcqSchema
            }
        )
    }

    fun streamTutorReply(
        userQuery: String,
        detectedPart: M3Part,
        mode: TeachingMode,
        language: TutorLanguage,
        contextTopic: SyllabusTopic?,
        userNotesSnippet: String?,
        conversationHistory: List<TutorMessage> = emptyList()
    ): Flow<String> = flow {
        val langInstruction = when (language) {
            TutorLanguage.ENGLISH -> "Respond in clear academic English."
            TutorLanguage.BILINGUAL_URDU -> "Respond in English with key explanations and memory summaries also clarified in easy Urdu (Nastaliq or Roman Urdu terms in brackets)."
            TutorLanguage.ROMAN_URDU -> "Respond in friendly, clear Roman Urdu while keeping official ICMA M3 English terminology intact."
        }
        val topicContext = contextTopic?.let {
            "Focused Syllabus Topic: ${it.title} (${it.part.code}: ${it.part.title}, ${it.part.weightPercent}% weight). Framework: ${it.keyFramework}."
        } ?: "Detected Syllabus Area: ${detectedPart.code} — ${detectedPart.title} (${detectedPart.weightPercent}% weight)."

        val notesContext = if (!userNotesSnippet.isNullOrBlank()) {
            "\nStudent's uploaded/pasted notes excerpt:\n${userNotesSnippet.take(1200)}"
        } else ""

        val prompt = """
            $topicContext
            Teaching Mode: ${mode.label} (${mode.promptHint})
            Language Mode: $langInstruction
            $notesContext

            Student Request:
            $userQuery
        """.trimIndent()

        // Build alternating user/model turn history for multi-turn Firebase AI Logic chat session
        val geminiHistory = buildGeminiHistory(conversationHistory)

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                val chat = chatModel.startChat(history = geminiHistory)
                chat.sendMessageStream(prompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        accumulated.append(chunkText)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("Gemini returned an empty response.")
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Tutor stream timed out", e)
            throw e
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Tutor stream error", e)
            throw e
        }
    }

    private fun buildGeminiHistory(messages: List<TutorMessage>): List<Content> {
        val validMessages = messages
            .filter { it.id != "welcome" && it.text.isNotBlank() && it.text != "Thinking..." }
            .takeLast(12)

        val turns = mutableListOf<Content>()
        var lastRole: String? = null
        for (msg in validMessages) {
            val role = if (msg.fromUser) "user" else "model"
            // Ensure history begins with a user message and alternates roles cleanly
            if (turns.isEmpty() && role != "user") continue
            if (role == lastRole) continue
            turns += content(role = role) { text(msg.text) }
            lastRole = role
        }
        // Ensure history ends on a model turn before sending the next user turn
        if (turns.isNotEmpty() && lastRole == "user") {
            turns.removeAt(turns.lastIndex)
        }
        return turns
    }

    suspend fun generateAiMcqs(
        part: M3Part,
        topic: SyllabusTopic?,
        difficulty: QuestionDifficulty,
        count: Int
    ): Result<List<McqQuestion>> {
        return try {
            val topicScope = topic?.let {
                "Topic: '${it.title}' (${it.subtitle}). Key concepts: ${it.keyConcepts.joinToString("; ")}"
            } ?: "Part: ${part.code} — ${part.title} (covering official M3 2025 topics: ${SyllabusRepository.getTopicsForPart(part).joinToString(", ") { t -> t.title }})"

            val prompt = """
                Generate $count original ICMA Pakistan ML-1 Paper M3 (Organizational Management, 2025 Scheme) practice MCQs.
                $topicScope
                Difficulty: ${difficulty.label}.
                Each question MUST have exactly 4 distinct options, a valid correctIndex (0..3), and a crisp explanation.
                Do not claim these are past paper questions; they are AI-generated practice items.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                mcqModel.generateContent(prompt)
            }
            val rawJson = response.text ?: throw IllegalStateException("Empty JSON response from Gemini.")
            val root = JSONObject(rawJson)
            val arr = root.getJSONArray("questions")
            val parsed = (0 until arr.length()).mapNotNull { idx ->
                val obj = arr.getJSONObject(idx)
                val qText = obj.optString("question").trim()
                val optsArr = obj.optJSONArray("options") ?: return@mapNotNull null
                val options = (0 until optsArr.length()).map { optsArr.getString(it).trim() }.take(4)
                if (qText.isBlank() || options.size < 4) return@mapNotNull null
                val correctIdx = obj.optInt("correctIndex", 0).coerceIn(0, 3)
                val explanation = obj.optString("explanation").trim().ifBlank { "Review the official ICMA M3 study concepts for this topic." }
                val matchedTopic = topic ?: SyllabusRepository.getTopicsForPart(part).firstOrNull {
                    it.title.equals(obj.optString("topicTitle"), ignoreCase = true)
                } ?: SyllabusRepository.getTopicsForPart(part).first()

                McqQuestion(
                    id = "ai_${System.currentTimeMillis()}_$idx",
                    part = part,
                    topicId = matchedTopic.id,
                    topicTitle = matchedTopic.title,
                    difficulty = difficulty,
                    question = qText,
                    options = options,
                    correctIndex = correctIdx,
                    explanation = explanation,
                    sourceLabel = AI_MCQ_LABEL,
                    isPastPaper = false
                )
            }
            if (parsed.isEmpty()) {
                Result.failure(IllegalStateException("No valid MCQs parsed from AI output."))
            } else {
                Result.success(parsed)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI MCQ generation timed out", e)
            Result.failure(Exception("AI MCQ generation timed out after 60s. Please retry or use the Offline Bank."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI MCQ generation failed", e)
            Result.failure(e)
        }
    }

    /**
     * Honest, clearly-labeled Offline Syllabus Tutor fallback built strictly from the embedded M3 knowledge base.
     */
    fun buildOfflineSyllabusReply(
        query: String,
        detectedPart: M3Part,
        mode: TeachingMode,
        language: TutorLanguage,
        contextTopic: SyllabusTopic?
    ): String {
        val matchedTopic = contextTopic ?: SyllabusRepository.allTopics.maxByOrNull { topic ->
            val lower = query.lowercase()
            var score = if (lower.contains(topic.title.lowercase())) 10 else 0
            topic.title.lowercase().split(" ", "/", ",").filter { it.length >= 4 }.forEach { w ->
                if (lower.contains(w)) score += 3
            }
            if (topic.part == detectedPart) score += 1
            score
        } ?: SyllabusRepository.getTopicsForPart(detectedPart).first()

        val header = "[Offline Syllabus Tutor — ${matchedTopic.part.code}: ${matchedTopic.part.title} (${matchedTopic.part.weightPercent}%)]\nTopic: ${matchedTopic.title} (${matchedTopic.keyFramework})"

        val body = when (mode) {
            TeachingMode.QUICK_REVISION -> buildString {
                appendLine("Quick Revision Hooks:")
                matchedTopic.revisionNotes.forEach { appendLine("• $it") }
                appendLine("\nCore Concepts:")
                matchedTopic.keyConcepts.take(2).forEach { appendLine("• $it") }
            }
            TeachingMode.MCQ_PRACTICE -> {
                val sample = QuestionBank.getQuestionsForPractice(matchedTopic.part, matchedTopic.id, null, 1).firstOrNull()
                if (sample != null) {
                    val letters = listOf("A", "B", "C", "D")
                    buildString {
                        appendLine("[${sample.sourceLabel}]")
                        appendLine(sample.question)
                        sample.options.forEachIndexed { i, opt -> appendLine("${letters[i]}) $opt") }
                        appendLine("\nAnswer: ${letters[sample.correctIndex]} — ${sample.explanation}")
                    }
                } else {
                    "Practice Prompt: Define ${matchedTopic.title} and list 2 exam applications.\n• ${matchedTopic.definition}"
                }
            }
            TeachingMode.ASK_ME_QUESTIONS -> buildString {
                appendLine("Active Recall Check on ${matchedTopic.title}:")
                appendLine("1) Explain ${matchedTopic.keyFramework} in your own words without looking at notes.")
                appendLine("2) Give one practical organizational example where ${matchedTopic.title} improves decisions.")
                appendLine("\nCheck Hint: ${matchedTopic.revisionNotes.firstOrNull() ?: matchedTopic.definition}")
            }
            TeachingMode.CASE_STUDY -> buildString {
                appendLine("Case Application Guide (${matchedTopic.title}):")
                appendLine("• Concept Anchor: ${matchedTopic.definition}")
                appendLine("• Scenario Examples:")
                matchedTopic.examples.forEach { appendLine("  - $it") }
                appendLine("• Exam Tip: Tie your diagnosis directly to ${matchedTopic.keyFramework} and justify trade-offs.")
            }
            else -> buildString {
                appendLine(matchedTopic.definition)
                appendLine("\nKey Points (${mode.label}):")
                matchedTopic.keyConcepts.forEach { appendLine("• $it") }
                appendLine("\nExample:")
                matchedTopic.examples.firstOrNull()?.let { appendLine("• $it") }
                appendLine("\nExam Revision Note:")
                matchedTopic.revisionNotes.forEach { appendLine("• $it") }
            }
        }

        val langNote = when (language) {
            TutorLanguage.ROMAN_URDU -> "\n\n[Roman Urdu Khulasa]: Is topic (${matchedTopic.title}) ka main maqsad yeh hai ke management ${matchedTopic.keyFramework} ke zariye behtar operational aur strategic decisions le sake. Exam mein framework ke exact points zaroor likhein."
            TutorLanguage.BILINGUAL_URDU -> "\n\n[Urdu / Roman Summary]: بنیادی خلاصہ — ${matchedTopic.title} (${matchedTopic.keyFramework}) آپ کو امتحان میں منظم تجزیہ کرنے میں مدد دیتا ہے۔"
            TutorLanguage.ENGLISH -> ""
        }

        return "$header\n\n$body$langNote"
    }
}
