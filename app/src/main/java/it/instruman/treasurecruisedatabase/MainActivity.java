package it.instruman.treasurecruisedatabase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private final Context context = this;
    //  URLs WHERE JSON DATABASES ARE
    //  I USED MY PERSONAL WEBSITE TO HOST JSON DBs
    //  ORIGINAL OPTC-DB PROJECT DOESN'T HAVE JSON DBs BUT JAVASCRIPT ARRAYS
    //  SO I NEEDED TO CONVERT THEM TO JSON AND HOST THEM ON MY WEBSITE
    private final String CharacterListJSONUrl = "http://www.instruman.it/assets/chars.json";
    private final String CharacterDetailsJSONUrl = "http://www.instruman.it/assets/details.json";
    private final String SpecialCooldownsJSONUrl = "http://www.instruman.it/assets/cooldowns.json";

    ImageView sortName, sortType, sortStars;
    Integer nsort, tsort, ssort;
    ListView lview;
    listViewAdapter adapter;
    EditText filterText;
    Activity activity;
    private ArrayList<HashMap> list, original_list, details;
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
    private ArrayList<CoolDowns> cooldowns;
    ListView.OnItemClickListener lvOnClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap item = (HashMap) adapter.getItem(position);
            boolean multiCD = false;
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.character_info);
            TextView title = (TextView) dialog.findViewById(R.id.titleText);
            title.setText((String) item.get(Constants.NAME));

            // set the custom dialog components - text, image and button
            ImageView image = (ImageView) dialog.findViewById(R.id.char_img_big);
            DrawableBackgroundDownloader Draw = new DrawableBackgroundDownloader();

            Draw.loadDrawable("http://onepiece-treasurecruise.com/wp-content/uploads/c" + convertID((Integer) item.get(Constants.ID)) + ".png", image, getResources().getDrawable(R.drawable.ic_refresh_dark));
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
                class2.setText("N/N");
            } else if (classes.getClass().equals(JSONArray.class)) {
                try {
                    class1.setText((String) ((JSONArray) classes).get(0));
                    class2.setText((String) ((JSONArray) classes).get(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            type.setText((String) item.get(Constants.TYPE));
            stars.setText((String) item.get(Constants.STARS));
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
                HashMap extra = details.get((Integer) item.get(Constants.ID));
                captability.setText((String) extra.get(Constants2.CAPTAIN));
                specname.setText((String) extra.get(Constants2.SPECIALNAME));
                Object specobject = extra.get(Constants2.SPECIAL);
                if (specobject.getClass().equals(JSONArray.class)) {
                    JSONArray specmulti = (JSONArray) specobject;
                    String txt = "";
                    for (int n = 0; n < specmulti.length(); n++) {
                        JSONObject currstep = specmulti.getJSONObject(n);
                        txt += "Level " + (n + 1) + ": ";
                        txt += currstep.getString("description");
                        txt += System.getProperty("line.separator");
                        CoolDowns cd = new CoolDowns(currstep.getJSONArray("cooldown"));
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
            int width = getResources().getDisplayMetrics().widthPixels * 1;
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.95);
            if (dialog.getWindow() != null)
                dialog.getWindow().setLayout(width, height);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        activity = this;

        nsort = tsort = ssort = 0;
        lview = (ListView) findViewById(R.id.listView1);
        populateList();
        adapter = new listViewAdapter(this, list);
        lview.setAdapter(adapter);
        lview.setOnItemClickListener(lvOnClick);

        sortName = (ImageView) findViewById(R.id.sortName);
        sortType = (ImageView) findViewById(R.id.sortType);
        sortStars = (ImageView) findViewById(R.id.sortStars);
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortName.setOnClickListener(sortNameOnClick);
        sortType.setOnClickListener(sortTypeOnClick);
        sortStars.setOnClickListener(sortStarsOnClick);
        lview.requestFocus();
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
            }
        });

        populateList();
    }

    protected String HTTPGetCall(String WebMethodURL) throws IOException {
        StringBuilder response = new StringBuilder();

        //Prepare the URL and the connection
        URL u = new URL(WebMethodURL);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            //Get the Stream reader ready
            BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);

            //Loop through the return data and copy it over to the response object to be processed
            String line;

            while ((line = input.readLine()) != null) {
                response.append(line);
            }

            input.close();
        }

        return response.toString();
    }

    private String getFileURL(String uri) {
        try {
            URL url = new URL(uri);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            String res = "";
            while ((line = br.readLine()) != null)
                res = res + line + System.getProperty("line.separator");

            br.close();
            is.close();
            return res;
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

    private void populateList() {
        list = new ArrayList<>();
        details = new ArrayList<>();
        cooldowns = new ArrayList<>();
        JSONArray characters = getJSON(CharacterListJSONUrl);
        if ((characters != null) && (characters.length() > 0)) {
            for (int i = 0; i < characters.length(); i++) {
                try {
                    JSONArray arr_2 = characters.getJSONArray(i);
                    String name = (String) arr_2.get(0);
                    String type = (String) arr_2.get(1);
                    String stars = arr_2.get(3).toString();
                    Object classes = arr_2.get(2);
                    String cost = arr_2.get(4).toString();
                    String combo = arr_2.get(5).toString();
                    String sockets = arr_2.get(6).toString();
                    String maxlvl = arr_2.get(7).toString();
                    String exptomax = arr_2.get(8).toString();
                    String lvl1hp = arr_2.get(9).toString();
                    String lvl1atk = arr_2.get(10).toString();
                    String lvl1rcv = arr_2.get(11).toString();
                    String maxhp = arr_2.get(12).toString();
                    String maxatk = arr_2.get(13).toString();
                    String maxrcv = arr_2.get(14).toString();

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
                }
            }
        }
        original_list = list;

        JSONObject dett = getJSONObj(CharacterDetailsJSONUrl);
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
        }

        JSONArray cooldwns = getJSON(SpecialCooldownsJSONUrl);
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
        }
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

    private class CoolDowns {
        private Integer init, max = -1;
        private Integer type = 0;

        public CoolDowns(Integer initiallvl, Integer maximumlvl) {
            init = initiallvl;
            max = maximumlvl;
            type = 2;
        }

        public CoolDowns(Integer initiallvl) {
            init = initiallvl;
            type = 1;
        }

        public CoolDowns() {
            type = 0;
        }

        public CoolDowns(JSONArray cd) {
            if (cd != null) {
                try {
                    switch (cd.length()) {
                        case 0:
                            type = 0;
                            break;
                        case 1:
                            init = cd.getInt(0);
                            type = 1;
                            break;
                        case 2:
                            init = cd.getInt(0);
                            max = cd.getInt(1);
                            type = 2;
                            break;
                        default:
                            type = 0;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else type = 0;
        }

        public Integer getType() {
            return type;
        }

        public Integer getInitLvl() {
            return init;
        }

        public Integer getMaxLvl() {
            return max;
        }

        public String print() {
            switch (type) {
                case 0:
                    return "";
                case 1:
                    return init.toString();
                case 2:
                    String o = init + "/" + max;
                    return o;
                default:
                    return "";
            }
        }
    }

    class MapComparator implements Comparator<HashMap> {
        private final String key;

        public MapComparator(String key) {
            this.key = key;
        }

        public int compare(HashMap first,
                           HashMap second) {
            // TODO: Null checking, both for maps and values
            String firstValue = (String) first.get(key);
            String secondValue = (String) second.get(key);
            return firstValue.compareTo(secondValue);
        }
    }
}
