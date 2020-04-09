package com.xlbp.afridgetoofar.ubereats;

import android.os.Handler;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.helpers.Javascript;
import com.xlbp.afridgetoofar.enums.UberAppState;

public class UberHomePage extends UberBase
{
    public UberHomePage(UberActivity uberActivity, WebView webView)
    {
        super(uberActivity, webView);

        deliveryDetailsLoaded = false;
    }

    public static boolean deliveryDetailsLoaded;

    @Override
    public void parseHtml(String html)
    {
        // need to delay here because the find food button click can sometimes not work for slow connections
        new Handler().postDelayed(() ->
        {
            if (!deliveryDetailsLoaded)
            {
                AppState.setUberEatsAppState(UberAppState.DeliveryDetailsLoading);

                Javascript.clickElementByKeyword(webView, "a", "Find Food");
            }
        }, 500);
    }
}
