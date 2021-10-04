package com.example.delivery.presentation.review.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.delivery.R
import com.example.delivery.databinding.ActivityGalleryBinding
import com.example.delivery.extensions.toGone
import com.example.delivery.extensions.toVisible
import com.example.delivery.presentation.base.BaseActivity
import com.example.delivery.presentation.review.AddRestaurantReviewActivity
import com.example.delivery.widget.adapter.GalleryPhotoListAdapter
import com.example.delivery.widget.decoration.GridDividerDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryActivity : BaseActivity<GalleryViewModel, ActivityGalleryBinding>() {

    override val viewModel: GalleryViewModel by viewModel()

    override fun getViewBinding(): ActivityGalleryBinding =
        ActivityGalleryBinding.inflate(layoutInflater)

    private val adapter = GalleryPhotoListAdapter {
        viewModel.selectPhoto(it)
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            GridDividerDecoration(
                this@GalleryActivity,
                R.drawable.bg_frame_gallery
            )
        )
        confirmButton.setOnClickListener {
            viewModel.confirmCheckedPhoto()
        }
    }

    override fun observeData() = with(viewModel) {
        galleryStateLiveData.observe(this@GalleryActivity) {
            when (it) {
                is GalleryState.Loading -> {
                    handleLoading()
                }
                is GalleryState.Success -> {
                    handleSuccess(it)
                }
                is GalleryState.Confirm -> {
                    handleConfirm(it)
                }
                else -> Unit
            }
        }
    }

    private fun handleConfirm(state: GalleryState.Confirm) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(URI_LIST_KEY, ArrayList(state.photoList.map { it.uri }))
        })
        finish()
    }

    private fun handleSuccess(state: GalleryState.Success) = with(binding) {
        progressBar.toGone()
        recyclerView.toVisible()
        adapter.setPhotoList(state.photoList)
    }

    private fun handleLoading() = with(binding) {
        progressBar.toVisible()
        recyclerView.toGone()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, GalleryActivity::class.java)

        const val URI_LIST_KEY = "uriList"
    }
}