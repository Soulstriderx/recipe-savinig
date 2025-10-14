package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Store::class,
            parentColumns = ["id"],
            childColumns = ["storeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("storeId"), Index("ingredientId")]
)
data class StoreItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val storeId: Int,
    val ingredientId: Int,
    val name: String,
    val price: Double,
    val packageAmount: Double,
    val packageUnit: String
)
