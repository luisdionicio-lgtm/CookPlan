package com.tecsup.cookplan.data.repository

import com.tecsup.cookplan.data.local.RecipeDao
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.firebase.FirestoreSyncRepository
import com.tecsup.cookplan.data.remote.MealApiService
import com.tecsup.cookplan.data.remote.MealSearchQueryTranslator
import com.tecsup.cookplan.data.remote.dto.MealDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val apiService: MealApiService,
    private val firestoreSyncRepository: FirestoreSyncRepository,
    private val syncScope: CoroutineScope
) {
    val allRecipes: Flow<List<RecipeEntity>> = recipeDao.getAllRecipes()

    suspend fun getRecipeById(id: Long): RecipeEntity? = recipeDao.getRecipeById(id)

    suspend fun insertRecipe(recipe: RecipeEntity): Long {
        val id = recipeDao.insertRecipe(recipe)
        syncScope.launch {
            runCatching { firestoreSyncRepository.saveRecipe(recipe.copy(id = id)) }
        }
        return id
    }

    suspend fun updateRecipe(recipe: RecipeEntity): Int {
        val updated = recipeDao.updateRecipe(recipe)
        syncScope.launch {
            runCatching { firestoreSyncRepository.saveRecipe(recipe) }
        }
        return updated
    }

    suspend fun deleteRecipe(recipe: RecipeEntity): Int {
        val deleted = recipeDao.deleteRecipe(recipe)
        syncScope.launch {
            runCatching { firestoreSyncRepository.deleteRecipe(recipe.id) }
        }
        return deleted
    }

    fun searchRecipes(query: String): Flow<List<RecipeEntity>> = recipeDao.searchRecipes(query)

    // Remoto. NO atrapamos la excepción aquí: la dejamos propagar para que el
    // ViewModel pueda distinguir "sin conexión" de "sin resultados" (RF-09 / CP-06).
    // TheMealDB devuelve {"meals": null} cuando no hay coincidencias, no un error.
    suspend fun searchOnline(query: String): List<MealDto> {
        val response = apiService.searchRecipes(MealSearchQueryTranslator.translate(query))
        return response.meals ?: emptyList()
    }

    suspend fun syncLocalRecipesToCloud() {
        firestoreSyncRepository.pushAll(recipeDao.getAllRecipesSnapshot(), emptyList())
    }

    suspend fun pullRecipesFromCloud() {
        val cloudRecipes = firestoreSyncRepository.fetchRecipes()
        if (cloudRecipes.isEmpty()) return
        cloudRecipes.forEach { recipeDao.insertRecipe(it) }
    }
}
