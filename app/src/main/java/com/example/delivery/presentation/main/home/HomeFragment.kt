package com.example.delivery.presentation.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.delivery.R
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.TmapSearchInfoEntity
import com.example.delivery.databinding.FragmentHomeBinding
import com.example.delivery.extensions.toGone
import com.example.delivery.extensions.toVisible
import com.example.delivery.presentation.base.BaseFragment
import com.example.delivery.presentation.main.MainActivity
import com.example.delivery.presentation.main.MainTabMenu
import com.example.delivery.presentation.main.home.restaurant.RestaurantCategory
import com.example.delivery.presentation.main.home.restaurant.RestaurantListFragment
import com.example.delivery.presentation.main.home.restaurant.RestaurantOrder
import com.example.delivery.widget.adapter.RestaurantListFragmentPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.example.delivery.presentation.mylocation.MyLocationActivity
import com.example.delivery.presentation.order.OrderMenuListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel: HomeViewModel by viewModel()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    private lateinit var viewPagerAdapter: RestaurantListFragmentPagerAdapter

    private lateinit var locationManager: LocationManager

    private lateinit var myLocationListener: MyLocationListener

    private val firebaseAuth : FirebaseAuth by lazy { Firebase.auth }

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val responsePermissions = permissions.entries.filter {
                (it.key == Manifest.permission.ACCESS_FINE_LOCATION)
                        || (it.key == Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (responsePermissions.filter { it.value == true }.size == locationPermissions.size) {
                // 권한이 있을 때
                setMyLocationListener()
            } else {
                // 권한이 없을 때
                with(binding.locationTitleText) {
                    setText(R.string.please_setup_your_location_permissions)
                    setOnClickListener {
                        // 클릭 하면 다시 권한 팝업 띄움(GPS 가 켜져있을 경우)
                        getMyLocation()
                    }
                    Toast.makeText(requireContext(),
                        context.getString(R.string.can_not_assigned_permission),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val changeLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<TmapSearchInfoEntity>(HomeViewModel.MY_LOCATION_KEY)
                    ?.let { myLocationInfo ->
                        viewModel.loadReverseGeoInformation(myLocationInfo.locationLatLngEntity)
                    }
            }
        }

    override fun initViews() = with(binding) {
        locationTitleText.setOnClickListener {
            viewModel.getMapSearchInfo()?.let { mapInfo ->
                changeLocationLauncher.launch(
                    MyLocationActivity.newIntent(requireContext(), mapInfo)
                )
            }
        }
        orderChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.chip_default -> {
                    chipInitialize.toGone()
                    changeRestaurantOrder(RestaurantOrder.DEFAULT)
                }
                R.id.chip_initialize -> {
                    chipDefault.isChecked = true
                }
                R.id.chip_low_delivery_tip -> {
                    chipInitialize.toVisible()
                    changeRestaurantOrder(RestaurantOrder.LOW_DELIVERY_TIP)
                }
                R.id.chip_fast_delivery -> {
                    chipInitialize.toVisible()
                    changeRestaurantOrder(RestaurantOrder.FAST_DELIVERY)
                }
                R.id.chip_top_rate -> {
                    chipInitialize.toVisible()
                    changeRestaurantOrder(RestaurantOrder.TOP_LATE)
                }
            }
        }
    }

    private fun changeRestaurantOrder(order: RestaurantOrder) {
        viewPagerAdapter.fragmentList.forEach {
            it.viewModel.setRestaurantOrder(order)
        }
    }

    private fun initViewPager(locationLatLngEntity: LocationLatLngEntity) = with(binding) {
        val restaurantCategories = RestaurantCategory.values()
        if (::viewPagerAdapter.isInitialized.not()) {
            orderChipGroup.toVisible()
            val restaurantListFragmentList = restaurantCategories.map {
                RestaurantListFragment.newInstance(it, locationLatLngEntity)
            }
            viewPagerAdapter = RestaurantListFragmentPagerAdapter(
                this@HomeFragment,
                restaurantListFragmentList,
                locationLatLngEntity
            )

            viewPager.adapter = viewPagerAdapter
            viewPager.offscreenPageLimit = restaurantCategories.size

            // TabLayout 과 ViewPager2 연결
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.setText(restaurantCategories[position].categoryNameId)
            }.attach()
        }

        // 현재 위치값이 변경되었을때 변경된 위치값을 바탕으로 데이터 불러오는 로직
        if (locationLatLngEntity != viewPagerAdapter.locationLatLngEntity) {
            viewPagerAdapter.locationLatLngEntity = locationLatLngEntity
            viewPagerAdapter.fragmentList.forEach {
                it.viewModel.setLocationLatLng(locationLatLngEntity)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.checkMyBasket()
    }

    override fun observeData() = with(viewModel) {
        homeStateLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is HomeState.Uninitialized -> {
                    getMyLocation()
                }
                is HomeState.Loading -> {
                    binding.locationLoading.toVisible()
                    binding.locationTitleText.text = getString(R.string.loading)
                }
                is HomeState.Success -> {
                    binding.locationLoading.toGone()
                    binding.locationTitleText.text = it.tMapSearchInfoEntity.fullAddress
                    binding.tabLayout.toVisible()
                    binding.filterScrollView.toVisible()
                    binding.viewPager.toVisible()
                    initViewPager(it.tMapSearchInfoEntity.locationLatLngEntity)
                    if (it.isLocationSame.not()) {
                        Toast.makeText(requireContext(), R.string.please_set_your_current_location, Toast.LENGTH_SHORT).show()
                    }
                }
                is HomeState.Error -> {
                    binding.locationLoading.toGone()
                    binding.locationTitleText.text = getString(R.string.location_not_found)
                    binding.locationTitleText.setOnClickListener {
                        getMyLocation()
                    }
                    showToast("${it.messageId}")
                }
            }
        })

        foodMenuBasketLiveDaa.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                binding.basketButtonContainer.toVisible()
                binding.basketCountTextView.text = getString(R.string.basket_count, it.size)
                binding.basketButton.setOnClickListener {
                    if (firebaseAuth.currentUser == null) {
                        alertLoginNeed {
                            // Bottom Menu Tab 을 이동시킴
                            (requireActivity() as MainActivity).goToTab(MainTabMenu.MY)
                        }
                    } else {
                        startActivity(
                            OrderMenuListActivity.newIntent(requireContext())
                        )
                    }
                }
            } else {
                binding.basketButtonContainer.toGone()
                binding.basketButton.setOnClickListener(null)
            }
        })
    }

    private fun alertLoginNeed(afterAction : () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("로그인이 필요합니다.")
            .setMessage("주문하려면 로그인이 필요합니다. My탭으로 이동하시겠습니까?")
            .setPositiveButton("이동") { dialog, _ ->
                afterAction()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun getMyLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsUnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (isGpsUnabled) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime = 1500L
        val minDistance = 100f // 100미터

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                myLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                myLocationListener
            )
        }
    }

    companion object {

        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        const val TAG = "HomeFragment"

        fun newInstance() = HomeFragment()

    }

    // 위치를 한번만 불러오게끔
    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }
    }

    inner class MyLocationListener : LocationListener {
        @SuppressLint("SetTextI18n")
        override fun onLocationChanged(location: Location) {
            // location 을 LocationEntity 로 바꿔주고 위도 경도 값을 기반으로 위치정보를 가져옴. (ReverseGeoCoding)
            viewModel.loadReverseGeoInformation(
                LocationLatLngEntity(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
            removeLocationListener()
        }
    }
}