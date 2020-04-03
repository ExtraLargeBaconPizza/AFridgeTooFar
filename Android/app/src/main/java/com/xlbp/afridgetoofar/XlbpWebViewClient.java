package com.xlbp.afridgetoofar;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xlbp.afridgetoofar.Javascript;
import com.xlbp.afridgetoofar.ubereats.UberActivity;

// TODO - can probably make this a more generic class and use for all apps
public class XlbpWebViewClient extends WebViewClient
{
    public XlbpWebViewClient(UberActivity uberActivity)
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