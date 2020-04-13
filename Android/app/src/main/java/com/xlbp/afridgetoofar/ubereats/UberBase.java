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

    protected void onDocumentComplete()
    {
        retrieveHtml();
    }

    protected void retrieveHtml()
    {
        Javascript.getRootInnerHtml(webView, this::parseHtml);
    }

    abstract void parseHtml(String html);

    
    protected UberActivity uberActivity;
    protected WebView webView;
}
