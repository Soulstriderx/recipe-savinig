package com.fwrdgrp.recipesaving.ui.detailsrecipe

import androidx.lifecycle.ViewModel
import com.fwrdgrp.recipesaving.data.enums.Category
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe

class RecipeDetailsViewModel: ViewModel() {
    val recipe = Recipe(0, "Pizza", "Yummy", listOf<Category>(), 10, 2, "")

}