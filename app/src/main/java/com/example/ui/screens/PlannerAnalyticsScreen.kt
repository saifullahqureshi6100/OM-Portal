package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SyllabusRepository
import com.example.domain.SrsAndPlannerEngine
import com.example.model.ExamAttempt
import com.example.model.LeaderboardEntry
import com.example.model.MasteryState
import com.example.model.StudyPlanSummary
import com.example.model.TopicProgress
import com.example.model.UserProfile
import com.example.ui.components.ExamScoreSparklineCanvas
import com.example.ui.components.MasteryBreakdownStrip
import com.example.ui.components.MobileInstallGuideDialog
import com.example.ui.components.PortalGuideDialog
import com.example.ui.components.XpLevelProgressCard
import com.example.ui.components.masteryColor
import com.example.ui.components.partAccentColor
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldGreen
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

@Composable
fun PlannerAnalyticsScreen(
    profile: UserProfile,
    userEmail: String?,
    progressMap: Map<String, TopicProgress>,
    examHistory: List<ExamAttempt>,
    studyPlan: StudyPlanSummary,
    leaderboard: List<LeaderboardEntry>,
    crashPreview: Boolean,
    backupBusy: Boolean,
    onToggleCrashPreview: () -> Unit,
    onUpdatePlanner: (Long, Double, Int) -> Unit,
    onLogBlockDone: (String, Int) -> Unit,
    onOpenTopic: (String) -> Unit,
    onSaveAccountPrefs: (String, Boolean, Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onBuildBackupJson: () -> String,
    onImportBackupJson: (String) -> Unit,
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Planner", "Analytics", "Badges & Rank")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(title, maxLines = 1) },
                    modifier = Modifier.testTag("plan_subtab_$idx")
                )
            }
        }

        when (selectedTab) {
            0 -> StudyPlannerTab(
                profile = profile,
                studyPlan = studyPlan,
                crashPreview = crashPreview,
                onToggleCrashPreview = onToggleCrashPreview,
                onUpdatePlanner = onUpdatePlanner,
                onLogBlockDone = onLogBlockDone,
                onOpenTopic = onOpenTopic
            )
            1 -> AdaptiveAnalyticsTab(
                progressMap = progressMap,
                examHistory = examHistory,
                studyPlan = studyPlan,
                onOpenTopic = onOpenTopic
            )
            else -> GamificationAndAccountTab(
                profile = profile,
                userEmail = userEmail,
                leaderboard = leaderboard,
                backupBusy = backupBusy,
                onSaveAccountPrefs = onSaveAccountPrefs,
                onToggleDarkMode = onToggleDarkMode,
                onBuildBackupJson = onBuildBackupJson,
                onImportBackupJson = onImportBackupJson,
                onSignOut = onSignOut
            )
        }
    }
}

