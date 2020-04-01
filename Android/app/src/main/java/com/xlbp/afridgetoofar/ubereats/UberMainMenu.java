package com.xlbp.afridgetoofar.ubereats;

import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.Helpers;

import java.util.ArrayList;

public class UberMainMenu extends UberBase
{

    public UberMainMenu(ActivityUber uberEatsActivity, WebView webView)
    {
        super(uberEatsActivity, webView);

        init();
    }

    public class Restaurant
    {
        String href;
        String name;
        // TODO get image and display
        String image;
    }

    public Restaurant getSelectedRestaurant()
    {
        return _selectedRestaurant;
    }

    @Override
    protected void parseHtml(String html)
    {
        String strFind = "href=";

        int fromIndex = 0;
        int restaurantCount = 0;
        ArrayList<String> hrefs = new ArrayList<>();

        // loop through and find all href's in the html's body
        while ((fromIndex = html.indexOf(strFind, fromIndex)) != -1)
        {
            int hrefIndex = fromIndex + 7;
            String href = "";

            while (html.charAt(hrefIndex) != '"')
            {
                // TODO can probably improve perf by just getting the final index then substringing. this can be done elsewhere as well
                // TODO or just use javascript to get all href strings ..
                href += html.charAt(hrefIndex);
                hrefIndex++;
            }

            // need to check to make sure its a potential food href
            if (href.contains("food-delivery"))
            {
                // check for unavailable restaurants
                int farthestIndex = Math.min(fromIndex + 1000, html.length());

                String substringToCheck = html.substring(fromIndex, farthestIndex);

                if (!substringToCheck.contains("Currently unavailable"))
                {
                    hrefs.add(href);

                    restaurantCount++;
                }
            }

            fromIndex++;
        }

        // if the data hasn't loaded yet, check again
        if (restaurantCount > 3)
        {
            if (!_mainMenuComplete)
            {
                _mainMenuComplete = true;

                AppState.setUberEatsAppState(UberAppState.MainMenuComplete);

                retrieveRestaurantInfo(hrefs);
            }
        }
        else
        {
            retrieveHtml();
        }
    }

    private void init()
    {
        _restaurants = new ArrayList<>();
    }

    private void retrieveRestaurantInfo(ArrayList<String> hrefs)
    {
        int hrefsSize = hrefs.size();

        for (String href : hrefs)
        {
            String javascriptFunction = "javascript: (function(){ return(document.querySelectorAll(\"a[href='" + href + "']\")[0].parentElement.innerText); })();";

            webView.evaluateJavascript(javascriptFunction,
                    innerText ->
                    {
                        parseInnerText(href, innerText, hrefsSize);
                    });
        }
    }

    private void parseInnerText(String href, String innerText, int hrefsSize)
    {
        _parseInnerTextCount++;

        String[] values = innerText.split("\\\\n");

        if (values.length > 3)
        {
            String restaurantName = getRestaurantName(values);

            if (!restaurantName.isEmpty())
            {
                Restaurant restaurant = new Restaurant();

                restaurant.href = href;
                restaurant.name = restaurantName;

                _restaurants.add(restaurant);
            }
        }

        if (_parseInnerTextCount >= hrefsSize && !_allRestaurantInfoRetrieved)
        {
            _allRestaurantInfoRetrieved = true;

            allRestaurantInfoRetrieved();
        }
    }

    private String getRestaurantName(String[] values)
    {
        String restaurantName = "";

        for (int i = 0; i < 3; i++)
        {
            if (!(values[i].contains("$")
                    || values[i].contains("\"")
                    || values[i].contains("Buy 1")
                    || values[i].contains("Delivery Fee")))
            {
                if (restaurantName.isEmpty())
                {
                    restaurantName = values[i];
                }
            }
        }

        return restaurantName;
    }

    private void allRestaurantInfoRetrieved()
    {
        Log.e("UberEatsMainMenu", "Number of Restaurants - " + _restaurants.size());

        int random = (int) (Math.random() * _restaurants.size());

        _selectedRestaurant = _restaurants.get(random);

        Log.e("UberEatsMainMenu", "selectedRestaurant - " + _selectedRestaurant.name);

        String _restaurantUrl = ActivityUber.uberEatsUrl + _selectedRestaurant.href;
        _restaurantUrl = Helpers.removeLastCharacter(_restaurantUrl);

        AppState.setUberEatsAppState(UberAppState.RestaurantMenuLoading);

        uberActivity.webViewLoadUrl(_restaurantUrl);
    }


    private ArrayList<Restaurant> _restaurants;
    private boolean _mainMenuComplete;
    private int _parseInnerTextCount;
    private boolean _allRestaurantInfoRetrieved;

    private Restaurant _selectedRestaurant;
}
