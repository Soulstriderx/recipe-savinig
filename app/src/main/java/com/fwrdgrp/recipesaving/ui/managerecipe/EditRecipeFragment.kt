package com.fwrdgrp.recipesaving.ui.managerecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Category
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.databinding.FragmentAddRecipeBinding
import com.fwrdgrp.recipesaving.ui.adapters.AddIngredientAdapter
import com.fwrdgrp.recipesaving.ui.adapters.AddInstructionAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.getValue


class EditRecipeFragment : Fragment() {
    private val viewModel: EditRecipeViewModel by viewModels {
        EditRecipeViewModel.Factory
    }
    private val args: EditRecipeFragmentArgs by navArgs()
    private lateinit var recipeDetails: RecipeWithDetails
    private val categories = Category.entries.toMutableList()
    private val selectedCategoryList = mutableListOf<Category>()

    private lateinit var binding: FragmentAddRecipeBinding
    private lateinit var instructionAdapter: AddInstructionAdapter
    private lateinit var ingredientAdapter: AddIngredientAdapter
    private lateinit var categoryAdapter: ArrayAdapter<Category>


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

        lifecycleScope.launch {
            recipeDetails = viewModel.fetchRecipe(args.recipeId)
            setData(recipeDetails)

        }

        lifecycleScope.launch {
            viewModel.finish.collect {
                findNavController().popBackStack()
            }
        }
        lifecycleScope.launch {
            viewModel.error.collect {
                showError(it)
            }
        }

        binding.mbSubmit.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.submitRecipe(
                    buildRecipe(selectedCategoryList),
                    instructionAdapter.fetchInstructions(),
                    ingredientAdapter.fetchIngredient()
                )
            }
        }
    }

    fun setData(recipeDetails: RecipeWithDetails) {
        setCategories(recipeDetails)
        binding.run {
            etTitle.setText(recipeDetails.recipe.title)
            etDesc.setText(recipeDetails.recipe.description)
            etTime.setText(recipeDetails.recipe.estTime.toString())
            etServing.setText(recipeDetails.recipe.totalServing.toString())
        }

        setupCategorySpinnerAdapter(categories, selectedCategoryList)

        instructionAdapter.applyInstruction(recipeDetails.instructions)

        val ingredientList = recipeDetails.ingredients.map {
            Pair(it.ingredient, Pair(it.amount ?: 0.0, it.unit ?: ""))
        }

        ingredientAdapter.applyIngredient(ingredientList)
    }

    fun setCategories(recipeDetails: RecipeWithDetails) {
        selectedCategoryList.apply {
            clear()
            addAll(recipeDetails.recipe.category)
        }

        categories.removeAll(recipeDetails.recipe.category)
        binding.glCategory.removeAllViews()
        for (category in selectedCategoryList) {
            val tvCategory = TextView(requireContext()).apply {
                text = category.name
                setBackgroundResource(R.drawable.box_bg)
            }
            binding.glCategory.addView(tvCategory)
        }
    }

    fun showError(msg: String) {
        val snackbar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red)).show()
    }

    fun setupInstructionAdapter() {
        instructionAdapter = AddInstructionAdapter(
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
        ingredientAdapter = AddIngredientAdapter(
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

    fun setupCategorySpinnerAdapter(
        categoryList: MutableList<Category>,
        selectedList: MutableList<Category>
    ) {
        binding.run {
            categoryAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item, categoryList
            )
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spCategory.adapter = categoryAdapter

            spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val selected = categoryAdapter.getItem(position) ?: return
                    if (selected == Category.INITIAL) return
                    selectedList.add(selected)
                    categoryList.remove(selected)
                    val tvCategory = categoryChipDelete(categoryList, selectedList, selected)
                    glCategory.addView(tvCategory)
                    categoryAdapter.notifyDataSetChanged()
                    spCategory.setSelection(0)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun categoryChipDelete(
        categoryList: MutableList<Category>,
        selectedList: MutableList<Category>,
        item: Category,
    ): TextView {
        return binding.run {
            TextView(requireContext()).apply {
                text = item.name
                setBackgroundResource(R.drawable.box_bg)

                setOnClickListener {
                    selectedList.remove(item)
                    categoryList.add(item)
                    categoryAdapter.notifyDataSetChanged()
                    glCategory.removeView(this)
                }
            }
        }
    }

    fun buildRecipe(category: List<Category>): Recipe {
        binding.run {
            return Recipe(
                id = recipeDetails.recipe.id,
                title = etTitle.text.toString(),
                description = etDesc.text.toString(),
                category = category,
                estTime = etTime.text.toString().toInt(),
                totalServing = etServing.text.toString().toInt(),
                //Add when linkable
                imageUri = ""
            )
        }
    }


}