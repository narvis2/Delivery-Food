package com.example.delivery.data.repository.user

import com.example.delivery.data.db.dao.LocationDao
import com.example.delivery.data.db.dao.RestaurantDao
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.RestaurantEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val locationDao: LocationDao,
    private val restaurantDao: RestaurantDao,
) : UserRepository {

    override suspend fun getUserLocation(): LocationLatLngEntity? = withContext(ioDispatcher) {
        locationDao.getUserLocationLatLngEntity(-1)
    }

    override suspend fun insertUserLocation(locationLatLngEntity: LocationLatLngEntity) =
        withContext(ioDispatcher) {
            locationDao.saveLocationLatLngEntity(locationLatLngEntity)
        }

    override suspend fun getUserLikeRestaurant(restaurantTitle: String): RestaurantEntity? =
        withContext(ioDispatcher) {
            restaurantDao.getSelectedRestaurantEntity(restaurantTitle)
        }

    override fun getAllUserLikedRestaurant(): Flow<List<RestaurantEntity>> =
        restaurantDao.getAll().distinctUntilChanged()
            .flowOn(ioDispatcher)

    override suspend fun insertUserLikedRestaurant(restaurantEntity: RestaurantEntity) =
        withContext(ioDispatcher) {
            restaurantDao.insert(restaurantEntity)
        }

    override suspend fun deleteUserLikedRestaurant(restaurantTitle: String) =
        withContext(ioDispatcher) {
            restaurantDao.deleteSelectedRestaurantEntity(restaurantTitle)
        }

    override suspend fun deleteAllUserLikedRestaurant() = withContext(ioDispatcher) {
        restaurantDao.deleteAll()
    }
}