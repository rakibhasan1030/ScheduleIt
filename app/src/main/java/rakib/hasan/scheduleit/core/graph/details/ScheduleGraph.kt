package rakib.hasan.scheduleit.core.graph.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import rakib.hasan.scheduleit.core.graph.root.Graph.SCHEDULE_GRAPH
import rakib.hasan.scheduleit.feature.schedule.presentation.view.ScheduleScreen

fun NavGraphBuilder.scheduleNavGraph(
    navController: NavHostController,
) {
    navigation(
        route = SCHEDULE_GRAPH,
        startDestination = ScheduleRoutes.Schedule.noArgRoute // Start without arguments
    ) {
        // Case 1: Navigate without arguments
        composable(route = ScheduleRoutes.Schedule.noArgRoute) {
            ScheduleScreen(
                packageName = "", // No argument case
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Case 2: Navigate with arguments
        composable(
            route = ScheduleRoutes.Schedule.routeWithArgs,
            arguments = listOf(
                navArgument("packageName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            ScheduleScreen(
                packageName = packageName,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}


sealed class ScheduleRoutes(val route: String) {
    data object Schedule : ScheduleRoutes(route = "schedule")

    // Route with argument
    val routeWithArgs: String get() = "$route/{packageName}"

    // Route without argument
    val noArgRoute: String get() = route
}
