package com.xlbp.afridgetoofar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
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

    public void animateAppSelectionOnToScreen(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        for (TextView selectionApp : _selectionApps)
        {
            new Animation(selectionApp)
                    .alpha(1)
                    .translationY(0)
                    .startDelay(600)
                    .withEndAction(endAction)
                    .start();
        }
    }

    public void animateAppSelectionOffScreen(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        for (TextView selectionApp : _selectionApps)
        {
            new Animation(selectionApp)
                    .alpha(0)
                    .translationY(_selectionAppsArrayListOffset)
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

        for (TextView selectionApp : _selectionApps)
        {
            if (selectionApp.getId() != _currentlySelectedView.getId())
            {
                new Animation(selectionApp)
                        .translationY(_selectionAppsArrayListOffset)
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
        for (TextView selectionApp : _selectionApps)
        {
            new Animation(selectionApp)
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
        for (TextView selectionApp : _selectionApps)
        {
            new Animation(selectionApp)
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

        for (TextView selectionApp : _selectionApps)
        {
            new Animation(selectionApp)
                    .alpha(0)
                    .translationY(_selectionAppsArrayListOffset)
                    .start();
        }
    }

    public void animateReturnFromSearchComplete(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        // set starting position and alpha
        _searchingTextView.setAlpha(0);
        _searchingTextView.setTranslationY(_searchingOffset);

        for (TextView selectionApp : _selectionApps)
        {
            selectionApp.setAlpha(0);
            selectionApp.setTranslationY(0);
        }

        // enter
        for (TextView selectionApp : _selectionApps)
        {
            selectionApp.setTranslationY(_selectionAppsArrayListOffset);

            new Animation(selectionApp)
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
        _addressNotFoundTextView = findViewById(R.id.addressNotFoundTextView);
        _searchingTextView = findViewById(R.id.searchingTextView);

        TextView uberTextView = findViewById(R.id.uberTextView);
        TextView doorTextView = findViewById(R.id.doorTextView);
        TextView skipTextView = findViewById(R.id.grubTextView);
        TextView foodTextView = findViewById(R.id.postTextView);

        _selectionApps = new ArrayList<>();

        _selectionApps.add(uberTextView);
        _selectionApps.add(doorTextView);
        _selectionApps.add(skipTextView);
        _selectionApps.add(foodTextView);
    }

    private void initViewAlphas()
    {
        _addressNotFoundTextView.setAlpha(0);
        _searchingTextView.setAlpha(0);
        _addressNotFoundTextView.setAlpha(0);

        for (TextView app : _selectionApps)
        {
            app.setAlpha(0);
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
                Helpers.initTopMargin(getContext());
                Helpers.adjustViewTopMarginForNotch(_titleTextView);
                Helpers.adjustViewTopMarginForNotch(_searchingTextView);

                _titleOffset = -_titleTextView.getHeight() - Helpers.topMargin;
                _autoCompleteOffset1 = -_autoCompleteTextView.getY() + Helpers.topMargin;
                _autoCompleteOffset2 = -_autoCompleteTextView.getY() - _autoCompleteTextView.getHeight();
                _selectionAppsArrayListOffset = Helpers.getScreenHeight() - _selectionApps.get(0).getY() + Helpers.topMargin;
                _searchingOffset = -_searchingTextView.getHeight() - Helpers.topMargin;
                _addressNotFoundOffset = _autoCompleteTextView.getHeight() + Helpers.topMargin;

                _searchingTextView.setTranslationY(_searchingOffset);
                _addressNotFoundTextView.setTranslationY(_addressNotFoundOffset);

                for (TextView seletionApp : _selectionApps)
                {
                    seletionApp.setTranslationY(_selectionAppsArrayListOffset);
                }

                // we need to remove the listener so positions are only set once
                _layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private float getSelectedAppOffset(View view)
    {
        return -view.getY() + _searchingTextView.getHeight() + Helpers.topMargin - Helpers.dpToPixels(10);
    }


    private ConstraintLayout _layout;
    private TextView _titleTextView;
    private AutoCompleteTextView _autoCompleteTextView;
    private TextView _addressNotFoundTextView;

    private TextView _searchingTextView;

    private ArrayList<TextView> _selectionApps;
    private View _currentlySelectedView;

    private float _titleOffset;
    private float _autoCompleteOffset1;
    private float _autoCompleteOffset2;
    private float _selectionAppsArrayListOffset;
    private float _searchingOffset;
    private float _addressNotFoundOffset;
}
