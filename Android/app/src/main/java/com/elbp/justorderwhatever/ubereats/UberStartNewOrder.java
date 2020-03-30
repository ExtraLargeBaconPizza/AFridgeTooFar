package com.elbp.justorderwhatever.ubereats;

import android.webkit.WebView;

public class UberStartNewOrder extends UberBase
{
    public UberStartNewOrder(UberActivity uberEatsActivity, WebView webView)
    {
        super(uberEatsActivity, webView);
    }

    @Override
    protected void parseHtml(String html)
    {
        String strFind = "http://www.w3.org/2000/svg";
        String strFind2 = "New";

        // Doubling up to cover for latency issues
        if (html.contains(strFind) && html.contains(strFind2))
        {
            AppState.setUberEatsAppState(UberAppState.StartNewOrderComplete);

            clickButtonByKeyword("New");
        }
        else
        {
            retrieveHtml();
        }
    }
}
