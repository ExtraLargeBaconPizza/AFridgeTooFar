package com.elbp.afridgetoofar.ubereats;

import android.webkit.WebView;

public class UberInitial extends UberBase
{
    public UberInitial(UberActivity uberEatsActivity, WebView webView)
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
        if (html.contains("Your favorite food, delivered with Uber"))
        {
            AppState.setUberEatsAppState(UberAppState.DeliveryDetailsLoading);

//            new android.os.Handler().postDelayed(() ->
//            {
//                clickLinkByKeyword("Find Food");
//            }, 5000);

            clickLinkByKeyword("Find Food");
        }
        else
        {
            AppState.setUberEatsAppState(UberAppState.MainMenuComplete);
        }
    }
}
