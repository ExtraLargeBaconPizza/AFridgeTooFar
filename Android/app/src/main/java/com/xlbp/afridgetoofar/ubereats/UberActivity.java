package com.xlbp.afridgetoofar.ubereats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;

import com.xlbp.afridgetoofar.AppState;
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
    protected void onStop()
    {
        super.onStop();

        _view.getWebView().destroy();
    }

    @Override
    public void onBackPressed()
    {
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

    public void onSameRestaurantClicked(View view)
    {
        if (AppState.getUberEatsAppState() != UberAppState.Animating)
        {
//            _view.setSubtitleTextView(_uberEatsMainMenu.getSelectedRestaurant().name);

            _view.animateSearchSameRestaurantAnimation(() ->
            {
                AppState.setUberEatsAppState(UberAppState.RestaurantMenuLoading);

                _uberEatsRestaurantMenu.onDocumentComplete();
            });
        }
    }

    public void onSameAppClicked(View view)
    {
        if (AppState.getUberEatsAppState() != UberAppState.Animating)
        {
            _view.setSubtitleTextView("Uber Eats");

            _view.animateSearchSameAppAnimation(() ->
            {
                AppState.setUberEatsAppState(UberAppState.MainMenuReady);

                _uberEatsMainMenu.selectRestaurant();
            });
        }
    }

    public void onDifferentAppClicked(View view)
    {
        if (AppState.getUberEatsAppState() != UberAppState.Animating)
        {
            _view.animateNavigateBackAfterSearchComplete(this::navigateBack);
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

        _view.setSubtitleTextView("Uber Eats");

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

        _view.animateSearchComplete(() ->
        {
            AppState.setMainScreenState(MainScreenState.SearchComplete);

            AppState.setUberEatsAppState(UberAppState.SearchComplete);
        });
    }

    // TODO - figure out how to load specific food/restaurants
    private void launchUberEats()
    {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.ubercab.eats");

        if (launchIntent != null)
        {
            startActivity(launchIntent);
        }
    }

    private void navigateBack()
    {
        _view.getWebView().destroy();

        finish();
        return;
    }


    private UberView _view;

    private UberHomePage _uberEatsInitial;
    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
}
