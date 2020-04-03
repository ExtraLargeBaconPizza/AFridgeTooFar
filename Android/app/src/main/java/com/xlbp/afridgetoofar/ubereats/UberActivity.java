package com.xlbp.afridgetoofar.ubereats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;

import com.xlbp.afridgetoofar.AppState;
import com.xlbp.afridgetoofar.XlbpWebViewClient;

public class UberActivity extends AppCompatActivity
{
    public static String uberEatsUrl = "https://www.ubereats.com";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    public void onBackPressed()
    {
        _view.getWebView().destroy();

        finish();
        return;
    }

    public void onDocumentComplete()
    {
        switch (AppState.getUberEatsAppState())
        {
            case HomePageLoading:
                _uberEatsInitial.onDocumentComplete();
                break;
            case DeliveryDetailsLoading:
                _uberEatsInitial = null;
                _uberEatsDeliveryDetails.onDocumentComplete();
                break;
            case MainMenuLoading:
                _uberEatsDeliveryDetails = null;
                _uberEatsMainMenu.onDocumentComplete();
                break;
            case RestaurantMenuLoading:
                _uberEatsRestaurantMenu.onDocumentComplete();
                break;
        }
    }

    public void searchComplete()
    {
        handleSearchComplete();
    }

    // TODO - figure out what to do in this situation? currently we can just instantly reroll
    public void onRerollFoodItemClicked(View view)
    {
//        _searchingSubtitleTextView.setText(_uberEatsMainMenu.getSelectedRestaurant().name);
//
//        rerollFoodItemAnimation();
//
//        new Handler().postDelayed(() ->
//        {
//            _uberEatsRestaurantMenu.onDocumentComplete();
//        }, 3000);
    }

    private void init()
    {
        _view = new UberView(this);

        initWebView();

        initUberEats();
    }

    private void initWebView()
    {
        _view.getWebView().getSettings().setJavaScriptEnabled(true);
        _view.getWebView().setWebViewClient(new XlbpWebViewClient(this));
        _view.getWebView().setWebChromeClient(new WebChromeClient());

        // The follow two lines remove uber eats cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        _view.getWebView().loadUrl(uberEatsUrl);
    }

    private void initUberEats()
    {
        AppState.setUberEatsAppState(UberAppState.HomePageLoading);

        _uberEatsInitial = new UberHomePage(_view.getWebView());
        _uberEatsDeliveryDetails = new UberDeliveryDetails(this, _view.getWebView());
        _uberEatsMainMenu = new UberMainMenu(_view.getWebView());
        _uberEatsRestaurantMenu = new UberRestaurantMenu(this, _view.getWebView());
    }

    private void handleSearchComplete()
    {
        UberMainMenu.Restaurant selectedRestaurant = _uberEatsMainMenu.getSelectedRestaurant();
        UberRestaurantMenu.FoodItem foodItem = _uberEatsRestaurantMenu.getSelectedFoodItem();

        _view.setSearchCompleteText(selectedRestaurant.name, foodItem.name, foodItem.price);

        _view.animateSearchComplete();
    }

    // TODO - @jim figure out how to load specific food/restaurants
    private void launchUberEats()
    {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.ubercab.eats");

        if (launchIntent != null)
        {
            startActivity(launchIntent);
        }
    }


    private UberView _view;

    private UberHomePage _uberEatsInitial;
    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
}
