package com.wim.gmapsapp_android.model

import android.location.Location
import android.location.LocationManager
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address (
    val id: Long,
    val address: String,
    val detail: String,
    val label: String,
    val lastKnownLocation: Location,
): Parcelable

val addressList = listOf(
    Address(
        id = 1L,
        address = "Jalan A no.3",
        detail = "Rumah blok B no.43",
        label = "Rumah",
        lastKnownLocation = Location(""),
    ),
    Address(
        id = 2L,
        address = "Jalan B no.5",
        detail = "Lantai 5",
        label = "Kantor",
        lastKnownLocation = Location(""),
    ),
)