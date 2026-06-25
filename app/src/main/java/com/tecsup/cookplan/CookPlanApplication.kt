package com.tecsup.cookplan

import android.app.Application
import com.tecsup.cookplan.data.firebase.AuthRepository
import com.tecsup.cookplan.data.firebase.FirestoreSyncRepository
import com.tecsup.cookplan.data.local.AppDatabase
import com.tecsup.cookplan.data.remote.MealApiService
import com.tecsup.cookplan.data.repository.CookPlanSyncRepository
import com.tecsup.cookplan.data.repository.MealPlanRepository
import com.tecsup.cookplan.data.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CookPlanApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val apiService by lazy { MealApiService.create() }
    private val firestoreSyncRepository by lazy { FirestoreSyncRepository() }
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val recipeRepository by lazy { RecipeRepository(database.recipeDao(), apiService, firestoreSyncRepository, applicationScope) }
    val mealPlanRepository by lazy { MealPlanRepository(database.mealPlanDao(), firestoreSyncRepository, applicationScope) }
    val syncRepository by lazy { CookPlanSyncRepository(recipeRepository, mealPlanRepository, firestoreSyncRepository) }
    val authRepository by lazy { AuthRepository() }
}
