package com.xlbp.afridgetoofar.ubereats;

import android.util.Log;

public class AppState
{
    public static UberAppState getUberEatsAppState()
    {
        return _currentAppState;
    }

    public static void setUberEatsAppState(UberAppState appState)
    {
        _currentAppState = appState;

        Log.e("AppState", "" + _currentAppState);

        ActivityUber.updateAppStateTextView(appState);
    }

    private static UberAppState _currentAppState = UberAppState.InitialLoading;
}

enum UberAppState
{
    InitialLoading,
    InitialComplete,
    DeliveryDetailsLoading,
    DeliveryDetailsComplete,
    MainMenuLoading,
    MainMenuComplete,
    RestaurantMenuLoading,
    RestaurantMenuComplete,
    SearchComplete,
}
