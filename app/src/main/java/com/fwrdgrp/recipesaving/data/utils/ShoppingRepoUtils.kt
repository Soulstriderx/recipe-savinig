package com.fwrdgrp.recipesaving.data.utils

import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.database.recipedao.IngredientDao

class ShoppingRepoUtils(
    private val ingredientDao: IngredientDao
) {
    suspend fun addSingleIngredients(ingredientName: String): Int {
        val exist = ingredientDao.getIngredientByName(ingredientName)
        return exist?.id ?: ingredientDao.upsertIngredient(Ingredient(name = ingredientName)).toInt()
    }

    fun buildShoppingListItem(
        listId: Int,
        ingredientId: Int,
        amount: Double,
        unit: String
    ): ShoppingListItem {
        return ShoppingListItem(
            listId = listId,
            ingredientId = ingredientId,
            amountNeeded = amount,
            neededUnit = unit
        )
    }

}