package com.example.delivery.data.repository.review

import android.net.Uri
import androidx.lifecycle.lifecycleScope
import com.example.delivery.data.entity.ReviewEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val fireStorage : FirebaseStorage,
    private val fireStore: FirebaseFirestore
) : ReviewRepository {

    override suspend fun uploadPhoto(uriList: List<Uri>) = withContext(ioDispatcher) {
        val uploadDeferred : List<Deferred<Any>> = uriList.mapIndexed { index, uri ->
            async {
                try {
                    val fileName = "image${index}.png"
                    return@async fireStorage.reference.child("reviews/photo").child(fileName)
                        .putFile(uri)
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                        .toString()
                } catch (e:Exception) {
                    e.printStackTrace()
                    return@async Pair(uri,e)
                }
            }
        }
        return@withContext uploadDeferred.awaitAll()
    }

    override suspend fun uploadArticle(
        userId: String,
        title: String,
        content: String,
        rating: Float,
        imageUrlList: List<String>,
        orderId: String,
        restaurantTitle: String
    )= withContext(ioDispatcher) {
        val reviewEntity = ReviewEntity(
            userId = userId,
            title = title,
            createAt = System.currentTimeMillis(),
            content = content,
            rating = rating,
            imageUrlList = imageUrlList,
            orderId = orderId,
            restaurantTitle = restaurantTitle
        )
        try {
            fireStore.collection("review")
                .add(reviewEntity)
        }catch (e:Exception) {
            e.printStackTrace()
        }
    }

}