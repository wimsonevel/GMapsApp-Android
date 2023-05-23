package com.wim.gmapsapp_android.ui.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.wim.gmapsapp_android.model.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(): ViewModel() {

    val address: MutableState<Address> = mutableStateOf(
        Address(
            id = 1L,
            address = "",
            detail = "",
            label = "",
            lastKnownLocation = Location(LocationManager.GPS_PROVIDER),
        ),
    )

    @SuppressLint("MissingPermission")
    fun getLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        try {
            val location = fusedLocationProviderClient.lastLocation
            location.addOnCompleteListener {
                if(it.isSuccessful && it.result != null) {
                    address.value = address.value.copy(
                        lastKnownLocation = it.result,
                    )
                }
            }
        }catch (e: SecurityException) {

        }

    }

}