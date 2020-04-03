package com.xlbp.afridgetoofar.ubereats;

import android.app.Activity;
import android.content.Context;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.AppState;
import com.xlbp.afridgetoofar.Javascript;


public class UberDeliveryDetails extends UberBase
{
    // TODO handle address not available
    public UberDeliveryDetails(UberActivity uberActivity, WebView webView)
    {
        super(webView);

        _uberActivity = uberActivity;
    }

    @Override
    public void parseHtml(String html)
    {
        InputMethodManager imm = (InputMethodManager) _uberActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null)
        {
            // We need to loop until the keyboard shows. Otherwise the input value will get messed up
            // by Uber's code
            if (!imm.isAcceptingText())
            {
                webView.requestFocus();

                retrieveHtml();
            }
            else
            {
                if (!_addressEntered)
                {
                    AppState.setUberEatsAppState(UberAppState.DeliveryDetailsReady);

                    String addressFull = "211 summit Ave e Seattle";

                    // convert the string to a char array
                    char[] addressFullChars = addressFull.toCharArray();

                    // need a KeyCharacterMap in order to call .getEvents
                    KeyCharacterMap fullKeyMap = KeyCharacterMap.load(KeyCharacterMap.FULL);

                    // map the chars into key event
                    KeyEvent[] keySequence = fullKeyMap.getEvents(addressFullChars);

                    // dispatch all the key events
                    for (int i = 0; i < keySequence.length; i++)
                    {
                        _uberActivity.dispatchKeyEvent(keySequence[i]);
                    }

                    _addressEntered = true;
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

                        AppState.setUberEatsAppState(UberAppState.MainMenuLoading);

                        Javascript.clickElementByKeyword(webView, "button", "Done");
                    }
                    else
                    {
                        retrieveHtml();
                    }
                }
            }
        }
        else
        {
            retrieveHtml();
        }
    }


    private Activity _uberActivity;

    private boolean _addressEntered;
    private boolean _addressClicked;
    private boolean _doneClicked;
}
