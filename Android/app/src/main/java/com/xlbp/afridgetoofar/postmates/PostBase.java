package com.xlbp.afridgetoofar.postmates;

import android.webkit.WebView;

import com.xlbp.afridgetoofar.helpers.Javascript;

public abstract class PostBase
{
    public PostBase(PostActivity postActivity, WebView webView)
    {
        this.postActivity = postActivity;
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


    protected PostActivity postActivity;
    protected WebView webView;
}
