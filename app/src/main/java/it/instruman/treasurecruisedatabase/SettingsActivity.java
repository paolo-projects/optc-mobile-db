package it.instruman.treasurecruisedatabase;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AppCompatDelegate mDelegate;
    private boolean lanChanged = false;
    Intent returndata = new Intent();

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isSystemAlertPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if(key.equals(getString(R.string.pref_language)))
        {
            returndata.putExtra(MainActivity.LANG_PREF_CHANGED, true);
            lanChanged = true;
        } else if (key.equals(getString(R.string.pref_daynight_theme)))
        {
            if(!lanChanged)
                returndata.putExtra(MainActivity.THEMEDAYNIGHT_CHANGED, true);
        } else if (key.equals(getString(R.string.pref_overlay)))
        {
            if(sharedPreferences.getBoolean(key, false)) {
                if ((Build.VERSION.SDK_INT >= 23) && !isSystemAlertPermissionGranted(this)) {
                    (new AlertDialog.Builder(this)).setMessage(getString(R.string.overlay_message_string)).setPositiveButton(getString(R.string.enable_unknown_sources_btn), new DialogInterface.OnClickListener() {
                        @TargetApi(23)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            startActivity(myIntent);
                        }
                    }).show();
                }
            }
        }
    }
    private int appTheme;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        appTheme = R.style.AppThemeTeal;
        if(b!=null) {
            appTheme = b.getInt("appTheme");
        }
        setTheme(appTheme);
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar actToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(actToolbar);
        android.support.v7.app.ActionBar actionBar = getDelegate().getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getString(R.string.prefs_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        addPreferencesFromResource(R.xml.preferences);
        Preference upd_pref = findPreference(getString(R.string.pref_update_app));
        upd_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                returndata.putExtra(MainActivity.UPDATE_APP_PREF, true);
                finish();
                return false;
            }
        });

        Preference donate_pref = findPreference(getString(R.string.pref_donation));
        donate_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                /*Spanned result;
                (new AlertDialog.Builder(context)).setTitle("Thank you!").setMessage(Html.fromHtml(getString(R.string.prefs_donation_message)))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=NRLCLUHJ3QF9W";
                        Intent f = new Intent(Intent.ACTION_VIEW);
                        f.setData(Uri.parse(url));
                        startActivity(f);
                    }
                }).setIcon(R.drawable.ic_happy).show();*/
                (new AlertDialog.Builder(context)).setTitle(R.string.prefDonationsDisabledTitle).setMessage(R.string.prefDonationsDisabledText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setIcon(android.R.drawable.ic_delete).show();
                return true;
            }
        });
        Preference clearThumbCache = findPreference(getString(R.string.prefClearThumbCacheKey));
        clearThumbCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                returndata.putExtra(MainActivity.CLEAR_THUMB_CACHE_PREF, true);
                finish();
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public void finish() {
        setResult(0, returndata);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}