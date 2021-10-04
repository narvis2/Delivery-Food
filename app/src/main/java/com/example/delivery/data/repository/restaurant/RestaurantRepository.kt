package com.example.delivery.data.repository.restaurant

import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.RestaurantEntity
import com.example.delivery.presentation.main.home.restaurant.RestaurantCategory

interface RestaurantRepository {

    suspend fun getList(
        restaurantCategory: RestaurantCategory,
        locationLatLngEntity: LocationLatLngEntity
    ) : List<RestaurantEntity>

}