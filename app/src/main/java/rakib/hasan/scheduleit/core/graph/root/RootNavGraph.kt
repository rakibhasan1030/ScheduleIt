package rakib.hasan.scheduleit.core.graph.root

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import rakib.hasan.scheduleit.core.graph.home.homeNavGraph
import rakib.hasan.scheduleit.core.graph.permission.permissionGraph
import rakib.hasan.scheduleit.core.graph.root.Graph.HOME
import rakib.hasan.scheduleit.core.graph.root.Graph.PERMISSION_GRAPH
import rakib.hasan.scheduleit.core.graph.root.Graph.ROOT
import rakib.hasan.scheduleit.feature.permission.viewmodel.PermissionViewModel

@Composable
fun RootNavGraph(
    viewModel: PermissionViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val initialDestination = if (viewModel.areAllPermissionsGranted()) HOME else PERMISSION_GRAPH

    NavHost(
        navController = navController, route = ROOT,
        startDestination = initialDestination
    ) {
        permissionGraph(navController = navController)
        homeNavGraph(navController = navController)
    }

}

object Graph {

    // parent graphs
    const val ROOT = "root_graph"
    const val PERMISSION_GRAPH = "permission_graph"
    const val HOME = "home_graph"

    // nested graphs
    const val SCHEDULE_GRAPH = "app_list_graph"
}


