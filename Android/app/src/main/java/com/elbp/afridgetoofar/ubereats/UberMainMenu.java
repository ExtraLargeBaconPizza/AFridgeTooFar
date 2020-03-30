package com.elbp.afridgetoofar.ubereats;

import android.util.Log;
import android.webkit.WebView;

import com.elbp.afridgetoofar.Helpers;

import java.util.ArrayList;

public class UberMainMenu extends UberBase
{
    public UberMainMenu(UberActivity uberEatsActivity, WebView webView)
    {
        super(uberEatsActivity, webView);
    }

    @Override
    protected void parseHtml(String html)
    {
        ArrayList restaurants = new ArrayList<String>();

        String strFind = "href=";

        int fromIndex = 0;
        int restaurantCount = 0;

        // loop through and find all href's in the html's body
        while ((fromIndex = html.indexOf(strFind, fromIndex)) != -1)
        {
            int hrefIndex = fromIndex + 7;
            String href = "";

            while (html.charAt(hrefIndex) != '"')
            {
                href += html.charAt(hrefIndex);
                hrefIndex++;
            }

            // need to check to make sure its a potential food href
            if (href.contains("food-delivery"))
            {
                // TODO @jim - need to check for unavailable restaurants
                restaurants.add(href);

                restaurantCount++;
            }

            fromIndex++;
        }

        // if the data hasn't loaded yet, check again
        if (restaurantCount > 3)
        {
            AppState.setUberEatsAppState(UberAppState.MainMenuComplete);

            selectRestaurant(restaurants, restaurantCount);
        }
        else
        {
            retrieveHtml();
        }
    }

    private void selectRestaurant(ArrayList restaurants, int restaurantCount)
    {
        Log.e("UberEatsMainMenu", "Number of Restaurants - " + restaurantCount);

        int random = (int) (Math.random() * restaurantCount);

        String _restaurantUrl = UberActivity.uberEatsUrl + restaurants.get(random);
        _restaurantUrl = Helpers.removeLastCharacter(_restaurantUrl);

        AppState.setUberEatsAppState(UberAppState.RestaurantMenuLoading);

        uberActivity.webViewLoadUrl(_restaurantUrl);
    }
}
