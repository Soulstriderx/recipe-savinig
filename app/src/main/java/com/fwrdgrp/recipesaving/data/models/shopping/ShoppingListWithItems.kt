package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Embedded
import androidx.room.Relation

data class ShoppingListWithItems(
    @Embedded val shoppingList: ShoppingList,

    @Relation(
        entity = ShoppingListItem::class,
        parentColumn = "id",
        entityColumn = "listId"
    )
    val items: List<ShoppingListItemWithDetails>
)
