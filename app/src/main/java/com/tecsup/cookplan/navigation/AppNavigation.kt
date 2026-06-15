package com.tecsup.cookplan.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
    val items = listOf(
        AppRoutes.Recipes,
        AppRoutes.Planner,
        AppRoutes.Explore
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Solo mostrar BottomBar en las rutas principales
            val showBottomBar = items.any { it.route == currentDestination?.route }

            if (showBottomBar) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Recipes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoutes.Recipes.route) {
                RecipeListScreen(
                    onAddClick = { navController.navigate(AppRoutes.Form.route) },
                    onRecipeClick = { id -> navController.navigate(AppRoutes.Detail.createRoute(id)) }
                )
            }

            composable(
                route = AppRoutes.Detail.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
                RecipeDetailScreen(recipeId, onBack = { navController.popBackStack() })
            }

            composable(AppRoutes.Form.route) {
                RecipeFormScreen(onBack = { navController.popBackStack() })
            }

            composable(AppRoutes.Planner.route) {
                PlannerScreen()
            }

            composable(AppRoutes.Explore.route) {
                ExploreScreen()
            }
        }
    }
}
