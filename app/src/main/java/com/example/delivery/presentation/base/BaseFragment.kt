package com.example.delivery.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

abstract class BaseFragment<VM : BaseViewModel, VB: ViewBinding> : Fragment() {

    abstract val viewModel : VM

    protected lateinit var binding : VB

    abstract fun getViewBinding() : VB

    private lateinit var fetchJob : Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initState()
    }

    open fun initState() {
        // Fragment 에서 상태값을 관리하기 위한 용도
        arguments?.let {
            viewModel.storeState(it)
        }
        initViews()
        fetchJob = viewModel.fetchData()
        observeData()
    }

    open fun initViews() = Unit

    abstract fun observeData()

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        if (fetchJob.isActive) {
            fetchJob.cancel()
        }
        super.onDestroyView()
    }
}