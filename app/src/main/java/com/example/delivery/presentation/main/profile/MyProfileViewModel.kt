package com.example.delivery.presentation.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.R
import com.example.delivery.data.entity.OrderEntity
import com.example.delivery.data.preference.DeliveryPreferenceManager
import com.example.delivery.data.repository.order.OrderRepository
import com.example.delivery.data.repository.order.Result
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.mapper.toOrderModelList
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyProfileViewModel(
    private val deliveryPreferenceManager: DeliveryPreferenceManager,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel() {

    private val _myProfileStateLiveData = MutableLiveData<MyProfileState>(MyProfileState.Uninitialized)
    val myProfileStateLiveData : LiveData<MyProfileState>
        get() = _myProfileStateLiveData

    override fun fetchData(): Job = viewModelScope.launch{
        _myProfileStateLiveData.postValue(MyProfileState.Loading)
        deliveryPreferenceManager.getIdToken()?.let {
            _myProfileStateLiveData.postValue(MyProfileState.Login(it))
        } ?: kotlin.run {
            _myProfileStateLiveData.postValue(MyProfileState.Success.NotRegistered)
        }
    }

    fun saveToken(idToken: String) = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.IO) {
            deliveryPreferenceManager.putIdToken(idToken)
            fetchData()
        }
    }

    fun setUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch(Dispatchers.IO) {
        firebaseUser?.let { user ->
            when(val orderMenusResult = orderRepository.getAllOrderMenus(user.uid)) {
                is Result.Success<*> -> {
                    val orderList = orderMenusResult.data as List<OrderEntity>
                    _myProfileStateLiveData.postValue(
                        MyProfileState.Success.Registered(
                            userName = user.displayName ?: "익명",
                            profileImageUri = user.photoUrl,
                            orderList = orderList.toOrderModelList()
                        )
                    )
                }
                is Result.Error -> {
                    _myProfileStateLiveData.postValue(
                        MyProfileState.Error(
                            R.string.request_error,
                            orderMenusResult.e
                        )
                    )
                }
            }
        } ?: kotlin.run {
            _myProfileStateLiveData.postValue(
                MyProfileState.Success.NotRegistered
            )
        }
    }


    fun signOut() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            deliveryPreferenceManager.removeIdToken()
        }
        // 로그아웃시 기존의 장바구니에 있었던 모든 Menu List 삭제
        userRepository.deleteAllUserLikedRestaurant()
        fetchData()
    }

}