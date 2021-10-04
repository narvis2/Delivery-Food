package com.example.delivery.presentation.main.profile

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.delivery.R
import com.example.delivery.databinding.FragmentMyProfileBinding
import com.example.delivery.extensions.load
import com.example.delivery.extensions.toGone
import com.example.delivery.extensions.toVisible
import com.example.delivery.model.restaurant.order.OrderModel
import com.example.delivery.presentation.base.BaseFragment
import com.example.delivery.presentation.review.AddRestaurantReviewActivity
import com.example.delivery.util.provider.ResourceProvider
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.BaseAdapterListener
import com.example.delivery.widget.adapter.listener.order.OrderListListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyProfileFragment : BaseFragment<MyProfileViewModel, FragmentMyProfileBinding>() {

    override val viewModel: MyProfileViewModel by viewModel()

    override fun getViewBinding(): FragmentMyProfileBinding =
        FragmentMyProfileBinding.inflate(layoutInflater)

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(requireActivity(), gso)
    }

    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    task.getResult(ApiException::class.java)?.let { account ->
                        viewModel.saveToken(account.idToken ?: throw Exception())
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }

    private val resourceProvider : ResourceProvider by inject()

    private val adapter by lazy {
        ModelRecyclerAdapter<OrderModel, MyProfileViewModel>(
            listOf(),
            viewModel,
            resourceProvider,
            baseAdapterListener = object : OrderListListener {
                override fun writeRestaurantReview(orderId: String, restaurantTitle: String) {
                    startActivity(
                        AddRestaurantReviewActivity.newIntent(requireContext(), orderId, restaurantTitle)
                    )
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            signInGoogle()
        }
        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            viewModel.signOut()
        }
        orderRecyclerView.adapter = adapter
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        loginLauncher.launch(signInIntent)
    }

    override fun observeData() = with(viewModel) {
        myProfileStateLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is MyProfileState.Loading -> handleLoadingState()
                is MyProfileState.Success -> handleSuccessState(it)
                is MyProfileState.Login -> handleLoginState(it)
                is MyProfileState.Error -> handleErrorState(it)
                else -> Unit
            }
        }
    }

    private fun handleLoadingState() {
        binding.loginRequiredGroup.toGone()
        binding.profileProgressBar.toVisible()
    }

    private fun handleSuccessState(state: MyProfileState.Success) = with(binding) {
        profileProgressBar.toGone()
        when(state) {
            is MyProfileState.Success.Registered -> {
                handleRegisteredState(state)
            }
            is MyProfileState.Success.NotRegistered -> {
                profileGroup.toGone()
                loginRequiredGroup.toVisible()
            }
        }
    }

    private fun handleRegisteredState(state : MyProfileState.Success.Registered) = with(binding) {
        profileGroup.toVisible()
        loginRequiredGroup.toGone()
        profileImageView.load(state.profileImageUri.toString(), 60f)
        userNameTextView.text = state.userName
//        showToast(state.orderList.toString())
        adapter.submitList(state.orderList)

    }

    private fun handleLoginState(state: MyProfileState.Login) {
        binding.profileProgressBar.toVisible()
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공, 로그인한 사용자 정보로 UI 업데이트
                    val user = firebaseAuth.currentUser
                    viewModel.setUserInfo(user)
                } else {
                    // 로그인에 실패하면 사용자에게 메시지를 표시합니다.
                    firebaseAuth.signOut()
                    viewModel.setUserInfo(null)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchData()
        binding.orderRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun handleErrorState(state: MyProfileState.Error) {
        showToast(getString(R.string.request_error, state.e))
    }

    companion object {

        fun newInstance() = MyProfileFragment()

        const val TAG = "MyProfileFragment"

    }
}