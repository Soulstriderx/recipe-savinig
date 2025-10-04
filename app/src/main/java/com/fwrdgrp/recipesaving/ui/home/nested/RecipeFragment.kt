package com.fwrdgrp.recipesaving.ui.home.nested

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.data.enums.Filter
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeBinding
import com.fwrdgrp.recipesaving.ui.adapters.RecipeAdapter
import com.fwrdgrp.recipesaving.ui.home.HomeFragmentDirections
import kotlinx.coroutines.launch
import kotlin.getValue

class RecipeFragment : Fragment() {
    private val viewModel: RecipeViewModel by viewModels {
        RecipeViewModel.Factory
    }
    private lateinit var binding: FragmentRecipeBinding
    private lateinit var adapter: RecipeAdapter
    private lateinit var filterAdapter: ArrayAdapter<Filter>
    private lateinit var ascDescAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupFilter()
        setupFilterAscDesc()
        lifecycleScope.launch {
            viewModel.recipes.collect {
                adapter.applyRecipes(it)
            }
        }

        binding.run {
            tvFilter.setOnClickListener { toggleFilter( llFilter) }
            mbRandomize.setOnClickListener {  } //View model random filter function
        }

    }

    fun setupFilter() {
        binding.run {
            filterAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Filter.entries.toList()
            )
            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spFilter.adapter = filterAdapter
            spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = filterAdapter.getItem(position) ?: return
                    if (selected == Filter.INITIAL) return
                    //View model filter stuff here
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun setupFilterAscDesc() {
        binding.run {
            ascDescAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listOf("Asc", "Desc")
            )
            ascDescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spAscDesc.adapter = ascDescAdapter
            spAscDesc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = ascDescAdapter.getItem(position) ?: return
                    if (selected == "Asc") return
                    //View model filter stuff here
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

    fun setupAdapter() {
        adapter = RecipeAdapter(
            emptyList(),
            {
                val action = HomeFragmentDirections.actionHomeToRecipeDetails(it)
                findNavController().navigate(action)
            },
            {}
        )

        binding.run {
            rvRecipes.adapter = adapter
            rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}