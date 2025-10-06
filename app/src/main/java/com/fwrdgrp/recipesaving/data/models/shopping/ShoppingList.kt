package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [
        ForeignKey(entity = Store::class, parentColumns = ["id"], childColumns = ["storeId"])
    ]
)
data class ShoppingList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dateCreated: Long,
    val storeId: Int
)
