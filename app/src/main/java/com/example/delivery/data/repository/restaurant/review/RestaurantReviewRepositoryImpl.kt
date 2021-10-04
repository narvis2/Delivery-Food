package com.example.delivery.data.repository.restaurant.review

import com.example.delivery.data.entity.ReviewEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RestaurantReviewRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val firestore: FirebaseFirestore
) : RestaurantReviewRepository {

    override suspend fun getReviews(restaurantTitle: String): RestaurantReviewResult =
        withContext(ioDispatcher) {
            return@withContext try {
                val snapshot = firestore
                    .collection("review")
                    .whereEqualTo("restaurantTitle", restaurantTitle)
                    .get()
                    .await()
                RestaurantReviewResult.Success(snapshot.documents.map {
                    ReviewEntity(
                        userId = it.get("userId") as String,
                        title = it.get("title") as String,
                        createAt = it.get("createAt") as Long,
                        content = it.get("content") as String,
                        rating = (it.get("rating") as Double).toFloat(),
                        imageUrlList = it.get("imageUrlList") as? List<String>,
                        orderId = it.get("orderId") as String,
                        restaurantTitle = it.get("restaurantTitle") as String
                    )
                })
            }catch (e: Exception) {
                e.printStackTrace()
                RestaurantReviewResult.Error(e)
            }
        }


}