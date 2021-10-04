package com.example.delivery.widget.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.delivery.model.BaseModel
import com.example.delivery.model.CellType
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.util.mapper.ModelViewHolderMapper
import com.example.delivery.widget.adapter.listener.BaseAdapterListener
import com.example.delivery.widget.adapter.viewholder.BaseViewHolder

class ModelRecyclerAdapter<M: BaseModel, VM: BaseViewModel>(
    private var modelList : List<BaseModel>,
    private val viewModel: VM,
    private val resourceProvider: ResourceProvider,
    private val baseAdapterListener: BaseAdapterListener
) : ListAdapter<BaseModel, BaseViewHolder<M>>(BaseModel.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<M> {
        // CellType -> Position 에 따라서 달라져야함.
        return ModelViewHolderMapper.map(parent, CellType.values()[viewType], viewModel, resourceProvider)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: BaseViewHolder<M>, position: Int) {
        with(holder) {
            bindData(modelList[position] as M)
            bindViews(modelList[position] as M, baseAdapterListener )
        }
    }

    override fun getItemCount(): Int = modelList.size

    override fun getItemViewType(position: Int): Int = modelList[position].type.ordinal

    override fun submitList(list: List<BaseModel>?) {
        list?.let {
            modelList = it
        }
        super.submitList(list)
    }
}