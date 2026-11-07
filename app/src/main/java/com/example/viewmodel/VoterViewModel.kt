package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.MockData
import com.example.data.models.Candidate
import com.example.data.models.Election
import com.example.data.models.Voter
import com.example.data.models.VotingReceipt
import com.example.util.DvsSoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class VoterScreen {
    LOGIN,   // Screen 1: Voter Login
    VOTING,  // Screen 2: Main Voting Screen (including Already Participated state)
    RECEIPT  // Screen 3: Voting Receipt
}

data class VoterUiState(
    val currentScreen: VoterScreen = VoterScreen.LOGIN,
    val voterIdInput: String = "",
    val isLoggingIn: Boolean = false,
    val currentVoter: Voter? = null,

    // Popups
    val showWalletPopup: Boolean = false,
    val showProfilePopup: Boolean = false,
    val showConfirmPopup: Boolean = false,

    // Wallet connection simulation
    val isWalletConnecting: Boolean = false,
    val isWalletConnected: Boolean = false,
    val walletSuccessAnim: Boolean = false,

    // Elections & Candidate selection
    val availableElections: List<Election> = emptyList(),
    val activeElectionIndex: Int = 0,
    val completedElections: Set<Int> = emptySet(),
    val selectedCandidate: Candidate? = null,
    val isSigningVote: Boolean = false,
    val receipt: VotingReceipt? = null,

    // Unified small error notification
    val errorMessage: String? = null,

    // Inactivity tracking
    val lastActivityTime: Long = System.currentTimeMillis()
) {
    val currentElection: Election?
        get() = availableElections.getOrNull(activeElectionIndex)
            ?: MockData.allElections.getOrNull(activeElectionIndex)

    val hasRemainingElections: Boolean
        get() = currentElection != null && !completedElections.contains(currentElection?.id)

    val lastParticipatedElectionName: String
        get() = availableElections.firstOrNull { completedElections.contains(it.id) }?.name
            ?: MockData.allElections.firstOrNull { completedElections.contains(it.id) }?.name
            ?: "Civic Council Election 2026"
}

class VoterViewModel : ViewModel() {

    var soundManager: DvsSoundManager? = null

    private val _uiState = MutableStateFlow(VoterUiState())
    val uiState: StateFlow<VoterUiState> = _uiState.asStateFlow()

    private var errorDismissJob: Job? = null

    init {
        // Periodic check for 30-minute inactivity timeout
        viewModelScope.launch {
            while (true) {
                delay(15_000) // check every 15s
                val now = System.currentTimeMillis()
                val lastActivity = _uiState.value.lastActivityTime
                if (_uiState.value.currentVoter != null && (now - lastActivity > 30 * 60 * 1000L)) {
                    signOut()
                }
            }
        }
    }

    fun recordActivity() {
        _uiState.update { it.copy(lastActivityTime = System.currentTimeMillis()) }
    }

    fun onVoterIdChanged(newId: String) {
        recordActivity()
        val sanitized = newId.filter { it.isDigit() }.take(12)
        _uiState.update { it.copy(voterIdInput = sanitized) }
    }

    fun selectPresetId(id: String) {
        recordActivity()
        _uiState.update { it.copy(voterIdInput = id) }
    }

