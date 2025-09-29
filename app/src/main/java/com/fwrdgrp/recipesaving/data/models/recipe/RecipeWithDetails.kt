package com.fwrdgrp.recipesaving.data.models.recipe

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class RecipeWithDetails(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val instructions: List<Instruction>,

    @Ignore
    val ingredients: List<IngredientWithAmount> = emptyList()
)