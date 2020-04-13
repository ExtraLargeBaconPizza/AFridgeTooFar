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
        String javaScript = "javascript:var readyStateCheckInterval = setInterval(function() {if (document.readyState === 'complete') {clearInterval(readyStateCheckInterval); return('document complete');}}, 10);";

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

    public static void clickElementByKeyword(WebView webView, String elementTag, String keyword)
    {
        String javascript = "javascript: (function(){var elements = document.getElementsByTagName('" + elementTag + "'); for(var i=0; i < elements.length; i++){ var innerHtml = elements[i].innerHTML; if(innerHtml != '' && innerHtml.includes('" + keyword + "')){ elements[i].click(); }}})();";

        webView.loadUrl(javascript);
    }

    public static void clickElementById(WebView webView, String id)
    {
        String javascript = "javascript: (function(){ document.getElementById('" + id + "').click(); })();";

        webView.loadUrl(javascript);
    }

    public static void getAllHrefsAndInnerText(WebView webView, ValueCallback<String> valueCallBack)
    {
        String javascript = "javascript: (function(){ var result = ''; var elem = document.getElementsByTagName('a'); for (var i = 0; i < elem.length; i++) { result += 'element'; result += elem[i].href; result += 'innerText' + elem[i].innerText; } return result; })();";

        webView.evaluateJavascript(javascript, valueCallBack);
    }

    public static void getHrefsInnerText(WebView webView, String href, ValueCallback<String> valueCallBack)
    {
        String javascriptFunction = "javascript: (function(){ return(document.querySelectorAll(\"a[href='" + href + "']\")[0].parentElement.innerText); })();";

        webView.evaluateJavascript(javascriptFunction, valueCallBack);
    }
}
