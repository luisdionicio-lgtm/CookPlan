package com.tecsup.cookplan.data.repository

import com.tecsup.cookplan.data.firebase.FirestoreSyncRepository

class CookPlanSyncRepository(
    private val recipeRepository: RecipeRepository,
    private val mealPlanRepository: MealPlanRepository,
    private val firestoreSyncRepository: FirestoreSyncRepository
) {
    suspend fun createUserProfile(email: String?) {
        firestoreSyncRepository.saveCurrentUserProfile(email)
    }

    suspend fun syncUserData() {
        firestoreSyncRepository.saveCurrentUserProfile(null)
        recipeRepository.pullRecipesFromCloud()
        mealPlanRepository.pullPlansFromCloud()
        recipeRepository.syncLocalRecipesToCloud()
        mealPlanRepository.syncLocalPlansToCloud()
    }
}
