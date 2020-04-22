package com.xlbp.afridgetoofar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
import com.xlbp.afridgetoofar.helpers.Animation;
import com.xlbp.afridgetoofar.helpers.Helpers;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MainView extends FrameLayout
{
    public MainView(Context context)
    {
        super(context);

        init();
    }

    public AutoCompleteTextView getAutoCompleteTextView()
    {
        return _autoCompleteTextView;
    }

    public void initInstalledApps(boolean[] isAppInstalled)
    {
        for (int i = 0; i < isAppInstalled.length; i++)
        {
            if (!isAppInstalled[i])
            {
                _installedApps.get(i).setVisibility(GONE);
            }
        }
    }

    public void animateAutoCompleteTextViewUp(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        new Animation(_titleTextView)
                .alpha(0)
                .translationY(_titleOffset)
                .start();

        new Animation(_autoCompleteTextView)
                .translationY(_autoCompleteOffset1)
                .withEndAction(endAction)
                .start();
    }

    public void animateAutoCompleteTextViewDown(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        new Animation(_titleTextView)
                .alpha(1)
                .translationY(0)
                .start();

        new Animation(_autoCompleteTextView)
                .translationY(0)
                .withEndAction(endAction)
                .start();
    }

    public void animateInstalledAppsOnToScreen(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        for (TextView installedApp : _installedApps)
        {
            new Animation(installedApp)
                    .alpha(1)
                    .translationY(0)
                    .startDelay(600)
                    .withEndAction(endAction)
                    .start();
        }
    }

    public void animateInstalledAppOffScreen(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        for (TextView installedApp : _installedApps)
        {
            new Animation(installedApp)
                    .alpha(0)
                    .translationY(_installedAppsLinearLayoutOffset)
                    .withEndAction(endAction)
                    .start();
        }
    }

    public void animateSelectedAppUp(View selectedView, Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        _currentlySelectedView = selectedView;

        // exit
        new Animation(_autoCompleteTextView)
                .alpha(0)
                .translationY(_autoCompleteOffset2)
                .start();

        new Animation(_addressNotFoundTextView)
                .alpha(0)
                .translationY(_addressNotFoundOffset)
                .start();

        for (TextView selectionApp : _installedApps)
        {
            if (selectionApp.getId() != _currentlySelectedView.getId())
            {
                new Animation(selectionApp)
                        .translationY(_installedAppsLinearLayoutOffset)
                        .alpha(0)
                        .start();
            }
        }

        // enter
        new Animation(_currentlySelectedView)
                .translationY(getSelectedAppOffset(_currentlySelectedView))
                .start();

        new Animation(_searchingTextView)
                .alpha(1)
                .translationY(0)
                .withEndAction(endAction)
                .start();
    }

    public void animateReturnFromSearching(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        // exit
        new Animation(_searchingTextView)
                .alpha(0)
                .translationY(_searchingOffset)
                .start();

        // enter
        for (TextView installedApp : _installedApps)
        {
            new Animation(installedApp)
                    .alpha(1)
                    .translationY(0)
                    .start();
        }

        new Animation(_autoCompleteTextView)
                .alpha(1)
                .translationY(_autoCompleteOffset1)
                .withEndAction(endAction)
                .start();
    }

    public void animateReturnFromAddressNotFound(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        // exit
        new Animation(_searchingTextView)
                .alpha(0)
                .translationY(_searchingOffset)
                .start();

        // middle
        for (TextView installedApp : _installedApps)
        {
            new Animation(installedApp)
                    .alpha(1)
                    .translationY(0)
                    .start();
        }

        // enter
        new Animation(_autoCompleteTextView)
                .alpha(1)
                .translationY(_autoCompleteOffset1)
                .start();

        new Animation(_addressNotFoundTextView)
                .alpha(1)
                .translationY(0)
                .withEndAction(endAction)
                .start();
    }

    public void animateAddressNotFoundOffScreen(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        new Animation(_addressNotFoundTextView)
                .alpha(0)
                .translationY(_addressNotFoundOffset)
                .withEndAction(endAction)
                .start();

        for (TextView installedApp : _installedApps)
        {
            new Animation(installedApp)
                    .alpha(0)
                    .translationY(_installedAppsLinearLayoutOffset)
                    .start();
        }
    }

    public void animateReturnFromSearchComplete(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        // set starting position and alpha
        _searchingTextView.setAlpha(0);
        _searchingTextView.setTranslationY(_searchingOffset);

        for (TextView selectionApp : _installedApps)
        {
            selectionApp.setAlpha(0);
            selectionApp.setTranslationY(0);
        }

        // enter
        for (TextView installedApp : _installedApps)
        {
            installedApp.setTranslationY(_installedAppsLinearLayoutOffset);

            new Animation(installedApp)
                    .alpha(1)
                    .translationY(0)
                    .start();
        }

        new Animation(_autoCompleteTextView)
                .alpha(1)
                .translationY(_autoCompleteOffset1)
                .withEndAction(endAction)
                .start();
    }

    public void clearFocus(String currentDeliveryAddress)
    {
        _autoCompleteTextView.setText(currentDeliveryAddress);

        if (currentDeliveryAddress != null)
        {
            _autoCompleteTextView.setSelection(currentDeliveryAddress.length());
        }

        _layout.requestFocus();
    }

    private void init()
    {
        initViews();

        initViewAlphas();

        initViewPositions();
    }

    private void initViews()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_main, this, true);

        _layout = findViewById(R.id.layout);
        _titleTextView = findViewById(R.id.titleTextView);
        _autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        _installedAppsLinearLayout = findViewById(R.id.installedAppsLinearLayout);
        _addressNotFoundTextView = findViewById(R.id.addressNotFoundTextView);
        _searchingTextView = findViewById(R.id.searchingTextView);

        _installedApps = new ArrayList<>();

        for (int i = 0; i < _installedAppsLinearLayout.getChildCount(); i++)
        {
            _installedApps.add((TextView) _installedAppsLinearLayout.getChildAt(i));
        }
    }

    private void initViewAlphas()
    {
        _addressNotFoundTextView.setAlpha(0);
        _searchingTextView.setAlpha(0);
        _addressNotFoundTextView.setAlpha(0);

        for (TextView installedApp : _installedApps)
        {
            installedApp.setAlpha(0);
        }
    }

    private void initViewPositions()
    {
        // this is needed so we only get positions/heights after onLayout has happened
        _layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                Helpers.initTopPadding(getContext());
                Helpers.initMainLayoutPadding(getContext(), _layout);

                _titleOffset = -_titleTextView.getHeight() - Helpers.topPadding;
                _autoCompleteOffset1 = -_autoCompleteTextView.getY() + _autoCompleteTextView.getHeight() + Helpers.topPadding;
                _autoCompleteOffset2 = -_autoCompleteTextView.getY();
                _installedAppsLinearLayoutOffset = Helpers.getScreenHeight() - _installedAppsLinearLayout.getY() + Helpers.topPadding;
                _searchingOffset = -_searchingTextView.getHeight() - Helpers.topPadding;
                _addressNotFoundOffset = _autoCompleteTextView.getHeight() + Helpers.topPadding;

                _searchingTextView.setTranslationY(_searchingOffset);
                _addressNotFoundTextView.setTranslationY(_addressNotFoundOffset);

                for (TextView installedApp : _installedApps)
                {
                    installedApp.setTranslationY(_installedAppsLinearLayoutOffset);
                }

                // we need to remove the listener so positions are only set once
                _layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private float getSelectedAppOffset(View view)
    {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        return -location[1] + _searchingTextView.getHeight() + Helpers.topPadding - Helpers.dpToPixels(10);
    }


    private ConstraintLayout _layout;
    private TextView _titleTextView;
    private AutoCompleteTextView _autoCompleteTextView;
    private TextView _addressNotFoundTextView;

    private TextView _searchingTextView;

    private LinearLayout _installedAppsLinearLayout;
    private ArrayList<TextView> _installedApps;
    private View _currentlySelectedView;

    private float _titleOffset;
    private float _autoCompleteOffset1;
    private float _autoCompleteOffset2;
    private float _installedAppsLinearLayoutOffset;
    private float _searchingOffset;
    private float _addressNotFoundOffset;
}
