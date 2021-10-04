package com.example.delivery.presentation.main.home.restaurant

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.databinding.FragmentRestaurantListBinding
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.presentation.base.BaseFragment
import com.example.delivery.presentation.main.home.restaurant.detail.RestaurantDetailActivity
import com.example.delivery.util.mapper.toRestaurantEntity
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.restaurant.RestaurantListListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestaurantListFragment : BaseFragment<RestaurantListViewModel, FragmentRestaurantListBinding>() {

    private val restaurantCategory by lazy {
        arguments?.getSerializable(RESTAURANT_CATEGORY_KEY) as RestaurantCategory
    }

    private val locationLatLngEntity by lazy {
        arguments?.getParcelable<LocationLatLngEntity>(LOCATION_KEY)
    }

    override val viewModel: RestaurantListViewModel by viewModel {
        parametersOf(restaurantCategory, locationLatLngEntity)
    }

    override fun getViewBinding(): FragmentRestaurantListBinding = FragmentRestaurantListBinding.inflate(layoutInflater)

    private val resourceProvider by inject<ResourceProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<RestaurantModel, RestaurantListViewModel>(
            listOf(),
            viewModel,
            resourceProvider,
            baseAdapterListener = object : RestaurantListListener {
                override fun onClickItem(model: RestaurantModel) {
                    startActivity(
                        RestaurantDetailActivity.newIntent(
                            requireContext(),
                            model.toRestaurantEntity()
                        )
                    )
                }
            }
        )
    }

    override fun initViews() = with(binding){
        recyclerView.adapter = adapter
    }

    override fun observeData() = with(viewModel) {
        restaurantListLiveData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    companion object {
        const val RESTAURANT_CATEGORY_KEY = "restaurantCategory"

        const val LOCATION_KEY = "location"

        const val RESTAURANT_KEY = "Restaurant"

        fun newInstance(
            restaurantCategory: RestaurantCategory,
            locationLatLngEntity: LocationLatLngEntity
        ): RestaurantListFragment {
            return RestaurantListFragment().apply {
                arguments = bundleOf(
                    RESTAURANT_CATEGORY_KEY to restaurantCategory,
                    LOCATION_KEY to locationLatLngEntity
                )
            }
        }
    }
}