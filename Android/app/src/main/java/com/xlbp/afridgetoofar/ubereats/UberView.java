package com.xlbp.afridgetoofar.ubereats;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.UberAppState;
import com.xlbp.afridgetoofar.helpers.Animation;
import com.xlbp.afridgetoofar.helpers.Helpers;
import com.xlbp.afridgetoofar.R;

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

    public static void updateUberAppStateTextView(UberAppState appState)
    {
        _appStateTextView.setText("Current AppState - " + appState.toString());
    }

    public void setSearchCompleteText(String foodItem, String foodItemDetails)
    {
        _foodItemTextView.setText(foodItem);
        _foodItemDetailsTextView.setText(foodItemDetails);
    }

    public void animateSearchComplete(Runnable endAction)
    {
        AppState.setUberEatsAppState(UberAppState.Animating);

        // exit
        new Animation(_searchingTitleTextView)
                .alpha(0)
                .translationY(_searchingOffset)
                .start();

        _debugMessageTextView.setAlpha(0);

        _appStateTextView.setAlpha(0);

        // middle
        for (TextView searchAgainItem : _searchAgainItems)
        {
            new Animation(searchAgainItem)
                    .alpha(1)
                    .translationY(0)
                    .withEndAction(() -> searchAgainItem.setClickable(true))
                    .start();
        }

        // enter
        // we need to modify and reset _foodItemOffset because the textview heights are dynamic
        _foodItemOffset = -_foodItemTextView.getHeight() - _foodItemDetailsTextView.getHeight() - Helpers.topMargin;

        _foodItemTextView.setTranslationY(_foodItemOffset);
        _foodItemDetailsTextView.setTranslationY(_foodItemOffset);

        new Animation(_foodItemTextView)
                .alpha(1)
                .translationY(0)
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(1)
                .translationY(0)
                .withEndAction(endAction)
                .start();
    }

    public void animateSearchAgainAnimation(Runnable endAction)
    {
        AppState.setUberEatsAppState(UberAppState.Animating);

        // exit
        new Animation(_foodItemTextView)
                .alpha(0)
                .translationY(_foodItemOffset)
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(0)
                .translationY(_foodItemOffset)
                .withEndAction(endAction)
                .start();

        for (TextView searchAgainItem : _searchAgainItems)
        {
            searchAgainItem.setClickable(false);
        }

        new Animation(_viewOnTextView)
                .alpha(0)
                .translationY(_searchAgainArrayListOffset)
                .start();

        new Animation(_searchAgainTextView)
                .alpha(0)
                .translationY(_searchAgainArrayListOffset)
                .start();

        new Animation(_donateTextView)
                .alpha(0)
                .translationY(_searchAgainArrayListOffset)
                .start();

        // middle
        new Animation(_selectedAppTextView)
                .translationY(_selectedAppOffset)
                .start();

        // enter
        new Animation(_searchingTitleTextView)
                .alpha(1)
                .translationY(0)
                .withEndAction(endAction)
                .start();
    }

    public void animateNavigateBackAfterSearchComplete(Runnable endAction)
    {
        AppState.setUberEatsAppState(UberAppState.Animating);

        // exit
        new Animation(_foodItemTextView)
                .alpha(0)
                .translationY(_foodItemOffset)
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(0)
                .translationY(_foodItemOffset)
                .withEndAction(endAction)
                .start();

        for (TextView searchAgainItem : _searchAgainItems)
        {
            new Animation(searchAgainItem)
                    .alpha(0)
                    .translationY(_searchAgainArrayListOffset)
                    .start();
        }
    }

    private void init()
    {
        initViews();

        initViewAlphas();

        initViewPositions();

        initIsDebugMode();
    }

    private void initViews()
    {
        _activity.setContentView(R.layout.activity_searching);

        _searchingTitleTextView = _activity.findViewById(R.id.searchingTitleTextView);
        _debugMessageTextView = _activity.findViewById(R.id.debugMessageTextView);
        _appStateTextView = _activity.findViewById(R.id.appStateTextView);
        _webView = _activity.findViewById(R.id.webview);
        _foodItemTextView = _activity.findViewById(R.id.foodItemTextView);
        _foodItemDetailsTextView = _activity.findViewById(R.id.foodItemDetailsTextView);
        _selectedAppTextView = _activity.findViewById(R.id.selectedAppTextView);
        _viewOnTextView = _activity.findViewById(R.id.viewOnTextView);
        _searchAgainTextView = _activity.findViewById(R.id.searchAgainTextView);
        _donateTextView = _activity.findViewById(R.id.donateTextView);

        _searchAgainItems = new ArrayList<>();

        _searchAgainItems.add(_selectedAppTextView);
        _searchAgainItems.add(_viewOnTextView);
        _searchAgainItems.add(_searchAgainTextView);
        _searchAgainItems.add(_donateTextView);
    }

    private void initViewAlphas()
    {
        _foodItemTextView.setAlpha(0);
        _foodItemDetailsTextView.setAlpha(0);

        for (TextView searchAgainItem : _searchAgainItems)
        {
            searchAgainItem.setAlpha(0);
            searchAgainItem.setClickable(false);
        }

        _selectedAppTextView.setAlpha(1);
    }

    private void initViewPositions()
    {
        // this is needed so we only get positions/heights after onLayout has happened
        _activity.findViewById(R.id.layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                Helpers.adjustViewTopMarginForNotch(_searchingTitleTextView);
                Helpers.adjustViewTopMarginForNotch(_foodItemTextView);

                _selectedAppOffset = -_selectedAppTextView.getY() + _searchingTitleTextView.getHeight() + Helpers.topMargin - Helpers.dpToPixels(10);
                _searchingOffset = -_searchingTitleTextView.getHeight() - Helpers.topMargin;
                _foodItemOffset = -_foodItemDetailsTextView.getY() - _foodItemDetailsTextView.getHeight();
                _searchAgainArrayListOffset = Helpers.getScreenHeight() - _searchAgainItems.get(0).getY() + Helpers.topMargin + Helpers.dpToPixels(48);

                for (TextView searchAgainItem : _searchAgainItems)
                {
                    searchAgainItem.setTranslationY(_searchAgainArrayListOffset);
                }

                _selectedAppTextView.setTranslationY(_selectedAppOffset);
                _foodItemTextView.setTranslationY(_foodItemOffset);
                _foodItemDetailsTextView.setTranslationY(_foodItemOffset);

                // we need to remove the listener so positions are only set once
                _activity.findViewById(R.id.layout).getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
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
//            _webView.setAlpha(0);
            _webView.setTranslationX(Helpers.getScreenWidth());
//            _webView.setOnTouchListener((v, event) -> false);
        }
    }


    private UberActivity _activity;

    private TextView _searchingTitleTextView;
    private TextView _debugMessageTextView;
    private static TextView _appStateTextView;

    private WebView _webView;

    private TextView _foodItemTextView;
    private TextView _foodItemDetailsTextView;

    private ArrayList<TextView> _searchAgainItems;

    private TextView _selectedAppTextView;
    private TextView _viewOnTextView;
    private TextView _searchAgainTextView;
    private TextView _donateTextView;

    private float _selectedAppOffset;
    private float _searchingOffset;
    private float _foodItemOffset;
    private float _searchAgainArrayListOffset;
}
