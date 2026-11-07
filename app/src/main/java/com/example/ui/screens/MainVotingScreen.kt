package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Candidate
import com.example.ui.components.DvsModalCard
import com.example.ui.components.DvsPrimaryButton
import com.example.ui.components.DvsSecondaryButton
import com.example.ui.theme.DvsAccent
import com.example.ui.theme.DvsAccentBorder
import com.example.ui.theme.DvsAccentSoft
import com.example.ui.theme.DvsBackground
import com.example.ui.theme.DvsBorder
import com.example.ui.theme.DvsBorderStrong
import com.example.ui.theme.DvsMutedText
import com.example.ui.theme.DvsPrimaryText
import com.example.ui.theme.DvsStatusWarningBg
import com.example.ui.theme.DvsStatusWarningBorder
import com.example.ui.theme.DvsStatusWarningText
import com.example.ui.theme.DvsSubtleText
import com.example.ui.theme.DvsSurface
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ManropeFontFamily
import com.example.viewmodel.VoterUiState
import com.example.viewmodel.VoterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainVotingScreen(
    state: VoterUiState,
    viewModel: VoterViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DvsBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // A. Minimal Top Navigation: Profile icon (left), Sign-out icon (right)
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.openProfile() },
                        modifier = Modifier.testTag("top_bar_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = DvsPrimaryText
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.signOut() },
                        modifier = Modifier.testTag("top_bar_signout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = DvsMutedText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DvsBackground
                )
            )

            if (!state.hasRemainingElections) {
                // ==========================================
                // SECTION 3: ALREADY PARTICIPATED STATE
                // ==========================================
                AlreadyParticipatedContent(state = state)
            } else {
                // Main voting interface
                val election = state.currentElection!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        // B. Election Information Card (Larger premium card showing ONLY name and date)
                        ElectionInfoCard(
                            electionName = election.name,
                            electionDate = election.date
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // C. Candidate List: Spacious, full-width vertical candidate cards
                    items(election.candidates, key = { it.id }) { candidate ->
                        val isSelected = state.selectedCandidate?.id == candidate.id
                        CandidateCard(
                            candidate = candidate,
                            isSelected = isSelected,
                            onSelect = { viewModel.selectCandidate(candidate) }
                        )
                    }

                    item {
                        // Generous space at bottom so candidate content is not covered by the Vote button
                        Spacer(modifier = Modifier.height(84.dp))
                    }
                }
            }
        }

        // D. Animated Vote Button: Revealed via smooth upward slide-in when candidate selected
        if (state.hasRemainingElections) {
            AnimatedVisibility(
                visible = state.selectedCandidate != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                DvsPrimaryButton(
                    text = "Vote",
                    onClick = { viewModel.openVoteConfirmation() },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "main_screen_vote_button"
                )
            }
        }
    }

    // ==========================================
    // POPUP 2: VOTER PROFILE
    // ==========================================
    if (state.showProfilePopup && state.currentVoter != null) {
        val voter = state.currentVoter
        DvsModalCard(
            onDismissRequest = { viewModel.dismissProfile() }
        ) {
            Text(
                text = "Voter Profile",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.3).sp,
                color = DvsPrimaryText
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileRow(label = "Voter ID", value = voter.id, isMono = true)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileRow(label = "Name", value = voter.name)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileRow(label = "Phone", value = voter.phone, isMono = true)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileRow(label = "Wallet", value = "DVS Wallet Connected")

            Spacer(modifier = Modifier.height(20.dp))

            DvsSecondaryButton(
                text = "Close",
                onClick = { viewModel.dismissProfile() },
                modifier = Modifier.fillMaxWidth(),
                testTag = "close_profile_button"
            )
        }
    }

    // ==========================================
    // POPUP 3: VOTE CONFIRMATION
    // ==========================================
    if (state.showConfirmPopup && state.selectedCandidate != null) {
        val candidate = state.selectedCandidate
        val election = state.currentElection ?: return

        DvsModalCard(
            onDismissRequest = {
                if (!state.isSigningVote) viewModel.dismissVoteConfirmation()
            }
        ) {
            Text(
                text = "Confirm Vote",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.3).sp,
                color = DvsPrimaryText
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Election name
            Text(
                text = election.name,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = DvsMutedText
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Candidate Summary Container
            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, DvsAccentBorder),
                color = DvsAccentSoft,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 3. Official party symbol in refined circular frame
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(1.dp, DvsBorder),
                        color = Color.White,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(8.dp)
                        ) {
                            if (candidate.symbolImageUrl.isNotBlank()) {
                                coil.compose.AsyncImage(
                                    model = candidate.symbolImageUrl,
                                    contentDescription = "${candidate.party} symbol",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (candidate.symbolResId != 0) {
                                Image(
                                    painter = painterResource(id = candidate.symbolResId),
                                    contentDescription = "${candidate.party} symbol",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // 2. Selected candidate name
                        Text(
                            text = candidate.name,
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DvsPrimaryText
                        )
                        // 4. Party name
                        Text(
                            text = candidate.party,
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = DvsAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. One short irreversible-vote warning
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DvsStatusWarningBg, RoundedCornerShape(6.dp))
                    .border(1.dp, DvsStatusWarningBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "This vote is final and cannot be changed once submitted.",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    color = DvsStatusWarningText
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions: Sign Vote (Primary) and Cancel (Secondary)
            DvsPrimaryButton(
                text = "Sign Vote",
                onClick = { viewModel.signVote(simulateFailure = false) },
                isLoading = state.isSigningVote,
                modifier = Modifier.fillMaxWidth(),
                testTag = "sign_vote_button"
            )

            Spacer(modifier = Modifier.height(8.dp))

            DvsSecondaryButton(
                text = "Cancel",
                onClick = { viewModel.dismissVoteConfirmation() },
                enabled = !state.isSigningVote,
                modifier = Modifier.fillMaxWidth(),
                testTag = "cancel_vote_button"
            )

            // Option to verify failure scenario for reviewers (hidden on UI by default)
            if (com.example.data.api.DvsServerConfig.SHOW_DEV_TEST_ACTIONS) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Test Submission Failure",
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    color = DvsSubtleText,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(enabled = !state.isSigningVote) {
                            viewModel.signVote(simulateFailure = true)
                        }
                        .padding(4.dp)
                        .testTag("test_submission_failure_button")
                )
            }
        }
    }
}

/**
 * B. Election Information Card (Larger premium card showing ONLY name and date)
 */
@Composable
private fun ElectionInfoCard(
    electionName: String,
    electionDate: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = DvsSurface,
        border = BorderStroke(1.dp, DvsBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = electionName,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = (-0.4).sp,
                color = DvsPrimaryText
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = electionDate,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = DvsMutedText
            )
        }
    }
}

