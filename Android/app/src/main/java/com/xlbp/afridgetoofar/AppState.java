package com.xlbp.afridgetoofar;

import android.util.Log;

import com.xlbp.afridgetoofar.ubereats.UberActivity;
import com.xlbp.afridgetoofar.ubereats.UberAppState;
import com.xlbp.afridgetoofar.ubereats.UberView;

public class AppState
{
    public static MainScreenState getMainScreenState()
    {
        return _mainScreenState;
    }

    public static void setMainScreenState(MainScreenState mainScreenState)
    {
        _mainScreenState = mainScreenState;
    }

    public static UberAppState getUberEatsAppState()
    {
        return _uberAppState;
    }

    public static void setUberEatsAppState(UberAppState appState)
    {
        _uberAppState = appState;

        Log.e("AppState", "" + _uberAppState);

        UberView.updateUberAppStateTextView(appState);
    }


    private static MainScreenState _mainScreenState = MainScreenState.StartingScreen;
    private static UberAppState _uberAppState = UberAppState.HomePageLoading;
}
