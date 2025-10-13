package com.fwrdgrp.recipesaving.ui.managerecipe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Category
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import kotlin.getValue

class AddRecipeFragment : BaseManageRecipeFragment() {
    override val viewModel: AddRecipeViewModel by viewModels {
        AddRecipeViewModel.Factory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvHeaderManage.text = getString(R.string.add_recipe)
    }
    override fun buildRecipe(category: List<Category>): Recipe {
        binding.run {
            return Recipe(
                title = etTitle.text.toString(),
                description = etDesc.text.toString(),
                category = category,
                estTime = etTime.text.toString().toIntOrNull() ?: 0,
                totalServing = etServing.text.toString().toIntOrNull() ?: 0,
                imageUri = ""
            )
        }
    }
}