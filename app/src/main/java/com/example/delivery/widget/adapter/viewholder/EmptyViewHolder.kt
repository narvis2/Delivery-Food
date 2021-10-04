package com.example.delivery.widget.adapter.viewholder

import com.example.delivery.databinding.ViewholderEmptyBinding
import com.example.delivery.model.BaseModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.listener.BaseAdapterListener

class EmptyViewHolder(
    private val binding: ViewholderEmptyBinding,
    viewModel: BaseViewModel,
    resourceProvider: ResourceProvider
) : BaseViewHolder<BaseModel>(binding, viewModel, resourceProvider) {

    override fun reset() = Unit

    override fun bindViews(model: BaseModel, baseAdapterListener: BaseAdapterListener) = Unit
}