package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Embedded
import androidx.room.Relation
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient

data class ShoppingListItemWithIngredient(
    @Embedded val shoppingListItem: ShoppingListItem,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: Ingredient,
    @Relation(
        entity = StoreItem::class,
        parentColumn = "ingredientId",
        entityColumn = "ingredientId"
    )
    val storeItems: List<StoreItem>
)