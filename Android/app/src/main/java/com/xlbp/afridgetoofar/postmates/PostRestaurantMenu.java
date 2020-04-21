package com.xlbp.afridgetoofar.postmates;

import android.util.Log;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.PostAppState;
import com.xlbp.afridgetoofar.helpers.Javascript;

import java.util.ArrayList;

public class PostRestaurantMenu extends PostBase
{
    public PostRestaurantMenu(PostActivity postActivity, WebView webView)
    {
        super(postActivity, webView);

        init();
    }

    public String getDeepLink()
    {
        return _deepLink;
    }

    public FoodItem getSelectedFoodItem()
    {
        return _selectedFoodItem;
    }

    @Override
    protected void parseHtml(String html)
    {
        if (html.contains("Popular"))
        {
            retrieveFoodItemInfo();
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

    private void retrieveFoodItemInfo()
    {
        Javascript.getPostMatesDeepLink(webView, this::setPostMatesDeepLink);
        Javascript.getPostMatesRestaurantMenuInnerText(webView, this::parseInnerText);
    }

    private void setPostMatesDeepLink(String deepLink)
    {
        _deepLink = deepLink.replace("\"", "");
        Log.e("PostRestaurantMenu", "setPostMatesDeepLink " + _deepLink);
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

        if (foodItems.size() > 1)
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

        if (_foodItems.size() > 1)
        {
            AppState.setPostmatesAppState(PostAppState.RestaurantMenuReady);

            allRestaurantInfoParsed();
        }
        else
        {
            retrieveHtml();
        }
    }

    private void allRestaurantInfoParsed()
    {
        Log.e("PostmatesMainMenu", "Number of Food Items - " + _foodItems.size());

        int random = (int) (Math.random() * _foodItems.size());

        _selectedFoodItem = _foodItems.get(random);

        Log.e("PostmatesMainMenu", "_selectedFoodItem - " + _selectedFoodItem.name);

        AppState.setPostmatesAppState(PostAppState.SearchComplete);

        postActivity.onSearchComplete();
    }


    public class FoodItem
    {
        String innerText;
        String name;
        String price;
    }


    private String _deepLink;
    private ArrayList<FoodItem> _foodItems;
    private FoodItem _selectedFoodItem;
}