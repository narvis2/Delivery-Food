package com.example.delivery.presentation.main.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.data.entity.RestaurantEntity
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.mapper.toRestaurantModelList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RestaurantLikeListViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    override fun fetchData(): Job = viewModelScope.launch {
        getSavedLikeItem()
    }

    fun getSavedLikeItem() : LiveData<List<RestaurantModel>> = liveData {

        val response = userRepository.getAllUserLikedRestaurant()

        response.collect {
            val result = it.toRestaurantModelList()
            emit(result)
        }

    }

    fun dislikeRestaurant(restaurantModel: RestaurantEntity) = viewModelScope.launch {
        userRepository.deleteUserLikedRestaurant(restaurantModel.restaurantTitle)
        fetchData()
    }

}