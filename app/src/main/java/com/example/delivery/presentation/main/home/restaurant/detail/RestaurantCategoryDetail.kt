package com.example.delivery.presentation.main.home.restaurant.detail

import androidx.annotation.StringRes
import com.example.delivery.R

enum class RestaurantCategoryDetail(
    @StringRes val categoryNameId: Int
) {

    MENU(R.string.menu),
    REVIEW(R.string.review)

}