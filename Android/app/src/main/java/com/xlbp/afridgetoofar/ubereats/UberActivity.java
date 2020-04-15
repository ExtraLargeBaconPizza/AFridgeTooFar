package com.xlbp.afridgetoofar.ubereats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;

import com.xlbp.afridgetoofar.DeliveryAppBaseActivity;
import com.xlbp.afridgetoofar.XlbpWebViewClient;
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
import com.xlbp.afridgetoofar.enums.UberAppState;
import com.xlbp.afridgetoofar.helpers.Helpers;

public class UberActivity extends DeliveryAppBaseActivity
{
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
            case DeliveryDetailsLoading:
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

    public void onViewOnAppClicked(View view)
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

    public void onDonateClicked(View view)
    {
        if (AppState.getUberEatsAppState() != UberAppState.Animating)
        {
            AppState.setMainScreenState(MainScreenState.SearchingApp);

            // TODO navigate to give me money
        }
    }

    public void onBackClicked(View view)
    {
        if (AppState.getUberEatsAppState() != UberAppState.Animating)
        {
            _view.animateNavigateBackAfterSearchComplete(this::navigateBack);
        }
    }

    private void init()
    {
        initView();

        initUberEats();

        initWebView();
    }

    private void initView()
    {
        _view = new UberView(this);
        setContentView(_view);

        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initUberEats()
    {
        AppState.setUberEatsAppState(UberAppState.DeliveryDetailsLoading);

        Bundle extras = getIntent().getExtras();
        String searchAddress = extras.getString("SearchAddress");
        boolean isDebugMode = (extras != null) ? extras.getBoolean("DebugMode") : false;

        if (!isDebugMode)
        {
            _view.getWebView().setTranslationX(Helpers.getScreenWidth());
        }

        _uberEatsDeliveryDetails = new UberDeliveryDetails(this, _view.getWebView(), searchAddress);
        _uberEatsMainMenu = new UberMainMenu(this, _view.getWebView());
        _uberEatsRestaurantMenu = new UberRestaurantMenu(this, _view.getWebView());
    }

    private void initWebView()
    {
        _view.getWebView().getSettings().setJavaScriptEnabled(true);
        _view.getWebView().setWebViewClient(new XlbpWebViewClient(this));
        _view.getWebView().setWebChromeClient(new WebChromeClient());

        // The follow two lines remove te webview's cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        _view.getWebView().loadUrl("https://www.ubereats.com/delivery-details");
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

    private void launchUberEats()
    {
        String url = _uberEatsRestaurantMenu.getSelectedFoodItem().href;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        startActivity(browserIntent);
    }

    private void navigateBack()
    {
        _view.getWebView().destroy();

        finish();
        return;
    }


    private UberView _view;

    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
}
