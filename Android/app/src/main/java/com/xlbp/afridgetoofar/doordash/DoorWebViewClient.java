package com.xlbp.afridgetoofar.doordash;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xlbp.afridgetoofar.DeliveryAppBaseActivity;

public class DoorWebViewClient extends WebViewClient
{
    public DoorWebViewClient(DeliveryAppBaseActivity activity)
    {
        _activity = activity;
    }

    @Override
    public void onLoadResource(WebView view, String url)
    {
        super.onLoadResource(view, url);

        Log.e("DoorWebViewClient", "onLoadResource" + url);
//        view.evaluateJavascript("document.querySelector('meta[name=\"viewport\"]').setAttribute('content', 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));", null);
    }

    // need this for debugging, otherwise the webview would just be a blank white screen
//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url)
//    {
//        view.loadUrl(url);
//        return true;
//    }
//
    @Override
    public void onPageFinished(WebView webView, String url)
    {
        super.onPageFinished(webView, url);
        Log.e("DoorWebViewClient", "onPageFinished" + url);
    }
//
//    @Override
//    public void doUpdateVisitedHistory(WebView webView, String url, boolean isReload)
//    {
//        super.doUpdateVisitedHistory(webView, url, isReload);
////        Log.e("XlbpWebViewClient", "doUpdateVisitedHistory " + url);
////        Log.e("XlbpWebViewClient", "isReload " + isReload);
////
////        if (!url.contains("blank"))
////        {
////            Log.e("XlbpWebViewClient", "doUpdateVisitedHistory " + url);
////            _activity.onDocumentComplete();
////        }
////
////        if (!isReload)
////        {
////            Javascript.startDocumentReadyStateCheck(webView,
////                    complete ->
////                    {
////                        _activity.onDocumentComplete();
////                    });
////        }
//    }

    private DeliveryAppBaseActivity _activity;
}