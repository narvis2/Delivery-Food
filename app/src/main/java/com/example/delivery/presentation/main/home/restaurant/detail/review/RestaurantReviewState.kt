package com.example.delivery.presentation.main.home.restaurant.detail.review

import androidx.annotation.StringRes
import com.example.delivery.model.restaurant.review.RestaurantReviewModel

sealed class RestaurantReviewState {
    object Uninitialized: RestaurantReviewState()

    object Loading : RestaurantReviewState()

    data class Success(
        val reviewList : List<RestaurantReviewModel>
    ) : RestaurantReviewState()

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable
    ) : RestaurantReviewState()
}
