package com.xlbp.afridgetoofar.ubereats;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xlbp.afridgetoofar.helpers.Javascript;

public class UberWebViewClient extends WebViewClient
{
    public UberWebViewClient(UberActivity uberActivity)
    {
        _uberActivity = uberActivity;
    }

    @Override
    public void doUpdateVisitedHistory(WebView webView, String url, boolean isReload)
    {
        super.doUpdateVisitedHistory(webView, url, isReload);

        Javascript.startDocumentReadyStateCheck(webView,
                complete ->
                {
                    _uberActivity.onDocumentComplete();
                });
    }


    private UberActivity _uberActivity;
}