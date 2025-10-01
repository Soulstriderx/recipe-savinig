package com.fwrdgrp.recipesaving.ui.managerecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Category
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.databinding.FragmentAddRecipeBinding
import com.fwrdgrp.recipesaving.ui.adapters.IngredientAdapter
import com.fwrdgrp.recipesaving.ui.adapters.InstructionAdapter
import kotlin.collections.take

class AddRecipeFragment : Fragment() {
    private lateinit var binding: FragmentAddRecipeBinding
    private lateinit var instructionAdapter: InstructionAdapter
    private lateinit var ingredientAdapter: IngredientAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentAddRecipeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInstructionAdapter()
        setupIngredientAdapter()

        val categories = Category.entries.toMutableList()
        val selectedCategoryList = mutableListOf<Category>()

        binding.run {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spCategory.adapter = adapter

            spCategory.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected = adapter.getItem(position)!!
                    selectedCategoryList.add(selected)
                    categories.remove(selected)
                    adapter.notifyDataSetChanged()

                    val tvCategory = TextView(requireContext()).apply {
                        text = selected.name
                        setBackgroundResource(R.drawable.box_bg)
                    }

                    glCategory.addView(tvCategory)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            tvCategory.setOnClickListener {
                val selected = Category.entries.find { it.name == tvCategory.text }!!
                selectedCategoryList.remove(selected)
                categories.add(selected)
                adapter.notifyDataSetChanged()
                glCategory.removeView(tvCategory)
            }
        }
    }

    fun setupInstructionAdapter() {
        instructionAdapter = InstructionAdapter(
            mutableListOf(
                Instruction(
                    recipeId = 0,
                    stepNumber = 1,
                    description = ""
                )
            )
        )

        binding.rvAddInstruction.adapter = instructionAdapter
        binding.rvAddInstruction.layoutManager = LinearLayoutManager(requireContext())
        binding.ivAddInstruction.setOnClickListener {
            instructionAdapter.addInstructions()
        }
    }

    fun setupIngredientAdapter() {
        ingredientAdapter = IngredientAdapter(
            mutableListOf(
                Pair(
                    Ingredient(name = ""),
                    Pair(0.0, "")
                )
            )
        )

        binding.rvAddIngredient.adapter = ingredientAdapter
        binding.rvAddIngredient.layoutManager = LinearLayoutManager(requireContext())
        binding.ivAddIngredient.setOnClickListener {
            ingredientAdapter.addIngredient()
        }
    }
}