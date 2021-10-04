package com.example.delivery.model.restaurant.order

import com.example.delivery.data.entity.RestaurantFoodEntity
import com.example.delivery.model.BaseModel
import com.example.delivery.model.CellType

data class OrderModel(
    override val id: Long,
    override val type: CellType = CellType.ORDER_CELL,
    val orderId: String,
    val userId: String,
    val restaurantId: Long,
    val foodMenuList : List<RestaurantFoodEntity>,
    val restaurantTitle: String
) : BaseModel(id, type)
