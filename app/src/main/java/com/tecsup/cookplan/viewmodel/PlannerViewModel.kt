package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cookplan.data.repository.MealPlanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlannerUiState(
    val selectedDay: String = "Lunes",
    val breakfast: String = "",
    val lunch: String = "",
    val dinner: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
class PlannerViewModel(private val repository: MealPlanRepository) : ViewModel() {
    private val _selectedDay = MutableStateFlow("Lunes")
    
    val uiState: StateFlow<PlannerUiState> = _selectedDay
        .flatMapLatest { day -> repository.getPlanByDay(day) }
        .map { meals ->
            PlannerUiState(
                selectedDay = _selectedDay.value,
                breakfast = meals.find { it.mealType == "Desayuno" }?.recipeName ?: "",
                lunch = meals.find { it.mealType == "Almuerzo" }?.recipeName ?: "",
                dinner = meals.find { it.mealType == "Cena" }?.recipeName ?: ""
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlannerUiState()
        )

    fun selectDay(day: String) {
        _selectedDay.value = day
    }
}

class PlannerViewModelFactory(private val repository: MealPlanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlannerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
