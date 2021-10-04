package com.example.delivery.widget.adapter.listener.restaurant

import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.widget.adapter.listener.BaseAdapterListener

interface RestaurantListListener : BaseAdapterListener {

    fun onClickItem(model: RestaurantModel)

}