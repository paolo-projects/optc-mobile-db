package it.instruman.treasurecruisedatabase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.mozilla.javascript.tools.debugger.Main;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AppCompatDelegate mDelegate;
    private boolean lanChanged = false;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if(key.equals(getString(R.string.pref_language)))
        {
            setResult(MainActivity.LANG_PREF_CHANGED);
            lanChanged = true;
        } else if (key.equals(getString(R.string.pref_daynight_theme)))
        {
            if(!lanChanged)
                setResult(MainActivity.THEMEDAYNIGHT_CHANGED);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        int appTheme = R.style.AppThemeTeal;
        if(b!=null) {
            appTheme = b.getInt("appTheme");
        }
        setTheme(appTheme);
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar actToolbar = (Toolbar) findViewById(R.id.toolbar);
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
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        Preference upd_pref = findPreference(getString(R.string.pref_update_app));
        upd_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setResult(MainActivity.UPDATE_APP_PREF);
                finish();
                return false;
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
}