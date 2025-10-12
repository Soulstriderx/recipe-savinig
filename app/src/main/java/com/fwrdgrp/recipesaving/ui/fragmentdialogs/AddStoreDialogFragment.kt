package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.databinding.FragmentAddStoreDialogBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AddStoreDialogFragment(
    private val storeName: String = "",
    private val location: String = "",
    private val storeData: (storeName: String, storeLocation: String?) -> Unit
) : DialogFragment() {
    private val viewModel: AddStoreDialogViewModel by viewModels()

    private lateinit var binding: FragmentAddStoreDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddStoreDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.error.collect {
                showError(it)
            }
        }
        binding.run {
            etStoreName.setText(storeName)
            etStoreLocation.setText(location)
            mbCancel.setOnClickListener { dismiss() }
            mbAdd.setOnClickListener { handleAddStore() }
        }
    }

    fun handleAddStore() {
        binding.run {
            val name = etStoreName.text.toString()
            val location = etStoreLocation.text.toString()
            lifecycleScope.launch {
                val isValid = viewModel.validateFields(name, location)
                if (!isValid) return@launch

                storeData(name, location)
                dismiss()
            }
        }
    }

    fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(), R.color.color_error
                )
            ).setTextColor(Color.WHITE).show()
    }
}