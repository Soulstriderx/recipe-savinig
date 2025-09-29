package com.fwrdgrp.recipesaving.data.models.recipe

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fwrdgrp.recipesaving.data.enums.Category

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: List<Category>,
    val estTime: Int?,
    val totalServing: Int?,
    val imageUri: String?
)