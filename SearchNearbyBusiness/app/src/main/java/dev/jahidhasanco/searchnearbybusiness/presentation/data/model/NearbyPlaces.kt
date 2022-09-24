package dev.jahidhasanco.searchnearbybusiness.presentation.data.model

import com.google.gson.annotations.SerializedName

data class NearbyPlaces(
    @SerializedName("results") val results: List<Place>
)
