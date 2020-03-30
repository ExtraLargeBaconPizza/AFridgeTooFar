package com.elbp.afridgetoofar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Helpers
{
    public static String removeLastCharacter(String inputString)
    {
        if ((!inputString.equals("")) && (inputString.length() > 0))
        {
            String outputString = inputString.substring(0, inputString.length() - 1);

            return outputString;
        }
        else
        {
            return inputString;
        }
    }

    public static void hideKeyboard(Activity activity)
    {
        View view = activity.getCurrentFocus();

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static int dpToPixels(float dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    public static int pixelsToDp(float pixels)
    {
        return (int) (pixels / Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    public static int getScreenWidth()
    {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight()
    {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
