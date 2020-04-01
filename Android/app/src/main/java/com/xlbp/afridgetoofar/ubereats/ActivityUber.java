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
    protected void onResume()
    {
        super.onResume();

        initAnimationPositions();
    }

    @Override
    public void onBackPressed()
    {
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

        _searchCompleteTitleTextView = findViewById(R.id.searchCompleteTitleTextView);
        _foodItemTextView = findViewById(R.id.foodItemTextView);
        _rerollFoodTextView = findViewById(R.id.rerollFoodItemTextView);
        _visitRestaurantTextView = findViewById(R.id.visitRestaurantTextView);
        _rerollTextView = findViewById(R.id.rerollTextView);

        _searchCompleteTitleTextView.setAlpha(0);
        _foodItemTextView.setAlpha(0);
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

    private void initAnimationPositions()
    {
        _searchCompleteTitleTextView.setTranslationY(_searchCompleteTitleTextView.getHeight() - Helpers.dpToPixels(ActivityMain.f_topMargin));

        float offset = Helpers.getScreenHeight() - _foodItemTextView.getY() + Helpers.dpToPixels(48);

        _foodItemTextView.setTranslationY(offset);
        _rerollFoodTextView.setTranslationY(offset);
        _visitRestaurantTextView.setTranslationY(offset);
        _rerollTextView.setTranslationY(offset);
    }

    private void handleSearchComplete()
    {
        // TODO add a "Pick One" or "Choose an Item" subtitle to the xml
        UberMainMenu.Restaurant selectedRestaurant = _uberEatsMainMenu.getSelectedRestaurant();
        UberRestaurantMenu.FoodItem foodItem = _uberEatsRestaurantMenu.getSelectedFoodItem();

        Log.e("UberActivity", "Search Complete: selectedRestaurant - " + selectedRestaurant.name + " food item - " + foodItem.name);

        _foodItemTextView.setText(foodItem.name + "\n" + selectedRestaurant.name + "\n" + foodItem.price);
        _rerollFoodTextView.setText("Different Item From Restaurant");
        _visitRestaurantTextView.setText("See Menu");
        _rerollTextView.setText("Restart");

        animateComplete();
    }

    private void animateComplete()
    {
        //exit
        float translationY = -_searchingSubtitleTextView.getY() - _searchingSubtitleTextView.getHeight();

        _searchingTitleTextView.animate()
                .translationYBy(translationY)
                .alpha(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _searchingSubtitleTextView.animate()
                .translationYBy(translationY)
                .alpha(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _debugMessageTextView.animate()
                .alpha(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _appStateTextView.animate()
                .alpha(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        // enter
        _searchCompleteTitleTextView.animate()
                .alpha(1)
                .translationY(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _foodItemTextView.animate()
                .alpha(1)
                .translationY(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _rerollFoodTextView.animate()
                .alpha(1)
                .translationY(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _visitRestaurantTextView.animate()
                .alpha(1)
                .translationY(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _rerollTextView.animate()
                .alpha(1)
                .translationY(0)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));
    }

    private void rerollFoodItemAnimation()
    {
        //enter
        float translationY = -_searchingSubtitleTextView.getY() - _searchingSubtitleTextView.getHeight();

        _searchingTitleTextView.animate()
                .translationY(0)
                .alpha(1)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _searchingSubtitleTextView.animate()
                .translationY(0)
                .alpha(1)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _debugMessageTextView.animate()
                .alpha(1)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _appStateTextView.animate()
                .alpha(1)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        // exit
        _searchCompleteTitleTextView.animate()
                .alpha(0)
                .translationY(-_searchCompleteTitleTextView.getY() - _searchCompleteTitleTextView.getHeight())
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        float offset = Helpers.getScreenHeight() - _foodItemTextView.getY() + Helpers.dpToPixels(48);

        _foodItemTextView.animate()
                .alpha(0)
                .translationY(offset)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _rerollFoodTextView.animate()
                .alpha(0)
                .translationY(offset)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _visitRestaurantTextView.animate()
                .alpha(0)
                .translationY(offset)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));

        _rerollTextView.animate()
                .alpha(0)
                .translationY(offset)
                .setDuration(ActivityMain.f_animTime)
                .setInterpolator(new DecelerateInterpolator(ActivityMain.f_animationFactor));
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

    private TextView _searchCompleteTitleTextView;
    private TextView _foodItemTextView;
    private TextView _rerollFoodTextView;
    private TextView _visitRestaurantTextView;
    private TextView _rerollTextView;

    private UberInitial _uberEatsInitial;
    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
}
