package com.fwrdgrp.recipesaving.data.models.shopping

import androidx.room.Embedded
import androidx.room.Relation

data class StoreWithItemsDetails(
    @Embedded val store: Store,

    @Relation(
        entity = StoreItem::class,
        parentColumn = "id",
        entityColumn = "storeId"
    )
    val items: List<StoreItemWithDetails>
)
