package com.example.delivery.presentation.main.home.restaurant.detail.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.R
import com.example.delivery.data.entity.ReviewEntity
import com.example.delivery.data.repository.restaurant.review.RestaurantReviewRepository
import com.example.delivery.data.repository.restaurant.review.RestaurantReviewResult
import com.example.delivery.presentation.base.BaseViewModel
import com.example.delivery.util.mapper.toReviewModelList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantReviewListViewModel(
    private val restaurantTitle: String,
    private val restaurantReviewRepository: RestaurantReviewRepository,
) : BaseViewModel() {

    private val _restaurantReviewStateLiveData =
        MutableLiveData<RestaurantReviewState>(RestaurantReviewState.Uninitialized)
    val restaurantReviewStateLiveData: LiveData<RestaurantReviewState>
        get() = _restaurantReviewStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _restaurantReviewStateLiveData.postValue(RestaurantReviewState.Loading)

        when (val result = restaurantReviewRepository.getReviews(restaurantTitle)) {
            is RestaurantReviewResult.Success<*> -> {
                val reviews = result.data as List<ReviewEntity>
                _restaurantReviewStateLiveData.postValue(
                    RestaurantReviewState.Success(
                        reviewList = reviews.toReviewModelList()
                    )
                )
            }
            is RestaurantReviewResult.Error -> {
                _restaurantReviewStateLiveData.postValue(
                    RestaurantReviewState.Error(
                        R.string.request_error,
                        e = result.e
                    )
                )
            }
        }

    }
}