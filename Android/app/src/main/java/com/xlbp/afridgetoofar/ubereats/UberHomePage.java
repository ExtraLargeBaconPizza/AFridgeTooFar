package com.xlbp.afridgetoofar.ubereats;

import android.webkit.WebView;

import com.xlbp.afridgetoofar.AppState;
import com.xlbp.afridgetoofar.Javascript;

public class UberHomePage extends UberBase
{
    public UberHomePage(WebView webView)
    {
        super(webView);
    }

    @Override
    public void parseHtml(String html)
    {
        AppState.setUberEatsAppState(UberAppState.HomePageReady);

        AppState.setUberEatsAppState(UberAppState.DeliveryDetailsLoading);

        Javascript.clickElementByKeyword(webView, "a", "Find Food");
    }
}
