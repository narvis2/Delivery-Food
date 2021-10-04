package com.example.delivery.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class LocationLatLngEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Long = -1,
    val latitude: Double,
    val longitude: Double
) : BaseEntity, Parcelable