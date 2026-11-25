package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.data.SyllabusRepository
import com.example.model.M3Part
import com.example.model.SyllabusTopic
import com.example.model.TeachingMode
import com.example.model.TutorMessage
import com.example.voice.VoiceUiState

/**
 * Reusable Chat interface component integrating the 'Tuto-Jarvis' AI & Offline Tutor
 * for ICMA Pakistan ML-1 Paper M3 (Organizational Management, 2025 Study Scheme).
 */
@Composable
fun TutoJarvisChatInterface(
    messages: List<TutorMessage>,
    isStreaming: Boolean,
    useOfflineMode: Boolean,
    activeMode: TeachingMode,
    focusedTopic: SyllabusTopic?,
    errorMessage: String?,
    hasRetryableFailure: Boolean,
    voiceState: VoiceUiState,
    onSelectFocusedTopic: (String?) -> Unit,
    onSendMessage: (String) -> Unit,
    onRetryLastMessage: () -> Unit,
    onSwitchOffline: (Boolean) -> Unit,
    onClearChat: () -> Unit,
    onSpeakToggle: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showTopicPicker by remember { mutableStateOf(false) }
    var copiedMessageId by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    val lastMsgText = messages.lastOrNull()?.text
    LaunchedEffect(messages.size, lastMsgText, errorMessage) {
        val targetIndex = messages.size + (if (errorMessage != null) 1 else 0)
        if (targetIndex > 0) {
            listState.animateScrollToItem((targetIndex - 1).coerceAtLeast(0))
        }
    }

    val quickPrompts = remember(focusedTopic?.id) {
        if (focusedTopic != null) {
            listOf(
                "Explain ${focusedTopic.title} with ICMA M3 exam points",
                "Give a real-world case example for ${focusedTopic.title}",
                "Quiz me with an exam-level MCQ on ${focusedTopic.title}",
                "Summarize ${focusedTopic.keyFramework} for 1-minute revision"
            )
        } else {
            listOf(
                "Compare MRP vs MRP II vs ERP for ICMA M3",
                "Explain Mintzberg's 5 configurations with examples",
                "Test me with an exam-style MCQ on Balanced Scorecard",
                "Explain Algorithmic recruitment bias in Roman Urdu"
            )
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Context & Session Action Strip
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (focusedTopic != null) {
                        partAccentColor(focusedTopic.part).copy(alpha = 0.16f)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .clickable { showTopicPicker = true }
                        .testTag("tutor_pick_topic_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MenuOpen,
                            contentDescription = "Choose M3 syllabus topic focus",
                            tint = if (focusedTopic != null) partAccentColor(focusedTopic.part) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = focusedTopic?.let { "${it.part.code} · ${it.title}" }
                                    ?: "All 56 M3 Topics (Tap to focus)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Mode: ${activeMode.label} · ${if (useOfflineMode) "Offline KB" else "Firebase AI"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        if (focusedTopic != null) {
                            IconButton(
                                onClick = { onSelectFocusedTopic(null) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear topic context",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = messages.size > 1) {
                    IconButton(
                        onClick = onClearChat,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("tutor_new_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddComment,
                            contentDescription = "Start new Tuto-Jarvis chat session",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Conversation Transcript
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (messages.size <= 1) {
                item(key = "quick_prompts_row") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (focusedTopic != null) "Suggested Questions for ${focusedTopic.title}:" else "Try Asking Tuto-Jarvis:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickPrompts.forEachIndexed { idx, prompt ->
                                AssistChip(
                                    onClick = { onSendMessage(prompt) },
                                    enabled = !isStreaming,
                                    label = { Text(prompt, style = MaterialTheme.typography.bodySmall) },
                                    modifier = Modifier.testTag("quick_prompt_$idx")
                                )
                            }
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isThinking = !msg.fromUser && msg.text == "Thinking..."
                val isLastAssistantMsg = !msg.fromUser && msg.id == messages.lastOrNull { !it.fromUser }?.id

                ChatMessageBubble(
                    message = msg,
                    isThinking = isThinking,
                    isCopied = copiedMessageId == msg.id,
                    voiceState = voiceState,
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(msg.text))
                        copiedMessageId = msg.id
                    },
                    onSpeakToggle = { onSpeakToggle(msg.id, msg.text) }
                )

                // Follow-up quick chips right below the latest completed Jarvis reply
                if (isLastAssistantMsg && !isThinking && !isStreaming && msg.id != "welcome") {
                    Row(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val followUps = listOf(
                            "Give a Pakistani corporate example",
                            "Quiz me with an M3 MCQ on this",
                            "Summarize in 3 exam bullet points"
                        )
                        followUps.forEachIndexed { i, followUp ->
                            AssistChip(
                                onClick = { onSendMessage(followUp) },
                                label = { Text(followUp, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.testTag("followup_prompt_$i")
                            )
                        }
                    }
                }
            }

            if (errorMessage != null) {
                item(key = "tutor_error_card") {
                    TutorErrorRecoveryCard(
                        message = errorMessage,
                        canRetryLive = hasRetryableFailure && !useOfflineMode,
                        onRetry = onRetryLastMessage,
                        onUseOffline = { onSwitchOffline(true) }
                    )
                }
            }
        }

        // Composer Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = focusedTopic?.let { "Ask about ${it.title}..." }
                                ?: "Ask Tuto-Jarvis any M3 question...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    maxLines = 4,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            val toSend = inputText.trim()
                            if (toSend.isNotBlank() && !isStreaming) {
                                onSendMessage(toSend)
                                inputText = ""
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tutor_chat_input")
                )

                IconButton(
                    onClick = {
                        val toSend = inputText.trim()
                        if (toSend.isNotBlank()) {
                            onSendMessage(toSend)
                            inputText = ""
                        }
                    },
                    enabled = !isStreaming && inputText.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("tutor_send_button")
                ) {
                    if (isStreaming) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send question to Tuto-Jarvis"
                        )
                    }
                }
            }
        }
    }

    if (showTopicPicker) {
        TopicContextPickerDialog(
            selectedTopicId = focusedTopic?.id,
            onSelectTopic = { id ->
                onSelectFocusedTopic(id)
                showTopicPicker = false
            },
            onDismiss = { showTopicPicker = false }
        )
    }
}

@Composable
private fun ChatMessageBubble(
    message: TutorMessage,
    isThinking: Boolean,
    isCopied: Boolean,
    voiceState: VoiceUiState,
    onCopy: () -> Unit,
    onSpeakToggle: () -> Unit
) {
    val partColor = message.detectedPart?.let { partAccentColor(it) } ?: MaterialTheme.colorScheme.primary
    val bubbleShape = if (message.fromUser) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.fromUser) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp, end = 8.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(partColor, MaterialTheme.colorScheme.primary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "Tuto-Jarvis AI avatar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Surface(
            shape = bubbleShape,
            color = if (message.fromUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            border = if (!message.fromUser) {
                BorderStroke(1.dp, partColor.copy(alpha = 0.25f))
            } else null,
            tonalElevation = if (message.fromUser) 0.dp else 2.dp,
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!message.fromUser) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.detectedPart != null) {
                            PartWeightChip(part = message.detectedPart)
                        }
                        if (message.isOfflineResponse) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "OFFLINE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else if (message.modeLabel.isNotBlank()) {
                            Text(
                                text = message.modeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (isThinking) {
                    StreamingTypingIndicator()
                } else {
                    Text(
                        text = if (message.fromUser) AnnotatedString(message.text) else formatTutorMarkdownLite(message.text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!message.fromUser && !isThinking) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VoiceReadChip(
                            isSpeakingThis = voiceState.isSpeaking && voiceState.activeLabel == message.id,
                            onClick = onSpeakToggle,
                            testTag = "tutor_voice_${message.id}"
                        )
                        AssistChip(
                            onClick = onCopy,
                            label = {
                                Text(
                                    text = if (isCopied) "Copied" else "Copy",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy response",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.testTag("tutor_copy_${message.id}")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamingTypingIndicator() {
    val transition = rememberInfiniteTransition(label = "jarvis_typing")
    val a1 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
        label = "dot1"
    )
    val a2 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(550, delayMillis = 160), RepeatMode.Reverse),
        label = "dot2"
    )
    val a3 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(550, delayMillis = 320), RepeatMode.Reverse),
        label = "dot3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        listOf(a1, a2, a3).forEach { alphaVal ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(alphaVal)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Tuto-Jarvis is thinking...",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TutorErrorRecoveryCard(
    message: String,
    canRetryLive: Boolean,
    onRetry: () -> Unit,
    onUseOffline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (canRetryLive) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("tutor_retry_button")
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry Live AI")
                    }
                }
                OutlinedButton(
                    onClick = onUseOffline,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("tutor_fallback_offline_button")
                ) {
                    Icon(Icons.Filled.WifiOff, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Use Offline Tutor")
                }
            }
        }
    }
}

