package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SyllabusRepository
import com.example.model.TeachingMode
import com.example.model.TutorLanguage
import com.example.model.UserProfile
import com.example.ui.components.TutoJarvisChatInterface
import com.example.viewmodel.TutorUiState
import com.example.voice.VoiceUiState

@Composable
fun TutorScreen(
    profile: UserProfile,
    tutorState: TutorUiState,
    voiceState: VoiceUiState,
    onSelectMode: (TeachingMode) -> Unit,
    onSelectLanguage: (TutorLanguage) -> Unit,
    onToggleOffline: (Boolean) -> Unit,
    onSelectFocusedTopic: (String?) -> Unit,
    onSendMessage: (String) -> Unit,
    onRetryLastMessage: () -> Unit,
    onClearChat: () -> Unit,
    onSpeakToggle: (String, String) -> Unit
) {
    val activeMode = TeachingMode.fromLabel(profile.preferredTeachingMode)
    val activeLang = TutorLanguage.fromId(profile.preferredLanguage)
    val focusedTopic = tutorState.focusedTopicId?.let { SyllabusRepository.getTopicById(it) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Tutor Configuration Strip
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tuto-Jarvis · M3 AI & Offline Tutor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${profile.learnedTopics.size}/56 topics explored · ${if (tutorState.useOfflineMode) "Offline Syllabus Mode" else "Firebase AI (Gemini 3.8 Flash)"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Offline", style = MaterialTheme.typography.labelSmall)
                        Switch(
                            checked = tutorState.useOfflineMode,
                            onCheckedChange = onToggleOffline,
                            modifier = Modifier.testTag("tutor_offline_switch")
                        )
                    }
                }

                // Language selector
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TutorLanguage.entries.forEach { lang ->
                        FilterChip(
                            selected = activeLang == lang,
                            onClick = { onSelectLanguage(lang) },
                            label = { Text(lang.label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("tutor_lang_${lang.id}")
                        )
                    }
                }

                // 8 Teaching Modes
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TeachingMode.entries.forEach { mode ->
                        FilterChip(
                            selected = activeMode == mode,
                            onClick = { onSelectMode(mode) },
                            label = { Text(mode.label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("tutor_mode_${mode.name}")
                        )
                    }
                }
            }
        }

        TutoJarvisChatInterface(
            messages = tutorState.messages,
            isStreaming = tutorState.isStreaming,
            useOfflineMode = tutorState.useOfflineMode,
            activeMode = activeMode,
            focusedTopic = focusedTopic,
            errorMessage = tutorState.errorMessage,
            hasRetryableFailure = tutorState.lastFailedQuery != null,
            voiceState = voiceState,
            onSelectFocusedTopic = onSelectFocusedTopic,
            onSendMessage = onSendMessage,
            onRetryLastMessage = onRetryLastMessage,
            onSwitchOffline = onToggleOffline,
            onClearChat = onClearChat,
            onSpeakToggle = onSpeakToggle,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}
