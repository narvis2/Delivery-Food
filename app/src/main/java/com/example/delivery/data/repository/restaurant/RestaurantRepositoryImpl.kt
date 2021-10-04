package com.example.delivery.data.repository.restaurant

import com.example.delivery.data.api.TmapApiService
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.RestaurantEntity
import com.example.delivery.presentation.main.home.restaurant.RestaurantCategory
import com.example.delivery.util.provider.ResourceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RestaurantRepositoryImpl(
    private val tMapApiService: TmapApiService,
    private val resourceProvider: ResourceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : RestaurantRepository {

    override suspend fun getList(
        restaurantCategory: RestaurantCategory,
        locationLatLngEntity: LocationLatLngEntity
    ): List<RestaurantEntity> = withContext(ioDispatcher) {
        // TODO: 2021/07/13 API 를 통한 데이터 받아오기

        val response = tMapApiService.getSearchLocationAround(
            categories = resourceProvider.getString(restaurantCategory.categoryTypeId),
            centerLat = locationLatLngEntity.latitude.toString(),
            centerLon = locationLatLngEntity.longitude.toString(),
            searchType = "name",
            radius = "1",
            resCoordType = "EPSG3857",
            searchtypCd = "A",
            reqCoordType = "WGS84GEO"
        )

        if (response.isSuccessful) {
            response.body()?.searchPoiInfo?.pois?.poi?.map { poi ->
                RestaurantEntity(
                    id = hashCode().toLong(),
                    restaurantInfoId = (1..10).random().toLong(),
                    restaurantCategory = restaurantCategory,
                    restaurantTitle = poi.name ?: "제목 없음",
                    restaurantImageUrl = "https://picsum.photos/200",
                    grade = (1 until 5).random() + ((0..10).random() / 10f),
                    reviewCount = (0 until 200).random(),
                    deliveryTimeRange = Pair((0..20).random(), (40..60).random()),
                    deliveryTipRange = Pair((0..1000).random(), (2000..4000).random()),
                    restaurantTelNumber = poi.telNo
                )
            } ?: listOf()
        } else {
            listOf()
        }

        /*listOf(
            RestaurantEntity(
                id = 0,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "마포화로집",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000),
                restaurantInfoId = 0
            ),
            RestaurantEntity(
                id = 1,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "옛날우동 & 덮밥",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000),
                restaurantInfoId = 1
            ),
            RestaurantEntity(
                id = 2,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "마스터석쇠불고기&냉면plus",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000),
                restaurantInfoId = 2
            ),
            RestaurantEntity(
                id = 3,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "통삼겹살",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000),
                restaurantInfoId = 3
            ),
            RestaurantEntity(
                id = 4,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "창영이 족발&보쌈",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000),
                restaurantInfoId = 4
            ),
            RestaurantEntity(
                id = 5,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "콩나물국밥&코다리조림 콩심 인천논현점",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000),
                restaurantInfoId = 5
            ),
            RestaurantEntity(
                id = 6,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "김여사 칼국수&냉면 논현점",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000),
                restaurantInfoId = 6
            ),
            RestaurantEntity(
                id = 7,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "돈키호테",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000),
                restaurantInfoId = 7
            )
        )*/
    }
}