package com.xlbp.afridgetoofar.enums;

import android.util.Log;

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

    public static AppScreenState getAppScreenState()
    {
        return _appScreenState;
    }

    public static void setAppScreenState(AppScreenState appScreenState)
    {
        if (_appScreenState != appScreenState)
        {
            _appScreenState = appScreenState;

            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];

            String fileName = stackTraceElement.getFileName();
            String methodName = stackTraceElement.getMethodName();

            Log.e("AppScreenState", _appScreenState + " - " + fileName + " - " + methodName);
        }
    }


    private static MainScreenState _mainScreenState;
    private static AppScreenState _appScreenState;
}
