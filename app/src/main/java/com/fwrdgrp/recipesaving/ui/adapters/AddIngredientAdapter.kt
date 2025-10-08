package com.fwrdgrp.recipesaving.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.enums.Units
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.databinding.LayoutItemAddIngredientBinding

class AddIngredientAdapter(
    val suggestions: List<String>,
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

    private var onDataChanged: (() -> Unit)? = null
    fun setOnDataChangedListener(listener: () -> Unit) {
        onDataChanged = listener
    }

    override fun onBindViewHolder(
        holder: IngredientViewHolder,
        position: Int
    ) {
        val ingredient = ingredients[position]
        holder.binding.run {
            val adapter = setupInternalAdapter(root.context)
            etName.setAdapter(adapter)
            etName.setText(ingredient.first.name)
            etAmount.setText(
                if (ingredient.second.first == 0.0) "" else ingredient.second.first.toString()
            )

            etName.doOnTextChanged { text, start, before, count ->
                val current = ingredients[position]
                ingredients[position] =
                    ingredient.copy(
                        first = Ingredient(name = text.toString()),
                        second = current.second
                    )
                onDataChanged?.invoke()
            }
            etAmount.doOnTextChanged { text, start, before, count ->
                val newAmount = text.toString().toDoubleOrNull() ?: 0.0
                val current = ingredients[position]
                ingredients[position] = ingredient.copy(
                    first = current.first,
                    second = Pair(newAmount, current.second.second)
                )
            }

            //Spinner for Unit input
            setupUnitSpinner(root.context, spUnit, ingredient.second.second, position)
        }
    }

    override fun getItemCount() = ingredients.size

    fun applyIngredient(ingredients: List<Pair<Ingredient, Pair<Double, String>>>) {
        this.ingredients = ingredients.toMutableList()
        notifyDataSetChanged()
    }

    private fun setupInternalAdapter(context: Context): ArrayAdapter<String>{
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            suggestions
        )
        return adapter
    }

    fun addIngredient() {
        if (ingredients.size > 29) return

        val newIngredient = Pair(Ingredient(name = ""), Pair(0.0, ""))
        ingredients.add(newIngredient)
        notifyItemInserted(ingredients.size - 1)
        onDataChanged?.invoke()
    }

    fun fetchIngredient(): List<Pair<Ingredient, Pair<Double, String>>> {
        return ingredients
    }

    inner class IngredientViewHolder(
        val binding: LayoutItemAddIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private fun setupUnitSpinner(
        context: Context,
        spinner: Spinner,
        currentUnit: String,
        position: Int
    ) {
        val unitValues = getUnitDisplayNames()
        val adapter = createUnitSpinnerAdapter(context, unitValues)
        spinner.adapter = adapter

        //Sets default value if value exists
        val selectedIndex = unitValues.indexOf(currentUnit).takeIf { it >= 0 } ?: 0
        spinner.setSelection(selectedIndex)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selectedUnit = unitValues[pos]
                val current = ingredients[position]
                ingredients[position] = current.copy(
                    first = current.first,
                    second = Pair(current.second.first, selectedUnit)
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }
    }

    private fun getUnitDisplayNames(): List<String> = Units.entries.map { it.label }

    private fun createUnitSpinnerAdapter(context: Context, units: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_item, units).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}