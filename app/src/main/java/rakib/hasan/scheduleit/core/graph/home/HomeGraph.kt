package rakib.hasan.scheduleit.core.graph.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import rakib.hasan.scheduleit.core.graph.details.appListNavGraph
import rakib.hasan.scheduleit.core.graph.root.Graph.APP_LIST_GRAPH
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
                    navController.navigate(APP_LIST_GRAPH)
                },
                onNavigateBack = {

                }
            )
        }

        appListNavGraph(navController = navController)

    }
}

sealed class HomeRoutes(val route: String) {
    data object Home : HomeRoutes(route = "home")
}