/**
 * C. Candidate Card:
 * Top: Candidate name
 * Bottom: Large official symbol on left, Party name on right
 * No radio buttons, checkboxes, or photos. Whole card clickable.
 */
@Composable
private fun CandidateCard(
    candidate: Candidate,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val bgColor = if (isSelected) DvsAccentSoft else DvsSurface
    val borderColor = if (isSelected) DvsAccent else DvsBorder
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .testTag("candidate_card_${candidate.id}"),
        color = bgColor,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Top Section: Candidate name, clearly displayed
            Text(
                text = candidate.name,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = DvsPrimaryText
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Section: Large official party symbol on left, Party name on right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Bottom Section: 80dp circular party-symbol frame on left, Party name on right
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(1.dp, DvsBorder),
                    color = Color.White,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(10.dp)
                    ) {
                        if (candidate.symbolImageUrl.isNotBlank()) {
                            coil.compose.AsyncImage(
                                model = candidate.symbolImageUrl,
                                contentDescription = "${candidate.party} symbol",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (candidate.symbolResId != 0) {
                            Image(
                                painter = painterResource(id = candidate.symbolResId),
                                contentDescription = "${candidate.party} symbol",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = candidate.party,
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = if (isSelected) DvsAccent else DvsMutedText,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Section 3: Already Participated State
 * Heading: Already Participated
 * Show ONLY: Previous election name and Date of participation.
 */
@Composable
private fun AlreadyParticipatedContent(state: VoterUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = DvsSurface,
            border = BorderStroke(1.dp, DvsBorder)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val hasCompletedAny = state.completedElections.isNotEmpty()
                Text(
                    text = if (hasCompletedAny) "Already Participated" else "No Active Elections Available",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = (-0.3).sp,
                    color = DvsPrimaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (hasCompletedAny) {
                    Text(
                        text = "Previous Election",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = DvsSubtleText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = state.lastParticipatedElectionName,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = DvsPrimaryText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Date of Participation",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = DvsSubtleText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = state.receipt?.timestamp ?: "Recent Session",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 13.sp,
                        color = DvsPrimaryText
                    )
                } else {
                    Text(
                        text = "There are currently no active ballots available for voting.",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = DvsMutedText,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(
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
