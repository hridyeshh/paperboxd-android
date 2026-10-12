package `in`.paperboxd.app.ui.screens.bookdetail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import `in`.paperboxd.app.ui.components.HL

/**
 * Public-domain reader: Project Gutenberg's own HTML page for the book in a
 * WebView, under a close bar. Navigation is pinned to gutenberg.org and
 * JavaScript stays off. Full-bleed: the dock only renders on tab routes.
 *
 * iOS twin: `GutenbergReaderView.swift`.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GutenbergReaderScreen(title: String, url: String, onClose: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    // Back walks the in-page history first (chapter anchors), then closes.
    BackHandler { if (webView?.canGoBack() == true) webView?.goBack() else onClose() }

    Column(modifier = Modifier.fillMaxSize().background(HL.Paper).statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .drawBehind {
                    drawRect(HL.Ink, topLeft = Offset(0f, size.height - 2.dp.toPx()), size = Size(size.width, 2.dp.toPx()))
                }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "Close", tint = HL.Ink, modifier = Modifier.size(20.dp))
            }
            Text(
                title.uppercase(),
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp,
                color = HL.Ink,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            if (loading) {
                CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = false
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                val host = request.url.host ?: return true
                                return host != "www.gutenberg.org" && host != "gutenberg.org"
                            }

                            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) { loading = true }
                            override fun onPageFinished(view: WebView, url: String?) { loading = false }
                        }
                        loadUrl(url)
                        webView = this
                    }
                }
            )
        }
    }
}
