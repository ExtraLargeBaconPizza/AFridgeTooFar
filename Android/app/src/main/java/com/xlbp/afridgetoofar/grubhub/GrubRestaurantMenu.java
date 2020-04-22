package com.xlbp.afridgetoofar.grubhub;

import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppScreenState;
import com.xlbp.afridgetoofar.enums.AppState;
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

    @Override
    protected void parseHtml(String html)
    {
        if (html.contains("Menu"))
        {
            if (!_allMenuHeadersClicked)
            {
                AppState.setAppScreenState(AppScreenState.RestaurantMenuReady);

                Javascript.grubHubClickAllMenuHeaders(webView, this::allMenuHeadersExpanded);
            }
            else
            {
                retrieveFoodItemInfo();
            }
        }
        else
        {
            retrieveHtml();
        }
    }

    private void init()
    {
        _foodItems = new ArrayList<>();
    }

    private void allMenuHeadersExpanded(String expansionComplete)
    {
        _allMenuHeadersClicked = true;

        retrieveFoodItemInfo();
    }

    private void retrieveFoodItemInfo()
    {
        Javascript.getGrubhubRestaurantMenuInnerText(webView, this::parseInnerText);
    }

    private void parseInnerText(String allInnerTexts)
    {
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
                    // remove anything non currency
                    foodItem.price = values[j].replace("\"", "");
                    foodItem.price = values[j].replace("\\+", "");
                    foodItem.price = values[j].replace("[^\\d.]", "");

                    String currencyOnly = foodItem.price.replace("$", "");

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
            allRestaurantInfoParsed();
        }
        else
        {
            retrieveHtml();
        }
    }

    private void allRestaurantInfoParsed()
    {
        Log.e("GrubhubMainMenu", "Number of Food Items - " + _foodItems.size());

        // need to set this to false so that the menu headers can be expanded when we search again
        _allMenuHeadersClicked = false;

        int random = (int) (Math.random() * _foodItems.size());

        _selectedFoodItem = _foodItems.get(random);

        Log.e("GrubhubMainMenu", "_selectedFoodItem - " + _selectedFoodItem.name);

        AppState.setAppScreenState(AppScreenState.SearchComplete);

        grubActivity.onSearchComplete();
    }


    public class FoodItem
    {
        String innerText;
        String name;
        String price;
    }


    private boolean _allMenuHeadersClicked;
    private ArrayList<FoodItem> _foodItems;
    private FoodItem _selectedFoodItem;
}
