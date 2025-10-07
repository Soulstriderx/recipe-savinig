package com.fwrdgrp.recipesaving.data.utils

import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.database.ShoppingDao

class ShoppingRepoUtils(
    private val shoppingDao: ShoppingDao
) {
    suspend fun addSingleIngredients(ingredientName: String): Int {
        val exist = shoppingDao.getIngredientByName(ingredientName)
        return exist?.id ?: shoppingDao.upsertIngredient(Ingredient(name = ingredientName)).toInt()
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