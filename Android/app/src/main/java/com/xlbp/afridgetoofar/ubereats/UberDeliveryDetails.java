package com.xlbp.afridgetoofar.ubereats;

import android.content.Context;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppScreenState;
import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.helpers.Helpers;
import com.xlbp.afridgetoofar.helpers.Javascript;


public class UberDeliveryDetails extends UberBase
{
    // TODO - ensure keyboard is never shown
    public UberDeliveryDetails(UberActivity uberActivity, WebView webView, String searchAddress)
    {
        super(uberActivity, webView);

        _searchAddress = searchAddress;
    }

    @Override
    public void parseHtml(String html)
    {
        InputMethodManager imm = (InputMethodManager) uberActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null)
        {
            // We need to loop until the keyboard shows. Otherwise the input value will get messed up
            if (imm.isAcceptingText())
            {
                if (!_addressEntered)
                {
                    AppState.setAppScreenState(AppScreenState.DeliveryDetailsReady);

                    // convert the string to a char array
                    char[] addressFullChars = _searchAddress.toCharArray();

                    // need a KeyCharacterMap in order to call getEvents
                    KeyCharacterMap fullKeyMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);

                    // map the chars into key event
                    KeyEvent[] keySequence = fullKeyMap.getEvents(addressFullChars);

                    // dispatch all the key events
                    for (int i = 0; i < keySequence.length; i++)
                    {
                        uberActivity.dispatchKeyEvent(keySequence[i]);
                    }

                    Helpers.hideKeyboard(uberActivity);

                    _addressEntered = true;
                }
            }
            else
            {
                webView.requestFocus();

                retrieveHtml();
            }

            if (_addressEntered && !_addressClicked)
            {
                // Loop until the address option appears
                if (html.contains("location-typeahead-item-0"))
                {
                    _addressClicked = true;

                    Javascript.clickElementById(webView, "location-typeahead-item-0");
                }
                else
                {
                    retrieveHtml();
                }
            }

            if (_addressClicked && !_doneClicked)
            {
                if (html.contains("Choose a time"))
                {
                    _doneClicked = true;

                    AppState.setAppScreenState(AppScreenState.MainMenuLoading);

                    Javascript.clickElementByKeyword(webView, "button", "Done");
                }
                else
                {
                    retrieveHtml();
                }
            }
        }
        else
        {
            retrieveHtml();
        }
    }


    private String _searchAddress;

    private boolean _addressEntered;
    private boolean _addressClicked;
    private boolean _doneClicked;
}
