package com.example.delivery.presentation.main.profile

import android.net.Uri
import androidx.annotation.StringRes
import com.example.delivery.model.restaurant.order.OrderModel
import kotlinx.coroutines.flow.Flow

sealed class MyProfileState {

    object Uninitialized : MyProfileState()

    object Loading : MyProfileState()

    data class Login(
        val idToken: String
    ) : MyProfileState()

    sealed class Success : MyProfileState() {

        // 유저가 로그인이 된 상태
        data class Registered(
            val userName: String,
            val profileImageUri: Uri?,
            val orderList: List<OrderModel>
        ) : Success()

        // 토큰이 없는 경우 (로그인이 안되어있을 경우)
        object NotRegistered : Success()

    }

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable
    ) : MyProfileState()
}
