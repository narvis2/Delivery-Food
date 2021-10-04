package com.example.delivery.widget.adapter.viewholder.review

import com.example.delivery.databinding.ViewholderRestaurantReviewBinding
import com.example.delivery.extensions.clear
import com.example.delivery.extensions.load
import com.example.delivery.extensions.toGone
import com.example.delivery.extensions.toVisible
import com.example.delivery.model.restaurant.review.RestaurantReviewModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.listener.BaseAdapterListener
import com.example.delivery.widget.adapter.viewholder.BaseViewHolder

class RestaurantReviewViewHolder(
    private val binding: ViewholderRestaurantReviewBinding,
    viewModel: BaseViewModel,
    resourceProvider: ResourceProvider,
) : BaseViewHolder<RestaurantReviewModel>(binding, viewModel, resourceProvider) {

    override fun reset() = with(binding) {
        reviewThumbnailImage.clear()
        reviewThumbnailImage.toGone()
    }

    override fun bindData(model: RestaurantReviewModel) {
        super.bindData(model)
        with(binding) {
            if (model.thumbnailImageUri != null) {
                reviewThumbnailImage.toVisible()
                reviewThumbnailImage.load(model.thumbnailImageUri.toString())
            } else {
                reviewThumbnailImage.toGone()
            }

            reviewTitleText.text = model.title
            reviewContentText.text = model.description
            ratingBar.rating = model.grade
        }
    }

    override fun bindViews(model: RestaurantReviewModel, baseAdapterListener: BaseAdapterListener) =
        Unit
}