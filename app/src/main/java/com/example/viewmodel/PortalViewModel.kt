package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiTutorRepository
import com.example.data.FirestoreRepository
import com.example.data.QuestionBank
import com.example.data.SyllabusRepository
import com.example.domain.BackupJsonSerializer
import com.example.domain.SrsAndPlannerEngine
import com.example.model.ExamAttempt
import com.example.model.LeaderboardEntry
import com.example.model.M3Part
import com.example.model.MasteryState
import com.example.model.McqQuestion
import com.example.model.QuestionDifficulty
import com.example.model.SrsRating
import com.example.model.StudyPlanSummary
import com.example.model.SyllabusTopic
import com.example.model.TeachingMode
import com.example.model.TopicProgress
import com.example.model.TutorLanguage
import com.example.model.TutorMessage
import com.example.model.UiState
import com.example.model.UserProfile
import com.example.voice.VoiceTutorManager
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class MockExamSessionState(
    val active: Boolean = false,
    val submitted: Boolean = false,
    val questionCount: Int = 20,
    val questions: List<McqQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val flaggedIds: Set<String> = emptySet(),
    val secondsRemaining: Int = 0,
    val totalDurationSeconds: Int = 0,
    val lastAttemptResult: ExamAttempt? = null,
    val newlyUnlockedBadges: List<String> = emptyList()
)

data class McqPracticeState(
    val questions: List<McqQuestion> = emptyList(),
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val isGeneratingAi: Boolean = false,
    val errorMessage: String? = null
)

data class TutorUiState(
    val messages: List<TutorMessage> = listOf(
        TutorMessage(
            id = "welcome",
            fromUser = false,
            text = "Assalam-o-Alaikum! I am Tuto-Jarvis, your ICMA Pakistan ML-1 Paper M3 (2025 Scheme) study partner. Ask about any Operations (35%), HR (20%), Marketing (25%), or Performance (20%) concept, or pick a topic from Study.",
            detectedPart = M3Part.PART_A,
            modeLabel = "Ready",
            isOfflineResponse = false
        )
    ),
    val isStreaming: Boolean = false,
    val useOfflineMode: Boolean = false,
    val errorMessage: String? = null,
    val lastFailedQuery: String? = null,
    val focusedTopicId: String? = null
)

