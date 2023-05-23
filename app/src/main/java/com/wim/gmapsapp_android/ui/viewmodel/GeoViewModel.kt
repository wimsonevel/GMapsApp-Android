package com.wim.gmapsapp_android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wim.gmapsapp_android.data.GeoService
import com.wim.gmapsapp_android.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeoViewModel @Inject constructor(
    private val geoService: GeoService
): ViewModel() {

    private val _suggestions = MutableLiveData<List<Item>>()
    val suggestions: LiveData<List<Item>> = _suggestions

    private val _picked = MutableLiveData<List<Item>>()
    val picked: LiveData<List<Item>> = _picked

    fun reverseGeocode(at: String) {
        viewModelScope.launch {
            try {
                val response = geoService.getLocation(at)
                if (response.isSuccessful) {
                    val data = response.body()?.items ?: emptyList()
                    _picked.value = data
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            delay(2000)

            try {
                val response = geoService.getLocationByName(query)
                if (response.isSuccessful) {
                    val suggestions = response.body()?.items ?: emptyList()
                    _suggestions.value = suggestions
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}