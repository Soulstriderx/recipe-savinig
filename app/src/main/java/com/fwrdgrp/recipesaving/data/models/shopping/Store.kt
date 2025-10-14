package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Store(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val location: String?
) {
    override fun toString(): String {
        return name
    }
}
