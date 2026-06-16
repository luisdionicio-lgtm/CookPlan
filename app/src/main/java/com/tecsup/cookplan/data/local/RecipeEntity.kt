package com.tecsup.cookplan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String? = null,
    val ingredients: String,
    val instructions: String,
    val timeMinutes: Int? = null,
    val servings: Int? = null,
    val isExternal: Boolean = false, // Para distinguir de las importadas de API
    val imageUrl: String? = null
)
