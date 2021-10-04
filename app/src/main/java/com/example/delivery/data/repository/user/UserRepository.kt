package com.example.delivery.data.repository.user

import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.RestaurantEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserLocation() : LocationLatLngEntity?

    suspend fun insertUserLocation(locationLatLngEntity: LocationLatLngEntity)

    suspend fun getUserLikeRestaurant(restaurantTitle: String) : RestaurantEntity?

    fun getAllUserLikedRestaurant() : Flow<List<RestaurantEntity>>

    suspend fun insertUserLikedRestaurant(restaurantEntity: RestaurantEntity)

    suspend fun deleteUserLikedRestaurant(restaurantTitle: String)

    suspend fun deleteAllUserLikedRestaurant()
}