package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.SyllabusRepository
import com.example.domain.SrsAndPlannerEngine
import com.example.model.ExamAttempt
import com.example.model.M3Part
import com.example.model.MasteryState
import com.example.model.StudyPlanSummary
import com.example.model.TopicProgress
import com.example.model.UserProfile
import com.example.ui.components.ExamReadinessGaugeRing
import com.example.ui.components.ExamScoreSparklineCanvas
import com.example.ui.components.LiveExamCountdownCard
import com.example.ui.components.MasteryBreakdownStrip
import com.example.ui.components.MobileInstallGuideDialog
import com.example.ui.components.PartCircularProgressCard
import com.example.ui.components.PortalGuideDialog
import com.example.ui.components.WeightedDistributionRibbon
import com.example.ui.components.computeCandidateLevel
import com.example.ui.components.computeExamReadinessScore
import com.example.ui.components.readinessTierLabel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldGreen

@Composable
fun DashboardScreen(
    profile: UserProfile,
    userEmail: String? = null,
    progressMap: Map<String, TopicProgress>,
    examHistory: List<ExamAttempt>,
    studyPlan: StudyPlanSummary,
    onStartSrsSession: () -> Unit,
    onOpenPart: (M3Part) -> Unit,
    onOpenTopic: (String) -> Unit,
    onOpenMockExam: () -> Unit,
    onOpenJarvis: () -> Unit,
    onOpenPlanner: () -> Unit
) {
    var showPortalGuide by remember { mutableStateOf(false) }
    var showMobileGuide by remember { mutableStateOf(false) }

    val srsQueue = remember(progressMap) {
        SrsAndPlannerEngine.buildSrsReviewQueue(SyllabusRepository.allTopics, progressMap)
    }
    val dueTopicsCount = remember(progressMap) {
        SyllabusRepository.allTopics.count { topic ->
            val p = progressMap[topic.id]
            p != null && (p.masteryState == MasteryState.NEEDS_WORK.name ||
                p.masteryState == MasteryState.REVISION_REQUIRED.name ||
                SrsAndPlannerEngine.isTopicDue(p))
        }
    }
    val nextRecommendedTopic = srsQueue.firstOrNull { it.part == studyPlan.weakestPart } ?: srsQueue.first()
    val levelInfo = remember(profile.xp) { computeCandidateLevel(profile.xp) }
    val readinessScore = remember(studyPlan, progressMap, examHistory) {
        computeExamReadinessScore(studyPlan.partAnalytics, progressMap, examHistory)
    }
    val (tierLabel, tierColor) = readinessTierLabel(readinessScore)
    val completedTotal = progressMap.values.count { it.completed }
    val masteredTotal = progressMap.values.count { it.masteryState == MasteryState.MASTERED.name }
    val formattedExamDate = remember(profile.examDateMillis) {
        SrsAndPlannerEngine.formatLocalDate(profile.examDateMillis)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 680.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dashboard_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top Executive Header Bar
            item(key = "exec_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(MaterialTheme.colorScheme.primary, AmberGold)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.displayName.trim().firstOrNull()?.uppercase() ?: "I",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "Level ${levelInfo.level} · ${levelInfo.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = profile.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { showPortalGuide = true },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("dashboard_help_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "How to use this portal",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (studyPlan.crashModeActive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { onOpenPlanner() }
                                .testTag("countdown_badge")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "${studyPlan.daysRemaining}d",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (studyPlan.crashModeActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = if (studyPlan.crashModeActive) "CRASH" else "TO EXAM",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (studyPlan.crashModeActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 2. Responsive Hero Readiness + Live Exam Countdown Timer
            if (isWide) {
                item(key = "wide_hero_and_countdown") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        HeroReadinessCard(
                            profile = profile,
                            readinessScore = readinessScore,
                            tierLabel = tierLabel,
                            tierColor = tierColor,
                            completedTotal = completedTotal,
                            modifier = Modifier.weight(1.25f)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            LiveExamCountdownCard(
                                examDateMillis = profile.examDateMillis,
                                crashModeActive = studyPlan.crashModeActive,
                                formattedExamDate = formattedExamDate,
                                onConfigureClick = onOpenPlanner,
                                modifier = Modifier.testTag("exam_countdown_timer_card")
                            )
                            PriorityFocusCard(
                                studyPlan = studyPlan,
                                nextRecommendedTopicTitle = nextRecommendedTopic.title,
                                onStudyTopic = { onOpenTopic(nextRecommendedTopic.id) },
                                onOpenPart = { onOpenPart(studyPlan.weakestPart) }
                            )
                        }
                    }
                }
            } else {
                item(key = "hero_readiness_card") {
                    HeroReadinessCard(
                        profile = profile,
                        readinessScore = readinessScore,
                        tierLabel = tierLabel,
                        tierColor = tierColor,
                        completedTotal = completedTotal
                    )
                }

                item(key = "exam_countdown_timer") {
                    LiveExamCountdownCard(
                        examDateMillis = profile.examDateMillis,
                        crashModeActive = studyPlan.crashModeActive,
                        formattedExamDate = formattedExamDate,
                        onConfigureClick = onOpenPlanner,
                        modifier = Modifier.testTag("exam_countdown_timer_card")
                    )
                }
            }

            // 3. Executive Quick-Launch Command Dock
            item(key = "command_dock") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommandActionTile(
                        icon = Icons.Filled.Psychology,
                        title = "SRS Deck",
                        subtitle = if (dueTopicsCount > 0) "$dueTopicsCount due" else "Recall",
                        accent = MaterialTheme.colorScheme.primary,
                        onClick = onStartSrsSession,
                        testTag = "start_srs_button",
                        modifier = Modifier.weight(1f)
                    )
                    CommandActionTile(
                        icon = Icons.Filled.Quiz,
                        title = "Mock Exam",
                        subtitle = "Weighted",
                        accent = EmeraldGreen,
                        onClick = onOpenMockExam,
                        testTag = "quick_mock_button",
                        modifier = Modifier.weight(1f)
                    )
                    CommandActionTile(
                        icon = Icons.Filled.AutoAwesome,
                        title = "Jarvis AI",
                        subtitle = "Tutor",
                        accent = AmberGold,
                        onClick = onOpenJarvis,
                        testTag = "dashboard_ask_jarvis_button",
                        modifier = Modifier.weight(1f)
                    )
                    CommandActionTile(
                        icon = Icons.Filled.CalendarMonth,
                        title = "Planner",
                        subtitle = if (studyPlan.crashModeActive) "Crash" else "${profile.studyDaysPerWeek}d/wk",
                        accent = Color(0xFF8B5CF6),
                        onClick = onOpenPlanner,
                        testTag = "dashboard_planner_button",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Overall Circular Progress Indicators for the 4 Syllabus Parts (A, B, C, D)
            item(key = "four_parts_circular_progress") {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Syllabus Part Progress",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap any Part gauge to drill into its official topics",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "$masteredTotal/56 Mastered",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (isWide) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                studyPlan.partAnalytics.forEach { stat ->
                                    PartCircularProgressCard(
                                        stat = stat,
                                        onClick = { onOpenPart(stat.part) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("dashboard_part_row_${stat.part.id}")
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                studyPlan.partAnalytics.chunked(2).forEach { rowStats ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowStats.forEach { stat ->
                                            PartCircularProgressCard(
                                                stat = stat,
                                                onClick = { onOpenPart(stat.part) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("dashboard_part_row_${stat.part.id}")
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        WeightedDistributionRibbon(partAnalytics = studyPlan.partAnalytics)

                        MasteryBreakdownStrip(progressMap = progressMap)
                    }
                }
            }

            if (!isWide) {
                item(key = "priority_focus_card") {
                    PriorityFocusCard(
                        studyPlan = studyPlan,
                        nextRecommendedTopicTitle = nextRecommendedTopic.title,
                        onStudyTopic = { onOpenTopic(nextRecommendedTopic.id) },
                        onOpenPart = { onOpenPart(studyPlan.weakestPart) }
                    )
                }
            }

            // 5. Recent Mock Exam Results & Score Trajectory
            item(key = "recent_mock_results_card") {
                RecentMockResultsCard(
                    examHistory = examHistory,
                    onOpenMockExam = onOpenMockExam
                )
            }
        }
    }

    if (showPortalGuide) {
        PortalGuideDialog(
            onDismiss = { showPortalGuide = false },
            onOpenMobileInstallGuide = {
                showPortalGuide = false
                showMobileGuide = true
            }
        )
    }

    if (showMobileGuide) {
        MobileInstallGuideDialog(
            userEmail = userEmail,
            displayName = profile.displayName,
            onDismiss = { showMobileGuide = false }
        )
    }
}

@Composable
private fun HeroReadinessCard(
    profile: UserProfile,
    readinessScore: Int,
    tierLabel: String,
    tierColor: Color,
    completedTotal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.hero_study_banner_1795537440724),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.16f,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f),
                                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.72f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "ICMA PAKISTAN · ML-1 PAPER M3",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Organizational Management",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = tierColor.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "$tierLabel · 2025 Scheme",
                                style = MaterialTheme.typography.labelSmall,
                                color = tierColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    ExamReadinessGaugeRing(readinessScore = readinessScore)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricPill(
                        icon = Icons.Filled.School,
                        label = "Topics Done",
                        value = "$completedTotal/${SyllabusRepository.allTopics.size}",
                        tint = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricPill(
                        icon = Icons.Filled.Schedule,
                        label = "Daily / Logged",
                        value = "${profile.dailyTargetHours}h · ${profile.totalStudyMinutes}m",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricPill(
                        icon = Icons.Filled.Bolt,
                        label = "XP & Badges",
                        value = "${profile.xp} · ${profile.earnedBadges.size}/10",
                        tint = AmberGold,
                        modifier = Modifier.weight(1f)
                    )
                    MetricPill(
                        icon = Icons.Filled.LocalFireDepartment,
                        label = "Streak",
                        value = "${profile.streakDays} day${if (profile.streakDays == 1) "" else "s"}",
                        tint = AmberGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityFocusCard(
    studyPlan: StudyPlanSummary,
    nextRecommendedTopicTitle: String,
    onStudyTopic: () -> Unit,
    onOpenPart: () -> Unit
) {
    val weakAnalytics = studyPlan.partAnalytics.first { it.part == studyPlan.weakestPart }
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Priority focus",
                    tint = AmberGold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Priority Focus: ${weakAnalytics.part.code} — ${weakAnalytics.part.title} (${weakAnalytics.part.weightPercent}%)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Completed ${weakAnalytics.completedTopics}/${weakAnalytics.totalTopics} topics · Accuracy ${if (weakAnalytics.attemptCount > 0) "${weakAnalytics.quizAccuracyPercent}%" else "Untested"}. Next target: '$nextRecommendedTopicTitle'.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onStudyTopic,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("recommended_topic_button")
                ) {
                    Text("Study Topic")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                FilledTonalButton(
                    onClick = onOpenPart,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                ) {
                    Text("View ${weakAnalytics.part.shortTitle}")
                }
            }
        }
    }
}

@Composable
private fun RecentMockResultsCard(
    examHistory: List<ExamAttempt>,
    onOpenMockExam: () -> Unit
) {
    val mockResults = remember(examHistory) {
        val mocks = examHistory.filter { it.mode == "MOCK_EXAM" }
        if (mocks.isNotEmpty()) mocks else examHistory
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.testTag("recent_mock_results_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Mock Exam Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${mockResults.size} recorded sitting${if (mockResults.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(
                    onClick = onOpenMockExam,
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text("Sit Mock")
                }
            }

            if (mockResults.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No mock exam results yet. Sit a timed 20, 40, 60, or 100-question weighted Mock Exam (35/20/25/20) to track your scores and calibrate weak areas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                ExamScoreSparklineCanvas(attempts = mockResults)

                mockResults.take(5).forEach { attempt ->
                    val mins = attempt.durationSeconds / 60
                    val secs = attempt.durationSeconds % 60
                    val durationLabel = "%02d:%02d".format(mins, secs)
                    val passed = attempt.scorePercent >= 50

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = BorderStroke(
                            1.dp,
                            (if (passed) EmeraldGreen else MaterialTheme.colorScheme.error).copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${if (attempt.mode == "MOCK_EXAM") "Weighted Mock" else "Practice"} · ${attempt.totalQuestions} Qs",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Timer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = durationLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "OPS ${attempt.partACorrect}/${attempt.partATotal} · HR ${attempt.partBCorrect}/${attempt.partBTotal} · MKT ${attempt.partCCorrect}/${attempt.partCTotal} · PERF ${attempt.partDCorrect}/${attempt.partDTotal}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${attempt.scorePercent}%",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (passed) EmeraldGreen else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "+${attempt.xpEarned} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 84.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MetricPill(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
