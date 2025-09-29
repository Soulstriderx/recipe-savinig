package com.fwrdgrp.recipesaving.data.models.recipe

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId")]
)
data class Instruction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val stepNumber: Int,
    val description: String
)