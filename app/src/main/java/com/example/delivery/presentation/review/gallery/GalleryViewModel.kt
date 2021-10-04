package com.example.delivery.presentation.review.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.delivery.data.entity.GalleryPhotoEntity
import com.example.delivery.data.repository.restaurant.gallery.GalleryPhotoRepository
import com.example.delivery.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val galleryPhotoRepository: GalleryPhotoRepository
) : BaseViewModel() {

    private lateinit var photoList: MutableList<GalleryPhotoEntity>

    private val _galleryStateLiveData = MutableLiveData<GalleryState>(GalleryState.Uninitialized)
    val galleryStateLiveData : LiveData<GalleryState>
        get() = _galleryStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        setState(GalleryState.Loading)
        photoList = galleryPhotoRepository.getAllPhotos()
        setState(
            GalleryState.Success(
                photoList = photoList
            )
        )
    }

    fun selectPhoto(galleryPhotoEntity: GalleryPhotoEntity) {
        val findGalleryPhoto = photoList.find { it.id == galleryPhotoEntity.id }
        findGalleryPhoto?.let { photo ->
            photoList[photoList.indexOf(photo)] =
                photo.copy(
                    isSelected = !photo.isSelected
                )
            setState(GalleryState.Success(photoList))
        }
    }

    fun confirmCheckedPhoto() {
        setState(GalleryState.Loading)
        setState(GalleryState.Confirm(
            // 체크된 포토만 가져옴
            photoList= photoList.filter { it.isSelected }
        ))
    }

    private fun setState(state: GalleryState) {
        _galleryStateLiveData.postValue(state)
    }
}