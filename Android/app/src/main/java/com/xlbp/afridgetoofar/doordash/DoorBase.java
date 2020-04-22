package com.xlbp.afridgetoofar.doordash;

import android.os.Handler;
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
        int random = (int) (Math.random() * 75 + 75);

        new Handler().postDelayed(() ->
        {
            Javascript.getBodyInnerHtml(webView, this::parseHtml);
        }, random);
    }

    abstract void parseHtml(String html);


    protected DoorActivity doorActivity;
    protected WebView webView;
}
