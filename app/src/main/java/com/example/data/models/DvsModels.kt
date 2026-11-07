package com.example.data.models

data class Candidate(
    val id: Int,
    val name: String,
    val party: String,
    val symbolResId: Int = 0,
    val symbolImageUrl: String = ""
)

data class Election(
    val id: Int,
    val name: String,
    val date: String,
    val candidates: List<Candidate>
)

data class Voter(
    val id: String, // 12-digit Voter ID
    val name: String,
    val phone: String,
    val isAuthorized: Boolean = true,
    val isFlagged: Boolean = false,
    val flagReason: String = ""
)

data class VotingReceipt(
    val receiptReference: String,
    val electionName: String,
    val timestamp: String,
    val status: String = "Vote Recorded"
)
