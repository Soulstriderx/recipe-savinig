package com.fwrdgrp.recipesaving.ui.detailsrecipe.nested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.IngredientWithAmount
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeOverviewBinding
import com.fwrdgrp.recipesaving.ui.adapters.DisplayIngredientAdapter
import com.fwrdgrp.recipesaving.ui.detailsrecipe.RecipeDetailsViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class RecipeOverviewFragment : Fragment() {
    private val viewModel: RecipeDetailsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { RecipeDetailsViewModel.Factory }
    )
    private lateinit var binding: FragmentRecipeOverviewBinding
    private lateinit var adapter: DisplayIngredientAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeOverviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.recipeDetails.filterNotNull().collect {
                binding.run {
                    //                ivImage
                    tvTitle.text = it.recipe.title
                    for (category in it.recipe.category) {
                        val tvCategory = TextView(requireContext()).apply {
                            text = category.name
                            setBackgroundResource(R.drawable.box_bg)
                        }
//                    tvCategory.setOnClickListener { (category) } navigate to filter thing
                        llCategory.addView(tvCategory)
                    }
                    tvDesc.text = it.recipe.description
                    tvTime.text = it.recipe.estTime.toString()
                    tvServing.text = it.recipe.totalServing.toString()

                    setupAdapter(rebuildIngredients(it.ingredients))
                    it.ingredients
                }
            }
        }
    }

    fun setupAdapter(ingredients: List<Pair<Ingredient, Pair<Double, String>>>) {
        adapter = DisplayIngredientAdapter(ingredients)
        binding.run {
            rvIngredient.adapter = adapter
            rvIngredient.layoutManager = LinearLayoutManager(requireContext())
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
}