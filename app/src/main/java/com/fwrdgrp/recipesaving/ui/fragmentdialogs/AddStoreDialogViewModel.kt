package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import androidx.lifecycle.ViewModel
import com.fwrdgrp.recipesaving.data.utils.Constant
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AddStoreDialogViewModel : ViewModel() {
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    suspend fun validateFields(name: String, location: String): Boolean {
        return try {
            require(name.isNotBlank()) { Constant.NO_NAME }
            require(location.isNotBlank()) { Constant.NO_LOCATION }
            true
        } catch (e: Exception) {
            _error.emit(e.message ?: Constant.UNKNOWN)
            false
        }
    }
}