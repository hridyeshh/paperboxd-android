package `in`.paperboxd.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
        // Samsung One UI shows a scrollbar thumb on the host AndroidComposeView
        // whenever a Compose list is touched (it reports scroll range + awakens
        // system scrollbars). Disable scrollbars across the view tree to kill it.
        window.decorView.post { disableScrollbars(window.decorView) }
    }

    private fun disableScrollbars(view: View) {
        view.isVerticalScrollBarEnabled = false
        view.isHorizontalScrollBarEnabled = false
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) disableScrollbars(view.getChildAt(i))
        }
    }
}
