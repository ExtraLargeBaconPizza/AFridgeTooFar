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
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.PostAppState;
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
        if (AppState.getPostmatesAppState() != PostAppState.Animating)
        {
            if (AppState.getPostmatesAppState() == PostAppState.SearchComplete)
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
        switch (AppState.getPostmatesAppState())
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
        if (AppState.getPostmatesAppState() == PostAppState.SearchComplete)
        {
            launchUberEats();
        }
    }

    public void onSearchAgainClicked(View view)
    {
        if (AppState.getPostmatesAppState() != PostAppState.Animating)
        {
            AppState.setMainScreenState(MainScreenState.SearchingApp);

            _view.animateSearchAgainAnimation(() ->
            {
                AppState.setPostmatesAppState(PostAppState.MainMenuReady);

                _postmatesMainMenu.selectRestaurant();
            });
        }
    }

    public void onDonateClicked(View view)
    {
        if (AppState.getPostmatesAppState() != PostAppState.Animating)
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
        _view = new PostView(this);
        setContentView(_view);

        _view.setSelectedAppTextView("Postmates");

        _webView = _view.getWebView();

        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initPostmates()
    {
        AppState.setPostmatesAppState(PostAppState.DeliveryDetailsLoading);

        Bundle extras = getIntent().getExtras();
        String searchAddress = extras.getString("SearchAddress");
        boolean isDebugMode = (extras != null) ? extras.getBoolean("DebugMode") : false;

        if (!isDebugMode)
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

        Log.e("UberView", "Search Complete: selectedRestaurant - " + selectedRestaurant.name + " - food item - " + foodItem.name + " - food price - " + foodItem.price);

        _view.animateSearchComplete(() ->
        {
            AppState.setMainScreenState(MainScreenState.SearchComplete);

            AppState.setPostmatesAppState(PostAppState.SearchComplete);
        });
    }

    private void launchUberEats()
    {
        String url = _postmatesMainMenu.getSelectedRestaurant().href;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        startActivity(browserIntent);
    }

    private void navigateBack()
    {
        _webView.destroy();

        finish();
        return;
    }


    private PostView _view;
    private WebView _webView;

    private PostDeliveryDetails _postmatesDeliveryDetails;
    private PostMainMenu _postmatesMainMenu;
    private PostRestaurantMenu _postmatesRestaurantMenu;
}
