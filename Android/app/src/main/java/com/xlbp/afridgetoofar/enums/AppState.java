package com.xlbp.afridgetoofar.enums;

import android.util.Log;

import com.xlbp.afridgetoofar.ubereats.UberView;

public class AppState
{
    public static MainScreenState getMainScreenState()
    {
        return _mainScreenState;
    }

    public static void setMainScreenState(MainScreenState mainScreenState)
    {
        if (_mainScreenState != mainScreenState)
        {
            _mainScreenState = mainScreenState;

            Log.e("MainScreenState", "" + _mainScreenState);
        }
    }

    public static UberAppState getUberEatsAppState()
    {
        return _uberAppState;
    }

    public static void setUberEatsAppState(UberAppState uberAppState)
    {
        if (_uberAppState != uberAppState)
        {
            _uberAppState = uberAppState;

            Log.e("UberAppState", "" + _uberAppState);

            UberView.updateUberAppStateTextView(uberAppState);
        }
    }


    private static MainScreenState _mainScreenState;
    private static UberAppState _uberAppState;
}
