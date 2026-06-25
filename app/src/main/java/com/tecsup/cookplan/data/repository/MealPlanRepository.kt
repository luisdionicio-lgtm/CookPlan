package com.tecsup.cookplan.data.repository

import com.tecsup.cookplan.data.local.MealPlanDao
import com.tecsup.cookplan.data.local.MealPlanEntity
import com.tecsup.cookplan.data.firebase.FirestoreSyncRepository
import kotlinx.coroutines.flow.Flow

class MealPlanRepository(
    private val mealPlanDao: MealPlanDao,
    private val firestoreSyncRepository: FirestoreSyncRepository
) {
    fun getAllPlans(): Flow<List<MealPlanEntity>> = mealPlanDao.getAllPlans()

    fun getPlanByDay(day: String): Flow<List<MealPlanEntity>> = mealPlanDao.getPlanByDay(day)

    // Asigna una receta a una casilla (día + tipo de comida). Si ya había una receta
    // en esa casilla, la reemplaza, garantizando una sola receta por espacio (RF-07).
    suspend fun assignMeal(mealPlan: MealPlanEntity) {
        mealPlanDao.removeMealFromPlan(mealPlan.dayOfWeek, mealPlan.mealType)
        firestoreSyncRepository.deleteMealSlot(mealPlan.dayOfWeek, mealPlan.mealType)
        val id = mealPlanDao.insertMealPlan(mealPlan)
        firestoreSyncRepository.saveMealPlan(mealPlan.copy(id = id))
    }

    suspend fun removeMealFromPlan(day: String, type: String): Int {
        val removed = mealPlanDao.removeMealFromPlan(day, type)
        firestoreSyncRepository.deleteMealSlot(day, type)
        return removed
    }

    suspend fun syncLocalPlansToCloud() {
        firestoreSyncRepository.pushAll(emptyList(), mealPlanDao.getAllPlansSnapshot())
    }

    suspend fun pullPlansFromCloud() {
        val cloudPlans = firestoreSyncRepository.fetchPlans()
        if (cloudPlans.isEmpty()) return
        cloudPlans.forEach { mealPlanDao.insertMealPlan(it) }
    }
}
