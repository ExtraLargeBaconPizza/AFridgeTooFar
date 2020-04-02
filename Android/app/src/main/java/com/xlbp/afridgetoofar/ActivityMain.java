// This app is dedicated to my dog Ruffles. He was the best.

package com.xlbp.afridgetoofar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.xlbp.afridgetoofar.ubereats.ActivityUber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

// magic 8 ball for food
// soup or salad?
// whats for dinner?
// what can I order?


public class ActivityMain extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        handleAutoCompleteTextView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (_thirdAnimationComplete)
        {
            new Handler().postDelayed(() ->
            {
                handleThirdAnimation(null, false);
            }, f_postTapDelay);
        }
    }

    @Override
    public void onBackPressed()
    {
        // TODO - @jim need to handle the case when the back button is pressed during the third animation
        if (_firstAnimationComplete)
        {
            _currentDeliveryAddress = null;

            handleFirstAnimation(false);

            handleSecondAnimation(true);
        }
        else
        {
            finish();
        }
    }

    public void backgroundClicked(View view)
    {
        clearFocus();
    }

    public void appClicked(View view)
    {
        new Handler().postDelayed(() ->
        {
            handleThirdAnimation(view, true);
        }, f_postTapDelay);
    }

    private void init()
    {
        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        findViews();

        _placeAutoSuggestAdapter = new PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1);
        _autoCompleteTextView.setAdapter(_placeAutoSuggestAdapter);
    }

    // TODO - @jim create a seperate view class for all activities
    private void findViews()
    {
        _layout = findViewById(R.id.layout);
        _titleTextView = findViewById(R.id.titleTextView);
        // TODO - @jim make the autocomplete overlap the underline
        _autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        _appConstraintLayout = findViewById(R.id.appConstraintLayout);

        _searchingTextView = findViewById(R.id.searchingTextView);
        _uberTextView = findViewById(R.id.uberTextView);
        _doorTextView = findViewById(R.id.doorTextView);
        _skipTextView = findViewById(R.id.skipTextView);
        _foodTextView = findViewById(R.id.foodTextView);

        _appConstraintLayout.setTranslationY(Helpers.dpToPixels(400));
        _appConstraintLayout.setAlpha(0);

        _searchingTextView.setTranslationY(-200);
        _searchingTextView.setAlpha(0);

    }

    // TODO - @jim handle when the keyboard is lowered
    private void handleAutoCompleteTextView()
    {
        _autoCompleteTextView.setOnFocusChangeListener((view, hasFocus) ->
        {
            if (_currentDeliveryAddress == null)
            {
                handleFirstAnimation(hasFocus);
            }
            else
            {
                handleSecondAnimation(hasFocus);
            }
        });

        _autoCompleteTextView.setOnItemClickListener((parent, view, position, id) ->
        {
            _currentDeliveryAddress = _placeAutoSuggestAdapter.getMainText(position);

            handleSecondAnimation(false);
        });
    }

    private void handleFirstAnimation(boolean hasFocus)
    {
        if (hasFocus)
        {
            new Animation(_titleTextView)
                    .alpha(0)
                    .translationY(-_titleTextView.getHeight() - f_topMargin)
                    .start();

            new Animation(_autoCompleteTextView)
                    .translationY(-_autoCompleteTextView.getY() + f_topMargin)
                    .withEndAction(() -> _firstAnimationComplete = true)
                    .start();
        }
        else
        {
            clearFocus();

            new Animation(_titleTextView)
                    .alpha(1)
                    .translationY(0)
                    .start();

            new Animation(_autoCompleteTextView)
                    .translationY(0)
                    .withEndAction(() -> _firstAnimationComplete = false)
                    .start();
        }
    }

    private void handleSecondAnimation(boolean hasFocus)
    {
        if (!hasFocus)
        {
            clearFocus();

            new Animation(_appConstraintLayout)
                    .alpha(1)
                    .translationY(0)
                    .startDelay(600)
                    .start();
        }
        else
        {
            new Animation(_appConstraintLayout)
                    .alpha(0)
                    .translationY(_appConstraintLayout.getHeight() + f_bottomMargin)
                    .start();
        }
    }

    private void handleThirdAnimation(View view, boolean navigatingForward)
    {
        // TODO - @jim value is hardcoded for uber eats. 64 = the top margin - the 10dp padding
        int selectionOffset = Helpers.dpToPixels(64);

        if (navigatingForward)
        {
            // exit
            new Animation(_autoCompleteTextView)
                    .alpha(0)
                    .translationY(_autoCompleteTextView.getTranslationY() - _autoCompleteTextView.getHeight() - f_topMargin)
                    .start();

            new Animation(_doorTextView)
                    .alpha(0)
                    .start();

            new Animation(_skipTextView)
                    .alpha(0)
                    .start();

            new Animation(_foodTextView)
                    .alpha(0)
                    .start();

            // enter
            // TODO - @jim handle different animations for each choice
            new Animation(_searchingTextView)
                    .alpha(1)
                    .translationY(0)
                    .startDelay(600)
                    .start();

            new Animation(_appConstraintLayout)
                    .translationY(-_appConstraintLayout.getY() + _searchingTextView.getHeight() + selectionOffset)
                    .startDelay(600)
                    .withEndAction(() -> navigateToSearching(view))
                    .start();
        }
        else
        {
            // exit
            new Animation(_searchingTextView)
                    .alpha(0)
                    .translationY(-_searchingTextView.getHeight() - f_topMargin)
                    .start();

            new Animation(_appConstraintLayout)
                    .translationY(_appConstraintLayout.getY() - _searchingTextView.getHeight() - selectionOffset)
                    .start();

            // enter
            new Animation(_autoCompleteTextView)
                    .alpha(1)
                    .translationY(_autoCompleteTextView.getTranslationY() + _autoCompleteTextView.getHeight() + f_topMargin)
                    .startDelay(400)
                    .start();

            new Animation(_doorTextView)
                    .alpha(1)
                    .startDelay(600)
                    .start();

            new Animation(_skipTextView)
                    .alpha(1)
                    .startDelay(600)
                    .start();

            new Animation(_foodTextView)
                    .alpha(1)
                    .startDelay(600)
                    .start();
        }
    }


    private void navigateToSearching(View view)
    {
        _thirdAnimationComplete = true;

        Intent intent = new Intent(getBaseContext(), ActivityUber.class);

        switch (view.getId())
        {
            case R.id.foodTextView:
                intent.putExtra("DebugMode", true);
                break;
            case R.id.uberTextView:
            case R.id.doorTextView:
            case R.id.skipTextView:
                intent.putExtra("DebugMode", false);
                break;
        }

        startActivity(intent);
    }

    private void clearFocus()
    {
        _autoCompleteTextView.setText(_currentDeliveryAddress);

        if (_currentDeliveryAddress != null)
        {
            _autoCompleteTextView.setSelection(_currentDeliveryAddress.length());
        }

        _layout.requestFocus();

        Helpers.hideKeyboard(this);
    }


    private PlaceAutoSuggestAdapter _placeAutoSuggestAdapter;

    private ConstraintLayout _layout;
    private TextView _titleTextView;
    private AutoCompleteTextView _autoCompleteTextView;

    private ConstraintLayout _appConstraintLayout;
    private TextView _searchingTextView;
    private TextView _uberTextView;
    private TextView _doorTextView;
    private TextView _skipTextView;
    private TextView _foodTextView;

    private String _currentDeliveryAddress;

    private boolean _firstAnimationComplete;
    private boolean _thirdAnimationComplete;

    // TODO move these values to a more central location
    // TODO account for notch
    public static final int f_topMargin = Helpers.dpToPixels(74);
    public static final int f_bottomMargin = Helpers.dpToPixels(98);

    public static final int f_postTapDelay = 100;
    public static final int f_animTime = 600;
    public static final int f_animationFactor = 2;
}