package com.example.delivery.presentation.mylocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.R
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.TmapSearchInfoEntity
import com.example.delivery.data.repository.map.TmapRepository
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.mapper.toSearchInfoEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyLocationViewModel(
    private val tMapSearchInfoEntity: TmapSearchInfoEntity,
    private val tMapRepository: TmapRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _myLocationStateLiveData = MutableLiveData<MyLocationState>(MyLocationState.Uninitialized)
    val myLocationStateLiveData : LiveData<MyLocationState>
        get() = _myLocationStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _myLocationStateLiveData.postValue(MyLocationState.Loading)
        _myLocationStateLiveData.postValue(
            MyLocationState.Success(
                tMapSearchInfoEntity
            )
        )
    }

    fun changeLocationInfo(
        locationLatLngEntity: LocationLatLngEntity
    ) = viewModelScope.launch {
        val addressInfo = tMapRepository.getReverseGeoInformation(locationLatLngEntity)
        addressInfo?.let { info ->
            _myLocationStateLiveData.postValue(
                MyLocationState.Success(
                    tMapSearchInfoEntity = info.toSearchInfoEntity(locationLatLngEntity)
                )
            )
        } ?: kotlin.run {
            _myLocationStateLiveData.postValue(
                MyLocationState.Error(
                    messageId = R.string.can_not_load_address_info
                )
            )
        }
    }

    fun confirmSelectLocation() = viewModelScope.launch {
        when(val data = myLocationStateLiveData.value) {
            is MyLocationState.Success -> {
                userRepository.insertUserLocation(data.tMapSearchInfoEntity.locationLatLngEntity)
                _myLocationStateLiveData.postValue(
                    MyLocationState.Confirm(
                        data.tMapSearchInfoEntity
                    )
                )
            }
        }
    }
}