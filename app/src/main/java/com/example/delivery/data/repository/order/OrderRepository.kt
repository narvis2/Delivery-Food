package com.example.delivery.data.repository.order

import com.example.delivery.data.entity.RestaurantFoodEntity

interface OrderRepository {

    suspend fun orderMenu(
        userId: String,
        restaurantId: Long,
        foodMenuList : List<RestaurantFoodEntity>,
        restaurantTitle : String
    ) : Result

    suspend fun getAllOrderMenus(
        userId: String
    ) : Result

}