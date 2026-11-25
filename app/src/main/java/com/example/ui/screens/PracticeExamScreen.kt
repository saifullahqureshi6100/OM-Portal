package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.QuestionBank
import com.example.data.SyllabusRepository
import com.example.model.M3Part
import com.example.model.McqQuestion
import com.example.model.QuestionDifficulty
import com.example.model.SyllabusTopic
import com.example.ui.components.ErrorBannerCard
import com.example.ui.components.PartWeightChip
import com.example.ui.components.VoiceReadChip
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.viewmodel.McqPracticeState
import com.example.viewmodel.MockExamSessionState
import com.example.voice.VoiceUiState

@Composable
fun PracticeExamScreen(
    mcqState: McqPracticeState,
    mockState: MockExamSessionState,
    voiceState: VoiceUiState,
    initialSubTab: Int = 0,
    onLoadOfflineMcqs: (M3Part?, String?, QuestionDifficulty?, Int) -> Unit,
    onGenerateAiMcqs: (M3Part, SyllabusTopic?, QuestionDifficulty, Int) -> Unit,
    onAnswerPracticeMcq: (McqQuestion, Int) -> Unit,
    onStartMockExam: (Int) -> Unit,
    onSelectMockAnswer: (String, Int) -> Unit,
    onToggleMockFlag: (String) -> Unit,
    onJumpMockQuestion: (Int) -> Unit,
    onSubmitMockExam: () -> Unit,
    onExitMockExam: () -> Unit,
    onOpenTopicFromResults: (String) -> Unit,
    onSpeakToggle: (String, String) -> Unit
) {
    if (mockState.active) {
        BackHandler { onExitMockExam() }
        if (mockState.submitted && mockState.lastAttemptResult != null) {
            MockExamResultsView(
                state = mockState,
                onOpenTopic = { id ->
                    onExitMockExam()
                    onOpenTopicFromResults(id)
                },
                onDone = onExitMockExam
            )
        } else {
            ActiveMockExamView(
                state = mockState,
                onSelectAnswer = onSelectMockAnswer,
                onToggleFlag = onToggleMockFlag,
                onJump = onJumpMockQuestion,
                onSubmit = onSubmitMockExam,
                onExit = onExitMockExam
            )
        }
        return
    }

    var selectedTab by remember(initialSubTab) { mutableIntStateOf(initialSubTab) }
    val tabs = listOf("MCQ Practice", "Mock Exam", "Past Cases")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { idx, label ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(label, maxLines = 1) },
                    modifier = Modifier.testTag("exam_subtab_$idx")
                )
            }
        }

        when (selectedTab) {
            0 -> McqPracticeView(
                state = mcqState,
                voiceState = voiceState,
                onLoadOffline = onLoadOfflineMcqs,
                onGenerateAi = onGenerateAiMcqs,
                onAnswer = onAnswerPracticeMcq,
                onSpeakToggle = onSpeakToggle
            )
            1 -> MockExamLauncherView(onStartMockExam = onStartMockExam)
            else -> PastPaperCaseVaultView(voiceState = voiceState, onSpeakToggle = onSpeakToggle)
        }
    }
}

