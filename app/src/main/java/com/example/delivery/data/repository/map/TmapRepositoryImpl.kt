package com.example.delivery.data.repository.map

import android.util.Log
import com.example.delivery.data.api.TmapApiService
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.response.address.AddressInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TmapRepositoryImpl(
    private val tmapApiService: TmapApiService,
    private val ioDispatcher: CoroutineDispatcher
) : TmapRepository {
    override suspend fun getReverseGeoInformation(
        locationLatLngEntity: LocationLatLngEntity
    ): AddressInfo? = withContext(ioDispatcher) {
        val response = tmapApiService.getReverseGeoCode(
            lat = locationLatLngEntity.latitude,
            lon = locationLatLngEntity.longitude
        )
        if (response.isSuccessful) {
            response.body()?.addressInfo
        } else {
            null
        }
    }
}