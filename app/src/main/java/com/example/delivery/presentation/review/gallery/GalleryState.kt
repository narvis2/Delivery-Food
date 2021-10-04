package com.example.delivery.presentation.review.gallery

import com.example.delivery.data.entity.GalleryPhotoEntity

sealed class GalleryState {
    object Uninitialized : GalleryState()

    object Loading : GalleryState()

    data class Success(
        val photoList : List<GalleryPhotoEntity>
    ) : GalleryState()

    data class Confirm(
        val photoList: List<GalleryPhotoEntity>
    ) : GalleryState()
}
