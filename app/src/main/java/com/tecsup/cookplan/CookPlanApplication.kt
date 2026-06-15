package com.tecsup.cookplan

import android.app.Application
import com.tecsup.cookplan.data.local.AppDatabase
import com.tecsup.cookplan.data.repository.MealPlanRepository
import com.tecsup.cookplan.data.repository.RecipeRepository

class CookPlanApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val recipeRepository by lazy { RecipeRepository(database.recipeDao()) }
    val mealPlanRepository by lazy { MealPlanRepository(database.mealPlanDao()) }
}
