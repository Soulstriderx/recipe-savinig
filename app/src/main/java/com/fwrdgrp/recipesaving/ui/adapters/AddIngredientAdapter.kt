package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.databinding.LayoutItemAddIngredientBinding

class AddIngredientAdapter(
    var ingredients: MutableList<Pair<Ingredient, Pair<Double, String>>>
) : RecyclerView.Adapter<AddIngredientAdapter.IngredientViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientViewHolder {
        val binding = LayoutItemAddIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: IngredientViewHolder,
        position: Int
    ) {
        val ingredient = ingredients[position]
        holder.binding.run {
            etName.setText(ingredient.first.name)
            etAmount.setText(ingredient.second.first.toString())
            etUnit.setText(ingredient.second.second)

            etName.doOnTextChanged { text, start, before, count ->
                ingredients[position] =
                    ingredient.copy(
                        first = Ingredient(name = text.toString()),
                        second = ingredient.second
                    )
            }
            etAmount.doOnTextChanged { text, start, before, count ->
                val newAmount = text.toString().toDoubleOrNull() ?: 0.0
                ingredients[position] = ingredient.copy(
                    second = Pair(newAmount, ingredient.second.second)
                )
            }
            etUnit.doOnTextChanged { text, start, before, count ->
                ingredients[position] = ingredient.copy(
                    second = Pair(ingredient.second.first, text.toString())
                )
            }
        }
    }

    override fun getItemCount() = ingredients.size

    fun applyIngredient(ingredients: MutableList<Pair<Ingredient, Pair<Double, String>>>) {
        this.ingredients = ingredients
        notifyDataSetChanged()
    }

    fun addIngredient() {
        if (ingredients.size > 29) return

        val newIngredient = Pair(Ingredient(name = ""), Pair(0.0, ""))
        ingredients.add(newIngredient)
        notifyItemInserted(ingredients.size - 1)
    }

    fun fetchIngredient(): List<Pair<Ingredient, Pair<Double, String>>> {
        return ingredients
    }

    inner class IngredientViewHolder(
        val binding: LayoutItemAddIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root)
}