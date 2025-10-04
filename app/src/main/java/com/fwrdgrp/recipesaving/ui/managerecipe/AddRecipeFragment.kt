package com.fwrdgrp.recipesaving.ui.managerecipe

import androidx.fragment.app.viewModels
import com.fwrdgrp.recipesaving.data.enums.Category
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import kotlin.getValue

class AddRecipeFragment : BaseManageRecipeFragment() {
    override val viewModel: AddRecipeViewModel by viewModels {
        AddRecipeViewModel.Factory
    }
    override fun buildRecipe(category: List<Category>): Recipe {
        binding.run {
            return Recipe(
                title = etTitle.text.toString(),
                description = etDesc.text.toString(),
                category = category,
                estTime = etTime.text.toString().toInt(),
                totalServing = etServing.text.toString().toInt(),
                //Add when linkable
                imageUri = ""
            )
        }
    }
}