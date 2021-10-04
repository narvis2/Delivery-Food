package com.example.delivery.model

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

abstract class BaseModel(
    open val id: Long,
    open val type: CellType,
) {
    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<BaseModel> =
            object : DiffUtil.ItemCallback<BaseModel>() {
                override fun areItemsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
                    return oldItem.id == newItem.id && oldItem.type == newItem.type
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
                    return oldItem === newItem
                }
            }
    }
}