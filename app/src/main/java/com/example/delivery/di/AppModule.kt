package com.example.delivery.di

import com.example.delivery.util.IO
import com.example.delivery.util.MAIN
import com.example.delivery.util.event.MenuChangeEventBus
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.util.provider.ResourceProviderImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule : Module = module {

    single<ResourceProvider> { ResourceProviderImpl(androidApplication()) }

    single<MenuChangeEventBus> { MenuChangeEventBus() }

    single<FirebaseAuth> { Firebase.auth }

    single<FirebaseFirestore> { Firebase.firestore }

    single<FirebaseStorage> { Firebase.storage }

    single(named(IO)) { Dispatchers.IO }
    single(named(MAIN)) { Dispatchers.Main }
}