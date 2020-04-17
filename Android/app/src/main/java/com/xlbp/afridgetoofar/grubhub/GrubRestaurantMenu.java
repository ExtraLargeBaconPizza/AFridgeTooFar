package com.xlbp.afridgetoofar.grubhub;

import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.GrubAppState;
import com.xlbp.afridgetoofar.helpers.Javascript;

import java.util.ArrayList;

public class GrubRestaurantMenu extends GrubBase
{
    public GrubRestaurantMenu(GrubActivity grubActivity, WebView webView)
    {
        super(grubActivity, webView);

        init();
    }

    public FoodItem getSelectedFoodItem()
    {
        return _selectedFoodItem;
    }

    private boolean _allMenuHeadersClicked;

    @Override
    protected void parseHtml(String html)
    {
        if (html.contains("Menu") && !_allMenuHeadersClicked)
        {
            _allMenuHeadersClicked = true;

            Javascript.grubHubClickAllMenuHeaders(webView);

            retrieveHrefAndFoodItemInfo();
        }
        else
        {
            Log.e("GrubRestaurantMenu", "retrieveHtml");

            retrieveHtml();
        }
    }

    private void init()
    {
        _allMenuHeadersClicked = false;

        _foodItems = new ArrayList<>();
    }

    private void retrieveHrefAndFoodItemInfo()
    {
        Log.e("GrubRestaurantMenu", "retrieveHrefAndFoodItemInfo");
        Javascript.getGrubhubRestaurantMenuInnerText(webView, this::parseInnerText);
    }

    private void parseInnerText(String allInnerTexts)
    {
        Log.e("GrubRestaurantMenu", "parseInnerText");
        ArrayList<FoodItem> foodItems = new ArrayList<>();

        String[] innerTextElements = allInnerTexts.split("element");

        for (String innerText : innerTextElements)
        {
            FoodItem foodItem = new FoodItem();

            foodItem.innerText = innerText;

            foodItems.add(foodItem);
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

    private void parseFoodItemsNamePrice()
    {
        Log.e("GrubRestaurantMenu", "parseFoodItemsNamePrice");
        for (int i = _foodItems.size() - 1; i >= 0; i--)
        {
            Log.e("GrubRestaurantMenu", "1");

            FoodItem foodItem = _foodItems.get(i);

            String[] values = foodItem.innerText.split("\\\\n");

            foodItem.name = values[0];

            foodItem.name = foodItem.name.replace("\\", "");

            boolean keepFoodItem = false;

            Log.e("GrubRestaurantMenu", "2");

            for (int j = 1; j < values.length; j++)
            {
                Log.e("GrubRestaurantMenu", "3");

                if (values[j].contains("$") && values[j].length() < 10)
                {
                    Log.e("GrubRestaurantMenu", "4");
                    // remove anything non currency
                    foodItem.price = values[j].replace("\"", "");
                    foodItem.price = values[j].replace("[^\\d.]", "");

                    String currencyOnly = foodItem.price.replace("$", "");

                    Log.e("GrubRestaurantMenu", "5");

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
            AppState.setGrubhubAppState(GrubAppState.RestaurantMenuReady);

            allRestaurantInfoParsed();
        }
        else
        {
            Log.e("GrubRestaurantMenu", "10");
            retrieveHtml();
        }
    }

    private void allRestaurantInfoParsed()
    {
        Log.e("GrubhubMainMenu", "Number of Food Items - " + _foodItems.size());

        int random = (int) (Math.random() * _foodItems.size());

        _selectedFoodItem = _foodItems.get(random);

        Log.e("GrubhubMainMenu", "_selectedFoodItem - " + _selectedFoodItem.name);

        AppState.setGrubhubAppState(GrubAppState.SearchComplete);

        grubActivity.onSearchComplete();
    }


    public class FoodItem
    {
        String innerText;
        String name;
        String price;
    }


    private ArrayList<FoodItem> _foodItems;

    private FoodItem _selectedFoodItem;
}
