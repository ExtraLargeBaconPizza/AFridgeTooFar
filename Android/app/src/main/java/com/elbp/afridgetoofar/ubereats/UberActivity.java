package com.elbp.afridgetoofar.ubereats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

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

    @Override
    public void onBackPressed()
    {
        finish();
        return;
    }

    public static void updateAppStateTextView(UberAppState appState)
    {
        _appStateTextView.setText("\nCurrent AppState: " + appState);
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
//                _uberEatsRestaurantMenu.onDocumentComplete();
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
        initIsDebugMode();

        if (!_isDebugMode)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initViews();

        initWebView();

        initUberEats();
    }

    private void initIsDebugMode()
    {
        Bundle extras = getIntent().getExtras();

        _isDebugMode = (extras != null) ? _isDebugMode = extras.getBoolean("DebugMode") : false;
    }

    private void initViews()
    {
        _webView = findViewById(R.id.webview);
        _appStateTextView = findViewById(R.id.appStateTextView);

        if (!_isDebugMode)
        {
            // The webView doesnt see to work correctly if we set it visibility to invisible or gone
            _webView.setAlpha(0);
            _webView.setOnTouchListener((v, event) -> false);
        }
    }

    private void initUberEats()
    {
        AppState.setUberEatsAppState(UberAppState.InitialLoading);

        _uberEatsInitial = new UberInitial(this, _webView);
        _uberEatsDeliveryDetails = new UberDeliveryDetails(this, _webView);
        _uberEatsMainMenu = new UberMainMenu(this, _webView);
        _uberEatsRestaurantMenu = new UberRestaurantMenu(this, _webView);
        _uberEatsFoodItem = new UberFoodItem(this, _webView);
        _uberEatsStartNewOrder = new UberStartNewOrder(this, _webView);
    }

    private void initWebView()
    {
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.setWebViewClient(new UberWebViewClient(this));
        _webView.setWebChromeClient(new WebChromeClient());

        // @jim the follow two lines remove uber eats cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        webViewLoadUrl(uberEatsUrl);
    }


    private boolean _isDebugMode;
    private WebView _webView;
    private static TextView _appStateTextView;

    private UberInitial _uberEatsInitial;
    private UberDeliveryDetails _uberEatsDeliveryDetails;
    private UberMainMenu _uberEatsMainMenu;
    private UberRestaurantMenu _uberEatsRestaurantMenu;
    private UberFoodItem _uberEatsFoodItem;
    private UberStartNewOrder _uberEatsStartNewOrder;
}
