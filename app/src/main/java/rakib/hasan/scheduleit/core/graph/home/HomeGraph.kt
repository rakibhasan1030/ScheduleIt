package rakib.hasan.scheduleit.core.graph.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import rakib.hasan.scheduleit.core.graph.details.ScheduleRoutes
import rakib.hasan.scheduleit.core.graph.details.scheduleNavGraph
import rakib.hasan.scheduleit.core.graph.root.Graph.SCHEDULE_GRAPH
import rakib.hasan.scheduleit.core.graph.root.Graph.HOME
import rakib.hasan.scheduleit.feature.home.view.HomeScreen


fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
) {
    navigation(
        route = HOME,
        startDestination = HomeRoutes.Home.route
    ) {
        composable(
            route = HomeRoutes.Home.route
        ) {
            HomeScreen(
                onAddClicked = {
                    // Navigate WITHOUT arguments
                    navController.navigate(ScheduleRoutes.Schedule.noArgRoute)
                },
                onEditClicked = { scheduledApp ->
                    // Navigate WITH arguments
                    navController.navigate(
                        ScheduleRoutes.Schedule.routeWithArgs.replace(
                            "{packageName}",
                            scheduledApp.packageName
                        )
                    )
                },
                onNavigateBack = {
                    // Handle back navigation if needed
                }
            )
        }

        // Include the scheduleNavGraph
        scheduleNavGraph(navController = navController)
    }
}

sealed class HomeRoutes(val route: String) {
    data object Home : HomeRoutes(route = "home")
}
