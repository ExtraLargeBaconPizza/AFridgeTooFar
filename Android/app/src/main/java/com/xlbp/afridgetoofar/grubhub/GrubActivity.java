package com.xlbp.afridgetoofar.grubhub;

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
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.GrubAppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
import com.xlbp.afridgetoofar.helpers.Helpers;

public class GrubActivity extends DeliveryAppBaseActivity
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
        if (AppState.getGrubhubAppState() != GrubAppState.Animating)
        {
            if (AppState.getGrubhubAppState() == GrubAppState.SearchComplete)
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
        switch (AppState.getGrubhubAppState())
        {
            case DeliveryDetailsLoading:
                _grubhubDeliveryDetails.onDocumentComplete();
                break;
            case MainMenuLoading:
                _grubhubDeliveryDetails = null;
                _grubhubMainMenu.onDocumentComplete();
                break;
            case RestaurantMenuLoading:
                _grubhubRestaurantMenu.onDocumentComplete();
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
        if (AppState.getGrubhubAppState() == GrubAppState.SearchComplete)
        {
            launchGrubhub();
        }
    }

    public void onSearchAgainClicked(View view)
    {
        if (AppState.getGrubhubAppState() != GrubAppState.Animating)
        {
            AppState.setMainScreenState(MainScreenState.SearchingApp);

            _view.animateSearchAgainAnimation(() ->
            {
                AppState.setGrubhubAppState(GrubAppState.MainMenuReady);

                _grubhubMainMenu.selectRestaurant();
            });
        }
    }

    public void onDonateClicked(View view)
    {
        if (AppState.getGrubhubAppState() != GrubAppState.Animating)
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

        initGrubhub();

        initWebView();
    }

    private void initView()
    {
        _view = new GrubView(this);
        setContentView(_view);

        _view.setSelectedAppTextView("Grubhub");

        _webView = _view.getWebView();

        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initGrubhub()
    {
        AppState.setGrubhubAppState(GrubAppState.DeliveryDetailsLoading);

        Bundle extras = getIntent().getExtras();
        String searchAddress = extras.getString("SearchAddress");
        boolean isDebugMode = (extras != null) ? extras.getBoolean("DebugMode") : false;

        if (!isDebugMode)
        {
            _webView.setTranslationX(Helpers.getScreenWidth());
        }

        _grubhubDeliveryDetails = new GrubDeliveryDetails(this, _webView, searchAddress);
        _grubhubMainMenu = new GrubMainMenu(this, _webView);
        _grubhubRestaurantMenu = new GrubRestaurantMenu(this, _webView);
    }

    private void initWebView()
    {
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.setWebViewClient(new GrubWebViewClient(this));
        _webView.setWebChromeClient(new WebChromeClient());

        // The follow two lines remove te webview's cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        _webView.loadUrl("https://www.grubhub.com/search?orderMethod=delivery&locationMode=DELIVERY&facetSet=umamiV2&pageSize=20&hideHateos=true&searchMetrics=true&facet=open_now%3Atrue&sortSetId=umamiv3&countOmittingTimes=true");
    }

    private void handleSearchComplete()
    {
        GrubMainMenu.Restaurant selectedRestaurant = _grubhubMainMenu.getSelectedRestaurant();
        GrubRestaurantMenu.FoodItem foodItem = _grubhubRestaurantMenu.getSelectedFoodItem();

        _view.setSearchCompleteText(foodItem.name, selectedRestaurant.name + "\n" + foodItem.price);

        Log.e("GrubActivity", "Search Complete: selectedRestaurant - " + selectedRestaurant.name + " - food item - " + foodItem.name + " - food price - " + foodItem.price);

        _view.animateSearchComplete(() ->
        {
            AppState.setMainScreenState(MainScreenState.SearchComplete);

            AppState.setGrubhubAppState(GrubAppState.SearchComplete);
        });
    }

    private void launchGrubhub()
    {
        String grubPackageName = "com.grubhub.android";
        boolean grubhubAppInstalled = Helpers.isAppInstalled(this, grubPackageName);

        String url = _grubhubMainMenu.getSelectedRestaurant().href;

        if (grubhubAppInstalled)
        {
            url.replace("https://www.grubhub.com/", "grubhubapp://");
        }

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


    private GrubView _view;
    private WebView _webView;

    private GrubDeliveryDetails _grubhubDeliveryDetails;
    private GrubMainMenu _grubhubMainMenu;
    private GrubRestaurantMenu _grubhubRestaurantMenu;
}
