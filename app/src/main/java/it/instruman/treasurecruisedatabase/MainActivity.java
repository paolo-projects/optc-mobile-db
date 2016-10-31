package it.instruman.treasurecruisedatabase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

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
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final Context context = this;

/*
    ################### APP VERSION ##################
*/
private final static Double APP_VERSION = 2.1;
/*
    ##################################################
*/

    ExpandableListAdapter explistAdapter;
    ExpandableListView expListView;
    List<String> explistDataHeader;
    HashMap<String, LinkedHashMap<String, Boolean>> explistDataChild;

    public enum FL_TYPE {
        STR, DEX, QCK, PSY, INT
    }

    public enum FL_CLASS {
        FIGHTER("fighter"),
        SLASHER("slasher"),
        STRIKER("striker"),
        SHOOTER("shooter"),
        FREESPIRIT("free spirit"),
        CEREBRAL("cerebral"),
        POWERHOUSE("powerhouse"),
        DRIVEN("driven"),
        BOOSTER("booster"),
        EVOLVER("evolver");


        private String text;

        FL_CLASS(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public boolean equalsCaseInsensitive(String what) {
            if (what != null) {
                return this.text.equalsIgnoreCase(what);
            }
            throw new IllegalArgumentException("Argument can't be null");
        }

        public static FL_CLASS fromString(String text) {
            if (text != null) {
                for (FL_CLASS b : FL_CLASS.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No constant named " + text);
        }
    }

    public enum FL_STARS {
        ONE, TWO, THREE, FOUR, FIVE, SIX
    }

    public EnumSet<FL_TYPE> TypeFlags = EnumSet.allOf(FL_TYPE.class);
    public EnumSet<FL_CLASS> ClassFlags = EnumSet.noneOf(FL_CLASS.class);
    public EnumSet<FL_STARS> StarsFlags = EnumSet.allOf(FL_STARS.class);
    public String FilterText = "";

    private final String UNITS_CACHED_NAME = "units.serial";
    private final String DETAILS_CACHED_NAME = "details.serial";
    private final String COOLDOWNS_CACHED_NAME = "cooldowns.serial";
    private final String EVOLUTIONS_CACHED_NAME = "evolutions.serial";
    private final String LAST_UPDATE_FILE = "lastupdate.serial";

    ImageView sortName, sortType, sortStars;
    Integer nsort, tsort, ssort;
    ListView lview;
    listViewAdapter adapter;
    EditText filterText;
    Activity activity;
    Dialog dlg_hwnd = null;
    Dialog loadingdlg_hwnd = null;
    ActionBarDrawerToggle mDrawerToggle;

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
    private Map<Integer, Map> evolutions;
    ListView.OnItemClickListener lvOnClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            launchDialog(position, false);
        }
    };

    private void launchDialog(int position, boolean sub) {
        HashMap item;
        if (!sub) item = (HashMap) adapter.getItem(position);
        else item = original_list.get(position);

        //if((dlg_hwnd!=null)&&(dlg_hwnd.isShowing())) dlg_hwnd.dismiss();

        boolean multiCD = false;
        final Dialog dialog = new Dialog(context, R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_main);//dialog.setContentView(R.layout.character_info);

        TabHost tabs = (TabHost) dialog.findViewById(R.id.tabs_host);
        tabs.setup();

        TabHost.TabSpec main_info = tabs.newTabSpec("MAIN_INFO");
        main_info.setIndicator("Main Info");
        main_info.setContent(R.id.tab_maininfo);
        tabs.addTab(main_info);

        TabHost.TabSpec abilities = tabs.newTabSpec("ABILITIES");
        abilities.setIndicator("Abilities");
        abilities.setContent(R.id.tab_abilities);
        tabs.addTab(abilities);

        TabHost.TabSpec evolutions_tab = tabs.newTabSpec("EVOLUTIONS");
        evolutions_tab.setIndicator("Evolutions");
        evolutions_tab.setContent(R.id.tab_evolutions);
        tabs.addTab(evolutions_tab);
        tabs.getTabWidget().getChildTabViewAt(2).setVisibility(View.GONE);

        tabs.setCurrentTab(0);

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
        TextView captnotes = (TextView) dialog.findViewById(R.id.capt_notes);
        TextView specname = (TextView) dialog.findViewById(R.id.specnameText);
        TextView specability = (TextView) dialog.findViewById(R.id.specabilityText);
        TextView specnotes = (TextView) dialog.findViewById(R.id.spec_notes);
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
        String ch_type = (String) item.get(Constants.TYPE);
        type.setText(ch_type);
        switch (ch_type.toLowerCase()) {
            case "str":
                type.setBackgroundColor(getResources().getColor(R.color.str_bg));
                type.setTextColor(getResources().getColor(R.color.str_txt));
                break;
            case "dex":
                type.setBackgroundColor(getResources().getColor(R.color.dex_bg));
                type.setTextColor(getResources().getColor(R.color.dex_txt));
                break;
            case "qck":
                type.setBackgroundColor(getResources().getColor(R.color.qck_bg));
                type.setTextColor(getResources().getColor(R.color.qck_txt));
                break;
            case "psy":
                type.setBackgroundColor(getResources().getColor(R.color.psy_bg));
                type.setTextColor(getResources().getColor(R.color.psy_txt));
                break;
            case "int":
                type.setBackgroundColor(getResources().getColor(R.color.int_bg));
                type.setTextColor(getResources().getColor(R.color.int_txt));
                break;
        }
        Integer ch_stars = (Integer) item.get(Constants.STARS);
        stars.setText(String.valueOf(ch_stars));
        switch (ch_stars) {
            case 1:
            case 2:
                stars.setBackgroundColor(getResources().getColor(R.color.bronze_bg));
                stars.setTextColor(getResources().getColor(R.color.bronze_txt));
                break;
            case 3:
                stars.setBackgroundColor(getResources().getColor(R.color.silver_bg));
                stars.setTextColor(getResources().getColor(R.color.silver_txt));
                break;
            case 4:
            case 5:
                stars.setBackgroundColor(getResources().getColor(R.color.gold_bg));
                stars.setTextColor(getResources().getColor(R.color.gold_txt));
                break;
            case 6:
                stars.setBackgroundColor(getResources().getColor(R.color.red_bg));
                stars.setTextColor(getResources().getColor(R.color.red_txt));
                break;
        }
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
            String capt_notes = (String) extra.get(Constants2.CAPTAINNOTES);
            if (!capt_notes.equals("")) {
                captnotes.setText("Notes: " + capt_notes);
                captnotes.setVisibility(View.VISIBLE);
            }
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
            String spec_notes = (String) extra.get(Constants2.SPECIALNOTES);
            if (!spec_notes.equals("")) {
                specnotes.setText("Notes: " + spec_notes);
                specnotes.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!multiCD) {
            CoolDowns cdwns = cooldowns.get((Integer) item.get(Constants.ID));
            speccooldown.setText(cdwns.print());
        } else {
            speccooldown.setVisibility(View.GONE);
            speccooldownTitle.setVisibility(View.GONE);
        }

        LinearLayout evolutions_content = (LinearLayout) dialog.findViewById(R.id.evolutions_content);
        Map<String, Object> char_evo = evolutions.get(item.get(Constants.ID));
        if (char_evo != null) {
            if (char_evo.get("evolution").getClass().equals(Double.class)) {
                //ONE EVOLUTION
                final Integer evo_ID = ((Double) char_evo.get("evolution")).intValue(); //GET EVOLUTION ID
                HorizontalScrollView evolution_scroll = new HorizontalScrollView(activity);
                evolution_scroll.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                ));
                LinearLayout evolution_row = new LinearLayout(context); //CREATE ROW TO SHOW EVOLUTION AND EVOLVERS
                evolution_row.setOrientation(LinearLayout.HORIZONTAL); //SET ORIENTATION TO HORIZONTAL
                evolution_row.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )); // SET WIDTH AND HEIGHT

                //############# EVOLUTION SMALL ICON ###############
                ImageButton evo_pic = new ImageButton(context); //CREATE EVOLUTION PIC
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dpToPx(48), dpToPx(48)
                );
                params.setMargins(2, 5, 2, 5);
                params.gravity = Gravity.CENTER;
                evo_pic.setLayoutParams(params); // SET WIDTH AND HEIGHT OF PIC
                evo_pic.setPadding(0, 0, 0, 0);
                evo_pic.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                Glide
                        .with(activity)
                        .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(evo_ID) + ".png")
                        .into(evo_pic); //ADD PIC
                evo_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: Add code to open details of evolved character
                        launchDialog(evo_ID - 1, true);
                    }
                });
                evolution_row.addView(evo_pic);

                //########### TEXT TO INDICATE EVOLVER MATERIAL ###########
                TextView evo_text = new TextView(context);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params1.setMargins(dpToPx(5), 5, dpToPx(5), 5);
                params1.gravity = Gravity.CENTER;
                evo_text.setLayoutParams(params1);
                evo_text.setGravity(Gravity.CENTER);
                evo_text.setText(getString(R.string.evolution_text));
                evolution_row.addView(evo_text);

                //########## EVOLVER MATERIAL PICS ###########
                List<Double> evolution_material = (List<Double>) char_evo.get("evolvers");
                for (final Double evolver : evolution_material) {
                    ImageButton evolver_pic = new ImageButton(context); //CREATE EVOLUTION PIC
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                            dpToPx(48), dpToPx(48)
                    );
                    params.setMargins(0, 5, 2, 5);
                    params.gravity = Gravity.CENTER;
                    evolver_pic.setLayoutParams(params2); // SET WIDTH AND HEIGHT OF PIC
                    evolver_pic.setPadding(0, 0, 0, 0);
                    evolver_pic.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                    Glide
                            .with(activity)
                            .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(evolver.intValue()) + ".png")
                            .into(evolver_pic); //ADD PIC
                    evolver_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO: Add code to open details of evolver character
                            launchDialog(evolver.intValue() - 1, true);
                        }
                    });
                    evolution_row.addView(evolver_pic);
                }
                evolution_scroll.addView(evolution_row);
                evolutions_content.addView(evolution_scroll);
            } else {
                //MULTIPLE EVOLUTIONS
                List<Double> evo_IDs = (List<Double>) char_evo.get("evolution"); //GET EVOLUTION IDs
                List<List> evo_materials = (List<List>) char_evo.get("evolvers"); //GET EVOLVERS IDs
                for (int i = 0; i < evo_IDs.size(); i++) {
                    final Integer this_id = evo_IDs.get(i).intValue();
                    List<Double> this_evos = evo_materials.get(i);
                    HorizontalScrollView evolution_scroll = new HorizontalScrollView(activity);
                    evolution_scroll.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                    ));
                    LinearLayout evolution_row = new LinearLayout(context); //CREATE ROW TO SHOW EVOLUTION AND EVOLVERS
                    evolution_row.setOrientation(LinearLayout.HORIZONTAL); //SET ORIENTATION TO HORIZONTAL
                    evolution_row.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )); // SET WIDTH AND HEIGHT

                    //############# EVOLUTION SMALL ICON ###############
                    ImageButton evo_pic = new ImageButton(context); //CREATE EVOLUTION PIC
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            dpToPx(48), dpToPx(48)
                    );
                    params.setMargins(2, 5, 2, 5);
                    params.gravity = Gravity.CENTER;
                    evo_pic.setLayoutParams(params); // SET WIDTH AND HEIGHT OF PIC
                    evo_pic.setPadding(0, 0, 0, 0);
                    evo_pic.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                    Glide
                            .with(activity)
                            .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(this_id) + ".png")
                            .into(evo_pic); //ADD PIC
                    evo_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO: Add code to open details of evolved character
                            launchDialog(this_id - 1, true);
                        }
                    });
                    evolution_row.addView(evo_pic);

                    //########### TEXT TO INDICATE EVOLVER MATERIAL ###########
                    TextView evo_text = new TextView(context);
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params1.setMargins(dpToPx(5), 5, dpToPx(5), 5);
                    params1.gravity = Gravity.CENTER;
                    evo_text.setLayoutParams(params1);
                    evo_text.setGravity(Gravity.CENTER);
                    evo_text.setText(getString(R.string.evolution_text));
                    evolution_row.addView(evo_text);

                    //########## EVOLVER MATERIAL PICS ###########
                    for (final Double evolver : this_evos) {
                        ImageButton evolver_pic = new ImageButton(context); //CREATE EVOLUTION PIC
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                                dpToPx(48), dpToPx(48)
                        );
                        params.setMargins(0, 5, 2, 5);
                        params.gravity = Gravity.CENTER;
                        evolver_pic.setLayoutParams(params2); // SET WIDTH AND HEIGHT OF PIC
                        evolver_pic.setPadding(0, 0, 0, 0);
                        evolver_pic.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                        Glide
                                .with(activity)
                                .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(evolver.intValue()) + ".png")
                                .into(evolver_pic); //ADD PIC
                        evolver_pic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //TODO: Add code to open details of evolver character
                                launchDialog(evolver.intValue() - 1, true);
                            }
                        });
                        evolution_row.addView(evolver_pic);
                    }
                    evolution_scroll.addView(evolution_row);
                    evolutions_content.addView(evolution_scroll);
                }
            }
            tabs.getTabWidget().getChildTabViewAt(2).setVisibility(View.VISIBLE);
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
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = .7f;
            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(width, height);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

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
        ImageButton resetBtn = (ImageButton) findViewById(R.id.resetBtn);
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
                FilterText = filterText.getText().toString();
                rebuildList();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterText.setText("");
                FilterText = "";
                rebuildList();
            }
        });

        lview = (ListView) findViewById(R.id.listView1);

        //CREATE EMPTY DATABASES
        list = new ArrayList<>();
        details = new HashMap<>();
        cooldowns = new ArrayList<>();
        evolutions = new HashMap<>();
        original_list = new ArrayList<>();

        //Add data to navigation drawer
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.left_drawer_list);
        // preparing list data
        prepareListData();
        explistAdapter = new ExpandableListAdapter(this, explistDataHeader, explistDataChild);
        // setting list adapter
        expListView.setAdapter(explistAdapter);
        expListView.setOnChildClickListener(setFlags);

        ImageButton filterReset = (ImageButton) findViewById(R.id.reset_filters);
        filterReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilters();
            }
        });

        // run async task to download or load DB
        if ((new File(getFilesDir(), LAST_UPDATE_FILE)).isFile()) {
            //There's cached data, so check if it's old
            Date lastupdateDB = getLastUpdate();
            Date datecachedDB = getSerializedDate();
            if (lastupdateDB.after(datecachedDB)) {
                //if last db update is earlier than cached data then download new data
                (new DownloadData(true, true)).execute();
                //else load cached data
            } else (new DownloadData(false, true)).execute();
            //if no cached data download new data
        } else (new DownloadData(true, true)).execute();

        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        Boolean displayedTutorial = mPrefs.getBoolean("displayed_tutorial", false);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        TextView placeholder = (TextView) findViewById(R.id.placeholder);
        placeholder.setX(0);
        placeholder.setY(height / 2);
        if (!displayedTutorial) {
            new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(placeholder))
                    .setContentTitle("New features!")
                    .setContentText("Try the new custom filters by swiping from the left side of screen!")
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .hideOnTouchOutside()
                    .build();
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putBoolean("displayed_tutorial", true).apply();
        }

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        try {

            // get dragger responsible for the dragging of the left drawer
            Field draggerField = DrawerLayout.class.getDeclaredField("mLeftDragger");
            draggerField.setAccessible(true);
            ViewDragHelper vdh = (ViewDragHelper) draggerField.get(mDrawerLayout);

            // get access to the private field which defines
            // how far from the edge dragging can start
            Field edgeSizeField = ViewDragHelper.class.getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);

            // increase the edge size - while x2 should be good enough,
            // try bigger values to easily see the difference
            int origEdgeSize = (int) edgeSizeField.get(vdh);
            int newEdgeSize = origEdgeSize * 2;
            edgeSizeField.setInt(vdh, newEdgeSize);

        } catch (Exception e) {
            // we unexpectedly failed - e.g. if internal implementation of
            // either ViewDragHelper or DrawerLayout changed
        }
    }

    private void prepareListData() {
        explistDataHeader = new ArrayList<String>();
        explistDataChild = new HashMap<String, LinkedHashMap<String, Boolean>>();

        // Adding child data
        explistDataHeader.add(getString(R.string.type_title));
        explistDataHeader.add(getString(R.string.class_title));
        explistDataHeader.add(getString(R.string.stars_title));

        // Adding child data
        List<String> type_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.type_array)));
        List<String> class_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.class_array)));
        List<String> stars_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.stars_array)));

        LinkedHashMap<String, Boolean> tmp = new LinkedHashMap<>();
        for (String entry : type_list) {
            tmp.put(entry, true);
        }
        explistDataChild.put(explistDataHeader.get(0), tmp); // Header, Child data

        tmp = new LinkedHashMap<>();
        for (String entry : class_list) {
            tmp.put(entry, false);
        }
        explistDataChild.put(explistDataHeader.get(1), tmp);

        tmp = new LinkedHashMap<>();
        for (String entry : stars_list) {
            tmp.put(entry, true);
        }
        explistDataChild.put(explistDataHeader.get(2), tmp);
    }

    private void resetFilters() {
        prepareListData();
        TypeFlags = EnumSet.allOf(FL_TYPE.class);
        ClassFlags = EnumSet.noneOf(FL_CLASS.class);
        StarsFlags = EnumSet.allOf(FL_STARS.class);
        rebuildList();
        explistAdapter = new ExpandableListAdapter(this, explistDataHeader, explistDataChild);
        // setting list adapter
        expListView.setAdapter(explistAdapter);
        expListView.setOnChildClickListener(setFlags);
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
        boolean doDownload = false;
        boolean updateCheck = true;
        boolean isDownloaded = false;

        protected void onPreExecute() {
            showLoading(context);
        }

        public DownloadData(boolean doDownload, boolean updateCheck) {
            this.doDownload = doDownload;
            this.updateCheck = updateCheck;
        }

        public DownloadData() {

        }

        protected DwResult doInBackground(Void... voids) {
            //android.os.Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
            DwResult storage = new DwResult();
            if (!doDownload) {

                //IF YES CHECK IF THERE'S ANY CACHED DATA AND LOAD IT :)
                if ((new File(getFilesDir(), UNITS_CACHED_NAME)).isFile() && (new File(getFilesDir(), DETAILS_CACHED_NAME)).isFile() &&
                        (new File(getFilesDir(), COOLDOWNS_CACHED_NAME)).isFile() && (new File(getFilesDir(), EVOLUTIONS_CACHED_NAME)).isFile()) {
                    storage = getFromSerialized();
                    //IF NO CACHED DATA, TRY TO DOWNLOAD IT :/
                } else {
                    //IF CONNECTION IS ON DOWNLOAD :)
                    if (isNetworkConnected())
                        storage = downloadData();
                    //ELSE LIST WILL BE EMPTY (DATA NEVER DOWNLOADED AND NO INTERNET CONNECTION MEANS BAD THINGS WILL HAPPEN) :(
                }
            } else {
                //IF CONNECTION IS ON DOWNLOAD DATA :)
                if (isNetworkConnected())
                    storage = downloadData();
                    //ELSE TRY TO GATHER IT FROM CACHED :/
                else {
                    if ((new File(getFilesDir(), UNITS_CACHED_NAME)).isFile() && (new File(getFilesDir(), DETAILS_CACHED_NAME)).isFile() && (new File(getFilesDir(), COOLDOWNS_CACHED_NAME)).isFile())
                        storage = getFromSerialized();
                    // NO CONNECTION + NO CACHED DATA = USELESS APP :(
                }
            }
            return storage;
        }

        protected void onPostExecute(DwResult result) {
            original_list = list = result.getChars();
            details = result.getDetails();
            cooldowns = result.getCooldowns();
            evolutions = result.getEvolutions();

            adapter = new listViewAdapter(getApplicationContext(), list);
            lview.setAdapter(adapter);
            lview.setOnItemClickListener(lvOnClick);
            lview.requestFocus();
            if (isDownloaded) {
                (new SerializeData(result)).run();
            }
            /// UPDATE CHECK
            if (updateCheck) (new CheckUpdates()).execute();
            hideLoading();
        }

        private DwResult getFromSerialized() {
            DwResult storage = new DwResult();
            try {
                FileInputStream unitsser = openFileInput(UNITS_CACHED_NAME);
                FileInputStream detailsser = openFileInput(DETAILS_CACHED_NAME);
                FileInputStream cooldownsser = openFileInput(COOLDOWNS_CACHED_NAME);
                FileInputStream evolutionsser = openFileInput(EVOLUTIONS_CACHED_NAME);

                ObjectInputStream units_ser = new ObjectInputStream(unitsser);
                storage.setChars((ArrayList<HashMap>) units_ser.readObject());
                unitsser.close();

                ObjectInputStream details_ser = new ObjectInputStream(detailsser);
                storage.setDetails((HashMap<Integer, Map>) details_ser.readObject());
                detailsser.close();

                ObjectInputStream cooldowns_ser = new ObjectInputStream(cooldownsser);
                storage.setCooldowns((ArrayList<CoolDowns>) cooldowns_ser.readObject());
                cooldownsser.close();

                ObjectInputStream evolutions_ser = new ObjectInputStream(evolutionsser);
                storage.setEvolutions((Map<Integer, Map>) evolutions_ser.readObject());
                evolutionsser.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return storage;
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
            ParseAdditionalNotes notes_parser = new ParseAdditionalNotes();
            Map<Integer, Map> details_js = (Map<Integer, Map>) parseJScript("https://optc-db.github.io/common/data/details.js", "details");
            HashMap<Integer, Map> det_tmp = new HashMap<>();
            if ((details_js != null) && (details_js.size() > 0)) {
                for (Map.Entry<Integer, Map> entry : details_js.entrySet()) {
                    Map<String, Object> value = entry.getValue();
                    HashMap map = new HashMap();
                    map.put(Constants2.SPECIAL, (value.containsKey("special") ? value.get("special") : ""));
                    map.put(Constants2.SPECIALNAME, (value.containsKey("specialName") ? value.get("specialName") : ""));
                    map.put(Constants2.CAPTAIN, (value.containsKey("captain") ? value.get("captain") : ""));
                    map.put(Constants2.CAPTAINNOTES, notes_parser.parseNotes(value.containsKey("captainNotes") ? (String) value.get("captainNotes") : ""));
                    map.put(Constants2.SPECIALNOTES, notes_parser.parseNotes(value.containsKey("specialNotes") ? (String) value.get("specialNotes") : ""));
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

            Map<Integer, Map> evolutions = (Map<Integer, Map>) parseJScript("https://optc-db.github.io/common/data/evolutions.js", "evolutions");
            storage.setEvolutions(evolutions);

            isDownloaded = true;
            return storage;
        }
    }

    private class DwResult {
        ArrayList<HashMap> this_list;
        HashMap<Integer, Map> this_details;
        ArrayList<CoolDowns> this_cooldowns;
        Map<Integer, Map> this_evolutions;

        public DwResult(ArrayList<HashMap> list, HashMap<Integer, Map> details, ArrayList<CoolDowns> cooldowns, Map<Integer, Map> evolutions) {
            this_list = list;
            this_details = details;
            this_cooldowns = cooldowns;
            this_evolutions = evolutions;
        }

        public DwResult() {
            this_list = new ArrayList<>();
            this_details = new HashMap<>();
            this_cooldowns = new ArrayList<>();
            this_evolutions = new HashMap<>();
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

        public void setEvolutions(Map<Integer, Map> evolutions) {
            this_evolutions = evolutions;
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

        public Map<Integer, Map> getEvolutions() {
            return this_evolutions;
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

    class SerializeData extends Thread {
        DwResult data = null;

        public SerializeData(DwResult data) {
            this.data = data;
        }

        public void run() {
            FileOutputStream unitsser;
            FileOutputStream detailsser;
            FileOutputStream cooldownsser;
            FileOutputStream evolutionsser;
            try {
                unitsser = openFileOutput(UNITS_CACHED_NAME, MODE_PRIVATE);
                ObjectOutputStream list_ser = new ObjectOutputStream(unitsser);
                list_ser.writeObject(data.getChars());
                unitsser.close();

                detailsser = openFileOutput(DETAILS_CACHED_NAME, MODE_PRIVATE);
                ObjectOutputStream details_ser = new ObjectOutputStream(detailsser);
                details_ser.writeObject(data.getDetails());
                detailsser.close();

                cooldownsser = openFileOutput(COOLDOWNS_CACHED_NAME, MODE_PRIVATE);
                ObjectOutputStream cooldowns_ser = new ObjectOutputStream(cooldownsser);
                cooldowns_ser.writeObject(data.getCooldowns());
                cooldownsser.close();

                evolutionsser = openFileOutput(EVOLUTIONS_CACHED_NAME, MODE_PRIVATE);
                ObjectOutputStream evolutions_ser = new ObjectOutputStream(evolutionsser);
                evolutions_ser.writeObject(data.getEvolutions());
                evolutionsser.close();

                serializeDate(getLastUpdate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Date getLastUpdate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1900);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date lastupdate = cal.getTime();
        try {
            FeedParser optc_db_check = new FeedParser(); // 2016-10-08T19:12:23+02:00
            String update_date = optc_db_check.readUpdated(FeedParser.downloadUrl("https://github.com/optc-db/optc-db.github.io/commits/master.atom"));
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            lastupdate = df1.parse(update_date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastupdate;
    }

    private void serializeDate(Date value) {
        try {
            FileOutputStream out = openFileOutput(LAST_UPDATE_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(value);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date getSerializedDate() {
        Date date = new Date();
        try {
            FileInputStream in = openFileInput(LAST_UPDATE_FILE);
            ObjectInputStream ois = new ObjectInputStream(in);
            date = (Date) ois.readObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    ExpandableListView.OnChildClickListener setFlags = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
            boolean remove = explistAdapter.getChildValue(groupPosition, childPosition);
            int ClassFlagsSize = ClassFlags.size();
            if (remove) {
                switch (groupPosition) {
                    case 0: //Type Flag
                        switch (childPosition) {
                            case 0: // STR
                                TypeFlags.remove(FL_TYPE.STR);
                                break;
                            case 1: // DEX
                                TypeFlags.remove(FL_TYPE.DEX);
                                break;
                            case 2: // QCK
                                TypeFlags.remove(FL_TYPE.QCK);
                                break;
                            case 3: // PSY
                                TypeFlags.remove(FL_TYPE.PSY);
                                break;
                            case 4: // INT
                                TypeFlags.remove(FL_TYPE.INT);
                                break;
                        }
                        break;
                    case 1: //Class Flag
                        switch (childPosition) {
                            case 0: // Fighter
                                ClassFlags.remove(FL_CLASS.FIGHTER);
                                break;
                            case 1: // Striker
                                ClassFlags.remove(FL_CLASS.STRIKER);
                                break;
                            case 2: // Slasher
                                ClassFlags.remove(FL_CLASS.SLASHER);
                                break;
                            case 3: // Shooter
                                ClassFlags.remove(FL_CLASS.SHOOTER);
                                break;
                            case 4: // Free Spirit
                                ClassFlags.remove(FL_CLASS.FREESPIRIT);
                                break;
                            case 5: // Cerebral
                                ClassFlags.remove(FL_CLASS.CEREBRAL);
                                break;
                            case 6: // Powerhouse
                                ClassFlags.remove(FL_CLASS.POWERHOUSE);
                                break;
                            case 7: // Driven
                                ClassFlags.remove(FL_CLASS.DRIVEN);
                                break;
                            case 8: //Booster
                                ClassFlags.remove(FL_CLASS.BOOSTER);
                                break;
                            case 9: //Evolver
                                ClassFlags.remove(FL_CLASS.EVOLVER);
                                break;
                        }
                        break;
                    case 2: //Stars Flag
                        switch (childPosition) {
                            case 0: // 6 stars
                                StarsFlags.remove(FL_STARS.SIX);
                                break;
                            case 1: // 5 stars
                                StarsFlags.remove(FL_STARS.FIVE);
                                break;
                            case 2: // 4 stars
                                StarsFlags.remove(FL_STARS.FOUR);
                                break;
                            case 3: // 3 stars
                                StarsFlags.remove(FL_STARS.THREE);
                                break;
                            case 4: // 2 stars
                                StarsFlags.remove(FL_STARS.TWO);
                                break;
                            case 5: // 1 star
                                StarsFlags.remove(FL_STARS.ONE);
                                break;
                        }
                        break;
                }
            } else {
                switch (groupPosition) {
                    case 0: //Type Flag
                        switch (childPosition) {
                            case 0: // STR
                                TypeFlags.add(FL_TYPE.STR);
                                break;
                            case 1: // DEX
                                TypeFlags.add(FL_TYPE.DEX);
                                break;
                            case 2: // QCK
                                TypeFlags.add(FL_TYPE.QCK);
                                break;
                            case 3: // PSY
                                TypeFlags.add(FL_TYPE.PSY);
                                break;
                            case 4: // INT
                                TypeFlags.add(FL_TYPE.INT);
                                break;
                        }
                        break;
                    case 1: //Class Flag
                        if (ClassFlagsSize <= 1) {
                            switch (childPosition) {
                                case 0: // Fighter
                                    ClassFlags.add(FL_CLASS.FIGHTER);
                                    break;
                                case 1: // Striker
                                    ClassFlags.add(FL_CLASS.STRIKER);
                                    break;
                                case 2: // Slasher
                                    ClassFlags.add(FL_CLASS.SLASHER);
                                    break;
                                case 3: // Shooter
                                    ClassFlags.add(FL_CLASS.SHOOTER);
                                    break;
                                case 4: // Free Spirit
                                    ClassFlags.add(FL_CLASS.FREESPIRIT);
                                    break;
                                case 5: // Cerebral
                                    ClassFlags.add(FL_CLASS.CEREBRAL);
                                    break;
                                case 6: // Powerhouse
                                    ClassFlags.add(FL_CLASS.POWERHOUSE);
                                    break;
                                case 7: // Driven
                                    ClassFlags.add(FL_CLASS.DRIVEN);
                                    break;
                                case 8: //Booster
                                    ClassFlags.add(FL_CLASS.BOOSTER);
                                    break;
                                case 9: //Evolver
                                    ClassFlags.add(FL_CLASS.EVOLVER);
                                    break;
                            }
                        }
                        break;
                    case 2: //Stars Flag
                        switch (childPosition) {
                            case 0: // 6 stars
                                StarsFlags.add(FL_STARS.SIX);
                                break;
                            case 1: // 5 stars
                                StarsFlags.add(FL_STARS.FIVE);
                                break;
                            case 2: // 4 stars
                                StarsFlags.add(FL_STARS.FOUR);
                                break;
                            case 3: // 3 stars
                                StarsFlags.add(FL_STARS.THREE);
                                break;
                            case 4: // 2 stars
                                StarsFlags.add(FL_STARS.TWO);
                                break;
                            case 5: // 1 star
                                StarsFlags.add(FL_STARS.ONE);
                                break;
                        }
                        break;
                }
            }

            if (remove || (groupPosition != 1) || (ClassFlagsSize <= 1)) {
                explistAdapter.setChildValue(groupPosition, childPosition, !remove);
                rebuildList();
            }
            return false;
        }
    };

    private void updateList() {
        adapter = new listViewAdapter(activity, list);
        lview.setAdapter(adapter);
        sortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortType.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        sortStars.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        nsort = tsort = ssort = 0;
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void rebuildList() {
        list = original_list;

        list = FilterClass.filterByStars(
                FilterClass.filterByClass(
                        FilterClass.filterByType(
                                FilterClass.filterByText(list, FilterText)
                                , TypeFlags)
                        , ClassFlags)
                , StarsFlags);
        updateList();
    }

    //DONE: Add loading screen when DB updates
    //DONE: Avoid downloading db every time app starts. Need to check RSS or something else to see if DB has been updated, and then ask user if he wants to update
    //DONE: Add update notification for this app
    //DONE: Custom filters
    //DONE: List type and stars need a custom background and text color (to improve readability)
    //TODO: Improve custom filters
    //DONE: Add evolutions and evolvers
    //TODO: Maybe a white theme is better?
}
