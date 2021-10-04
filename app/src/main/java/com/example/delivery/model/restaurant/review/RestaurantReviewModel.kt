package com.example.delivery.model.restaurant.review

import android.net.Uri
import com.example.delivery.model.BaseModel
import com.example.delivery.model.CellType

data class RestaurantReviewModel(
    override val id: Long,
    override val type: CellType = CellType.REVIEW_CELL,
    val title: String,
    val description: String,
    val grade: Float,
    val thumbnailImageUri : Uri? = null
) : BaseModel(id, type)
