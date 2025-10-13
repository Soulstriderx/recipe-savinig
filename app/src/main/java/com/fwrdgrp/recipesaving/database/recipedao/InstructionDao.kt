package com.fwrdgrp.recipesaving.database.recipedao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction

@Dao
interface InstructionDao {
    //Instruction
    @Insert
    suspend fun insertInstruction(instruction: Instruction): Long

    @Query("DELETE FROM Instruction WHERE recipeId = :recipeId")
    suspend fun deleteInstructionsByRecipeId(recipeId: Int)
}