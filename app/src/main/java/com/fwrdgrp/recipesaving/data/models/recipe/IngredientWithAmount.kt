package com.fwrdgrp.recipesaving.data.models.recipe

import androidx.room.Embedded

data class IngredientWithAmount(
    @Embedded val ingredient: Ingredient,
    val amount: Double?,
    val unit: String?
)