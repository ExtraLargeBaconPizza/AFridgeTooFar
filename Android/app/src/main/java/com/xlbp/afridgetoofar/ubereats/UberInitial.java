package com.xlbp.afridgetoofar.ubereats;

import android.webkit.WebView;

public class UberInitial extends UberBase
{
    public UberInitial(ActivityUber uberEatsActivity, WebView webView)
    {
        super(uberEatsActivity, webView);
    }

    @Override
    public void parseHtml(String html)
    {
        AppState.setUberEatsAppState(UberAppState.InitialComplete);

        determineInitialScreen(html);
    }

    private void determineInitialScreen(String html)
    {
        AppState.setUberEatsAppState(UberAppState.DeliveryDetailsLoading);

        clickLinkByKeyword("Find Food");

//        if (html.contains("Your favorite food, delivered with Uber"))
//        {
//            AppState.setUberEatsAppState(UberAppState.DeliveryDetailsLoading);
//
//            clickLinkByKeyword("Find Food");
//        }
//        else
//        {
//            AppState.setUberEatsAppState(UberAppState.MainMenuLoading);
//
//            uberActivity.onDocumentComplete();
//        }
    }
}
