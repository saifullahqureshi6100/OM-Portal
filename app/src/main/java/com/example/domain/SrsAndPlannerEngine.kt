package com.example.domain

import com.example.data.SyllabusRepository
import com.example.model.DailyStudyBlock
import com.example.model.ExamAttempt
import com.example.model.M3Part
import com.example.model.MasteryState
import com.example.model.PartAnalytics
import com.example.model.SrsRating
import com.example.model.StudyPlanSummary
import com.example.model.SyllabusTopic
import com.example.model.TopicDifficulty
import com.example.model.TopicProgress
import com.example.model.UserProfile
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.max
import kotlin.math.roundToInt

object SrsAndPlannerEngine {
    private const val DAY_MILLIS = 86_400_000L

    /**
     * SM-2 Spaced Repetition Update:
     * Struggling ratings (AGAIN/HARD) shorten intervals and mark NEEDS_WORK / REVISION_REQUIRED.
     * Strong ratings (GOOD/EASY) lengthen intervals and promote toward MASTERED.
     */
    fun applySrsRating(
        current: TopicProgress,
        rating: SrsRating,
        nowMillis: Long = System.currentTimeMillis()
    ): TopicProgress {
        val q = rating.quality
        val newEase = (current.easeFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)))
            .coerceIn(1.3, 4.0)

        var reps = current.repetitions
        var lapses = current.lapses
        val nextIntervalDays: Int
        val nextMastery: MasteryState

        when (rating) {
            SrsRating.AGAIN -> {
                reps = 0
                lapses += 1
                nextIntervalDays = 0 // Due immediately / same day
                nextMastery = MasteryState.NEEDS_WORK
            }
            SrsRating.HARD -> {
                reps = max(1, reps)
                nextIntervalDays = if (current.intervalDays <= 1) 1 else (current.intervalDays * 1.2).roundToInt().coerceAtLeast(2)
                nextMastery = if (lapses > 0) MasteryState.REVISION_REQUIRED else MasteryState.LEARNING
            }
            SrsRating.GOOD -> {
                reps += 1
                nextIntervalDays = when (reps) {
                    1 -> 1
                    2 -> 6
                    else -> (current.intervalDays * newEase).roundToInt().coerceAtLeast(7)
                }
                nextMastery = if (reps >= 3 && nextIntervalDays >= 10) MasteryState.MASTERED else MasteryState.LEARNING
            }
            SrsRating.EASY -> {
                reps += 1
                nextIntervalDays = when (reps) {
                    1 -> 4
                    2 -> 10
                    else -> (current.intervalDays * newEase * 1.3).roundToInt().coerceAtLeast(14)
                }
                nextMastery = if (reps >= 2 || current.completed) MasteryState.MASTERED else MasteryState.LEARNING
            }
        }

        val nextReviewAt = if (nextIntervalDays == 0) {
            nowMillis + 4 * 3_600_000L // 4 hours later for failed items
        } else {
            nowMillis + nextIntervalDays * DAY_MILLIS
        }

        return current.copy(
            easeFactor = (newEase * 100.0).roundToInt() / 100.0,
            intervalDays = nextIntervalDays,
            repetitions = reps,
            lapses = lapses,
            nextReviewAtMillis = nextReviewAt,
            lastReviewedAtMillis = nowMillis,
            masteryState = nextMastery.name
        )
    }

    /**
     * Updates topic mastery and SRS urgency from quiz/mock-exam answers.
     * Wrong answers bring the topic forward for immediate review; repeated accuracy advances mastery.
     */
    fun applyQuizOutcome(
        current: TopicProgress,
        isCorrect: Boolean,
        nowMillis: Long = System.currentTimeMillis()
    ): TopicProgress {
        val newAttempts = current.attemptCount + 1
        val newCorrect = current.correctCount + if (isCorrect) 1 else 0
        val accuracy = newCorrect.toDouble() / newAttempts.toDouble()

        return if (!isCorrect) {
            current.copy(
                attemptCount = newAttempts,
                correctCount = newCorrect,
                repetitions = 0,
                lapses = current.lapses + 1,
                easeFactor = (current.easeFactor - 0.2).coerceAtLeast(1.3),
                intervalDays = 1,
                nextReviewAtMillis = nowMillis, // Immediately due
                lastReviewedAtMillis = nowMillis,
                masteryState = if (current.masteryState == MasteryState.MASTERED.name) {
                    MasteryState.REVISION_REQUIRED.name
                } else {
                    MasteryState.NEEDS_WORK.name
                }
            )
        } else {
            val promotedMastery = when {
                accuracy >= 0.80 && newCorrect >= 2 -> MasteryState.MASTERED
                current.masteryState == MasteryState.NEEDS_WORK.name -> MasteryState.REVISION_REQUIRED
                else -> MasteryState.LEARNING
            }
            val interval = if (promotedMastery == MasteryState.MASTERED) max(7, current.intervalDays.coerceAtLeast(7)) else max(2, current.intervalDays)
            current.copy(
                attemptCount = newAttempts,
                correctCount = newCorrect,
                repetitions = current.repetitions + 1,
                intervalDays = interval,
                nextReviewAtMillis = nowMillis + interval * DAY_MILLIS,
                lastReviewedAtMillis = nowMillis,
                masteryState = promotedMastery.name
            )
        }
    }

    /**
     * Builds the Spaced Repetition queue ordered by highest review urgency:
     * 1. Needs Work / Revision Required & overdue topics first
     * 2. Higher syllabus weightage (Ops 35%, Mkt 25%) & Exam-Critical difficulty
     * 3. Mastered topics with future due dates appear infrequently
     */
    fun buildSrsReviewQueue(
        topics: List<SyllabusTopic>,
        progressMap: Map<String, TopicProgress>,
        nowMillis: Long = System.currentTimeMillis()
    ): List<SyllabusTopic> {
        return topics.sortedByDescending { topic ->
            val p = progressMap[topic.id]
            val state = MasteryState.fromName(p?.masteryState ?: MasteryState.NOT_STARTED.name)
            val isDue = p == null || p.nextReviewAtMillis <= nowMillis
            val dueScore = if (isDue) 100.0 else -((p.nextReviewAtMillis - nowMillis) / DAY_MILLIS.toDouble()).coerceAtMost(90.0)
            val stateBoost = when (state) {
                MasteryState.NEEDS_WORK -> 80.0
                MasteryState.REVISION_REQUIRED -> 65.0
                MasteryState.LEARNING -> 40.0
                MasteryState.NOT_STARTED -> 30.0
                MasteryState.MASTERED -> -40.0
            }
            val weightBoost = topic.part.weightPercent.toDouble() * 0.5
            val difficultyBoost = when (topic.difficulty) {
                TopicDifficulty.ADVANCED -> 12.0
                TopicDifficulty.CORE -> 6.0
                TopicDifficulty.FOUNDATION -> 0.0
            }
            dueScore + stateBoost + weightBoost + difficultyBoost
        }
    }

    fun isTopicDue(progress: TopicProgress?, nowMillis: Long = System.currentTimeMillis()): Boolean {
        if (progress == null) return false // Only reviewed/started items or explicit new learning queue count as scheduled SRS due
        return progress.lastReviewedAtMillis > 0L && progress.nextReviewAtMillis <= nowMillis
    }

    /**
     * Computes adaptive weightage-based study plan & Crash Mode timetable.
     * NEVER lowers any part below its mandatory minimum syllabus coverage floor (60% of official weight).
     */
    fun buildStudyPlan(
        profile: UserProfile,
        progressMap: Map<String, TopicProgress>,
        examHistory: List<ExamAttempt>,
        forceCrashPreview: Boolean = false,
        nowMillis: Long = System.currentTimeMillis()
    ): StudyPlanSummary {
        val daysRemaining = ((profile.examDateMillis - nowMillis) / DAY_MILLIS).coerceAtLeast(0L)
        val crashMode = forceCrashPreview || daysRemaining < 7L
        val dailyMinutes = (profile.dailyTargetHours.coerceIn(0.5, 16.0) * 60.0).roundToInt()

        // 1. Gather raw analytics per part
        val rawPartStats = M3Part.entries.map { part ->
            val partTopics = SyllabusRepository.getTopicsForPart(part)
            val progresses = partTopics.map { progressMap[it.id] }
            val completedCount = progresses.count { it?.completed == true }
            val masteredCount = progresses.count { it?.masteryState == MasteryState.MASTERED.name }
            val needsWorkCount = progresses.count {
                it?.masteryState == MasteryState.NEEDS_WORK.name ||
                    it?.masteryState == MasteryState.REVISION_REQUIRED.name
            }
            val dueCount = progresses.count { p ->
                p != null && (p.masteryState == MasteryState.NEEDS_WORK.name ||
                    p.masteryState == MasteryState.REVISION_REQUIRED.name ||
                    (p.lastReviewedAtMillis > 0L && p.nextReviewAtMillis <= nowMillis))
            }
            val totalAttempts = progresses.sumOf { it?.attemptCount ?: 0 }
            val totalCorrect = progresses.sumOf { it?.correctCount ?: 0 }
            val accuracy = if (totalAttempts > 0) ((totalCorrect * 100.0) / totalAttempts).roundToInt() else 0

            // Check historical exam score for this part
            val histCorrect = examHistory.sumOf {
                when (part) {
                    M3Part.PART_A -> it.partACorrect
                    M3Part.PART_B -> it.partBCorrect
                    M3Part.PART_C -> it.partCCorrect
                    M3Part.PART_D -> it.partDCorrect
                }
            }
            val histTotal = examHistory.sumOf {
                when (part) {
                    M3Part.PART_A -> it.partATotal
                    M3Part.PART_B -> it.partBTotal
                    M3Part.PART_C -> it.partCTotal
                    M3Part.PART_D -> it.partDTotal
                }
            }
            val combinedAccuracy = when {
                totalAttempts > 0 && histTotal > 0 -> (((totalCorrect + histCorrect) * 100.0) / (totalAttempts + histTotal)).roundToInt()
                histTotal > 0 -> ((histCorrect * 100.0) / histTotal).roundToInt()
                else -> accuracy
            }

            // Compute raw demand score starting from official weightage
            val incompleteRatio = 1.0 - (completedCount.toDouble() / partTopics.size.coerceAtLeast(1))
            val weakRatio = needsWorkCount.toDouble() / partTopics.size.coerceAtLeast(1)
            val accuracyPenalty = if (totalAttempts + histTotal > 0) (100 - combinedAccuracy) / 100.0 else 0.35
            val crashBoost = if (crashMode) (part.weightPercent / 100.0) * 0.4 else 0.0
            val adaptiveMultiplier = 1.0 + (incompleteRatio * 0.45) + (weakRatio * 0.65) + (accuracyPenalty * 0.4) + crashBoost

            Triple(part, part.weightPercent.toDouble() * adaptiveMultiplier, Triple(completedCount, masteredCount, needsWorkCount)) to
                Triple(dueCount, combinedAccuracy, totalAttempts + histTotal)
        }

        // 2. Enforce mandatory syllabus coverage floor (never starve a part) and normalize to 100%
        val totalRaw = rawPartStats.sumOf { it.first.second }.coerceAtLeast(1.0)
        val floored = rawPartStats.map { entry ->
            val part = entry.first.first
            val pct = (entry.first.second / totalRaw) * 100.0
            part to max(pct, part.minCoverageFloorPercent.toDouble())
        }
        val flooredSum = floored.sumOf { it.second }.coerceAtLeast(1.0)
        val normalizedWeights = floored.map { (part, v) ->
            part to ((v / flooredSum) * 100.0).roundToInt()
        }.toMap().toMutableMap()

        // Fix rounding drift to sum exactly 100
        val weightDiff = 100 - normalizedWeights.values.sum()
        if (weightDiff != 0) {
            normalizedWeights[M3Part.PART_A] = (normalizedWeights[M3Part.PART_A] ?: 35) + weightDiff
        }

        // Identify weakest part (lowest readiness/accuracy weighted by coverage gap)
        val weakestPart = rawPartStats.minByOrNull { entry ->
            val part = entry.first.first
            val totalTopics = SyllabusRepository.getTopicsForPart(part).size.coerceAtLeast(1)
            val completed = entry.first.third.first
            val needsWork = entry.first.third.third
            val acc = if (entry.second.third > 0) entry.second.second else 50
            val readiness = ((completed.toDouble() / totalTopics) * 50.0) + (acc * 0.5) - (needsWork * 6.0)
            readiness
        }?.first?.first ?: M3Part.PART_A

        val partAnalytics = rawPartStats.map { entry ->
            val part = entry.first.first
            val topicsCount = SyllabusRepository.getTopicsForPart(part).size
            val allocatedPct = normalizedWeights[part] ?: part.weightPercent
            PartAnalytics(
                part = part,
                totalTopics = topicsCount,
                completedTopics = entry.first.third.first,
                masteredTopics = entry.first.third.second,
                needsWorkTopics = entry.first.third.third,
                dueSrsCount = entry.second.first,
                quizAccuracyPercent = entry.second.second,
                attemptCount = entry.second.third,
                allocatedPercent = allocatedPct,
                allocatedMinutesPerDay = ((dailyMinutes * allocatedPct) / 100.0).roundToInt().coerceAtLeast(15),
                isPriorityWeakest = part == weakestPart
            )
        }

        // 3. Build 7-day actionable timetable (or Crash Mode 7-day emergency sprint)
        val prioritizedTopics = buildSrsReviewQueue(SyllabusRepository.allTopics, progressMap, nowMillis)
        val daysToPlan = if (crashMode) daysRemaining.toInt().coerceIn(1, 7) else profile.studyDaysPerWeek.coerceIn(1, 7)
        val dailyBlocks = mutableListOf<DailyStudyBlock>()

        for (day in 1..daysToPlan) {
            val dayLabel = if (crashMode) "Crash Day $day" else "Day $day"
            // Pick 3 blocks per day respecting part weights and priority topics
            val sortedPartsForDay = partAnalytics.sortedByDescending { pa ->
                pa.allocatedPercent + (if (pa.isPriorityWeakest) 15 else 0) + ((day * 7 + pa.part.ordinal * 3) % 10)
            }
            val partForBlock1 = sortedPartsForDay[0].part
            val partForBlock2 = sortedPartsForDay[1].part

            val topic1 = prioritizedTopics.firstOrNull { it.part == partForBlock1 } ?: prioritizedTopics.first()
            val topic2 = prioritizedTopics.firstOrNull { it.part == partForBlock2 && it.id != topic1.id }
                ?: prioritizedTopics.drop(1).firstOrNull() ?: topic1

            val block1Minutes = (dailyMinutes * 0.45).roundToInt().coerceAtLeast(20)
            val block2Minutes = (dailyMinutes * 0.30).roundToInt().coerceAtLeast(15)
            val block3Minutes = (dailyMinutes - block1Minutes - block2Minutes).coerceAtLeast(15)

            dailyBlocks += DailyStudyBlock(
                dayNumber = day,
                dayLabel = dayLabel,
                part = partForBlock1,
                activityType = if (crashMode) "High-Yield Concept + SRS Recall" else "Concept Study & Notes",
                topicId = topic1.id,
                topicTitle = topic1.title,
                durationMinutes = block1Minutes,
                reason = if (partForBlock1 == weakestPart) "Priority weak area (${partForBlock1.weightPercent}% exam weight)" else "${partForBlock1.weightPercent}% official syllabus weight"
            )
            dailyBlocks += DailyStudyBlock(
                dayNumber = day,
                dayLabel = dayLabel,
                part = partForBlock2,
                activityType = "SRS Active Recall Review",
                topicId = topic2.id,
                topicTitle = topic2.title,
                durationMinutes = block2Minutes,
                reason = "Scheduled retention & coverage floor"
            )
            dailyBlocks += DailyStudyBlock(
                dayNumber = day,
                dayLabel = dayLabel,
                part = weakestPart,
                activityType = if (crashMode) "Timed Past-Paper & Exam MCQ Drill" else "Weighted MCQ Practice Set",
                topicId = prioritizedTopics.firstOrNull { it.part == weakestPart }?.id ?: topic1.id,
                topicTitle = "${weakestPart.shortTitle} Rapid Drill",
                durationMinutes = block3Minutes,
                reason = if (crashMode) "Emergency <7d exam speed sharpening" else "Closes weakest-part gap"
            )
        }

        val totalTopicsCount = SyllabusRepository.allTopics.size.coerceAtLeast(1)
        val totalCompleted = progressMap.values.count { it.completed }
        val overallCompletion = ((totalCompleted * 100.0) / totalTopicsCount).roundToInt().coerceIn(0, 100)

        return StudyPlanSummary(
            daysRemaining = daysRemaining,
            crashModeActive = crashMode,
            partAnalytics = partAnalytics,
            dailyBlocks = dailyBlocks,
            weakestPart = weakestPart,
            overallCompletionPercent = overallCompletion
        )
    }

    /**
     * Evaluates earned gamification badges and returns updated list + newly unlocked badge IDs.
     */
    fun evaluateBadges(
        profile: UserProfile,
        progressMap: Map<String, TopicProgress>,
        examHistory: List<ExamAttempt>
    ): Pair<List<String>, List<String>> {
        val current = profile.earnedBadges.toMutableSet()
        val newlyUnlocked = mutableListOf<String>()

        fun unlock(badgeId: String, condition: Boolean) {
            if (condition && current.add(badgeId)) {
                newlyUnlocked += badgeId
            }
        }

        val completedTopics = progressMap.values.count { it.completed }
        fun partCompletedOrMastered(part: M3Part): Boolean {
            val partTopics = SyllabusRepository.getTopicsForPart(part)
            return partTopics.isNotEmpty() && partTopics.all { topic ->
                val p = progressMap[topic.id]
                p != null && (p.completed || p.masteryState == MasteryState.MASTERED.name)
            }
        }

        unlock("first_step", completedTopics >= 1)
        unlock("srs_starter", profile.srsReviewsCount >= 5L)
        unlock("first_mock", examHistory.any { it.mode == "MOCK_EXAM" })
        unlock("high_scorer", examHistory.any { it.scorePercent >= 80 && it.totalQuestions >= 10 })
        unlock("mastered_ops", partCompletedOrMastered(M3Part.PART_A))
        unlock("mastered_hr", partCompletedOrMastered(M3Part.PART_B))
        unlock("mastered_mkt", partCompletedOrMastered(M3Part.PART_C))
        unlock("mastered_perf", partCompletedOrMastered(M3Part.PART_D))
        unlock("m3_scholar", profile.xp >= 1000L)
        unlock("streak_keeper", profile.streakDays >= 3)

        return current.toList() to newlyUnlocked
    }

    fun computeUpdatedStreak(currentStreak: Int, lastStudyDate: String, todayMillis: Long = System.currentTimeMillis()): Pair<Int, String> {
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(todayMillis).atZone(zone).toLocalDate()
        val todayStr = today.toString()
        if (lastStudyDate == todayStr) {
            return currentStreak.coerceAtLeast(1) to todayStr
        }
        val yesterdayStr = today.minusDays(1).toString()
        val nextStreak = if (lastStudyDate == yesterdayStr) currentStreak + 1 else 1
        return nextStreak to todayStr
    }

    fun formatLocalDate(millis: Long): String {
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return "${date.dayOfMonth} ${date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${date.year}"
    }

    fun computeTodayIso(millis: Long = System.currentTimeMillis()): String =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
}
