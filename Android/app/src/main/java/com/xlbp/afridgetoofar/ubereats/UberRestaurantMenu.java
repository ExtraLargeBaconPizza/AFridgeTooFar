package com.xlbp.afridgetoofar.ubereats;

import android.util.Log;
import android.webkit.WebView;

import java.util.ArrayList;

public class UberRestaurantMenu extends UberBase
{
    public UberRestaurantMenu(ActivityUber uberEatsActivity, WebView webView)
    {
        super(uberEatsActivity, webView);

        init();
    }

    public class FoodItem
    {
        String href;
        String innerText;
        String name;
        String price;
    }

    public FoodItem getSelectedFoodItem()
    {
        return _selectedFoodItem;
    }


    private void init()
    {
        _foodItems = new ArrayList<>();
    }

    @Override
    protected void parseHtml(String html)
    {
        retrieveHrefAndFoodItemInfo();
    }

    private void retrieveHrefAndFoodItemInfo()
    {
        String javascriptFunction = "javascript: (function(){ var result = ''; var elem = document.getElementsByTagName('a'); for (var i = 0; i < elem.length; i++) { result += 'foodItem'; result += elem[i].href; result += 'innerText' + elem[i].innerText; } return result; })();";

        webView.evaluateJavascript(javascriptFunction,
                hrefsAndFoodItemInfo ->
                {
                    parseHrefsAndFoodItemInfo(hrefsAndFoodItemInfo);
                });
    }

    private void parseHrefsAndFoodItemInfo(String hrefsAndFoodItemInfo)
    {
        ArrayList<FoodItem> foodItems = new ArrayList<>();
        boolean hasFoodItems = false;

        String[] splitHrefsAndFoodItemInfo = hrefsAndFoodItemInfo.split("foodItem");

        for (int i = 0; i < splitHrefsAndFoodItemInfo.length; i++)
        {
            String[] hrefInnerText = splitHrefsAndFoodItemInfo[i].split("innerText");

            if (hrefInnerText.length > 1)
            {
                String href = hrefInnerText[0];
                String innerText = hrefInnerText[1];

                if (href.contains("food-delivery")
                        && !href.contains("location-and-hours"))
                {
                    if (!(innerText.contains("Sign In")
                            || innerText.contains("Deliver now")
                            || innerText.contains("Menu")
                            || innerText.contains("Sold out")
                            || innerText.contains("Breakfast")
                            || innerText.contains("Lunch")
                            || innerText.contains("Dinner")))
                    {
                        hasFoodItems = true;

                        FoodItem foodItem = new FoodItem();

                        foodItem.href = href;
                        foodItem.innerText = innerText;

                        foodItems.add(foodItem);
                    }
                }
            }
        }

        if (hasFoodItems)
        {
            AppState.setUberEatsAppState(UberAppState.RestaurantMenuComplete);

            _foodItems = foodItems;

            parseFoodItemsNamePrice();
        }
        else
        {
            retrieveHtml();
        }
    }

    // TODO get Description
    private void parseFoodItemsNamePrice()
    {
        for (FoodItem foodItem : _foodItems)
        {
            String[] values = foodItem.innerText.split("\\\\n");

            foodItem.name = values[0];

            for (int i = 1; i < values.length; i++)
            {
                // TODO set min price of $5
                if (values[i].contains("$"))
                {
                    foodItem.price = values[i];
                }
            }
        }

        allRestaurantInfoParsed();
    }

    private void allRestaurantInfoParsed()
    {
        Log.e("UberEatsMainMenu", "Number of Food Items - " + _foodItems.size());

        int random = (int) (Math.random() * _foodItems.size());

        _selectedFoodItem = _foodItems.get(random);

        Log.e("UberEatsMainMenu", "selectedRestaurant - " + _selectedFoodItem.name);

        AppState.setUberEatsAppState(UberAppState.SearchComplete);

        uberActivity.searchComplete();
    }


    private ArrayList<FoodItem> _foodItems;

    private FoodItem _selectedFoodItem;

}
