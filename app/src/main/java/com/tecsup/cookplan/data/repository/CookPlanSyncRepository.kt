package com.tecsup.cookplan.data.repository

class CookPlanSyncRepository(
    private val recipeRepository: RecipeRepository,
    private val mealPlanRepository: MealPlanRepository
) {
    suspend fun syncUserData() {
        recipeRepository.pullRecipesFromCloud()
        mealPlanRepository.pullPlansFromCloud()
        recipeRepository.syncLocalRecipesToCloud()
        mealPlanRepository.syncLocalPlansToCloud()
    }
}
