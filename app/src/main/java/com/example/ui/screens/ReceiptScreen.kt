package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DvsPrimaryButton
import com.example.ui.components.DvsSecondaryButton
import com.example.ui.theme.DvsAccent
import com.example.ui.theme.DvsAccentSoft
import com.example.ui.theme.DvsBackground
import com.example.ui.theme.DvsBorder
import com.example.ui.theme.DvsMutedText
import com.example.ui.theme.DvsPrimaryText
import com.example.ui.theme.DvsStatusSuccessBg
import com.example.ui.theme.DvsStatusSuccessBorder
import com.example.ui.theme.DvsStatusSuccessText
import com.example.ui.theme.DvsSubtleText
import com.example.ui.theme.DvsSurface
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ManropeFontFamily
import com.example.viewmodel.VoterUiState
import com.example.viewmodel.VoterViewModel

@Composable
fun ReceiptScreen(
    state: VoterUiState,
    viewModel: VoterViewModel,
    modifier: Modifier = Modifier
) {
    val receipt = state.receipt ?: return
    var animateCheckmark by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateCheckmark = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DvsBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Centered Premium Receipt Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = DvsSurface,
            border = BorderStroke(1.dp, DvsBorder),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Smooth checkmark success animation
                AnimatedVisibility(
                    visible = animateCheckmark,
                    enter = scaleIn(animationSpec = tween(400)) + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(DvsStatusSuccessBg)
                            .border(1.dp, DvsStatusSuccessBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = DvsStatusSuccessText,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Voting confirmation heading
                Text(
                    text = "Vote Confirmed",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = (-0.4).sp,
                    color = DvsPrimaryText
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Participation details (strictly no candidate name or blockchain hashes)
                ReceiptDetailRow(label = "Election", value = receipt.electionName)
                Spacer(modifier = Modifier.height(12.dp))
                ReceiptDetailRow(label = "Date & Time", value = receipt.timestamp, isMono = true)
                Spacer(modifier = Modifier.height(12.dp))
                ReceiptDetailRow(label = "Receipt Reference", value = receipt.receiptReference, isMono = true)
                Spacer(modifier = Modifier.height(12.dp))
                ReceiptDetailRow(label = "Status", value = receipt.status, isStatus = true)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Receipt actions: Two equal-width buttons side by side beneath the card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Button 1: Back to Home (requests next eligible election sequentially)
            DvsPrimaryButton(
                text = "Back to Home",
                onClick = { viewModel.backToHomeFromReceipt() },
                modifier = Modifier.weight(1f),
                testTag = "receipt_back_home_button"
            )

            // Button 2: Sign Out (clears session and returns to Screen 1)
            DvsSecondaryButton(
                text = "Sign Out",
                onClick = { viewModel.signOut() },
                modifier = Modifier.weight(1f),
                testTag = "receipt_sign_out_button"
            )
        }
    }
}

@Composable
private fun ReceiptDetailRow(
    label: String,
    value: String,
    isMono: Boolean = false,
    isStatus: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = DvsMutedText
        )

        if (isStatus) {
            Box(
                modifier = Modifier
                    .background(DvsAccentSoft, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = value,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DvsAccent
                )
            }
        } else {
            Text(
                text = value,
                fontFamily = if (isMono) JetBrainsMonoFontFamily else ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = DvsPrimaryText
            )
        }
    }
}
