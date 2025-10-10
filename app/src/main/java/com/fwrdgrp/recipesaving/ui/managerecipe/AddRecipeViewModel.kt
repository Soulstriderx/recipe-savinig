package com.fwrdgrp.recipesaving.ui.managerecipe

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo

class AddRecipeViewModel(
    repo: RecipeRepo
) : BaseManageRecipeViewModel(repo) {

    override suspend fun submitRecipe(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>,
        image: Uri?
    ) {
        try {
            val duplicateIngredient = ingredients.map { it.first.name.trim().lowercase() }
                .groupingBy { it }.eachCount().filter { it.value > 1 }.keys.firstOrNull()
            if (duplicateIngredient != null) throw Exception("Please remove duplicate ingredients.")

            repo.addRecipeWithDetails(recipe.copy(imageUri = image.toString()), instruction, ingredients)
            _finish.emit(Unit)
        } catch (e: Exception) {
            _error.emit(e.message.toString())
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).RecipeRepository
                AddRecipeViewModel(repo = myRepository)
            }
        }
    }
}