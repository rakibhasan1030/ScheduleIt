package rakib.hasan.scheduleit.core.graph.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import rakib.hasan.scheduleit.core.graph.root.Graph.APP_LIST_GRAPH
import rakib.hasan.scheduleit.core.graph.root.Graph.HOME
import rakib.hasan.scheduleit.feature.app_list.view.AppListScreen

fun NavGraphBuilder.appListNavGraph(
    navController: NavHostController,
) {
    navigation(
        route = APP_LIST_GRAPH,
        startDestination = AppListRoutes.AppList.route
    ) {
        composable(
            route = AppListRoutes.AppList.route
        ) {
            AppListScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

sealed class AppListRoutes(val route: String) {
    data object AppList : AppListRoutes(route = "app_list")
}
