package com.xlbp.afridgetoofar.ubereats;

import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

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

        if (isDebugMode)
        {
            // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
            _activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);=
        }
        else
        {
            // The webView doesn't work correctly if we set it visibility to invisible or gone
            _webView.setAlpha(0);
            _webView.setOnTouchListener((v, event) -> false);
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
    private TextView _rerollFoodTextView;
    private TextView _visitRestaurantTextView;
    private TextView _rerollTextView;
}
