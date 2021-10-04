package com.example.delivery.presentation.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.R
import com.example.delivery.data.repository.order.OrderRepository
import com.example.delivery.data.repository.order.Result
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.model.restaurant.food.FoodModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.mapper.toFoodModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OrderMenuListViewModel(
    private val restaurantFoodRepository: RestaurantFoodRepository,
    private val orderRepository: OrderRepository,
    private val firebaseAuth: FirebaseAuth,
) : BaseViewModel() {

    private val _orderMenuStateLiveData =
        MutableLiveData<OrderMenuState>(OrderMenuState.Uninitialized)
    val orderMenuStateLiveData: LiveData<OrderMenuState>
        get() = _orderMenuStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _orderMenuStateLiveData.postValue(OrderMenuState.Loading)
        val foodMenuList = restaurantFoodRepository.getAllFoodMenuListInBasket()
        _orderMenuStateLiveData.postValue(
            OrderMenuState.Success(
                foodMenuList.map {
                    it.toFoodModel()
                }
            )
        )
    }

    fun orderMenu() = viewModelScope.launch {
        // 장바구니에 있는 모든 메뉴를 가져옴
        val foodMenuList = restaurantFoodRepository.getAllFoodMenuListInBasket()
        if (foodMenuList.isNotEmpty()) {
            val restaurantId = foodMenuList.first().restaurantId
            val restaurantTitle = foodMenuList.first().restaurantTitle
            firebaseAuth.currentUser?.let { user ->
                when (val data = orderRepository.orderMenu(user.uid,
                    restaurantId,
                    foodMenuList,
                    restaurantTitle)) {
                    is Result.Success<*> -> {
                        restaurantFoodRepository.clearFoodMenuListInBasket()
                        _orderMenuStateLiveData.postValue(
                            OrderMenuState.Order
                        )
                    }
                    is Result.Error -> {
                        _orderMenuStateLiveData.postValue(
                            OrderMenuState.Error(
                                R.string.request_error, data.e
                            )
                        )
                    }
                }
            } ?: kotlin.run {
                _orderMenuStateLiveData.postValue(
                    OrderMenuState.Error(
                        R.string.user_id_not_found, IllegalStateException()
                    )
                )
            }
        }
    }

    fun clearOrderMenu() = viewModelScope.launch {
        restaurantFoodRepository.clearFoodMenuListInBasket()
        fetchData()
    }

    fun removeOrderMenu(model: FoodModel) = viewModelScope.launch {
        restaurantFoodRepository.removeFoodMenuListInBasket(model.foodId)
        fetchData()
    }

}