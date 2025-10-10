package com.fwrdgrp.recipesaving.data.utils

import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem

class Utils {
    fun generateStoreId(item: RecipeWithDetails, storeItems: List<StoreItem>): Int {
        val storeMap = mutableMapOf<Int, Pair<Int, Double>>()
        item.ingredients.forEach { ing ->
            val filtered = storeItems.filter { ing.ingredient.id == it.ingredientId }

            if ((filtered.isNotEmpty())) {
                filtered.forEach {
                    val (count, total) = storeMap[it.storeId] ?: (0 to 0.0)
                    storeMap[it.storeId] = (count + 1) to (total + it.price)
                }
            }
        }

        val selectedStore = storeMap.maxWithOrNull(
            compareBy<Map.Entry<Int, Pair<Int, Double>>> { it.value.first }
                .thenBy { -it.value.second }
        )

        return selectedStore?.key ?: -1
    }
}