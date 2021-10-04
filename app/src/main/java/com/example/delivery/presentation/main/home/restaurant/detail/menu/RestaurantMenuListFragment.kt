package com.example.delivery.presentation.main.home.restaurant.detail.menu

import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.example.delivery.data.entity.RestaurantFoodEntity
import com.example.delivery.databinding.FragmentRestaurantMenuListBinding
import com.example.delivery.model.restaurant.food.FoodModel
import com.example.delivery.presentation.base.BaseFragment
import com.example.delivery.presentation.main.home.restaurant.detail.RestaurantDetailViewModel
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.BaseAdapterListener
import com.example.delivery.widget.adapter.listener.restaurant.food.FoodMenuListListener
import okhttp3.internal.notify
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class RestaurantMenuListFragment :
    BaseFragment<RestaurantMenuListViewModel, FragmentRestaurantMenuListBinding>() {

    private val restaurantId by lazy {
        arguments?.getLong(RESTAURANT_ID_KEY, -1)
    }

    private val restaurantFoodList by lazy {
        arguments?.getParcelableArrayList<RestaurantFoodEntity>(FOOD_LIST_KEY)!!
    }

    override val viewModel: RestaurantMenuListViewModel by viewModel {
        parametersOf(restaurantId, restaurantFoodList)
    }

    // 상위 ViewModel 주입받기
    private val restaurantDetailViewModel by sharedViewModel<RestaurantDetailViewModel>()

    override fun getViewBinding(): FragmentRestaurantMenuListBinding =
        FragmentRestaurantMenuListBinding.inflate(layoutInflater)

    private val resourceProvider : ResourceProvider by inject()

    private val adapter by lazy {
        ModelRecyclerAdapter<FoodModel, RestaurantMenuListViewModel>(
            listOf(),
            viewModel,
            resourceProvider,
            baseAdapterListener = object : FoodMenuListListener {
                // 장바구니 담기
                override fun onClickItem(model: FoodModel) {
                    viewModel.insertMenuInBasket(model)
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        menuRecyclerView.adapter = adapter
    }

    override fun observeData() = with(viewModel) {
        restaurantFoodListLiveData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.toList())
        })
        menuBasketLiveData.observe(viewLifecycleOwner, Observer {
            showToast("장바구니에 담겼습니다. 메뉴 : ${it.title}")
            restaurantDetailViewModel.notifyFoodMenuListInBasket(it)
        })
        isClearNeedInBasketLiveData.observe(viewLifecycleOwner, Observer { (isClearNeed, afterAction) ->
            if (isClearNeed) {
                restaurantDetailViewModel.notifyClearNeedAlertInBasket(isClearNeed, afterAction)
            }
        })
    }

    companion object {

        const val RESTAURANT_ID_KEY = "restaurantId"

        const val FOOD_LIST_KEY = "foodLIst"

        fun newInstance(restaurantId: Long, foodList: ArrayList<RestaurantFoodEntity>) : RestaurantMenuListFragment {
            val bundle = bundleOf(
                RESTAURANT_ID_KEY to restaurantId,
                FOOD_LIST_KEY to foodList
            )
            return RestaurantMenuListFragment().apply {
                arguments = bundle
            }
        }
    }
}