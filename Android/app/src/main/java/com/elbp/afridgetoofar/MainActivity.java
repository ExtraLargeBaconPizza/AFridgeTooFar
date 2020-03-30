// This app is dedicated to my dog Ruffles. He was the best.

package com.elbp.afridgetoofar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.elbp.afridgetoofar.ubereats.UberActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity
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
    public void onBackPressed()
    {
        if (_currentDeliveryAddress != null)
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
        navigateToSearching(view);
    }

    private void init()
    {
        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        findViews();

        _placeAutoSuggestAdapter = new PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1);
        _autoCompleteTextView.setAdapter(_placeAutoSuggestAdapter);

        _appConstraintLayout = findViewById(R.id.appConstraintLayout);
        _appConstraintLayout.setTranslationY(Helpers.dpToPixels(278));
    }

    private void findViews()
    {
        _layout = findViewById(R.id.layout);
        _titleTextView = findViewById(R.id.titleTextView);
        _autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
    }

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
            _titleTextView.animate()
                    .alpha(0)
                    .translationY(-Helpers.dpToPixels(100))
                    .setInterpolator(new DecelerateInterpolator());

            _autoCompleteTextView.animate()
                    .translationY(-_autoCompleteTextView.getY() + Helpers.dpToPixels(75))
                    .setInterpolator(new DecelerateInterpolator());
        }
        else
        {
            clearFocus();

            _titleTextView.animate()
                    .alpha(1)
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator());

            _autoCompleteTextView.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator());
        }
    }

    private void handleSecondAnimation(boolean hasFocus)
    {
        if (hasFocus)
        {
            _appConstraintLayout.animate()
                    .setDuration(300)
                    .translationY(_appConstraintLayout.getHeight() + Helpers.dpToPixels(72))
                    .setInterpolator(new DecelerateInterpolator(3));
        }
        else
        {
            clearFocus();

            new Handler().postDelayed(() ->
            {
                _appConstraintLayout.animate()
                        .translationY(0)
                        .setDuration(300)
                        .setInterpolator(new DecelerateInterpolator(3));
            }, 500);
        }
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

    private void navigateToSearching(View view)
    {
        new Handler().postDelayed(() ->
        {
            // TODO - @jim determine which was clicked
            startActivity(new Intent(MainActivity.this, UberActivity.class));

            overridePendingTransition(R.anim.nav_forward_enter, R.anim.nav_forward_exit);
        }, 100);
    }


    private PlaceAutoSuggestAdapter _placeAutoSuggestAdapter;

    private ConstraintLayout _layout;
    private TextView _titleTextView;
    private AutoCompleteTextView _autoCompleteTextView;

    private ConstraintLayout _appConstraintLayout;

    private String _currentDeliveryAddress;
}