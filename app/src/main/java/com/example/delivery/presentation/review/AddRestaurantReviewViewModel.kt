package com.example.delivery.presentation.review

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.data.entity.ReviewEntity
import com.example.delivery.data.repository.review.ReviewRepository
import com.example.delivery.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddRestaurantReviewViewModel(
    private val reviewRepository: ReviewRepository,
) : BaseViewModel() {

    private val _reviewStateLiveData = MutableLiveData<ReviewState>(ReviewState.Uninitialized)
    val reviewStateLiveData: LiveData<ReviewState>
        get() = _reviewStateLiveData

    fun uploadUri(
        uriList: List<Uri>,
        userId: String,
        title: String,
        content: String,
        rating: Float,
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            val response = reviewRepository.uploadPhoto(uriList)
            _reviewStateLiveData.postValue(ReviewState.Loading)

            if (uriList.isNotEmpty()) {
                _reviewStateLiveData.postValue(ReviewState.Upload.AfterUploadPhoto(
                    userId = userId,
                    title = title,
                    content = content,
                    rating = rating,
                    imageUrlList = response
                ))
            } else {
                _reviewStateLiveData.postValue(ReviewState.Upload.UploadArticle(
                    userId = userId,
                    title = title,
                    content = content,
                    rating = rating,
                    imageUrlList = listOf()
                ))
            }
        }

    fun upLoadArticle(
        userId: String,
        title: String,
        content: String,
        rating: Float,
        imageUrlList: List<String>,
        orderId: String,
        restaurantTitle: String,
    ) = viewModelScope.launch {
        reviewRepository.uploadArticle(
            userId = userId,
            title = title,
            content = content,
            rating = rating,
            imageUrlList = imageUrlList,
            orderId = orderId,
            restaurantTitle = restaurantTitle
        )
    }

}