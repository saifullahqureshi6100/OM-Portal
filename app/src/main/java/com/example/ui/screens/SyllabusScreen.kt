package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.SyllabusRepository
import com.example.domain.SrsAndPlannerEngine
import com.example.model.M3Part
import com.example.model.MasteryState
import com.example.model.SrsRating
import com.example.model.SyllabusTopic
import com.example.model.TopicProgress
import com.example.ui.components.MasteryPill
import com.example.ui.components.PartWeightChip
import com.example.ui.components.VoiceReadChip
import com.example.ui.components.partAccentColor
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldGreen
import com.example.voice.VoiceUiState

private enum class TopicFilterMode(val label: String) {
    ALL("All"),
    BOOKMARKED("Bookmarked"),
    DUE_OR_WEAK("Due / Weak"),
    COMPLETED("Completed")
}

private sealed interface SyllabusHierarchyLevel {
    data object SrsSession : SyllabusHierarchyLevel
    data object Overview : SyllabusHierarchyLevel
    data class PartDrillDown(val part: M3Part) : SyllabusHierarchyLevel
    data class TopicDetail(val topic: SyllabusTopic) : SyllabusHierarchyLevel
}

private fun partIcon(part: M3Part): ImageVector = when (part) {
    M3Part.PART_A -> Icons.Filled.PrecisionManufacturing
    M3Part.PART_B -> Icons.Filled.Groups
    M3Part.PART_C -> Icons.Filled.Campaign
    M3Part.PART_D -> Icons.Filled.Insights
}

@Composable
fun SyllabusScreen(
    progressMap: Map<String, TopicProgress>,
    selectedPart: M3Part?,
    selectedTopicId: String?,
    srsSessionActive: Boolean,
    srsQueue: List<SyllabusTopic>,
    voiceState: VoiceUiState,
    onSelectPart: (M3Part?) -> Unit,
    onSelectTopic: (String?) -> Unit,
    onToggleBookmark: (SyllabusTopic) -> Unit,
    onToggleComplete: (SyllabusTopic) -> Unit,
    onSaveNotes: (SyllabusTopic, String) -> Unit,
    onRateSrs: (SyllabusTopic, SrsRating) -> Unit,
    onStartSrs: () -> Unit,
    onCloseSrs: () -> Unit,
    onAskJarvisTopic: (SyllabusTopic) -> Unit,
    onAskJarvisPart: (M3Part) -> Unit,
    onPracticeTopicMcq: (SyllabusTopic) -> Unit,
    onPracticePartMcq: (M3Part) -> Unit,
    onSpeakToggle: (String, String) -> Unit
) {
    val selectedTopic = selectedTopicId?.let { SyllabusRepository.getTopicById(it) }
    val activeLevel: SyllabusHierarchyLevel = when {
        srsSessionActive -> SyllabusHierarchyLevel.SrsSession
        selectedTopic != null -> SyllabusHierarchyLevel.TopicDetail(selectedTopic)
        selectedPart != null -> SyllabusHierarchyLevel.PartDrillDown(selectedPart)
        else -> SyllabusHierarchyLevel.Overview
    }

    AnimatedContent(
        targetState = activeLevel,
        transitionSpec = {
            val depth = { lvl: SyllabusHierarchyLevel ->
                when (lvl) {
                    is SyllabusHierarchyLevel.Overview -> 0
                    is SyllabusHierarchyLevel.PartDrillDown -> 1
                    is SyllabusHierarchyLevel.TopicDetail -> 2
                    is SyllabusHierarchyLevel.SrsSession -> 3
                }
            }
            if (depth(targetState) >= depth(initialState)) {
                (slideInHorizontally { it / 4 } + fadeIn()) togetherWith (slideOutHorizontally { -it / 4 } + fadeOut())
            } else {
                (slideInHorizontally { -it / 4 } + fadeIn()) togetherWith (slideOutHorizontally { it / 4 } + fadeOut())
            }
        },
        label = "syllabus_hierarchy"
    ) { level ->
        when (level) {
            is SyllabusHierarchyLevel.SrsSession -> {
                BackHandler { onCloseSrs() }
                SrsReviewSessionView(
                    queue = srsQueue,
                    progressMap = progressMap,
                    voiceState = voiceState,
                    onRate = onRateSrs,
                    onClose = onCloseSrs,
                    onSpeakToggle = onSpeakToggle
                )
            }

            is SyllabusHierarchyLevel.TopicDetail -> {
                val topic = level.topic
                BackHandler { onSelectTopic(null) }
                val partTopics = remember(topic.part) { SyllabusRepository.getTopicsForPart(topic.part) }
                val idx = partTopics.indexOfFirst { it.id == topic.id }
                val prevTopic = if (idx > 0) partTopics[idx - 1] else null
                val nextTopic = if (idx in 0 until partTopics.lastIndex) partTopics[idx + 1] else null

                TopicDetailView(
                    topic = topic,
                    topicIndexInPart = idx + 1,
                    totalTopicsInPart = partTopics.size,
                    prevTopic = prevTopic,
                    nextTopic = nextTopic,
                    progress = progressMap[topic.id],
                    voiceState = voiceState,
                    onBackToPart = { onSelectTopic(null) },
                    onBackToOverview = { onSelectPart(null) },
                    onSelectSiblingTopic = { siblingId -> onSelectTopic(siblingId) },
                    onToggleBookmark = { onToggleBookmark(topic) },
                    onToggleComplete = { onToggleComplete(topic) },
                    onSaveNotes = { notes -> onSaveNotes(topic, notes) },
                    onRateSrs = { rating -> onRateSrs(topic, rating) },
                    onAskJarvis = { onAskJarvisTopic(topic) },
                    onPracticeMcq = { onPracticeTopicMcq(topic) },
                    onSpeakToggle = onSpeakToggle
                )
            }

            is SyllabusHierarchyLevel.PartDrillDown -> {
                BackHandler { onSelectPart(null) }
                PartTopicsView(
                    part = level.part,
                    progressMap = progressMap,
                    onBackToOverview = { onSelectPart(null) },
                    onSwitchPart = { newPart -> onSelectPart(newPart) },
                    onSelectTopic = { topicId -> onSelectTopic(topicId) },
                    onToggleBookmark = onToggleBookmark,
                    onToggleComplete = onToggleComplete,
                    onStartSrs = onStartSrs,
                    onAskJarvisPart = { onAskJarvisPart(level.part) },
                    onPracticePartMcq = { onPracticePartMcq(level.part) }
                )
            }

            is SyllabusHierarchyLevel.Overview -> {
                SyllabusOverviewView(
                    progressMap = progressMap,
                    onSelectPart = { part -> onSelectPart(part) },
                    onSelectTopic = { topicId -> onSelectTopic(topicId) },
                    onToggleBookmark = onToggleBookmark,
                    onToggleComplete = onToggleComplete,
                    onStartSrs = onStartSrs
                )
            }
        }
    }
}

