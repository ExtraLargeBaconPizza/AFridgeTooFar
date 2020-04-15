package com.xlbp.afridgetoofar.doordash;

import android.webkit.WebView;

import com.xlbp.afridgetoofar.helpers.Javascript;

public abstract class DoorBase
{
    public DoorBase(DoorActivity uberActivity, WebView webView)
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

    
    protected DoorActivity uberActivity;
    protected WebView webView;
}
