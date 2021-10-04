package com.example.delivery.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class RestaurantFoodEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val restaurantId: Long,
    val restaurantTitle : String
) : Parcelable
