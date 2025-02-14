package rakib.hasan.scheduleit.core.graph.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import rakib.hasan.scheduleit.core.graph.permission.permissionGraph
import rakib.hasan.scheduleit.core.graph.root.Graph.PERMISSION_GRAPH
import rakib.hasan.scheduleit.core.graph.root.Graph.ROOT

@Composable
fun RootNavGraph(
    navController: NavHostController,
) {

    NavHost(
        navController = navController, route = ROOT,
        startDestination = PERMISSION_GRAPH
    ) {
        permissionGraph(navController = navController)
    }

}

object Graph {

    // parent graphs
    const val ROOT = "root_graph"
    const val PERMISSION_GRAPH = "permission_graph"
    const val HOME = "home_graph"

    // nested graphs
    const val APP_LIST_GRAPH = "app_list_graph"
}


