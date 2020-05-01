package com.xlbp.afridgetoofar.postmates;

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

public class PostActivity extends DeliveryAppBaseActivity
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
                _postmatesDeliveryDetails.onDocumentComplete();
                break;
            case MainMenuLoading:
                _postmatesDeliveryDetails = null;
                _postmatesMainMenu.onDocumentComplete();
                break;
            case RestaurantMenuLoading:
                _postmatesRestaurantMenu.onDocumentComplete();
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
            launchPostmates();
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

                _postmatesMainMenu.selectRestaurant();
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

        initPostmates();

        initWebView();
    }

    private void initView()
    {
        _view = new SearchingView(this);
        setContentView(_view);

        _view.setSelectedAppTextView("Postmates");

        _webView = _view.getWebView();

        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initPostmates()
    {
        AppState.setAppScreenState(AppScreenState.DeliveryDetailsLoading);

        Bundle extras = getIntent().getExtras();
        String searchAddress = extras.getString("SearchAddress");
        _isDebugMode = (extras != null) ? extras.getBoolean("DebugMode") : false;

        if (!_isDebugMode)
        {
            _webView.setTranslationX(Helpers.getScreenWidth());
        }

        _postmatesDeliveryDetails = new PostDeliveryDetails(this, _webView, searchAddress);
        _postmatesMainMenu = new PostMainMenu(this, _webView);
        _postmatesRestaurantMenu = new PostRestaurantMenu(this, _webView);
    }

    private void initWebView()
    {
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.setWebViewClient(new PostWebViewClient(this));
        _webView.setWebChromeClient(new WebChromeClient());

        // The follow two lines remove te webview's cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        _webView.loadUrl("https://postmates.com");
    }

    private void handleSearchComplete()
    {
        PostMainMenu.Restaurant selectedRestaurant = _postmatesMainMenu.getSelectedRestaurant();
        PostRestaurantMenu.FoodItem foodItem = _postmatesRestaurantMenu.getSelectedFoodItem();

        _view.setSearchCompleteText(foodItem.name, selectedRestaurant.name + "\n" + foodItem.price);

        Log.e("PostActivity", "Search Complete: selectedRestaurant - " + selectedRestaurant.name + " - food item - " + foodItem.name + " - food price - " + foodItem.price);

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

    private void launchPostmates()
    {
        String postPackageName = "com.postmates.android";
        boolean postmatesAppInstalled = Helpers.isAppInstalled(this, postPackageName);

        String url = postmatesAppInstalled ?
                _postmatesRestaurantMenu.getDeepLink() :
                _postmatesMainMenu.getSelectedRestaurant().href;

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

    private PostDeliveryDetails _postmatesDeliveryDetails;
    private PostMainMenu _postmatesMainMenu;
    private PostRestaurantMenu _postmatesRestaurantMenu;
}
