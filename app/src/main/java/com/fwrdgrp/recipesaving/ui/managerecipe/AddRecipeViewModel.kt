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
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.utils.Constant

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
            validateFields(recipe, instruction, ingredients)
            val recipeId = repo.addRecipeWithDetails(recipe.copy(imageUri = image.toString()))
            addInstructions(instruction, recipeId)
            addIngredients(ingredients, recipeId)
            _finish.emit(Unit)
        } catch (e: Exception) {
            _error.emit(e.message ?: Constant.UNKNOWN)
        }
    }

    suspend fun addInstructions(instruction: List<Instruction>, id: Int) {
        instruction.filter { it.description.isNotBlank() }.forEachIndexed { index, instruction ->
            repo.insertInstruction(
                instruction.copy(id = 0, recipeId = id, stepNumber = index + 1)
            )
        }
    }

    suspend fun addIngredients(ingredients: List<Pair<Ingredient, Pair<Double, String>>>, id: Int) {
        ingredients.filter { it.first.name.isNotBlank() }.forEach { (ingredient, amountUnit) ->
            val exists = repo.getIngredientByName(ingredient.name)
            val ingredientId = exists?.id ?: repo.upsertSingleIngredient(ingredient).toInt()

            repo.insertRecipeIngredient(
                RecipeIngredient(
                    id,
                    ingredientId,
                    amountUnit.first,
                    amountUnit.second
                )
            )
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

        val duplicateIngredient = ingredients
            .mapNotNull { it.first.name.trim().takeIf { name -> name.isNotBlank() }?.lowercase() }
            .groupingBy { it }.eachCount()
            .filter { it.value > 1 }.keys.firstOrNull()
        require(duplicateIngredient == null) { Constant.DUPLICATE_INGREDIENTS }
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