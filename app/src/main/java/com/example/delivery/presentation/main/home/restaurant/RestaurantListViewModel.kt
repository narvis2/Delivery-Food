package com.example.delivery.presentation.main.home.restaurant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.repository.restaurant.RestaurantRepository
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.mapper.toRestaurantModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantListViewModel(
    private val restaurantCategory: RestaurantCategory,
    private val restaurantRepository: RestaurantRepository,
    private var locationLatLngEntity: LocationLatLngEntity,
    private var restaurantOrder: RestaurantOrder = RestaurantOrder.DEFAULT
) : BaseViewModel() {

    private val _restaurantListLiveData = MutableLiveData<List<RestaurantModel>>()
    val restaurantListLiveData: LiveData<List<RestaurantModel>>
        get() = _restaurantListLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        val response = restaurantRepository.getList(restaurantCategory, locationLatLngEntity)
        val sortedList = when(restaurantOrder) {
            RestaurantOrder.DEFAULT -> {
                response
            }
            RestaurantOrder.LOW_DELIVERY_TIP -> {
                // 배달팁 가장 낮은 값이 위로 올라오도록
                response.sortedBy {
                    it.deliveryTipRange.first
                }
            }
            RestaurantOrder.FAST_DELIVERY -> {
                // 배달 시간이 가장 낮은 값이 위로
                response.sortedBy {
                    it.deliveryTimeRange.first
                }
            }
            RestaurantOrder.TOP_LATE -> {
                // 내림 차순 별점이 가장 높은 것이 맨 위로
                response.sortedByDescending {
                    it.grade
                }
            }
        }
        _restaurantListLiveData.postValue(sortedList.map {
            it.toRestaurantModel()
        })
    }

    fun setLocationLatLng(locationLatLngEntity: LocationLatLngEntity) {
        this.locationLatLngEntity = locationLatLngEntity
        fetchData()
    }

    fun setRestaurantOrder(order: RestaurantOrder) {
        this.restaurantOrder = order
        fetchData()
    }
}