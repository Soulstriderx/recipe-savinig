package com.fwrdgrp.recipesaving.ui.managerecipe

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseManageRecipeViewModel(
    protected val repo: RecipeRepo
) : ViewModel() {
    protected val _finish = MutableSharedFlow<Unit>()
    val finish: SharedFlow<Unit> = _finish
    protected val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    protected val _ingredientList = MutableStateFlow<List<Ingredient>?>(null)
    val ingredientList: StateFlow<List<Ingredient>?> = _ingredientList

    init {
        getIngredients()
    }

    fun getIngredients() {
        viewModelScope.launch {
            repo.getAllIngredients().collect { ingredients ->
                _ingredientList.update { ingredients }
            }
        }
    }

    abstract suspend fun submitRecipe(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>,
        image: Uri?
    )
}