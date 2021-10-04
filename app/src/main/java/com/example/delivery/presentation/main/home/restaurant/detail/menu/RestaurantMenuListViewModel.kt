package com.example.delivery.presentation.main.home.restaurant.detail.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.data.entity.RestaurantFoodEntity
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.model.restaurant.food.FoodModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.mapper.toFoodEntity
import com.example.delivery.util.mapper.toFoodModelList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantMenuListViewModel(
    private val restaurantId: Long,
    private val foodEntityList: List<RestaurantFoodEntity>,
    private val restaurantFoodRepository: RestaurantFoodRepository,
) : BaseViewModel() {

    private val _restaurantFoodListLiveData = MutableLiveData<List<FoodModel>>()
    val restaurantFoodListLiveData: LiveData<List<FoodModel>>
        get() = _restaurantFoodListLiveData

    private val _isClearNeedInBasketLiveData = MutableLiveData<Pair<Boolean, () -> Unit>>()
    val isClearNeedInBasketLiveData: LiveData<Pair<Boolean, () -> Unit>>
        get() = _isClearNeedInBasketLiveData

    private val _menuBasketLiveData = MutableLiveData<RestaurantFoodEntity>()
    val menuBasketLiveData : LiveData<RestaurantFoodEntity>
        get() = _menuBasketLiveData


    override fun fetchData(): Job = viewModelScope.launch {
        _restaurantFoodListLiveData.postValue(foodEntityList.toFoodModelList(restaurantId))
    }

    fun insertMenuInBasket(foodModel: FoodModel) = viewModelScope.launch {
        /**
         * 하나의 식당에 대한 메뉴만 장바구니에 담을 수 있음.
         */
        // 현재 식당에 담겨진 장바구니 리스트 가져오기
        val restaurantMenuListInBasket =
            restaurantFoodRepository.getFoodMenuListInBasket(restaurantId)

        val foodMenuEntity = foodModel.toFoodEntity(restaurantMenuListInBasket.size)

        // 다른 식당에 있는 메뉴가 장바구니에 있는지 체크
        val anotherRestaurantMenuListInBasket =
            restaurantFoodRepository.getAllFoodMenuListInBasket()
                .filter { it.restaurantId != restaurantId }

        // 다른 식당에 있는 메뉴가 장바구니에 있으면 장바구니를 비움 없으면 장바구니 저장
        if (anotherRestaurantMenuListInBasket.isNotEmpty()) {
            _isClearNeedInBasketLiveData.postValue(Pair(true, {clearMenuAndInsertNewMenuInBasket(foodMenuEntity)}))
        } else {
            restaurantFoodRepository.insertFoodMenuInBasket(foodMenuEntity)
            _menuBasketLiveData.postValue(
                foodMenuEntity
            )
        }

    }

    // 기존의 메뉴 삭제하고 새로운 메뉴 장바구니에 넣기
    private fun clearMenuAndInsertNewMenuInBasket(foodMenuEntity: RestaurantFoodEntity) =
        viewModelScope.launch {
            restaurantFoodRepository.clearFoodMenuListInBasket()
            restaurantFoodRepository.insertFoodMenuInBasket(foodMenuEntity)
            _menuBasketLiveData.postValue(
                foodMenuEntity
            )
        }

}