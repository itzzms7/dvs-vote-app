package com.example.data.mock

import com.example.R
import com.example.data.models.Candidate
import com.example.data.models.Election
import com.example.data.models.Voter

object MockData {

    val election1Candidates = listOf(
        Candidate(
            id = 1,
            name = "Rahul Gandhi",
            party = "Indian National Congress",
            symbolResId = R.drawable.ic_symbol_congress
        ),
        Candidate(
            id = 2,
            name = "Narendar Modi",
            party = "Bharatiya Janata Party",
            symbolResId = R.drawable.ic_symbol_bjp
        ),
        Candidate(
            id = 3,
            name = "Akhilesh Yadav",
            party = "Samajwadi Party",
            symbolResId = R.drawable.ic_symbol_samajwadi
        )
    )

    val election2Candidates = listOf(
        Candidate(
            id = 1,
            name = "Civil Infrastructure Reform",
            party = "Proposal A",
            symbolResId = R.drawable.ic_symbol_congress
        ),
        Candidate(
            id = 2,
            name = "Digital Governance Initiative",
            party = "Proposal B",
            symbolResId = R.drawable.ic_symbol_bjp
        )
    )

    val allElections = listOf(
        Election(
            id = 1,
            name = "Civic Council Election 2026",
            date = "November 4, 2026",
            candidates = election1Candidates
        ),
        Election(
            id = 2,
            name = "Metropolitan Civic Referendum",
            date = "November 18, 2026",
            candidates = election2Candidates
        )
    )

    val mockVoters = mapOf(
        // Registered and authorised voter
        "111122223333" to Voter(
            id = "111122223333",
            name = "Aarav Sharma",
            phone = "+91 98765 43210",
            isAuthorized = true,
            isFlagged = false
        ),
        "100080809000" to Voter(
            id = "100080809000",
            name = "Evan Vance",
            phone = "+91 98111 22334",
            isAuthorized = true,
            isFlagged = false
        ),
        // Unauthorised voter
        "999988887777" to Voter(
            id = "999988887777",
            name = "Rohan Verma",
            phone = "+91 97222 33445",
            isAuthorized = false,
            isFlagged = false
        ),
        // Flagged voter
        "123456789012" to Voter(
            id = "123456789012",
            name = "Vikram Singh",
            phone = "+91 96333 44556",
            isAuthorized = false,
            isFlagged = true,
            flagReason = "Account flagged for administrative review"
        )
    )
}
