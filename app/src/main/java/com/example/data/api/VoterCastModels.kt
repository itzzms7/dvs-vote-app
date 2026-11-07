package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Ballot submission payload sent to POST /api/voter/cast/
 */
@JsonClass(generateAdapter = true)
data class VoterCastRequest(
    @Json(name = "election_id")
    val electionId: Int,
    @Json(name = "candidate_id")
    val candidateId: Int
)

/**
 * Ballot receipt response payload received from POST /api/voter/cast/
 */
@JsonClass(generateAdapter = true)
data class VoterCastResponse(
    @Json(name = "success")
    val success: Boolean? = null,
    @Json(name = "receipt_reference")
    val receiptReference: String? = null,
    @Json(name = "tx_hash")
    val txHash: String? = null,
    @Json(name = "block_number")
    val blockNumber: Long? = null,
    @Json(name = "confirmed_at")
    val confirmedAt: String? = null,
    @Json(name = "election_title")
    val electionTitle: String? = null,
    @Json(name = "error")
    val error: String? = null
)
