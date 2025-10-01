package com.fwrdgrp.recipesaving.ui.manageshoplist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fwrdgrp.recipesaving.R.
 *
import com.fwrdgrp.recipesaving.databinding.FragmentAddShopListBinding

class AddShopListFragment : Fragment() {
    private lateinit var binding: FragmentAddShopListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddShopListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}