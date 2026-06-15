package com.tecsup.cookplan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String, // Lunes, Martes...
    val mealType: String, // Desayuno, Almuerzo, Cena
    val recipeId: Long,
    val recipeName: String // Denormalización para mostrar rápido sin JOIN
)
