package com.duotech.studysmart.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duotech.studysmart.StudySmartApp
import com.duotech.studysmart.repo.TaskRepository
import com.duotech.studysmart.ui.screens.TaskEditScreen
import com.duotech.studysmart.ui.screens.TaskListScreen
import com.duotech.studysmart.ui.screens.AboutScreen


@Composable
fun AppNav() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as StudySmartApp
    val repo = TaskRepository(app.db.dao())

    NavHost(navController = navController, startDestination = "list") {

        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }


        composable("list") {
            TaskListScreen(
                repo = repo,
                onAdd = { navController.navigate("edit?taskId=0") },
                onEdit = { id -> navController.navigate("edit?taskId=$id") },
                onAbout = { navController.navigate("about")}
            )
        }

        composable(
            route = "edit?taskId={taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            TaskEditScreen(
                repo = repo,
                taskId = taskId,
                onDone = { navController.popBackStack() }
            )
        }
    }
}
