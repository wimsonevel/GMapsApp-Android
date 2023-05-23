package com.wim.gmapsapp_android.model

data class PlaceResponse(val items: List<Item>)

data class Item (
    val title: String,
    val id: String,
    val language: String,
    val resultType: String,
    val position: Position,
    val address: ItemAddress,
    val highlights: Highlights
)

data class ItemAddress (
    val label: String,
    val countryCode: String,
    val countryName: String,
    val countyCode: String,
    val county: String,
    val city: String,
    val district: String,
    val subdistrict: String? = null,
    val street: String,
    val postalCode: String
)

data class Position (
    val lat: Double,
    val lng: Double
)

data class Highlights (
    val title: List<Title>,
    val address: HighlightsAddress
)

data class HighlightsAddress (
    val label: List<Title>,
    val street: List<Title>
)

data class Title (
    val start: Long,
    val end: Long
)