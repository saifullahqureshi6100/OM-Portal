package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.data.SyllabusRepository
import com.example.model.ExamAttempt
import com.example.model.M3Part
import com.example.model.MasteryState
import com.example.model.PartAnalytics
import com.example.model.TopicProgress
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

data class CandidateLevelInfo(
    val level: Int,
    val title: String,
    val currentLevelXp: Long,
    val nextLevelXp: Long,
    val progressFraction: Float
)

fun computeCandidateLevel(xp: Long): CandidateLevelInfo {
    val thresholds = listOf(
        0L to "Foundation Associate",
        300L to "Operations Analyst",
        800L to "Strategic Planner",
        1600L to "Management Lead",
        3000L to "ICMA M3 Fellow"
    )
    var idx = 0
    for (i in thresholds.indices) {
        if (xp >= thresholds[i].first) idx = i
    }
    val currentMin = thresholds[idx].first
    val nextMin = if (idx < thresholds.lastIndex) thresholds[idx + 1].first else thresholds[idx].first + 1500L
    val span = (nextMin - currentMin).coerceAtLeast(1L)
    val frac = ((xp - currentMin).toFloat() / span.toFloat()).coerceIn(0f, 1f)
    return CandidateLevelInfo(
        level = idx + 1,
        title = thresholds[idx].second,
        currentLevelXp = xp,
        nextLevelXp = nextMin,
        progressFraction = frac
    )
}

fun computeExamReadinessScore(
    partAnalytics: List<PartAnalytics>,
    progressMap: Map<String, TopicProgress>,
    examHistory: List<ExamAttempt>
): Int {
    val totalTopics = SyllabusRepository.allTopics.size.coerceAtLeast(1)
    val completedTopics = progressMap.values.count { it.completed }
    val masteredTopics = progressMap.values.count { it.masteryState == MasteryState.MASTERED.name }
    val completionRatio = completedTopics.toDouble() / totalTopics.toDouble()
    val masteryRatio = masteredTopics.toDouble() / totalTopics.toDouble()

    // Weight accuracy using recent tests or tested part stats
    val weightedAcc = if (examHistory.isNotEmpty()) {
        examHistory.take(5).map { it.scorePercent }.average() / 100.0
    } else {
        val testedParts = partAnalytics.filter { it.attemptCount > 0 }
        if (testedParts.isNotEmpty()) testedParts.map { it.quizAccuracyPercent / 100.0 }.average() else 0.0
    }

    val raw = (completionRatio * 45.0) + (masteryRatio * 30.0) + (weightedAcc * 25.0)
    return raw.roundToInt().coerceIn(0, 100)
}

fun readinessTierLabel(score: Int): Pair<String, Color> = when {
    score >= 80 -> "Distinction Ready" to EmeraldGreen
    score >= 55 -> "Exam Competitive" to Color(0xFF22D3EE)
    score >= 25 -> "Momentum Building" to AmberGold
    else -> "Foundation Stage" to Color(0xFF94A3B8)
}

/**
 * Custom Canvas Circular Exam Readiness Gauge with spring animation.
 */