/**
 * LEVEL 1: Hierarchical M3 Syllabus Overview (Parts A, B, C, D + Weightage Table + Global Search)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SyllabusOverviewView(
    progressMap: Map<String, TopicProgress>,
    onSelectPart: (M3Part) -> Unit,
    onSelectTopic: (String) -> Unit,
    onToggleBookmark: (SyllabusTopic) -> Unit,
    onToggleComplete: (SyllabusTopic) -> Unit,
    onStartSrs: () -> Unit
) {
    var globalQuery by remember { mutableStateOf("") }
    val totalTopics = SyllabusRepository.allTopics.size
    val totalCompleted = progressMap.values.count { it.completed }
    val totalMastered = progressMap.values.count { it.masteryState == MasteryState.MASTERED.name }

    val matchingTopics = remember(globalQuery) {
        if (globalQuery.isBlank()) emptyList()
        else SyllabusRepository.allTopics.filter { topic ->
            topic.title.contains(globalQuery, ignoreCase = true) ||
                topic.subtitle.contains(globalQuery, ignoreCase = true) ||
                topic.keyFramework.contains(globalQuery, ignoreCase = true) ||
                topic.part.title.contains(globalQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("syllabus_overview_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header + SRS Action
        item(key = "overview_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ICMA ML-1 · Paper M3",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "M3 Syllabus Explorer",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalCompleted/$totalTopics completed · $totalMastered mastered · 2025 Scheme",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(
                    onClick = onStartSrs,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("syllabus_srs_button")
                ) {
                    Icon(Icons.Filled.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SRS Deck")
                }
            }
        }

        // Global Topic Search across all 56 topics
        item(key = "overview_search") {
            OutlinedTextField(
                value = globalQuery,
                onValueChange = { globalQuery = it },
                placeholder = { Text("Search all 56 topics across Parts A–D...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search syllabus") },
                trailingIcon = {
                    if (globalQuery.isNotBlank()) {
                        IconButton(
                            onClick = { globalQuery = "" },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("syllabus_search_input")
            )
        }

        if (globalQuery.isNotBlank()) {
            item(key = "search_results_header") {
                Text(
                    text = "${matchingTopics.size} matching topic${if (matchingTopics.size == 1) "" else "s"} across M3 Syllabus",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (matchingTopics.isEmpty()) {
                item(key = "search_empty") {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Text(
                            text = "No topics matched \"$globalQuery\". Clear search to browse Parts A, B, C, and D.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(matchingTopics, key = { "search_${it.id}" }) { topic ->
                    TopicHierarchyCard(
                        topic = topic,
                        progress = progressMap[topic.id],
                        showPartChip = true,
                        onOpen = { onSelectTopic(topic.id) },
                        onToggleComplete = { onToggleComplete(topic) },
                        onToggleBookmark = { onToggleBookmark(topic) }
                    )
                }
            }
        } else {
            // Syllabus Weightage Overview Table (Part A 35%, Part B 20%, Part C 25%, Part D 20%)
            item(key = "weightage_table") {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Syllabus Weightage Overview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Part & Content Area",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Weightage",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        M3Part.entries.forEach { part ->
                            val accent = partAccentColor(part)
                            val count = SyllabusRepository.getTopicsForPart(part).size
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelectPart(part) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(accent)
                                    )
                                    Text(
                                        text = "${part.code} · ${part.title}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "($count)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${part.weightPercent}%",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = accent
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Official Weightage",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "100%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            }

            item(key = "portion_heading") {
                Text(
                    text = "Select a Syllabus Part to Drill Down",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // 4 Interactive Drill-Down Cards for Parts A, B, C, and D
            items(SyllabusRepository.partOverviews, key = { it.part.id }) { meta ->
                val part = meta.part
                val topics = remember(part) { SyllabusRepository.getTopicsForPart(part) }
                val completed = topics.count { progressMap[it.id]?.completed == true }
                val mastered = topics.count { progressMap[it.id]?.masteryState == MasteryState.MASTERED.name }
                val weakOrDue = topics.count { t ->
                    val p = progressMap[t.id]
                    p != null && (p.masteryState == MasteryState.NEEDS_WORK.name ||
                        p.masteryState == MasteryState.REVISION_REQUIRED.name ||
                        SrsAndPlannerEngine.isTopicDue(p))
                }
                val accent = partAccentColor(part)
                val fraction = (completed.toFloat() / topics.size.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectPart(part) }
                        .testTag("part_card_${part.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(accent.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = partIcon(part),
                                    contentDescription = part.title,
                                    tint = accent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${part.code.uppercase()} · ${part.weightPercent}% WEIGHT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = accent,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (weakOrDue > 0) {
                                        Text(
                                            text = "· $weakOrDue due",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AmberGold,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Text(
                                    text = part.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Open ${part.title} topics",
                                tint = accent
                            )
                        }

                        Text(
                            text = meta.breakdownSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            meta.sampleHighlights.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$completed/${topics.size} topics completed · $mastered mastered",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Explore ${topics.size} topics →",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { fraction },
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
    }
}

/**
 * LEVEL 2: Drill-Down into a Specific Syllabus Part (Part A, B, C, or D)
 */
