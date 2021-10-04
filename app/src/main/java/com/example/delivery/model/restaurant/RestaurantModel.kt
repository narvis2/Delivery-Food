package com.example.delivery.model.restaurant

import com.example.delivery.model.BaseModel
import com.example.delivery.model.CellType
import com.example.delivery.presentation.main.home.restaurant.RestaurantCategory

data class RestaurantModel(
    override val id: Long,
    override val type: CellType,
    val restaurantInfoId: Long,
    val restaurantCategory: RestaurantCategory,
    val restaurantTitle : String,
    val restaurantImageUrl : String,
    val grade: Float,
    val reviewCount: Int,
    val deliveryTimeRange : Pair<Int, Int>,
    val deliveryTipRange : Pair<Int, Int>,
    val restaurantTelNumber: String?
) : BaseModel(id, type)