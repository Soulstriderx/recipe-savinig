package com.fwrdgrp.recipesaving.ui.managerecipe

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.utils.Constant
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

    protected suspend fun addIngredients(
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>,
        id: Int
    ) {
        ingredients.filter { it.first.name.isNotBlank() }.forEach { (ingredient, amountUnit) ->
            val trimmedName = ingredient.name.trim()
            val exists = repo.getIngredientByName(trimmedName)
            val ingredientId =
                exists?.id ?: repo.upsertSingleIngredient(ingredient.copy(name = trimmedName))
                    .toInt()

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

    protected suspend fun addInstructions(instruction: List<Instruction>, id: Int) {
        instruction.filter { it.description.isNotBlank() }.forEachIndexed { index, instruction ->
            repo.insertInstruction(
                instruction.copy(
                    id = 0,
                    recipeId = id,
                    stepNumber = index + 1,
                    description = instruction.description.trim()
                )
            )
        }
    }

    protected fun validateFields(
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

    abstract suspend fun submitRecipe(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>,
        image: Uri?
    )
}