package com.xlbp.afridgetoofar.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class Helpers
{
    public static final int topMargin = Helpers.dpToPixels(50) + getStatusBarHeight();

    public static void adjustViewTopMarginForNotch(View view)
    {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        layoutParams.topMargin = topMargin;

        view.setLayoutParams(layoutParams);
    }

    public static void hideKeyboard(Activity activity)
    {
        View view = activity.getCurrentFocus();

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null)
            {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
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

    private static int getStatusBarHeight()
    {
        int statusBarHeight = 0;

        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
        {
            statusBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }
}
