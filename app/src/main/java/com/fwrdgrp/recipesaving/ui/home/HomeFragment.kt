package com.fwrdgrp.recipesaving.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.databinding.FragmentHomeBinding
import com.fwrdgrp.recipesaving.ui.adapters.TabsAdapter
import com.fwrdgrp.recipesaving.ui.fragmentdialogs.AddShopListDialogFragment
import com.fwrdgrp.recipesaving.ui.home.nested.RecipeFragment
import com.fwrdgrp.recipesaving.ui.home.nested.ShopListFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import kotlin.getValue

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory
    }
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mbAdd.setOnClickListener {
            if (binding.vpTabs.currentItem == 0) {
                findNavController().navigate(HomeFragmentDirections.actionHomeToAddRecipe())
            } else {
                val dialog = AddShopListDialogFragment { listName, store ->
                    lifecycleScope.launch {
                        viewModel.addShopList(listName, store.id)
                    }
                }
                dialog.show(parentFragmentManager, "AddShopListDialog")
            }
        }
        val adapter =
            TabsAdapter(fragments = listOf(RecipeFragment(), ShopListFragment()), fragment = this)
        binding.vpTabs.adapter = adapter
        TabLayoutMediator(binding.tlTabs, binding.vpTabs) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.recipes)
                else -> tab.text = getString(R.string.shop_list)
            }
        }.attach()
        binding.vpTabs.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                addButtonText(position)
            }
        })
    }

//    fun showAddShopListDialog(): Dialog {
//        return
//        }

        fun addButtonText(pos: Int) {
            binding.mbAdd.text = if (pos == 0) getString(R.string.add_recipes)
            else getString(R.string.add_shop_list)
        }
    }