@Composable
fun ExamReadinessGaugeRing(
    readinessScore: Int,
    ringSize: Dp = 104.dp,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (readinessScore / 100f).coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "readiness_ring"
    )
    val (_, tierColor) = readinessTierLabel(readinessScore)
    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(ringSize)) {
            val strokePx = 10.dp.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            drawArc(
                color = trackColor,
                startAngle = -220f,
                sweepAngle = 260f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            if (animatedProgress > 0.005f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(primaryColor, tierColor, AmberGold, primaryColor)
                    ),
                    startAngle = -220f,
                    sweepAngle = 260f * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$readinessScore%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "READINESS",
                style = MaterialTheme.typography.labelSmall,
                color = tierColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Segmented 4-Part Weighted Progress Ribbon (35% Ops / 20% HR / 25% Mkt / 20% Perf).
 */
@Composable
fun WeightedDistributionRibbon(
    partAnalytics: List<PartAnalytics>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            partAnalytics.forEach { stat ->
                val accent = partAccentColor(stat.part)
                val fillFrac = (stat.completedTopics.toFloat() / stat.totalTopics.coerceAtLeast(1)).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .weight(stat.part.weightPercent.toFloat())
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(accent.copy(alpha = 0.22f))
                ) {
                    if (fillFrac > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fillFrac)
                                .height(14.dp)
                                .background(accent)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            partAnalytics.forEach { stat ->
                val accent = partAccentColor(stat.part)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Text(
                        text = "${stat.part.shortTitle} ${stat.part.weightPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 56-Topic Mastery Distribution Strip across SM-2 states.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MasteryBreakdownStrip(
    progressMap: Map<String, TopicProgress>,
    modifier: Modifier = Modifier
) {
    val allTopics = SyllabusRepository.allTopics
    val mastered = allTopics.count { progressMap[it.id]?.masteryState == MasteryState.MASTERED.name }
    val learning = allTopics.count { progressMap[it.id]?.masteryState == MasteryState.LEARNING.name }
    val revision = allTopics.count { progressMap[it.id]?.masteryState == MasteryState.REVISION_REQUIRED.name }
    val needsWork = allTopics.count { progressMap[it.id]?.masteryState == MasteryState.NEEDS_WORK.name }
    val notStarted = (allTopics.size - mastered - learning - revision - needsWork).coerceAtLeast(0)

    val buckets = listOf(
        Triple("Mastered", mastered, masteryColor(MasteryState.MASTERED)),
        Triple("Learning", learning, masteryColor(MasteryState.LEARNING)),
        Triple("Revision", revision, masteryColor(MasteryState.REVISION_REQUIRED)),
        Triple("Needs Work", needsWork, masteryColor(MasteryState.NEEDS_WORK)),
        Triple("Untouched", notStarted, masteryColor(MasteryState.NOT_STARTED))
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            buckets.filter { it.second > 0 }.forEach { (_, count, color) ->
                Box(
                    modifier = Modifier
                        .weight(count.toFloat())
                        .height(8.dp)
                        .background(color)
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            buckets.forEach { (label, count, color) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = "$label: $count",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Custom Canvas Exam Trajectory Chart with 50% ICMA pass-mark reference threshold.
 */
@Composable
fun ExamScoreSparklineCanvas(
    attempts: List<ExamAttempt>,
    modifier: Modifier = Modifier
) {
    val chronological = attempts.take(10).reversed()
    val lineColor = MaterialTheme.colorScheme.primary
    val passLineColor = EmeraldGreen.copy(alpha = 0.65f)
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            val w = size.width
            val h = size.height
            val padH = 10.dp.toPx()
            val padV = 10.dp.toPx()
            val chartW = (w - padH * 2).coerceAtLeast(1f)
            val chartH = (h - padV * 2).coerceAtLeast(1f)

            // 50% pass threshold dashed line
            val passY = padV + chartH * 0.5f
            drawLine(
                color = passLineColor,
                start = Offset(padH, passY),
                end = Offset(w - padH, passY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            )
            // Baseline 0% & 100% grid hairlines
            drawLine(gridColor, Offset(padH, padV), Offset(w - padH, padV), 1.dp.toPx())
            drawLine(gridColor, Offset(padH, h - padV), Offset(w - padH, h - padV), 1.dp.toPx())

            if (chronological.isEmpty()) return@Canvas

            val points = chronological.mapIndexed { idx, attempt ->
                val x = if (chronological.size == 1) {
                    padH + chartW / 2f
                } else {
                    padH + (idx.toFloat() / (chronological.size - 1).toFloat()) * chartW
                }
                val y = padV + (1f - (attempt.scorePercent / 100f).coerceIn(0f, 1f)) * chartH
                Offset(x, y)
            }

            if (points.size >= 2) {
                val areaPath = Path().apply {
                    moveTo(points.first().x, h - padV)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, h - padV)
                    close()
                }
                drawPath(
                    path = areaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.28f), Color.Transparent),
                        startY = padV,
                        endY = h - padV
                    )
                )

                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            points.forEachIndexed { i, pt ->
                val score = chronological[i].scorePercent
                val dotColor = if (score >= 50) EmeraldGreen else CoralRed
                drawCircle(color = dotColor, radius = 5.dp.toPx(), center = pt)
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = pt)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dashed green line = 50% ICMA pass mark",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            chronological.lastOrNull()?.let { latest ->
                Text(
                    text = "Latest: ${latest.scorePercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (latest.scorePercent >= 50) EmeraldGreen else CoralRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Executive XP Rank & Level Progress Card.
 */
@Composable
fun XpLevelProgressCard(
    xp: Long,
    badgesCount: Int,
    totalBadges: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val levelInfo = computeCandidateLevel(xp)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberGold.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "L${levelInfo.level}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                    }
                    Column {
                        Text(
                            text = levelInfo.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${levelInfo.currentLevelXp} / ${levelInfo.nextLevelXp} XP to next rank",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Text(
                        text = "$badgesCount/$totalBadges Badges · ${streakDays}d",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            }
            LinearProgressIndicator(
                progress = { levelInfo.progressFraction },
                color = AmberGold,
                trackColor = AmberGold.copy(alpha = 0.18f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

/**
 * Interactive Portal Walkthrough Dialog explaining every module in plain language.
 */
@Composable
fun PortalGuideDialog(
    onDismiss: () -> Unit,
    onOpenMobileInstallGuide: (() -> Unit)? = null
) {
    val steps = listOf(
        Triple(
            Icons.Filled.School,
            "1. Executive Home Dashboard",
            "Track your Exam Countdown, Readiness Score (0–100%), weakest syllabus part, and 4-part weightage progress (Operations 35%, HRM 20%, Marketing 25%, Performance 20%) at a glance."
        ),
        Triple(
            Icons.AutoMirrored.Filled.MenuBook,
            "2. Study — Hierarchical Syllabus & Notes",
            "Drill down from Parts A–D into all 56 official topics. Read key frameworks, listen with Voice Read Aloud, paste or import (.txt/.md) your own ICMA PDF/Doc notes, and mark topics completed."
        ),
        Triple(
            Icons.Filled.Psychology,
            "3. Spaced Repetition (SRS) Memory Deck",
            "Tap 'Review' or 'SRS Deck' to practice active recall. Rate each topic (Again / Hard / Good / Easy): tough topics return sooner while mastered concepts space out automatically."
        ),
        Triple(
            Icons.Filled.AutoAwesome,
            "4. Tuto-Jarvis AI & Offline Tutor",
            "Chat with Tuto-Jarvis (Gemini AI) or toggle Offline Mode anytime. Choose from 8 teaching styles (Exam Prep, Beginner, Quick Revision, Case Study, etc.) and English, Urdu, or Roman Urdu."
        ),
        Triple(
            Icons.Filled.Quiz,
            "5. Exams — Practice MCQs, Weighted Mock & Past Papers",
            "Drill filtered offline MCQs or generate fresh AI practice MCQs, sit 20/40/60/100-question timed weighted Mock Exams, and review 10 Past-Paper Descriptive Q&As."
        ),
        Triple(
            Icons.Filled.Analytics,
            "6. Plan — Adaptive Timetable, Analytics & Leaderboard",
            "Set your exam date and daily study hours to auto-generate a floor-protected timetable (Crash Mode activates automatically under 7 days). Inspect the 56-topic mastery grid, unlock 10 badges, export JSON backups, and opt in to the leaderboard."
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "How to Use ICMA M3 Prep",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                steps.forEach { (icon, title, body) ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        dismissButton = if (onOpenMobileInstallGuide != null) {
            {
                OutlinedButton(
                    onClick = onOpenMobileInstallGuide,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Icon(Icons.Filled.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Install APK")
                }
            }
        } else null,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Got It")
            }
        }
    )
}

/**
 * Dedicated Android APK Mobile Installation Guide & Device Readiness Dialog.
 */
@Composable
fun MobileInstallGuideDialog(
    userEmail: String?,
    displayName: String,
    onDismiss: () -> Unit
) {
    val steps = listOf(
        "1. Get the APK from AI Studio (Browser)" to
            "In the AI Studio web workspace (not Android phone Settings), check the top toolbar or gear Settings menu for APK/AAB generation or Download ZIP. On a phone browser, turn on 'Desktop site' in Chrome's menu (or open on a PC) if toolbar buttons are hidden.",
        "2. Allow Unknown Apps & Install" to
            "Tap the downloaded .apk in your phone's Files or Chrome Downloads. If Android asks, enable 'Allow from this source' for your browser or file manager, then tap Install.",
        "3. Sign In With Google on Mobile" to
            "Open ICMA M3 Prep and tap 'Continue with Google' using ${userEmail ?: "your Google account"}. Your 56-topic progress, notes, XP, badges, and mock scores sync automatically.",
        "4. Study Offline & Keep JSON Backups" to
            "Toggle 'Offline' inside the Jarvis tab whenever you have no mobile data, and use Plan -> Badges & Rank -> Backup & Portable Data to export/import .json snapshots anytime."
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Install APK on Mobile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Live Mobile & Cloud Sync Readiness Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EmeraldGreen.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudDone,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Device & Sync Readiness: Ready",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                        Text(
                            text = "Build: v${BuildConfig.VERSION_NAME} (${BuildConfig.BUILD_TYPE.uppercase()}) · Account: ${userEmail ?: displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Bundle: 56 official topics, 60+ weighted MCQs & 10 past-paper Q&As ready offline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                steps.forEachIndexed { idx, (title, body) ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Close")
            }
        }
    )
}

/**
 * Live ticking Exam Countdown Timer displaying Days, Hours, Minutes, and Seconds to examDateMillis.
 */
@Composable
fun LiveExamCountdownCard(
    examDateMillis: Long,
    crashModeActive: Boolean,
    formattedExamDate: String,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(examDateMillis) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val diffSeconds = ((examDateMillis - nowMillis) / 1000L).coerceAtLeast(0L)
    val days = (diffSeconds / 86_400L).toInt()
    val hours = ((diffSeconds % 86_400L) / 3600L).toInt()
    val minutes = ((diffSeconds % 3600L) / 60L).toInt()
    val seconds = (diffSeconds % 60L).toInt()

    val accent = if (crashModeActive) CoralRed else MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onConfigureClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        colors = CardDefaults.cardColors(
            containerColor = if (crashModeActive) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        )
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (crashModeActive) "EXAM CRASH COUNTDOWN (< 7 DAYS)" else "ICMA M3 EXAM COUNTDOWN",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Target Sitting: $formattedExamDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accent.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "Edit Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CountdownSegmentBox(value = "%02d".format(days), label = "DAYS", accent = accent, modifier = Modifier.weight(1f))
                CountdownSegmentBox(value = "%02d".format(hours), label = "HRS", accent = accent, modifier = Modifier.weight(1f))
                CountdownSegmentBox(value = "%02d".format(minutes), label = "MIN", accent = accent, modifier = Modifier.weight(1f))
                CountdownSegmentBox(value = "%02d".format(seconds), label = "SEC", accent = accent, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CountdownSegmentBox(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Animated Circular Progress Indicator Card for each of the 4 Syllabus Parts (A, B, C, D).
 */
@Composable
fun PartCircularProgressCard(
    stat: PartAnalytics,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = partAccentColor(stat.part)
    val completionFraction = (stat.completedTopics.toFloat() / stat.totalTopics.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val completionPercent = (completionFraction * 100f).roundToInt()
    val animatedFraction by animateFloatAsState(
        targetValue = completionFraction,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "part_ring_${stat.part.id}"
    )
    val trackColor = accent.copy(alpha = 0.16f)

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.part.code,
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accent.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "${stat.part.weightPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Box(
                modifier = Modifier.size(82.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(82.dp)) {
                    val strokePx = 8.dp.toPx()
                    val inset = strokePx / 2f
                    val arcSize = Size(size.width - strokePx, size.height - strokePx)
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                    if (animatedFraction > 0.005f) {
                        drawArc(
                            color = accent,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedFraction,
                            useCenter = false,
                            topLeft = Offset(inset, inset),
                            size = arcSize,
                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$completionPercent%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stat.completedTopics}/${stat.totalTopics}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stat.part.shortTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stat.masteredTopics} mastered · " +
                        if (stat.attemptCount > 0) "${stat.quizAccuracyPercent}% acc" else "Untested",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

