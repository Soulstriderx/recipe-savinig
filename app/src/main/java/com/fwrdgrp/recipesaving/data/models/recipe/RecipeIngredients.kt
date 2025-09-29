package com.fwrdgrp.recipesaving.data.models.recipe

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["recipeId", "ingredientId"],
    foreignKeys = [
        ForeignKey(entity = Recipe::class, parentColumns = ["id"], childColumns = ["recipeId"]),
        ForeignKey(entity = Ingredient::class, parentColumns = ["id"], childColumns = ["ingredientId"])
    ]
)
data class RecipeIngredient(
    val recipeId: Int,
    val ingredientId: Int,
    val amount: Double?,  // numeric value
    val unit: String?     // e.g., "cups", "grams"
)