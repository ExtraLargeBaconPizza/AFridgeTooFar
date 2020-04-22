package com.xlbp.afridgetoofar.postmates;

import android.os.Handler;
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
        Javascript.getBodyInnerHtml(webView, this::parseHtml);
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


    protected PostActivity postActivity;
    protected WebView webView;
}
