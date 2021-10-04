package com.example.delivery.widget.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.delivery.model.BaseModel
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.listener.BaseAdapterListener

abstract class BaseViewHolder<M : BaseModel>(
    binding: ViewBinding,
    protected val viewModel : BaseViewModel,
    protected val resourceProvider: ResourceProvider
) : RecyclerView.ViewHolder(binding.root) {

    abstract fun reset()

    open fun bindData(model: M) {
        reset()
    }

    abstract fun bindViews(model : M, baseAdapterListener: BaseAdapterListener)
}