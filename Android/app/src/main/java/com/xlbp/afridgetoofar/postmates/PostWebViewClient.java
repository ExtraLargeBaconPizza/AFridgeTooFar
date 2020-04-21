package com.xlbp.afridgetoofar.postmates;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xlbp.afridgetoofar.DeliveryAppBaseActivity;
import com.xlbp.afridgetoofar.helpers.Javascript;

public class PostWebViewClient extends WebViewClient
{
    public PostWebViewClient(DeliveryAppBaseActivity activity)
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

        // if this url is loaded twice, it means the address was not found
        // we also need to make sure its not a reload from
        if (url.equals("https://postmates.com/") && !isReload)
        {
            if (!_alreadyLoadedHomepage)
            {
                _alreadyLoadedHomepage = true;
            }
            else
            {
                _activity.onAddressNotFound();
                return;
            }
        }

        Javascript.startDocumentReadyStateCheck(webView,
                complete ->
                {
                    _activity.onDocumentComplete();
                });
    }


    private DeliveryAppBaseActivity _activity;
    private boolean _alreadyLoadedHomepage;
}