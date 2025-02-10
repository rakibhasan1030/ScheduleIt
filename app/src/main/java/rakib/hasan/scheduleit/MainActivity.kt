package rakib.hasan.scheduleit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import rakib.hasan.scheduleit.feature.view.AppListScreen
import rakib.hasan.scheduleit.feature.viewmodel.AppViewModel
import rakib.hasan.scheduleit.ui.theme.ScheduleItTheme
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScheduleItTheme {
                AppListScreen(viewModel = viewModel)
            }
        }
    }
}