@Composable
private fun TopicContextPickerDialog(
    selectedTopicId: String?,
    onSelectTopic: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var partFilter by remember { mutableStateOf<M3Part?>(null) }

    val filteredTopics = remember(search, partFilter) {
        SyllabusRepository.allTopics.filter { topic ->
            (partFilter == null || topic.part == partFilter) &&
                (search.isBlank() ||
                    topic.title.contains(search, ignoreCase = true) ||
                    topic.keyFramework.contains(search, ignoreCase = true))
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Focus Tuto-Jarvis on M3 Topic") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Filter 56 M3 topics...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = partFilter == null,
                        onClick = { partFilter = null },
                        label = { Text("All", style = MaterialTheme.typography.labelSmall) }
                    )
                    M3Part.entries.forEach { part ->
                        FilterChip(
                            selected = partFilter == part,
                            onClick = { partFilter = part },
                            label = { Text("${part.shortTitle} ${part.weightPercent}%", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item(key = "all_topics_option") {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTopicId == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectTopic(null) }
                        ) {
                            Text(
                                text = "General M3 Paper Mode (Auto-detect Part)",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    items(filteredTopics, key = { it.id }) { topic ->
                        val isSelected = selectedTopicId == topic.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectTopic(topic.id) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = topic.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${topic.part.code} (${topic.part.weightPercent}%) · ${topic.keyFramework}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Formats bold (**text**), headings (### Heading), and bullet markers cleanly in Compose AnnotatedString.
 */
private fun formatTutorMarkdownLite(raw: String): AnnotatedString = buildAnnotatedString {
    val lines = raw.lines()
    lines.forEachIndexed { index, rawLine ->
        val trimmed = rawLine.trimStart()
        when {
            trimmed.startsWith("### ") || trimmed.startsWith("## ") || trimmed.startsWith("# ") -> {
                val heading = trimmed.trimStart('#', ' ')
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    appendBoldSegments(heading)
                }
            }
            trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                append("• ")
                appendBoldSegments(trimmed.drop(2))
            }
            else -> appendBoldSegments(rawLine)
        }
        if (index < lines.lastIndex) append("\n")
    }
}

private fun AnnotatedString.Builder.appendBoldSegments(text: String) {
    var cursor = 0
    while (cursor < text.length) {
        val start = text.indexOf("**", cursor)
        if (start == -1) {
            append(text.substring(cursor))
            break
        }
        val end = text.indexOf("**", start + 2)
        if (end == -1) {
            append(text.substring(cursor))
            break
        }
        if (start > cursor) {
            append(text.substring(cursor, start))
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(text.substring(start + 2, end))
        }
        cursor = end + 2
    }
}
