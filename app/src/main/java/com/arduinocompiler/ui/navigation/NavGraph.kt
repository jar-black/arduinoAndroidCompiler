package com.arduinocompiler.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arduinocompiler.ui.screens.projects.ProjectListScreen
import com.arduinocompiler.ui.screens.editor.EditorScreen
import com.arduinocompiler.ui.screens.compile.CompileScreen
import com.arduinocompiler.ui.screens.upload.UploadScreen
import com.arduinocompiler.ui.screens.monitor.MonitorScreen
import com.arduinocompiler.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Projects : Screen("projects")
    object Editor : Screen("editor/{projectId}") {
        fun createRoute(projectId: Long) = "editor/$projectId"
    }
    object Compile : Screen("compile/{projectId}") {
        fun createRoute(projectId: Long) = "compile/$projectId"
    }
    object Upload : Screen("upload/{projectId}") {
        fun createRoute(projectId: Long) = "upload/$projectId"
    }
    object Monitor : Screen("monitor")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Projects.route,
        modifier = modifier
    ) {
        composable(Screen.Projects.route) {
            ProjectListScreen(
                onNavigateToEditor = { projectId ->
                    navController.navigate(Screen.Editor.createRoute(projectId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Editor.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toLongOrNull() ?: 0L
            EditorScreen(
                projectId = projectId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCompile = { navController.navigate(Screen.Compile.createRoute(projectId)) }
            )
        }

        composable(Screen.Compile.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toLongOrNull() ?: 0L
            CompileScreen(
                projectId = projectId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUpload = { navController.navigate(Screen.Upload.createRoute(projectId)) }
            )
        }

        composable(Screen.Upload.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toLongOrNull() ?: 0L
            UploadScreen(
                projectId = projectId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMonitor = { navController.navigate(Screen.Monitor.route) }
            )
        }

        composable(Screen.Monitor.route) {
            MonitorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
