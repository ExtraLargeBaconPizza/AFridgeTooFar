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

    public static DoorAppState getDoorDashAppState()
    {
        return _doorAppState;
    }

    public static void setDoorDashAppState(DoorAppState doorAppState)
    {
        if (_doorAppState != doorAppState)
        {
            _doorAppState = doorAppState;

            Log.e("DoorAppState", "" + _doorAppState);
        }
    }

    public static GrubAppState getGrubhubAppState()
    {
        return _grubAppState;
    }

    public static void setGrubhubAppState(GrubAppState grubAppState)
    {
        if (_grubAppState != grubAppState)
        {
            _grubAppState = grubAppState;

            Log.e("GrubAppState", "" + _grubAppState);
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
        }
    }


    private static MainScreenState _mainScreenState;
    private static DoorAppState _doorAppState;
    private static GrubAppState _grubAppState;
    private static UberAppState _uberAppState;
}
