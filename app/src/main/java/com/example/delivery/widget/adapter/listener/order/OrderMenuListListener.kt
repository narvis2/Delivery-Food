package com.example.delivery.widget.adapter.listener.order

import com.example.delivery.model.restaurant.food.FoodModel
import com.example.delivery.widget.adapter.listener.BaseAdapterListener

interface OrderMenuListListener : BaseAdapterListener {

    fun onRemoveItem(model: FoodModel)

}