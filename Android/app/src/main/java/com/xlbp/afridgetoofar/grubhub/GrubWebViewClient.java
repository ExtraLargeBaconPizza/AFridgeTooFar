package com.xlbp.afridgetoofar.grubhub;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xlbp.afridgetoofar.DeliveryAppBaseActivity;
import com.xlbp.afridgetoofar.helpers.Javascript;

public class GrubWebViewClient extends WebViewClient
{
    public GrubWebViewClient(DeliveryAppBaseActivity activity)
    {
        _activity = activity;
    }

    // need this for debugging, otherwise the webview would just be a blank white screen
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void doUpdateVisitedHistory(WebView webView, String url, boolean isReload)
    {
        super.doUpdateVisitedHistory(webView, url, isReload);

        Log.e("GrubWebViewClient", "doUpdateVisitedHistory " + url);

        Javascript.startDocumentReadyStateCheck(webView,
                complete ->
                {
                    _activity.onDocumentComplete();
                });
    }


    private DeliveryAppBaseActivity _activity;
}