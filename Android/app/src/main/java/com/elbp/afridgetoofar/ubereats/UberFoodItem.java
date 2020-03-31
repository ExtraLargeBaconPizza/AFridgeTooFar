package com.elbp.afridgetoofar.ubereats;

import android.util.Log;
import android.webkit.WebView;

public class UberFoodItem extends UberBase
{
    public UberFoodItem(UberActivity uberEatsActivity, WebView webView)
    {
        super(uberEatsActivity, webView);
    }

    @Override
    protected void parseHtml(String html)
    {
        if (!_requiredSectionsComplete)
        {
            handleRequiredSections(html);
        }

        handleAddToOrderButton(html);
    }

    private void handleRequiredSections(String html)
    {
        // find if there are any required forms so we can parse and select an option
        String strFind = "Required";
        int fromIndex = 0;

        int requiredSectionCount = 0;

        while ((fromIndex = html.indexOf(strFind, fromIndex)) != -1)
        {
            requiredSectionCount++;
            fromIndex++;
        }

        Log.e("UberEatsFoodItem", strFind + " found: " + requiredSectionCount);

        _requiredSectionsComplete = true;
    }

    private void handleAddToOrderButton(String html)
    {
        // TODO @jim - there are some cases where the fooditem options are all out of stock, but it doesn't show on the main menu. we'll need to navigate back somehow
        String strFind = "http://www.w3.org/2000/svg";
        String strFind2 = "Add";

        // Doubling up to cover for latency issues
        if (html.contains(strFind) && html.contains(strFind2))
        {
            AppState.setUberEatsAppState(UberAppState.FoodItemComplete);

//            clickButtonByKeyword("Add");
        }
        else
        {
            retrieveHtml();
        }
    }


    private boolean _requiredSectionsComplete;
}
