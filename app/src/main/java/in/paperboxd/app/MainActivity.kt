package `in`.paperboxd.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import `in`.paperboxd.app.ui.navigation.AppRoot
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appState: AppState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaperBoxdTheme {
                AppRoot(appState = appState)
            }
        }
    }
}
