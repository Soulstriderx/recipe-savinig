package com.fwrdgrp.recipesaving.ui.managerecipe

import androidx.lifecycle.ViewModel
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

abstract class BaseManageRecipeViewModel(
    protected val repo: RecipeRepo
) : ViewModel() {
    protected val _finish = MutableSharedFlow<Unit>()
    val finish: SharedFlow<Unit> = _finish
    protected val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    abstract suspend fun submitRecipe(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>
    )
}