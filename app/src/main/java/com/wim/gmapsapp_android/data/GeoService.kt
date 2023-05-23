package com.wim.gmapsapp_android.data

import com.wim.gmapsapp_android.BuildConfig
import com.wim.gmapsapp_android.model.PlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoService {

    @GET("/v1/revgeocode")
    suspend fun getLocation(
        @Query("at") at: String,
        @Query("apiKey") apiKey: String = BuildConfig.GEO_KEY
    ): Response<PlaceResponse>

    @GET("/v1/autocomplete")
    suspend fun getLocationByName(
        @Query("q") q: String,
        @Query("apiKey") apiKey: String = BuildConfig.GEO_KEY
    ): Response<PlaceResponse>
}