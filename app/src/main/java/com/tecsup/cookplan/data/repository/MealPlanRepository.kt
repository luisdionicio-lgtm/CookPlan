package com.tecsup.cookplan.data.repository

import com.tecsup.cookplan.data.local.MealPlanDao
import com.tecsup.cookplan.data.local.MealPlanEntity
import kotlinx.coroutines.flow.Flow

class MealPlanRepository(private val mealPlanDao: MealPlanDao) {
    fun getPlanByDay(day: String): Flow<List<MealPlanEntity>> = mealPlanDao.getPlanByDay(day)

    suspend fun addMealToPlan(mealPlan: MealPlanEntity) = mealPlanDao.insertMealPlan(mealPlan)

    suspend fun removeMealFromPlan(day: String, type: String) = mealPlanDao.removeMealFromPlan(day, type)
}
