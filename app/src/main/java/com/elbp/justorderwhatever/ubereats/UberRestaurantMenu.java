package com.elbp.justorderwhatever.ubereats;

import android.util.Log;
import android.webkit.WebView;

import com.elbp.justorderwhatever.Helpers;

import java.util.ArrayList;

public class UberRestaurantMenu extends UberBase
{
    public UberRestaurantMenu(UberActivity uberEatsActivity, WebView webView)
    {
        super(uberEatsActivity, webView);
    }

    @Override
    protected void parseHtml(String html)
    {
//        Log.e("UberEatsRestaurantMenu", "parseHtml");

        _foodItems = new ArrayList<>();

        String strFind = "href=";

        int fromIndex = 0;
        int foodItemCount = 0;

        // loop through and find all href's in the html's body
        while ((fromIndex = html.indexOf(strFind, fromIndex)) != -1)
        {
            // get the href
            int hrefIndex = fromIndex + 7;
            String href = "";

            while (html.charAt(hrefIndex) != '"')
            {
                href += html.charAt(hrefIndex);
                hrefIndex++;
            }

            // need to check to make sure its a potential food href
            if (href.contains("food-delivery") && !href.contains("location-and-hours"))
            {
                FoodItem foodItem = new FoodItem();

                foodItem.id = foodItemCount;
                foodItem.href = href;

                // Sometimes the last few hrefs don't have a $. This means the loop can go out of bounds.
                // We need to limit the bounds of the while loop so we don't exceed them and have an
                // exception thrown
                int limitBoundsIndex = html.length() - 10;

                // move hrefIndex to the prices' $ but not further than the limits
                while (html.charAt(hrefIndex) != '$' && hrefIndex < limitBoundsIndex)
                {
                    hrefIndex++;
                }

                String price = "";

                for (int i = hrefIndex + 1; i < hrefIndex + 6; i++)
                {
                    char charAtI = html.charAt(i);

                    // only add numbers or . to the price string
                    if (Character.isDigit(charAtI) || charAtI == '.')
                    {
                        price += charAtI;
                    }
                }

                foodItem.price = price;


                // verify this is a food item by ensuring it has a price and does not begin with a period
                // It could potentially have a period at the start due to the limitBoundsIndex issue
                if (!foodItem.price.equals("") && foodItem.price.charAt(0) != '.' && !foodItem.href.contains(uberEatsActivity.uberEatsUrl) && !foodItem.href.contains("menu"))
                {
                    // check for sold out or unavailable first
                    int farthestIndex = Math.min(fromIndex + 1000, html.length());

                    String substringToCheck = html.substring(fromIndex, farthestIndex);

                    if (substringToCheck.contains("Sold out") || substringToCheck.contains("Unavailable"))
                    {
                        Log.e("UberEatsRestaurantMenu", "Bad food item - Sold out or Unavailable " + substringToCheck);
                    }
                    else
                    {
                        _foodItems.add(foodItem);

                        foodItemCount++;
                    }
                }
            }

            fromIndex++;
        }

        foodItemCount--;

        Log.e("UberEatsRestaurantMenu", "Number of Food Items - " + (foodItemCount + 1));

        if (foodItemCount > 0)
        {
            AppState.setUberEatsAppState(UberAppState.RestaurantMenuComplete);
            // TODO @jim - temp hard coded price bound values
            // selectCart(20, 30, foodItemCount);
            navigateToFoodItem(0);
        }
        else
        {
            retrieveHtml();
        }
    }

    private void navigateToFoodItem(int index)
    {
        FoodItem foodItem = _foodItems.get(5);

        _foodItemUrl = uberEatsActivity.uberEatsUrl + foodItem.href;

        _foodItemUrl = Helpers.removeLastCharacter(_foodItemUrl);

        AppState.setUberEatsAppState(UberAppState.FoodItemLoading);

        uberEatsActivity.webViewLoadUrl(_foodItemUrl);
    }

    // Select food items that will be added to cart
    private void selectCart(int priceLowBounds, int priceUpperBounds, int foodItemCount)
    {
        if (!_cartSelectionComplete)
        {
            AppState.setUberEatsAppState(UberAppState.SelectingCart);

            Log.e("UberEatsRestaurantMenu", "Selecting Cart - Price range between $" + priceLowBounds + " - $" + priceUpperBounds);

            _cartItems = new ArrayList<>();

            float cartPrice = 0;

            while (cartPrice < priceLowBounds)
            {
                int random = (int) (Math.random() * foodItemCount);

                // make sure we haven't already added the item
                for (FoodItem foodItem : _cartItems)
                {
                    if (random == foodItem.id)
                    {
                        break;
                    }
                }

                FoodItem foodItem = _foodItems.get(random);

                if (cartPrice + Float.parseFloat("" + foodItem.price) < priceUpperBounds)
                {
                    cartPrice += Float.parseFloat("" + foodItem.price);

                    _cartItems.add(foodItem);
                }
            }

            for (FoodItem foodItem : _cartItems)
            {
                Log.e("UberEatsRestaurantMenu", "Cart Item - id: " + foodItem.id + " price: " + foodItem.price + " href: " + foodItem.href);
            }

            _cartSelectionComplete = true;

            AppState.setUberEatsAppState(UberAppState.SelectingCartComplete);

            navigateToNextFoodItem();
        }
    }

    private void navigateToNextFoodItem()
    {
        if (_cartSelectionIndex < _cartItems.size())
        {
            _foodItemUrl = uberEatsActivity.uberEatsUrl + _cartItems.get(_cartSelectionIndex).href;

            _foodItemUrl = Helpers.removeLastCharacter(_foodItemUrl);

            _cartSelectionIndex++;

            AppState.setUberEatsAppState(UberAppState.FoodItemLoading);

            uberEatsActivity.webViewLoadUrl(_foodItemUrl);
        }
        else
        {
            Log.e("UberEatsRestaurantMenu", " cart full bitches");
        }
    }


    private class FoodItem
    {
        int id;
        String href;
        String price;
    }

    private ArrayList<FoodItem> _foodItems;
    private ArrayList<FoodItem> _cartItems;

    private boolean _cartSelectionComplete;
    private int _cartSelectionIndex = 0;

    private String _foodItemUrl;
}
