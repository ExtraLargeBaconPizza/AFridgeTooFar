package com.xlbp.afridgetoofar.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.DisplayCutout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Arrays;

public class Helpers
{
    public static int topPadding;

    public static void initTopPadding(Context context)
    {
        topPadding = Helpers.dpToPixels(50) + getSafeInsetTop(context);
    }

    public static int getSafeInsetTop(Context context)
    {
        int safeInsetTop = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
        {
            DisplayCutout displayCutout =
                    ((Activity) context)
                            .getWindow()
                            .getDecorView()
                            .getRootWindowInsets()
                            .getDisplayCutout();

            assert displayCutout != null;
            safeInsetTop = displayCutout.getSafeInsetTop();
        }
        else
        {
            safeInsetTop = Helpers.dpToPixels(24);
        }

        return safeInsetTop;
    }

    public static void initMainLayoutPadding(Context context, View view)
    {
        int sidePadding = Helpers.dpToPixels(50);
        int bottomPadding = Helpers.dpToPixels(98);

        view.setPadding(sidePadding, topPadding, sidePadding, bottomPadding);
    }

    public static void showKeyboard(Activity activity)
    {
        View view = activity.getCurrentFocus();

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null)
            {
                imm.showSoftInput(view, 0);
            }
        }
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

    public static boolean isAppInstalled(Context context, String packageName)
    {
        PackageManager pm = context.getPackageManager();

        boolean installed = false;

        try
        {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            installed = false;
        }

        return installed;
    }

    public static ArrayList<ActivityInfo> getAllRunningActivities(Context context, String packageName)
    {
        try
        {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            return new ArrayList<>(Arrays.asList(pi.activities));
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
