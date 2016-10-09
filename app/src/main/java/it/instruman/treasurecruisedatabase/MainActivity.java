package it.instruman.treasurecruisedatabase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final Context context = this;

    /*
        ################### APP VERSION ##################
    */
    private final static Double APP_VERSION = 1.1;
/*
    ##################################################
 */


    private final String UNITS_CACHED_NAME = "units.serial";
    private final String DETAILS_CACHED_NAME = "details.serial";
    private final String COOLDOWNS_CACHED_NAME = "cooldowns.serial";

    ImageView sortName, sortType, sortStars;
    Integer nsort, tsort, ssort;
    ListView lview;
    listViewAdapter adapter;
    EditText filterText;
    Activity activity;
    Dialog dlg_hwnd = null;
    Dialog loadingdlg_hwnd = null;

    private ArrayList<HashMap> list, original_list;
    ImageView.OnClickListener sortNameOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (nsort == 0) sortNameAscending();
            else if (nsort == 1) sortNameDescending();
            else sortNameAscending();
        }
    };
    ImageView.OnClickListener sortTypeOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (tsort == 0) sortTypeAscending();
            else if (tsort == 1) sortTypeDescending();
            else sortTypeAscending();
        }
    };
    ImageView.OnClickListener sortStarsOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ssort == 0) sortStarsAscending();
            else if (ssort == 1) sortStarsDescending();
            else sortStarsAscending();
        }
    };
    private HashMap<Integer, Map> details;
    private ArrayList<CoolDowns> cooldowns;
    ListView.OnItemClickListener lvOnClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap item = (HashMap) adapter.getItem(position);
            boolean multiCD = false;
            final Dialog dialog = new Dialog(context, R.style.AppTheme);
            dialog.setContentView(R.layout.character_info);
            TextView title = (TextView) dialog.findViewById(R.id.titleText);
            title.setText((String) item.get(Constants.NAME));

            // set the custom dialog components - text, image and button
            ImageView image = (ImageView) dialog.findViewById(R.id.char_img_big);

            Glide
                    .with(activity)
                    .load("http://onepiece-treasurecruise.com/wp-content/uploads/c" + convertID((Integer) item.get(Constants.ID)) + ".png")
                    .into(image);

            dialog.setCanceledOnTouchOutside(true);

            //NOW WE SHOULD SET EVERYTHING (OUCH!)
            TextView class1 = (TextView) dialog.findViewById(R.id.class1Text);
            TextView class2 = (TextView) dialog.findViewById(R.id.class2Text);
            TextView type = (TextView) dialog.findViewById(R.id.typeText);
            TextView stars = (TextView) dialog.findViewById(R.id.starsText);
            TextView cost = (TextView) dialog.findViewById(R.id.costText);

            TextView combo = (TextView) dialog.findViewById(R.id.comboText);
            TextView slots = (TextView) dialog.findViewById(R.id.slotsText);
            TextView maxlevel = (TextView) dialog.findViewById(R.id.maxlevelText);
            TextView exptomax = (TextView) dialog.findViewById(R.id.exptomaxText);

            TextView lvl1hp = (TextView) dialog.findViewById(R.id.lvl1hpText);
            TextView lvl1atk = (TextView) dialog.findViewById(R.id.lvl1atkText);
            TextView lvl1rcv = (TextView) dialog.findViewById(R.id.lvl1rcvText);

            TextView maxhp = (TextView) dialog.findViewById(R.id.lvlmaxhpText);
            TextView maxatk = (TextView) dialog.findViewById(R.id.lvlmaxatkText);
            TextView maxrcv = (TextView) dialog.findViewById(R.id.lvlmaxrcvText);
            TextView lvlmax = (TextView) dialog.findViewById(R.id.lvlmaxtext);

            TextView captability = (TextView) dialog.findViewById(R.id.captabilityText);
            TextView specname = (TextView) dialog.findViewById(R.id.specnameText);
            TextView specability = (TextView) dialog.findViewById(R.id.specabilityText);
            TextView speccooldown = (TextView) dialog.findViewById(R.id.speccooldownTxt);
            TextView speccooldownTitle = (TextView) dialog.findViewById(R.id.speccooldownTitle);

            Object classes = item.get(Constants.CLASSES);
            if (classes.getClass().equals(String.class)) {
                class1.setText((String) classes);
                class2.setText("");
            } else if (classes.getClass().equals(NativeArray.class)) {
                try {
                    class1.setText((String) ((NativeArray) classes).get(0));
                    class2.setText((String) ((NativeArray) classes).get(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            type.setText((String) item.get(Constants.TYPE));
            stars.setText(String.valueOf(item.get(Constants.STARS)));
            cost.setText((String) item.get(Constants.COST));

            combo.setText((String) item.get(Constants.COMBO));
            slots.setText((String) item.get(Constants.SOCKETS));
            maxlevel.setText((String) item.get(Constants.MAXLVL));
            exptomax.setText((String) item.get(Constants.EXPTOMAX));

            lvl1hp.setText((String) item.get(Constants.LVL1HP));
            lvl1atk.setText((String) item.get(Constants.LVL1ATK));
            lvl1rcv.setText((String) item.get(Constants.LVL1RCV));
            lvlmax.setText((String) item.get(Constants.MAXLVL));

            maxhp.setText((String) item.get(Constants.MAXHP));
            maxatk.setText((String) item.get(Constants.MAXATK));
            maxrcv.setText((String) item.get(Constants.MAXRCV));

            try {
                Map extra = details.get(item.get(Constants.ID));
                captability.setText((String) extra.get(Constants2.CAPTAIN));
                specname.setText((String) extra.get(Constants2.SPECIALNAME));
                Object specobject = extra.get(Constants2.SPECIAL);
                if (specobject.getClass().equals(NativeArray.class)) {
                    List<Map> specmulti = (List<Map>) specobject;
                    String txt = "";
                    for (int n = 0; n < specmulti.size(); n++) {
                        Map<String, Object> currstep = specmulti.get(n);
                        txt += "Level " + (n + 1) + ": ";
                        txt += currstep.get("description");
                        txt += System.getProperty("line.separator");
                        CoolDowns cd = new CoolDowns(currstep.get("cooldown"));
                        txt += cd.print();
                        txt += System.getProperty("line.separator");
                    }
                    specability.setText(txt);
                    multiCD = true;

                } else specability.setText((String) extra.get(Constants2.SPECIAL));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!multiCD) {
                CoolDowns cdwns = cooldowns.get((Integer) item.get(Constants.ID));
                speccooldown.setText(cdwns.print());
            } else {
                speccooldown.setVisibility(View.INVISIBLE);
                speccooldownTitle.setVisibility(View.INVISIBLE);
            }

            ImageButton backbtn = (ImageButton) dialog.findViewById(R.id.backBtn);
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            dlg_hwnd = dialog;

            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.97);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(width, height);
            }
        }
    };

    public void showLoading(Context context) {
        final Dialog loading = new Dialog(context, R.style.AppTheme);
        loading.setContentView(R.layout.loading_layout);
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(false);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        loading.show();
        loadingdlg_hwnd = loading;
    }

    public void hideLoading() {
        if ((loadingdlg_hwnd != null) && (loadingdlg_hwnd.isShowing())) {
            loadingdlg_hwnd.dismiss();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        activity = this;

        nsort = tsort = ssort = 0;

        sortName = (ImageView) findViewById(R.id.sortName);
        sortType = (ImageView) findViewById(R.id.sortType);
        sortStars = (ImageView) findViewById(R.id.sortStars);
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortName.setOnClickListener(sortNameOnClick);
        sortType.setOnClickListener(sortTypeOnClick);
        sortStars.setOnClickListener(sortStarsOnClick);

        final ImageButton filterBtn = (ImageButton) findViewById(R.id.filterBtn);
        filterText = (EditText) findViewById(R.id.filterText);
        filterText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (filterBtn != null) filterBtn.performClick();
                    handled = true;
                }
                return handled;
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filterText.getText().toString().equals("")) {
                    list = original_list;
                    String tocheck = filterText.getText().toString();
                    ArrayList<HashMap> result = new ArrayList<>();
                    for (HashMap foo : list) {
                        if (((String) foo.get(Constants.NAME)).toLowerCase().contains(tocheck.toLowerCase()))
                            result.add(foo);
                    }
                    list = result;
                    adapter = new listViewAdapter(activity, list);
                    lview.setAdapter(adapter);
                    sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
                    sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
                    sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
                    nsort = tsort = ssort = 0;
                } else {
                    list = original_list;
                    adapter = new listViewAdapter(activity, list);
                    lview.setAdapter(adapter);
                    sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
                    sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
                    sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
                    nsort = tsort = ssort = 0;
                }
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        lview = (ListView) findViewById(R.id.listView1);

        //CREATE EMPTY DATABASES
        list = new ArrayList<>();
        details = new HashMap<>();
        cooldowns = new ArrayList<>();
        original_list = new ArrayList<>();
        // run async task to download or load DB
        (new DownloadData()).execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ((dlg_hwnd != null) && (dlg_hwnd.isShowing())) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.97);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85);
            if (dlg_hwnd.getWindow() != null) {
                dlg_hwnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dlg_hwnd.getWindow().setLayout(width, height);
            }
        }
    }

    private String getFileURL(String uri) {
        try {
            URL url = new URL(uri);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder bdr = new StringBuilder();
            String endLine = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) bdr.append(line + endLine);
            //res = res + line + System.getProperty("line.separator");
            br.close();
            is.close();
            return bdr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private JSONArray getJSON(String url) {
        //Make the actual request - method displayed above

        try {
            String result = getFileURL(url);
            return new JSONArray(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        //Parse the result for the JSON data
    }

    private JSONObject getJSONObj(String url) {
        //Make the actual request - method displayed above

        try {
            String result = getFileURL(url);
            return new JSONObject(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        //Parse the result for the JSON data
    }

    public Object parseJScript(String uri, String objname) {

        String dump = getFileURL(uri);

        // Every Rhino VM begins with the enter()
        // This Context is not Android's Context

        org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();

        // Turn off optimization to make Rhino Android compatible

        rhino.setOptimizationLevel(-1);

        try {
            Scriptable scope = rhino.initStandardObjects();

            // Note the forth argument is 1, which means the JavaScript source has
            // been compressed to only one line using something like YUI
            rhino.evaluateString(scope, "var window = {" + objname + ":null};" + dump, "JavaScript", 1, null);
            // Get the functionName defined in JavaScriptCode
            Object x = scope.get("window", scope);
            Map<String, Object> y = (Map<String, Object>) x;
            return y.get(objname);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            org.mozilla.javascript.Context.exit();
        }
        return null;
    }

    private void populateList() {
        List<List> characters = (List) parseJScript("https://optc-db.github.io/common/data/units.js", "units");
        //JSONArray characters = getJSON(CharacterListJSONUrl);
        int char_size = characters.size();
        if (char_size > 0) {
            for (int i = 0; i < char_size; i++) {
                try {
                    List arr_2 = characters.get(i);
                    String name = (arr_2.get(0) == null) ? "" : (String) arr_2.get(0);
                    String type = (arr_2.get(1) == null) ? "" : (String) arr_2.get(1);
                    Integer stars = (arr_2.get(3) == null) ? 1 : ((Double) arr_2.get(3)).intValue();
                    Object classes = (arr_2.get(2) == null) ? null : arr_2.get(2);
                    String cost = (arr_2.get(4) == null) ? "" : String.valueOf(((Double) arr_2.get(4)).intValue());
                    String combo = (arr_2.get(5) == null) ? "" : String.valueOf(((Double) arr_2.get(5)).intValue());
                    String sockets = (arr_2.get(6) == null) ? "" : String.valueOf(((Double) arr_2.get(6)).intValue());
                    String maxlvl = (arr_2.get(7) == null) ? "" : String.valueOf(((Double) arr_2.get(7)).intValue());
                    String exptomax = (arr_2.get(8) == null) ? "" : String.valueOf(((Double) arr_2.get(8)).intValue());
                    String lvl1hp = (arr_2.get(9) == null) ? "" : String.valueOf(((Double) arr_2.get(9)).intValue());
                    String lvl1atk = (arr_2.get(10) == null) ? "" : String.valueOf(((Double) arr_2.get(10)).intValue());
                    String lvl1rcv = (arr_2.get(11) == null) ? "" : String.valueOf(((Double) arr_2.get(11)).intValue());
                    String maxhp = (arr_2.get(12) == null) ? "" : String.valueOf(((Double) arr_2.get(12)).intValue());
                    String maxatk = (arr_2.get(13) == null) ? "" : String.valueOf(((Double) arr_2.get(13)).intValue());
                    String maxrcv = (arr_2.get(14) == null) ? "" : String.valueOf(((Double) arr_2.get(14)).intValue());

                    HashMap temp = new HashMap();
                    temp.put(Constants.NAME, name);
                    temp.put(Constants.TYPE, type);
                    temp.put(Constants.STARS, stars);
                    temp.put(Constants.ID, i + 1);
                    temp.put(Constants.CLASSES, classes);
                    temp.put(Constants.COST, cost);
                    temp.put(Constants.COMBO, combo);
                    temp.put(Constants.SOCKETS, sockets);
                    temp.put(Constants.MAXLVL, maxlvl);
                    temp.put(Constants.EXPTOMAX, exptomax);
                    temp.put(Constants.LVL1HP, lvl1hp);
                    temp.put(Constants.LVL1ATK, lvl1atk);
                    temp.put(Constants.LVL1RCV, lvl1rcv);
                    temp.put(Constants.MAXHP, maxhp);
                    temp.put(Constants.MAXATK, maxatk);
                    temp.put(Constants.MAXRCV, maxrcv);
                    list.add(temp);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("CYCLE NUM", String.valueOf(i));
                }
            }
        }

        original_list = list;
        Map<Integer, Map> details_js = (Map<Integer, Map>) parseJScript("https://optc-db.github.io/common/data/details.js", "details");
        if ((details_js != null) && (details_js.size() > 0)) {
            for (Map.Entry<Integer, Map> entry : details_js.entrySet()) {
                Map<String, Object> value = entry.getValue();
                HashMap map = new HashMap();
                map.put(Constants2.SPECIAL, (value.containsKey("special") ? value.get("special") : ""));
                map.put(Constants2.SPECIALNAME, (value.containsKey("specialName") ? value.get("specialName") : ""));
                map.put(Constants2.CAPTAIN, (value.containsKey("captain") ? value.get("captain") : ""));
                details.put(entry.getKey(), map);
            }
        }
        /*JSONObject dett = getJSONObj(CharacterDetailsJSONUrl);
        if ((dett != null) && (dett.length() > 0)) {
            try {
                Iterator<String> temp = dett.keys();

                while (temp.hasNext()) {
                    String key = temp.next();
                    if (details.size() != Integer.parseInt(key)) { //indexes must always match character number!
                        while (details.size() != Integer.parseInt(key)) details.add(new HashMap());
                    }

                    JSONObject value = dett.getJSONObject(key);
                    HashMap map = new HashMap();
                    map.put(Constants2.ID, Integer.parseInt(key));
                    map.put(Constants2.SPECIAL, (value.has("special") ? value.get("special") : ""));
                    map.put(Constants2.SPECIALNAME, (value.has("specialName") ? value.get("specialName") : ""));
                    map.put(Constants2.CAPTAIN, (value.has("captain") ? value.get("captain") : ""));
                    details.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
        List<Object> coolds = (List) parseJScript("https://optc-db.github.io/common/data/cooldowns.js", "cooldowns");
        if ((coolds != null) && (coolds.size() > 0)) {
            cooldowns.add(new CoolDowns());
            for (int i = 0; i < coolds.size(); i++) {
                Object entry = coolds.get(i);
                if (entry == null) cooldowns.add(new CoolDowns());
                else if (entry.getClass().equals(Integer.class))
                    cooldowns.add(new CoolDowns((Integer) coolds.get(i)));
                else if (entry.getClass().equals(NativeArray.class)) {
                    List<Double> entry_array = (List<Double>) coolds.get(i);
                    Integer a = entry_array.get(0).intValue();
                    Integer b = entry_array.get(1).intValue();
                    cooldowns.add(new CoolDowns(a, b));
                } else {
                    Log.e("ERR", "Object type determination failed!");
                    cooldowns.add(new CoolDowns());
                }
                if (i == 905) {
                    Log.d("", "");
                }
            }
        }

        //CACHING DATA

        // with Serializable interface
        FileOutputStream unitsser;
        FileOutputStream detailsser;
        FileOutputStream cooldownsser;
        try {
            unitsser = openFileOutput(UNITS_CACHED_NAME, MODE_PRIVATE);
            ObjectOutputStream list_ser = new ObjectOutputStream(unitsser);
            list_ser.writeObject(list);
            unitsser.close();

            detailsser = openFileOutput(DETAILS_CACHED_NAME, MODE_PRIVATE);
            ObjectOutputStream details_ser = new ObjectOutputStream(detailsser);
            details_ser.writeObject(details);
            detailsser.close();

            cooldownsser = openFileOutput(COOLDOWNS_CACHED_NAME, MODE_PRIVATE);
            ObjectOutputStream cooldowns_ser = new ObjectOutputStream(cooldownsser);
            cooldowns_ser.writeObject(cooldowns);
            cooldownsser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*JSONArray cooldwns = getJSON(SpecialCooldownsJSONUrl);
        if ((cooldwns != null) && (cooldwns.length() > 0)) {
            try {
                cooldowns.add(new CoolDowns()); //first entry null to make index start from 1
                for (int i = 0; i < cooldwns.length(); i++) {
                    Object entry = cooldwns.get(i);
                    if (entry == JSONObject.NULL) cooldowns.add(new CoolDowns());
                    else if (entry.getClass().equals(Integer.class))
                        cooldowns.add(new CoolDowns(cooldwns.getInt(i)));
                    else if (entry.getClass().equals(JSONArray.class)) {
                        JSONArray entry_array = cooldwns.getJSONArray(i);
                        Integer a = entry_array.getInt(0);
                        Integer b = entry_array.getInt(1);
                        cooldowns.add(new CoolDowns(a, b));
                    } else Log.e("CRITICAL", "Object type determination failed!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void sortNameAscending() {
        Collections.sort(list, new MapComparator(Constants.NAME));
        adapter = new listViewAdapter(this, list);
        lview.setAdapter(adapter);
        //END
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        nsort = 1;
        tsort = ssort = 0;
    }

    public void sortNameDescending() {
        Collections.sort(list, Collections.reverseOrder(new MapComparator(Constants.NAME)));
        adapter = new listViewAdapter(this, list);
        lview.setAdapter(adapter);
        //END
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        nsort = 2;
        tsort = ssort = 0;
    }

    public void sortTypeAscending() {
        Collections.sort(list, new MapComparator(Constants.TYPE));
        adapter = new listViewAdapter(this, list);
        lview.setAdapter(adapter);
        //END
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        tsort = 1;
        nsort = ssort = 0;
    }

    public void sortTypeDescending() {
        Collections.sort(list, Collections.reverseOrder(new MapComparator(Constants.TYPE)));
        adapter = new listViewAdapter(this, list);
        lview.setAdapter(adapter);
        //END
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        tsort = 2;
        nsort = ssort = 0;
    }

    public void sortStarsAscending() {
        Collections.sort(list, new MapComparator(Constants.STARS));
        adapter = new listViewAdapter(this, list);
        lview.setAdapter(adapter);
        //END
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        ssort = 1;
        tsort = nsort = 0;
    }

    public void sortStarsDescending() {
        Collections.sort(list, Collections.reverseOrder(new MapComparator(Constants.STARS)));
        adapter = new listViewAdapter(this, list);
        lview.setAdapter(adapter);
        //END
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        ssort = 2;
        tsort = nsort = 0;
    }

    public String convertID(Integer ID) {
        if (ID < 10) return ("000" + ID.toString());
        else if (ID < 100) return ("00" + ID.toString());
        else if (ID < 1000) return ("0" + ID.toString());
        else return ID.toString();
    }

    class MapComparator implements Comparator<HashMap> {
        private final String key;

        public MapComparator(String key) {
            this.key = key;
        }

        public int compare(HashMap first,
                           HashMap second) {
            if ((first != null) && (second != null)) {
                String firstValue = String.valueOf((first.get(key) == null) ? "" : first.get(key));
                String secondValue = String.valueOf((second.get(key) == null) ? "" : second.get(key));
                return firstValue.compareTo(secondValue);
            }
            return "".compareTo("");
        }
    }

    class DownloadData extends AsyncTask<Void, Void, DwResult> {
        boolean forceDownload = false;
        boolean updateCheck = true;

        protected void onPreExecute() {
            showLoading(context);
        }

        public DownloadData(boolean forceDownload, boolean updateCheck) {
            this.forceDownload = forceDownload;
            this.updateCheck = updateCheck;
        }

        public DownloadData() {

        }

        protected DwResult doInBackground(Void... voids) {
            DwResult storage = new DwResult();
            if (!forceDownload) {
                if (!isNetworkConnected()) {
                    //IF YES CHECK IF THERE'S ANY CACHED DATA
                    if ((new File(getFilesDir(), UNITS_CACHED_NAME)).isFile() && (new File(getFilesDir(), DETAILS_CACHED_NAME)).isFile() && (new File(getFilesDir(), COOLDOWNS_CACHED_NAME)).isFile()) {
                        //LOAD CACHED DATA
                        try {
                            FileInputStream unitsser = openFileInput(UNITS_CACHED_NAME);
                            FileInputStream detailsser = openFileInput(DETAILS_CACHED_NAME);
                            FileInputStream cooldownsser = openFileInput(COOLDOWNS_CACHED_NAME);

                            ObjectInputStream units_ser = new ObjectInputStream(unitsser);
                            storage.setChars((ArrayList<HashMap>) units_ser.readObject());
                            unitsser.close();

                            ObjectInputStream details_ser = new ObjectInputStream(detailsser);
                            storage.setDetails((HashMap<Integer, Map>) details_ser.readObject());
                            detailsser.close();

                            ObjectInputStream cooldowns_ser = new ObjectInputStream(cooldownsser);
                            storage.setCooldowns((ArrayList<CoolDowns>) cooldowns_ser.readObject());
                            cooldownsser.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //IF NO CACHED DATA, JUMP POPULATELIST AND SHOW EMPTY LIST
                } else {
                    //IF CONNECTION IS ON DOWNLOAD NORMALLY THROUGH POPULATELIST
                    storage = downloadData();
                }
            } else {
                storage = downloadData();
            }
            return storage;
        }

        protected void onPostExecute(DwResult result) {
            original_list = list = result.getChars();
            details = result.getDetails();
            cooldowns = result.getCooldowns();

            adapter = new listViewAdapter(getApplicationContext(), list);
            lview.setAdapter(adapter);
            lview.setOnItemClickListener(lvOnClick);
            lview.requestFocus();
            hideLoading();
            /// UPDATE CHECK
            if (updateCheck) (new CheckUpdates()).execute();
        }

        private DwResult downloadData() {
            DwResult storage = new DwResult();
            List<List> characters = (List) parseJScript("https://optc-db.github.io/common/data/units.js", "units");
            int char_size = characters.size();
            ArrayList<HashMap> chars = new ArrayList<>();
            if (char_size > 0) {
                for (int i = 0; i < char_size; i++) {
                    try {
                        List arr_2 = characters.get(i);
                        String name = (arr_2.get(0) == null) ? "" : (String) arr_2.get(0);
                        String type = (arr_2.get(1) == null) ? "" : (String) arr_2.get(1);
                        Integer stars = (arr_2.get(3) == null) ? 1 : ((Double) arr_2.get(3)).intValue();
                        Object classes = (arr_2.get(2) == null) ? null : arr_2.get(2);
                        String cost = (arr_2.get(4) == null) ? "" : String.valueOf(((Double) arr_2.get(4)).intValue());
                        String combo = (arr_2.get(5) == null) ? "" : String.valueOf(((Double) arr_2.get(5)).intValue());
                        String sockets = (arr_2.get(6) == null) ? "" : String.valueOf(((Double) arr_2.get(6)).intValue());
                        String maxlvl = (arr_2.get(7) == null) ? "" : String.valueOf(((Double) arr_2.get(7)).intValue());
                        String exptomax = (arr_2.get(8) == null) ? "" : String.valueOf(((Double) arr_2.get(8)).intValue());
                        String lvl1hp = (arr_2.get(9) == null) ? "" : String.valueOf(((Double) arr_2.get(9)).intValue());
                        String lvl1atk = (arr_2.get(10) == null) ? "" : String.valueOf(((Double) arr_2.get(10)).intValue());
                        String lvl1rcv = (arr_2.get(11) == null) ? "" : String.valueOf(((Double) arr_2.get(11)).intValue());
                        String maxhp = (arr_2.get(12) == null) ? "" : String.valueOf(((Double) arr_2.get(12)).intValue());
                        String maxatk = (arr_2.get(13) == null) ? "" : String.valueOf(((Double) arr_2.get(13)).intValue());
                        String maxrcv = (arr_2.get(14) == null) ? "" : String.valueOf(((Double) arr_2.get(14)).intValue());

                        HashMap temp = new HashMap();
                        temp.put(Constants.NAME, name);
                        temp.put(Constants.TYPE, type);
                        temp.put(Constants.STARS, stars);
                        temp.put(Constants.ID, i + 1);
                        temp.put(Constants.CLASSES, classes);
                        temp.put(Constants.COST, cost);
                        temp.put(Constants.COMBO, combo);
                        temp.put(Constants.SOCKETS, sockets);
                        temp.put(Constants.MAXLVL, maxlvl);
                        temp.put(Constants.EXPTOMAX, exptomax);
                        temp.put(Constants.LVL1HP, lvl1hp);
                        temp.put(Constants.LVL1ATK, lvl1atk);
                        temp.put(Constants.LVL1RCV, lvl1rcv);
                        temp.put(Constants.MAXHP, maxhp);
                        temp.put(Constants.MAXATK, maxatk);
                        temp.put(Constants.MAXRCV, maxrcv);
                        chars.add(temp);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("CYCLE NUM", String.valueOf(i));
                    }
                }
            }
            storage.setChars(chars);

            Map<Integer, Map> details_js = (Map<Integer, Map>) parseJScript("https://optc-db.github.io/common/data/details.js", "details");
            HashMap<Integer, Map> det_tmp = new HashMap<>();
            if ((details_js != null) && (details_js.size() > 0)) {
                for (Map.Entry<Integer, Map> entry : details_js.entrySet()) {
                    Map<String, Object> value = entry.getValue();
                    HashMap map = new HashMap();
                    map.put(Constants2.SPECIAL, (value.containsKey("special") ? value.get("special") : ""));
                    map.put(Constants2.SPECIALNAME, (value.containsKey("specialName") ? value.get("specialName") : ""));
                    map.put(Constants2.CAPTAIN, (value.containsKey("captain") ? value.get("captain") : ""));
                    det_tmp.put(entry.getKey(), map);
                }
            }
            storage.setDetails(det_tmp);

            List<Object> coolds = (List) parseJScript("https://optc-db.github.io/common/data/cooldowns.js", "cooldowns");
            ArrayList<CoolDowns> cools_tmp = new ArrayList<>();
            if ((coolds != null) && (coolds.size() > 0)) {
                cools_tmp.add(new CoolDowns());
                for (int i = 0; i < coolds.size(); i++) {
                    Object entry = coolds.get(i);
                    if (entry == null) cools_tmp.add(new CoolDowns());
                    else if (entry.getClass().equals(Integer.class))
                        cools_tmp.add(new CoolDowns((Integer) coolds.get(i)));
                    else if (entry.getClass().equals(NativeArray.class)) {
                        List<Double> entry_array = (List<Double>) coolds.get(i);
                        Integer a = entry_array.get(0).intValue();
                        Integer b = entry_array.get(1).intValue();
                        cools_tmp.add(new CoolDowns(a, b));
                    } else {
                        Log.e("ERR", "Object type determination failed!");
                        cools_tmp.add(new CoolDowns());
                    }
                    if (i == 905) {
                        Log.d("", "");
                    }
                }
            }
            storage.setCooldowns(cools_tmp);

            //CACHING DATA

            // with Serializable interface
            FileOutputStream unitsser;
            FileOutputStream detailsser;
            FileOutputStream cooldownsser;
            try {
                unitsser = openFileOutput(UNITS_CACHED_NAME, MODE_PRIVATE);
                ObjectOutputStream list_ser = new ObjectOutputStream(unitsser);
                list_ser.writeObject(chars);
                unitsser.close();

                detailsser = openFileOutput(DETAILS_CACHED_NAME, MODE_PRIVATE);
                ObjectOutputStream details_ser = new ObjectOutputStream(detailsser);
                details_ser.writeObject(det_tmp);
                detailsser.close();

                cooldownsser = openFileOutput(COOLDOWNS_CACHED_NAME, MODE_PRIVATE);
                ObjectOutputStream cooldowns_ser = new ObjectOutputStream(cooldownsser);
                cooldowns_ser.writeObject(cools_tmp);
                cooldownsser.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return storage;
        }
    }

    private class DwResult {
        ArrayList<HashMap> this_list;
        HashMap<Integer, Map> this_details;
        ArrayList<CoolDowns> this_cooldowns;

        public DwResult(ArrayList<HashMap> list, HashMap<Integer, Map> details, ArrayList<CoolDowns> cooldowns) {
            this_list = list;
            this_details = details;
            this_cooldowns = cooldowns;
        }

        public DwResult() {
            this_list = new ArrayList<>();
            this_details = new HashMap<>();
            this_cooldowns = new ArrayList<>();
        }

        public void setChars(ArrayList<HashMap> list) {
            this_list = list;
        }

        public void setDetails(HashMap<Integer, Map> details) {
            this_details = details;
        }

        public void setCooldowns(ArrayList<CoolDowns> cooldowns) {
            this_cooldowns = cooldowns;
        }

        public ArrayList<HashMap> getChars() {
            return this_list;
        }

        public HashMap<Integer, Map> getDetails() {
            return this_details;
        }

        public ArrayList<CoolDowns> getCooldowns() {
            return this_cooldowns;
        }
    }

    class CheckUpdates extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            FeedParser parser = new FeedParser();
            String uri = "";
            try {
                InputStream releases = FeedParser.downloadUrl("https://github.com/paolo-optc/optc-mobile-db/releases.atom");
                List<FeedParser.Entry> feed = parser.parse(releases);
                for (FeedParser.Entry entry : feed) {
                    String version = entry.id.replace("tag:github.com,2008:Repository/70237456/", "");
                    Double vrs = Double.valueOf(version);
                    if (vrs > APP_VERSION) {
                        uri += "https://github.com" + entry.link;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return uri;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(final String result) {
            if (!result.equals("")) {
                final Snackbar msg = Snackbar.make(findViewById(android.R.id.content), "An update is out! Do you want to know more?", Snackbar.LENGTH_INDEFINITE);
                msg.setAction("Sure!", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(result));
                        startActivity(i);
                    }
                })
                        .setActionTextColor(Color.WHITE).setDuration(8000).show();
                msg.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        msg.dismiss();
                    }
                });
            }
        }
    }

    //DONE: Add loading screen when DB updates
    //TODO: Avoid downloading db every time app starts. Need to check RSS or something else to see if DB has been updated, and then ask user if he wants to update
    //DONE: Add update notification for this app
}
