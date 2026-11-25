package com.example.domain

import com.example.data.SyllabusRepository
import com.example.model.ExamAttempt
import com.example.model.M3Part
import com.example.model.MasteryState
import com.example.model.TeachingMode
import com.example.model.TopicProgress
import com.example.model.TutorLanguage
import com.example.model.UserProfile
import org.json.JSONArray
import org.json.JSONObject

data class ParsedBackupData(
    val profile: UserProfile?,
    val topics: List<TopicProgress>,
    val examHistory: List<ExamAttempt>,
    val notesCount: Int
)

object BackupJsonSerializer {
    private const val SCHEMA_VERSION = 1
    private val validBadgeIds: Set<String> = SyllabusRepository.badges.map { it.id }.toSet()

    fun serializeBackup(
        profile: UserProfile,
        progressMap: Map<String, TopicProgress>,
        examHistory: List<ExamAttempt>,
        nowMillis: Long = System.currentTimeMillis()
    ): String {
        val root = JSONObject().apply {
            put("schemaVersion", SCHEMA_VERSION)
            put("app", "ICMA M3 Prep (2025 Scheme)")
            put("exportedAtMillis", nowMillis)

            put(
                "profile",
                JSONObject().apply {
                    put("displayName", profile.displayName)
                    put("examDateMillis", profile.examDateMillis)
                    put("dailyTargetHours", profile.dailyTargetHours)
                    put("studyDaysPerWeek", profile.studyDaysPerWeek)
                    put("preferredLanguage", profile.preferredLanguage)
                    put("preferredTeachingMode", profile.preferredTeachingMode)
                    put("darkModeEnabled", profile.darkModeEnabled)
                    put("voiceEnabled", profile.voiceEnabled)
                    put("leaderboardOptIn", profile.leaderboardOptIn)
                    put("xp", profile.xp)
                    put("streakDays", profile.streakDays)
                    put("lastStudyDate", profile.lastStudyDate)
                    put("totalStudyMinutes", profile.totalStudyMinutes)
                    put("srsReviewsCount", profile.srsReviewsCount)
                    put("earnedBadges", JSONArray(profile.earnedBadges))
                    put("learnedTopics", JSONArray(profile.learnedTopics))
                }
            )

            val topicArray = JSONArray()
            progressMap.entries
                .sortedBy { it.key }
                .forEach { (topicId, p) ->
                    if (SyllabusRepository.getTopicById(topicId) != null) {
                        topicArray.put(
                            JSONObject().apply {
                                put("topicId", topicId)
                                put("partId", p.partId)
                                put("masteryState", p.masteryState)
                                put("completed", p.completed)
                                put("bookmarked", p.bookmarked)
                                put("easeFactor", p.easeFactor)
                                put("intervalDays", p.intervalDays)
                                put("repetitions", p.repetitions)
                                put("lapses", p.lapses)
                                put("nextReviewAtMillis", p.nextReviewAtMillis)
                                put("lastReviewedAtMillis", p.lastReviewedAtMillis)
                                put("correctCount", p.correctCount)
                                put("attemptCount", p.attemptCount)
                                put("studyMinutes", p.studyMinutes)
                                put("userNotes", p.userNotes)
                            }
                        )
                    }
                }
            put("topicProgress", topicArray)

            val examArray = JSONArray()
            examHistory.take(50).forEach { a ->
                examArray.put(
                    JSONObject().apply {
                        put("mode", a.mode)
                        put("totalQuestions", a.totalQuestions)
                        put("correctCount", a.correctCount)
                        put("incorrectCount", a.incorrectCount)
                        put("skippedCount", a.skippedCount)
                        put("scorePercent", a.scorePercent)
                        put("durationSeconds", a.durationSeconds)
                        put("xpEarned", a.xpEarned)
                        put("partACorrect", a.partACorrect)
                        put("partATotal", a.partATotal)
                        put("partBCorrect", a.partBCorrect)
                        put("partBTotal", a.partBTotal)
                        put("partCCorrect", a.partCCorrect)
                        put("partCTotal", a.partCTotal)
                        put("partDCorrect", a.partDCorrect)
                        put("partDTotal", a.partDTotal)
                        put("weakTopicIds", JSONArray(a.weakTopicIds))
                    }
                )
            }
            put("examHistory", examArray)
        }
        return root.toString(2)
    }

