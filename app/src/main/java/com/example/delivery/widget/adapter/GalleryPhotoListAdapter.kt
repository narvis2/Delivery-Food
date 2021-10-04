package com.example.delivery.widget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.delivery.R
import com.example.delivery.data.entity.GalleryPhotoEntity
import com.example.delivery.databinding.ViewholderGalleryPhotoItemBinding
import com.example.delivery.extensions.load

class GalleryPhotoListAdapter(
    private val checkPhotoListener: (GalleryPhotoEntity) -> Unit
) : RecyclerView.Adapter<GalleryPhotoListAdapter.PhotoItemViewHolder>() {

    private var galleryPhotoList: List<GalleryPhotoEntity> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewholderGalleryPhotoItemBinding.inflate(layoutInflater, parent,false)
        return PhotoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        holder.bindData(galleryPhotoList[position])
        holder.bindViews(galleryPhotoList[position])
    }

    override fun getItemCount(): Int {
        return galleryPhotoList.size
    }

    inner class PhotoItemViewHolder(
        private val binding: ViewholderGalleryPhotoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: GalleryPhotoEntity) = with(binding) {
            photoImageView.load(data.uri.toString(), scaleType = CenterCrop())
            checkButton.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    if (data.isSelected) {
                        R.drawable.ic_check_enabled
                    } else {
                        R.drawable.ic_check_disabled
                    }
                )
            )
        }

        fun bindViews(data: GalleryPhotoEntity) = with(binding) {
            root.setOnClickListener {
                checkPhotoListener(data)
            }
        }
    }

    fun setPhotoList(galleryPhotoList: List<GalleryPhotoEntity>) {
        this.galleryPhotoList = galleryPhotoList
        notifyDataSetChanged()
    }
}