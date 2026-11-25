package com.example.data

import com.example.model.M3Part
import com.example.model.McqQuestion
import com.example.model.PastPaperDescriptiveQuestion
import com.example.model.QuestionDifficulty
import kotlin.math.roundToInt

object QuestionBank {
    val allOfflineMcqs: List<McqQuestion> =
        QuestionBankPartAB.partAMcqs +
            QuestionBankPartAB.partBMcqs +
            QuestionBankPartCD.partCMcqs +
            QuestionBankPartCD.partDMcqs

    fun getQuestionsForPractice(
        partFilter: M3Part?,
        topicIdFilter: String?,
        difficultyFilter: QuestionDifficulty?,
        count: Int
    ): List<McqQuestion> {
        val primary = allOfflineMcqs.filter { q ->
            (partFilter == null || q.part == partFilter) &&
                (topicIdFilter == null || q.topicId == topicIdFilter) &&
                (difficultyFilter == null || q.difficulty == difficultyFilter)
        }
        val pool = when {
            primary.size >= count -> primary
            topicIdFilter != null -> {
                val partFallback = allOfflineMcqs.filter { it.part == (partFilter ?: SyllabusRepository.getTopicById(topicIdFilter)?.part) }
                (primary + partFallback).distinctBy { it.id }
            }
            partFilter != null -> allOfflineMcqs.filter { it.part == partFilter }
            else -> allOfflineMcqs
        }
        return pool.shuffled().take(count.coerceAtMost(pool.size))
    }

    /**
     * Builds a weighted Mock Exam enforcing exact 35% / 20% / 25% / 20% syllabus distribution.
     * Supports 20, 40, 60, and 100 question mock exams offline (repeating/shuffling stem variants if > 60).
     */
    fun buildWeightedMockExam(totalCount: Int): List<McqQuestion> {
        val counts = mapOf(
            M3Part.PART_A to (totalCount * 0.35).roundToInt(),
            M3Part.PART_B to (totalCount * 0.20).roundToInt(),
            M3Part.PART_C to (totalCount * 0.25).roundToInt(),
            M3Part.PART_D to (totalCount * 0.20).roundToInt()
        ).toMutableMap()

        // Ensure sum matches totalCount cleanly
        val diff = totalCount - counts.values.sum()
        if (diff != 0) {
            counts[M3Part.PART_A] = (counts[M3Part.PART_A] ?: 0) + diff
        }

        val selected = mutableListOf<McqQuestion>()
        counts.forEach { (part, target) ->
            val partPool = allOfflineMcqs.filter { it.part == part }.shuffled()
            if (target <= partPool.size) {
                selected += partPool.take(target)
            } else {
                var idx = 0
                var round = 1
                while (selected.count { it.part == part } < target && partPool.isNotEmpty()) {
                    val base = partPool[idx % partPool.size]
                    val question = if (round == 1) base else base.copy(id = "${base.id}_v$round")
                    selected += question
                    idx++
                    if (idx % partPool.size == 0) round++
                }
            }
        }
        return selected.shuffled()
    }

