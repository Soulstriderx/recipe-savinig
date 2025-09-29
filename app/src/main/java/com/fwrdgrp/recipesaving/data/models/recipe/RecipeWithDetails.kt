package com.fwrdgrp.recipesaving.data.models.recipe

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RecipeWithDetails(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val instructions: List<Instruction>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        associateBy = Junction(RecipeIngredient::class)
    )
    val ingredients: List<Ingredient>
)