package com.example.delivery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.delivery.data.entity.RestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM RestaurantEntity")
    fun getAll(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM RestaurantEntity WHERE restaurantTitle=:title")
    suspend fun getSelectedRestaurantEntity(title: String) : RestaurantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(restaurantEntity: RestaurantEntity)

    @Query("DELETE FROM RestaurantEntity WHERE restaurantTitle=:title")
    suspend fun deleteSelectedRestaurantEntity(title: String)

    @Query("DELETE FROM RestaurantEntity")
    suspend fun deleteAll()

}