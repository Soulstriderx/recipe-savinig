package com.fwrdgrp.recipesaving.ui.home.nested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeBinding
import com.fwrdgrp.recipesaving.ui.adapters.RecipeAdapter
import kotlinx.coroutines.launch
import kotlin.getValue

class RecipeFragment : Fragment() {
    private val viewModel: RecipeViewModel by viewModels {
        RecipeViewModel.Factory
    }
    private lateinit var binding: FragmentRecipeBinding
    private lateinit var adapter: RecipeAdapter

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
        lifecycleScope.launch {
            viewModel.recipes.collect {
                adapter.applyRecipes(it)
            }
        }
    }

    fun setupAdapter() {
        adapter = RecipeAdapter(
            emptyList(),
            {}, {}
            //Nav to Edit
        )

        binding.run {
            rvRecipes.adapter = adapter
            rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}