class PortalViewModel(
    private val repository: FirestoreRepository,
    private val aiRepository: AiTutorRepository,
    val voiceManager: VoiceTutorManager,
    private val currentUserId: String,
    initialDisplayName: String
) : ViewModel() {

    companion object {
        private const val TAG = "PortalViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }

    init {
        viewModelScope.launch {
            repository.ensureProfileInitialized(currentUserId, initialDisplayName)
        }
    }

    // Two-tier Flow error unwrapping preserving CancellationException
    private fun <T> unwrapFirestoreErrors(
        source: kotlinx.coroutines.flow.Flow<T>,
        fallbackMsg: String
    ): StateFlow<UiState<T>> =
        source.map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, fallbackMsg, root)
                emit(UiState.Error(root.localizedMessage ?: fallbackMsg))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val profileState: StateFlow<UiState<UserProfile>> = unwrapFirestoreErrors(
        repository.observeProfile(currentUserId).map { it ?: UserProfile(userId = currentUserId) },
        "Failed to load profile"
    )

    val topicProgressState: StateFlow<UiState<Map<String, TopicProgress>>> = unwrapFirestoreErrors(
        repository.observeTopicProgress(currentUserId),
        "Failed to load topic progress"
    )

    val examHistoryState: StateFlow<UiState<List<ExamAttempt>>> = unwrapFirestoreErrors(
        repository.observeExamHistory(currentUserId),
        "Failed to load exam history"
    )

    val leaderboardState: StateFlow<UiState<List<LeaderboardEntry>>> = unwrapFirestoreErrors(
        repository.observeOptedInLeaderboard(),
        "Failed to load leaderboard"
    )

    private val _crashPreview = MutableStateFlow(false)
    val crashPreview: StateFlow<Boolean> = _crashPreview.asStateFlow()

    val studyPlanState: StateFlow<StudyPlanSummary> = combine(
        profileState,
        topicProgressState,
        examHistoryState,
        _crashPreview
    ) { pState, tState, eState, forceCrash ->
        val profile = (pState as? UiState.Success)?.data ?: UserProfile(userId = currentUserId)
        val progress = (tState as? UiState.Success)?.data ?: emptyMap()
        val history = (eState as? UiState.Success)?.data ?: emptyList()
        SrsAndPlannerEngine.buildStudyPlan(profile, progress, history, forceCrash)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        SrsAndPlannerEngine.buildStudyPlan(UserProfile(userId = currentUserId), emptyMap(), emptyList())
    )

    private val _selectedPart = MutableStateFlow<M3Part?>(null)
    val selectedPart: StateFlow<M3Part?> = _selectedPart.asStateFlow()

    private val _selectedTopicId = MutableStateFlow<String?>(null)
    val selectedTopicId: StateFlow<String?> = _selectedTopicId.asStateFlow()

    private val _srsQueue = MutableStateFlow<List<SyllabusTopic>>(emptyList())
    val srsQueue: StateFlow<List<SyllabusTopic>> = _srsQueue.asStateFlow()

    private val _srsSessionActive = MutableStateFlow(false)
    val srsSessionActive: StateFlow<Boolean> = _srsSessionActive.asStateFlow()

    private val _tutorState = MutableStateFlow(TutorUiState())
    val tutorState: StateFlow<TutorUiState> = _tutorState.asStateFlow()

    private val _mcqPracticeState = MutableStateFlow(
        McqPracticeState(questions = QuestionBank.getQuestionsForPractice(null, null, null, 8))
    )
    val mcqPracticeState: StateFlow<McqPracticeState> = _mcqPracticeState.asStateFlow()

    private val _mockExamState = MutableStateFlow(MockExamSessionState())
    val mockExamState: StateFlow<MockExamSessionState> = _mockExamState.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    private val _backupBusy = MutableStateFlow(false)
    val backupBusy: StateFlow<Boolean> = _backupBusy.asStateFlow()

    private var timerJob: Job? = null
    private var tutorStreamJob: Job? = null

    private fun currentProfile(): UserProfile =
        (profileState.value as? UiState.Success)?.data ?: UserProfile(userId = currentUserId)

    private fun currentProgressMap(): Map<String, TopicProgress> =
        (topicProgressState.value as? UiState.Success)?.data ?: emptyMap()

    private fun currentExamHistory(): List<ExamAttempt> =
        (examHistoryState.value as? UiState.Success)?.data ?: emptyList()

    fun dismissBanner() {
        _bannerMessage.value = null
    }

    fun selectPart(part: M3Part?) {
        if (_selectedPart.value != part) {
            _selectedTopicId.value = null
        }
        _selectedPart.value = part
    }

    fun selectTopic(topicId: String?) {
        _selectedTopicId.value = topicId
        if (topicId != null) {
            SyllabusRepository.getTopicById(topicId)?.let { topic ->
                _selectedPart.value = topic.part
            }
        }
    }

    fun toggleCrashPreview() {
        _crashPreview.value = !_crashPreview.value
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val updated = currentProfile().copy(darkModeEnabled = enabled)
            repository.updateProfile(currentUserId, updated)
        }
    }

    fun updatePlannerSettings(examDateMillis: Long, dailyHours: Double, daysPerWeek: Int) {
        viewModelScope.launch {
            val updated = currentProfile().copy(
                examDateMillis = examDateMillis,
                dailyTargetHours = dailyHours.coerceIn(0.5, 16.0),
                studyDaysPerWeek = daysPerWeek.coerceIn(1, 7)
            )
            repository.updateProfile(currentUserId, updated).onSuccess {
                _bannerMessage.value = "Study plan updated for exam target."
            }
        }
    }

    fun updateProfilePreferences(
        displayName: String,
        leaderboardOptIn: Boolean,
        voiceEnabled: Boolean
    ) {
        viewModelScope.launch {
            val updated = currentProfile().copy(
                displayName = displayName.trim().ifBlank { "ICMA Candidate" }.take(50),
                leaderboardOptIn = leaderboardOptIn,
                voiceEnabled = voiceEnabled
            )
            repository.updateProfile(currentUserId, updated).onSuccess {
                val mastered = currentProgressMap().values.count { it.masteryState == MasteryState.MASTERED.name }
                repository.syncLeaderboard(currentUserId, updated, mastered)
                _bannerMessage.value = "Account settings saved."
            }.onFailure {
                _bannerMessage.value = it.localizedMessage ?: "Failed to save settings."
            }
        }
    }

    fun toggleTopicBookmark(topic: SyllabusTopic) {
        viewModelScope.launch {
            val existing = currentProgressMap()[topic.id] ?: TopicProgress(
                userId = currentUserId,
                partId = topic.part.id
            )
            val updated = existing.copy(bookmarked = !existing.bookmarked)
            repository.upsertTopicProgress(currentUserId, topic.id, topic.part, updated)
        }
    }

    fun toggleTopicCompleted(topic: SyllabusTopic) {
        viewModelScope.launch {
            val progressMap = currentProgressMap().toMutableMap()
            val existing = progressMap[topic.id] ?: TopicProgress(
                userId = currentUserId,
                partId = topic.part.id
            )
            val nowCompleted = !existing.completed
            val nextMastery = if (nowCompleted && existing.masteryState == MasteryState.NOT_STARTED.name) {
                MasteryState.LEARNING.name
            } else if (!nowCompleted && existing.masteryState == MasteryState.LEARNING.name) {
                MasteryState.NOT_STARTED.name
            } else existing.masteryState

            val updatedTopic = existing.copy(
                completed = nowCompleted,
                masteryState = nextMastery,
                studyMinutes = existing.studyMinutes + if (nowCompleted) 15 else 0
            )
            repository.upsertTopicProgress(currentUserId, topic.id, topic.part, updatedTopic)

            if (nowCompleted) {
                progressMap[topic.id] = updatedTopic
                awardXpAndEvaluateBadges(
                    xpGain = 50L,
                    minutesGain = 15L,
                    srsIncrement = 0L,
                    learnedTopicId = topic.id,
                    updatedProgressMap = progressMap
                )
            }
        }
    }

    fun saveTopicUserNotes(topic: SyllabusTopic, notes: String) {
        viewModelScope.launch {
            val existing = currentProgressMap()[topic.id] ?: TopicProgress(
                userId = currentUserId,
                partId = topic.part.id
            )
            val updated = existing.copy(
                userNotes = notes.take(10_000),
                masteryState = if (existing.masteryState == MasteryState.NOT_STARTED.name && notes.isNotBlank()) {
                    MasteryState.LEARNING.name
                } else existing.masteryState
            )
            repository.upsertTopicProgress(currentUserId, topic.id, topic.part, updated).onSuccess {
                _bannerMessage.value = "Study notes saved to your account."
            }
        }
    }

    fun startSrsReviewSession() {
        val queue = SrsAndPlannerEngine.buildSrsReviewQueue(
            SyllabusRepository.allTopics,
            currentProgressMap()
        ).take(12)
        _srsQueue.value = queue
        _srsSessionActive.value = true
    }

    fun closeSrsReviewSession() {
        _srsSessionActive.value = false
    }

    fun rateTopicSrs(topic: SyllabusTopic, rating: SrsRating) {
        viewModelScope.launch {
            val progressMap = currentProgressMap().toMutableMap()
            val existing = progressMap[topic.id] ?: TopicProgress(
                userId = currentUserId,
                partId = topic.part.id
            )
            val next = SrsAndPlannerEngine.applySrsRating(existing, rating)
            repository.upsertTopicProgress(currentUserId, topic.id, topic.part, next)
            progressMap[topic.id] = next

            val xp = when (rating) {
                SrsRating.AGAIN -> 10L
                SrsRating.HARD -> 20L
                SrsRating.GOOD -> 30L
                SrsRating.EASY -> 35L
            }
            awardXpAndEvaluateBadges(
                xpGain = xp,
                minutesGain = 5L,
                srsIncrement = 1L,
                learnedTopicId = topic.id,
                updatedProgressMap = progressMap
            )

            // Pop rated item from active SRS deck
            val remaining = _srsQueue.value.drop(1)
            _srsQueue.value = remaining
            if (remaining.isEmpty() && _srsSessionActive.value) {
                _srsSessionActive.value = false
                _bannerMessage.value = "Spaced repetition deck complete! Intervals updated."
            }
        }
    }

    fun logPlannerBlockCompleted(topicId: String, durationMinutes: Int) {
        val topic = SyllabusRepository.getTopicById(topicId) ?: return
        viewModelScope.launch {
            val progressMap = currentProgressMap().toMutableMap()
            val existing = progressMap[topic.id] ?: TopicProgress(
                userId = currentUserId,
                partId = topic.part.id
            )
            val updated = existing.copy(
                studyMinutes = existing.studyMinutes + durationMinutes,
                masteryState = if (existing.masteryState == MasteryState.NOT_STARTED.name) MasteryState.LEARNING.name else existing.masteryState
            )
            repository.upsertTopicProgress(currentUserId, topic.id, topic.part, updated)
            progressMap[topic.id] = updated
            awardXpAndEvaluateBadges(
                xpGain = 30L,
                minutesGain = durationMinutes.toLong(),
                srsIncrement = 0L,
                learnedTopicId = topic.id,
                updatedProgressMap = progressMap
            )
            _bannerMessage.value = "Logged ${durationMinutes}m for ${topic.title} (+30 XP)."
        }
    }

    // --- Tuto-Jarvis AI & Offline Tutor ---

    fun setTutorMode(mode: TeachingMode) {
        viewModelScope.launch {
            repository.updateProfile(currentUserId, currentProfile().copy(preferredTeachingMode = mode.label))
        }
    }

    fun setTutorLanguage(language: TutorLanguage) {
        viewModelScope.launch {
            repository.updateProfile(currentUserId, currentProfile().copy(preferredLanguage = language.id))
        }
    }

    fun setTutorOfflineMode(offline: Boolean) {
        _tutorState.update { it.copy(useOfflineMode = offline, errorMessage = null) }
    }

    fun focusTutorOnTopic(topicId: String?) {
        _tutorState.update { it.copy(focusedTopicId = topicId) }
    }

    fun clearTutorChat() {
        tutorStreamJob?.cancel()
        _tutorState.update { state ->
            TutorUiState(
                useOfflineMode = state.useOfflineMode,
                focusedTopicId = state.focusedTopicId
            )
        }
    }

    fun retryLastTutorMessage() {
        val failed = _tutorState.value.lastFailedQuery ?: return
        sendTutorMessage(failed)
    }

    fun sendTutorMessage(rawQuery: String) {
        val query = rawQuery.trim()
        if (query.isBlank() || _tutorState.value.isStreaming) return

        val profile = currentProfile()
        val mode = TeachingMode.fromLabel(profile.preferredTeachingMode)
        val language = TutorLanguage.fromId(profile.preferredLanguage)
        val contextTopic = _tutorState.value.focusedTopicId?.let { SyllabusRepository.getTopicById(it) }
        val detectedPart = contextTopic?.part ?: SyllabusRepository.detectPartFromText(query)
        val userNotesSnippet = contextTopic?.let { currentProgressMap()[it.id]?.userNotes }
        val priorHistory = _tutorState.value.messages

        val userMsg = TutorMessage(
            id = "u_${System.currentTimeMillis()}",
            fromUser = true,
            text = query,
            detectedPart = detectedPart
        )
        val replyId = "a_${System.currentTimeMillis()}"

        _tutorState.update {
            it.copy(
                messages = it.messages + userMsg,
                isStreaming = true,
                errorMessage = null,
                lastFailedQuery = null
            )
        }

        tutorStreamJob?.cancel()
        tutorStreamJob = viewModelScope.launch {
            if (_tutorState.value.useOfflineMode) {
                val offlineText = aiRepository.buildOfflineSyllabusReply(query, detectedPart, mode, language, contextTopic)
                val botMsg = TutorMessage(
                    id = replyId,
                    fromUser = false,
                    text = offlineText,
                    detectedPart = detectedPart,
                    modeLabel = mode.label,
                    isOfflineResponse = true
                )
                _tutorState.update { it.copy(messages = it.messages + botMsg, isStreaming = false) }
                trackLearnedTopic(contextTopic?.id)
                return@launch
            }

            try {
                // Seed streaming bubble
                _tutorState.update {
                    it.copy(
                        messages = it.messages + TutorMessage(
                            id = replyId,
                            fromUser = false,
                            text = "Thinking...",
                            detectedPart = detectedPart,
                            modeLabel = mode.label,
                            isOfflineResponse = false
                        )
                    )
                }
                aiRepository.streamTutorReply(
                    userQuery = query,
                    detectedPart = detectedPart,
                    mode = mode,
                    language = language,
                    contextTopic = contextTopic,
                    userNotesSnippet = userNotesSnippet,
                    conversationHistory = priorHistory
                ).collect { partial ->
                    _tutorState.update { state ->
                        state.copy(
                            messages = state.messages.map { m ->
                                if (m.id == replyId) m.copy(text = partial) else m
                            }
                        )
                    }
                }
                _tutorState.update { it.copy(isStreaming = false, lastFailedQuery = null) }
                trackLearnedTopic(contextTopic?.id)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini tutor request timed out", e)
                _tutorState.update { state ->
                    state.copy(
                        messages = state.messages.filterNot { it.id == replyId },
                        isStreaming = false,
                        lastFailedQuery = query,
                        errorMessage = "Tuto-Jarvis timed out after 60 seconds. Tap Retry or switch to Offline Tutor."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini tutor request failed", e)
                _tutorState.update { state ->
                    state.copy(
                        messages = state.messages.filterNot { it.id == replyId },
                        isStreaming = false,
                        lastFailedQuery = query,
                        errorMessage = e.localizedMessage ?: "AI tutor request failed. Tap Retry or switch to Offline Tutor."
                    )
                }
            }
        }
    }

    private fun trackLearnedTopic(topicId: String?) {
        if (topicId.isNullOrBlank()) return
        viewModelScope.launch {
            val profile = currentProfile()
            if (!profile.learnedTopics.contains(topicId)) {
                val updated = profile.copy(
                    learnedTopics = (profile.learnedTopics + topicId).distinct().take(60),
                    xp = profile.xp + 15L
                )
                repository.updateProfile(currentUserId, updated)
            }
        }
    }

    // --- Intelligent MCQ Practice ---

    fun loadOfflineMcqs(part: M3Part?, topicId: String?, difficulty: QuestionDifficulty?, count: Int = 8) {
        val list = QuestionBank.getQuestionsForPractice(part, topicId, difficulty, count)
        _mcqPracticeState.value = McqPracticeState(questions = list)
    }

    fun generateAiMcqs(part: M3Part, topic: SyllabusTopic?, difficulty: QuestionDifficulty, count: Int = 5) {
        viewModelScope.launch {
            _mcqPracticeState.update { it.copy(isGeneratingAi = true, errorMessage = null) }
            aiRepository.generateAiMcqs(part, topic, difficulty, count)
                .onSuccess { generated ->
                    _mcqPracticeState.value = McqPracticeState(questions = generated)
                }
                .onFailure { err ->
                    _mcqPracticeState.update {
                        it.copy(
                            isGeneratingAi = false,
                            errorMessage = err.localizedMessage ?: "Failed to generate AI MCQs."
                        )
                    }
                }
        }
    }

    fun answerPracticeMcq(question: McqQuestion, optionIndex: Int) {
        if (_mcqPracticeState.value.selectedAnswers.containsKey(question.id)) return
        _mcqPracticeState.update {
            it.copy(selectedAnswers = it.selectedAnswers + (question.id to optionIndex))
        }
        val isCorrect = optionIndex == question.correctIndex
        viewModelScope.launch {
            val progressMap = currentProgressMap().toMutableMap()
            val existing = progressMap[question.topicId] ?: TopicProgress(
                userId = currentUserId,
                partId = question.part.id
            )
            val updated = SrsAndPlannerEngine.applyQuizOutcome(existing, isCorrect)
            repository.upsertTopicProgress(currentUserId, question.topicId, question.part, updated)
            progressMap[question.topicId] = updated

            awardXpAndEvaluateBadges(
                xpGain = if (isCorrect) 15L else 5L,
                minutesGain = 1L,
                srsIncrement = 0L,
                learnedTopicId = question.topicId,
                updatedProgressMap = progressMap
            )
        }
    }

    // --- Full Weighted Mock Exam (35 / 20 / 25 / 20) ---

    fun startMockExam(questionCount: Int) {
        val questions = QuestionBank.buildWeightedMockExam(questionCount)
        val totalSeconds = (questionCount * 90).coerceAtLeast(600)
        _mockExamState.value = MockExamSessionState(
            active = true,
            submitted = false,
            questionCount = questionCount,
            questions = questions,
            currentIndex = 0,
            selectedAnswers = emptyMap(),
            flaggedIds = emptySet(),
            secondsRemaining = totalSeconds,
            totalDurationSeconds = totalSeconds
        )
        startExamTimer()
    }

    private fun startExamTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_mockExamState.value.active && !_mockExamState.value.submitted) {
                delay(1000L)
                val next = _mockExamState.value.secondsRemaining - 1
                if (next <= 0) {
                    _mockExamState.update { it.copy(secondsRemaining = 0) }
                    submitMockExam()
                    break
                } else {
                    _mockExamState.update { it.copy(secondsRemaining = next) }
                }
            }
        }
    }

    fun selectMockAnswer(questionId: String, optionIndex: Int) {
        if (_mockExamState.value.submitted) return
        _mockExamState.update {
            it.copy(selectedAnswers = it.selectedAnswers + (questionId to optionIndex))
        }
    }

    fun toggleMockFlag(questionId: String) {
        _mockExamState.update { state ->
            val updated = if (state.flaggedIds.contains(questionId)) state.flaggedIds - questionId else state.flaggedIds + questionId
            state.copy(flaggedIds = updated)
        }
    }

    fun jumpToMockQuestion(index: Int) {
        val maxIdx = (_mockExamState.value.questions.size - 1).coerceAtLeast(0)
        _mockExamState.update { it.copy(currentIndex = index.coerceIn(0, maxIdx)) }
    }

    fun submitMockExam() {
        val session = _mockExamState.value
        if (!session.active || session.submitted) return
        timerJob?.cancel()

        val questions = session.questions
        var correct = 0
        var incorrect = 0
        var skipped = 0
        val partCorrect = mutableMapOf<M3Part, Int>()
        val partTotal = mutableMapOf<M3Part, Int>()
        val weakTopicIds = mutableListOf<String>()

        questions.forEach { q ->
            partTotal[q.part] = (partTotal[q.part] ?: 0) + 1
            val chosen = session.selectedAnswers[q.id]
            when {
                chosen == null -> {
                    skipped++
                    weakTopicIds += q.topicId
                }
                chosen == q.correctIndex -> {
                    correct++
                    partCorrect[q.part] = (partCorrect[q.part] ?: 0) + 1
                }
                else -> {
                    incorrect++
                    weakTopicIds += q.topicId
                }
            }
        }

        val totalQ = questions.size.coerceAtLeast(1)
        val scorePct = ((correct * 100.0) / totalQ).roundToInt()
        val elapsedSeconds = (session.totalDurationSeconds - session.secondsRemaining).coerceAtLeast(1)
        val xpEarned = 100 + (scorePct * 2) + if (scorePct >= 80) 150 else 0

        val attempt = ExamAttempt(
            userId = currentUserId,
            mode = "MOCK_EXAM",
            totalQuestions = totalQ,
            correctCount = correct,
            incorrectCount = incorrect,
            skippedCount = skipped,
            scorePercent = scorePct,
            durationSeconds = elapsedSeconds,
            xpEarned = xpEarned,
            partACorrect = partCorrect[M3Part.PART_A] ?: 0,
            partATotal = partTotal[M3Part.PART_A] ?: 0,
            partBCorrect = partCorrect[M3Part.PART_B] ?: 0,
            partBTotal = partTotal[M3Part.PART_B] ?: 0,
            partCCorrect = partCorrect[M3Part.PART_C] ?: 0,
            partCTotal = partTotal[M3Part.PART_C] ?: 0,
            partDCorrect = partCorrect[M3Part.PART_D] ?: 0,
            partDTotal = partTotal[M3Part.PART_D] ?: 0,
            weakTopicIds = weakTopicIds.distinct().take(56)
        )

        viewModelScope.launch {
            repository.saveExamAttempt(currentUserId, attempt)

            // Update SRS & topic mastery based on exam results
            val progressMap = currentProgressMap().toMutableMap()
            questions.groupBy { it.topicId }.forEach { (topicId, qList) ->
                val topicPart = qList.first().part
                var p = progressMap[topicId] ?: TopicProgress(userId = currentUserId, partId = topicPart.id)
                qList.forEach { q ->
                    val chosen = session.selectedAnswers[q.id]
                    if (chosen != null) {
                        p = SrsAndPlannerEngine.applyQuizOutcome(p, chosen == q.correctIndex)
                    }
                }
                progressMap[topicId] = p
                repository.upsertTopicProgress(currentUserId, topicId, topicPart, p)
            }

            val updatedHistory = listOf(attempt) + currentExamHistory()
            val unlocked = awardXpAndEvaluateBadges(
                xpGain = xpEarned.toLong(),
                minutesGain = (elapsedSeconds / 60L).coerceAtLeast(1L),
                srsIncrement = 0L,
                learnedTopicId = null,
                updatedProgressMap = progressMap,
                overrideExamHistory = updatedHistory
            )

            _mockExamState.update {
                it.copy(
                    submitted = true,
                    lastAttemptResult = attempt,
                    newlyUnlockedBadges = unlocked
                )
            }
        }
    }

    fun exitMockExam() {
        timerJob?.cancel()
        _mockExamState.value = MockExamSessionState()
    }

    private suspend fun awardXpAndEvaluateBadges(
        xpGain: Long,
        minutesGain: Long,
        srsIncrement: Long,
        learnedTopicId: String?,
        updatedProgressMap: Map<String, TopicProgress>,
        overrideExamHistory: List<ExamAttempt>? = null
    ): List<String> {
        val current = currentProfile()
        val (nextStreak, todayIso) = SrsAndPlannerEngine.computeUpdatedStreak(current.streakDays, current.lastStudyDate)
        val nextLearned = if (learnedTopicId != null) {
            (current.learnedTopics + learnedTopicId).distinct().take(60)
        } else current.learnedTopics

        val intermediate = current.copy(
            xp = current.xp + xpGain,
            streakDays = nextStreak,
            lastStudyDate = todayIso,
            totalStudyMinutes = current.totalStudyMinutes + minutesGain,
            srsReviewsCount = current.srsReviewsCount + srsIncrement,
            learnedTopics = nextLearned
        )
        val history = overrideExamHistory ?: currentExamHistory()
        val (allBadges, newlyUnlocked) = SrsAndPlannerEngine.evaluateBadges(intermediate, updatedProgressMap, history)
        val bonusXp = newlyUnlocked.sumOf { id ->
            SyllabusRepository.badges.firstOrNull { it.id == id }?.bonusXp?.toLong() ?: 0L
        }
        val finalProfile = intermediate.copy(
            xp = intermediate.xp + bonusXp,
            earnedBadges = allBadges
        )
        repository.updateProfile(currentUserId, finalProfile)
        val masteredCount = updatedProgressMap.values.count { it.masteryState == MasteryState.MASTERED.name }
        repository.syncLeaderboard(currentUserId, finalProfile, masteredCount)
        if (newlyUnlocked.isNotEmpty()) {
            val names = newlyUnlocked.mapNotNull { id -> SyllabusRepository.badges.firstOrNull { it.id == id }?.title }
            _bannerMessage.value = "Badge unlocked: ${names.joinToString(", ")}!"
        }
        return newlyUnlocked
    }

    fun buildBackupJson(): String {
        return BackupJsonSerializer.serializeBackup(
            profile = currentProfile(),
            progressMap = currentProgressMap(),
            examHistory = currentExamHistory()
        )
    }

    fun importBackupJson(rawJson: String) {
        if (_backupBusy.value) return
        viewModelScope.launch {
            _backupBusy.value = true
            try {
                val parsedResult = BackupJsonSerializer.parseAndSanitizeBackup(
                    rawJson = rawJson,
                    targetUserId = currentUserId,
                    currentProfile = currentProfile()
                )
                val parsed = parsedResult.getOrElse { err ->
                    _bannerMessage.value = err.message ?: "Failed to read backup JSON."
                    return@launch
                }

                val mergedMap = currentProgressMap().toMutableMap()
                parsed.topics.forEach { imported ->
                    val official = SyllabusRepository.getTopicById(imported.id) ?: return@forEach
                    repository.upsertTopicProgress(currentUserId, imported.id, official.part, imported)
                    mergedMap[imported.id] = imported
                }

                val existingSignatures = currentExamHistory().map {
                    "${it.mode}|${it.totalQuestions}|${it.scorePercent}|${it.durationSeconds}|${it.partACorrect}|${it.partBCorrect}|${it.partCCorrect}|${it.partDCorrect}"
                }.toMutableSet()
                var importedExams = 0
                parsed.examHistory.forEach { attempt ->
                    val sig = "${attempt.mode}|${attempt.totalQuestions}|${attempt.scorePercent}|${attempt.durationSeconds}|${attempt.partACorrect}|${attempt.partBCorrect}|${attempt.partCCorrect}|${attempt.partDCorrect}"
                    if (existingSignatures.add(sig)) {
                        repository.saveExamAttempt(currentUserId, attempt).onSuccess {
                            importedExams++
                        }
                    }
                }

                val profileToSave = parsed.profile ?: currentProfile()
                repository.updateProfile(currentUserId, profileToSave)
                val mastered = mergedMap.values.count { it.masteryState == MasteryState.MASTERED.name }
                repository.syncLeaderboard(currentUserId, profileToSave, mastered)

                _bannerMessage.value = "Restored ${parsed.topics.size} topics, ${parsed.notesCount} notes, and $importedExams exam attempts."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Backup import failed", e)
                _bannerMessage.value = e.localizedMessage ?: "Failed to import backup."
            } finally {
                _backupBusy.value = false
            }
        }
    }

    fun cleanupForSignOut() {
        timerJob?.cancel()
        tutorStreamJob?.cancel()
        voiceManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        tutorStreamJob?.cancel()
    }
}
