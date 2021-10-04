package com.example.delivery.di

import com.example.delivery.data.repository.map.TmapRepository
import com.example.delivery.data.repository.map.TmapRepositoryImpl
import com.example.delivery.data.repository.order.OrderRepository
import com.example.delivery.data.repository.order.OrderRepositoryImpl
import com.example.delivery.data.repository.restaurant.RestaurantRepository
import com.example.delivery.data.repository.restaurant.RestaurantRepositoryImpl
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepositoryImpl
import com.example.delivery.data.repository.restaurant.gallery.GalleryPhotoRepository
import com.example.delivery.data.repository.restaurant.gallery.GalleryPhotoRepositoryImpl
import com.example.delivery.data.repository.restaurant.review.RestaurantReviewRepository
import com.example.delivery.data.repository.restaurant.review.RestaurantReviewRepositoryImpl
import com.example.delivery.data.repository.review.ReviewRepository
import com.example.delivery.data.repository.review.ReviewRepositoryImpl
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.data.repository.user.UserRepositoryImpl
import com.example.delivery.util.IO
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule : Module = module {
    single<RestaurantRepository> { RestaurantRepositoryImpl(get(), get(), get(named(IO))) }
    single<TmapRepository> { TmapRepositoryImpl(get(), get(named(IO))) }
    single<UserRepository> { UserRepositoryImpl(get(named(IO)), get(), get()) }
    single<RestaurantFoodRepository> { RestaurantFoodRepositoryImpl(get(named(IO)),get(),get()) }
    single<RestaurantReviewRepository> { RestaurantReviewRepositoryImpl(get(named(IO)), get()) }
    single<OrderRepository> { OrderRepositoryImpl(get(named(IO)), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get(named(IO)), get(), get()) }
    single<GalleryPhotoRepository> { GalleryPhotoRepositoryImpl(androidContext(), get(named(IO))) }
}