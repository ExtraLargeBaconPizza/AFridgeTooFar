package com.xlbp.afridgetoofar.grubhub;

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
    }

    protected void retrieveHtml()
    {
        Javascript.getBodyInnerHtml(webView, this::parseHtml);
    }

    abstract void parseHtml(String html);


    protected GrubActivity grubActivity;
    protected WebView webView;
}
