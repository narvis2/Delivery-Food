package com.example.delivery.widget.adapter.listener.restaurant.food

import com.example.delivery.model.restaurant.food.FoodModel
import com.example.delivery.widget.adapter.listener.BaseAdapterListener

interface FoodMenuListListener : BaseAdapterListener {

    fun onClickItem(model: FoodModel)
}