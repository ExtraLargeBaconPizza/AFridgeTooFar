package com.xlbp.afridgetoofar.doordash;

import android.webkit.WebView;

import com.xlbp.afridgetoofar.helpers.Javascript;

public abstract class DoorBase
{
    public DoorBase(DoorActivity doorActivity, WebView webView)
    {
        this.doorActivity = doorActivity;
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

    protected void retrieveRootInnerHtml()
    {
        Javascript.getRootInnerHtml(webView, this::parseHtml);
    }

    abstract void parseHtml(String html);


    protected DoorActivity doorActivity;
    protected WebView webView;
}
