package com.example.delivery.di

import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.RestaurantEntity
import com.example.delivery.data.entity.RestaurantFoodEntity
import com.example.delivery.data.entity.TmapSearchInfoEntity
import com.example.delivery.presentation.main.home.HomeViewModel
import com.example.delivery.presentation.main.home.restaurant.RestaurantCategory
import com.example.delivery.presentation.main.home.restaurant.RestaurantListViewModel
import com.example.delivery.presentation.main.home.restaurant.detail.RestaurantDetailViewModel
import com.example.delivery.presentation.main.home.restaurant.detail.menu.RestaurantMenuListViewModel
import com.example.delivery.presentation.main.home.restaurant.detail.review.RestaurantReviewListViewModel
import com.example.delivery.presentation.main.like.RestaurantLikeListViewModel
import com.example.delivery.presentation.main.profile.MyProfileViewModel
import com.example.delivery.presentation.mylocation.MyLocationViewModel
import com.example.delivery.presentation.order.OrderMenuListViewModel
import com.example.delivery.presentation.review.AddRestaurantReviewViewModel
import com.example.delivery.presentation.review.gallery.GalleryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule: Module = module {
    viewModel<HomeViewModel> { HomeViewModel(get(), get(), get()) }
    viewModel<MyProfileViewModel> { MyProfileViewModel(get(), get(), get()) }
    viewModel<RestaurantListViewModel> { (restaurantCategory: RestaurantCategory, locationLatLngEntity: LocationLatLngEntity) ->
        RestaurantListViewModel(restaurantCategory, get(), locationLatLngEntity)
    }
    viewModel<MyLocationViewModel> { (tMapSearchInfoEntity: TmapSearchInfoEntity) ->
        MyLocationViewModel(tMapSearchInfoEntity, get(), get())
    }
    viewModel<RestaurantDetailViewModel> { (restaurantEntity: RestaurantEntity) ->
        RestaurantDetailViewModel(restaurantEntity, get(), get())
    }
    viewModel<RestaurantMenuListViewModel> { (restaurantId: Long, restaurantFoodList: List<RestaurantFoodEntity>) ->
        RestaurantMenuListViewModel(restaurantId, restaurantFoodList, get())
    }
    viewModel<RestaurantReviewListViewModel> { (restaurantTitle: String) ->
        RestaurantReviewListViewModel(restaurantTitle, get())
    }
    viewModel<RestaurantLikeListViewModel> { RestaurantLikeListViewModel(get()) }
    viewModel<OrderMenuListViewModel>{OrderMenuListViewModel(get(), get(), get())}
    viewModel<AddRestaurantReviewViewModel> { AddRestaurantReviewViewModel(get()) }
    viewModel<GalleryViewModel>{ GalleryViewModel(get()) }
}