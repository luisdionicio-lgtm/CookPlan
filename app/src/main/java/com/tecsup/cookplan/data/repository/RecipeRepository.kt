package com.tecsup.cookplan.data.repository

import com.tecsup.cookplan.data.local.RecipeDao
import com.tecsup.cookplan.data.local.RecipeEntity
import com.tecsup.cookplan.data.remote.MealApiService
import com.tecsup.cookplan.data.remote.MealSearchQueryTranslator
import com.tecsup.cookplan.data.remote.dto.MealDto
import kotlinx.coroutines.flow.Flow

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val apiService: MealApiService
) {
    val allRecipes: Flow<List<RecipeEntity>> = recipeDao.getAllRecipes()

    suspend fun getRecipeById(id: Long): RecipeEntity? = recipeDao.getRecipeById(id)

    suspend fun insertRecipe(recipe: RecipeEntity) = recipeDao.insertRecipe(recipe)

    suspend fun updateRecipe(recipe: RecipeEntity) = recipeDao.updateRecipe(recipe)

    suspend fun deleteRecipe(recipe: RecipeEntity) = recipeDao.deleteRecipe(recipe)

    fun searchRecipes(query: String): Flow<List<RecipeEntity>> = recipeDao.searchRecipes(query)

    // Remoto. NO atrapamos la excepción aquí: la dejamos propagar para que el
    // ViewModel pueda distinguir "sin conexión" de "sin resultados" (RF-09 / CP-06).
    // TheMealDB devuelve {"meals": null} cuando no hay coincidencias, no un error.
    suspend fun searchOnline(query: String): List<MealDto> {
        val response = apiService.searchRecipes(MealSearchQueryTranslator.translate(query))
        return response.meals ?: emptyList()
    }
}
