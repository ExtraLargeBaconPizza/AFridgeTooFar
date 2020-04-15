package com.xlbp.afridgetoofar.doordash;

import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
import com.xlbp.afridgetoofar.enums.UberAppState;
import com.xlbp.afridgetoofar.helpers.Javascript;

import java.util.ArrayList;

public class DoorMainMenu extends DoorBase
{
    public DoorMainMenu(DoorActivity uberActivity, WebView webView)
    {
        super(uberActivity, webView);

        init();
    }

    @Override
    protected void parseHtml(String html)
    {
        if (html.contains("Sorry, we're not there yet"))
        {
            AppState.setMainScreenState(MainScreenState.AddressNotFound);

            uberActivity.onAddressNotFound();
        }
        else
        {
            Javascript.getAllHrefsAndInnerText(webView, this::parseHrefsAndInnerText);
        }
    }

    public Restaurant getSelectedRestaurant()
    {
        return _selectedRestaurant;
    }

    public void selectRestaurant()
    {
        handleRestaurantSelection();
    }

    private void init()
    {
        _restaurants = new ArrayList<>();
        _restaurantsAlreadyPicked = new ArrayList<>();
    }

    private void parseHrefsAndInnerText(String hrefsAndInnerText)
    {
        ArrayList<Restaurant> restaurants = new ArrayList<>();

        String[] hrefsAndInnerTextElements = hrefsAndInnerText.split("element");

        for (int i = 0; i < hrefsAndInnerTextElements.length; i++)
        {
            String[] hrefInnerText = hrefsAndInnerTextElements[i].split("innerText");

            if (hrefInnerText.length > 1)
            {
                String href = hrefInnerText[0];
                String innerText = hrefInnerText[1];

                if (href.contains("food-delivery"))
                {
                    if (!innerText.contains("Currently unavailable")
                            && !innerText.contains("Opens On"))
                    {
                        Restaurant restaurant = new Restaurant();

                        restaurant.href = href;
                        restaurant.name = getRestaurantName(innerText.split("\\\\n"));

                        if (!restaurant.name.isEmpty())
                        {
                            restaurants.add(restaurant);
                        }
                    }
                }
            }
        }

        // if the data hasn't loaded yet, check again
        if (restaurants.size() > 3)
        {
            if (!_mainMenuComplete)
            {
                _mainMenuComplete = true;

                AppState.setUberEatsAppState(UberAppState.MainMenuReady);

                _restaurants = restaurants;

                handleRestaurantSelection();
            }
        }
        else
        {
            retrieveHtml();
        }
    }

    private String getRestaurantName(String[] values)
    {
        String restaurantName = "";

        values[values.length - 1] = values[0].replace("\"", "");

        for (int i = 0; i < values.length; i++)
        {
            values[i] = values[i].replace("\"", "");
            values[i] = values[i].replace("\\", "");

            // TODO restaurants with Min will get filtered and probably cause an error, but this is good enough for now
            if (!(values[i].contains("$")
                    || values[i].contains("Buy 1")
                    || values[i].contains("Delivery Fee")
                    || values[i].contains("Min")
                    || values[i].isEmpty()))
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

            Log.e("UberEatsMainMenu", "_selectedRestaurant - " + _selectedRestaurant.name + " href - " + _selectedRestaurant.href);

            AppState.setUberEatsAppState(UberAppState.RestaurantMenuLoading);

            webView.loadUrl(_selectedRestaurant.href);
        }
        else
        {
            handleRestaurantSelection();
        }
    }


    public class Restaurant
    {
        String href;
        String name;
        String image;
    }


    private ArrayList<Restaurant> _restaurants;
    private ArrayList<Restaurant> _restaurantsAlreadyPicked;
    private boolean _mainMenuComplete;

    private Restaurant _selectedRestaurant;
}
