package com.xlbp.afridgetoofar.helpers;

import android.webkit.ValueCallback;
import android.webkit.WebView;

public class Javascript
{
    public static void startDocumentReadyStateCheck(WebView webView, ValueCallback<String> valueCallBack)
    {
        // Knowing that the document is in an "interactive" ready state may be useful at some point

        // We need to get determine that the document loading is complete through this javascript
        // function because the WebViews onPageFinished callback will not trigger when we've
        // navigated to a new page through a button click.
        String javaScript = "(function() { var readyStateCheckInterval = setInterval(function() {if (document.readyState === 'complete') {clearInterval(readyStateCheckInterval); return('document complete');}}, 10); } )();";

        // We need to use evaluateJavascript so we have a callback once the document is complete
        webView.evaluateJavascript(javaScript, valueCallBack);
    }

    public static void getRootInnerHtml(WebView webView, ValueCallback<String> valueCallback)
    {
        // !!! This function is called often recursively so be careful when using it !!!

        // if we ever need a tiny bit of perf improvement we can call getElementsByTagName('main')
        // instead.  However, some pages don't have a main element on uber, so this would need to be
        // accounted for
        String javaScript = "(function() { return (document.getElementById('root').innerHTML); })();";

        webView.evaluateJavascript(javaScript, valueCallback);
    }

    public static void getBodyInnerHtml(WebView webView, ValueCallback<String> valueCallback)
    {
        String javaScript = "(function() { return (document.body.innerHTML); })();";

        webView.evaluateJavascript(javaScript, valueCallback);
    }

    public static void clickElementByKeyword(WebView webView, String elementTag, String keyword)
    {
        String javaScript = "(function(){var elements = document.getElementsByTagName('" + elementTag + "'); for(var i=0; i < elements.length; i++){ var innerHtml = elements[i].innerHTML; if(innerHtml != '' && innerHtml.includes('" + keyword + "')){ elements[i].click(); }}})();";

        webView.loadUrl(javaScript);
    }

    public static void clickElementById(WebView webView, String id)
    {
        String javaScript = "(function(){ document.getElementById('" + id + "').click(); })();";

        webView.loadUrl(javaScript);
    }

    public static void getAllHrefsAndInnerText(WebView webView, ValueCallback<String> valueCallBack)
    {
        String javascript = "(function(){ var result = ''; var elem = document.getElementsByTagName('a'); for (var i = 0; i < elem.length; i++) { result += 'element'; result += elem[i].href; result += 'innerText' + elem[i].innerText; } return result; })();";

        webView.evaluateJavascript(javascript, valueCallBack);
    }

    public static void getActiveElementPlaceHolderText(WebView webView, ValueCallback<String> valueCallBack)
    {
        String javaScript = "(function(){ return document.activeElement.placeholder })();";

        webView.evaluateJavascript(javaScript, valueCallBack);
    }

    public static void getGrubHubMainMenuHrefsInnerText(WebView webView, ValueCallback<String> valueCallBack)
    {
        String javaScript = "(function(){ var result = ''; var elem = document.getElementsByTagName('ghs-restaurant-card'); for (var i = 0; i < elem.length; i++) { result += 'element'; result += elem[i].getElementsByTagName('a')[1]; result += 'innerText' + elem[i].innerText; } return result; })();";

        webView.evaluateJavascript(javaScript, valueCallBack);
    }

    public static void grubHubClickAllMenuHeaders(WebView webView, ValueCallback<String> valueCallBack)
    {
        String javaScript = "(function() { var expandMenuHeadersInterval = setInterval(function(){ var elem = document.getElementsByClassName('menuSection-header u-background--tinted isClosed'); if (elem.length > 0){ clearInterval(expandMenuHeadersInterval); for (var i = 0; i < elem.length; i++) { elem[i].click(); }}}, 50); })();";

        webView.evaluateJavascript(javaScript, valueCallBack);
    }

    public static void getGrubhubRestaurantMenuInnerText(WebView webView, ValueCallback<String> valueCallBack)
    {
        String javaScript = "(function(){ var result = ''; var elem = document.getElementsByTagName('ghs-restaurant-menu-item'); for (var i = 0; i < elem.length; i++) { result += elem[i].innerText + 'element';  } return result; })();";

        webView.evaluateJavascript(javaScript, valueCallBack);
    }

    public static void getPostMatesMainMenuHrefsInnerText(WebView webView, ValueCallback<String> valueCallBack)
    {
        String javaScript = "(function(){ var result = ''; var elem = document.querySelectorAll('[role=\"presentation\"]'); for (var i = 0; i < elem.length; i++) { result += 'element'; result += elem[i].getElementsByTagName('a')[0]; result += 'innerText' + elem[i].innerText;  } return result; })();";

        webView.evaluateJavascript(javaScript, valueCallBack);
    }

    public static void getPostMatesRestaurantMenuInnerText(WebView webView, ValueCallback<String> valueCallBack)
    {
        String javaScript = "(function(){ var result = ''; var elem = document.getElementsByClassName('product-container'); for (var i = 0; i < elem.length; i++) { result += elem[i].innerText + 'element';  } return result; })();";

        webView.evaluateJavascript(javaScript, valueCallBack);
    }
}
