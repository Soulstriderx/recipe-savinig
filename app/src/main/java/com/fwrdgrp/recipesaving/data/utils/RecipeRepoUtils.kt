package com.fwrdgrp.recipesaving.data.utils

import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient
import com.fwrdgrp.recipesaving.database.RecipeDao

class RecipeRepoUtils(private val recipeDao: RecipeDao) {
    suspend fun addInstructions(instruction: List<Instruction>, id: Int) {
        instruction.filter { it.description.isNotBlank() }.forEachIndexed { index, instruction -> recipeDao.insertInstruction(
            instruction.copy(id = 0, recipeId = id, stepNumber = index + 1)
        ) }
    }

    suspend fun addIngredients(ingredients: List<Pair<Ingredient , Pair<Double, String>>>, id: Int) {
        ingredients.filter { it.first.name.isNotBlank() }.forEach { (ingredient, amountUnit) ->
            val exists = recipeDao.getIngredientByName(ingredient.name)
            val ingredientId = exists?.id ?: recipeDao.upsertIngredient(ingredient).toInt()

            recipeDao.insertRecipeIngredient(
                RecipeIngredient(
                    id,
                    ingredientId,
                    amountUnit.first,
                    amountUnit.second
                )
            )
        }
    }

    suspend fun addSingleIngredients(ingredientName: String): Int {
        val exist = recipeDao.getIngredientByName(ingredientName)
        return exist?.id ?: recipeDao.upsertIngredient(Ingredient(name = ingredientName)).toInt()
    }
}