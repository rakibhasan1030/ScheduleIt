package rakib.hasan.scheduleit.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import rakib.hasan.scheduleit.core.graph.root.RootNavGraph
import rakib.hasan.scheduleit.ui.theme.ScheduleItTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScheduleItTheme {
                RootNavGraph(navController = rememberNavController())
            }
        }
    }

}