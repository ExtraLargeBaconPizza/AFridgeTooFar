package com.elbp.justorderwhatever.ubereats;

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
    SelectingCart,
    SelectingCartComplete,
    FoodItemLoading,
    FoodItemComplete,
    FoodItemAdded,
    StartNewOrder,
    StartNewOrderComplete,
}
