package dev.entao.pages

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.*
import android.widget.LinearLayout
import dev.entao.page.LinearPage
import dev.entao.views.Params
import dev.entao.views.widthFill
import dev.entao.views.flexY
import dev.entao.views.needId

open class WebPage : LinearPage() {

    lateinit var webView: WebView
    var rootUrl: String? = null
    var title: String? = null


    open fun onLoadWebUrl(view: WebView, url: String) {

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)
        titleBar.title(title ?: "")
        webView = WebView(context).needId()
        contentView.addView(webView, Params.linear.widthFill.flexY)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            databaseEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadsImagesAutomatically = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
        }


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                onLoadWebUrl(view, url)
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                showLoading()
            }

            override fun onPageFinished(view: WebView, url: String) {
                hideLoading()
            }

        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                return super.onJsAlert(view, url, message, result)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        if (rootUrl != null) {
            webView.loadUrl(rootUrl!!)
        }
    }


    override fun onBackPressed(): Boolean {
        return if (webView.canGoBack()) {
            webView.goBack()
            true
        } else {
            super.onBackPressed()
        }
    }

    fun loadAsset(assetPath: String) {
        webView.loadUrl("file:///android_asset/$assetPath")
    }


}
