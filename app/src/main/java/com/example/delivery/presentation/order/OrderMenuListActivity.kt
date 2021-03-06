package com.example.delivery.presentation.order

import android.content.Context
import android.content.Intent
import com.example.delivery.databinding.ActivityOrderMenuListBinding
import com.example.delivery.extensions.toGone
import com.example.delivery.extensions.toVisible
import com.example.delivery.model.restaurant.food.FoodModel
import com.example.delivery.presentation.base.BaseActivity
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.order.OrderMenuListListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderMenuListActivity : BaseActivity<OrderMenuListViewModel, ActivityOrderMenuListBinding>() {

    override val viewModel: OrderMenuListViewModel by viewModel()

    override fun getViewBinding(): ActivityOrderMenuListBinding =
        ActivityOrderMenuListBinding.inflate(layoutInflater)

    private val resourceProvider : ResourceProvider by inject()

    private val adapter by lazy {
        ModelRecyclerAdapter<FoodModel, OrderMenuListViewModel>(
            listOf(),
            viewModel,
            resourceProvider,
            baseAdapterListener = object : OrderMenuListListener {
                override fun onRemoveItem(model: FoodModel) {
                    viewModel.removeOrderMenu(model)
                }
            }
        )
    }

    override fun initViews() = with(binding) {

        recyclerView.adapter = adapter

        toolBar.setNavigationOnClickListener {
            finish()
        }
        confirmButton.setOnClickListener {
            viewModel.orderMenu()
        }

        orderClearButton.setOnClickListener {
            viewModel.clearOrderMenu()
        }
    }

    override fun observeData() = with(viewModel) {
        orderMenuStateLiveData.observe(this@OrderMenuListActivity) {
            when(it) {
                is OrderMenuState.Loading -> {
                    handleLoading()
                }
                is OrderMenuState.Success -> {
                    handleSuccess(it)
                }
                is OrderMenuState.Order -> {
                    handleOrder()
                }
                is OrderMenuState.Error -> {
                    handleErrorState(it)
                }
                else -> Unit
            }
        }
    }

    private fun handleLoading() = with(binding) {
        progressBar.toVisible()
    }

    private fun handleSuccess(state: OrderMenuState.Success) = with(binding) {
        progressBar.toGone()
        adapter.submitList(state.restaurantFoodMenuList)
        val menuOrderIsEmpty = state.restaurantFoodMenuList.isNullOrEmpty()
        confirmButton.isEnabled = menuOrderIsEmpty.not()
        if (menuOrderIsEmpty) {
            showToast("?????? ????????? ?????? ????????? ???????????????.")
            finish()
        }
    }

    private fun handleErrorState(state: OrderMenuState.Error) {
        showToast(getString(state.messageId, state.e))
    }

    private fun handleOrder() {
        showToast("??????????????? ????????? ?????????????????????.")
        finish()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, OrderMenuListActivity::class.java)
    }
}