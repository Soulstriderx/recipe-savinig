package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Embedded
import androidx.room.Relation

data class ShoppingListWithStoreAndItems(
    @Embedded val shoppingList: ShoppingList,
    @Relation(
        parentColumn = "storeId",
        entityColumn = "id"
    )
    val store: Store,
    @Relation(
        entity = ShoppingListItem::class,
        parentColumn = "id",
        entityColumn = "listId"
    )
    val items: List<ShoppingListItemWithIngredient>
)
