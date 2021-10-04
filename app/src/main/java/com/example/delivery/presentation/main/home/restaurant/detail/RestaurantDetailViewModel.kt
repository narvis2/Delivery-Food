package com.example.delivery.presentation.main.home.restaurant.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.data.entity.RestaurantEntity
import com.example.delivery.data.entity.RestaurantFoodEntity
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private val restaurantEntity: RestaurantEntity,
    private val userRepository: UserRepository,
    private val restaurantFoodRepository: RestaurantFoodRepository,
) : BaseViewModel() {

    private val _restaurantDetailStateLiveData =
        MutableLiveData<RestaurantDetailState>(RestaurantDetailState.Uninitialized)
    val restaurantDetailStateLiveData: LiveData<RestaurantDetailState>
        get() = _restaurantDetailStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _restaurantDetailStateLiveData.postValue(
            RestaurantDetailState.Success(
                restaurantEntity = restaurantEntity
            )
        )

        _restaurantDetailStateLiveData.postValue(RestaurantDetailState.Loading)
        val foods =
            restaurantFoodRepository.getFoods(
                restaurantId = restaurantEntity.restaurantInfoId,
                restaurantTitle = restaurantEntity.restaurantTitle
            )
        val foodMenuListInBasket = restaurantFoodRepository.getAllFoodMenuListInBasket()
        // 좋아요 상태
        val isLiked = userRepository.getUserLikeRestaurant(restaurantEntity.restaurantTitle) != null
        _restaurantDetailStateLiveData.postValue(
            RestaurantDetailState.Success(
                restaurantEntity = restaurantEntity,
                restaurantFoodList = foods,
                foodMenuListInBasket = foodMenuListInBasket,
                isLiked = isLiked
            )
        )
    }

    fun getRestaurantTelNumber(): String? {
        return when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                data.restaurantEntity.restaurantTelNumber
            }
            else -> null
        }
    }

    // 공유하기
    fun getRestaurantInfo(): RestaurantEntity? {
        return when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                data.restaurantEntity
            }
            else -> null
        }
    }

    fun toggleLikedRestaurant() = viewModelScope.launch {
        when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                userRepository.getUserLikeRestaurant(restaurantEntity.restaurantTitle)?.let {
                    // 좋아요 상태
                    userRepository.deleteUserLikedRestaurant(it.restaurantTitle)
                    _restaurantDetailStateLiveData.postValue(
                        data.copy(
                            isLiked = false
                        )
                    )
                } ?: kotlin.run {
                    userRepository.insertUserLikedRestaurant(restaurantEntity)
                    _restaurantDetailStateLiveData.postValue(
                        data.copy(
                            isLiked = true
                        )
                    )
                }
            }
        }
    }

    fun notifyFoodMenuListInBasket(restaurantFoodEntity: RestaurantFoodEntity) =
        viewModelScope.launch {
            when (val data = restaurantDetailStateLiveData.value) {
                is RestaurantDetailState.Success -> {
                    _restaurantDetailStateLiveData.postValue(
                        data.copy(
                            foodMenuListInBasket = data.foodMenuListInBasket?.toMutableList()
                                ?.apply {
                                    add(restaurantFoodEntity)
                                }
                        )
                    )
                }
                else -> Unit
            }
        }

    fun notifyClearNeedAlertInBasket(clearNeed: Boolean, afterAction: () -> Unit) {
        when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                _restaurantDetailStateLiveData.postValue(
                    data.copy(
                        isClearNeedInBasketAndAction = Pair(clearNeed, afterAction)
                    )
                )
            }
            else -> Unit
        }
    }

    fun notifyClearBasket() = viewModelScope.launch {
        when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                _restaurantDetailStateLiveData.postValue(
                    data.copy(
                        foodMenuListInBasket = listOf(),
                        isClearNeedInBasketAndAction = Pair(false, {})
                    )
                )
            }
            else -> Unit
        }
    }

}