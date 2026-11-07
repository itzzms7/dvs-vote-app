package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CandidateDto(
    @Json(name = "candidate_id")
    val candidateId: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "symbol")
    val symbol: String? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "symbol_image_url")
    val symbolImageUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class ElectionSummaryDto(
    @Json(name = "election_id")
    val electionId: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "status")
    val status: String? = null,
    @Json(name = "start_datetime")
    val startDatetime: String? = null,
    @Json(name = "end_datetime")
    val endDatetime: String? = null,
    @Json(name = "candidates")
    val candidates: List<CandidateDto>? = null
)

@JsonClass(generateAdapter = true)
data class PublicElectionsResponse(
    @Json(name = "elections")
    val elections: List<ElectionSummaryDto>? = null
)
