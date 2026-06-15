package com.tecsup.cookplan.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.cookplan.ui.detail.RecipeDetailScreen
import com.tecsup.cookplan.ui.explore.ExploreScreen
import com.tecsup.cookplan.ui.form.RecipeFormScreen
import com.tecsup.cookplan.ui.planner.PlannerScreen
import com.tecsup.cookplan.ui.recipes.RecipeListScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Recipes.route
    ) {

        composable(AppRoutes.Recipes.route) {
            RecipeListScreen()
        }

        composable(
            route = AppRoutes.Detail.route,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val recipeId =
                backStackEntry.arguments?.getLong("recipeId") ?: 0L

            RecipeDetailScreen(recipeId)
        }

        composable(AppRoutes.Form.route) {
            RecipeFormScreen()
        }

        composable(AppRoutes.Planner.route) {
            PlannerScreen()
        }

        composable(AppRoutes.Explore.route) {
            ExploreScreen()
        }
    }
}
