package com.xlbp.afridgetoofar.ubereats;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.xlbp.afridgetoofar.helpers.Animation;
import com.xlbp.afridgetoofar.AppState;
import com.xlbp.afridgetoofar.helpers.Helpers;
import com.xlbp.afridgetoofar.R;
import com.xlbp.afridgetoofar.enums.UberAppState;

import java.util.ArrayList;

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

    public View getLayout()
    {
        return _activity.findViewById(R.id.layout);
    }

    public void setSubtitleTextView(String text)
    {
        _searchingSubtitleTextView.setText(text);
    }

    public static void updateUberAppStateTextView(UberAppState appState)
    {
        _appStateTextView.setText("Current AppState - " + appState.toString());
    }

    public void setSearchCompleteText(String selectedRestaurantName, String foodItemName, String foodItemPrice)
    {
        Log.e("UberView", "Search Complete: selectedRestaurant - " + selectedRestaurantName + " - food item - " + foodItemName);

        _foodItemTextView.setText(foodItemName);
        _foodItemDetailsTextView.setText(selectedRestaurantName + "\n" + foodItemPrice);
    }

    public void animateSearchComplete(Runnable endAction)
    {
        AppState.setUberEatsAppState(UberAppState.Animating);

        if (!_animationPositionsSet)
        {
            _animationPositionsSet = true;

            initAnimationPositions();
        }

        //exit
        new Animation(_searchingTitleTextView)
                .alpha(0)
                .translationY(_titleOffset)
                .start();

        new Animation(_searchingSubtitleTextView)
                .alpha(0)
                .translationY(_titleOffset)
                .start();

        new Animation(_debugMessageTextView)
                .alpha(0)
                .start();

        new Animation(_appStateTextView)
                .alpha(0)
                .start();

        // enter
        new Animation(_foodItemTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(600)
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(600)
                .withEndAction(endAction)
                .start();

        for (TextView searchAgainItem : _searchAgainItems)
        {
            new Animation(searchAgainItem)
                    .alpha(1)
                    .translationY(0)
                    .startDelay(600)
                    .start();
        }
    }

    public void animateSearchSameRestaurantAnimation(Runnable endAction)
    {
        AppState.setUberEatsAppState(UberAppState.Animating);

        // exit
        new Animation(_foodItemTextView)
                .alpha(0)
                .translationY(-_foodItemTextView.getY() - _foodItemTextView.getHeight() - _foodItemDetailsTextView.getHeight())
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(0)
                .translationY(-_foodItemDetailsTextView.getY() - _foodItemDetailsTextView.getHeight())
                .withEndAction(endAction)
                .start();

        // middle
        for (TextView searchAgainItem : _searchAgainItems)
        {
            if (searchAgainItem.getId() != _sameRestaurantTextView.getId())
            {
                new Animation(searchAgainItem)
                        .alpha(0)
                        .translationY(_searchAgainOffset)
                        .start();
            }
            else
            {
                new Animation(searchAgainItem)
                        .translationY(-searchAgainItem.getY() + _searchingTitleTextView.getHeight() + Helpers.dpToPixels(64))
                        .start();
            }
        }

        // enter
        new Animation(_searchingTitleTextView)
                .alpha(1)
                .translationY(0)
                .withEndAction(endAction)
                .start();
    }

    public void animateSearchSameAppAnimation(Runnable endAction)
    {
        AppState.setUberEatsAppState(UberAppState.Animating);

        // exit
        new Animation(_foodItemTextView)
                .alpha(0)
                .translationY(-_foodItemTextView.getY() - _foodItemTextView.getHeight() - _foodItemDetailsTextView.getHeight())
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(0)
                .translationY(-_foodItemDetailsTextView.getY() - _foodItemDetailsTextView.getHeight())
                .start();

        // middle
        for (TextView searchAgainItem : _searchAgainItems)
        {
            if (searchAgainItem.getId() != _sameAppTextView.getId())
            {
                new Animation(searchAgainItem)
                        .alpha(0)
                        .translationY(_searchAgainOffset)
                        .start();
            }
            else
            {
                new Animation(searchAgainItem)
                        .translationY(-searchAgainItem.getY() + _searchingTitleTextView.getHeight() + Helpers.dpToPixels(64))
                        .start();
            }
        }

        // enter
        new Animation(_searchingTitleTextView)
                .alpha(1)
                .translationY(0)
                .withEndAction(endAction)
                .start();
    }

    public void animateSameSearchCompleteAnimation(Runnable endAction)
    {
        AppState.setUberEatsAppState(UberAppState.Animating);

        //exit
        new Animation(_searchingTitleTextView)
                .alpha(0)
                .translationY(_titleOffset)
                .start();

        //enter
        new Animation(_foodItemTextView)
                .alpha(1)
                .translationY(0)
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(1)
                .translationY(0)
                .withEndAction(endAction)
                .start();

        boolean endActionRun = false;

        for (TextView searchAgainItem : _searchAgainItems)
        {
            Runnable endAction2 = endActionRun ? null : endAction;
            endActionRun = true;

            if (searchAgainItem.getTranslationY() < 0)
            {
                new Animation(searchAgainItem)
                        .translationY(0)
                        .start();
            }
            else
            {
                searchAgainItem.setTranslationY(0);

                new Animation(searchAgainItem)
                        .alpha(1)
                        .startDelay(600)
                        .withEndAction(endAction2)
                        .start();
            }

        }
    }

    public void animateNavigateBackAfterSearchComplete(Runnable endAction)
    {
        AppState.setUberEatsAppState(UberAppState.Animating);

        // exit
        new Animation(_foodItemTextView)
                .alpha(0)
                .translationY(-_foodItemTextView.getY() - _foodItemTextView.getHeight() - _foodItemDetailsTextView.getHeight())
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(0)
                .translationY(-_foodItemDetailsTextView.getY() - _foodItemDetailsTextView.getHeight())
                .withEndAction(endAction)
                .start();

        for (TextView searchAgainItem : _searchAgainItems)
        {
            new Animation(searchAgainItem)
                    .alpha(0)
                    .translationY(_searchAgainOffset)
                    .start();
        }
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

        TextView searchAgain = _activity.findViewById(R.id.searchTextView);
        _sameRestaurantTextView = _activity.findViewById(R.id.sameRestaurantTextView);
        _sameAppTextView = _activity.findViewById(R.id.sameAppTextView);
        TextView differentAppTextView = _activity.findViewById(R.id.differentAppTextView);

        _searchAgainItems = new ArrayList<>();

        _searchAgainItems.add(searchAgain);
        _searchAgainItems.add(_sameRestaurantTextView);
        _searchAgainItems.add(_sameAppTextView);
        _searchAgainItems.add(differentAppTextView);

        _foodItemTextView.setAlpha(0);
        _foodItemDetailsTextView.setAlpha(0);

        for (TextView searchAgainItem : _searchAgainItems)
        {
            searchAgainItem.setAlpha(0);
            searchAgainItem.setClickable(false);
        }
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
            // So we just set its alpha to 0 and remove it touch listeners. move it offscreen as well just in case
            _webView.setAlpha(0);
            _webView.setTranslationX(Helpers.getScreenWidth());
            _webView.setOnTouchListener((v, event) -> false);
        }
    }

    private void initAnimationPositions()
    {
        _titleOffset = -_searchingSubtitleTextView.getY() - _searchingSubtitleTextView.getHeight();

        _foodItemTextView.setTranslationY(_foodItemTextView.getHeight() - _foodItemDetailsTextView.getHeight() - Helpers.dpToPixels(Helpers.topMargin));
        _foodItemDetailsTextView.setTranslationY(_foodItemDetailsTextView.getHeight() - Helpers.dpToPixels(Helpers.topMargin));

        _searchAgainOffset = Helpers.getScreenHeight() - _searchAgainItems.get(0).getY() + Helpers.dpToPixels(48);

        for (TextView searchAgainItem : _searchAgainItems)
        {
            searchAgainItem.setTranslationY(_searchAgainOffset);
            searchAgainItem.setClickable(true);
        }
    }


    private UberActivity _activity;

    private TextView _searchingTitleTextView;
    private TextView _searchingSubtitleTextView;
    private TextView _debugMessageTextView;
    private static TextView _appStateTextView;

    private WebView _webView;

    private TextView _foodItemTextView;
    private TextView _foodItemDetailsTextView;

    private ArrayList<TextView> _searchAgainItems;

    private TextView _sameRestaurantTextView;
    private TextView _sameAppTextView;
    private boolean _animationPositionsSet;
    private float _titleOffset;
    private float _searchAgainOffset;
}
