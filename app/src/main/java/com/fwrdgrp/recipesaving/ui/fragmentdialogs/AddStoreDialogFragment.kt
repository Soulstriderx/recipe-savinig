package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.databinding.FragmentAddStoreDialogBinding

class AddStoreDialogFragment : DialogFragment() {
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
    }
}