@Composable
private fun PartTopicsView(
    part: M3Part,
    progressMap: Map<String, TopicProgress>,
    onBackToOverview: () -> Unit,
    onSwitchPart: (M3Part) -> Unit,
    onSelectTopic: (String) -> Unit,
    onToggleBookmark: (SyllabusTopic) -> Unit,
    onToggleComplete: (SyllabusTopic) -> Unit,
    onStartSrs: () -> Unit,
    onAskJarvisPart: () -> Unit,
    onPracticePartMcq: () -> Unit
) {
    var searchQuery by remember(part) { mutableStateOf("") }
    var filterMode by remember(part) { mutableStateOf(TopicFilterMode.ALL) }

    val allPartTopics = remember(part) { SyllabusRepository.getTopicsForPart(part) }
    val filteredTopics = remember(allPartTopics, searchQuery, filterMode, progressMap) {
        allPartTopics.filter { topic ->
            val p = progressMap[topic.id]
            val matchesSearch = searchQuery.isBlank() ||
                topic.title.contains(searchQuery, ignoreCase = true) ||
                topic.subtitle.contains(searchQuery, ignoreCase = true) ||
                topic.keyFramework.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (filterMode) {
                TopicFilterMode.ALL -> true
                TopicFilterMode.BOOKMARKED -> p?.bookmarked == true
                TopicFilterMode.COMPLETED -> p?.completed == true
                TopicFilterMode.DUE_OR_WEAK -> p != null && (
                    p.masteryState == MasteryState.NEEDS_WORK.name ||
                        p.masteryState == MasteryState.REVISION_REQUIRED.name ||
                        SrsAndPlannerEngine.isTopicDue(p)
                    )
            }
            matchesSearch && matchesFilter
        }
    }

    val completedCount = allPartTopics.count { progressMap[it.id]?.completed == true }
    val masteredCount = allPartTopics.count { progressMap[it.id]?.masteryState == MasteryState.MASTERED.name }
    val accent = partAccentColor(part)
    val overviewMeta = remember(part) { SyllabusRepository.getPartOverview(part) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Breadcrumb Navigation Header
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBackToOverview,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("part_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to M3 syllabus parts")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "M3 Syllabus",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { onBackToOverview() }
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${part.code} (${part.weightPercent}%)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    FilledTonalButton(
                        onClick = onStartSrs,
                        modifier = Modifier.heightIn(min = 44.dp)
                    ) {
                        Icon(Icons.Filled.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SRS")
                    }
                }

                // Quick Part Switcher Strip (stay at Level 2 while hopping between A, B, C, D)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    M3Part.entries.forEach { p ->
                        FilterChip(
                            selected = p == part,
                            onClick = { onSwitchPart(p) },
                            label = {
                                Text(
                                    text = "${p.code}: ${p.shortTitle} (${p.weightPercent}%)",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.testTag("part_tab_${p.id}")
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("part_topics_list_${part.id}"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Part Banner + Quick Practice/Jarvis Actions
            item(key = "part_summary_card") {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.28f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = partIcon(part),
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${part.code} — ${part.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${part.weightPercent}% Exam Weight · $completedCount/${allPartTopics.size} completed · $masteredCount mastered",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accent
                                )
                            }
                        }

                        Text(
                            text = overviewMeta.breakdownSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        LinearProgressIndicator(
                            progress = { (completedCount.toFloat() / allPartTopics.size.coerceAtLeast(1)).coerceIn(0f, 1f) },
                            color = accent,
                            trackColor = accent.copy(alpha = 0.16f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = onAskJarvisPart,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("part_ask_jarvis_button")
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ask Jarvis")
                            }
                            FilledTonalButton(
                                onClick = onPracticePartMcq,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("part_practice_mcq_button")
                            ) {
                                Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Part MCQs")
                            }
                        }
                    }
                }
            }

            // Search & Filter within Part
            item(key = "part_filters") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Filter ${allPartTopics.size} topics in ${part.shortTitle}...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("part_search_input")
                    )

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TopicFilterMode.entries.forEach { mode ->
                            FilterChip(
                                selected = filterMode == mode,
                                onClick = { filterMode = mode },
                                label = { Text(mode.label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.testTag("part_filter_${mode.name}")
                            )
                        }
                    }
                }
            }

            if (filteredTopics.isEmpty()) {
                item(key = "part_empty_filtered") {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Text(
                            text = "No topics in ${part.title} match the active filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(filteredTopics, key = { it.id }) { topic ->
                    TopicHierarchyCard(
                        topic = topic,
                        progress = progressMap[topic.id],
                        showPartChip = false,
                        onOpen = { onSelectTopic(topic.id) },
                        onToggleComplete = { onToggleComplete(topic) },
                        onToggleBookmark = { onToggleBookmark(topic) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicHierarchyCard(
    topic: SyllabusTopic,
    progress: TopicProgress?,
    showPartChip: Boolean,
    onOpen: () -> Unit,
    onToggleComplete: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val mastery = MasteryState.fromName(progress?.masteryState ?: MasteryState.NOT_STARTED.name)
    val completed = progress?.completed == true
    val bookmarked = progress?.bookmarked == true
    val accent = partAccentColor(topic.part)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("topic_card_${topic.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showPartChip) {
                PartWeightChip(part = topic.part)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onToggleComplete,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (completed) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = if (completed) "Completed" else "Mark complete",
                            tint = if (completed) EmeraldGreen else accent
                        )
                    }
                    Column {
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = topic.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark topic",
                            tint = if (bookmarked) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Open topic",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MasteryPill(state = mastery)
                Text(
                    text = "${topic.difficulty.label} · ${topic.keyFramework}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * LEVEL 3: Topic Detail & Study Workspace with Breadcrumb + Previous/Next Topic Navigation
 */
@Composable
private fun TopicDetailView(
    topic: SyllabusTopic,
    topicIndexInPart: Int,
    totalTopicsInPart: Int,
    prevTopic: SyllabusTopic?,
    nextTopic: SyllabusTopic?,
    progress: TopicProgress?,
    voiceState: VoiceUiState,
    onBackToPart: () -> Unit,
    onBackToOverview: () -> Unit,
    onSelectSiblingTopic: (String) -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleComplete: () -> Unit,
    onSaveNotes: (String) -> Unit,
    onRateSrs: (SrsRating) -> Unit,
    onAskJarvis: () -> Unit,
    onPracticeMcq: () -> Unit,
    onSpeakToggle: (String, String) -> Unit
) {
    val context = LocalContext.current
    val mastery = MasteryState.fromName(progress?.masteryState ?: MasteryState.NOT_STARTED.name)
    var notesDraft by remember(topic.id, progress?.userNotes) {
        mutableStateOf(progress?.userNotes ?: "")
    }
    var importFeedback by remember { mutableStateOf<String?>(null) }

    val textFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val imported = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (imported.isNotBlank()) {
                    val combined = if (notesDraft.isBlank()) imported else "$notesDraft\n\n$imported"
                    notesDraft = combined.take(10_000)
                    importFeedback = "Imported ${imported.length.coerceAtMost(10_000)} characters. Tap Save Notes to sync."
                }
            } catch (e: Exception) {
                importFeedback = "Could not read file: ${e.localizedMessage ?: "Unsupported format"}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Breadcrumb Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackToPart,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("topic_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to ${topic.part.shortTitle} topics")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Syllabus",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { onBackToOverview() }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = topic.part.code,
                        style = MaterialTheme.typography.labelMedium,
                        color = partAccentColor(topic.part),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onBackToPart() }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$topicIndexInPart/$totalTopicsInPart",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = if (progress?.bookmarked == true) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Toggle bookmark",
                        tint = if (progress?.bookmarked == true) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onToggleComplete, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = if (progress?.completed == true) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = "Mark completed",
                        tint = if (progress?.completed == true) EmeraldGreen else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        PartWeightChip(part = topic.part)

        Text(
            text = topic.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MasteryPill(state = mastery)
            Text(
                text = "Framework: ${topic.keyFramework}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        val fullReadText = buildString {
            append("${topic.title}. ${topic.definition} ")
            append("Key concepts: ${topic.keyConcepts.joinToString(". ")}. ")
            append("Revision notes: ${topic.revisionNotes.joinToString(". ")}")
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VoiceReadChip(
                isSpeakingThis = voiceState.isSpeaking && voiceState.activeLabel == topic.id,
                onClick = { onSpeakToggle(topic.id, fullReadText) },
                testTag = "topic_voice_button"
            )
            FilledTonalButton(
                onClick = onAskJarvis,
                modifier = Modifier.testTag("topic_ask_jarvis_button")
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ask Jarvis")
            }
            FilledTonalButton(
                onClick = onPracticeMcq,
                modifier = Modifier.testTag("topic_practice_mcq_button")
            ) {
                Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Practice MCQs")
            }
        }

        if (voiceState.errorMessage != null) {
            Text(
                text = voiceState.errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        SectionCard("Definition", listOf(topic.definition))
        SectionCard("Key Concepts & Exam Points", topic.keyConcepts)
        SectionCard("Real-World Examples", topic.examples)
        SectionCard("Quick Revision Hooks", topic.revisionNotes)

        // Spaced Repetition Self-Rating Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Spaced Repetition (SRS) Recall Check",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                val nextText = if (progress != null && progress.nextReviewAtMillis > 0L) {
                    "Current interval: ${progress.intervalDays}d · Next review: ${SrsAndPlannerEngine.formatLocalDate(progress.nextReviewAtMillis)}"
                } else "Not yet scheduled in SRS. Rate how confident you feel to schedule your next review:"
                Text(
                    text = nextText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SrsRating.entries.forEach { rating ->
                        OutlinedButton(
                            onClick = { onRateSrs(rating) },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("srs_rate_${rating.name}")
                        ) {
                            Text(rating.label, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // User-Pasted / Imported Study Material Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Study Material & Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Paste text from Study Scheme PDFs/Docs or import a .txt/.md file",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = { textFilePicker.launch(arrayOf("text/plain", "text/markdown", "text/*")) },
                        modifier = Modifier.testTag("import_notes_button")
                    ) {
                        Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import")
                    }
                }

                OutlinedTextField(
                    value = notesDraft,
                    onValueChange = { notesDraft = it.take(10_000) },
                    placeholder = { Text("Paste excerpts from your ICMA PDFs, class notes, or Google Doc here...") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("topic_notes_input")
                )

                if (importFeedback != null) {
                    Text(
                        text = importFeedback!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = {
                        onSaveNotes(notesDraft)
                        importFeedback = null
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag("save_topic_notes_button")
                ) {
                    Text("Save Notes to Account")
                }
            }
        }

        // Sibling Topic Navigation (Previous / Next inside current Part)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { prevTopic?.let { onSelectSiblingTopic(it.id) } },
                enabled = prevTopic != null,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("topic_prev_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = prevTopic?.title ?: "Start of ${topic.part.code}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            OutlinedButton(
                onClick = { nextTopic?.let { onSelectSiblingTopic(it.id) } },
                enabled = nextTopic != null,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("topic_next_button")
            ) {
                Text(
                    text = nextTopic?.title ?: "End of ${topic.part.code}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, items: List<String>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            items.forEach { item ->
                Text(
                    text = if (items.size == 1) item else "• $item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SrsReviewSessionView(
    queue: List<SyllabusTopic>,
    progressMap: Map<String, TopicProgress>,
    voiceState: VoiceUiState,
    onRate: (SyllabusTopic, SrsRating) -> Unit,
    onClose: () -> Unit,
    onSpeakToggle: (String, String) -> Unit
) {
    val current = queue.firstOrNull()
    var revealed by remember(current?.id) { mutableStateOf(false) }

    LaunchedEffect(queue.size) {
        revealed = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(48.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close SRS session")
            }
            Text(
                text = "SRS Active Recall (${queue.size} left)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        if (current == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("All queued SRS topics reviewed!", style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = onClose) { Text("Return to Syllabus") }
                }
            }
        } else {
            val p = progressMap[current.id]
            val mastery = MasteryState.fromName(p?.masteryState ?: MasteryState.NOT_STARTED.name)

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PartWeightChip(part = current.part)
                        MasteryPill(state = mastery)
                    }

                    Text(
                        text = current.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Framework Prompt: ${current.keyFramework}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (!revealed) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Try to recall the definition, 3 key concepts, and an exam example before revealing.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { revealed = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("srs_reveal_button")
                        ) {
                            Text("Show Answer & Revision Hooks")
                        }
                    } else {
                        VoiceReadChip(
                            isSpeakingThis = voiceState.isSpeaking && voiceState.activeLabel == "srs_${current.id}",
                            onClick = {
                                onSpeakToggle(
                                    "srs_${current.id}",
                                    "${current.title}. ${current.definition} Revision hooks: ${current.revisionNotes.joinToString(". ")}"
                                )
                            },
                            testTag = "srs_voice_button"
                        )
                        Text(current.definition, style = MaterialTheme.typography.bodyLarge)
                        Text("Key Concepts:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        current.keyConcepts.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
                        Text("Quick Revision:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        current.revisionNotes.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            }

            if (revealed) {
                Text(
                    text = "How well did you recall this topic? Struggling items reappear sooner.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SrsRating.entries.forEach { rating ->
                        Button(
                            onClick = { onRate(current, rating) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (rating) {
                                    SrsRating.AGAIN -> MaterialTheme.colorScheme.error
                                    SrsRating.HARD -> AmberGold
                                    SrsRating.GOOD -> MaterialTheme.colorScheme.primary
                                    SrsRating.EASY -> EmeraldGreen
                                }
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("srs_deck_rate_${rating.name}")
                        ) {
                            Text(rating.label, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
