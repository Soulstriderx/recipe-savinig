package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ShoppingList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId"), Index("ingredientId")]
)
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listId: Int,
    val ingredientId: Int,
    val amountNeeded: Double,
    val neededUnit: String,
    val bought: Boolean = false
)