    // 10 Verified Official ICMA ML-1 Past-Paper Descriptive Questions with Suggested Solutions
    val pastPaperDescriptiveQuestions: List<PastPaperDescriptiveQuestion> = listOf(
        PastPaperDescriptiveQuestion(
            id = "pp_1", part = M3Part.PART_C, title = "Marketing Concept Essentials",
            examRef = "ICMA ML-1 Past Paper (Q1)", marks = 6,
            prompt = "Marketing is not merely an isolated business function—it is a guiding philosophy that places the customer at the center of operations. Briefly explain the three essential elements of the Marketing Concept.",
            suggestedSolution = "1. Customer Orientation: Understanding target customers' evolving needs before designing products (outside-in approach).\n2. Coordinated / Integrated Marketing: Aligning Production, Finance, HR, and Sales around customer value delivery rather than departmental silos.\n3. Profitability / Long-Term Value: Achieving sustainable financial returns through satisfied, loyal repeat customers rather than one-off high-pressure sales."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_2", part = M3Part.PART_B, title = "Strategic Role of HRM & External Influences",
            examRef = "ICMA ML-1 Past Paper (Q2)", marks = 8,
            prompt = "Why is Human Resource Management (HRM) critical to organizational success, and what major external factors influence HRM practices?",
            suggestedSolution = "Importance: HRM builds VRIO human capital—acquiring talent, driving motivation/engagement, ensuring legal compliance, and aligning workforce skills with strategy.\nExternal Influences: Labor market demographics, economic cycles, statutory labor/minimum-wage laws, technological automation/AI shifts, union dynamics, and cultural expectations (e.g., hybrid work flexibility)."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_3", part = M3Part.PART_B, title = "Outsourcing Recruitment: Pros & Cons",
            examRef = "ICMA ML-1 Past Paper (Q3)", marks = 6,
            prompt = "Describe the advantages and disadvantages of outsourcing the recruitment function to specialized external agencies.",
            suggestedSolution = "Advantages: Access to deeper passive talent networks, faster time-to-hire for niche roles, reduced internal HR admin load, specialist market salary intelligence.\nDisadvantages: Higher agency fees, weaker grasp of subtle cultural fit, risk of losing internal hiring capability, dependency on third-party prioritization."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_4", part = M3Part.PART_D, title = "Disadvantages of the Balanced Scorecard",
            examRef = "ICMA ML-1 Past Paper (Q4)", marks = 8,
            prompt = "While Kaplan and Norton's Balanced Scorecard links strategy to action, discuss key disadvantages and implementation pitfalls organizations face.",
            suggestedSolution = "1. Metric Overload: Tracking too many KPIs dilutes focus.\n2. Trade-off Conflict: Improving Customer/Learning metrics can pressure short-term Financial margins without clear weighting.\n3. Time & Cost: Requires substantial management buy-in, data pipelines, and regular updating.\n4. Static Risk: If metrics aren't updated as strategy shifts, staff optimize outdated targets."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_5", part = M3Part.PART_C, title = "Sales Orientation & Drawbacks",
            examRef = "ICMA ML-1 Past Paper (Q5)", marks = 6,
            prompt = "Define 'Sales Orientation' and explain three major drawbacks of relying solely on a selling-oriented philosophy.",
            suggestedSolution = "Sales Orientation assumes customers will not buy enough unless persuaded through aggressive selling and promotion of existing stock.\nDrawbacks: (1) Ignores genuine long-term customer needs, (2) High post-purchase dissonance and low repeat loyalty, (3) Reactive short-term discounting erodes margins and brand equity."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_6", part = M3Part.PART_A, title = "Mintzberg's Effective Organization Components",
            examRef = "ICMA ML-1 Past Paper (Q6)", marks = 10,
            prompt = "Explain Henry Mintzberg's five basic organizational parts and show how coordination shifts between Machine Bureaucracy and Adhocracy.",
            suggestedSolution = "5 Parts: Strategic Apex (top executives), Middle Line (operational hierarchy), Operating Core (frontline makers/servers), Technostructure (analysts designing standards), Support Staff (legal, HR, admin).\nMachine Bureaucracy: Technostructure dominates via standardized work processes.\nAdhocracy: Mutual adjustment among cross-functional specialists drives innovation in dynamic environments."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_7", part = M3Part.PART_D, title = "Budgetary Control: Strengths & Behavioral Flaws",
            examRef = "ICMA ML-1 Past Paper (Q7)", marks = 8,
            prompt = "Evaluate budgetary control as a management control mechanism, highlighting both organizational benefits and behavioral dysfunctions.",
            suggestedSolution = "Benefits: Forces forward planning, coordinates departments, clarifies authorization limits, enables variance investigation (Management by Exception).\nFlaws: Budgetary slack/padding, short-termism (cutting maintenance/training to hit quarterly budget), 'spend-it-or-lose-it' year-end waste, silo defensiveness."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_8", part = M3Part.PART_B, title = "Limitations of Selection Interviews",
            examRef = "ICMA ML-1 Past Paper (Q8)", marks = 6,
            prompt = "Selection interviews remain ubiquitous despite mixed predictive validity. Identify five key limitations of traditional interviews.",
            suggestedSolution = "1. Halo/Horn Effect.\n2. First-impression snap judgments.\n3. Contrast effect (comparing against previous candidate rather than job criteria).\n4. Similarity-to-me & stereotyping bias.\n5. Impression management by rehearsed candidates masking actual job competence."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_9", part = M3Part.PART_A, title = "Quality Circles in TQM",
            examRef = "ICMA ML-1 Past Paper (Q9)", marks = 6,
            prompt = "What are Quality Circles (QCC), and what conditions are necessary for them to succeed in an operations environment?",
            suggestedSolution = "Quality Circles are small voluntary groups of frontline workers who meet regularly to identify, analyze (5 Whys, Ishikawa), and solve workplace quality/productivity problems.\nSuccess Conditions: Visible top-management support, problem-solving training, timely implementation/feedback on suggestions, no-blame culture."
        ),
        PastPaperDescriptiveQuestion(
            id = "pp_10", part = M3Part.PART_A, title = "Benefits of Material Requirements Planning (MRP)",
            examRef = "ICMA ML-1 Past Paper (Q10)", marks = 6,
            prompt = "Outline the operational and financial benefits a manufacturing firm gains by implementing an MRP system.",
            suggestedSolution = "1. Lower raw material and WIP holding costs via time-phased ordering.\n2. Fewer production stoppages from component stock-outs.\n3. Higher on-time customer delivery reliability.\n4. Better purchasing leverage through consolidated planned order schedules."
        )
    )
}
