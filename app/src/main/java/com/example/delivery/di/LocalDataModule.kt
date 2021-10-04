package com.example.delivery.di

import androidx.room.Room
import com.example.delivery.data.db.DeliveryDatabase
import com.example.delivery.data.db.dao.FoodMenuBasketDao
import com.example.delivery.data.db.dao.LocationDao
import com.example.delivery.data.db.dao.RestaurantDao
import com.example.delivery.data.preference.DeliveryPreferenceManager
import com.example.delivery.util.DB_NAME
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

val localDataModule : Module = module {

    single<LocationDao> { get<DeliveryDatabase>().locationDao()  }

    single<DeliveryPreferenceManager> { DeliveryPreferenceManager(androidApplication()) }

    single<RestaurantDao> { get<DeliveryDatabase>().restaurantDao() }

    single<FoodMenuBasketDao> { get<DeliveryDatabase>().foodMenuBasketDao() }

    single<DeliveryDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            DeliveryDatabase::class.java,
            DB_NAME
        ).build()
    }
}