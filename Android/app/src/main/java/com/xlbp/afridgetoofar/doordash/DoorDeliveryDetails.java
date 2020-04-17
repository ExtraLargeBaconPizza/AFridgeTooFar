package com.xlbp.afridgetoofar.doordash;

import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.DoorAppState;
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
        if (html.contains("Continue in browser") && !_continueInBrowserClicked)
        {
            AppState.setDoorDashAppState(DoorAppState.DeliveryDetailsReady);

            _continueInBrowserClicked = true;

            clickContinueInBrowser();

            retrieveHtml();
        }
        else if (html.contains("Your favorite restaurants, delivered") && !_clickEnterDeliveryAddress)
        {
            _clickEnterDeliveryAddress = true;

            clickEnterDeliveryAddress();

            retrieveHtml();
        }
//        else if (html.contains("Enter") && !_enteredDeliveryAddress)
//        {
//            InputMethodManager imm = (InputMethodManager) doorActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//
//            if (imm != null)
//            {
//                // We need to loop until the keyboard shows. Otherwise the input value will get messed up
//                // by Uber's code
//                if (!imm.isAcceptingText())
//                {
//                    webView.requestFocus();
//
//                    retrieveHtml();
//                }
//                else
//                {
//                    _enteredDeliveryAddress = true;
//
//                    enterDeliveryAddress();
//                }
//            }
//        }
        else
        {
            retrieveHtml();
        }
    }

    private void clickContinueInBrowser()
    {
        Javascript.clickElementByKeyword(webView, "span", "Continue in browser");
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

//        Helpers.hideKeyboard(doorActivity);
    }


    private String _searchAddress;

    private boolean _continueInBrowserClicked;
    private boolean _clickEnterDeliveryAddress;
    private boolean _enteredDeliveryAddress;
}
