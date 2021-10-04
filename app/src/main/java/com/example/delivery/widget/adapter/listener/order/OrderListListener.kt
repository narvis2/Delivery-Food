package com.example.delivery.widget.adapter.listener.order

import com.example.delivery.widget.adapter.listener.BaseAdapterListener

interface OrderListListener : BaseAdapterListener {

    fun writeRestaurantReview(orderId: String, restaurantTitle: String)
}