package com.xlbp.afridgetoofar.ubereats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.DeliveryAppBaseActivity;
import com.xlbp.afridgetoofar.SearchingView;
import com.xlbp.afridgetoofar.enums.AppScreenState;
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
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

        _webView.destroy();
        finish();
    }

    @Override
    public void onBackPressed()
    {
        if (AppState.getAppScreenState() != AppScreenState.Animating)
        {
            if (AppState.getAppScreenState() == AppScreenState.SearchComplete)
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
        switch (AppState.getAppScreenState())
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
        AppState.setMainScreenState(MainScreenState.AddressNotFound);

        navigateBack();
    }

    public void onSearchComplete()
    {
        handleSearchComplete();
    }

    public void onViewOnAppClicked(View view)
    {
        if (AppState.getAppScreenState() == AppScreenState.SearchComplete)
        {
            launchUberEats();
        }
    }

    public void onSearchAgainClicked(View view)
    {
        if (AppState.getAppScreenState() != AppScreenState.Animating)
        {
            AppState.setMainScreenState(MainScreenState.SearchingApp);

            _view.animateSearchAgainAnimation(() ->
            {
                AppState.setAppScreenState(AppScreenState.MainMenuReady);

                if (_isDebugMode)
                {
                    _webView.setTranslationX(0);
                }

                _uberEatsMainMenu.selectRestaurant();
            });
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
        _view = new SearchingView(this);
        setContentView(_view);

        _view.setSelectedAppTextView("Uber Eats");

        _webView = _view.getWebView();

        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initUberEats()
    {
        AppState.setAppScreenState(AppScreenState.DeliveryDetailsLoading);

        Bundle extras = getIntent().getExtras();
        String searchAddress = extras.getString("SearchAddress");
        _isDebugMode = (extras != null) ? extras.getBoolean("DebugMode") : false;

        if (!_isDebugMode)
        {
            _webView.setTranslationX(Helpers.getScreenWidth());
        }

        _uberEatsDeliveryDetails = new UberDeliveryDetails(this, _webView, searchAddress);
        _uberEatsMainMenu = new UberMainMenu(this, _webView);
        _uberEatsRestaurantMenu = new UberRestaurantMenu(this, _webView);
    }

    private void initWebView()
    {
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.setWebViewClient(new UberWebViewClient(this));
        _webView.setWebChromeClient(new WebChromeClient());

        // The follow two lines remove te webview's cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        _webView.loadUrl("https://www.ubereats.com/delivery-details");
    }

    private void handleSearchComplete()
    {
        UberMainMenu.Restaurant selectedRestaurant = _uberEatsMainMenu.getSelectedRestaurant();
        UberRestaurantMenu.FoodItem foodItem = _uberEatsRestaurantMenu.getSelectedFoodItem();

        _view.setSearchCompleteText(foodItem.name, selectedRestaurant.name + "\n" + foodItem.price);

        Log.e("UberActivity", "Search Complete: selectedRestaurant - " + selectedRestaurant.name + " - food item - " + foodItem.name);

        if (_isDebugMode)
        {
            _webView.setTranslationX(Helpers.getScreenWidth());
        }

        _view.animateSearchComplete(() ->
        {
            AppState.setMainScreenState(MainScreenState.SearchComplete);

            AppState.setAppScreenState(AppScreenState.SearchComplete);
        });
    }

    private void launchUberEats()
    {
        String url = _uberEatsMainMenu.getSelectedRestaurant().href;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void navigateBack()
    {
        _webView.destroy();

        finish();
        return;
    }


    private SearchingView _view;
    private WebView _webView;
    private boolean _isDebugMode;

    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
}
