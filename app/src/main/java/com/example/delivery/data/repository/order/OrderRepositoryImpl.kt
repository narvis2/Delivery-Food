package com.example.delivery.data.repository.order

import android.icu.text.CaseMap
import com.example.delivery.data.entity.OrderEntity
import com.example.delivery.data.entity.RestaurantFoodEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val firebaseFirestore: FirebaseFirestore,
) : OrderRepository {

    override suspend fun orderMenu(
        userId: String,
        restaurantId: Long,
        foodMenuList: List<RestaurantFoodEntity>,
        restaurantTitle: String,
    ): Result = withContext(ioDispatcher) {
        val result: Result
        val orderMenuData = hashMapOf(
            "restaurantId" to restaurantId,
            "userId" to userId,
            "orderMenuList" to foodMenuList,
            "restaurantTitle" to restaurantTitle
        )
        result = try {
            // FirebaseFirestore 에 저장
            firebaseFirestore
                .collection("order")
                .add(orderMenuData)
            Result.Success<Any>()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }

        return@withContext result
    }

    override suspend fun getAllOrderMenus(userId: String): Result = withContext(ioDispatcher) {
        return@withContext try {
            // FirebaseFirestore 에 저장된 데이터 가져오기
            val result: QuerySnapshot = firebaseFirestore
                .collection("order")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val mapper = result.documents
                .map {
                    OrderEntity(
                        id = it.id,
                        userId = it.get("userId") as String,
                        restaurantId = it.get("restaurantId") as Long,
                        foodMenuList = (it.get("orderMenuList") as ArrayList<Map<String, Any>>).map { food ->
                            RestaurantFoodEntity(
                                id = food["id"] as String,
                                title = food["title"] as String,
                                description = food["description"] as String,
                                price = (food["price"] as Long).toInt(),
                                imageUrl = food["imageUrl"] as String,
                                restaurantId = food["restaurantId"] as Long,
                                restaurantTitle = food["restaurantTitle"] as String
                            )
                        },
                        restaurantTitle = it.get("restaurantTitle") as String
                    )
                }

            Result.Success(
                mapper
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}