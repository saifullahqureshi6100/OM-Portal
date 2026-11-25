package com.example.data

import com.example.model.BadgeDefinition
import com.example.model.M3Part
import com.example.model.SyllabusTopic

data class PartOverviewMeta(
    val part: M3Part,
    val breakdownSummary: String,
    val sampleHighlights: List<String>
)

object SyllabusRepository {
    val partOverviews: List<PartOverviewMeta> = listOf(
        PartOverviewMeta(
            part = M3Part.PART_A,
            breakdownSummary = "Covers enterprise and plant management, Mintzberg's organizational framework, production methods (job, batch, flow, and 3D printing), MRP/ERP/OPT systems, resource productivity, and modern quality frameworks like TQM, Kaizen, Lean, TPM, TQMEX, ISO standards (incl. ISO 56005), and AI-driven quality control.",
            sampleHighlights = listOf("PESTEL & ESG", "Mintzberg", "MRP / ERP / OPT", "TQM & Lean", "ISO 56005")
        ),
        PartOverviewMeta(
            part = M3Part.PART_B,
            breakdownSummary = "Focuses on human capital theories, employee motivation, remote/hybrid work policies, codes of conduct, the complete HR lifecycle (planning, algorithmic recruitment bias, selection, appraisals, compensation, training, decruitment), Employee Experience (EX) platforms, and mental health analytics.",
            sampleHighlights = listOf("Human Capital", "Motivation", "Remote & Hybrid", "AI Bias", "EX & Mental Health")
        ),
        PartOverviewMeta(
            part = M3Part.PART_C,
            breakdownSummary = "Explores core marketing philosophies, marketing environment, corporate vs. marketing strategy, brand management, IMC, consumer behavior, NPO/internal/social marketing, and emerging channels including metaverse branding, micro-influencer partnerships, and Web3/NFT campaigns.",
            sampleHighlights = listOf("Marketing Strategy", "IMC & Brand", "Consumer Behavior", "Metaverse & NFTs", "Social & NPO")
        ),
        PartOverviewMeta(
            part = M3Part.PART_D,
            breakdownSummary = "Emphasizes organizational control systems, Critical Success Factors (CSFs) vs. KPIs, real-time IoT dashboards, budgetary control, financial & non-financial tools, Balanced Scorecard, and predictive analytics.",
            sampleHighlights = listOf("Control Systems", "CSFs vs KPIs", "IoT Dashboards", "Balanced Scorecard", "Predictive Analytics")
        )
    )

    fun getPartOverview(part: M3Part): PartOverviewMeta =
        partOverviews.first { it.part == part }

    val allTopics: List<SyllabusTopic> =
        SyllabusPartAB.partATopics +
            SyllabusPartAB.partBTopics +
            SyllabusPartCD.partCTopics +
            SyllabusPartCD.partDTopics

    private val topicsById: Map<String, SyllabusTopic> = allTopics.associateBy { it.id }

    fun getTopicsForPart(part: M3Part): List<SyllabusTopic> =
        allTopics.filter { it.part == part }

    fun getTopicById(id: String): SyllabusTopic? = topicsById[id]

    fun detectPartFromText(query: String): M3Part {
        val lower = query.lowercase()
        val partScores = mutableMapOf<M3Part, Int>()
        allTopics.forEach { topic ->
            var score = 0
            if (lower.contains(topic.title.lowercase())) score += 5
            topic.title.lowercase().split(" ", "/", "(", ")", ",").filter { it.length >= 4 }.forEach { token ->
                if (lower.contains(token)) score += 2
            }
            if (score > 0) {
                partScores[topic.part] = (partScores[topic.part] ?: 0) + score
            }
        }
        if (lower.contains("operations") || lower.contains("mrp") || lower.contains("tqm") || lower.contains("lean") || lower.contains("mintzberg") || lower.contains("iso")) {
            partScores[M3Part.PART_A] = (partScores[M3Part.PART_A] ?: 0) + 4
        }
        if (lower.contains("hr") || lower.contains("human") || lower.contains("motivation") || lower.contains("herzberg") || lower.contains("appraisal") || lower.contains("recruitment")) {
            partScores[M3Part.PART_B] = (partScores[M3Part.PART_B] ?: 0) + 4
        }
        if (lower.contains("marketing") || lower.contains("brand") || lower.contains("imc") || lower.contains("metaverse") || lower.contains("nft") || lower.contains("consumer")) {
            partScores[M3Part.PART_C] = (partScores[M3Part.PART_C] ?: 0) + 4
        }
        if (lower.contains("performance") || lower.contains("scorecard") || lower.contains("kpi") || lower.contains("csf") || lower.contains("budget") || lower.contains("iot")) {
            partScores[M3Part.PART_D] = (partScores[M3Part.PART_D] ?: 0) + 4
        }
        return partScores.maxByOrNull { it.value }?.key ?: M3Part.PART_A
    }

    val badges: List<BadgeDefinition> = listOf(
        BadgeDefinition(
            id = "first_step",
            title = "First Step",
            description = "Completed your first official ICMA M3 topic.",
            iconKey = "rocket",
            bonusXp = 50
        ),
        BadgeDefinition(
            id = "srs_starter",
            title = "Memory Architect",
            description = "Completed 5 spaced repetition active-recall reviews.",
            iconKey = "psychology",
            bonusXp = 75
        ),
        BadgeDefinition(
            id = "first_mock",
            title = "Completed First Mock Exam",
            description = "Submitted your first weighted ICMA M3 Mock Exam.",
            iconKey = "assignment",
            bonusXp = 100
        ),
        BadgeDefinition(
            id = "high_scorer",
            title = "Distinction Ready",
            description = "Achieved 80% or higher on a Mock Exam or 10+ MCQ assessment.",
            iconKey = "grade",
            bonusXp = 150
        ),
        BadgeDefinition(
            id = "mastered_ops",
            title = "Mastered Operations Management",
            description = "Mastered or completed all 24 topics in Part A (35%).",
            iconKey = "precision",
            bonusXp = 250
        ),
        BadgeDefinition(
            id = "mastered_hr",
            title = "Mastered Human Resource Management",
            description = "Mastered or completed all 13 topics in Part B (20%).",
            iconKey = "groups",
            bonusXp = 200
        ),
        BadgeDefinition(
            id = "mastered_mkt",
            title = "Mastered Marketing Management",
            description = "Mastered or completed all 12 topics in Part C (25%).",
            iconKey = "campaign",
            bonusXp = 220
        ),
        BadgeDefinition(
            id = "mastered_perf",
            title = "Mastered Performance Management",
            description = "Mastered or completed all 7 topics in Part D (20%).",
            iconKey = "insights",
            bonusXp = 200
        ),
        BadgeDefinition(
            id = "m3_scholar",
            title = "ICMA M3 Scholar",
            description = "Accumulated 1,000+ XP across your M3 preparation.",
            iconKey = "trophy",
            bonusXp = 200
        ),
        BadgeDefinition(
            id = "streak_keeper",
            title = "Streak Keeper",
            description = "Maintained a 3-day consecutive M3 study streak.",
            iconKey = "flame",
            bonusXp = 100
        )
    )
}
