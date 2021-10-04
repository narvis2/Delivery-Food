package com.example.delivery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.delivery.data.entity.RestaurantFoodEntity

@Dao
interface FoodMenuBasketDao {

    @Query("SELECT * FROM RestaurantFoodEntity WHERE id=:foodId AND restaurantId=:restaurantId")
    suspend fun getSelectedFoodEntity(restaurantId: Long, foodId: Long) : RestaurantFoodEntity?

    @Query("SELECT * FROM RestaurantFoodEntity")
    suspend fun getAll(): List<RestaurantFoodEntity>

    // 해당 식당에 대한 id 를 갖고 전체를 가져옴
    @Query("SELECT * FROM RestaurantFoodEntity WHERE restaurantId=:restaurantId")
    suspend fun getAllByRestaurantId(restaurantId: Long) : List<RestaurantFoodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(restaurantFoodEntity: RestaurantFoodEntity)

    @Query("DELETE FROM RestaurantFoodEntity WHERE id=:foodId")
    suspend fun deleteSelectedFoodEntity(foodId: String)

    @Query("DELETE FROM RestaurantFoodEntity")
    suspend fun deleteAll()
}