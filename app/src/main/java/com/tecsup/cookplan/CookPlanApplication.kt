package com.tecsup.cookplan

import android.app.Application
import com.tecsup.cookplan.data.firebase.AuthRepository
import com.tecsup.cookplan.data.local.AppDatabase
import com.tecsup.cookplan.data.remote.MealApiService
import com.tecsup.cookplan.data.repository.MealPlanRepository
import com.tecsup.cookplan.data.repository.RecipeRepository

class CookPlanApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val apiService by lazy { MealApiService.create() }

    val recipeRepository by lazy { RecipeRepository(database.recipeDao(), apiService) }
    val mealPlanRepository by lazy { MealPlanRepository(database.mealPlanDao()) }
    val authRepository by lazy { AuthRepository() }
}
