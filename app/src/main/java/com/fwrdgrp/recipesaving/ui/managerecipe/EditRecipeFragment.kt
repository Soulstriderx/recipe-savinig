package com.fwrdgrp.recipesaving.ui.managerecipe

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Category
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import kotlinx.coroutines.launch
import kotlin.getValue


class EditRecipeFragment : BaseManageRecipeFragment() {
    override val viewModel: EditRecipeViewModel by viewModels {
        EditRecipeViewModel.Factory
    }
    private val args: EditRecipeFragmentArgs by navArgs()
    private lateinit var recipeDetails: RecipeWithDetails

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvHeaderManage.text = getString(R.string.edit_recipe)
        lifecycleScope.launch {
            recipeDetails = viewModel.fetchRecipe(args.recipeId)
            setData(recipeDetails)
        }
    }
    fun setData(recipeDetails: RecipeWithDetails) {
        setCategories(recipeDetails)
        binding.run {
            recipeDetails.recipe.imageUri?.let { uriString ->
                val uri = uriString.toUri()
                binding.tvAddImage.visibility = View.GONE
                ivImage.loadPersistedUri(binding.root.context, uri)
            }
            etTitle.setText(recipeDetails.recipe.title)
            etDesc.setText(recipeDetails.recipe.description)
            etTime.setText(recipeDetails.recipe.estTime.toString())
            etServing.setText(recipeDetails.recipe.totalServing.toString())
        }

        instructionAdapter.applyInstruction(recipeDetails.instructions)

        val ingredientList = recipeDetails.ingredients.map {
            Pair(it.ingredient, Pair(it.amount ?: 0.0, it.unit ?: ""))
        }

        ingredientAdapter.applyIngredient(ingredientList)
    }

    fun setCategories(recipeDetails: RecipeWithDetails) {
        selectedCategoryList.apply {
            clear()
            addAll(recipeDetails.recipe.category)
        }

        categories.removeAll(recipeDetails.recipe.category)
        binding.glCategory.removeAllViews()
        for (category in selectedCategoryList) {
            val tvCategory = TextView(requireContext()).apply {
                text = category.name
                setBackgroundResource(R.drawable.box_bg)
            }
            binding.glCategory.addView(tvCategory)
        }
    }

    override fun buildRecipe(category: List<Category>): Recipe {
        binding.run {
            return Recipe(
                id = recipeDetails.recipe.id,
                title = etTitle.text.toString(),
                description = etDesc.text.toString(),
                category = category,
                estTime = etTime.text.toString().toInt(),
                totalServing = etServing.text.toString().toInt(),
                imageUri = ""
            )
        }
    }
}