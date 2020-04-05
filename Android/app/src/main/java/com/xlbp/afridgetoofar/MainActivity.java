// This app is dedicated to my dog Ruffles. He was the best.
package com.xlbp.afridgetoofar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xlbp.afridgetoofar.google.PlaceAutoSuggestAdapter;
import com.xlbp.afridgetoofar.ubereats.UberActivity;
import com.xlbp.afridgetoofar.enums.MainScreenState;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (AppState.getMainScreenState() == MainScreenState.ScreenComplete)
        {
            _view.animateSelectedAppDown(() -> AppState.setMainScreenState(MainScreenState.AppSelection));
        }
        else if (AppState.getMainScreenState() == MainScreenState.SearchComplete)
        {
            _view.animateReturnFromSearchComplete(() -> AppState.setMainScreenState(MainScreenState.AppSelection));
        }
    }

    @Override
    public void onBackPressed()
    {
        if (AppState.getMainScreenState() != MainScreenState.Animating)
        {
            if (AppState.getMainScreenState() == MainScreenState.EnterDeliveryAddress
                    || AppState.getMainScreenState() == MainScreenState.AppSelection)
            {
                _currentDeliveryAddress = null;

                _view.clearFocus(_currentDeliveryAddress);

                _view.animateAutoCompleteTextViewDown(null);

                _view.animateAppSelectionOffScreen(() -> AppState.setMainScreenState(MainScreenState.StartingScreen));
            }
            else
            {
                finish();
            }
        }
    }

    public void backgroundClicked(View view)
    {
        _view.clearFocus(_currentDeliveryAddress);
    }

    public void appClicked(View selectedView)
    {
        if (AppState.getMainScreenState() != MainScreenState.Animating)
        {
            _view.animateSelectedAppUp(selectedView, () ->
            {
                AppState.setMainScreenState(MainScreenState.ScreenComplete);

                navigateToSearching(selectedView);
            });
        }
    }

    private void init()
    {
        _view = new MainView(this);

        AppState.setMainScreenState(MainScreenState.StartingScreen);

        _placeAutoSuggestAdapter = new PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1);
        _view.getAutoCompleteTextView().setAdapter(_placeAutoSuggestAdapter);

        handleAutoCompleteTextView();
    }

    private void handleAutoCompleteTextView()
    {
        _view.getAutoCompleteTextView().setOnFocusChangeListener((view, hasFocus) ->
        {
            switch (AppState.getMainScreenState())
            {
                case Animating:
                    break;
                case StartingScreen:
                    _view.animateAutoCompleteTextViewUp(() ->
                            AppState.setMainScreenState(MainScreenState.EnterDeliveryAddress));
                    break;
                case EnterDeliveryAddress:
                    _view.clearFocus(null);
                    _view.animateAutoCompleteTextViewDown(() ->
                            AppState.setMainScreenState(MainScreenState.StartingScreen));
                    break;
                case DeliveryAddressSelected:
                    _view.animateAppSelectionOnToScreen(() ->
                            AppState.setMainScreenState(MainScreenState.AppSelection));
                    break;
                case AppSelection:
                    _view.animateAppSelectionOffScreen(() ->
                            AppState.setMainScreenState(MainScreenState.ModifyDeliveryAddress));
                    break;
                case ModifyDeliveryAddress:
                    _view.clearFocus(_currentDeliveryAddress);
                    _view.animateAppSelectionOnToScreen(() ->
                            AppState.setMainScreenState(MainScreenState.AppSelection));
                    break;
            }
        });

        _view.getAutoCompleteTextView().setOnItemClickListener((parent, view, position, id) ->
        {
            AppState.setMainScreenState(MainScreenState.DeliveryAddressSelected);

            _currentDeliveryAddress = _placeAutoSuggestAdapter.getMainText(position);

            _view.clearFocus(_currentDeliveryAddress);
        });
    }

    private void navigateToSearching(View view)
    {
        Intent intent = new Intent(getBaseContext(), UberActivity.class);

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


    private MainView _view;

    private PlaceAutoSuggestAdapter _placeAutoSuggestAdapter;

    private String _currentDeliveryAddress;
}