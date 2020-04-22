package com.xlbp.afridgetoofar.doordash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.DeliveryAppBaseActivity;
import com.xlbp.afridgetoofar.enums.AppScreenState;
import com.xlbp.afridgetoofar.enums.AppState;
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

    public void clickContinueInBrowser(View view)
    {
        _doorDashDeliveryDetails.clickContinueInBrowser();
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
        if (AppState.getAppScreenState() == AppScreenState.SearchComplete)
        {
            launchDoorDash();
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

                _doorDashMainMenu.selectRestaurant();
            });
        }
    }

    public void onDonateClicked(View view)
    {
        if (AppState.getAppScreenState() != AppScreenState.Animating)
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
        AppState.setAppScreenState(AppScreenState.DeliveryDetailsLoading);

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
//        _webView.setWebViewClient(new DoorWebViewClient(this));
//        _webView.setWebChromeClient(new WebChromeClient());
//        _webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");

//        _webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.132 Safari/537.36");

//        String newUA= "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
//        _webView.getSettings().setUserAgentString(newUA);

//        _webView.getSettings().setLoadWithOverviewMode(true);
//        _webView.getSettings().setUseWideViewPort(true);
//
//        _webView.getSettings().setSupportZoom(true);
//        _webView.getSettings().setBuiltInZoomControls(true);
//        _webView.getSettings().setDisplayZoomControls(false);
//
//        _webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        _webView.setScrollbarFadingEnabled(false);

//        // The follow two lines remove te webview's cookies
//        CookieManager.getInstance().removeAllCookies(null);
//        CookieManager.getInstance().flush();

        _webView.loadUrl("https://www.doordash.com/");
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

            AppState.setAppScreenState(AppScreenState.SearchComplete);
        });
    }

    private void launchDoorDash()
    {
        String doorPackageName = "com.dd.doordash";

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


