package com.fwrdgrp.recipesaving.data.utils

import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient
import com.fwrdgrp.recipesaving.database.recipedao.IngredientDao
import com.fwrdgrp.recipesaving.database.recipedao.InstructionDao
import com.fwrdgrp.recipesaving.database.recipedao.RecipeDao

class RecipeRepoUtils(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val instructionDao: InstructionDao
) {
    suspend fun addInstructions(instruction: List<Instruction>, id: Int) {
        instruction.filter { it.description.isNotBlank() }.forEachIndexed { index, instruction ->
            instructionDao.insertInstruction(
                instruction.copy(id = 0, recipeId = id, stepNumber = index + 1)
            )
        }
    }

    suspend fun addIngredients(ingredients: List<Pair<Ingredient, Pair<Double, String>>>, id: Int) {
        ingredients.filter { it.first.name.isNotBlank() }.forEach { (ingredient, amountUnit) ->
            val exists = ingredientDao.getIngredientByName(ingredient.name)
            val ingredientId = exists?.id ?: ingredientDao.upsertIngredient(ingredient).toInt()

            ingredientDao.insertRecipeIngredient(
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
        val exist = ingredientDao.getIngredientByName(ingredientName)
        return exist?.id ?: ingredientDao.upsertIngredient(Ingredient(name = ingredientName)).toInt()
    }
}