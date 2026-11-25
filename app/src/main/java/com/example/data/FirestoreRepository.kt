package com.example.data

import android.content.Context
import com.example.R
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.example.model.ExamAttempt
import com.example.model.LeaderboardEntry
import com.example.model.M3Part
import com.example.model.MasteryState
import com.example.model.TopicProgress
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(expectedUserId: String? = null): String {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
        }
        if (expectedUserId != null && uid != expectedUserId) {
            throw IllegalStateException("Signed-in user changed; refusing cross-user access.")
        }
        return uid
    }

    fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/profile/main"
        emitAll(
            db.collection("users").document(uid).collection("profile").document("main")
                .snapshots()
                .map { snapshot ->
                    if (!snapshot.exists()) null
                    else snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.GET, path, uid)
                    throw error
                }
        )
    }

    fun observeTopicProgress(userId: String): Flow<Map<String, TopicProgress>> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/topicProgress"
        emitAll(
            db.collection("users").document(uid).collection("topicProgress")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(TopicProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .associateBy { it.id }
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.LIST, path, uid)
                    throw error
                }
        )
    }

    fun observeExamHistory(userId: String): Flow<List<ExamAttempt>> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/examHistory"
        emitAll(
            db.collection("users").document(uid).collection("examHistory")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(ExamAttempt::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.LIST, path, uid)
                    throw error
                }
        )
    }

    fun observeOptedInLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        val uid = requireUserId()
        val path = "leaderboard"
        emitAll(
            db.collection("leaderboard")
                .whereEqualTo("optedIn", true)
                .limit(50)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(LeaderboardEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.xp }
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.LIST, path, uid)
                    throw error
                }
        )
    }

    private fun logStreamErrorIfActive(error: Throwable, op: OperationType, path: String, expectedUid: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(exceptionToLog, op, path)
        }
    }

    suspend fun ensureProfileInitialized(userId: String, defaultDisplayName: String): Result<UserProfile> {
        val path = "users/$userId/profile/main"
        return try {
            val uid = requireUserId(userId)
            val docRef = db.collection("users").document(uid).collection("profile").document("main")
            val existing = docRef.get().await()
            if (existing.exists()) {
                val parsed = existing.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    ?: UserProfile(userId = uid)
                Result.success(parsed)
            } else {
                val safeName = defaultDisplayName.trim().ifBlank { "ICMA Candidate" }.take(50)
                val initial = UserProfile(
                    userId = uid,
                    displayName = safeName,
                    examDateMillis = System.currentTimeMillis() + 30L * 86_400_000L
                )
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to initial.displayName,
                    "examDateMillis" to initial.examDateMillis,
                    "dailyTargetHours" to initial.dailyTargetHours,
                    "studyDaysPerWeek" to initial.studyDaysPerWeek,
                    "preferredLanguage" to initial.preferredLanguage,
                    "preferredTeachingMode" to initial.preferredTeachingMode,
                    "darkModeEnabled" to initial.darkModeEnabled,
                    "voiceEnabled" to initial.voiceEnabled,
                    "leaderboardOptIn" to initial.leaderboardOptIn,
                    "xp" to initial.xp,
                    "streakDays" to initial.streakDays,
                    "lastStudyDate" to initial.lastStudyDate,
                    "totalStudyMinutes" to initial.totalStudyMinutes,
                    "srsReviewsCount" to initial.srsReviewsCount,
                    "earnedBadges" to initial.earnedBadges,
                    "learnedTopics" to initial.learnedTopics,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                Result.success(initial)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateProfile(userId: String, profile: UserProfile): Result<Unit> {
        val path = "users/$userId/profile/main"
        return try {
            val uid = requireUserId(userId)
            val docRef = db.collection("users").document(uid).collection("profile").document("main")
            val snap = docRef.get().await()
            val safeName = profile.displayName.trim().ifBlank { "ICMA Candidate" }.take(50)
            val safeHours = profile.dailyTargetHours.coerceIn(0.5, 16.0)
            val safeDays = profile.studyDaysPerWeek.coerceIn(1, 7)
            val safeMode = profile.preferredTeachingMode.trim().ifBlank { "Exam Preparation" }.take(40)
            val safeBadges = profile.earnedBadges.distinct().take(30)
            val safeLearned = profile.learnedTopics.distinct().take(60)

            if (!snap.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "examDateMillis" to profile.examDateMillis.coerceAtLeast(0L),
                    "dailyTargetHours" to safeHours,
                    "studyDaysPerWeek" to safeDays,
                    "preferredLanguage" to profile.preferredLanguage,
                    "preferredTeachingMode" to safeMode,
                    "darkModeEnabled" to profile.darkModeEnabled,
                    "voiceEnabled" to profile.voiceEnabled,
                    "leaderboardOptIn" to profile.leaderboardOptIn,
                    "xp" to profile.xp.coerceIn(0L, 10_000_000L),
                    "streakDays" to profile.streakDays.coerceIn(0, 10_000),
                    "lastStudyDate" to profile.lastStudyDate.take(20),
                    "totalStudyMinutes" to profile.totalStudyMinutes.coerceIn(0L, 1_000_000L),
                    "srsReviewsCount" to profile.srsReviewsCount.coerceIn(0L, 1_000_000L),
                    "earnedBadges" to safeBadges,
                    "learnedTopics" to safeLearned,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                docRef.update(
                    mapOf(
                        "displayName" to safeName,
                        "examDateMillis" to profile.examDateMillis.coerceAtLeast(0L),
                        "dailyTargetHours" to safeHours,
                        "studyDaysPerWeek" to safeDays,
                        "preferredLanguage" to profile.preferredLanguage,
                        "preferredTeachingMode" to safeMode,
                        "darkModeEnabled" to profile.darkModeEnabled,
                        "voiceEnabled" to profile.voiceEnabled,
                        "leaderboardOptIn" to profile.leaderboardOptIn,
                        "xp" to profile.xp.coerceIn(0L, 10_000_000L),
                        "streakDays" to profile.streakDays.coerceIn(0, 10_000),
                        "lastStudyDate" to profile.lastStudyDate.take(20),
                        "totalStudyMinutes" to profile.totalStudyMinutes.coerceIn(0L, 1_000_000L),
                        "srsReviewsCount" to profile.srsReviewsCount.coerceIn(0L, 1_000_000L),
                        "earnedBadges" to safeBadges,
                        "learnedTopics" to safeLearned,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun upsertTopicProgress(
        userId: String,
        topicId: String,
        part: M3Part,
        progress: TopicProgress
    ): Result<Unit> {
        val path = "users/$userId/topicProgress/$topicId"
        return try {
            val uid = requireUserId(userId)
            require(topicId.isNotBlank()) { "Topic ID must not be blank" }
            val docRef = db.collection("users").document(uid).collection("topicProgress").document(topicId)
            val snap = docRef.get().await()
            val safeEase = progress.easeFactor.coerceIn(1.3, 5.0)
            val safeNotes = progress.userNotes.take(10_000)
            val safeMastery = MasteryState.fromName(progress.masteryState).name

            if (!snap.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "partId" to part.id,
                    "masteryState" to safeMastery,
                    "completed" to progress.completed,
                    "bookmarked" to progress.bookmarked,
                    "easeFactor" to safeEase,
                    "intervalDays" to progress.intervalDays.coerceIn(0, 3650),
                    "repetitions" to progress.repetitions.coerceIn(0, 10_000),
                    "lapses" to progress.lapses.coerceIn(0, 10_000),
                    "nextReviewAtMillis" to progress.nextReviewAtMillis.coerceAtLeast(0L),
                    "lastReviewedAtMillis" to progress.lastReviewedAtMillis.coerceAtLeast(0L),
                    "correctCount" to progress.correctCount.coerceIn(0, 100_000),
                    "attemptCount" to progress.attemptCount.coerceIn(0, 100_000),
                    "studyMinutes" to progress.studyMinutes.coerceIn(0, 100_000),
                    "userNotes" to safeNotes,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                docRef.update(
                    mapOf(
                        "masteryState" to safeMastery,
                        "completed" to progress.completed,
                        "bookmarked" to progress.bookmarked,
                        "easeFactor" to safeEase,
                        "intervalDays" to progress.intervalDays.coerceIn(0, 3650),
                        "repetitions" to progress.repetitions.coerceIn(0, 10_000),
                        "lapses" to progress.lapses.coerceIn(0, 10_000),
                        "nextReviewAtMillis" to progress.nextReviewAtMillis.coerceAtLeast(0L),
                        "lastReviewedAtMillis" to progress.lastReviewedAtMillis.coerceAtLeast(0L),
                        "correctCount" to progress.correctCount.coerceIn(0, 100_000),
                        "attemptCount" to progress.attemptCount.coerceIn(0, 100_000),
                        "studyMinutes" to progress.studyMinutes.coerceIn(0, 100_000),
                        "userNotes" to safeNotes,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun saveExamAttempt(userId: String, attempt: ExamAttempt): Result<Unit> {
        val attemptId = "attempt_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
        val path = "users/$userId/examHistory/$attemptId"
        return try {
            val uid = requireUserId(userId)
            val docRef = db.collection("users").document(uid).collection("examHistory").document(attemptId)
            val safeMode = if (attempt.mode == "PRACTICE_QUIZ") "PRACTICE_QUIZ" else "MOCK_EXAM"
            val payload = mapOf(
                "userId" to uid,
                "mode" to safeMode,
                "totalQuestions" to attempt.totalQuestions.coerceIn(1, 100),
                "correctCount" to attempt.correctCount.coerceIn(0, 100),
                "incorrectCount" to attempt.incorrectCount.coerceIn(0, 100),
                "skippedCount" to attempt.skippedCount.coerceIn(0, 100),
                "scorePercent" to attempt.scorePercent.coerceIn(0, 100),
                "durationSeconds" to attempt.durationSeconds.coerceIn(0, 36_000),
                "xpEarned" to attempt.xpEarned.coerceIn(0, 2_000),
                "partACorrect" to attempt.partACorrect.coerceIn(0, 100),
                "partATotal" to attempt.partATotal.coerceIn(0, 100),
                "partBCorrect" to attempt.partBCorrect.coerceIn(0, 100),
                "partBTotal" to attempt.partBTotal.coerceIn(0, 100),
                "partCCorrect" to attempt.partCCorrect.coerceIn(0, 100),
                "partCTotal" to attempt.partCTotal.coerceIn(0, 100),
                "partDCorrect" to attempt.partDCorrect.coerceIn(0, 100),
                "partDTotal" to attempt.partDTotal.coerceIn(0, 100),
                "weakTopicIds" to attempt.weakTopicIds.distinct().take(56),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun syncLeaderboard(
        userId: String,
        profile: UserProfile,
        masteredCount: Int
    ): Result<Unit> {
        val path = "leaderboard/$userId"
        return try {
            val uid = requireUserId(userId)
            val docRef = db.collection("leaderboard").document(uid)
            if (!profile.leaderboardOptIn) {
                val existing = docRef.get().await()
                if (existing.exists()) {
                    docRef.delete().await()
                }
                return Result.success(Unit)
            }
            val snap = docRef.get().await()
            val safeName = profile.displayName.trim().ifBlank { "ICMA Candidate" }.take(50)
            val safeXp = profile.xp.coerceIn(0L, 10_000_000L)
            val safeStreak = profile.streakDays.coerceIn(0, 10_000)
            val safeBadges = profile.earnedBadges.size.coerceIn(0, 50)
            val safeMastered = masteredCount.coerceIn(0, 56)

            if (!snap.exists()) {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "displayName" to safeName,
                        "xp" to safeXp,
                        "streakDays" to safeStreak,
                        "badgeCount" to safeBadges,
                        "masteredCount" to safeMastered,
                        "optedIn" to true,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.update(
                    mapOf(
                        "displayName" to safeName,
                        "xp" to safeXp,
                        "streakDays" to safeStreak,
                        "badgeCount" to safeBadges,
                        "masteredCount" to safeMastered,
                        "optedIn" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }
}
