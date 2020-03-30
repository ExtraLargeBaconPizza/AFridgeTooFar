package com.elbp.afridgetoofar.ubereats;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class UberWebViewClient extends WebViewClient
{
    public UberWebViewClient(UberActivity uberEatsActivity)
    {
        _uberEatsActivity = uberEatsActivity;
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload)
    {
        super.doUpdateVisitedHistory(view, url, isReload);

        // Knowing that the document is in an interactive state may be useful at some point
//        view.evaluateJavascript("javascript:var readyStateCheckInterval = setInterval(function() {if (document.readyState === 'interactive') {clearInterval(readyStateCheckInterval); return('document interactive');}}, 10);",
//                                interactive ->
//                                {
//                                });

        // We need to get determine that the document loading is complete through this javascript
        // function because the WebViews onPageFinished callback will not trigger when we've
        // navigated to a new page through a button click.
        view.evaluateJavascript("javascript:var readyStateCheckInterval = setInterval(function() {if (document.readyState === 'complete') {clearInterval(readyStateCheckInterval); return('document complete');}}, 10);",
                complete ->
                {
                    _uberEatsActivity.onDocumentComplete();
                });
    }


    // TODO @jim - just retrieve MainActivity when needed. Don't need to store it
    private UberActivity _uberEatsActivity;
}