// This app is dedicated to my dog Ruffles. He was a good boy.
package com.xlbp.afridgetoofar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.xlbp.afridgetoofar.doordash.DoorActivity;
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.MainScreenState;
import com.xlbp.afridgetoofar.google.PlaceAutoSuggestAdapter;
import com.xlbp.afridgetoofar.grubhub.GrubActivity;
import com.xlbp.afridgetoofar.helpers.Helpers;
import com.xlbp.afridgetoofar.postmates.PostActivity;
import com.xlbp.afridgetoofar.ubereats.UberActivity;

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

                clearFocus(_shortDeliveryAddress);

                _view.animateAutoCompleteTextViewDown(null);

                _view.animateInstalledAppOffScreen(() -> AppState.setMainScreenState(MainScreenState.StartingScreen));
            }
            else
            {
                finish();
            }
        }
    }

    public void backgroundClicked(View view)
    {
        clearFocus(_shortDeliveryAddress);
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
        initView();

        initMainScreen();

        initAutoCompleteTextView();

        initIsAppInstalled();
    }

    private void initView()
    {
        _view = new MainView(this);
        setContentView(_view);

        // Make fullscreen. Action bar height is 24dp. Navigation bar height is 48dp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void initMainScreen()
    {
        AppState.setMainScreenState(MainScreenState.StartingScreen);

        _placeAutoSuggestAdapter = new PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1);
        _view.getAutoCompleteTextView().setAdapter(_placeAutoSuggestAdapter);
    }

    private void initAutoCompleteTextView()
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
                    clearFocus(null);
                    _view.animateAutoCompleteTextViewDown(() ->
                            AppState.setMainScreenState(MainScreenState.StartingScreen));
                    break;
                case DeliveryAddressSelected:
                    _view.animateInstalledAppsOnToScreen(() ->
                            AppState.setMainScreenState(MainScreenState.AppSelection));
                    break;
                case AppSelection:
                    _view.animateInstalledAppOffScreen(() ->
                            AppState.setMainScreenState(MainScreenState.ModifyDeliveryAddress));
                    break;
                case ModifyDeliveryAddress:
                    clearFocus(_shortDeliveryAddress);
                    _view.animateInstalledAppsOnToScreen(() ->
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

            clearFocus(_shortDeliveryAddress);
        });
    }

    private void initIsAppInstalled()
    {
        String doorPackageName = "com.dd.doordash";
        String grubPackageName = "com.grubhub.android";
        String postPackageName = "com.postmates.android";
        String uberPackageName = "com.ubercab.eats";

        boolean[] isAppInstalled = new boolean[4];

        // TODO doordash shelved door due to strange loading sequence. possibly caused by slow internet
        isAppInstalled[0] = Helpers.isAppInstalled(this, doorPackageName);
        isAppInstalled[1] = Helpers.isAppInstalled(this, grubPackageName);
        isAppInstalled[2] = Helpers.isAppInstalled(this, postPackageName);
        isAppInstalled[3] = Helpers.isAppInstalled(this, uberPackageName);

        _view.initInstalledApps(isAppInstalled);
    }

    private void navigateToSearching(View view)
    {
        Intent intent;

        // TODO - refactor UberActivity into a more general SearchingActivity
        // TODO - also refactor deliveryDetails, MainMenu, Restaurant into general views.
        switch (view.getId())
        {
            case R.id.doorTextView:
                intent = new Intent(getBaseContext(), DoorActivity.class);
                intent.putExtra("SearchAddress", _fullDeliveryAddress);
                intent.putExtra("DebugMode", true);
                startActivity(intent);
                break;
            case R.id.grubTextView:
                intent = new Intent(getBaseContext(), GrubActivity.class);
                intent.putExtra("SearchAddress", _fullDeliveryAddress);
                startActivity(intent);
            case R.id.postTextView:
                intent = new Intent(getBaseContext(), PostActivity.class);
                intent.putExtra("SearchAddress", _fullDeliveryAddress);
                intent.putExtra("DebugMode", true);
                startActivity(intent);
                break;
            case R.id.uberTextView:
                intent = new Intent(getBaseContext(), UberActivity.class);
                intent.putExtra("SearchAddress", _fullDeliveryAddress);
                startActivity(intent);
                break;
        }
    }

    private void clearFocus(String currentDeliveryAddress)
    {
        _view.clearFocus(currentDeliveryAddress);

        Helpers.hideKeyboard(this);
    }


    private MainView _view;

    private PlaceAutoSuggestAdapter _placeAutoSuggestAdapter;

    private String _fullDeliveryAddress;
    private String _shortDeliveryAddress;
}