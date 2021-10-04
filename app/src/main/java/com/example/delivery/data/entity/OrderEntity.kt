package com.example.delivery.data.entity

data class OrderEntity(
    val id: String,
    val userId: String,
    val restaurantId: Long,
    val foodMenuList: List<RestaurantFoodEntity>,
    val restaurantTitle: String
)
