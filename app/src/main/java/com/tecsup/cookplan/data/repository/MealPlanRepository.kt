package com.tecsup.cookplan.data.repository

import com.tecsup.cookplan.data.local.MealPlanDao
import com.tecsup.cookplan.data.local.MealPlanEntity
import kotlinx.coroutines.flow.Flow

class MealPlanRepository(private val mealPlanDao: MealPlanDao) {
    fun getPlanByDay(day: String): Flow<List<MealPlanEntity>> = mealPlanDao.getPlanByDay(day)

    // Asigna una receta a una casilla (día + tipo de comida). Si ya había una receta
    // en esa casilla, la reemplaza, garantizando una sola receta por espacio (RF-07).
    suspend fun assignMeal(mealPlan: MealPlanEntity) {
        mealPlanDao.removeMealFromPlan(mealPlan.dayOfWeek, mealPlan.mealType)
        mealPlanDao.insertMealPlan(mealPlan)
    }

    suspend fun removeMealFromPlan(day: String, type: String) = mealPlanDao.removeMealFromPlan(day, type)
}
