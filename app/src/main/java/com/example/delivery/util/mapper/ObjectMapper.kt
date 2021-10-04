package com.example.delivery.util.mapper

import android.net.Uri
import com.example.delivery.data.entity.*
import com.example.delivery.data.response.address.AddressInfo
import com.example.delivery.data.response.restaurant.RestaurantFoodResponse
import com.example.delivery.model.CellType
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.model.restaurant.food.FoodModel
import com.example.delivery.model.restaurant.order.OrderModel
import com.example.delivery.model.restaurant.review.RestaurantReviewModel
import org.koin.core.component.getScopeId

fun RestaurantModel.toRestaurantEntity() = RestaurantEntity(
    id = id,
    restaurantInfoId = restaurantInfoId,
    restaurantCategory = restaurantCategory,
    restaurantTitle = restaurantTitle,
    restaurantImageUrl = restaurantImageUrl,
    grade = grade,
    reviewCount = reviewCount,
    deliveryTimeRange = deliveryTimeRange,
    deliveryTipRange = deliveryTipRange,
    restaurantTelNumber = restaurantTelNumber
)

fun RestaurantEntity.toRestaurantModel() = RestaurantModel(
    id = id,
    type = CellType.RESTAURANT_CELL,
    restaurantInfoId = restaurantInfoId,
    restaurantCategory = restaurantCategory,
    restaurantTitle = restaurantTitle,
    restaurantImageUrl = restaurantImageUrl,
    grade = grade,
    reviewCount = reviewCount,
    deliveryTimeRange = deliveryTimeRange,
    deliveryTipRange = deliveryTipRange,
    restaurantTelNumber = restaurantTelNumber
)

fun AddressInfo.toSearchInfoEntity(
    locationLatLngEntity: LocationLatLngEntity,
) = TmapSearchInfoEntity(
    fullAddress = fullAddress ?: "주소 정보 없음",
    name = buildingName ?: "빌딩 정보 없음",
    locationLatLngEntity = locationLatLngEntity
)

fun RestaurantFoodResponse.toRestaurantFoodEntity(restaurantId: Long, restaurantTitle: String) =
    RestaurantFoodEntity(
        id = id,
        title = title,
        description = description,
        price = price.toDouble().toInt(),
        imageUrl = imageUrl,
        restaurantId = restaurantId,
        restaurantTitle = restaurantTitle
    )

fun List<RestaurantFoodEntity>.toFoodModelList(restaurantId: Long): List<FoodModel> = map {
    FoodModel(
        id = it.hashCode().toLong(),
        title = it.title,
        description = it.description,
        price = it.price,
        imageUrl = it.imageUrl,
        restaurantId = restaurantId,
        foodId = it.id,
        restaurantTitle = it.restaurantTitle
    )
}

fun RestaurantFoodEntity.toFoodModel(): FoodModel = FoodModel(
    id = hashCode().toLong(),
    type = CellType.ORDER_FOOD_CELL,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    restaurantId = restaurantId,
    foodId = id,
    restaurantTitle = restaurantTitle
)

// basketIndex -> 같은 메뉴도 여러개 담을 수 있게 하기 위한 용도
fun FoodModel.toFoodEntity(basketIndex: Int) = RestaurantFoodEntity(
    id = "${foodId}_${basketIndex}",
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    restaurantId = restaurantId,
    restaurantTitle = restaurantTitle
)

fun List<ReviewEntity>.toReviewModelList() = map {
    RestaurantReviewModel(
        id = it.hashCode().toLong(),
        title = it.title,
        description = it.content,
        grade = it.rating,
        thumbnailImageUri = if (it.imageUrlList.isNullOrEmpty()) {
            null
        } else {
            Uri.parse(it.imageUrlList.first() )
        }
    )
}

fun List<RestaurantEntity>.toRestaurantModelList() = map {
    RestaurantModel(
        id = it.id,
        type = CellType.LIKE_RESTAURANT_CELL,
        restaurantInfoId = it.restaurantInfoId,
        restaurantCategory = it.restaurantCategory,
        restaurantTitle = it.restaurantTitle,
        restaurantImageUrl = it.restaurantImageUrl,
        grade = it.grade,
        reviewCount = it.reviewCount,
        deliveryTimeRange = it.deliveryTimeRange,
        deliveryTipRange = it.deliveryTipRange,
        restaurantTelNumber = it.restaurantTelNumber
    )
}

fun OrderModel.toOrderEntity() =
    OrderEntity(
        id = orderId,
        userId = userId,
        restaurantId = restaurantId,
        foodMenuList = foodMenuList,
        restaurantTitle = restaurantTitle
    )

fun OrderEntity.toOrderModel() =
    OrderModel(
        id = hashCode().toLong(),
        orderId = id,
        userId = userId,
        restaurantId = restaurantId,
        foodMenuList = foodMenuList,
        restaurantTitle = restaurantTitle
    )

fun List<OrderEntity>.toOrderModelList() =
    map {
        OrderModel(
            id = it.hashCode().toLong(),
            orderId = it.id,
            userId = it.userId,
            restaurantId = it.restaurantId,
            foodMenuList = it.foodMenuList,
            restaurantTitle = it.restaurantTitle
        )
    }