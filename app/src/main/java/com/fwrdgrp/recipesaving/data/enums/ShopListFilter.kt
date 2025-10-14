package com.fwrdgrp.recipesaving.data.enums

enum class ShopListFilter(val label: String) {
    DATE("Date"), //In the order it was made
    ALPHABETICALLY("A-Z"),
    SIZE("Size");

    override fun toString(): String = label
}