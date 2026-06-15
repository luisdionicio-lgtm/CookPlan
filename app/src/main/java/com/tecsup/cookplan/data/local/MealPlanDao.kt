package com.tecsup.cookplan.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans WHERE dayOfWeek = :day")
    fun getPlanByDay(day: String): Flow<List<MealPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlanEntity)

    @Query("DELETE FROM meal_plans WHERE dayOfWeek = :day AND mealType = :type")
    suspend fun removeMealFromPlan(day: String, type: String)
}
