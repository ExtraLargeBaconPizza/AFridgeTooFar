package com.xlbp.afridgetoofar.ubereats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
import com.xlbp.afridgetoofar.enums.UberAppState;

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
    protected void onDestroy()
    {
        super.onDestroy();

        _view.getWebView().destroy();
    }

    @Override
    public void onBackPressed()
    {
        if (temp)
        {
            _view.getWebView().goBack();

            return;
        }

        if (AppState.getUberEatsAppState() != UberAppState.Animating)
        {
            if (AppState.getUberEatsAppState() == UberAppState.SearchComplete)
            {
                _view.animateNavigateBackAfterSearchComplete(this::navigateBack);
            }
            else
            {
                navigateBack();
            }
        }
    }

    public void onDocumentComplete()
    {
        switch (AppState.getUberEatsAppState())
        {
            case HomePageLoading:
                _uberEatsHomePage.onDocumentComplete();
                break;
            case DeliveryDetailsLoading:
                _uberEatsHomePage = null;
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

    public void onAddressNotFound()
    {
        navigateBack();
    }

    public void onSearchComplete()
    {
        handleSearchComplete();
    }

    public void onViewOnClicked(View view)
    {
        if (AppState.getUberEatsAppState() == UberAppState.SearchComplete)
        {
            launchUberEats();
        }
    }

    public void onSearchAgainClicked(View view)
    {
        if (AppState.getUberEatsAppState() != UberAppState.Animating)
        {
            AppState.setMainScreenState(MainScreenState.SearchingApp);

            _view.animateSearchAgainAnimation(() ->
            {
                AppState.setUberEatsAppState(UberAppState.MainMenuReady);

                _uberEatsMainMenu.selectRestaurant();
            });
        }
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
        _view.getWebView().setWebViewClient(new UberWebViewClient(this));
        _view.getWebView().setWebChromeClient(new WebChromeClient());

        // The follow two lines remove uber eats cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        _view.getWebView().loadUrl(uberEatsUrl);
    }

    private void initUberEats()
    {
        AppState.setUberEatsAppState(UberAppState.HomePageLoading);

        Bundle extras = getIntent().getExtras();
        String searchAddress = extras.getString("SearchAddress");

        _uberEatsHomePage = new UberHomePage(this, _view.getWebView());
        _uberEatsDeliveryDetails = new UberDeliveryDetails(this, _view.getWebView(), searchAddress);
        _uberEatsMainMenu = new UberMainMenu(this, _view.getWebView());
        _uberEatsRestaurantMenu = new UberRestaurantMenu(this, _view.getWebView());
    }

    private void handleSearchComplete()
    {
        UberMainMenu.Restaurant selectedRestaurant = _uberEatsMainMenu.getSelectedRestaurant();
        UberRestaurantMenu.FoodItem foodItem = _uberEatsRestaurantMenu.getSelectedFoodItem();

        _view.setSearchCompleteText(foodItem.name, selectedRestaurant.name + "\n" + foodItem.price);

        Log.e("UberView", "Search Complete: selectedRestaurant - " + selectedRestaurant.name + " - food item - " + foodItem.name);

        _view.animateSearchComplete(() ->
        {
            AppState.setMainScreenState(MainScreenState.SearchComplete);

            AppState.setUberEatsAppState(UberAppState.SearchComplete);
        });
    }

    private boolean temp;

    private void launchUberEats()
    {
        temp = true;
//        String url = _uberEatsRestaurantMenu.getSelectedFoodItem().href;
//
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//
//        startActivity(browserIntent);
//        _view.getWebView().setAlpha(1);
        _view.getWebView().loadUrl(_uberEatsRestaurantMenu.getSelectedFoodItem().href);
        _view.getWebView().setTranslationX(0);
//        _view.getWebView().setClickable(true);
    }

    private void navigateBack()
    {
        _view.getWebView().destroy();

        finish();
        return;
    }


    private UberView _view;

    private UberHomePage _uberEatsHomePage;
    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
}
