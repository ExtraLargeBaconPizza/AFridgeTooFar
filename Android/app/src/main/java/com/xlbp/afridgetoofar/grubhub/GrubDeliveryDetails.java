package com.xlbp.afridgetoofar.grubhub;

import android.content.Context;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.GrubAppState;
import com.xlbp.afridgetoofar.helpers.Helpers;
import com.xlbp.afridgetoofar.helpers.Javascript;


public class GrubDeliveryDetails extends GrubBase
{
    public GrubDeliveryDetails(GrubActivity grubActivity, WebView webView, String searchAddress)
    {
        super(grubActivity, webView);

        _searchAddress = searchAddress;
    }

    @Override
    public void parseHtml(String html)
    {
        if (html.contains("Let's get this right from the start") && !_activeElementPlaceHolderTextRetrieved)
        {
            AppState.setGrubhubAppState(GrubAppState.DeliveryDetailsReady);

            stopWebViewReload();

            _activeElementPlaceHolderTextRetrieved = true;

            retrieveActiveElementPlaceHolderText();
        }
        else
        {
            retrieveHtml();
        }
    }

    private void retrieveActiveElementPlaceHolderText()
    {
        Javascript.getActiveElementPlaceHolderText(webView, this::handleActiveElementPlaceHolderText);
    }

    private void handleActiveElementPlaceHolderText(String activeElementPlaceHolderText)
    {
        if (activeElementPlaceHolderText.contains("Enter street address or zip code"))
        {
            enterAddress();
        }
        else
        {
            KeyEvent tabKeyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB);
            grubActivity.dispatchKeyEvent(tabKeyEventDown);

            retrieveActiveElementPlaceHolderText();
        }
    }

    private void enterAddress()
    {
        InputMethodManager imm = (InputMethodManager) grubActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null)
        {
            // We need to loop until the keyboard shows. Otherwise the input value will get messed up
            if (imm.isAcceptingText())
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
                    grubActivity.dispatchKeyEvent(keyEvent);
                }

                AppState.setGrubhubAppState(GrubAppState.MainMenuLoading);

                KeyEvent enterKeyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
                grubActivity.dispatchKeyEvent(enterKeyEventDown);
                grubActivity.dispatchKeyEvent(enterKeyEventDown);

                Helpers.hideKeyboard(grubActivity);

                grubActivity.onDocumentComplete();
            }
            else
            {
                retrieveActiveElementPlaceHolderText();
            }
        }
    }


    private String _searchAddress;

    private boolean _activeElementPlaceHolderTextRetrieved;
}