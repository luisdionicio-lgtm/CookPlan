package com.tecsup.cookplan.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.cookplan.CookPlanApplication
import com.tecsup.cookplan.ui.auth.LoginScreen
import com.tecsup.cookplan.ui.auth.RegisterScreen
import com.tecsup.cookplan.ui.detail.RecipeDetailScreen
import com.tecsup.cookplan.ui.explore.ExploreScreen
import com.tecsup.cookplan.ui.form.RecipeFormScreen
import com.tecsup.cookplan.ui.planner.PlannerScreen
import com.tecsup.cookplan.ui.profile.ProfileScreen
import com.tecsup.cookplan.ui.recipes.RecipeListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Si ya hay sesión activa entra directo a Recetas; si no, a Login (RF-12).
    val startDestination = remember {
        val loggedIn = (context.applicationContext as CookPlanApplication).authRepository.isLoggedIn()
        if (loggedIn) AppRoutes.Recipes.route else AppRoutes.Login.route
    }

    val items = listOf(
        AppRoutes.Recipes,
        AppRoutes.Planner,
        AppRoutes.Explore,
        AppRoutes.Profile
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // Solo mostrar BottomBar en las rutas principales (no en login/registro/detalle/form)
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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoutes.Login.route) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(AppRoutes.Recipes.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                        }
                    },
                    onGoToRegister = { navController.navigate(AppRoutes.Register.route) }
                )
            }

            composable(AppRoutes.Register.route) {
                RegisterScreen(
                    onRegistered = {
                        navController.navigate(AppRoutes.Recipes.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                        }
                    },
                    onGoToLogin = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.Recipes.route) {
                RecipeListScreen(
                    onAddClick = { navController.navigate(AppRoutes.Form.createRoute()) },
                    onRecipeClick = { id -> navController.navigate(AppRoutes.Detail.createRoute(id)) }
                )
            }

            composable(
                route = AppRoutes.Detail.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
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
                })
            ) { backStackEntry ->
                val argId = backStackEntry.arguments?.getLong("recipeId") ?: -1L
                RecipeFormScreen(
                    recipeId = if (argId == -1L) null else argId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.Planner.route) {
                PlannerScreen()
            }

            composable(AppRoutes.Explore.route) {
                ExploreScreen()
            }

            composable(AppRoutes.Profile.route) {
                ProfileScreen(
                    onLoggedOut = {
                        navController.navigate(AppRoutes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
