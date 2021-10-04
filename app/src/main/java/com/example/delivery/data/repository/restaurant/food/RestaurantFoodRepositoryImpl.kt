package com.example.delivery.data.repository.restaurant.food

import com.example.delivery.data.api.FoodApiService
import com.example.delivery.data.db.dao.FoodMenuBasketDao
import com.example.delivery.data.entity.RestaurantFoodEntity
import com.example.delivery.util.mapper.toRestaurantFoodEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RestaurantFoodRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val foodApiService: FoodApiService,
    private val foodMenuBasketDao: FoodMenuBasketDao,
) : RestaurantFoodRepository {

    override suspend fun getFoods(restaurantId: Long, restaurantTitle: String): List<RestaurantFoodEntity> =
        withContext(ioDispatcher) {
            val response = foodApiService.getRestaurantFoods(restaurantId)
            if (response.isSuccessful) {
                response.body()?.map {
                    it.toRestaurantFoodEntity(restaurantId, restaurantTitle)
                } ?: listOf()
            } else {
                listOf()
            }
        }

    override suspend fun getFoodMenuListInBasket(restaurantId: Long): List<RestaurantFoodEntity> =
        withContext(ioDispatcher) {
            foodMenuBasketDao.getAllByRestaurantId(restaurantId)
        }

    override suspend fun getAllFoodMenuListInBasket(): List<RestaurantFoodEntity> =
        withContext(ioDispatcher) {
            foodMenuBasketDao.getAll()
        }

    override suspend fun insertFoodMenuInBasket(restaurantFoodEntity: RestaurantFoodEntity) =
        withContext(ioDispatcher) {
            foodMenuBasketDao.insert(restaurantFoodEntity)
        }

    override suspend fun removeFoodMenuListInBasket(foodId: String) = withContext(ioDispatcher) {
        foodMenuBasketDao.deleteSelectedFoodEntity(foodId)
    }

    override suspend fun clearFoodMenuListInBasket() = withContext(ioDispatcher) {
        foodMenuBasketDao.deleteAll()
    }
}