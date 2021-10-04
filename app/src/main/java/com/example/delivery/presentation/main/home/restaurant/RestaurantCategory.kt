package com.example.delivery.presentation.main.home.restaurant

import androidx.annotation.StringRes
import com.example.delivery.R

enum class RestaurantCategory(
    @StringRes val categoryNameId: Int, // Tab 에 보여줄 이름
    @StringRes val categoryTypeId: Int // POI 검색에 사용될 타입
) {

    ALL(R.string.all, R.string.all_type),
    KOREAN_FOOD(R.string.korean_food, R.string.korean_food_type),
    DUMPLING_FOOD(R.string.dumpling_food, R.string.dumpling_food_type),
    CAFE_DESSERT(R.string.cafe_dessert, R.string.cafe_dessert_type),
    JAPANESE_FOOD(R.string.japanese_food, R.string.japanese_food_type),
    CHINESE_FOOD(R.string.chinese_food, R.string.chinese_food_type),
    ASIAN_EUROPE_FOOD(R.string.asian_europe_food, R.string.asian_europe_food_type),
    FAST_FOOD(R.string.fast_food, R.string.fast_food_type),
    CHICKEN_PIZZA(R.string.chicken_pizza, R.string.chicken_pizza_type)

}