package com.example.delivery.data.repository.review

import android.net.Uri

interface ReviewRepository {

    suspend fun uploadPhoto(uriList: List<Uri>): List<Any>

    suspend fun uploadArticle(
        userId: String,
        title: String,
        content: String,
        rating: Float,
        imageUrlList: List<String>,
        orderId: String,
        restaurantTitle: String
    ) : Any
}