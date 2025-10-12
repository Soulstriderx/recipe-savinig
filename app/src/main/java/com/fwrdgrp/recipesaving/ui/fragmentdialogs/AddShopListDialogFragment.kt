package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.databinding.FragmentAddShopListDialogBinding
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class AddShopListDialogFragment(
    private val shopListData: (listName: String, store: Store) -> Unit
) : DialogFragment() {
    private val viewModel: AddShopListDialogViewModel by viewModels {
        AddShopListDialogViewModel.Factory
    }
    private lateinit var binding: FragmentAddShopListDialogBinding
    private lateinit var spinnerAdapter: ArrayAdapter<Store>

    private var selectedStore: Store? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddShopListDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.stores.filterNotNull().collect {
                setupSpinner(it)
            }
        }

        binding.run {
            mbCancel.setOnClickListener { dismiss() }
            mbAdd.setOnClickListener {
                val name = etListName.text.toString()
                val store = selectedStore

                if (name.isNotEmpty() && store != null) {
                    shopListData(name, store)
                    dismiss()
                }
            }
        }
    }

    fun setupSpinner(stores: List<Store>) {
        binding.run {
            val validStores = if (stores.isNotEmpty()) stores else {
                spStoreName.isEnabled = false
                mbAdd.isEnabled = false
                listOf(Store(name = "Please create a store", location = "") )
            }
            spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
                validStores)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spStoreName.adapter = spinnerAdapter
        }
    }
}