    fun signIn(simulateNetworkError: Boolean = false) {
        recordActivity()
        val id = _uiState.value.voterIdInput.trim()
        if (id.length != 12) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingIn = true) }

            if (simulateNetworkError) {
                _uiState.update { it.copy(isLoggingIn = false) }
                showError("Server Offline.\nPlease Comeback later")
                return@launch
            }

            try {
                val apiService = com.example.data.api.DvsApiClient.createService()

                // 1. Fetch public active elections from Django backend
                val electionsResponse = apiService.getPublicElections()
                if (!electionsResponse.isSuccessful || electionsResponse.body() == null) {
                    _uiState.update { it.copy(isLoggingIn = false) }
                    showError("Server Offline.\nPlease Comeback later")
                    return@launch
                }

                val publicElectionsDto = electionsResponse.body()?.elections ?: emptyList()
                val serverBase = com.example.data.api.DvsServerConfig.serverBaseUrl

                // 2. Map all public elections and verify voted status per election for this voter
                val completedElectionIds = mutableSetOf<Int>()
                val unvotedElections = mutableListOf<Election>()

                var authenticatedVoterName: String? = null
                var authenticatedPhone: String? = null
                var isFoundInAny = false
                var isSecurityFlagged = false

                // Evaluate each public election to check if voter has already voted in it
                for (elDto in publicElectionsDto) {
                    val authResp = apiService.authenticateVoter(
                        com.example.data.api.VoterAuthRequest(
                            voterId = id,
                            electionId = elDto.electionId
                        )
                    )

                    if (authResp.isSuccessful) {
                        isFoundInAny = true
                        val authBody = authResp.body()
                        if (authBody?.voterName != null) {
                            authenticatedVoterName = authBody.voterName
                        }
                        if (!authBody?.phone.isNullOrBlank()) {
                            authenticatedPhone = authBody?.phone
                        }

                        val mappedCandidates = (elDto.candidates ?: emptyList()).map { candDto ->
                            val fullImageUrl = if (!candDto.symbolImageUrl.isNullOrBlank()) {
                                if (candDto.symbolImageUrl.startsWith("http")) {
                                    candDto.symbolImageUrl
                                } else {
                                    "$serverBase${candDto.symbolImageUrl}"
                                }
                            } else {
                                ""
                            }
                            Candidate(
                                id = candDto.candidateId,
                                name = candDto.name,
                                party = candDto.symbol ?: candDto.description ?: "Independent",
                                symbolResId = 0,
                                symbolImageUrl = fullImageUrl
                            )
                        }

                        val mappedElection = Election(
                            id = elDto.electionId,
                            name = elDto.title,
                            date = elDto.startDatetime?.take(10) ?: "Active Election",
                            candidates = mappedCandidates
                        )

                        if (authBody?.hasVoted == true) {
                            // Voter has already voted in this election -> Exclude from voting pool
                            completedElectionIds.add(elDto.electionId)
                        } else {
                            // Voter is eligible and has NOT yet voted
                            unvotedElections.add(mappedElection)
                        }
                    } else if (authResp.code() == 403) {
                        isSecurityFlagged = true
                    }
                }

                if (!isFoundInAny) {
                    _uiState.update { it.copy(isLoggingIn = false) }
                    if (isSecurityFlagged) {
                        showError("Not authorised")
                    } else {
                        showError("Voter ID not found")
                    }
                    return@launch
                }

                // 3. Priority: Oldest election in which voter has not voted (sort by ID ascending / start date)
                unvotedElections.sortBy { it.id }

                val resolvedPhone = if (!authenticatedPhone.isNullOrBlank()) {
                    authenticatedPhone
                } else {
                    val last2 = id.takeLast(2)
                    "+91 ••••• •••$last2"
                }

                val currentVoter = Voter(
                    id = id,
                    name = authenticatedVoterName ?: "Registered Voter",
                    phone = resolvedPhone,
                    isAuthorized = true,
                    isFlagged = false
                )

                _uiState.update {
                    it.copy(
                        isLoggingIn = false,
                        currentVoter = currentVoter,
                        availableElections = unvotedElections,
                        activeElectionIndex = 0,
                        completedElections = completedElectionIds,
                        showWalletPopup = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoggingIn = false) }
                // Server offline requirement throughout the app
                showError("Server Offline.\nPlease Comeback later")
            }
        }
    }

    fun dismissWalletPopup() {
        recordActivity()
        _uiState.update { it.copy(showWalletPopup = false) }
    }

    fun connectWallet(simulateFailure: Boolean = false) {
        recordActivity()
        viewModelScope.launch {
            _uiState.update { it.copy(isWalletConnecting = true) }
            delay(600)

            if (simulateFailure) {
                _uiState.update { it.copy(isWalletConnecting = false) }
                showError("Wallet connection failed")
                return@launch
            }

            // Connection success animation
            _uiState.update {
                it.copy(
                    isWalletConnecting = false,
                    walletSuccessAnim = true,
                    isWalletConnected = true
                )
            }
            soundManager?.playWalletConnectedSound()
            delay(500)
            _uiState.update {
                it.copy(
                    walletSuccessAnim = false,
                    showWalletPopup = false,
                    currentScreen = VoterScreen.VOTING
                )
            }
        }
    }

    fun selectCandidate(candidate: Candidate) {
        recordActivity()
        _uiState.update { it.copy(selectedCandidate = candidate) }
    }

    fun clearCandidateSelection() {
        recordActivity()
        _uiState.update { it.copy(selectedCandidate = null) }
    }

    fun openVoteConfirmation() {
        recordActivity()
        if (_uiState.value.selectedCandidate != null) {
            _uiState.update { it.copy(showConfirmPopup = true) }
        }
    }

    fun dismissVoteConfirmation() {
        recordActivity()
        _uiState.update { it.copy(showConfirmPopup = false) }
    }

    fun signVote(simulateFailure: Boolean = false) {
        recordActivity()
        val election = _uiState.value.currentElection ?: return
        val candidate = _uiState.value.selectedCandidate ?: return

        // Prevent repeated submission while operation is processing
        if (_uiState.value.isSigningVote) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSigningVote = true) }

            if (simulateFailure) {
                _uiState.update { it.copy(isSigningVote = false) }
                showError("Vote submission failed")
                return@launch
            }

            try {
                val apiService = com.example.data.api.DvsApiClient.createService()
                val response = apiService.castVote(
                    com.example.data.api.VoterCastRequest(
                        electionId = election.id,
                        candidateId = candidate.id
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    // Standardize Receipt Reference format (e.g. DVS-XXXXXXXX)
                    val rawRef = body.receiptReference ?: ("DVS-" + UUID.randomUUID().toString().take(8).uppercase(Locale.US))
                    val standardizedRef = if (rawRef.startsWith("DVS-")) {
                        if (rawRef.length > 16) {
                            // Extract the meaningful reference code
                            "DVS-" + rawRef.substringAfterLast("-").take(8).uppercase(Locale.US)
                        } else {
                            rawRef
                        }
                    } else {
                        "DVS-" + rawRef.take(8).uppercase(Locale.US)
                    }

                    // Format Date & Time cleanly into app standard: "MMM d, yyyy HH:mm"
                    val formattedTimestamp = try {
                        if (!body.confirmedAt.isNullOrBlank()) {
                            // Parse ISO timestamp from Django (e.g. 2026-11-06T20:15:30.123456Z)
                            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                            val displayFormat = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
                            val parsedDate = isoFormat.parse(body.confirmedAt.substringBefore("."))
                            if (parsedDate != null) displayFormat.format(parsedDate)
                            else SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date())
                        } else {
                            SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date())
                        }
                    } catch (e: Exception) {
                        SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date())
                    }

                    val standardReceipt = VotingReceipt(
                        receiptReference = standardizedRef,
                        electionName = body.electionTitle ?: election.name,
                        timestamp = formattedTimestamp,
                        status = "Vote Recorded"
                    )

                    val updatedCompleted = _uiState.value.completedElections + election.id

                    _uiState.update {
                        it.copy(
                            isSigningVote = false,
                            showConfirmPopup = false,
                            completedElections = updatedCompleted,
                            receipt = standardReceipt,
                            currentScreen = VoterScreen.RECEIPT
                        )
                    }
                    soundManager?.playVoteSuccessSound()
                } else if (response.code() == 409) {
                    // Duplicate vote defense-in-depth: mark completed and close modal
                    val updatedCompleted = _uiState.value.completedElections + election.id
                    _uiState.update {
                        it.copy(
                            isSigningVote = false,
                            showConfirmPopup = false,
                            completedElections = updatedCompleted
                        )
                    }
                    showError("Vote submission failed")
                } else {
                    _uiState.update {
                        it.copy(
                            isSigningVote = false,
                            showConfirmPopup = false
                        )
                    }
                    showError("Vote submission failed")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSigningVote = false,
                        showConfirmPopup = false
                    )
                }
                showError("Server Offline.\nPlease Comeback later")
            }
        }
    }

    fun openProfile() {
        recordActivity()
        _uiState.update { it.copy(showProfilePopup = true) }
    }

    fun dismissProfile() {
        recordActivity()
        _uiState.update { it.copy(showProfilePopup = false) }
    }

    fun backToHomeFromReceipt() {
        recordActivity()
        // Check next eligible election sequentially
        val nextIdx = _uiState.value.activeElectionIndex + 1
        _uiState.update {
            it.copy(
                activeElectionIndex = nextIdx,
                selectedCandidate = null,
                currentScreen = VoterScreen.VOTING
            )
        }
    }

    fun signOut() {
        _uiState.value = VoterUiState(
            currentScreen = VoterScreen.LOGIN,
            voterIdInput = "",
            currentVoter = null,
            activeElectionIndex = 0,
            completedElections = emptySet(),
            selectedCandidate = null,
            isWalletConnected = false,
            lastActivityTime = System.currentTimeMillis()
        )
    }

    fun showError(message: String) {
        errorDismissJob?.cancel()
        _uiState.update { it.copy(errorMessage = message) }
        errorDismissJob = viewModelScope.launch {
            delay(2800)
            _uiState.update { it.copy(errorMessage = null) }
        }
    }
}
