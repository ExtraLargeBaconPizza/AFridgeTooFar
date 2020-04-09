package com.xlbp.afridgetoofar.ubereats;

import android.webkit.WebView;

import com.xlbp.afridgetoofar.helpers.Javascript;

public abstract class UberBase
{
    public UberBase(UberActivity uberActivity, WebView webView)
    {
        this.uberActivity = uberActivity;
        this.webView = webView;
    }

    abstract void parseHtml(String html);

    protected void onDocumentComplete()
    {
        retrieveHtml();
    }

    protected void retrieveHtml()
    {
        Javascript.getRootInnerHtml(webView, this::parseHtml);
    }

    protected UberActivity uberActivity;
    protected WebView webView;
}
