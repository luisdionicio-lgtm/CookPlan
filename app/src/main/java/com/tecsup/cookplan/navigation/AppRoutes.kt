package com.tecsup.cookplan.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoutes(val route: String, val title: String = "", val icon: ImageVector? = null) {

    object Recipes : AppRoutes("recipes", "Recetas", Icons.Default.Home)

    object Detail : AppRoutes("detail/{recipeId}") {
        fun createRoute(recipeId: Long): String {
            return "detail/$recipeId"
        }
    }

    object Form : AppRoutes("form?recipeId={recipeId}") {
        // Sin id -> crear una receta nueva; con id -> editar la existente (RF-04).
        fun createRoute(recipeId: Long? = null): String {
            return if (recipeId == null) "form" else "form?recipeId=$recipeId"
        }
    }

    object Planner : AppRoutes("planner", "Planificador", Icons.Default.CalendarMonth)

    object Explore : AppRoutes("explore", "Explorar", Icons.Default.Explore)
}
