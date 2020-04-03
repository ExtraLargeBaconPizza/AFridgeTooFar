package com.xlbp.afridgetoofar;

import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MainView
{
    public MainView(MainActivity activity)
    {
        _activity = activity;

        init();
    }

    public AutoCompleteTextView getAutoCompleteTextView()
    {
        return _autoCompleteTextView;
    }

    public void animateAutoCompleteTextViewUp(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        if (!_animationPositionsSet)
        {
            _animationPositionsSet = true;

            initAnimationPositions();
        }

        new Animation(_titleTextView)
                .alpha(0)
                .translationY(-_titleTextView.getHeight() - Helpers.topMargin)
                .start();

        new Animation(_autoCompleteTextView)
                .translationY(-_autoCompleteTextView.getY() + Helpers.topMargin)
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
                    .translationY(_selectionAppOffset)
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
                .translationY(_autoCompleteTextView.getTranslationY() - _autoCompleteTextView.getHeight() - Helpers.topMargin)
                .start();

        for (TextView selectionApp : _selectionApps)
        {
            if (selectionApp.getId() != _currentlySelectedView.getId())
            {
                new Animation(selectionApp)
                        .alpha(0)
                        .start();
            }
        }

        // enter
        new Animation(_searchingTextView)
                .alpha(1)
                .translationY(0)
                .startDelay(600)
                .start();

        // 64 = the top margin - the 10dp padding
        new Animation(_currentlySelectedView)
                .translationY(-_currentlySelectedView.getY() + _searchingTextView.getHeight() + Helpers.dpToPixels(64))
                .startDelay(600)
                .withEndAction(endAction)
                .start();
    }

    public void animateSelectedAppDown(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        // exit
        new Animation(_searchingTextView)
                .alpha(0)
                .translationY(-_searchingTextView.getHeight() - Helpers.topMargin)
                .start();

        new Animation(_currentlySelectedView)
                .translationY(0)
                .start();

        // enter
        for (TextView selectionApp : _selectionApps)
        {
            if (selectionApp != _currentlySelectedView)
            {
                new Animation(selectionApp)
                        .alpha(1)
                        .startDelay(600)
                        .start();
            }
        }

        new Animation(_autoCompleteTextView)
                .alpha(1)
                .translationY(_autoCompleteTextView.getTranslationY() + _autoCompleteTextView.getHeight() + Helpers.topMargin)
                .startDelay(900)
                .withEndAction(endAction)
                .start();
    }

    // TODO verify this animation
    public void animateReturnFromSearchComplete(Runnable endAction)
    {
        AppState.setMainScreenState(MainScreenState.Animating);

        // set starting position and alpha
        _searchingTextView.setAlpha(0);
        _searchingTextView.setTranslationY(-_searchingTextView.getHeight() - Helpers.topMargin);

        for (TextView selectionApp : _selectionApps)
        {
            selectionApp.setAlpha(0);
            selectionApp.setTranslationY(_selectionAppOffset);
        }

        // enter
        new Animation(_autoCompleteTextView)
                .alpha(1)
                .translationY(_autoCompleteTextView.getTranslationY() + _autoCompleteTextView.getHeight() + Helpers.topMargin)
                .startDelay(300)
                .start();

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

    public void clearFocus(String currentDeliveryAddress)
    {
        _autoCompleteTextView.setText(currentDeliveryAddress);

        if (currentDeliveryAddress != null)
        {
            _autoCompleteTextView.setSelection(currentDeliveryAddress.length());
        }

        _layout.requestFocus();

        Helpers.hideKeyboard(_activity);
    }

    private void init()
    {
        _activity.setContentView(R.layout.activity_main);

        initViews();

        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        _activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initViews()
    {
        _layout = _activity.findViewById(R.id.layout);
        _titleTextView = _activity.findViewById(R.id.titleTextView);
        // TODO - @jim make the autocomplete overlap the underline, maybe
        _autoCompleteTextView = _activity.findViewById(R.id.autoCompleteTextView);

        _searchingTextView = _activity.findViewById(R.id.searchingTextView);
        TextView uberTextView = _activity.findViewById(R.id.uberTextView);
        TextView doorTextView = _activity.findViewById(R.id.doorTextView);
        TextView skipTextView = _activity.findViewById(R.id.skipTextView);
        TextView foodTextView = _activity.findViewById(R.id.foodTextView);

        _selectionApps = new ArrayList<>();

        _selectionApps.add(uberTextView);
        _selectionApps.add(doorTextView);
        _selectionApps.add(skipTextView);
        _selectionApps.add(foodTextView);

        _searchingTextView.setAlpha(0);

        for (TextView app : _selectionApps)
        {
            app.setAlpha(0);
        }
    }

    private void initAnimationPositions()
    {
        _searchingTextView.setTranslationY(-_searchingTextView.getHeight() - Helpers.topMargin);

        int[] location = new int[2];
        _selectionApps.get(0).getLocationOnScreen(location);

        _selectionAppOffset = Helpers.getScreenHeight() - location[1] + Helpers.dpToPixels(96);

        for (TextView selectionApp : _selectionApps)
        {
            selectionApp.setTranslationY(_selectionAppOffset);
        }
    }


    private MainActivity _activity;

    private ConstraintLayout _layout;
    private TextView _titleTextView;
    private AutoCompleteTextView _autoCompleteTextView;

    private TextView _searchingTextView;

    private ArrayList<TextView> _selectionApps;
    private View _currentlySelectedView;

    private boolean _animationPositionsSet;
    private float _selectionAppOffset;
}
