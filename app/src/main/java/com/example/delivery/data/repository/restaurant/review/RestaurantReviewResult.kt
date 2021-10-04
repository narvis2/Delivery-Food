package com.example.delivery.data.repository.restaurant.review

import com.example.delivery.data.repository.order.Result

sealed class RestaurantReviewResult {
    data class Success<T>(
        val data: T? = null
    ) : RestaurantReviewResult()

    data class Error(
        val e: Throwable
    ) : RestaurantReviewResult()
}
