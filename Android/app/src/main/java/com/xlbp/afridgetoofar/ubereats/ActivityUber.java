package com.xlbp.afridgetoofar.ubereats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.xlbp.afridgetoofar.ActivityMain;
import com.xlbp.afridgetoofar.Animation;
import com.xlbp.afridgetoofar.Helpers;
import com.xlbp.afridgetoofar.R;

public class ActivityUber extends AppCompatActivity
{
    public static String uberEatsUrl = "https://www.ubereats.com";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        init();
    }

    @Override
    public void onBackPressed()
    {
        _webView.destroy();

        finish();
        return;
    }

    public static void updateAppStateTextView(UberAppState appState)
    {
        _appStateTextView.setText("\nCurrent AppState: " + appState);
    }

    public void webViewLoadUrl(String url)
    {
        _webView.loadUrl(url);
    }

    public void onDocumentComplete()
    {
        switch (AppState.getUberEatsAppState())
        {
            case InitialLoading:
                _uberEatsInitial.onDocumentComplete();
                break;
            case DeliveryDetailsLoading:
                _uberEatsDeliveryDetails.onDocumentComplete();
                break;
            case MainMenuLoading:
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

    public void onRerollFoodItemClicked(View view)
    {
        _searchingSubtitleTextView.setText(_uberEatsMainMenu.getSelectedRestaurant().name);

        rerollFoodItemAnimation();

        new Handler().postDelayed(() ->
        {
            _uberEatsRestaurantMenu.onDocumentComplete();
        }, 3000);
    }

    private void init()
    {
        initIsDebugMode();

        if (!_isDebugMode)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initViews();

        initWebView();

        initUberEats();
    }

    private void initIsDebugMode()
    {
        Bundle extras = getIntent().getExtras();

        _isDebugMode = (extras != null) ? _isDebugMode = extras.getBoolean("DebugMode") : false;
    }

    private void initViews()
    {
        _searchingTitleTextView = findViewById(R.id.searchingTitleTextView);
        _searchingSubtitleTextView = findViewById(R.id.searchingSubtitleTextView);
        _debugMessageTextView = findViewById(R.id.debugMessageTextView);
        _appStateTextView = findViewById(R.id.appStateTextView);

        _webView = findViewById(R.id.webview);

        _foodItemTextView = findViewById(R.id.foodItemTextView);
        _foodItemDetailsTextView = findViewById(R.id.foodItemDetailsTextView);
        _rerollFoodTextView = findViewById(R.id.rerollFoodItemTextView);
        _visitRestaurantTextView = findViewById(R.id.visitRestaurantTextView);
        _rerollTextView = findViewById(R.id.rerollTextView);

        _foodItemTextView.setAlpha(0);
        _foodItemDetailsTextView.setAlpha(0);
        _rerollFoodTextView.setAlpha(0);
        _visitRestaurantTextView.setAlpha(0);
        _rerollTextView.setAlpha(0);
    }

    private void initUberEats()
    {
        AppState.setUberEatsAppState(UberAppState.InitialLoading);

        _uberEatsInitial = new UberInitial(this, _webView);
        _uberEatsDeliveryDetails = new UberDeliveryDetails(this, _webView);
        _uberEatsMainMenu = new UberMainMenu(this, _webView);
        _uberEatsRestaurantMenu = new UberRestaurantMenu(this, _webView);
    }

    private void initWebView()
    {
        if (!_isDebugMode)
        {
            // The webView doesn't seem to work correctly if we set it visibility to invisible or gone
            _webView.setAlpha(0);
            _webView.setOnTouchListener((v, event) -> false);
        }

        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.setWebViewClient(new UberWebViewClient(this));
        _webView.setWebChromeClient(new WebChromeClient());

        // TODO @jim the follow two lines remove uber eats cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        webViewLoadUrl(uberEatsUrl);
    }

    private void handleSearchComplete()
    {
        // TODO add a "Pick One" or "Choose an Item" subtitle to the xml
        UberMainMenu.Restaurant selectedRestaurant = _uberEatsMainMenu.getSelectedRestaurant();
        UberRestaurantMenu.FoodItem foodItem = _uberEatsRestaurantMenu.getSelectedFoodItem();

        Log.e("UberActivity", "Search Complete: selectedRestaurant - " + selectedRestaurant.name + " - food item - " + foodItem.name);

        _foodItemTextView.setText(foodItem.name);
        _foodItemDetailsTextView.setText(selectedRestaurant.name + "\n" + foodItem.price + " • Select ->");
        _rerollFoodTextView.setText("Different Item From Restaurant");
        _visitRestaurantTextView.setText("See Menu");
        _rerollTextView.setText("Restart");

        if (_offset == 0.0f)
        {
            initAnimationPositions();
        }

        animateSearchCompleteExit();
    }

    private void initAnimationPositions()
    {
        _foodItemTextView.setTranslationY(_foodItemTextView.getHeight() - _foodItemDetailsTextView.getHeight() - Helpers.dpToPixels(ActivityMain.f_topMargin));
        _foodItemDetailsTextView.setTranslationY(_foodItemDetailsTextView.getHeight() - Helpers.dpToPixels(ActivityMain.f_topMargin));

        _offset = Helpers.getScreenHeight() - _rerollFoodTextView.getY() + Helpers.dpToPixels(48);

        _rerollFoodTextView.setTranslationY(_offset);
        _visitRestaurantTextView.setTranslationY(_offset);
        _rerollTextView.setTranslationY(_offset);
    }

    private void animateSearchCompleteExit()
    {
        //exit
        float offset = -_searchingSubtitleTextView.getY() - _searchingSubtitleTextView.getHeight();

        new Animation(_searchingTitleTextView)
                .alpha(0)
                .translationY(_searchingTitleTextView.getTranslationY() + offset)
                .start();

        new Animation(_searchingSubtitleTextView)
                .alpha(0)
                .translationY(_searchingSubtitleTextView.getTranslationY() + offset)
                .start();

        new Animation(_debugMessageTextView)
                .alpha(0);

        new Animation(_appStateTextView)
                .alpha(0)
                .start();

        // enter
        int enterDelay = 400;

        new Animation(_foodItemTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(enterDelay)
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(enterDelay)
                .start();

        new Animation(_rerollFoodTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(enterDelay)
                .start();

        new Animation(_visitRestaurantTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(enterDelay)
                .start();

        new Animation(_rerollTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(enterDelay)
                .start();
    }

    private void rerollFoodItemAnimation()
    {
        // exit
        new Animation(_foodItemTextView)
                .alpha(0)
                .translationY(-_foodItemTextView.getY() - _foodItemTextView.getHeight() - _foodItemDetailsTextView.getHeight())
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(0)
                .translationY(-_foodItemDetailsTextView.getY() - _foodItemDetailsTextView.getHeight())
                .start();

        new Animation(_rerollFoodTextView)
                .alpha(0)
                .translationY(_offset)
                .start();

        new Animation(_visitRestaurantTextView)
                .alpha(0)
                .translationY(_offset)
                .start();

        new Animation(_rerollTextView)
                .alpha(0)
                .translationY(_offset)
                .start();

        // enter
        new Animation(_searchingTitleTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(400)
                .start();

        new Animation(_searchingSubtitleTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(400)
                .start();
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


    private boolean _isDebugMode;

    private TextView _searchingTitleTextView;
    private TextView _searchingSubtitleTextView;
    private TextView _debugMessageTextView;
    private static TextView _appStateTextView;

    private WebView _webView;

    private TextView _foodItemTextView;
    private TextView _foodItemDetailsTextView;
    private TextView _rerollFoodTextView;
    private TextView _visitRestaurantTextView;
    private TextView _rerollTextView;

    private float _offset;

    private UberInitial _uberEatsInitial;
    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
}
