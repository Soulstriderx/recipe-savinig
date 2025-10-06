package com.fwrdgrp.recipesaving.data.enums

enum class SortOrder(val label: String) {
    ASCENDING("Ascending"),
    DESCENDING("Descending");

    override fun toString(): String = label
}