package com.xlbp.afridgetoofar.ubereats;

import android.os.Handler;
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
        Javascript.getRootInnerHtml(webView, this::parseHtml);
    }

    protected void retrieveHtml()
    {
        int random = (int) (Math.random() * 75 + 75);

        new Handler().postDelayed(() ->
        {
            Javascript.getRootInnerHtml(webView, this::parseHtml);
        }, random);
    }

    abstract void parseHtml(String html);


    protected UberActivity uberActivity;
    protected WebView webView;
}
