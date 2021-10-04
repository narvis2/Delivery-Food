package com.example.delivery.presentation.mylocation


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.delivery.R
import com.example.delivery.data.entity.LocationLatLngEntity
import com.example.delivery.data.entity.TmapSearchInfoEntity
import com.example.delivery.databinding.ActivityMyLocationBinding
import com.example.delivery.extensions.toGone
import com.example.delivery.extensions.toVisible
import com.example.delivery.presentation.base.BaseActivity
import com.example.delivery.presentation.main.home.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MyLocationActivity : BaseActivity<MyLocationViewModel, ActivityMyLocationBinding>(),
    OnMapReadyCallback {

    override val viewModel: MyLocationViewModel by viewModel {
        parametersOf(
            intent.getParcelableExtra<TmapSearchInfoEntity>(HomeViewModel.MY_LOCATION_KEY)
        )
    }

    override fun getViewBinding(): ActivityMyLocationBinding {
        return ActivityMyLocationBinding.inflate(layoutInflater)
    }

    private lateinit var map: GoogleMap

    private var isMapInitialized: Boolean = false

    private var isChangeLocation: Boolean = false

    override fun initViews() = with(binding) {
        toolBar.setNavigationOnClickListener {
            finish()
        }
        confirmButton.setOnClickListener {
            viewModel.confirmSelectLocation()
        }
        setUpGoogleMap()
    }

    private fun setUpGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map ?: return
        viewModel.fetchData()
    }

    override fun observeData() = with(viewModel) {
        myLocationStateLiveData.observe(this@MyLocationActivity, Observer {
            when (it) {
                is MyLocationState.Loading -> {
                    handleLoadingState()
                }
                is MyLocationState.Success -> {
                    if (::map.isInitialized) {
                        handleSuccessState(it)
                    }
                }
                is MyLocationState.Confirm -> {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(HomeViewModel.MY_LOCATION_KEY, it.tMapSearchInfoEntity)
                    })
                    finish()
                }
                is MyLocationState.Error -> {
                    Toast.makeText(this@MyLocationActivity, it.messageId, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        })
    }

    private fun handleLoadingState() = with(binding) {
        locationLoading.toVisible()
        locationTitleText.text = getString(R.string.loading)
    }

    private fun handleSuccessState(state: MyLocationState.Success) = with(binding) {
        val mapSearchInfo = state.tMapSearchInfoEntity
        locationLoading.toGone()
        locationTitleText.text = mapSearchInfo.fullAddress
        if (isMapInitialized.not()) {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        mapSearchInfo.locationLatLngEntity.latitude,
                        mapSearchInfo.locationLatLngEntity.longitude
                    ),
                    CAMERA_ZOOM_LEVEL
                )
            )
            // 지도가 멈춰있는지를 판단하는 리스너
            map.setOnCameraIdleListener {
                if (isChangeLocation.not()) {
                    isChangeLocation = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        val cameraLatLng = map.cameraPosition.target
                        viewModel.changeLocationInfo(
                            LocationLatLngEntity(
                                latitude = cameraLatLng.latitude,
                                longitude = cameraLatLng.longitude
                            )
                        )
                        isChangeLocation = false
                    }, 1000)
                }
            }
            isMapInitialized = true
        }
    }

    companion object {
        const val CAMERA_ZOOM_LEVEL = 17f

        fun newIntent(context: Context, mapSearchInfoEntity: TmapSearchInfoEntity) =
            Intent(context, MyLocationActivity::class.java).apply {
                putExtra(HomeViewModel.MY_LOCATION_KEY, mapSearchInfoEntity)
            }
    }
}