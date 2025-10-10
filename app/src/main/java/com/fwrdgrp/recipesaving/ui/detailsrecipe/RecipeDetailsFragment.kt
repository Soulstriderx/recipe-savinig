package com.fwrdgrp.recipesaving.ui.detailsrecipe

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeDetailsBinding
import com.fwrdgrp.recipesaving.ui.adapters.TabsAdapter
import com.fwrdgrp.recipesaving.ui.detailsrecipe.nested.RecipeInstructionsFragment
import com.fwrdgrp.recipesaving.ui.detailsrecipe.nested.RecipeOverviewFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class RecipeDetailsFragment : Fragment() {
    private val viewModel: RecipeDetailsViewModel by viewModels {
        RecipeDetailsViewModel.Factory
    }
    private lateinit var binding: FragmentRecipeDetailsBinding
    private val args: RecipeDetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.fetchRecipe(args.recipeId)
        }
        lifecycleScope.launch {
            viewModel.error.collect {
                showError(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shoppingListId.filterNotNull().collect {
                    val action =
                        RecipeDetailsFragmentDirections.actionRecipeDetailsToShopListDetails(it)
                    findNavController().navigate(action)
                }
            }
        }
        setOnClickListeners()
        attachTabsAdapter()
        lifecycleScope.launch {
            viewModel.recipeDetails.collect { details ->
                details?.let {
                    binding.ivFavorite.setImageResource(
                        if (it.recipe.favorite) R.drawable.ic_star_filled
                        else R.drawable.ic_star_outline
                    )
                }
            }
        }
    }

    fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(), android.R.color.holo_red_light
                )
            ).setTextColor(Color.WHITE).show()

    }

    fun setOnClickListeners() {
        binding.run {
            ivBack.setOnClickListener { findNavController().popBackStack() }
            ivFavorite.setOnClickListener { viewModel.toggleFavorite() } //Favorite function
            ivGenerateShopList.setOnClickListener { viewModel.addShopList() } //Navigate to shop and add ingredients to list
            mbEdit.setOnClickListener {
                findNavController().navigate(
                    RecipeDetailsFragmentDirections.actionRecipeDetailsToEditRecipe(args.recipeId)
                )
            }
            mbDelete.setOnClickListener { deleteDialogCreation().show() } //Delete function and popBackStack
        }
    }

    fun attachTabsAdapter() {
        val adapter = TabsAdapter(
            fragments = listOf(RecipeOverviewFragment(), RecipeInstructionsFragment()),
            fragment = this
        )
        binding.vpTabs.adapter = adapter
        TabLayoutMediator(binding.tlTabs, binding.vpTabs) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.overview)
                else -> tab.text = getString(R.string.instructions)
            }
        }.attach()
    }

    fun deleteDialogCreation(): Dialog {
        return Dialog(requireContext()).apply {
            setContentView(R.layout.layout_dialog_confirmation)
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            findViewById<MaterialButton>(R.id.mbCancel).setOnClickListener { dismiss() }
            findViewById<MaterialButton>(R.id.mbConfirm).setOnClickListener {
                viewModel.deleteRecipe()
                findNavController().popBackStack()
                dismiss()
            }
        }
    }
}