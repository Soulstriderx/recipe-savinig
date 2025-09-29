package com.fwrdgrp.recipesaving.data.models.recipe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val instructions: String,
    val category: String?,
    val imageUri: String?
)