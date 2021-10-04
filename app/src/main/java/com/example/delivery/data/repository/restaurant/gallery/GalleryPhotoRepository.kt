package com.example.delivery.data.repository.restaurant.gallery

import com.example.delivery.data.entity.GalleryPhotoEntity

interface GalleryPhotoRepository {

    suspend fun getAllPhotos() : MutableList<GalleryPhotoEntity>
}