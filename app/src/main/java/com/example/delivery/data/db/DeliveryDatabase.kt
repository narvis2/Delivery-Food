package com.example.delivery.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.delivery.data.db.dao.FoodMenuBasketDao
import com.example.delivery.data.db.dao.LocationDao
import com.example.delivery.data.db.dao.RestaurantDao
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.RestaurantEntity
import com.example.delivery.data.entity.RestaurantFoodEntity

@Database(
    entities = [LocationLatLngEntity::class, RestaurantEntity::class, RestaurantFoodEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DeliveryDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun restaurantDao(): RestaurantDao

    abstract fun foodMenuBasketDao(): FoodMenuBasketDao

}