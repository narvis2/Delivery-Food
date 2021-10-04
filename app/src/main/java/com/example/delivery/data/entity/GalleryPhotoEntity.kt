package com.example.delivery.data.entity

import android.net.Uri

data class GalleryPhotoEntity(
    val id: Long,
    val uri: Uri,
    val name: String,
    val date: String,
    val size: Int,
    val isSelected: Boolean = false
)