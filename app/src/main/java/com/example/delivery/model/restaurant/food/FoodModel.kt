package com.example.delivery.model.restaurant.food

import com.example.delivery.model.BaseModel
import com.example.delivery.model.CellType

data class FoodModel(
    override val id: Long,
    override val type: CellType = CellType.FOOD_CELL,
    val title: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val restaurantId: Long,
    val foodId : String,
    val restaurantTitle: String
) : BaseModel(id, type)
