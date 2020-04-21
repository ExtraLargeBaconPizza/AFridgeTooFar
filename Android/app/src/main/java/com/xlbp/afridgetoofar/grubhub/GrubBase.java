package com.xlbp.afridgetoofar.grubhub;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.helpers.Javascript;

public abstract class GrubBase
{
    public GrubBase(GrubActivity grubActivity, WebView webView)
    {
        this.grubActivity = grubActivity;
        this.webView = webView;
    }

    protected void onDocumentComplete()
    {
        retrieveHtml();

        _webViewReloadHandler = new Handler();

        _webViewReloadRunnable = () ->
        {
            webView.loadUrl("javascript:window.location.reload(true)");
        };
//        _webViewReloadHandler.postDelayed(_webViewReloadRunnable, 5000);
    }

    protected void retrieveHtml()
    {
        Javascript.getBodyInnerHtml(webView, this::parseHtml);
    }

    protected void stopWebViewReload()
    {
        _webViewReloadHandler.removeCallbacks(_webViewReloadRunnable);
    }

    abstract void parseHtml(String html);


    protected GrubActivity grubActivity;
    protected WebView webView;

    private Handler _webViewReloadHandler;
    private Runnable _webViewReloadRunnable;
}
