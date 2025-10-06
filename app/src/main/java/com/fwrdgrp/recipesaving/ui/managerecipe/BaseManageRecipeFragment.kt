package com.fwrdgrp.recipesaving.ui.managerecipe

import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Category
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.databinding.FragmentManageRecipeBinding
import com.fwrdgrp.recipesaving.ui.adapters.AddIngredientAdapter
import com.fwrdgrp.recipesaving.ui.adapters.AddInstructionAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseManageRecipeFragment : Fragment() {
    protected lateinit var binding: FragmentManageRecipeBinding
    protected abstract val viewModel: BaseManageRecipeViewModel

    protected lateinit var instructionAdapter: AddInstructionAdapter
    protected lateinit var ingredientAdapter: AddIngredientAdapter
    protected lateinit var categoryAdapter: ArrayAdapter<Category>

    protected val categories = Category.entries.toMutableList()
    protected val selectedCategoryList = mutableListOf<Category>()
    protected var image: Uri? = null
    protected val pickImage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            image = it
            binding.tvAddImage.visibility = View.GONE
            binding.ivImage.setImageURI(image)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageRecipeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInstructionAdapter()
        setupIngredientAdapter()

        binding.ivImage.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }

        setupCategorySpinnerAdapter(categories, selectedCategoryList)
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
            setupSubmitOnClick()
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }


    }

    abstract fun buildRecipe(category: List<Category>): Recipe

    fun ImageView.loadPersistedUri(context: Context, uri: Uri) {
        try {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val drawable = ImageDecoder.decodeDrawable(source)
            this.setImageDrawable(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
            this.setImageResource(R.drawable.image_save_bg) // fallback
        }
    }

    protected fun showError(msg: String) {
        val snackbar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red)).show()
    }

    protected fun setupInstructionAdapter() {
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

    protected fun setupIngredientAdapter() {
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

    protected fun setupCategorySpinnerAdapter(
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

    protected fun categoryChipDelete(
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

    protected fun setupSubmitOnClick() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.submitRecipe(
                buildRecipe(selectedCategoryList),
                instructionAdapter.fetchInstructions(),
                ingredientAdapter.fetchIngredient(),
                image ?: null

            )
        }
    }

}