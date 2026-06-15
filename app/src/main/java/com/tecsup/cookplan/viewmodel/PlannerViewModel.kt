package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlannerUiState(
    val selectedDay: String = "Lunes",
    val breakfast: String = "",
    val lunch: String = "",
    val dinner: String = ""
)

class PlannerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    fun selectDay(day: String) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
    }
}
