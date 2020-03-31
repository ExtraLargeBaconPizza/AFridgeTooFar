package com.elbp.afridgetoofar.ubereats;

import android.webkit.WebView;

public abstract class UberBase
{
    public UberBase(UberActivity uberEatsActivity, WebView webView)
    {
        this.uberActivity = uberEatsActivity;
        this.webView = webView;
    }

    abstract void parseHtml(String html);

    protected void onDocumentComplete()
    {
        retrieveHtml();
    }

    protected void retrieveHtml()
    {
        // !!! This function is called often as a recursive call so be careful when using it !!!

        // if we ever need a tiny bit of perf improvement we can call getElementsByTagName('main')
        // instead.  However, some pages don't have a main element on uber, so this would need to be
        // accounted for
        webView.evaluateJavascript("(function() { return (document.getElementById('root').innerHTML); })();",
                html ->
                {
                    parseHtml(html);
                });
    }

    protected void clickLinkByKeyword(String linkKeyword)
    {
        String javascriptFunction = "javascript: (function(){var elem = document.getElementsByTagName('a'); for(var i=0; i < elem.length; i++){ var t = elem[i].innerHTML; if(t != '' && t.includes('" + linkKeyword + "')){ elem[i].click(); }}})();";

        uberActivity.webViewLoadUrl(javascriptFunction);
    }

    protected void clickButtonByKeyword(String buttonKeyword)
    {
        String javascriptFunction = "javascript: (function(){var elem = document.getElementsByTagName('button'); for(var i=0; i < elem.length; i++){ var t = elem[i].innerHTML; if(t != '' && t.includes('" + buttonKeyword + "')){ elem[i].click(); }}})();";

        uberActivity.webViewLoadUrl(javascriptFunction);
    }

    protected void clickElementById(String elementId)
    {
        String javascriptFunction = "javascript: (function(){ document.getElementById('" + elementId + "').click(); })();";

        uberActivity.webViewLoadUrl(javascriptFunction);
    }

    protected void test(String elementId)
    {
        String javascriptFunction = "javascript: (function(){ alert(document.getElementById('" + elementId + "').select()); })();";

        uberActivity.webViewLoadUrl(javascriptFunction);
    }


    protected UberActivity uberActivity;
    protected WebView webView;
}
