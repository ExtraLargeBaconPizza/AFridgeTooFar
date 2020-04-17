package com.xlbp.afridgetoofar.doordash;

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
import com.xlbp.afridgetoofar.ubereats.UberWebViewClient;
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.DoorAppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
import com.xlbp.afridgetoofar.helpers.Helpers;

public class DoorActivity extends DeliveryAppBaseActivity
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
        if (AppState.getDoorDashAppState() != DoorAppState.Animating)
        {
            if (AppState.getDoorDashAppState() == DoorAppState.SearchComplete)
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
        switch (AppState.getDoorDashAppState())
        {
            case DeliveryDetailsLoading:
                _doorDashDeliveryDetails.onDocumentComplete();
                break;
            case MainMenuLoading:
                _doorDashDeliveryDetails = null;
                _doorDashMainMenu.onDocumentComplete();
                break;
            case RestaurantMenuLoading:
                _doorDashRestaurantMenu.onDocumentComplete();
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
        if (AppState.getDoorDashAppState() == DoorAppState.SearchComplete)
        {
            launchDoorDash();
        }
    }

    public void onSearchAgainClicked(View view)
    {
        if (AppState.getDoorDashAppState() != DoorAppState.Animating)
        {
            AppState.setMainScreenState(MainScreenState.SearchingApp);

            _view.animateSearchAgainAnimation(() ->
            {
                AppState.setDoorDashAppState(DoorAppState.MainMenuReady);

                _doorDashMainMenu.selectRestaurant();
            });
        }
    }

    public void onDonateClicked(View view)
    {
        if (AppState.getDoorDashAppState() != DoorAppState.Animating)
        {
            AppState.setMainScreenState(MainScreenState.SearchingApp);

            // TODO - remove joke
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=SIdxVR_7ikg"));

            startActivity(browserIntent);
        }
    }

    private void init()
    {
        initView();

        initDoorDash();

        initWebView();
    }

    private void initView()
    {
        _view = new DoorView(this);
        setContentView(_view);

        _webView = _view.getWebView();

        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initDoorDash()
    {
        AppState.setDoorDashAppState(DoorAppState.DeliveryDetailsLoading);

        Bundle extras = getIntent().getExtras();
        String searchAddress = extras.getString("SearchAddress");
        boolean isDebugMode = (extras != null) ? extras.getBoolean("DebugMode") : false;

        if (!isDebugMode)
        {
            _webView.setTranslationX(Helpers.getScreenWidth());
        }

        _view.setSelectedAppTextView("DoorDash");

        _doorDashDeliveryDetails = new DoorDeliveryDetails(this, _webView, searchAddress);
        _doorDashMainMenu = new DoorMainMenu(this, _webView);
        _doorDashRestaurantMenu = new DoorRestaurantMenu(this, _webView);
    }

    private void initWebView()
    {
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.setWebViewClient(new UberWebViewClient(this));
        _webView.setWebChromeClient(new WebChromeClient());

//        // The follow two lines remove te webview's cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        _webView.loadUrl("https://www.doordash.com/en-US");
    }

    private void handleSearchComplete()
    {
        DoorMainMenu.Restaurant selectedRestaurant = _doorDashMainMenu.getSelectedRestaurant();
        DoorRestaurantMenu.FoodItem foodItem = _doorDashRestaurantMenu.getSelectedFoodItem();

        _view.setSearchCompleteText(foodItem.name, selectedRestaurant.name + "\n" + foodItem.price);

        Log.e("DoorActivity", "Search Complete: selectedRestaurant - " + selectedRestaurant.name + " - food item - " + foodItem.name);

        _view.animateSearchComplete(() ->
        {
            AppState.setMainScreenState(MainScreenState.SearchComplete);

            AppState.setDoorDashAppState(DoorAppState.SearchComplete);
        });
    }

    private void launchDoorDash()
    {
        String url = _doorDashRestaurantMenu.getSelectedFoodItem().href;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        startActivity(browserIntent);
    }

    private void navigateBack()
    {
        _webView.destroy();

        finish();
        return;
    }


    private DoorView _view;
    private WebView _webView;

    private DoorDeliveryDetails _doorDashDeliveryDetails;
    private DoorMainMenu _doorDashMainMenu;
    private DoorRestaurantMenu _doorDashRestaurantMenu;
}


