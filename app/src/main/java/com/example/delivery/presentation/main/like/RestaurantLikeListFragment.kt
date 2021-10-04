package com.example.delivery.presentation.main.like

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.delivery.databinding.FragmentRestaurantLikeListBinding
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.presentation.base.BaseFragment
import com.example.delivery.presentation.main.home.restaurant.detail.RestaurantDetailActivity
import com.example.delivery.util.mapper.toRestaurantEntity
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.restaurant.RestaurantLikeListListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RestaurantLikeListFragment :
    BaseFragment<RestaurantLikeListViewModel, FragmentRestaurantLikeListBinding>() {

    override val viewModel: RestaurantLikeListViewModel by viewModel()

    override fun getViewBinding(): FragmentRestaurantLikeListBinding =
        FragmentRestaurantLikeListBinding.inflate(layoutInflater)

    private val resourceProvider: ResourceProvider by inject()

    private val adapter by lazy {
        ModelRecyclerAdapter<RestaurantModel, RestaurantLikeListViewModel>(
            listOf(),
            viewModel,
            resourceProvider,
            baseAdapterListener = object : RestaurantLikeListListener {
                override fun onDislikeItem(model: RestaurantModel) {
                    viewModel.dislikeRestaurant(model.toRestaurantEntity())
                    showToast("찜한 ${model.restaurantTitle}이 삭제되었습니다.")
                }

                override fun onClickItem(model: RestaurantModel) {
                    startActivity(
                        RestaurantDetailActivity.newIntent(requireContext(), model.toRestaurantEntity())
                    )
                }
            }
        )
    }

    override fun initViews() {
        binding.likeRecyclerView.adapter = adapter
    }

    override fun observeData() = with(viewModel) {
        getSavedLikeItem().observe(viewLifecycleOwner, Observer {
            checkListEmpty(it.toList())
        })
    }

    private fun checkListEmpty( restaurantList: List<RestaurantModel>) {
        val isEmpty = restaurantList.isEmpty()
        binding.likeRecyclerView.isGone = isEmpty
        binding.emptyResultTextView.isVisible = isEmpty
        if (isEmpty.not()) {
            adapter.submitList(restaurantList)
        }
    }

    companion object {

        const val TAG ="restaurantLikeListFragment"

        fun newInstance() = RestaurantLikeListFragment()

    }
}