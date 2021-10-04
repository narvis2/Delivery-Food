package com.example.delivery.presentation.main.home.restaurant.detail.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.example.delivery.R
import com.example.delivery.databinding.FragmentRestaurantReviewListBinding
import com.example.delivery.extensions.toGone
import com.example.delivery.extensions.toVisible
import com.example.delivery.model.restaurant.review.RestaurantReviewModel
import com.example.delivery.presentation.base.BaseFragment
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.BaseAdapterListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class RestaurantReviewListFragment :
    BaseFragment<RestaurantReviewListViewModel, FragmentRestaurantReviewListBinding>() {

    override val viewModel: RestaurantReviewListViewModel by viewModel {
        parametersOf(
            arguments?.getString(RESTAURANT_TITLE_KEY)
        )
    }

    override fun getViewBinding(): FragmentRestaurantReviewListBinding =
        FragmentRestaurantReviewListBinding.inflate(layoutInflater)

    private val resourceProvider: ResourceProvider by inject()

    private val adapter by lazy {
        ModelRecyclerAdapter<RestaurantReviewModel, RestaurantReviewListViewModel>(
            listOf(),
            viewModel,
            resourceProvider,
            baseAdapterListener = object : BaseAdapterListener {

            }
        )
    }

    override fun initViews() {
        binding.reviewRecyclerView.adapter = adapter
    }

    override fun observeData() = with(viewModel) {
        restaurantReviewStateLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RestaurantReviewState.Loading -> {
                    binding.reviewProgressBar.toVisible()
                }
                is RestaurantReviewState.Success -> {
                    binding.reviewProgressBar.toGone()
                    handleSuccess(it)
                }
                is RestaurantReviewState.Error -> {
                    handleError(it)
                }
                else -> Unit
            }
        })
    }

    private fun handleError(state: RestaurantReviewState.Error) {
        showToast(getString(R.string.request_error, state.e))
    }

    private fun handleSuccess(state: RestaurantReviewState.Success) {
        adapter.submitList(state.reviewList)
    }

    companion object {

        const val RESTAURANT_TITLE_KEY = "restaurantTitle"

        fun newInstance(restaurantTitle: String): RestaurantReviewListFragment {
            val bundle = bundleOf(
                RESTAURANT_TITLE_KEY to restaurantTitle
            )
            return RestaurantReviewListFragment().apply {
                arguments = bundle
            }
        }
    }
}