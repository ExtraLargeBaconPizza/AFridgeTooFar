package com.xlbp.afridgetoofar.grubhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xlbp.afridgetoofar.R;
import com.xlbp.afridgetoofar.SearchingAnimationView;
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.GrubAppState;
import com.xlbp.afridgetoofar.helpers.Animation;
import com.xlbp.afridgetoofar.helpers.Helpers;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;

public class GrubView extends FrameLayout
{
    public GrubView(Context context)
    {
        super(context);

        init();
    }

    public WebView getWebView()
    {
        return _webView;
    }

    public void setSelectedAppTextView(String selectedApp)
    {
        _selectedAppTextView.setText(selectedApp);
    }

    public void setSearchCompleteText(String foodItem, String foodItemDetails)
    {
        _foodItemTextView.setText(foodItem);
        _foodItemDetailsTextView.setText(foodItemDetails);
    }

    public void animateSearchComplete(Runnable endAction)
    {
        AppState.setGrubhubAppState(GrubAppState.Animating);

        // exit
        new Animation(_searchingTitleTextView)
                .alpha(0)
                .translationY(_searchingOffset)
                .start();

        _searchingAnimationView.stopAnimation();

        new Animation(_searchingAnimationView)
                .alpha(0)
                .start();

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
        AppState.setGrubhubAppState(GrubAppState.Animating);

        // exit
        new Animation(_foodItemTextView)
                .alpha(0)
                .translationY(_foodItemOffset)
                .start();

        new Animation(_foodItemDetailsTextView)
                .alpha(0)
                .translationY(_foodItemOffset)
                .start();

        for (TextView searchAgainItem : _searchAgainItems)
        {
            searchAgainItem.setClickable(false);

            if (searchAgainItem != _selectedAppTextView)
            {
                new Animation(searchAgainItem)
                        .alpha(0)
                        .translationY(_searchAgainArrayListOffset)
                        .start();
            }
        }

        // middle
        new Animation(_selectedAppTextView)
                .translationY(_selectedAppOffset)
                .withEndAction(() ->
                {
                    _searchingAnimationView.setAlpha(1);
                    _searchingAnimationView.startAnimation();
                })
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
        AppState.setGrubhubAppState(GrubAppState.Animating);

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

        _searchingAnimationView.startAnimation();
    }

    private void initViews()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_searching, this, true);

        _searchingTitleTextView = findViewById(R.id.searchingTitleTextView);
        _searchingAnimationView = findViewById(R.id.searchingAnimationView);
        _webView = findViewById(R.id.webview);
        _foodItemTextView = findViewById(R.id.foodItemTextView);
        _foodItemDetailsTextView = findViewById(R.id.foodItemDetailsTextView);
        _selectedAppTextView = findViewById(R.id.selectedAppTextView);
        _viewOnTextView = findViewById(R.id.viewOnTextView);
        _searchAgainTextView = findViewById(R.id.searchAgainTextView);
        _donateTextView = findViewById(R.id.donateTextView);

        _searchAgainItems = new ArrayList<>();

        _searchAgainItems.add(_selectedAppTextView);
        _searchAgainItems.add(_viewOnTextView);
        _searchAgainItems.add(_searchAgainTextView);
        _searchAgainItems.add(_donateTextView);

        Helpers.adjustViewTopMarginForNotch(_searchingTitleTextView);
        Helpers.adjustViewTopMarginForNotch(_foodItemTextView);
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
        ConstraintLayout layout = findViewById(R.id.layout);

        // this is needed so we only get positions/heights after onLayout has happened
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
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
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }


    private TextView _searchingTitleTextView;
    private SearchingAnimationView _searchingAnimationView;

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
