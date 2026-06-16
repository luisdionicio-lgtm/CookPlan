package com.tecsup.cookplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cookplan.data.local.MealPlanEntity
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.repository.MealPlanRepository
import com.tecsup.cookplan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlannerUiState(
    // día -> (tipo de comida -> asignación)
    val plan: Map<String, Map<String, MealPlanEntity>> = emptyMap(),
    val recipes: List<RecipeEntity> = emptyList()
)

class PlannerViewModel(
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    val uiState: StateFlow<PlannerUiState> = combine(
        mealPlanRepository.getAllPlans(),
        recipeRepository.allRecipes
    ) { plans, recipes ->
        val map = plans.groupBy { it.dayOfWeek }
            .mapValues { (_, list) -> list.associateBy { it.mealType } }
        PlannerUiState(plan = map, recipes = recipes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlannerUiState()
    )

    fun assign(day: String, mealType: String, recipe: RecipeEntity) {
        viewModelScope.launch {
            mealPlanRepository.assignMeal(
                MealPlanEntity(
                    dayOfWeek = day,
                    mealType = mealType,
                    recipeId = recipe.id,
                    recipeName = recipe.name
                )
            )
        }
    }

    fun remove(day: String, mealType: String) {
        viewModelScope.launch {
            mealPlanRepository.removeMealFromPlan(day, mealType)
        }
    }
}

class PlannerViewModelFactory(
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlannerViewModel(mealPlanRepository, recipeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
