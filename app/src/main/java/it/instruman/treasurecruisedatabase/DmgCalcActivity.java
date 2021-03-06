package it.instruman.treasurecruisedatabase;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class DmgCalcActivity extends AppCompatActivity {
private String DMGCALC_URL = "http://optc-db.github.io/damage/";

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        String lan = "";
        if(b != null) {
            lan = b.getString("lan");
        }

        switch(lan)
        {
            case "it":
                DMGCALC_URL = "http://www.one-piece-treasure-cruise-italia.org/damage/";
                break;
            case "es":
                DMGCALC_URL = "http://optc-sp.github.io/damage/";
                break;
            case "":
            default:
                DMGCALC_URL = "http://optc-db.github.io/damage/";
                break;
        }

        int themeR = b.getInt("theme");
        setTheme(themeR);

        setContentView(R.layout.activity_dmg_calc);

        final WebView webView = findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                return handleUri(uri);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return handleUri(uri);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            private boolean handleUri(final Uri uri) {
                final String host = uri.getHost();
                final String scheme = uri.getScheme();
                final Uri dmg_url = Uri.parse(DMGCALC_URL);
                // Based on some condition you need to determine if you are going to load the url
                // in your web view itself or in a browser.
                // You can use `host` or `scheme` or any part of the `uri` to decide.
                if (dmg_url.getHost().equals(host)) {
                    // Returning false means that you are going to load this url in the webView itself
                    return false;
                } else {
                    // Returning true means that you need to handle what to do with the url
                    // e.g. open web page in a Browser
                    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        if(Build.VERSION.SDK_INT >= 21)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String databasePath = this.getApplicationContext().getDir("databases", Context.MODE_PRIVATE).getPath();
            webSettings.setDatabasePath(databasePath);
        }

        if(Build.VERSION.SDK_INT < 18)
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.loadUrl(DMGCALC_URL);

        if (Build.VERSION.SDK_INT >= 21) CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager.allowFileSchemeCookies();
        CookieManager.getInstance().setAcceptCookie(true);

        ImageButton backBtn = findViewById(R.id.dmgcalc_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton navigateBackBtn = findViewById(R.id.dmgcalc_navigateback);
        ImageButton navigateForwardBtn = findViewById(R.id.dmgcalc_navigateforward);
        navigateBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.goBack();
            }
        });
        navigateForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.goForward();
            }
        });
    }
}
