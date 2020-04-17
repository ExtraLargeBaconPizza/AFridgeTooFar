package com.xlbp.afridgetoofar.doordash;

import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xlbp.afridgetoofar.DeliveryAppBaseActivity;
import com.xlbp.afridgetoofar.helpers.Javascript;

public class DoorWebViewClient extends WebViewClient
{
    public DoorWebViewClient(DeliveryAppBaseActivity activity)
    {
        _activity = activity;
    }

    // need this for debugging, otherwise the webview would just be a blank white screen
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
//        Log.e("XlbpWebViewClient", "shouldOverrideUrlLoading" + url);
        view.loadUrl(url);
        return true;
    }

//    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon)
//    {
//        super.onPageStarted(view, url, favicon);
//        Log.e("XlbpWebViewClient", "onPageStarted " + url);
//    }

    @Override
    public void onPageFinished(WebView webView, String url)
    {
        super.onPageFinished(webView, url);
        Log.e("XlbpWebViewClient", "onPageFinished " + url);

        Javascript.startDocumentReadyStateCheck(webView,
                complete ->
                {
                    Log.e("XlbpWebViewClient", "onPageFinished document ready state = complete");
                    _activity.onDocumentComplete();
                });
    }

    @Override
    public void doUpdateVisitedHistory(WebView webView, String url, boolean isReload)
    {
        super.doUpdateVisitedHistory(webView, url, isReload);
        Log.e("XlbpWebViewClient", "doUpdateVisitedHistory " + url);
//        Javascript.startDocumentReadyStateCheck(webView,
//                complete ->
//                {
//                    Log.e("XlbpWebViewClient", "doUpdateVisitedHistory document ready state = complete");
//                    _activity.onDocumentComplete();
//                });
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
    {
        Toast.makeText(_activity, "Your Internet Connection May not be active Or " + error, Toast.LENGTH_LONG).show();
        Log.e("XlbpWebViewClient", "onReceivedError " + error);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
        Log.e("XlbpWebViewClient", "errorCode " + errorCode);
        Log.e("XlbpWebViewClient", "description " + description);
        Log.e("XlbpWebViewClient", "failingUrl " + failingUrl);
    }


    private DeliveryAppBaseActivity _activity;
}