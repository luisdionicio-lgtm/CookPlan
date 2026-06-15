package com.tecsup.cookplan.data.repository

import com.tecsup.cookplan.data.local.RecipeDao
import com.tecsup.cookplan.data.local.RecipeEntity
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {
    val allRecipes: Flow<List<RecipeEntity>> = recipeDao.getAllRecipes()

    suspend fun getRecipeById(id: Long): RecipeEntity? = recipeDao.getRecipeById(id)

    suspend fun insertRecipe(recipe: RecipeEntity) = recipeDao.insertRecipe(recipe)

    suspend fun updateRecipe(recipe: RecipeEntity) = recipeDao.updateRecipe(recipe)

    suspend fun deleteRecipe(recipe: RecipeEntity) = recipeDao.deleteRecipe(recipe)

    fun searchRecipes(query: String): Flow<List<RecipeEntity>> = recipeDao.searchRecipes(query)
}
