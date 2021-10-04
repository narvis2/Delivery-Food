package com.example.delivery.presentation.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.R
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.RestaurantFoodEntity
import com.example.delivery.data.entity.TmapSearchInfoEntity
import com.example.delivery.data.repository.map.TmapRepository
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.mapper.toSearchInfoEntity
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tmapRepository: TmapRepository,
    private val userRepository: UserRepository,
    private val restaurantFoodRepository: RestaurantFoodRepository
) : BaseViewModel() {

    private val _homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)
    val homeStateLiveData: LiveData<HomeState>
        get() = _homeStateLiveData

    private val _foodMenuBasketLiveData = MutableLiveData<List<RestaurantFoodEntity>>()
    val foodMenuBasketLiveDaa : LiveData<List<RestaurantFoodEntity>>
        get() = _foodMenuBasketLiveData

    fun loadReverseGeoInformation(
        locationLatLngEntity: LocationLatLngEntity
    )  = viewModelScope.launch {
        try {
            _homeStateLiveData.postValue(HomeState.Loading)
            val userLocation = userRepository.getUserLocation()

            val currentLocation = userLocation ?: locationLatLngEntity

            val addressInfo = tmapRepository.getReverseGeoInformation(currentLocation)
            addressInfo?.let { info ->
                _homeStateLiveData.postValue(HomeState.Success(
                    tMapSearchInfoEntity = info.toSearchInfoEntity(locationLatLngEntity),
                    isLocationSame = currentLocation == locationLatLngEntity
                ))
            } ?: kotlin.run {
                _homeStateLiveData.postValue(HomeState.Error(
                    R.string.can_not_load_address_info
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _homeStateLiveData.postValue(HomeState.Error(
                R.string.can_not_load_address_info
            ))
        }
    }

    // 데이터가 있는지 판별
    fun getMapSearchInfo() : TmapSearchInfoEntity? {
        when(val data = homeStateLiveData.value) {
            is HomeState.Success -> {
                return data.tMapSearchInfoEntity
            }
        }
        return null
    }

    fun checkMyBasket() = viewModelScope.launch {
        _foodMenuBasketLiveData.postValue(
            restaurantFoodRepository.getAllFoodMenuListInBasket()
        )
    }

    companion object {
        const val MY_LOCATION_KEY = "myLocationKey"
    }
}