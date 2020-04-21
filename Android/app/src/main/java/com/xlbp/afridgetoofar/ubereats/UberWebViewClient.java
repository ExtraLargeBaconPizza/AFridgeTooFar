package com.xlbp.afridgetoofar.ubereats;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xlbp.afridgetoofar.DeliveryAppBaseActivity;
import com.xlbp.afridgetoofar.helpers.Javascript;

public class UberWebViewClient extends WebViewClient
{
    public UberWebViewClient(DeliveryAppBaseActivity activity)
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