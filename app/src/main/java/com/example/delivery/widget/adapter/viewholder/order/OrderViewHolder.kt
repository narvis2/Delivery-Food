package com.example.delivery.widget.adapter.viewholder.order

import com.example.delivery.R
import com.example.delivery.databinding.ViewholderOrderBinding
import com.example.delivery.model.restaurant.order.OrderModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.listener.BaseAdapterListener
import com.example.delivery.widget.adapter.listener.order.OrderListListener
import com.example.delivery.widget.adapter.viewholder.BaseViewHolder

class OrderViewHolder(
    private val binding: ViewholderOrderBinding,
    viewModel: BaseViewModel,
    resourceProvider: ResourceProvider,
) : BaseViewHolder<OrderModel>(binding, viewModel, resourceProvider) {

    override fun reset() = Unit

    override fun bindData(model: OrderModel) {
        super.bindData(model)
        with(binding) {
            orderTitleText.text =
                resourceProvider.getString(R.string.order_history_title, model.restaurantTitle)

            val foodMenuList = model.foodMenuList

            // 같은 메뉴인 경우 하나로 합쳐줌
            foodMenuList
                .groupBy { it.title }
                .entries.forEach { (title, menuList) ->
                    val orderDataStr =
                        orderContentText.text.toString() + "메뉴 : $title | 가격 : ${menuList.first().price} 원 X ${menuList.size}\n"
                    orderContentText.text = orderDataStr
                }
            orderContentText.text = orderContentText.text.trim()

            orderTotalPriceText.text =
                resourceProvider.getString(
                    R.string.price,
                    foodMenuList.map { it.price }.reduce { total, price -> total + price }
                )
        }
    }

    override fun bindViews(model: OrderModel, baseAdapterListener: BaseAdapterListener) {
        if (baseAdapterListener is OrderListListener) {
            binding.root.setOnClickListener {
                baseAdapterListener.writeRestaurantReview(model.orderId, model.restaurantTitle)
            }
        }
    }
}