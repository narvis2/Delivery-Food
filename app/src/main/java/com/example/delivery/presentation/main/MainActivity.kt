package com.example.delivery.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.delivery.R
import com.example.delivery.databinding.ActivityMainBinding
import com.example.delivery.presentation.main.home.HomeFragment
import com.example.delivery.presentation.main.like.RestaurantLikeListFragment
import com.example.delivery.presentation.main.profile.MyProfileFragment
import com.example.delivery.util.event.MenuChangeEventBus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val menuChangeEventBus : MenuChangeEventBus by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
        initViews()
    }

    private fun initViews() = with(binding) {
        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_home -> {
                    showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
                    true
                }
                R.id.menu_like -> {
                    showFragment(RestaurantLikeListFragment.newInstance(), RestaurantLikeListFragment.TAG)
                    true
                }
                R.id.menu_my -> {
                    showFragment(MyProfileFragment.newInstance(), MyProfileFragment.TAG)
                    true
                }
                else -> false
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            menuChangeEventBus.mainTabMenuFlow.collect {
                goToTab(it)
            }
        }
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction().hide(it).commitAllowingStateLoss()
        }
        findFragment?.let {
            supportFragmentManager.beginTransaction().show(it).commitAllowingStateLoss()
        } ?: kotlin.run {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment, tag)
                .commitAllowingStateLoss()
        }
    }

    // 어디에서든 탭 변경가능하게 하기위한 함수
    fun goToTab(mainTabMenu: MainTabMenu) {
        binding.bottomNavigation.selectedItemId = mainTabMenu.menuId
    }
}

// 바텀 Navigation tab 을 이동시키기 위해 추가
enum class MainTabMenu(@IdRes val menuId: Int) {
    HOME(R.id.menu_home),
    LIKE(R.id.menu_like),
    MY(R.id.menu_my)
}