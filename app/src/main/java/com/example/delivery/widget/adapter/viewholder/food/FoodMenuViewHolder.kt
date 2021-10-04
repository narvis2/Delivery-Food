package com.example.delivery.widget.adapter.viewholder.food

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.delivery.R
import com.example.delivery.databinding.ViewholderFoodMenuBinding
import com.example.delivery.extensions.clear
import com.example.delivery.extensions.load
import com.example.delivery.model.restaurant.food.FoodModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.listener.BaseAdapterListener
import com.example.delivery.widget.adapter.listener.restaurant.food.FoodMenuListListener
import com.example.delivery.widget.adapter.viewholder.BaseViewHolder

class FoodMenuViewHolder(
    private val binding : ViewholderFoodMenuBinding,
    viewModel: BaseViewModel,
    resourceProvider: ResourceProvider
) : BaseViewHolder<FoodModel>(binding, viewModel, resourceProvider) {

    override fun reset() = with(binding) {
        foodImage.clear()
    }

    override fun bindData(model: FoodModel) {
        super.bindData(model)
        with(binding) {
            foodImage.load(model.imageUrl, 24f, CenterCrop())
            foodTitleText.text = model.title
            foodDescriptionText.text = model.description
            priceText.text = resourceProvider.getString(R.string.price, model.price)
        }
    }

    override fun bindViews(model: FoodModel, baseAdapterListener: BaseAdapterListener) {
        if (baseAdapterListener is FoodMenuListListener) {
            binding.root.setOnClickListener {
                baseAdapterListener.onClickItem(model)
            }
        }
    }
}