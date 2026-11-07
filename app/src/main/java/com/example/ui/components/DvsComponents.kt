package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DvsAccent
import com.example.ui.theme.DvsAccentSoft
import com.example.ui.theme.DvsBorder
import com.example.ui.theme.DvsBorderStrong
import com.example.ui.theme.DvsMutedText
import com.example.ui.theme.DvsPrimaryText
import com.example.ui.theme.DvsStatusErrorBg
import com.example.ui.theme.DvsStatusErrorBorder
import com.example.ui.theme.DvsStatusErrorText
import com.example.ui.theme.DvsSubtleText
import com.example.ui.theme.DvsSurface
import com.example.ui.theme.ManropeFontFamily

/**
 * Unified small error notification component throughout the application.
 * Considerably smaller than a modal or dialog.
 * Compact padding, rounded corners, subtle DVS-consistent styling, animated appearance.
 */
@Composable
fun DvsErrorNotification(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (!message.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DvsStatusErrorBg,
                    border = BorderStroke(1.dp, DvsStatusErrorBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.testTag("unified_error_notification")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = DvsStatusErrorText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message,
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = DvsStatusErrorText
                        )
                    }
                }
            }
        }
    }
}

/**
 * Official DVS Civic Logo mark
 */
@Composable
fun DvsLogo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(DvsAccentSoft)
            .border(1.dp, DvsAccent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.HowToVote,
            contentDescription = "DVS Civic Logo",
            tint = DvsAccent,
            modifier = Modifier.size(30.dp)
        )
    }
}

/**
 * Reusable centred modal card with softly dimmed background, solid card, and subtle shadow.
 */
@Composable
fun DvsModalCard(
    onDismissRequest: () -> Unit,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, DvsBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = DvsSurface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    }
}

@Composable
fun DvsPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    testTag: String = "primary_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DvsAccent,
            contentColor = Color.White,
            disabledContainerColor = DvsBorderStrong,
            disabledContentColor = DvsSubtleText
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier
            .height(48.dp)
            .testTag(testTag)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = text,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun DvsSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "secondary_button"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, DvsBorderStrong),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DvsPrimaryText,
            disabledContentColor = DvsSubtleText
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier
            .height(48.dp)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}
