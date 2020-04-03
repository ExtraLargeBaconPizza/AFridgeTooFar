package com.xlbp.afridgetoofar.ubereats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.xlbp.afridgetoofar.AppState;
import com.xlbp.afridgetoofar.Animation;
import com.xlbp.afridgetoofar.Helpers;
import com.xlbp.afridgetoofar.R;
import com.xlbp.afridgetoofar.XlbpWebViewClient;

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
    public void onBackPressed()
    {
        _view.getWebView().destroy();

        finish();
        return;
    }

    public static void updateAppStateTextView(UberAppState appState)
    {
//        _appStateTextView.setText("\nCurrent AppState: " + appState);
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

    // TODO - figure out what to do in this situation? currently we can just instantly reroll
    public void onRerollFoodItemClicked(View view)
    {
//        _searchingSubtitleTextView.setText(_uberEatsMainMenu.getSelectedRestaurant().name);
//
//        rerollFoodItemAnimation();
//
//        new Handler().postDelayed(() ->
//        {
//            _uberEatsRestaurantMenu.onDocumentComplete();
//        }, 3000);
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
        _view.getWebView().setWebViewClient(new XlbpWebViewClient(this));
        _view.getWebView().setWebChromeClient(new WebChromeClient());

        // The follow two lines remove uber eats cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        _view.getWebView().loadUrl(uberEatsUrl);
    }

    private void initUberEats()
    {
        AppState.setUberEatsAppState(UberAppState.HomePageLoading);

        _uberEatsInitial = new UberHomePage(_view.getWebView());
        _uberEatsDeliveryDetails = new UberDeliveryDetails(this, _view.getWebView());
        _uberEatsMainMenu = new UberMainMenu(_view.getWebView());
        _uberEatsRestaurantMenu = new UberRestaurantMenu(this, _view.getWebView());
    }

    private void handleSearchComplete()
    {
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
        _foodItemTextView.setTranslationY(_foodItemTextView.getHeight() - _foodItemDetailsTextView.getHeight() - Helpers.dpToPixels(Helpers.topMargin));
        _foodItemDetailsTextView.setTranslationY(_foodItemDetailsTextView.getHeight() - Helpers.dpToPixels(Helpers.topMargin));

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


    private UberView _view;

    private float _offset;

    private UberHomePage _uberEatsInitial;
    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
}
