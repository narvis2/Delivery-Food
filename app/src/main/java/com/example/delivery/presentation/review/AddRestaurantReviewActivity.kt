package com.example.delivery.presentation.review

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.delivery.databinding.ActivityAddRestaurantReviewBinding
import com.example.delivery.extensions.toGone
import com.example.delivery.extensions.toVisible
import com.example.delivery.presentation.base.BaseActivity
import com.example.delivery.presentation.review.camera.CameraActivity
import com.example.delivery.presentation.review.gallery.GalleryActivity
import com.example.delivery.widget.adapter.PhotoListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddRestaurantReviewActivity :
    BaseActivity<AddRestaurantReviewViewModel, ActivityAddRestaurantReviewBinding>() {

    override val viewModel: AddRestaurantReviewViewModel by viewModel {
        parametersOf(imageUriList)
    }

    override fun getViewBinding(): ActivityAddRestaurantReviewBinding =
        ActivityAddRestaurantReviewBinding.inflate(layoutInflater)

    private var imageUriList: ArrayList<Uri> = arrayListOf()

    private val auth: FirebaseAuth by inject()

    private val photoListAdapter = PhotoListAdapter { uri -> removePhoto(uri) }

    private val restaurantTitle by lazy { intent?.getStringExtra("restaurantTitle") ?: "식당이름" }

    private val orderId by lazy { intent?.getStringExtra("orderId") ?: "익명" }

    private val startCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val uriList = it.getParcelableArrayListExtra<Uri>("uriList")
                    uriList?.let { list ->
                        imageUriList.addAll(list)
                        photoListAdapter.setPhotoList(imageUriList)
                    }
                } ?: kotlin.run {
                    showToast("카메라에서 사진을 가져오지 못했습니다.")
                }
            }
        }

    private val startGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val uriList = it.getParcelableArrayListExtra<Uri>("uriList")
                    uriList?.let { list ->
                        imageUriList.addAll(list)
                        photoListAdapter.setPhotoList(imageUriList)
                    }
                } ?: kotlin.run {
                    showToast("갤러리에서 사진을 가져오지 못했습니다.")
                }
            }
        }

    override fun initViews() = with(binding) {
        photoRecyclerView.adapter = photoListAdapter

        titleTextView.text = restaurantTitle

        toolBar.setNavigationOnClickListener {
            finish()
        }

        imageAddButton.setOnClickListener {
            showPictureUploadDialog()
        }

        submitButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val userId = auth.currentUser?.uid.orEmpty()
            val rating = binding.ratingBar.rating

            // 중간에 이미지가 있으면 업로드 과정을 추가
            if (imageUriList.isNotEmpty()) {
                viewModel.uploadUri(
                    uriList = imageUriList,
                    userId = userId,
                    title = title,
                    content = content,
                    rating = rating
                )
            } else {
                viewModel.upLoadArticle(userId, title, content, rating, listOf(), orderId, restaurantTitle)
                showToast("리뷰가 성공적으로 업로드 되었습니다.")
                finish()
            }
        }
    }

    override fun observeData() = with(viewModel) {
        reviewStateLiveData.observe(this@AddRestaurantReviewActivity) {
            when (it) {
                is ReviewState.Loading -> {
                    handleLoading()
                }
                is ReviewState.Upload -> {
                    handleUpLoad(it)
                }
                else -> Unit
            }
        }
    }

    private fun handleUpLoad(state: ReviewState.Upload)  {
        when(state) {
            is ReviewState.Upload.AfterUploadPhoto -> {
                afterUploadPhoto(state)
                showToast("리뷰가 성공적으로 업로드 되었습니다.")
                finish()
            }
            is ReviewState.Upload.UploadArticle -> {
                viewModel.upLoadArticle(
                    userId = state.userId,
                    title = state.title,
                    content = state.content,
                    rating = state.rating,
                    imageUrlList = listOf(),
                    orderId = orderId,
                    restaurantTitle = restaurantTitle
                )

                binding.progressBar.toGone()

                showToast("리뷰가 성공적으로 업로드 되었습니다.")
                finish()
            }
        }
    }

    private fun afterUploadPhoto(state: ReviewState.Upload.AfterUploadPhoto) {
        val errorResults = state.imageUrlList.filterIsInstance<Pair<Uri, Exception>>()
        val successResults = state.imageUrlList.filterIsInstance<String>()

        when {
            errorResults.isNotEmpty() && successResults.isNotEmpty() -> {
                photoUploadErrorButContinueDialog(
                    errorResults,
                    successResults,
                    state.title,
                    state.content,
                    state.rating,
                    state.userId
                )
            }
            errorResults.isNotEmpty() && successResults.isEmpty() -> {
                uploadError()
            }
            else -> {
                viewModel.upLoadArticle(
                    userId = state.userId,
                    title = state.title,
                    content = state.content,
                    rating = state.rating,
                    imageUrlList = successResults,
                    orderId = orderId,
                    restaurantTitle = restaurantTitle
                )
            }
        }
    }

    private fun photoUploadErrorButContinueDialog(
        errorResults: List<Pair<Uri, Exception>>,
        successResults: List<String>,
        title: String,
        content: String,
        rating: Float,
        userId: String,
    ) {
        AlertDialog.Builder(this)
            .setTitle("특정 이미지 업로드 실패")
            .setMessage("업로드에 실패한 이미지가 있습니다."+errorResults.map { (uri, _) ->
                "$uri\n"
            } + "그럼에도 불구하고 업로드 하시겠습니까?")
            .setPositiveButton("업로드") { _, _ ->
                viewModel.upLoadArticle(userId, title, content, rating, successResults, orderId, restaurantTitle)
            }
            .create()
            .show()
    }

    private fun handleLoading() {
        binding.progressBar.toVisible()
    }

    private fun showPictureUploadDialog() {
        AlertDialog.Builder(this)
            .setTitle("사진첨부")
            .setMessage("사진 첨부할 방식을 선택하세요.")
            .setPositiveButton("카메라") { _, _ ->
                checkExternalStoragePermission {
                    startCameraScreen()
                }
            }
            .setNegativeButton("갤러리") { _, _ ->
                checkExternalStoragePermission {
                    startGalleryScreen()
                }
            }
            .create()
            .show()
    }

    private fun startCameraScreen() {
        startCameraLauncher.launch(CameraActivity.newIntent(this))
    }

    private fun startGalleryScreen() {
        startGalleryLauncher.launch(GalleryActivity.newIntent(this))
    }

    // 외부 저장소 권한 확인
    private fun checkExternalStoragePermission(uploadAction: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                uploadAction()
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionContextPopup()
            }

            else -> {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE)
            }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGalleryScreen()
                } else {
                    showToast("권한을 거부하셨습니다.")
                }
        }
    }

    private fun uploadError() {
        showToast("사진 업로드에 실패하였습니다.")
        binding.progressBar.toGone()
    }

    private fun removePhoto(uri: Uri) {
        imageUriList.remove(uri)
        photoListAdapter.setPhotoList(imageUriList)
    }

    companion object {

        fun newIntent(
            context: Context,
            orderId: String,
            restaurantTitle: String,
        ) = Intent(context, AddRestaurantReviewActivity::class.java).apply {
            putExtra("orderId", orderId)
            putExtra("restaurantTitle", restaurantTitle)
        }

        const val PERMISSION_REQUEST_CODE = 1000
    }
}