    fun parseAndSanitizeBackup(
        rawJson: String,
        targetUserId: String,
        currentProfile: UserProfile
    ): Result<ParsedBackupData> {
        return try {
            val trimmed = rawJson.trim()
            if (trimmed.isEmpty()) {
                return Result.failure(IllegalArgumentException("The selected backup file is empty."))
            }
            val root = JSONObject(trimmed)
            val version = root.optInt("schemaVersion", -1)
            if (version != SCHEMA_VERSION) {
                return Result.failure(
                    IllegalArgumentException("Unsupported backup format (expected schemaVersion $SCHEMA_VERSION).")
                )
            }

            val profileJson = root.optJSONObject("profile")
            val parsedProfile = if (profileJson != null) {
                val safeName = profileJson.optString("displayName", currentProfile.displayName)
                    .trim()
                    .ifBlank { "ICMA Candidate" }
                    .take(50)
                val safeLang = TutorLanguage.fromId(
                    profileJson.optString("preferredLanguage", currentProfile.preferredLanguage)
                ).id
                val safeMode = TeachingMode.fromLabel(
                    profileJson.optString("preferredTeachingMode", currentProfile.preferredTeachingMode)
                ).label.take(40)

                val badgesArr = profileJson.optJSONArray("earnedBadges")
                val safeBadges = if (badgesArr != null) {
                    (0 until badgesArr.length())
                        .map { badgesArr.optString(it) }
                        .filter { it in validBadgeIds }
                        .distinct()
                        .take(30)
                } else {
                    currentProfile.earnedBadges
                }

                val learnedArr = profileJson.optJSONArray("learnedTopics")
                val safeLearned = if (learnedArr != null) {
                    (0 until learnedArr.length())
                        .map { learnedArr.optString(it) }
                        .filter { SyllabusRepository.getTopicById(it) != null }
                        .distinct()
                        .take(60)
                } else {
                    currentProfile.learnedTopics
                }

                currentProfile.copy(
                    userId = targetUserId,
                    displayName = safeName,
                    examDateMillis = profileJson.optLong("examDateMillis", currentProfile.examDateMillis).coerceAtLeast(0L),
                    dailyTargetHours = profileJson.optDouble("dailyTargetHours", currentProfile.dailyTargetHours).coerceIn(0.5, 16.0),
                    studyDaysPerWeek = profileJson.optInt("studyDaysPerWeek", currentProfile.studyDaysPerWeek).coerceIn(1, 7),
                    preferredLanguage = safeLang,
                    preferredTeachingMode = safeMode,
                    darkModeEnabled = profileJson.optBoolean("darkModeEnabled", currentProfile.darkModeEnabled),
                    voiceEnabled = profileJson.optBoolean("voiceEnabled", currentProfile.voiceEnabled),
                    leaderboardOptIn = profileJson.optBoolean("leaderboardOptIn", currentProfile.leaderboardOptIn),
                    xp = profileJson.optLong("xp", currentProfile.xp).coerceIn(0L, 10_000_000L),
                    streakDays = profileJson.optInt("streakDays", currentProfile.streakDays).coerceIn(0, 10_000),
                    lastStudyDate = profileJson.optString("lastStudyDate", currentProfile.lastStudyDate).take(20),
                    totalStudyMinutes = profileJson.optLong("totalStudyMinutes", currentProfile.totalStudyMinutes).coerceIn(0L, 1_000_000L),
                    srsReviewsCount = profileJson.optLong("srsReviewsCount", currentProfile.srsReviewsCount).coerceIn(0L, 1_000_000L),
                    earnedBadges = safeBadges,
                    learnedTopics = safeLearned
                )
            } else {
                null
            }

            val topicProgressList = mutableListOf<TopicProgress>()
            val topicArr = root.optJSONArray("topicProgress")
            if (topicArr != null) {
                for (i in 0 until topicArr.length()) {
                    val obj = topicArr.optJSONObject(i) ?: continue
                    val topicId = obj.optString("topicId").trim()
                    val officialTopic = SyllabusRepository.getTopicById(topicId) ?: continue
                    val part = officialTopic.part
                    val safeMastery = MasteryState.fromName(obj.optString("masteryState")).name
                    val safeEase = obj.optDouble("easeFactor", 2.5).let { if (it.isNaN()) 2.5 else it }.coerceIn(1.3, 5.0)
                    topicProgressList += TopicProgress(
                        id = topicId,
                        userId = targetUserId,
                        partId = part.id,
                        masteryState = safeMastery,
                        completed = obj.optBoolean("completed", false),
                        bookmarked = obj.optBoolean("bookmarked", false),
                        easeFactor = safeEase,
                        intervalDays = obj.optInt("intervalDays", 0).coerceIn(0, 3650),
                        repetitions = obj.optInt("repetitions", 0).coerceIn(0, 10_000),
                        lapses = obj.optInt("lapses", 0).coerceIn(0, 10_000),
                        nextReviewAtMillis = obj.optLong("nextReviewAtMillis", 0L).coerceAtLeast(0L),
                        lastReviewedAtMillis = obj.optLong("lastReviewedAtMillis", 0L).coerceAtLeast(0L),
                        correctCount = obj.optInt("correctCount", 0).coerceIn(0, 100_000),
                        attemptCount = obj.optInt("attemptCount", 0).coerceIn(0, 100_000),
                        studyMinutes = obj.optInt("studyMinutes", 0).coerceIn(0, 100_000),
                        userNotes = obj.optString("userNotes", "").take(10_000)
                    )
                }
            }

            val examList = mutableListOf<ExamAttempt>()
            val examArr = root.optJSONArray("examHistory")
            if (examArr != null) {
                for (i in 0 until examArr.length().coerceAtMost(50)) {
                    val obj = examArr.optJSONObject(i) ?: continue
                    val mode = if (obj.optString("mode") == "PRACTICE_QUIZ") "PRACTICE_QUIZ" else "MOCK_EXAM"
                    val weakArr = obj.optJSONArray("weakTopicIds")
                    val weakIds = if (weakArr != null) {
                        (0 until weakArr.length())
                            .map { weakArr.optString(it) }
                            .filter { SyllabusRepository.getTopicById(it) != null }
                            .distinct()
                            .take(56)
                    } else {
                        emptyList()
                    }
                    examList += ExamAttempt(
                        userId = targetUserId,
                        mode = mode,
                        totalQuestions = obj.optInt("totalQuestions", 20).coerceIn(1, 100),
                        correctCount = obj.optInt("correctCount", 0).coerceIn(0, 100),
                        incorrectCount = obj.optInt("incorrectCount", 0).coerceIn(0, 100),
                        skippedCount = obj.optInt("skippedCount", 0).coerceIn(0, 100),
                        scorePercent = obj.optInt("scorePercent", 0).coerceIn(0, 100),
                        durationSeconds = obj.optInt("durationSeconds", 0).coerceIn(0, 36_000),
                        xpEarned = obj.optInt("xpEarned", 0).coerceIn(0, 2_000),
                        partACorrect = obj.optInt("partACorrect", 0).coerceIn(0, 100),
                        partATotal = obj.optInt("partATotal", 0).coerceIn(0, 100),
                        partBCorrect = obj.optInt("partBCorrect", 0).coerceIn(0, 100),
                        partBTotal = obj.optInt("partBTotal", 0).coerceIn(0, 100),
                        partCCorrect = obj.optInt("partCCorrect", 0).coerceIn(0, 100),
                        partCTotal = obj.optInt("partCTotal", 0).coerceIn(0, 100),
                        partDCorrect = obj.optInt("partDCorrect", 0).coerceIn(0, 100),
                        partDTotal = obj.optInt("partDTotal", 0).coerceIn(0, 100),
                        weakTopicIds = weakIds
                    )
                }
            }

            if (parsedProfile == null && topicProgressList.isEmpty() && examList.isEmpty()) {
                return Result.failure(IllegalArgumentException("No valid ICMA M3 backup data found in file."))
            }

            val notesCount = topicProgressList.count { it.userNotes.isNotBlank() }
            Result.success(
                ParsedBackupData(
                    profile = parsedProfile,
                    topics = topicProgressList,
                    examHistory = examList,
                    notesCount = notesCount
                )
            )
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Invalid JSON backup file: ${e.localizedMessage ?: "Parse error"}"))
        }
    }
}
