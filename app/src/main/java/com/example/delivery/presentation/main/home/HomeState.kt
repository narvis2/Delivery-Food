package com.example.delivery.presentation.main.home

import androidx.annotation.StringRes
import com.example.delivery.data.entity.TmapSearchInfoEntity

sealed class HomeState {

    object Uninitialized: HomeState()

    object Loading : HomeState()

    data class Success(
        val tMapSearchInfoEntity: TmapSearchInfoEntity,
        val isLocationSame : Boolean
    ) : HomeState()

    data class Error(
        @StringRes val messageId: Int
    ) : HomeState()
}
