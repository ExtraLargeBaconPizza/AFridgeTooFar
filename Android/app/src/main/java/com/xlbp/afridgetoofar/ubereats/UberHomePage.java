package com.xlbp.afridgetoofar.ubereats;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.AppState;
import com.xlbp.afridgetoofar.helpers.Javascript;
import com.xlbp.afridgetoofar.enums.UberAppState;

public class UberHomePage extends UberBase
{
    public UberHomePage(WebView webView)
    {
        super(webView);

        deliveryDetailsLoaded = false;
    }

    public static boolean deliveryDetailsLoaded;

    @Override
    public void parseHtml(String html)
    {
        // need to delay and loop here because the find food button click can sometimes not work for slow connections
        new Handler().postDelayed(() ->
        {
            if (!deliveryDetailsLoaded)
            {
                AppState.setUberEatsAppState(UberAppState.DeliveryDetailsLoading);

                Javascript.clickElementByKeyword(webView, "a", "Find Food");

                retrieveHtml();
            }
        }, 500);
    }
}
