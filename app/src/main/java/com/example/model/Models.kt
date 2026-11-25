package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class M3Part(
    val id: String,
    val code: String,
    val title: String,
    val shortTitle: String,
    val weightPercent: Int,
    val minCoverageFloorPercent: Int
) {
    PART_A("PART_A", "Part A", "Operations Management", "Operations", 35, 21),
    PART_B("PART_B", "Part B", "Human Resource Management", "HRM", 20, 12),
    PART_C("PART_C", "Part C", "Marketing Management", "Marketing", 25, 15),
    PART_D("PART_D", "Part D", "Performance Management", "Performance", 20, 12);

    companion object {
        fun fromId(raw: String): M3Part =
            entries.firstOrNull { it.id == raw } ?: PART_A
    }
}

enum class MasteryState(val label: String, val priorityWeight: Double) {
    NOT_STARTED("Not Started", 1.5),
    LEARNING("Learning", 1.25),
    NEEDS_WORK("Needs Work", 1.9),
    REVISION_REQUIRED("Revision Required", 1.75),
    MASTERED("Mastered", 0.6);

    companion object {
        fun fromName(raw: String): MasteryState =
            entries.firstOrNull { it.name == raw } ?: NOT_STARTED
    }
}

enum class TopicDifficulty(val label: String) {
    FOUNDATION("Foundation"),
    CORE("Core"),
    ADVANCED("Exam-Critical")
}

enum class QuestionDifficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    DIFFICULT("Difficult"),
    EXAM_LEVEL("Exam-level")
}

enum class SrsRating(val label: String, val subtitle: String, val quality: Int) {
    AGAIN("Again", "Reset & review soon", 1),
    HARD("Hard", "Short interval", 3),
    GOOD("Good", "Standard interval", 4),
    EASY("Easy", "Boost interval", 5)
}

enum class TutorLanguage(val id: String, val label: String) {
    ENGLISH("ENGLISH", "English"),
    BILINGUAL_URDU("BILINGUAL_URDU", "English + Urdu"),
    ROMAN_URDU("ROMAN_URDU", "Roman Urdu");

    companion object {
        fun fromId(raw: String): TutorLanguage =
            entries.firstOrNull { it.id == raw } ?: ENGLISH
    }
}

enum class TeachingMode(val label: String, val promptHint: String) {
    BEGINNER("Beginner", "Explain from first principles with clear everyday examples."),
    DETAILED("Detailed Learning", "Provide comprehensive textbook depth, frameworks, and nuances."),
    SIMPLE_ENGLISH("Simple English", "Use short, plain sentences easy for fast comprehension."),
    EXAM_PREP("Exam Preparation", "Focus on ICMA ML-1 marking points, traps, and command words."),
    QUICK_REVISION("Quick Revision", "Deliver crisp bullet-point memory hooks and formulas."),
    CASE_STUDY("Case Study Analysis", "Walk through scenario diagnosis, application, and recommendations."),
    ASK_ME_QUESTIONS("Ask Me Questions", "Test the student interactively with 1-2 probing recall questions."),
    MCQ_PRACTICE("MCQ Practice", "Provide an exam-style practice MCQ labeled as AI-generated practice with answer breakdown.");

    companion object {
        fun fromLabel(raw: String): TeachingMode =
            entries.firstOrNull { it.label == raw } ?: EXAM_PREP
    }
}

data class SyllabusTopic(
    val id: String,
    val part: M3Part,
    val title: String,
    val subtitle: String,
    val difficulty: TopicDifficulty,
    val definition: String,
    val keyConcepts: List<String>,
    val examples: List<String>,
    val revisionNotes: List<String>,
    val keyFramework: String
)

data class BadgeDefinition(
    val id: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val bonusXp: Int
)

data class McqQuestion(
    val id: String,
    val part: M3Part,
    val topicId: String,
    val topicTitle: String,
    val difficulty: QuestionDifficulty,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val sourceLabel: String,
    val isPastPaper: Boolean = false
)

data class PastPaperDescriptiveQuestion(
    val id: String,
    val part: M3Part,
    val title: String,
    val examRef: String,
    val marks: Int,
    val prompt: String,
    val suggestedSolution: String
)

// --- Firestore Models (1:1 parity with firestore.rules) ---

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "ICMA Candidate",
    val examDateMillis: Long = System.currentTimeMillis() + 30L * 86_400_000L,
    val dailyTargetHours: Double = 2.0,
    val studyDaysPerWeek: Int = 6,
    val preferredLanguage: String = TutorLanguage.ENGLISH.id,
    val preferredTeachingMode: String = TeachingMode.EXAM_PREP.label,
    val darkModeEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val leaderboardOptIn: Boolean = false,
    val xp: Long = 0L,
    val streakDays: Int = 0,
    val lastStudyDate: String = "",
    val totalStudyMinutes: Long = 0L,
    val srsReviewsCount: Long = 0L,
    val earnedBadges: List<String> = emptyList(),
    val learnedTopics: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class TopicProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val partId: String = M3Part.PART_A.id,
    val masteryState: String = MasteryState.NOT_STARTED.name,
    val completed: Boolean = false,
    val bookmarked: Boolean = false,
    val easeFactor: Double = 2.5,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val lapses: Int = 0,
    val nextReviewAtMillis: Long = 0L,
    val lastReviewedAtMillis: Long = 0L,
    val correctCount: Int = 0,
    val attemptCount: Int = 0,
    val studyMinutes: Int = 0,
    val userNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ExamAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val mode: String = "MOCK_EXAM",
    val totalQuestions: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val skippedCount: Int = 0,
    val scorePercent: Int = 0,
    val durationSeconds: Int = 0,
    val xpEarned: Int = 0,
    val partACorrect: Int = 0,
    val partATotal: Int = 0,
    val partBCorrect: Int = 0,
    val partBTotal: Int = 0,
    val partCCorrect: Int = 0,
    val partCTotal: Int = 0,
    val partDCorrect: Int = 0,
    val partDTotal: Int = 0,
    val weakTopicIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "ICMA Candidate",
    val xp: Long = 0L,
    val streakDays: Int = 0,
    val badgeCount: Int = 0,
    val masteredCount: Int = 0,
    val optedIn: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

// --- Domain & UI Helper Models ---

data class PartAnalytics(
    val part: M3Part,
    val totalTopics: Int,
    val completedTopics: Int,
    val masteredTopics: Int,
    val needsWorkTopics: Int,
    val dueSrsCount: Int,
    val quizAccuracyPercent: Int,
    val attemptCount: Int,
    val allocatedPercent: Int,
    val allocatedMinutesPerDay: Int,
    val isPriorityWeakest: Boolean
)

data class DailyStudyBlock(
    val dayNumber: Int,
    val dayLabel: String,
    val part: M3Part,
    val activityType: String,
    val topicId: String,
    val topicTitle: String,
    val durationMinutes: Int,
    val reason: String
)

data class StudyPlanSummary(
    val daysRemaining: Long,
    val crashModeActive: Boolean,
    val partAnalytics: List<PartAnalytics>,
    val dailyBlocks: List<DailyStudyBlock>,
    val weakestPart: M3Part,
    val overallCompletionPercent: Int
)

data class TutorMessage(
    val id: String,
    val fromUser: Boolean,
    val text: String,
    val detectedPart: M3Part? = null,
    val modeLabel: String = "",
    val isOfflineResponse: Boolean = false
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
