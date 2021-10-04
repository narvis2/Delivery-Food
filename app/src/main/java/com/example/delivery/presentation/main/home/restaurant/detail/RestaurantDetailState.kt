package com.example.delivery.presentation.main.home.restaurant.detail

import com.example.delivery.data.entity.RestaurantEntity
import com.example.delivery.data.entity.RestaurantFoodEntity

sealed class RestaurantDetailState {

    object Uninitialized: RestaurantDetailState()

    object Loading: RestaurantDetailState()

    data class Success(
        val restaurantEntity: RestaurantEntity,
        val restaurantFoodList : List<RestaurantFoodEntity>? = null,
        val foodMenuListInBasket : List<RestaurantFoodEntity>? = null,
        val isClearNeedInBasketAndAction: Pair<Boolean, () -> Unit> = Pair(false, {}),
        val isLiked: Boolean? = null
    ) : RestaurantDetailState()
}