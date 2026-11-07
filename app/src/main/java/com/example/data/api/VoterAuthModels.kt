package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Authentication request payload sent to POST /api/voter/authenticate/
 */
@JsonClass(generateAdapter = true)
data class VoterAuthRequest(
    @Json(name = "voter_id")
    val voterId: String,
    @Json(name = "election_id")
    val electionId: Int
)

/**
 * Response payload received from POST /api/voter/authenticate/
 */
@JsonClass(generateAdapter = true)
data class VoterAuthResponse(
    @Json(name = "success")
    val success: Boolean? = null,
    @Json(name = "has_voted")
    val hasVoted: Boolean? = null,
    @Json(name = "voter_name")
    val voterName: String? = null,
    @Json(name = "voter_id_masked")
    val voterIdMasked: String? = null,
    @Json(name = "phone")
    val phone: String? = null,
    @Json(name = "election_title")
    val electionTitle: String? = null,
    @Json(name = "error")
    val error: String? = null
)
