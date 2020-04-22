package com.xlbp.afridgetoofar.postmates;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppScreenState;
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
import com.xlbp.afridgetoofar.helpers.Javascript;

import java.util.ArrayList;

public class PostMainMenu extends PostBase
{
    public PostMainMenu(PostActivity postActivity, WebView webView)
    {
        super(postActivity, webView);

        init();
    }

    @Override
    protected void parseHtml(String html)
    {
        if (html.contains("Sorry, no results were found"))
        {
            AppState.setMainScreenState(MainScreenState.AddressNotFound);

            postActivity.onAddressNotFound();
        }
        else if (!_mainMenuComplete)
        {
            yeetClick();

            Javascript.getPostMatesMainMenuHrefsInnerText(webView, this::parseHrefsAndInnerText);
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

    private boolean _yeetClickRunning;

    private void yeetClick()
    {
        if (!_yeetClickRunning)
        {
            _yeetClickRunning = true;

            // if loading takes too long, yeet another enter press
            new Handler().postDelayed(() ->
            {
                if (!_mainMenuComplete)
                {
                    KeyEvent enterKeyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
                    postActivity.dispatchKeyEvent(enterKeyEventDown);

                    _yeetClickRunning = false;
                    yeetClick();
                }
            }, 3000);
        }
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
                restaurant.name = innerText.split("\\\\n")[0];

                if (!restaurant.name.isEmpty() && restaurant.name != null)
                {
                    restaurants.add(restaurant);
                }
            }
        }

        // if the data hasn't loaded yet, check again
        if (restaurants.size() > 3)
        {
            if (!_mainMenuComplete)
            {
                _mainMenuComplete = true;

                AppState.setAppScreenState(AppScreenState.MainMenuReady);

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

            Log.e("PostmatesMainMenu", "_selectedRestaurant - " + _selectedRestaurant.name + " - href - " + _selectedRestaurant.href);

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
