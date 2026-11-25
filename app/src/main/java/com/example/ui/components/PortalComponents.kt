package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.M3Part
import com.example.model.MasteryState
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PartAOpsColor
import com.example.ui.theme.PartBHrColor
import com.example.ui.theme.PartCMktColor
import com.example.ui.theme.PartDPerfColor

fun partAccentColor(part: M3Part): Color = when (part) {
    M3Part.PART_A -> PartAOpsColor
    M3Part.PART_B -> PartBHrColor
    M3Part.PART_C -> PartCMktColor
    M3Part.PART_D -> PartDPerfColor
}

fun masteryColor(state: MasteryState): Color = when (state) {
    MasteryState.NOT_STARTED -> Color(0xFF64748B)
    MasteryState.LEARNING -> PartAOpsColor
    MasteryState.NEEDS_WORK -> CoralRed
    MasteryState.REVISION_REQUIRED -> AmberGold
    MasteryState.MASTERED -> EmeraldGreen
}

@Composable
fun PartWeightChip(part: M3Part, modifier: Modifier = Modifier) {
    val accent = partAccentColor(part)
    Surface(
        modifier = modifier,
        color = accent.copy(alpha = 0.16f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "${part.code} · ${part.shortTitle} (${part.weightPercent}%)",
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun MasteryPill(state: MasteryState, modifier: Modifier = Modifier) {
    val color = masteryColor(state)
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = state.label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VoiceReadChip(
    isSpeakingThis: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        modifier = modifier.testTag(testTag),
        label = {
            Text(
                text = if (isSpeakingThis) "Stop Voice" else "Read Aloud",
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = if (isSpeakingThis) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (isSpeakingThis) "Stop voice playback" else "Read aloud with voice tutor",
                modifier = Modifier.size(18.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSpeakingThis) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
            labelColor = if (isSpeakingThis) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
            leadingIconContentColor = if (isSpeakingThis) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun LoadingStateCard(message: String = "Syncing your ICMA M3 study workspace...") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorBannerCard(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.WarningAmber,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            if (onRetry != null) {
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}
