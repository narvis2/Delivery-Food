package com.example.delivery.presentation.review



sealed class ReviewState {

    object Uninitialized: ReviewState()

    object Loading: ReviewState()

    sealed class Upload : ReviewState() {
        data class AfterUploadPhoto(
            val userId: String,
            val title: String,
            val content: String,
            val rating: Float,
            val imageUrlList: List<Any>
        ) : Upload()

        data class UploadArticle(
            val userId: String,
            val title: String,
            val content: String,
            val rating: Float,
            val imageUrlList: List<String>
        ) : Upload()
    }

    object Success : ReviewState()
}
