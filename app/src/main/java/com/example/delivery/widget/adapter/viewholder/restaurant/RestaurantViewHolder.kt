package com.example.delivery.widget.adapter.viewholder.restaurant

import com.example.delivery.R
import com.example.delivery.databinding.ViewholderRestaurantBinding
import com.example.delivery.extensions.clear
import com.example.delivery.extensions.load
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.listener.BaseAdapterListener
import com.example.delivery.widget.adapter.listener.restaurant.RestaurantListListener
import com.example.delivery.widget.adapter.viewholder.BaseViewHolder

class RestaurantViewHolder(
    private val binding: ViewholderRestaurantBinding,
    viewModel: BaseViewModel,
    resourceProvider: ResourceProvider
) : BaseViewHolder<RestaurantModel>(binding, viewModel, resourceProvider) {

    override fun reset() = with(binding) {
        restaurantImage.clear()
    }

    override fun bindViews(model: RestaurantModel, baseAdapterListener: BaseAdapterListener) =
        with(binding) {
            if (baseAdapterListener is RestaurantListListener) {
                root.setOnClickListener {
                    baseAdapterListener.onClickItem(model)
                }
            }
        }

    override fun bindData(model: RestaurantModel) = with(binding) {
        restaurantImage.load(model.restaurantImageUrl, 24f)
        restaurantTitleText.text = model.restaurantTitle
        gradeText.text = resourceProvider.getString(R.string.grade_format, model.grade)
        reviewCountText.text = resourceProvider.getString(R.string.review_count, model.reviewCount)
        val (minTime, maxTime) = model.deliveryTimeRange
        deliveryTimeText.text = resourceProvider.getString(R.string.delivery_time, minTime, maxTime)

        val (minTip, maxTip) = model.deliveryTipRange
        deliveryTipText.text = resourceProvider.getString(R.string.delivery_tip, minTip, maxTip)
    }
}