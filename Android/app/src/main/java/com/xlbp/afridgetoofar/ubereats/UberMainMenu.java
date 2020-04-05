package com.xlbp.afridgetoofar.ubereats;

import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.AppState;
import com.xlbp.afridgetoofar.helpers.Helpers;
import com.xlbp.afridgetoofar.helpers.Javascript;
import com.xlbp.afridgetoofar.enums.UberAppState;

import java.util.ArrayList;

public class UberMainMenu extends UberBase
{

    public UberMainMenu(WebView webView)
    {
        super(webView);

        init();
    }

    public class Restaurant
    {
        String href;
        String name;
        String image;
    }

    public Restaurant getSelectedRestaurant()
    {
        return _selectedRestaurant;
    }

    public void selectRestaurant()
    {
        handleRestaurantSelection();
    }

    // TODO - refactor this to match UberRestaurantMenu
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

                AppState.setUberEatsAppState(UberAppState.MainMenuReady);

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
        _restaurantsAlreadyPicked = new ArrayList<>();
    }

    private void retrieveRestaurantInfo(ArrayList<String> hrefs)
    {
        int hrefsSize = hrefs.size();

        for (String href : hrefs)
        {
            Javascript.getHrefsInnerText(webView, href,
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

            selectRestaurant();
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

    private void handleRestaurantSelection()
    {
        int random = (int) (Math.random() * _restaurants.size());

        Restaurant selectedRestaurant = _restaurants.get(random);

        if (!_restaurantsAlreadyPicked.contains(selectedRestaurant))
        {
            _selectedRestaurant = selectedRestaurant;

            _restaurantsAlreadyPicked.add(_selectedRestaurant);

            Log.e("UberEatsMainMenu", "_selectedRestaurant - " + _selectedRestaurant.name);

            String _restaurantUrl = UberActivity.uberEatsUrl + _selectedRestaurant.href;
            _restaurantUrl = Helpers.removeLastCharacter(_restaurantUrl);

            AppState.setUberEatsAppState(UberAppState.RestaurantMenuLoading);

            webView.loadUrl(_restaurantUrl);
        }
        else
        {
            handleRestaurantSelection();
        }
    }


    private ArrayList<Restaurant> _restaurants;
    private ArrayList<Restaurant> _restaurantsAlreadyPicked;
    private boolean _mainMenuComplete;
    private int _parseInnerTextCount;
    private boolean _allRestaurantInfoRetrieved;

    private Restaurant _selectedRestaurant;
}
