package com.tecsup.cookplan

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
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

    override fun onCreate() {
        super.onCreate()
        // Imprime el token FCM en Logcat para poder enviar push de prueba desde Firebase Console.
        // Busca la etiqueta "FCM_TOKEN" en Logcat y copia el token largo.
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d("FCM_TOKEN", token)
            }
            .addOnFailureListener { e ->
                // Si esto aparece, casi siempre es porque el emulador no tiene Google Play Services.
                Log.e("FCM_TOKEN", "No se pudo obtener el token FCM: ${e.message}", e)
            }
    }
}
