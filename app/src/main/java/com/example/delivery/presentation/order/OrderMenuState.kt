package com.example.delivery.presentation.order

import androidx.annotation.StringRes
import com.example.delivery.model.restaurant.food.FoodModel

sealed class OrderMenuState {

    object Uninitialized : OrderMenuState()

    object Loading : OrderMenuState()

    /**
     * 장바구니에 있었던 Food List 를 Model 형태로 변환해서 추가
     */
    data class Success(
        val restaurantFoodMenuList : List<FoodModel>? = null
    ) : OrderMenuState()

    // 주문하기 버튼을 눌럿을 때
    object Order : OrderMenuState()

    data class Error(
        @StringRes val messageId : Int,
        val e: Throwable
    ) : OrderMenuState()

}
