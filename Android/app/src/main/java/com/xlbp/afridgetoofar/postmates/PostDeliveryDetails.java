package com.xlbp.afridgetoofar.postmates;

import android.content.Context;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.xlbp.afridgetoofar.enums.AppState;
import com.xlbp.afridgetoofar.enums.PostAppState;
import com.xlbp.afridgetoofar.helpers.Helpers;


public class PostDeliveryDetails extends PostBase
{
    public PostDeliveryDetails(PostActivity postActivity, WebView webView, String searchAddress)
    {
        super(postActivity, webView);

        _searchAddress = searchAddress;
    }

    @Override
    public void parseHtml(String html)
    {
        if (html.contains("Use Current Location"))
        {
            AppState.setPostmatesAppState(PostAppState.DeliveryDetailsReady);

            enterAddress();
        }
        else
        {
            retrieveHtml();
        }
    }

    private void enterAddress()
    {
        InputMethodManager imm = (InputMethodManager) postActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null)
        {
            // We need to loop until the keyboard shows. Otherwise the input value will get messed up
            if (imm.isAcceptingText())
            {
                // convert the string to a char array
                char[] addressFullChars = _searchAddress.toCharArray();

                // need a KeyCharacterMap in order to call getEvents
                KeyCharacterMap fullKeyMap = KeyCharacterMap.load(KeyCharacterMap.FULL);

                // map the chars into key event
                KeyEvent[] keySequence = fullKeyMap.getEvents(addressFullChars);

                // dispatch all the key events
                for (KeyEvent keyEvent : keySequence)
                {
                    postActivity.dispatchKeyEvent(keyEvent);
                }

                AppState.setPostmatesAppState(PostAppState.MainMenuLoading);

                // need to press enter twice
                KeyEvent enterKeyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
                postActivity.dispatchKeyEvent(enterKeyEventDown);
                postActivity.dispatchKeyEvent(enterKeyEventDown);

                Helpers.hideKeyboard(postActivity);

                postActivity.onDocumentComplete();
            }
            else
            {
                retrieveHtml();
            }
        }
    }


    private String _searchAddress;
}