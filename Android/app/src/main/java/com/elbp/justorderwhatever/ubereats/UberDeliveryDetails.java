package com.elbp.justorderwhatever.ubereats;

import android.content.Context;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;


public class UberDeliveryDetails extends UberBase
{
    public UberDeliveryDetails(UberActivity uberEatsActivity, WebView webView)
    {
        super(uberEatsActivity, webView);
    }

    @Override
    public void parseHtml(String html)
    {
        InputMethodManager imm = (InputMethodManager) uberEatsActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // We need to loop until the keyboard shows. Otherwise the input value will get messed up
        // by Uber's code
        if (!imm.isAcceptingText())
        {
            retrieveHtml();
        }
        else
        {
            if (!_addressEntered)
            {
                String addressFull = "211 summit Ave e Seattle";

                // convert the string to a char array
                char[] addressFullChars = addressFull.toCharArray();

                // need a KeyCharacterMap in order to call .getEvents
                KeyCharacterMap fullKmap = KeyCharacterMap.load(KeyCharacterMap.FULL);

                // map the chars into key kevent
                KeyEvent[] keySequence = fullKmap.getEvents(addressFullChars);

                // dispatch all the key events
                for (int i = 0; i < keySequence.length; i++)
                {
                    uberEatsActivity.dispatchKeyEvent(keySequence[i]);
                }

                _addressEntered = true;
            }

            // Loop until the address option appears
            if (html.contains("location-typeahead-item-0"))
            {
                clickElementById("location-typeahead-item-0");
            }
            else
            {
                retrieveHtml();
            }

            // TODO @jim - check to make sure done button is ready instead of just a timer
            try
            {
                Thread.sleep(2000);

                clickButtonByKeyword("Done");

                AppState.setUberEatsAppState(UberAppState.MainMenuLoading);
            }
            catch (Exception e)
            {

            }
        }
    }


    private boolean _addressEntered;
}