@Composable
private fun StudyPlannerTab(
    profile: UserProfile,
    studyPlan: StudyPlanSummary,
    crashPreview: Boolean,
    onToggleCrashPreview: () -> Unit,
    onUpdatePlanner: (Long, Double, Int) -> Unit,
    onLogBlockDone: (String, Int) -> Unit,
    onOpenTopic: (String) -> Unit
) {
    var daysOutSlider by remember(profile.examDateMillis) {
        mutableFloatStateOf((((profile.examDateMillis - System.currentTimeMillis()) / 86_400_000f).coerceIn(1f, 120f)))
    }
    var dailyHoursSlider by remember(profile.dailyTargetHours) {
        mutableFloatStateOf(profile.dailyTargetHours.toFloat())
    }
    var daysPerWeekSlider by remember(profile.studyDaysPerWeek) {
        mutableFloatStateOf(profile.studyDaysPerWeek.toFloat())
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (studyPlan.crashModeActive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (studyPlan.crashModeActive) "EXAM CRASH MODE ACTIVE (< 7 Days)" else "Weightage-Based Adaptive Planner",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${studyPlan.daysRemaining} days to exam · Weakest priority: ${studyPlan.weakestPart.shortTitle} · All 4 parts protected by mandatory syllabus coverage floor.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = crashPreview,
                            onCheckedChange = { onToggleCrashPreview() },
                            modifier = Modifier.testTag("crash_mode_switch")
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Configure Exam Target", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("Days until exam: ${daysOutSlider.roundToInt()} days", style = MaterialTheme.typography.bodySmall)
                    Slider(value = daysOutSlider, onValueChange = { daysOutSlider = it }, valueRange = 1f..120f)
                    Text("Daily study target: ${"%.1f".format(dailyHoursSlider)} hrs/day", style = MaterialTheme.typography.bodySmall)
                    Slider(value = dailyHoursSlider, onValueChange = { dailyHoursSlider = it }, valueRange = 1f..8f)
                    Text("Study days per week: ${daysPerWeekSlider.roundToInt()} days", style = MaterialTheme.typography.bodySmall)
                    Slider(value = daysPerWeekSlider, onValueChange = { daysPerWeekSlider = it }, valueRange = 1f..7f, steps = 5)
                    Button(
                        onClick = {
                            val nextDate = System.currentTimeMillis() + daysOutSlider.roundToInt() * 86_400_000L
                            onUpdatePlanner(nextDate, dailyHoursSlider.toDouble(), daysPerWeekSlider.roundToInt())
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_planner_button")
                    ) {
                        Text("Recalculate Plan")
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Adaptive Part Time Allocation (Floor-Protected)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    studyPlan.partAnalytics.forEach { stat ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${stat.part.code} (${stat.part.weightPercent}% official)", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "${stat.allocatedPercent}% · ${stat.allocatedMinutesPerDay} min/day",
                                style = MaterialTheme.typography.labelMedium,
                                color = partAccentColor(stat.part),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        items(studyPlan.dailyBlocks) { block ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenTopic(block.topicId) },
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("${block.dayLabel} · ${block.durationMinutes} min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(block.topicTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${block.activityType} — ${block.reason}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(onClick = { onLogBlockDone(block.topicId, block.durationMinutes) }) {
                        Text("Log")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdaptiveAnalyticsTab(
    progressMap: Map<String, TopicProgress>,
    examHistory: List<ExamAttempt>,
    studyPlan: StudyPlanSummary,
    onOpenTopic: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Part-Wise Accuracy & Readiness", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    studyPlan.partAnalytics.forEach { stat ->
                        val accent = partAccentColor(stat.part)
                        val accFrac = (stat.quizAccuracyPercent / 100f).coerceIn(0f, 1f)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${stat.part.shortTitle}${if (stat.isPriorityWeakest) " (Priority)" else ""}", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    if (stat.attemptCount > 0) "${stat.quizAccuracyPercent}% (${stat.attemptCount} Qs)" else "Untested",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accent
                                )
                            }
                            LinearProgressIndicator(
                                progress = { accFrac },
                                color = accent,
                                trackColor = accent.copy(alpha = 0.16f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("SM-2 Mastery Distribution (56 Topics)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    MasteryBreakdownStrip(progressMap = progressMap)
                    Text("Tap any tile below to jump straight to that topic.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SyllabusRepository.allTopics.forEach { topic ->
                            val state = MasteryState.fromName(progressMap[topic.id]?.masteryState ?: MasteryState.NOT_STARTED.name)
                            val color = masteryColor(state)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = color.copy(alpha = 0.25f),
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { onOpenTopic(topic.id) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(topic.part.code.last().toString(), style = MaterialTheme.typography.labelSmall, color = color)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Persisted Exam & Quiz History (${examHistory.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (examHistory.isEmpty()) {
                        Text("No completed attempts yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        ExamScoreSparklineCanvas(attempts = examHistory)
                        examHistory.forEach { a ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${a.mode} · ${a.totalQuestions} Qs", style = MaterialTheme.typography.bodyMedium)
                                Text("${a.scorePercent}% (+${a.xpEarned} XP)", style = MaterialTheme.typography.titleSmall, color = EmeraldGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GamificationAndAccountTab(
    profile: UserProfile,
    userEmail: String?,
    leaderboard: List<LeaderboardEntry>,
    backupBusy: Boolean,
    onSaveAccountPrefs: (String, Boolean, Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onBuildBackupJson: () -> String,
    onImportBackupJson: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    var aliasDraft by remember(profile.displayName) { mutableStateOf(profile.displayName) }
    var optInDraft by remember(profile.leaderboardOptIn) { mutableStateOf(profile.leaderboardOptIn) }
    var voiceDraft by remember(profile.voiceEnabled) { mutableStateOf(profile.voiceEnabled) }
    var showGuide by remember { mutableStateOf(false) }
    var showMobileGuide by remember { mutableStateOf(false) }
    var backupStatusMessage by remember { mutableStateOf<String?>(null) }

    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                val payload = onBuildBackupJson()
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.bufferedWriter(Charsets.UTF_8).use { writer ->
                        writer.write(payload)
                    }
                } ?: error("Unable to open destination file.")
            }.onSuccess {
                backupStatusMessage = "Backup JSON exported successfully."
            }.onFailure { err ->
                backupStatusMessage = "Export failed: ${err.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val buffer = ByteArray(8192)
                    val out = ByteArrayOutputStream()
                    var total = 0
                    while (true) {
                        val read = stream.read(buffer)
                        if (read <= 0) break
                        total += read
                        require(total <= 2 * 1024 * 1024) { "Backup file exceeds 2 MB limit." }
                        out.write(buffer, 0, read)
                    }
                    out.toString(Charsets.UTF_8.name())
                } ?: error("Unable to open selected file.")
            }.onSuccess { rawJson ->
                backupStatusMessage = null
                onImportBackupJson(rawJson)
            }.onFailure { err ->
                backupStatusMessage = "Import failed: ${err.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            XpLevelProgressCard(
                xp = profile.xp,
                badgesCount = profile.earnedBadges.size,
                totalBadges = SyllabusRepository.badges.size,
                streakDays = profile.streakDays
            )
        }

        // Account & Settings
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Account & Portal Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(userEmail ?: "Signed in with Google", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("sign_out_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Out")
                        }
                    }

                    OutlinedTextField(
                        value = aliasDraft,
                        onValueChange = { aliasDraft = it.take(50) },
                        label = { Text("Study Display Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_alias_input")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Dark Theme", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = profile.darkModeEnabled, onCheckedChange = onToggleDarkMode)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Voice Tutor (Male English TTS)", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = voiceDraft, onCheckedChange = { voiceDraft = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Opt-In Public Leaderboard", style = MaterialTheme.typography.bodyMedium)
                            Text("Shares only your display alias, XP, streak & badge count (never email).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = optInDraft,
                            onCheckedChange = { optInDraft = it },
                            modifier = Modifier.testTag("leaderboard_optin_switch")
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showGuide = true },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("portal_guide_button")
                        ) {
                            Text("How to Use Portal")
                        }
                        OutlinedButton(
                            onClick = { showMobileGuide = true },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("install_apk_button")
                        ) {
                            Icon(Icons.Filled.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Install APK")
                        }
                        Button(
                            onClick = { onSaveAccountPrefs(aliasDraft, optInDraft, voiceDraft) },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("save_account_settings_button")
                        ) {
                            Text("Save Settings")
                        }
                    }
                }
            }
        }

        // Portable JSON Backup & Restore
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Backup & Portable Data (JSON)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Export all 56-topic mastery, SM-2 intervals, custom notes, XP, badges, and exam history as a .json file, or restore a previous backup into your synced account.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                exportBackupLauncher.launch("icma_m3_backup_${SrsAndPlannerEngine.computeTodayIso()}.json")
                            },
                            enabled = !backupBusy,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("export_backup_button")
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export JSON")
                        }
                        OutlinedButton(
                            onClick = {
                                importBackupLauncher.launch(arrayOf("application/json", "text/plain"))
                            },
                            enabled = !backupBusy,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("import_backup_button")
                        ) {
                            Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import JSON")
                        }
                    }
                    if (backupBusy) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Text(
                                text = "Validating and restoring backup to cloud...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    backupStatusMessage?.let { status ->
                        val isError = status.startsWith("Export failed") || status.startsWith("Import failed")
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isError) MaterialTheme.colorScheme.error else EmeraldGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Badges
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = AmberGold)
                        Text(
                            text = "Milestones & Badges (${profile.earnedBadges.size}/${SyllabusRepository.badges.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    SyllabusRepository.badges.forEach { badge ->
                        val unlocked = profile.earnedBadges.contains(badge.id)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (unlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (unlocked) Icons.Filled.Verified else Icons.Filled.Lock,
                                    contentDescription = if (unlocked) "Unlocked" else "Locked",
                                    tint = if (unlocked) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(badge.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(badge.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("+${badge.bonusXp} XP", style = MaterialTheme.typography.labelSmall, color = AmberGold)
                            }
                        }
                    }
                }
            }
        }

        // Opt-In Leaderboard
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ICMA M3 Opt-In Leaderboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (leaderboard.isEmpty()) {
                        Text(
                            text = "No opted-in students yet. Enable 'Opt-In Public Leaderboard' above to join the standings!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        leaderboard.forEachIndexed { idx, entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (idx == 0) AmberGold else MaterialTheme.colorScheme.surfaceContainerHigh),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${idx + 1}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text(entry.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("${entry.badgeCount} badges · ${entry.masteredCount} mastered · ${entry.streakDays}d streak", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                                    Text("${entry.xp} XP", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGuide) {
        PortalGuideDialog(
            onDismiss = { showGuide = false },
            onOpenMobileInstallGuide = {
                showGuide = false
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
