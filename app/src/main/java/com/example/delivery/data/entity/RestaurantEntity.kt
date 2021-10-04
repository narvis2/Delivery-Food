package com.example.delivery.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.delivery.presentation.main.home.restaurant.RestaurantCategory
import com.example.delivery.util.convertor.RoomTypeConverters
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@TypeConverters(RoomTypeConverters::class)
data class RestaurantEntity(
    override val id: Long,
    val restaurantInfoId: Long,
    val restaurantCategory: RestaurantCategory,
    @PrimaryKey
    val restaurantTitle : String,
    val restaurantImageUrl : String,
    val grade: Float,
    val reviewCount: Int,
    val deliveryTimeRange : Pair<Int, Int>,
    val deliveryTipRange : Pair<Int, Int>,
    val restaurantTelNumber : String?
) : BaseEntity, Parcelable