package it.instruman.treasurecruisedatabase;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
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

        setContentView(R.layout.activity_dmg_calc);

        final WebView webView = (WebView) findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();

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
        });

        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
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

        ImageButton backBtn = (ImageButton)findViewById(R.id.dmgcalc_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton navigateBackBtn = (ImageButton)findViewById(R.id.dmgcalc_navigateback);
        ImageButton navigateForwardBtn = (ImageButton)findViewById(R.id.dmgcalc_navigateforward);
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
