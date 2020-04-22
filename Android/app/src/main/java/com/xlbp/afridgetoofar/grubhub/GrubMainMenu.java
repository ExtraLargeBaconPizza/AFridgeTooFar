package com.xlbp.afridgetoofar.grubhub;

import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppScreenState;
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.helpers.Javascript;

import java.util.ArrayList;

public class GrubMainMenu extends GrubBase
{
    public GrubMainMenu(GrubActivity grubActivity, WebView webView)
    {
        super(grubActivity, webView);

        init();
    }

    @Override
    protected void parseHtml(String html)
    {
        if (html.contains("Sorry, no results were found"))
        {
            grubActivity.onAddressNotFound();
        }
        else if (!_mainMenuComplete)
        {
            AppState.setAppScreenState(AppScreenState.MainMenuReady);

            Javascript.getGrubHubMainMenuHrefsInnerText(webView, this::parseHrefsAndInnerText);
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

                Restaurant restaurant = new Restaurant();

                restaurant.href = href;
                restaurant.name = "";

                String[] innerTextItems = innerText.split("\\\\n");

                for (String innerTextItem : innerTextItems)
                {
                    if (!innerTextItem.isEmpty() && restaurant.name.isEmpty())
                    {
                        restaurant.name = innerTextItem;
                        restaurants.add(restaurant);
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

                _restaurants = restaurants;

                handleRestaurantSelection();
            }
        }
        else
        {
            retrieveHtml();
        }
    }

    private void handleRestaurantSelection()
    {
        int random = (int) (Math.random() * _restaurants.size());

        Restaurant selectedRestaurant = _restaurants.get(random);

        if (!_restaurantsAlreadyPicked.contains(selectedRestaurant))
        {
            _selectedRestaurant = selectedRestaurant;

            _restaurantsAlreadyPicked.add(_selectedRestaurant);

            Log.e("GrubhubMainMenu", "_selectedRestaurant - " + _selectedRestaurant.name + " - href - " + _selectedRestaurant.href);

            AppState.setAppScreenState(AppScreenState.RestaurantMenuLoading);

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
    }


    private ArrayList<Restaurant> _restaurants;
    private ArrayList<Restaurant> _restaurantsAlreadyPicked;
    private boolean _mainMenuComplete;

    private Restaurant _selectedRestaurant;
}
