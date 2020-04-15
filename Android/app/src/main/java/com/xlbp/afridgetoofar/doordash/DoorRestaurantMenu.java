package com.xlbp.afridgetoofar.doordash;

import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.UberAppState;
import com.xlbp.afridgetoofar.helpers.Javascript;

import java.util.ArrayList;

public class DoorRestaurantMenu extends DoorBase
{
    public DoorRestaurantMenu(DoorActivity uberActivity, WebView webView)
    {
        super(uberActivity, webView);

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
        Javascript.getAllHrefsAndInnerText(webView, this::parseHrefsAndInnerText);
    }

    private void parseHrefsAndInnerText(String hrefsAndInnerText)
    {
        ArrayList<FoodItem> foodItems = new ArrayList<>();

        String[] hrefsAndInnerTextElements = hrefsAndInnerText.split("element");

        for (int i = 0; i < hrefsAndInnerTextElements.length; i++)
        {
            String[] hrefInnerText = hrefsAndInnerTextElements[i].split("innerText");

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
                        FoodItem foodItem = new FoodItem();

                        foodItem.href = href;
                        foodItem.innerText = innerText;

                        foodItems.add(foodItem);
                    }
                }
            }
        }

        if (foodItems.size() > 0)
        {
            _foodItems = foodItems;

            parseFoodItemsNamePrice();
        }
        else
        {
            retrieveHtml();
        }
    }

    // TODO - get picture will probably have to navigate to food item, or delay to ensure images have loaded...
    private void parseFoodItemsNamePrice()
    {
        for (int i = _foodItems.size() - 1; i >= 0; i--)
        {
            FoodItem foodItem = _foodItems.get(i);

            String[] values = foodItem.innerText.split("\\\\n");

            foodItem.name = values[0];

            foodItem.name = foodItem.name.replace("\\", "");

            boolean keepFoodItem = false;

            for (int j = 1; j < values.length; j++)
            {
                if (values[j].contains("$") && values[j].length() < 10)
                {
                    foodItem.price = values[j].replace("US", "");
                    foodItem.price = values[j].replace("CA", "");

                    String currencyOnly = foodItem.price.replace("[^\\d.]", "").replace("$", "").replace("\"", "");

                    if (!currencyOnly.isEmpty())
                    {
                        try
                        {
                            float priceCheck = Float.parseFloat(currencyOnly);

                            if (priceCheck >= 4.99f)
                            {
                                keepFoodItem = true;
                            }
                        }
                        catch (Exception e)
                        {
                            // do nothing
                        }
                    }
                }
            }

            if (!keepFoodItem)
            {
                _foodItems.remove(foodItem);
            }
        }

        if (_foodItems.size() > 0)
        {
            AppState.setUberEatsAppState(UberAppState.RestaurantMenuReady);

            allRestaurantInfoParsed();
        }
        else
        {
            retrieveHtml();
        }
    }

    private void allRestaurantInfoParsed()
    {
        Log.e("UberEatsMainMenu", "Number of Food Items - " + _foodItems.size());

        int random = (int) (Math.random() * _foodItems.size());

        _selectedFoodItem = _foodItems.get(random);

        Log.e("UberEatsMainMenu", "_selectedFoodItem - " + _selectedFoodItem.name);

        AppState.setUberEatsAppState(UberAppState.SearchComplete);

        uberActivity.onSearchComplete();
    }


    private ArrayList<FoodItem> _foodItems;

    private FoodItem _selectedFoodItem;
}