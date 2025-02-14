package rakib.hasan.scheduleit.core.graph.permission

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import rakib.hasan.scheduleit.core.graph.home.homeNavGraph
import rakib.hasan.scheduleit.core.graph.root.Graph.HOME
import rakib.hasan.scheduleit.core.graph.root.Graph.PERMISSION_GRAPH
import rakib.hasan.scheduleit.feature.permission.view.PermissionScreen

fun NavGraphBuilder.permissionGraph(
    navController: NavHostController,
) {
    navigation(
        route = PERMISSION_GRAPH,
        startDestination = PermissionRoutes.Permission.route
    ) {
        composable(
            route = PermissionRoutes.Permission.route
        ) {
            PermissionScreen(
                onAllPermissionGranted = {
                    navController.navigate(HOME)
                },
                onNavigateBack = {

                }
            )
        }
        homeNavGraph(navController = navController)
    }
}

sealed class PermissionRoutes(val route: String) {
    data object Permission : PermissionRoutes(route = "Permission")
}
