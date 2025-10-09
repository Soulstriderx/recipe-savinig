package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.databinding.LayoutItemAddInstructionBinding
import kotlin.toString

class AddInstructionAdapter(
    var instructions: MutableList<Instruction>
): RecyclerView.Adapter<AddInstructionAdapter.InstructionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InstructionViewHolder {
        val binding = LayoutItemAddInstructionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InstructionViewHolder(binding)
    }

    private var onDataChanged: (() -> Unit)? = null
    fun setOnDataChangedListener(listener:() -> Unit) {
        onDataChanged = listener
    }

    override fun onBindViewHolder(
        holder: InstructionViewHolder,
        position: Int
    ) {
        val instruction = instructions[position]
        holder.binding.etInstruction.setText(instruction.description)
        holder.binding.tvAddInstructionStep.setText(" ${instruction.stepNumber.toString()}.")

        holder.binding.etInstruction.doOnTextChanged { text, start, before, count ->
            instructions[position] =
                instruction.copy(description = text.toString(), stepNumber = position + 1)
            onDataChanged?.invoke()
        }
    }

    override fun getItemCount() = instructions.size

    fun applyInstruction(instructions: List<Instruction>) {
        this.instructions = instructions.toMutableList()
        notifyDataSetChanged()
    }

    fun addInstructions() {
        if (instructions.size > 19) return
        val newInstruction = Instruction(
            recipeId = 0,
            stepNumber = instructions.size + 1,
            description = ""
        )
        instructions.add(newInstruction)
        notifyItemInserted(instructions.size - 1)
    }

    fun fetchInstructions(): List<Instruction> {
        return instructions
    }

    inner class InstructionViewHolder(
        val binding: LayoutItemAddInstructionBinding
    ) : RecyclerView.ViewHolder(binding.root)
}