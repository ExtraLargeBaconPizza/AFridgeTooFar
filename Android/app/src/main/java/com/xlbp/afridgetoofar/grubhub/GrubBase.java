package com.xlbp.afridgetoofar.grubhub;

import android.os.Handler;
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
        Javascript.getRootInnerHtml(webView, this::parseHtml);
    }

    protected void retrieveHtml()
    {
        int random = (int) (Math.random() * 75 + 75);

        new Handler().postDelayed(() ->
        {
            Javascript.getBodyInnerHtml(webView, this::parseHtml);
        }, random);
    }


    abstract void parseHtml(String html);


    protected GrubActivity grubActivity;
    protected WebView webView;

    private Handler _webViewReloadHandler;
    private Runnable _webViewReloadRunnable;
}
