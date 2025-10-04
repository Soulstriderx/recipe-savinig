package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.databinding.LayoutItemDisplayInstructionBinding

class DisplayInstructionAdapter(
    var instructions: List<Instruction>
) : RecyclerView.Adapter<DisplayInstructionAdapter.InstructionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InstructionViewHolder {
        val binding = LayoutItemDisplayInstructionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InstructionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: InstructionViewHolder,
        position: Int
    ) {
        val instruction = instructions[position]
        holder.binding.run {
            tvStep.text = instruction.stepNumber.toString()
            tvInstruction.text = instruction.description
        }
    }

    override fun getItemCount() = instructions.size

    fun applyIngredient(instructions: List<Instruction>) {
        this.instructions = instructions
        notifyDataSetChanged()
    }

    inner class InstructionViewHolder(
        val binding: LayoutItemDisplayInstructionBinding
    ) : RecyclerView.ViewHolder(binding.root)
}