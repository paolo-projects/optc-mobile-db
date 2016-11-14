package it.instruman.treasurecruisedatabase;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
