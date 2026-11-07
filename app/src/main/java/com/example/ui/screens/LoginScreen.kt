package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DvsLogo
import com.example.ui.components.DvsModalCard
import com.example.ui.components.DvsPrimaryButton
import com.example.ui.theme.DvsAccent
import com.example.ui.theme.DvsAccentSoft
import com.example.ui.theme.DvsBackground
import com.example.ui.theme.DvsBorder
import com.example.ui.theme.DvsBorderStrong
import com.example.ui.theme.DvsMutedText
import com.example.ui.theme.DvsPrimaryText
import com.example.ui.theme.DvsStatusSuccessBg
import com.example.ui.theme.DvsStatusSuccessText
import com.example.ui.theme.DvsSubtleText
import com.example.ui.theme.DvsSurface
import com.example.ui.theme.DvsSurfaceSubtle
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ManropeFontFamily
import com.example.viewmodel.VoterUiState
import com.example.viewmodel.VoterViewModel

@Composable
fun LoginScreen(
    state: VoterUiState,
    viewModel: VoterViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DvsBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Official DVS Logo at the top
        DvsLogo(modifier = Modifier.testTag("dvs_official_logo"))

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Welcome heading
        Text(
            text = "Voter Login",
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = (-0.5).sp,
            color = DvsPrimaryText
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Short supporting subtitle
        Text(
            text = "Enter your 12-digit Voter ID to begin.",
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = DvsMutedText
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 4. 12-digit Voter ID input field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = DvsSurface,
            border = BorderStroke(1.dp, DvsBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Voter ID",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = DvsPrimaryText
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.voterIdInput,
                    onValueChange = { viewModel.onVoterIdChanged(it) },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "12-digit Voter ID",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 15.sp,
                            color = DvsSubtleText
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DvsPrimaryText
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (state.voterIdInput.length == 12) viewModel.signIn()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DvsAccent,
                        unfocusedBorderColor = DvsBorderStrong,
                        focusedContainerColor = DvsSurface,
                        unfocusedContainerColor = DvsSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voter_id_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 5. Sign In button (remains disabled until exactly 12 digits entered)
                DvsPrimaryButton(
                    text = "Sign In",
                    onClick = { viewModel.signIn() },
                    enabled = state.voterIdInput.length == 12,
                    isLoading = state.isLoggingIn,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "sign_in_button"
                )
            }
        }

        // Verification shortcuts for easy scenario testing (hidden on UI by default)
        if (com.example.data.api.DvsServerConfig.SHOW_DEV_TEST_ACTIONS) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Testing Shortcuts",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = DvsSubtleText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TestShortcutChip(
                    label = "Eligible",
                    id = "111122223333",
                    onClick = { viewModel.selectPresetId("111122223333") },
                    modifier = Modifier.weight(1f)
                )
                TestShortcutChip(
                    label = "Not Found",
                    id = "777777777777",
                    onClick = { viewModel.selectPresetId("777777777777") },
                    modifier = Modifier.weight(1f)
                )
                TestShortcutChip(
                    label = "Flagged",
                    id = "123456789012",
                    onClick = { viewModel.selectPresetId("123456789012") },
                    modifier = Modifier.weight(1f)
                )
                TestShortcutChip(
                    label = "Net Error",
                    id = "888888888888",
                    onClick = { viewModel.selectPresetId("888888888888") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // ==========================================
    // POPUP 1: REGISTERED VOTER / WALLET CONNECT
    // ==========================================
    if (state.showWalletPopup && state.currentVoter != null) {
        val voter = state.currentVoter
        DvsModalCard(
            onDismissRequest = { viewModel.dismissWalletPopup() }
        ) {
            Text(
                text = "Registered Voter",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.3).sp,
                color = DvsPrimaryText
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Information displayed: ONLY Voter ID, Voter name, Registered phone number
            PopupInfoRow(label = "Voter ID", value = voter.id, isMono = true)
            Spacer(modifier = Modifier.height(10.dp))
            PopupInfoRow(label = "Voter Name", value = voter.name)
            Spacer(modifier = Modifier.height(10.dp))
            PopupInfoRow(label = "Phone", value = voter.phone, isMono = true)

            Spacer(modifier = Modifier.height(20.dp))

            if (state.walletSuccessAnim) {
                // Short, elegant connection-success animation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(DvsStatusSuccessBg, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = DvsStatusSuccessText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DVS Wallet Connected",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DvsStatusSuccessText
                        )
                    }
                }
            } else {
                DvsPrimaryButton(
                    text = "Connect to Wallet",
                    onClick = { viewModel.connectWallet(simulateFailure = false) },
                    isLoading = state.isWalletConnecting,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "connect_wallet_button"
                )

                // Option to verify failure scenario for reviewers (hidden on UI by default)
                if (com.example.data.api.DvsServerConfig.SHOW_DEV_TEST_ACTIONS) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Test Connection Failure",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = DvsSubtleText,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { viewModel.connectWallet(simulateFailure = true) }
                            .padding(4.dp)
                            .testTag("test_wallet_failure_button")
                    )
                }
            }
        }
    }
}

@Composable
private fun PopupInfoRow(
    label: String,
    value: String,
    isMono: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = DvsMutedText
        )
        Text(
            text = value,
            fontFamily = if (isMono) JetBrainsMonoFontFamily else ManropeFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = DvsPrimaryText
        )
    }
}

@Composable
private fun TestShortcutChip(
    label: String,
    id: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, DvsBorder),
        color = DvsSurfaceSubtle,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = DvsMutedText
            )
        }
    }
}
