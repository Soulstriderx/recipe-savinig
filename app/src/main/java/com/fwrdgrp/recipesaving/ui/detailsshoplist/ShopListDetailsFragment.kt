package com.fwrdgrp.recipesaving.ui.detailsshoplist

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.databinding.FragmentShopListDetailsBinding
import com.fwrdgrp.recipesaving.ui.adapters.ShopListDetailsAdapter

class ShopListDetailsFragment : Fragment() {
    private lateinit var binding: FragmentShopListDetailsBinding
    private lateinit var adapter: ShopListDetailsAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<Store>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopListDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupSpinner()
        binding.run {
//            tvShopListName.text =
//            tvStoreName.text =
            ivBack.setOnClickListener { findNavController().popBackStack() }
            ivAdd.setOnClickListener { toggleFilter(llAddIngredient) }
        }
    }

    fun setupAdapter() {
        adapter = ShopListDetailsAdapter(
            emptyList()
        )
        binding.run {
            binding.rvShopListDetails.adapter = adapter
            binding.rvShopListDetails.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun setupSpinner() {
        binding.run {
            spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item
                //Stores here
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spStoreName.adapter = spinnerAdapter
            spStoreName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = spinnerAdapter.getItem(position) ?: return
                    //Selected thing here
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun toggleFilter(view: View) {
        val isToggled = view.height == 0
        val viewHeight = if(isToggled) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec((view.parent as View).width,
                    View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.measuredHeight
        } else 0

        val animation = ValueAnimator.ofInt(view.height, viewHeight)
        animation.addUpdateListener {
            val value = it.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        animation.duration = 250
        animation.start()
    }
}