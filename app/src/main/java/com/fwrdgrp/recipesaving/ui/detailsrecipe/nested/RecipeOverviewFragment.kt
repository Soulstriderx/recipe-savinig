package com.fwrdgrp.recipesaving.ui.detailsrecipe.nested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Category
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.IngredientWithAmount
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeOverviewBinding
import com.fwrdgrp.recipesaving.ui.adapters.DisplayIngredientAdapter
import com.fwrdgrp.recipesaving.ui.detailsrecipe.RecipeDetailsViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class RecipeOverviewFragment : Fragment() {
    private val viewModel: RecipeDetailsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { RecipeDetailsViewModel.Factory }
    )
    private lateinit var binding: FragmentRecipeOverviewBinding
    private val adapter by lazy { DisplayIngredientAdapter(emptyList()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeOverviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        lifecycleScope.launch {
            viewModel.recipeDetails.filterNotNull().filter { it.ingredients.isNotEmpty() }.collect {
                setData(it)
            }
        }
    }

    fun setupAdapter() {
        binding.run {
            rvIngredient.adapter = adapter
            rvIngredient.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    fun setData(details: RecipeWithDetails) {
        binding.run {
            //                ivImage
            tvTitle.text = details.recipe.title
            buildCategories(details.recipe.category)
            tvDesc.text = details.recipe.description
            tvTime.text = details.recipe.estTime.toString()
            tvServing.text = details.recipe.totalServing.toString()

            adapter.applyIngredient(rebuildIngredients(details.ingredients))
        }
    }

    fun rebuildIngredients(
        ingredients: List<IngredientWithAmount>
    ): List<Pair<Ingredient, Pair<Double, String>>> {
        return ingredients.map { Pair(
            it.ingredient,
            Pair(it.amount ?: 0.0, it.unit ?: "")
        ) }.toMutableList()
    }

    fun buildCategories(
        categories: List<Category>
    ) {
        binding.llCategory.removeAllViews()
        for (category in categories) {
            val tvCategory = TextView(requireContext()).apply {
                text = category.name
                setBackgroundResource(R.drawable.box_bg)
            }
//                    tvCategory.setOnClickListener { (category) } navigate to filter thing
            binding.llCategory.addView(tvCategory)
        }
    }
}