package com.fwrdgrp.recipesaving.ui.managerecipe

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditRecipeViewModel(
    repo: RecipeRepo
) : BaseManageRecipeViewModel(repo) {

    private val _recipeDetails = MutableStateFlow<RecipeWithDetails?>(null)

    suspend fun fetchRecipe(id: Int): RecipeWithDetails {
        val details = repo.getRecipeWithDetails(id).first()
        _recipeDetails.value = details
        return details
    }

    override suspend fun submitRecipe(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>,
        image: Uri?
    ) {
        try {
            validateFields(recipe, instruction, ingredients)

            viewModelScope.launch(Dispatchers.IO) {
                repo.upsertRecipeWithDetails(
                    recipe.copy(imageUri = image.toString()),
                    instruction,
                    ingredients,
                )
                _finish.emit(Unit)
            }
        } catch (e: Exception) {
            _error.emit(e.message ?: Constant.UNKNOWN)
        }
    }

    private fun validateFields(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>,
    ) {
        require(recipe.title.isNotBlank()) { Constant.NO_TITLE }
        require(recipe.description.isNotBlank()) { Constant.NO_DESCRIPTION }
        require(recipe.category.isNotEmpty()) { Constant.NO_CATEGORY }
        require(instruction.any { it.description.isNotBlank() }) { Constant.NO_INSTRUCTIONS }
        require(ingredients.any { it.first.name.isNotBlank() }) { Constant.NO_INGREDIENTS }

        val duplicateIngredient = ingredients.map { it.first.name.trim().lowercase() }
            .groupingBy { it }.eachCount().filter { it.value > 1 }.keys.firstOrNull()
        require(duplicateIngredient == null) { Constant.DUPLICATE_INGREDIENTS }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).RecipeRepository
                EditRecipeViewModel(repo = myRepository)
            }
        }
    }
}