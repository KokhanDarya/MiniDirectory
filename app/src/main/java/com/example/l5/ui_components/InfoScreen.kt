package com.example.l5.ui_components

import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.l5.utils.ListItem
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel

class HtmlViewModel : ViewModel() {
    var webViewState: Bundle? = null
}

@Composable
fun InfoScreen(item: ListItem) {
    val isDarkTheme = isSystemInDarkTheme()
    val htmlViewModel: HtmlViewModel = viewModel()

    Card(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color.Black else Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) Color.Black else Color.White)
        ) {
            if (LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AssetImage(
                        imageName = item.imageName,
                        contentDescription = item.title,
                        modifier = Modifier
                            .width(250.dp)
                            .height(200.dp)
                            .align(Alignment.CenterVertically),
                    )
                    HtmlLoader(htmlName = item.htmlName, htmlViewModel = htmlViewModel)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AssetImage(
                        imageName = item.imageName,
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                    HtmlLoader(htmlName = item.htmlName, htmlViewModel = htmlViewModel)
                }
            }
        }
    }
}




@Composable
fun HtmlLoader(htmlName: String, htmlViewModel: HtmlViewModel = viewModel()) {
    var backEnabled by remember { mutableStateOf(false) }
    var webView: WebView? = null

    val context = LocalContext.current
    val assetManager = context.assets
    val inputStream = assetManager.open("html/$htmlName")
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    val htmlString = String(buffer)
    val styledHtml = """
        <html>
        <head>
            <style>
                body {
                    background-color: ${if (isSystemInDarkTheme()) "black" else "white"};
                    color: ${if (isSystemInDarkTheme()) "white" else "black"};
                }
            </style>
        </head>
        <body>
            ${htmlString}
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        backEnabled = view.canGoBack()
                    }
                }
                settings.javaScriptEnabled = true

                if (htmlViewModel.webViewState != null) {
                    restoreState(htmlViewModel.webViewState!!)
                } else {
                    loadData(styledHtml, "text/html", "utf-8")
                }
                webView = this
            }
        },
        update = {
            webView = it
        }
    )

    BackHandler(enabled = backEnabled) {
        webView?.goBack()
    }

    DisposableEffect(Unit) {
        onDispose {
            htmlViewModel.webViewState = Bundle()
            webView?.saveState(htmlViewModel.webViewState!!)
        }
    }
}

