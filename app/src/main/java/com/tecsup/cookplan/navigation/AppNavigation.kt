package com.tecsup.cookplan.navigation

import androidx.compose.animation.*
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
            
            val showBottomBar = items.any { it.route == currentDestination?.route }

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
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
            composable(
                route = AppRoutes.Recipes.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                RecipeListScreen(
                    onAddClick = { navController.navigate(AppRoutes.Form.createRoute()) },
                    onRecipeClick = { id -> navController.navigate(AppRoutes.Detail.createRoute(id)) }
                )
            }

            composable(
                route = AppRoutes.Detail.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.LongType }),
                enterTransition = { slideInHorizontally { it } },
                popExitTransition = { slideOutHorizontally { it } }
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
                RecipeDetailScreen(
                    recipeId = recipeId,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(AppRoutes.Form.createRoute(id)) }
                )
            }

            composable(
                route = AppRoutes.Form.route,
                arguments = listOf(navArgument("recipeId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }),
                enterTransition = { slideInVertically { it } },
                popExitTransition = { slideOutVertically { it } }
            ) { backStackEntry ->
                val argId = backStackEntry.arguments?.getLong("recipeId") ?: -1L
                RecipeFormScreen(
                    recipeId = if (argId == -1L) null else argId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoutes.Planner.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                PlannerScreen()
            }

            composable(
                route = AppRoutes.Explore.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                ExploreScreen()
            }
        }
    }
}