@Composable
private fun McqPracticeView(
    state: McqPracticeState,
    voiceState: VoiceUiState,
    onLoadOffline: (M3Part?, String?, QuestionDifficulty?, Int) -> Unit,
    onGenerateAi: (M3Part, SyllabusTopic?, QuestionDifficulty, Int) -> Unit,
    onAnswer: (McqQuestion, Int) -> Unit,
    onSpeakToggle: (String, String) -> Unit
) {
    var selectedPart by remember { mutableStateOf<M3Part?>(null) }
    var selectedDifficulty by remember { mutableStateOf<QuestionDifficulty?>(null) }

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
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Intelligent MCQ Generator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedPart == null,
                            onClick = { selectedPart = null },
                            label = { Text("All Parts") }
                        )
                        M3Part.entries.forEach { part ->
                            FilterChip(
                                selected = selectedPart == part,
                                onClick = { selectedPart = part },
                                label = { Text("${part.shortTitle} (${part.weightPercent}%)") }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedDifficulty == null,
                            onClick = { selectedDifficulty = null },
                            label = { Text("Any Level") }
                        )
                        QuestionDifficulty.entries.forEach { diff ->
                            FilterChip(
                                selected = selectedDifficulty == diff,
                                onClick = { selectedDifficulty = diff },
                                label = { Text(diff.label) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onLoadOffline(selectedPart, null, selectedDifficulty, 8) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("load_offline_mcqs_button")
                        ) {
                            Text("Offline Bank (8)")
                        }
                        Button(
                            onClick = {
                                val targetPart = selectedPart ?: M3Part.PART_A
                                val targetDiff = selectedDifficulty ?: QuestionDifficulty.EXAM_LEVEL
                                onGenerateAi(targetPart, null, targetDiff, 5)
                            },
                            enabled = !state.isGeneratingAi,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("generate_ai_mcqs_button")
                        ) {
                            if (state.isGeneratingAi) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Generate (5)")
                            }
                        }
                    }

                    if (state.errorMessage != null) {
                        ErrorBannerCard(message = state.errorMessage)
                    }
                }
            }
        }

        items(state.questions, key = { it.id }) { q ->
            val selectedOpt = state.selectedAnswers[q.id]
            val answered = selectedOpt != null

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PartWeightChip(part = q.part)
                        Surface(
                            color = if (q.isPastPaper) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = q.sourceLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (q.isPastPaper) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "${q.topicTitle} · ${q.difficulty.label}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = q.question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    q.options.forEachIndexed { idx, opt ->
                        val isSelected = selectedOpt == idx
                        val isCorrect = idx == q.correctIndex
                        val bg = when {
                            !answered -> MaterialTheme.colorScheme.surfaceContainerLow
                            isCorrect -> EmeraldGreen.copy(alpha = 0.2f)
                            isSelected -> CoralRed.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceContainerLow
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = bg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !answered) { onAnswer(q, idx) }
                                .testTag("mcq_opt_${q.id}_$idx")
                        ) {
                            Text(
                                text = "${'A' + idx}) $opt",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    if (answered) {
                        Text(
                            text = if (selectedOpt == q.correctIndex) "Correct! ${q.explanation}" else "Incorrect. ${q.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selectedOpt == q.correctIndex) EmeraldGreen else MaterialTheme.colorScheme.error
                        )
                        VoiceReadChip(
                            isSpeakingThis = voiceState.isSpeaking && voiceState.activeLabel == q.id,
                            onClick = { onSpeakToggle(q.id, "${q.question}. Explanation: ${q.explanation}") },
                            testTag = "mcq_voice_${q.id}"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MockExamLauncherView(onStartMockExam: (Int) -> Unit) {
    val options = listOf(20, 40, 60, 100)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ICMA M3 Full Weighted Mock Exam",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Every mock exam enforces the exact 2025 Syllabus Weightage: Part A Operations (35%), Part B HRM (20%), Part C Marketing (25%), and Part D Performance (20%), with live countdown timer, review flags, and automatic SRS calibration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(options) { count ->
            val ops = (count * 0.35).toInt()
            val hr = (count * 0.20).toInt()
            val mkt = (count * 0.25).toInt()
            val perf = count - ops - hr - mkt
            val minutes = (count * 1.5).toInt()

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$count-Question Mock Exam",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Time limit: $minutes min · OPS $ops · HR $hr · MKT $mkt · PERF $perf",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { onStartMockExam(count) },
                        modifier = Modifier.testTag("start_mock_$count")
                    ) {
                        Text("Start")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveMockExamView(
    state: MockExamSessionState,
    onSelectAnswer: (String, Int) -> Unit,
    onToggleFlag: (String) -> Unit,
    onJump: (Int) -> Unit,
    onSubmit: () -> Unit,
    onExit: () -> Unit
) {
    val q = state.questions.getOrNull(state.currentIndex) ?: return
    val mins = state.secondsRemaining / 60
    val secs = state.secondsRemaining % 60
    var showJumpGrid by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onExit) { Text("Quit") }
            Surface(
                color = if (state.secondsRemaining < 120) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Timer, contentDescription = "Timer", modifier = Modifier.size(16.dp))
                    Text(
                        text = "%02d:%02d".format(mins, secs),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.testTag("submit_mock_exam_button")
            ) {
                Text("Submit")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${state.currentIndex + 1} of ${state.questions.size} (${state.selectedAnswers.size} answered)",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedButton(onClick = { showJumpGrid = !showJumpGrid }) {
                Text(if (showJumpGrid) "Hide Navigator" else "Jump Grid")
            }
        }

        if (showJumpGrid) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .height(140.dp)
            ) {
                state.questions.forEachIndexed { idx, item ->
                    val answered = state.selectedAnswers.containsKey(item.id)
                    val flagged = state.flaggedIds.contains(item.id)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            idx == state.currentIndex -> MaterialTheme.colorScheme.primary
                            flagged -> AmberGold
                            answered -> EmeraldGreen.copy(alpha = 0.35f)
                            else -> MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clickable {
                                onJump(idx)
                                showJumpGrid = false
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("${idx + 1}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PartWeightChip(part = q.part)
                    IconButton(onClick = { onToggleFlag(q.id) }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = if (state.flaggedIds.contains(q.id)) Icons.Filled.Flag else Icons.Filled.OutlinedFlag,
                            contentDescription = "Mark for review",
                            tint = if (state.flaggedIds.contains(q.id)) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "${q.topicTitle} · ${q.sourceLabel}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = q.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                val chosen = state.selectedAnswers[q.id]
                q.options.forEachIndexed { idx, opt ->
                    val isSelected = chosen == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAnswer(q.id, idx) }
                            .testTag("mock_opt_$idx")
                    ) {
                        Text(
                            text = "${'A' + idx}) $opt",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { onJump(state.currentIndex - 1) },
                enabled = state.currentIndex > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text("Previous")
            }
            Button(
                onClick = {
                    if (state.currentIndex < state.questions.lastIndex) onJump(state.currentIndex + 1)
                    else onSubmit()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (state.currentIndex < state.questions.lastIndex) "Next" else "Finish & Grade")
            }
        }
    }
}

@Composable
private fun MockExamResultsView(
    state: MockExamSessionState,
    onOpenTopic: (String) -> Unit,
    onDone: () -> Unit
) {
    val result = state.lastAttemptResult ?: return
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                        Text("Mock Exam Results", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${result.scorePercent}% (${result.correctCount}/${result.totalQuestions} correct)",
                        style = MaterialTheme.typography.displaySmall,
                        color = if (result.scorePercent >= 50) EmeraldGreen else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Incorrect: ${result.incorrectCount} · Skipped: ${result.skippedCount} · Earned +${result.xpEarned} XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Part Breakdown:\n• Operations (35%): ${result.partACorrect}/${result.partATotal}\n• HRM (20%): ${result.partBCorrect}/${result.partBTotal}\n• Marketing (25%): ${result.partCCorrect}/${result.partCTotal}\n• Performance (20%): ${result.partDCorrect}/${result.partDTotal}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (result.weakTopicIds.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Recommended Next Study Topics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        result.weakTopicIds.take(6).forEach { topicId ->
                            val t = SyllabusRepository.getTopicById(topicId)
                            if (t != null) {
                                OutlinedButton(
                                    onClick = { onOpenTopic(t.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Review: ${t.title} (${t.part.shortTitle})")
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Back to Exam Center")
            }
        }
    }
}

@Composable
private fun PastPaperCaseVaultView(
    voiceState: VoiceUiState,
    onSpeakToggle: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(QuestionBank.pastPaperDescriptiveQuestions, key = { it.id }) { item ->
            var showSolution by remember(item.id) { mutableStateOf(false) }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PartWeightChip(part = item.part)
                        Text(
                            text = "${item.examRef} · ${item.marks} marks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(item.prompt, style = MaterialTheme.typography.bodyMedium)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { showSolution = !showSolution }) {
                            Text(if (showSolution) "Hide Solution" else "Suggested Answer")
                        }
                        if (showSolution) {
                            VoiceReadChip(
                                isSpeakingThis = voiceState.isSpeaking && voiceState.activeLabel == item.id,
                                onClick = { onSpeakToggle(item.id, item.suggestedSolution) },
                                testTag = "pp_voice_${item.id}"
                            )
                        }
                    }

                    if (showSolution) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = item.suggestedSolution,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
