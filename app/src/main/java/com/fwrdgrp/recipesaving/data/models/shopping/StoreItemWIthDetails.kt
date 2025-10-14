package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Embedded
import androidx.room.Relation
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient

data class StoreItemWithDetails(
    @Embedded val storeItem: StoreItem,

    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: Ingredient
)
