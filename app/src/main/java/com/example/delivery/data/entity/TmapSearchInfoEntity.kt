package com.example.delivery.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TmapSearchInfoEntity(
    val fullAddress: String,
    val name: String,
    val locationLatLngEntity: LocationLatLngEntity
) : Parcelable