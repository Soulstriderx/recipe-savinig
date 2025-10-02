package com.fwrdgrp.recipesaving.ui.managerecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AddRecipeViewModel(
    private val repo: RecipeRepo
) : ViewModel() {

    private val _finish = MutableSharedFlow<Unit>()
    val finish: SharedFlow<Unit> = _finish
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    suspend fun submitRecipe(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>
    ) {
        try {
            repo.addRecipeWithDetails(recipe, instruction, ingredients)
            _finish.emit(Unit)
        } catch (e: Exception) {
            _error.emit(e.message.toString())
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).repo
                AddRecipeViewModel(repo = myRepository)
            }
        }
    }
}