package com.xlbp.afridgetoofar.ubereats;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.xlbp.afridgetoofar.Animation;
import com.xlbp.afridgetoofar.Helpers;
import com.xlbp.afridgetoofar.R;

public class UberView
{
    public UberView(UberActivity uberActivity)
    {
        _activity = uberActivity;

        init();
    }

    public WebView getWebView()
    {
        return _webView;
    }

    public static void updateUberAppStateTextView(UberAppState appState)
    {
        _appStateTextView.setText(appState.toString());
    }

    public void setSearchCompleteText(String selectedRestaurantName, String foodItemName, String foodItemPrice)
    {
        Log.e("UberView", "Search Complete: selectedRestaurant - " + selectedRestaurantName + " - food item - " + foodItemName);

        // view set shit
        _foodItemTextView.setText(foodItemName);
        _foodItemDetailsTextView.setText(selectedRestaurantName + "\n" + foodItemPrice + " • View in Uber App");
        _rerollFoodTextView.setText("Different Item From Restaurant");
        _visitRestaurantTextView.setText("See Menu");
        _rerollTextView.setText("Restart");
    }

    public void animateSearchComplete()
    {
        if (!_animationPositionsSet)
        {
            _animationPositionsSet = true;

            initAnimationPositions();
        }

        //exit
        float topOffset = -_searchingSubtitleTextView.getY() - _searchingSubtitleTextView.getHeight();

        new Animation(_searchingTitleTextView)
                .alpha(0)
                .translationY(_searchingTitleTextView.getTranslationY() + topOffset)
                .start();

        new Animation(_searchingSubtitleTextView)
                .alpha(0)
                .translationY(_searchingSubtitleTextView.getTranslationY() + topOffset)
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

    private void init()
    {
        _activity.setContentView(R.layout.activity_searching);

        initViews();

        initIsDebugMode();
    }

    private void initViews()
    {
        _searchingTitleTextView = _activity.findViewById(R.id.searchingTitleTextView);
        _searchingSubtitleTextView = _activity.findViewById(R.id.searchingSubtitleTextView);
        _debugMessageTextView = _activity.findViewById(R.id.debugMessageTextView);
        _appStateTextView = _activity.findViewById(R.id.appStateTextView);

        _webView = _activity.findViewById(R.id.webview);

        _foodItemTextView = _activity.findViewById(R.id.foodItemTextView);
        _foodItemDetailsTextView = _activity.findViewById(R.id.foodItemDetailsTextView);
        _rerollFoodTextView = _activity.findViewById(R.id.rerollFoodItemTextView);
        _visitRestaurantTextView = _activity.findViewById(R.id.visitRestaurantTextView);
        _rerollTextView = _activity.findViewById(R.id.rerollTextView);

        _foodItemTextView.setAlpha(0);
        _foodItemDetailsTextView.setAlpha(0);
        _rerollFoodTextView.setAlpha(0);
        _visitRestaurantTextView.setAlpha(0);
        _rerollTextView.setAlpha(0);
    }

    private void initIsDebugMode()
    {
        Bundle extras = _activity.getIntent().getExtras();

        boolean isDebugMode = (extras != null) ? extras.getBoolean("DebugMode") : false;

        if (!isDebugMode)
        {
            // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
            _activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            // The webView doesn't work correctly if its visibility is invisible or gone.
            // So we just set its alpha to 0 and remove it touch listeners
            _webView.setAlpha(0);
            _webView.setOnTouchListener((v, event) -> false);
        }
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


    private UberActivity _activity;

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

    private boolean _animationPositionsSet;
    private float _offset;
}
