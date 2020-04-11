// This app is dedicated to my dog Ruffles. He was a good boy.
package com.xlbp.afridgetoofar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xlbp.afridgetoofar.enums.AppState;
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

        switch (AppState.getMainScreenState())
        {
            case Animating:
                break;
            case SearchingApp:
                _view.animateReturnFromSearching(() -> AppState.setMainScreenState(MainScreenState.AppSelection));
                break;
            case AddressNotFound:
                _view.animateReturnFromAddressNotFound(() -> AppState.setMainScreenState(MainScreenState.AddressNotFound));
                break;
            case SearchComplete:
                _view.animateReturnFromSearchComplete(() -> AppState.setMainScreenState(MainScreenState.AppSelection));
                break;
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
                _shortDeliveryAddress = null;

                _view.clearFocus(_shortDeliveryAddress);

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
        _view.clearFocus(_shortDeliveryAddress);
    }

    public void appClicked(View selectedView)
    {
        if (AppState.getMainScreenState() != MainScreenState.Animating)
        {
            _view.animateSelectedAppUp(selectedView, () ->
            {
                AppState.setMainScreenState(MainScreenState.SearchingApp);

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
                    _view.clearFocus(_shortDeliveryAddress);
                    _view.animateAppSelectionOnToScreen(() ->
                            AppState.setMainScreenState(MainScreenState.AppSelection));
                    break;
                case AddressNotFound:
                    _view.animateAddressNotFoundOffScreen(() ->
                            AppState.setMainScreenState(MainScreenState.ModifyDeliveryAddress));
                    break;
            }
        });

        _view.getAutoCompleteTextView().setOnItemClickListener((parent, view, position, id) ->
        {
            AppState.setMainScreenState(MainScreenState.DeliveryAddressSelected);

            _fullDeliveryAddress = _placeAutoSuggestAdapter.getItem(position);
            _shortDeliveryAddress = _placeAutoSuggestAdapter.getMainText(position);

            _view.clearFocus(_shortDeliveryAddress);
        });
    }

    private void navigateToSearching(View view)
    {
        Intent intent = new Intent(getBaseContext(), UberActivity.class);

        switch (view.getId())
        {
            case R.id.foodTextView:
                intent.putExtra("SearchAddress", _fullDeliveryAddress);
                intent.putExtra("DebugMode", true);
                break;
            case R.id.uberTextView:
            case R.id.doorTextView:
            case R.id.skipTextView:
                intent.putExtra("SearchAddress", _fullDeliveryAddress);
                break;
        }

        startActivity(intent);
    }


    private MainView _view;

    private PlaceAutoSuggestAdapter _placeAutoSuggestAdapter;

    private String _fullDeliveryAddress;
    private String _shortDeliveryAddress;
}