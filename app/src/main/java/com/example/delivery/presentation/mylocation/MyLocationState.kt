package com.example.delivery.presentation.mylocation

import androidx.annotation.StringRes
import com.example.delivery.data.entity.TmapSearchInfoEntity

sealed class MyLocationState {

    object Uninitialized : MyLocationState()

    object Loading : MyLocationState()

    data class Success(
        val tMapSearchInfoEntity: TmapSearchInfoEntity
    ) : MyLocationState()

    // 위치가 변경되고 최종적으로 확인된 위치를 넘겨줌
    data class Confirm(
        val tMapSearchInfoEntity: TmapSearchInfoEntity
    ) : MyLocationState()

    data class Error(
        @StringRes val messageId: Int
    ) : MyLocationState()

}
