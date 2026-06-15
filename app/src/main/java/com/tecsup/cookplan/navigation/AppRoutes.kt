package com.tecsup.cookplan.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoutes(val route: String, val title: String = "", val icon: ImageVector? = null) {

    object Recipes : AppRoutes("recipes", "Recetas", Icons.Default.List)

    object Detail : AppRoutes("detail/{recipeId}") {
        fun createRoute(recipeId: Long): String {
            return "detail/$recipeId"
        }
    }

    object Form : AppRoutes("form")

    object Planner : AppRoutes("planner", "Plan", Icons.Default.DateRange)

    object Explore : AppRoutes("explore", "Explorar", Icons.Default.Search)
}
