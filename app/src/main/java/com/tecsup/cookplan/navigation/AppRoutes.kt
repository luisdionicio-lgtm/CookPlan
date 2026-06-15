package com.tecsup.cookplan.navigation

sealed class AppRoutes(val route: String) {

    object Recipes : AppRoutes("recipes")

    object Detail : AppRoutes("detail/{recipeId}") {
        fun createRoute(recipeId: Long): String {
            return "detail/$recipeId"
        }
    }

    object Form : AppRoutes("form")

    object Planner : AppRoutes("planner")

    object Explore : AppRoutes("explore")
}
