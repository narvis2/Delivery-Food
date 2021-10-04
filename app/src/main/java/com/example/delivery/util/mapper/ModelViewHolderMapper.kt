package com.example.delivery.util.mapper

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.delivery.databinding.*
import com.example.delivery.model.BaseModel
import com.example.delivery.model.CellType
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.viewholder.BaseViewHolder
import com.example.delivery.widget.adapter.viewholder.EmptyViewHolder
import com.example.delivery.widget.adapter.viewholder.food.FoodMenuViewHolder
import com.example.delivery.widget.adapter.viewholder.like.RestaurantLikeViewHolder
import com.example.delivery.widget.adapter.viewholder.order.OrderMenuViewHolder
import com.example.delivery.widget.adapter.viewholder.order.OrderViewHolder
import com.example.delivery.widget.adapter.viewholder.restaurant.RestaurantViewHolder
import com.example.delivery.widget.adapter.viewholder.review.RestaurantReviewViewHolder

object ModelViewHolderMapper {

    @Suppress("UNCHECKED_CAST")
    fun <M: BaseModel> map(
        parent: ViewGroup,
        type: CellType,
        viewModel: BaseViewModel,
        resourceProvider: ResourceProvider
    ) : BaseViewHolder<M> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder = when(type) {
            CellType.EMPTY_CELL -> EmptyViewHolder(
                binding = ViewholderEmptyBinding.inflate(layoutInflater, parent, false),
                viewModel = viewModel,
                resourceProvider = resourceProvider
            )
            CellType.RESTAURANT_CELL -> RestaurantViewHolder(
                binding = ViewholderRestaurantBinding.inflate(layoutInflater, parent, false),
                viewModel = viewModel,
                resourceProvider = resourceProvider
            )
            CellType.LIKE_RESTAURANT_CELL -> RestaurantLikeViewHolder(
                ViewholderLikeRestaurantBinding.inflate(layoutInflater,parent,false),
                viewModel = viewModel,
                resourceProvider = resourceProvider
            )
            CellType.FOOD_CELL -> FoodMenuViewHolder(
                binding = ViewholderFoodMenuBinding.inflate(layoutInflater, parent, false),
                viewModel = viewModel,
                resourceProvider = resourceProvider
            )
            CellType.REVIEW_CELL -> RestaurantReviewViewHolder(
                ViewholderRestaurantReviewBinding.inflate(layoutInflater, parent, false),
                viewModel = viewModel,
                resourceProvider = resourceProvider
            )
            CellType.ORDER_FOOD_CELL -> OrderMenuViewHolder(
                binding = ViewholderOrderMenuBinding.inflate(layoutInflater, parent, false),
                viewModel = viewModel,
                resourceProvider = resourceProvider
            )
            CellType.ORDER_CELL -> OrderViewHolder(
                binding = ViewholderOrderBinding.inflate(layoutInflater, parent, false),
                viewModel = viewModel,
                resourceProvider = resourceProvider
            )
        }
        return viewHolder as BaseViewHolder<M>
    }

}