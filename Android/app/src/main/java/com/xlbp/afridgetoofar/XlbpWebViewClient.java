package com.xlbp.afridgetoofar;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xlbp.afridgetoofar.helpers.Javascript;

public class XlbpWebViewClient extends WebViewClient
{
    public XlbpWebViewClient(DeliveryAppBaseActivity activity)
    {
        _activity = activity;
    }

    @Override
    public void doUpdateVisitedHistory(WebView webView, String url, boolean isReload)
    {
        super.doUpdateVisitedHistory(webView, url, isReload);

        Javascript.startDocumentReadyStateCheck(webView,
                complete ->
                {
                    _activity.onDocumentComplete();
                });
    }


    private DeliveryAppBaseActivity _activity;
}