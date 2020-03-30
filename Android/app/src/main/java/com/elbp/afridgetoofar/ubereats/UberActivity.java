package com.elbp.afridgetoofar.ubereats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.elbp.afridgetoofar.R;

public class UberActivity extends AppCompatActivity
{
    public static String uberEatsUrl = "https://www.ubereats.com";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        init();
    }

    public void webViewLoadUrl(String url)
    {
        _webView.loadUrl(url);
    }

    public void onDocumentComplete()
    {
        switch (AppState.getUberEatsAppState())
        {
            case InitialLoading:
                _uberEatsInitial.onDocumentComplete();
                break;
            case DeliveryDetailsLoading:
                _uberEatsDeliveryDetails.onDocumentComplete();
                break;
            case MainMenuLoading:
                _uberEatsMainMenu.onDocumentComplete();
                break;
            case RestaurantMenuLoading:
                _uberEatsRestuarantMenu.onDocumentComplete();
                break;
            case FoodItemLoading:
                _uberEatsFoodItem.onDocumentComplete();
                break;
            case FoodItemAdded:
                _uberEatsStartNewOrder.onDocumentComplete();
                break;
        }
    }

    private void init()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initWebView();

        initUberEats();
    }

    private void initUberEats()
    {
        _uberEatsInitial = new UberInitial(this, _webView);
        _uberEatsDeliveryDetails = new UberDeliveryDetails(this, _webView);
        _uberEatsMainMenu = new UberMainMenu(this, _webView);
        _uberEatsRestuarantMenu = new UberRestaurantMenu(this, _webView);
        _uberEatsFoodItem = new UberFoodItem(this, _webView);
        _uberEatsStartNewOrder = new UberStartNewOrder(this, _webView);
    }

    private void initWebView()
    {
        _webView = findViewById(R.id.webview);

        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.setWebViewClient(new UberWebViewClient(this));
        _webView.setWebChromeClient(new WebChromeClient());

        // @jim the follow two lines remove ubereats cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        webViewLoadUrl(uberEatsUrl);
    }


    private WebView _webView;

    private UberInitial _uberEatsInitial;
    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestuarantMenu;
    private UberFoodItem _uberEatsFoodItem;
    private UberStartNewOrder _uberEatsStartNewOrder;
}
