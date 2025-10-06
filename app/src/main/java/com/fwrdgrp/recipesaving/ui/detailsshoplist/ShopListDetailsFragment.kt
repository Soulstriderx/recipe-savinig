package com.fwrdgrp.recipesaving.ui.detailsshoplist

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.databinding.FragmentShopListDetailsBinding

class ShopListDetailsFragment : Fragment() {
    private lateinit var binding: FragmentShopListDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopListDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            ivAdd.setOnClickListener { toggleFilter(llAddIngredient) }
        }
    }

    fun toggleFilter(view: View) {
        val isToggled = view.height == 0
        val viewHeight = if(isToggled) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec((view.parent as View).width,
                    View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.measuredHeight
        } else 0

        val animation = ValueAnimator.ofInt(view.height, viewHeight)
        animation.addUpdateListener {
            val value = it.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        animation.duration = 250
        animation.start()
    }
}