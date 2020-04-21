package com.xlbp.afridgetoofar.doordash;

import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.helpers.Helpers;
import com.xlbp.afridgetoofar.helpers.Javascript;


public class DoorDeliveryDetails extends DoorBase
{
    // TODO - ensure keyboard is never shown
    public DoorDeliveryDetails(DoorActivity doorActivity, WebView webView, String searchAddress)
    {
        super(doorActivity, webView);

        _searchAddress = searchAddress;
    }

    @Override
    public void parseHtml(String html)
    {
//        asdf();

//        if (html.contains("Continue in browser"))
//        {
//            AppState.setDoorDashAppState(DoorAppState.DeliveryDetailsReady);
//
//            if (!_continueInBrowserClicked)
//            {
//                _continueInBrowserClicked = true;
//
//                asdf();
//            }
//        }
//        else
//        {
//            retrieveHtml();
//        }
    }

    public void clickContinueInBrowser()
    {
//        asdf();
        Javascript.removeDoorDashSplash(webView);
//        Javascript.clickElementByKeyword(webView, "span", "Continue in browser");

//        new Handler().postDelayed(() ->
//        {
//            Javascript.removeDoorDashSplash(webView);
//            Javascript.clickElementByKeyword(webView, "span", "Continue in browser");
//        }, 2000);
    }

    private void asdf()
    {
        Javascript.getActiveElementPlaceHolderText(webView, this::askldj);
    }

    private void askldj(String asd)
    {
        Log.e("DoorDeliveryDetails", "getActiveElementPlaceHolderText " + asd);

        asdf();
    }


    private void clickEnterDeliveryAddress()
    {
        // We need to call this twice
        Javascript.clickElementByKeyword(webView, "input", "");
        Javascript.clickElementByKeyword(webView, "input", "");
    }

    private void enterDeliveryAddress()
    {
        // convert the string to a char array
        char[] addressFullChars = _searchAddress.toCharArray();

        // need a KeyCharacterMap in order to call getEvents
        KeyCharacterMap fullKeyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);

        // map the chars into key event
        KeyEvent[] keySequence = fullKeyMap.getEvents(addressFullChars);

        // dispatch all the key events
        for (KeyEvent keyEvent : keySequence)
        {
            doorActivity.dispatchKeyEvent(keyEvent);
        }

        KeyEvent enterKeyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
        KeyEvent enterKeyEventUp = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER);

        // Press Enter
        doorActivity.dispatchKeyEvent(enterKeyEventDown);
        doorActivity.dispatchKeyEvent(enterKeyEventUp);

        Helpers.hideKeyboard(doorActivity);
    }


    private String _searchAddress;

    private boolean _continueInBrowserClicked;
    private boolean _clickEnterDeliveryAddress;
    private boolean _enteredDeliveryAddress;
}
