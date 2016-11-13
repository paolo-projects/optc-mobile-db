package it.instruman.treasurecruisedatabase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.lzyzsd.circleprogress.ArcProgress;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final Context context = this;

/*
    ################### APP VERSION ##################
*/
private final static Double APP_VERSION = 3.0;
/*
    ##################################################
*/

    public static final int thumbnail_width = 96;
    public static final int thumbnail_height = 96;

    private static final String locale_pref = "locale-preference";

    ExpandableListAdapter explistAdapter;
    ExpandableListView expListView;
    List<String> explistDataHeader;
    HashMap<String, LinkedHashMap<String, Boolean>> explistDataChild;

    public enum FL_TYPE {
        STR("STR"),
        DEX("DEX"),
        QCK("QCK"),
        PSY("PSY"),
        INT("INT");

        private String text;

        FL_TYPE(String text) {
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

        public static FL_TYPE fromString(String text) {
            if (text != null) {
                for (FL_TYPE b : FL_TYPE.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No constant named " + text);
        }
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
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6");

        private String text;

        FL_STARS(String text) {
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

        public static FL_STARS fromString(String text) {
            if (text != null) {
                for (FL_STARS b : FL_STARS.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No constant named " + text);
        }
    }

    private Dialog loading;
    public EnumSet<FL_TYPE> TypeFlags = EnumSet.allOf(FL_TYPE.class);
    public EnumSet<FL_CLASS> ClassFlags = EnumSet.noneOf(FL_CLASS.class);
    public EnumSet<FL_STARS> StarsFlags = EnumSet.allOf(FL_STARS.class);
    public String FilterText = "";
    private Activity activity = this;

    ImageView sortName, sortType, sortStars, sortAtk, sortHP, sortRCV;
    ListView lview;
    listViewAdapter adapter;
    Map<String, String> directives;
    EditText filterText;
    Dialog dlg_hwnd = null;
    Dialog loadingdlg_hwnd = null;
    ActionBarDrawerToggle mDrawerToggle;

    private String UNITS_JS;
    private String DETAILS_JS;
    private String COOLDOWNS_JS;
    private String EVOLUTIONS_JS;
    private String DIRECTIVES_JS;
    private String DROPS_JS;

    private String DROPS_STORY = "Story Island";
    private String DROPS_WEEKLY = "Weekly Island";
    private String DROPS_FORTNIGHT = "Fortnight";
    private String DROPS_RAID = "Raid";
    private String DROPS_SPECIAL = "Special";

    private String DROP_COMPLETION = "Completion Units";
    private String DROP_ALLDIFFS = "All Difficulties";

    private void sortList(View v) {
        ListSortUtility utils = new ListSortUtility();
        list = utils.sortList(activity, v, list);
        adapter = new listViewAdapter(activity, list);
        lview.setAdapter(adapter);
        hideKeyboard();
    }

    private ArrayList<HashMap> list, original_list;
    LinearLayout.OnClickListener sortNameOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup vg = (ViewGroup) v;
            View u = null;
            for (int i = 0; i < vg.getChildCount(); i++) {

                View child = vg.getChildAt(i);

                if (child instanceof ImageView) u = child;
            }
            sortList(u);
        }
    };
    LinearLayout.OnClickListener sortTypeOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {

            ViewGroup vg = (ViewGroup) v;
            View u = null;
            for (int i = 0; i < vg.getChildCount(); i++) {

                View child = vg.getChildAt(i);

                if (child instanceof ImageView) u = child;
            }
            sortList(u);
        }
    };
    LinearLayout.OnClickListener sortStarsOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup vg = (ViewGroup) v;
            View u = null;
            for (int i = 0; i < vg.getChildCount(); i++) {

                View child = vg.getChildAt(i);

                if (child instanceof ImageView) u = child;
            }
            sortList(u);
        }
    };
    LinearLayout.OnClickListener sortAtkOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup vg = (ViewGroup) v;
            View u = null;
            for (int i = 0; i < vg.getChildCount(); i++) {

                View child = vg.getChildAt(i);

                if (child instanceof ImageView) u = child;
            }
            sortList(u);
        }
    };
    LinearLayout.OnClickListener sortHPOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup vg = (ViewGroup) v;
            View u = null;
            for (int i = 0; i < vg.getChildCount(); i++) {

                View child = vg.getChildAt(i);

                if (child instanceof ImageView) u = child;
            }
            sortList(u);
        }
    };
    LinearLayout.OnClickListener sortRCVOnClick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup vg = (ViewGroup) v;
            View u = null;
            for (int i = 0; i < vg.getChildCount(); i++) {

                View child = vg.getChildAt(i);

                if (child instanceof ImageView) u = child;
            }
            sortList(u);
        }
    };

    ListView.OnItemClickListener lvOnClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            launchDialog(adapter.getIDfromPosition(position));
        }
    };

    private String replaceBr(String input) {
        String output = input.replace(" <br> ", System.getProperty("line.separator"));
        output = output.replace("<br> ", System.getProperty("line.separator"));
        output = output.replace("<br>", System.getProperty("line.separator"));
        return output;
    }

    private void launchDialog(int id) {

        //if((dlg_hwnd!=null)&&(dlg_hwnd.isShowing())) dlg_hwnd.dismiss();
        Integer theme_id = R.style.AppThemeTeal;
        try {
            theme_id = getPackageManager().getActivityInfo(getComponentName(), 0).theme;
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Dialog dialog = new Dialog(context, theme_id);
        dialog.setContentView(R.layout.dialog_main);//dialog.setContentView(R.layout.character_info);

        final TabHost tabs = (TabHost) dialog.findViewById(R.id.tabs_host);
        tabs.setup();

        TabHost.TabSpec main_info = tabs.newTabSpec("MAIN_INFO");
        main_info.setIndicator(getString(R.string.tab_maininfo));
        main_info.setContent(R.id.tab_maininfo);
        tabs.addTab(main_info);

        TabHost.TabSpec abilities = tabs.newTabSpec("ABILITIES");
        abilities.setIndicator(getString(R.string.tab_abilities));
        abilities.setContent(R.id.tab_abilities);
        tabs.addTab(abilities);

        TabHost.TabSpec evolutions_tab = tabs.newTabSpec("EVOLUTIONS");
        evolutions_tab.setIndicator(getString(R.string.tab_evolutions));
        evolutions_tab.setContent(R.id.tab_evolutions);
        tabs.addTab(evolutions_tab);
        tabs.getTabWidget().getChildTabViewAt(2).setVisibility(View.GONE);

        TabHost.TabSpec drops_tab = tabs.newTabSpec("DROPS");
        drops_tab.setIndicator(getString(R.string.tab_drops));
        drops_tab.setContent(R.id.tab_drops);
        tabs.addTab(drops_tab);
        tabs.getTabWidget().getChildTabViewAt(3).setVisibility(View.GONE);

        tabs.setCurrentTab(0);

        for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_txt)));
        }

        TextView title = (TextView) dialog.findViewById(R.id.titleText);

        // set the custom dialog components - text, image and button
        ImageView image = (ImageView) dialog.findViewById(R.id.char_img_big);

        Glide
                .with(context)
                .load("http://onepiece-treasurecruise.com/wp-content/uploads/c" + convertID(id) + ".png")
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

        DBHelper db = new DBHelper(context);
        SQLiteDatabase database = db.getReadableDatabase();
        CharacterInfo charInfo = DBHelper.getCharacterInfo(database, id);
        database.close();
        db.close();

        if (charInfo == null) return;


        title.setText(charInfo.getName());
        title.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_txt)));

        class1.setText(charInfo.getClass1());
        class2.setText(charInfo.getClass2());

        String ch_type = charInfo.getType();
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

        Integer ch_stars = charInfo.getStars();
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

        cost.setText(charInfo.getCost().toString());
        combo.setText(charInfo.getCombo().toString());
        slots.setText(charInfo.getSockets().toString());
        maxlevel.setText(charInfo.getMaxLvl().toString());
        exptomax.setText(charInfo.getExpToMax().toString());

        lvl1hp.setText(charInfo.getLvl1HP().toString());
        lvl1atk.setText(charInfo.getLvl1ATK().toString());
        lvl1rcv.setText(charInfo.getLvl1RCV().toString());
        lvlmax.setText(charInfo.getMaxLvl().toString());

        maxhp.setText(charInfo.getMaxHP().toString());
        maxatk.setText(charInfo.getMaxATK().toString());
        maxrcv.setText(charInfo.getMaxRCV().toString());

        captability.setText(charInfo.getCaptainDescription());
        String capt_notes = charInfo.getCaptainNotes();
        if (!capt_notes.equals("")) {
            capt_notes = replaceBr(capt_notes);
            captnotes.setText(getString(R.string.notes_text) + capt_notes);
            captnotes.setVisibility(View.VISIBLE);
        }
        List<CharacterSpecials> char_specials = charInfo.getSpecials();
        if (char_specials.size() > 0) {
            specname.setText(charInfo.getSpecialName());
            LinearLayout specials_container = (LinearLayout) dialog.findViewById(R.id.specials_container);
            for (CharacterSpecials special : char_specials) {
                TextView special_description = new TextView(context);
                special_description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                special_description.setText(special.getSpecialDescription());
                special_description.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_txt)));
                specials_container.addView(special_description);

                LinearLayout coold_layout = new LinearLayout(context);
                coold_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView coold_title = new TextView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, dpToPx(4), 0);
                coold_title.setLayoutParams(params);
                coold_title.setText(getString(R.string.speccooldown));
                coold_title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                coold_title.setBackgroundColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_header_bg)));
                coold_title.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_header_txt)));
                coold_title.setText(getString(R.string.speccooldown));
                coold_layout.addView(coold_title);

                TextView coold_content = new TextView(context);
                coold_content.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                Integer maxCD = special.getMaxCooldown();
                Integer minCD = special.getMinCooldown();

                String maxCDs = "?";
                if ((maxCD != -1) && (maxCD != 0))
                    maxCDs = String.valueOf(maxCD);

                String minCDs = "?";
                if ((minCD != -1) && (minCD != 0))
                    minCDs = String.valueOf(minCD);

                String coold_s = maxCDs + "/" + minCDs;
                coold_content.setText(coold_s);
                coold_content.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_txt)));

                coold_layout.addView(coold_content);

                String specialnotes = special.getSpecialNotes();
                if (!specialnotes.equals("")) {
                    specialnotes = replaceBr(specialnotes);
                    TextView special_notes = new TextView(context);
                    special_notes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    special_notes.setText(getString(R.string.notes_text) + specialnotes);
                    special_notes.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_header_txt)));
                    specials_container.addView(special_notes);
                }

                coold_layout.setPadding(2, 8, 2, 8);

                specials_container.addView(coold_layout);
            }
        }

        LinearLayout evolutions_content = (LinearLayout) dialog.findViewById(R.id.evolutions_content);
        List<CharacterEvolutions> evos = charInfo.getEvolutions();

        if (evos.size() > 0) {
            //MULTIPLE EVOLUTIONS
            for (int i = 0; i < evos.size(); i++) {
                final Integer this_id = evos.get(i).getEvolutionCharacter();
                HorizontalScrollView evolution_scroll = new HorizontalScrollView(context);
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
                        .with(context)
                        .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(this_id) + ".png")
                        .dontTransform()
                        .override(thumbnail_width, thumbnail_height)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(evo_pic); //ADD PIC
                evo_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchDialog(this_id);
                    }
                });
                evolution_row.addView(evo_pic);

                ImageView evo_text = new ImageView(context);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params1.setMargins(dpToPx(5), 5, dpToPx(5), 5);
                params1.gravity = Gravity.CENTER;
                evo_text.setLayoutParams(params1);
                evo_text.setBackgroundResource(R.drawable.ic_left_arrow);
                evolution_row.addView(evo_text);

                List<Integer> evolvers = evos.get(i).getEvolvers();
                //########## EVOLVER MATERIAL PICS ###########
                for (final Integer evolver : evolvers) {
                    if (evolver != 0) {
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
                                .with(context)
                                .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(evolver) + ".png")
                                .dontTransform()
                                .override(thumbnail_width, thumbnail_height)
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(evolver_pic); //ADD PIC
                        evolver_pic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                launchDialog(evolver);
                            }
                        });
                        evolution_row.addView(evolver_pic);
                    }
                }
                evolution_scroll.addView(evolution_row);
                evolutions_content.addView(evolution_scroll);
            }
            tabs.getTabWidget().getChildTabViewAt(2).setVisibility(View.VISIBLE);
        }

        LinearLayout drops_content = (LinearLayout) dialog.findViewById(R.id.drops_content);
        List<DropInfo> drops = charInfo.getDropInfo();

        if (drops.size() > 0) {
            //MULTIPLE EVOLUTIONS
            for (int i = 0; i < drops.size(); i++) {
                DropInfo this_drops = drops.get(i);

                LinearLayout drops_row = new LinearLayout(context); //CREATE ROW TO SHOW EVOLUTION AND EVOLVERS
                drops_row.setOrientation(LinearLayout.HORIZONTAL); //SET ORIENTATION TO HORIZONTAL
                drops_row.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )); // SET WIDTH AND HEIGHT
                drops_row.setGravity(Gravity.CENTER_VERTICAL);

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
                Integer cont_id = this_drops.getDropThumbnail();
                Glide
                        .with(context)
                        .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(cont_id) + ".png")
                        .dontTransform()
                        .override(thumbnail_width, thumbnail_height)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(evo_pic); //ADD PIC
                drops_row.addView(evo_pic);

                TextView drop_name = new TextView(context);
                LinearLayout.LayoutParams txt_params =  new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                txt_params.setMargins(dpToPx(10), 0, 5, 0);
                drop_name.setLayoutParams(txt_params);
                drop_name.setText(this_drops.getDropLocation());
                drop_name.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_txt)));
                drop_name.setGravity(Gravity.CENTER);

                drops_row.addView(drop_name);

                TextView drop_det = new TextView(context);
                LinearLayout.LayoutParams txt2_params =  new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                txt2_params.setMargins(5, 0, 5, 0);
                drop_det.setLayoutParams(txt2_params);
                drop_det.setText(this_drops.getDropChapterOrDifficulty());
                drop_det.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_txt)));
                drop_det.setGravity(Gravity.CENTER);

                drops_row.addView(drop_det);

                TextView drop_notes = new TextView(context);
                drop_notes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                drop_notes.setGravity(Gravity.CENTER);
                drop_notes.setTypeface(drop_notes.getTypeface(), Typeface.BOLD);
                drop_det.setTextColor(getResources().getColor(getResIdFromAttribute(activity, R.attr.char_info_header_txt)));

                if(this_drops.isGlobal() && !this_drops.isJapan())
                    drop_notes.setText(getString(R.string.drops_global));
                else if(!this_drops.isGlobal() && this_drops.isJapan())
                    drop_notes.setText(getString(R.string.drops_japan));

                drops_content.addView(drops_row);
                if(!drop_notes.getText().equals("")) drops_content.addView(drop_notes);
            }
            tabs.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
        }

        HorizontalScrollView scr = (HorizontalScrollView) dialog.findViewById(R.id.tabs_scrollview);
        scr.invalidate();
        scr.requestLayout();

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
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void showLoading(Context context) {
        try {
            loading = new Dialog(context, getPackageManager().getActivityInfo(getComponentName(), 0).theme);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.pref_name), 0);
        String theme_str = mPrefs.getString(getResources().getString(R.string.theme_pref), "teal");
        switch (theme_str) {
            case "teal":
                setTheme(R.style.AppThemeTeal);
                break;
            case "red":
                setTheme(R.style.AppThemeRed);
                break;
            case "amber":
                setTheme(R.style.AppThemeAmber);
                break;
            case "light":
                setTheme(R.style.AppThemeLight);
                break;
            case "dark":
                setTheme(R.style.AppThemeDark);
                break;
            default:
                setTheme(R.style.AppThemeTeal);
                break;
        }
        if (!mPrefs.contains(locale_pref)) {
            Locale lan = Locale.getDefault();
            String locale = lan.getLanguage().toLowerCase();
            String country = lan.getCountry();
            if (!country.equals("")) {
                locale += "-" + country.toLowerCase();
            }
            mPrefs.edit().putString(locale_pref, locale).commit();
        }

        String locale = mPrefs.getString(locale_pref, "");
        if (!locale.equals("")) {
            Resources res = context.getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            if (locale.contains("-")) {
                String[] combined = locale.split("-");
                conf.locale = new Locale(combined[0].toLowerCase(), combined[1].toLowerCase());
            } else conf.locale = new Locale(locale.toLowerCase());
            res.updateConfiguration(conf, dm);
        }

        switch (locale.toLowerCase()) {
            case "es":
                UNITS_JS = "https://optc-sp.github.io/common/data/units.js";
                COOLDOWNS_JS = "https://optc-sp.github.io/common/data/cooldowns.js";
                DETAILS_JS = "https://optc-sp.github.io/common/data/details.js";
                EVOLUTIONS_JS = "https://optc-sp.github.io/common/data/evolutions.js";
                DIRECTIVES_JS = "https://optc-sp.github.io/common/js/directives.js";
                DROPS_JS = "https://optc-sp.github.io/common/data/drops.js";
                break;
            case "it":
                UNITS_JS = "http://www.one-piece-treasure-cruise-italia.org/common/data/units.js";
                COOLDOWNS_JS = "http://www.one-piece-treasure-cruise-italia.org/common/data/cooldowns.js";
                DETAILS_JS = "http://www.one-piece-treasure-cruise-italia.org/common/data/details.js";
                EVOLUTIONS_JS = "http://www.one-piece-treasure-cruise-italia.org/common/data/evolutions.js";
                DIRECTIVES_JS = "http://www.one-piece-treasure-cruise-italia.org/common/js/directives.js";
                DROPS_JS = "http://www.one-piece-treasure-cruise-italia.org/common/data/drops.js";

                DROPS_STORY = "Isole della Storia";
                DROPS_WEEKLY = "Isole Settimanali";
                DROPS_FORTNIGHT = "Fortnight";
                DROPS_RAID = "Raid";
                DROPS_SPECIAL = "Isole Speciali";

                DROP_COMPLETION = "Dopo completamento";
                DROP_ALLDIFFS = "Tutte le difficoltà";
                break;
            default:
                UNITS_JS = "https://optc-db.github.io/common/data/units.js";
                COOLDOWNS_JS = "https://optc-db.github.io/common/data/cooldowns.js";
                DETAILS_JS = "https://optc-db.github.io/common/data/details.js";
                EVOLUTIONS_JS = "https://optc-db.github.io/common/data/evolutions.js";
                DIRECTIVES_JS = "https://optc-db.github.io/common/js/directives.js";
                DROPS_JS = "https://optc-db.github.io/common/data/drops.js";
                break;
        }

        setContentView(R.layout.activity_main);


        sortName = (ImageView) findViewById(R.id.sortName);
        sortType = (ImageView) findViewById(R.id.sortType);
        sortStars = (ImageView) findViewById(R.id.sortStars);
        sortAtk = (ImageView) findViewById(R.id.sortAtk);
        sortHP = (ImageView) findViewById(R.id.sortHp);
        sortRCV = (ImageView) findViewById(R.id.sortRcv);


        sortName.setBackgroundResource(R.drawable.ic_circle);
        sortName.setTag(R.id.TAG_SORT_ID, R.id.sortName);
        sortName.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortType.setBackgroundResource(R.drawable.ic_circle);
        sortType.setTag(R.id.TAG_SORT_ID, R.id.sortType);
        sortType.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortStars.setBackgroundResource(R.drawable.ic_circle);
        sortStars.setTag(R.id.TAG_SORT_ID, R.id.sortStars);
        sortStars.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortAtk.setBackgroundResource(R.drawable.ic_circle);
        sortAtk.setTag(R.id.TAG_SORT_ID, R.id.sortAtk);
        sortAtk.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortHP.setBackgroundResource(R.drawable.ic_circle);
        sortHP.setTag(R.id.TAG_SORT_ID, R.id.sortHp);
        sortHP.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortRCV.setBackgroundResource(R.drawable.ic_circle);
        sortRCV.setTag(R.id.TAG_SORT_ID, R.id.sortRcv);
        sortRCV.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        LinearLayout sortNamel = (LinearLayout) findViewById(R.id.sortName_l);
        LinearLayout sortTypel = (LinearLayout) findViewById(R.id.sortType_l);
        LinearLayout sortStarsl = (LinearLayout) findViewById(R.id.sortStars_l);
        LinearLayout sortAtkl = (LinearLayout) findViewById(R.id.sortAtk_l);
        LinearLayout sortHPl = (LinearLayout) findViewById(R.id.sortHp_l);
        LinearLayout sortRCVl = (LinearLayout) findViewById(R.id.sortRcv_l);

        sortNamel.setOnClickListener(sortNameOnClick);
        sortTypel.setOnClickListener(sortTypeOnClick);
        sortStarsl.setOnClickListener(sortStarsOnClick);
        sortAtkl.setOnClickListener(sortAtkOnClick);
        sortHPl.setOnClickListener(sortHPOnClick);
        sortRCVl.setOnClickListener(sortRCVOnClick);

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
        final LinearLayout F_TEXT = (LinearLayout) findViewById(R.id.filtertext_layout);
        filterText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try { // try - catch is here to be sure that on older android version, since elevation property is not supported, an error is not raised
                    if (b) {
                        ObjectAnimator anim = ObjectAnimator.ofFloat(F_TEXT, "elevation", 2f, 8f);
                        anim.setInterpolator(new DecelerateInterpolator());
                        anim.setDuration(300);
                        anim.start();
                    } else {
                        ObjectAnimator anim = ObjectAnimator.ofFloat(F_TEXT, "elevation", 8f, 2f);
                        anim.setInterpolator(new DecelerateInterpolator());
                        anim.setDuration(300);
                        anim.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterText = filterText.getText().toString();
                rebuildList();
                hideKeyboard();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterText.setText("");
                FilterText = "";
                rebuildList();
                showKeyboard();
            }
        });

        lview = (ListView) findViewById(R.id.listView1);

        directives = new HashMap<>();

        //CREATE EMPTY DATABASES
        list = new ArrayList<>();
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

        ImageButton resetDb = (ImageButton) findViewById(R.id.reset_db);
        resetDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.reset_db_title))
                        .setMessage(getString(R.string.reset_db_message))
                        .setPositiveButton(getString(R.string.reset_db_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list = new ArrayList<>();
                                updateList();
                                (new DownloadData(true, false)).execute();
                            }
                        })
                        .setNegativeButton(getString(R.string.reset_db_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //nothing do to
                            }
                        }).create();
                alert.show();
            }
        });

        Button tealBtn = (Button) findViewById(R.id.tealBtn);
        Button redBtn = (Button) findViewById(R.id.redBtn);
        Button amberBtn = (Button) findViewById(R.id.amberBtn);
        Button lightBtn = (Button) findViewById(R.id.lightBtn);
        Button darkBtn = (Button) findViewById(R.id.darkBtn);

        tealBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme(R.style.AppThemeTeal);
                SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_name), 0);
                prefs.edit().putString(getResources().getString(R.string.theme_pref), "teal").commit();
                crossfade(300);
            }
        });
        redBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme(R.style.AppThemeRed);
                SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_name), 0);
                prefs.edit().putString(getResources().getString(R.string.theme_pref), "red").commit();
                crossfade(300);
            }
        });
        amberBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme(R.style.AppThemeAmber);
                SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_name), 0);
                prefs.edit().putString(getResources().getString(R.string.theme_pref), "amber").commit();
                crossfade(300);
            }
        });
        lightBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme(R.style.AppThemeLight);
                SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_name), 0);
                prefs.edit().putString(getResources().getString(R.string.theme_pref), "light").commit();
                crossfade(300);
            }
        });
        darkBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme(R.style.AppThemeDark);
                SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_name), 0);
                prefs.edit().putString(getResources().getString(R.string.theme_pref), "dark").commit();
                crossfade(300);
            }
        });

        // run async task to download or load DB
        if (mPrefs.contains(getString(R.string.lastupdate)) && isDbOn()) {
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

        Boolean displayedTutorial = mPrefs.getBoolean(getString(R.string.tutorial_displayed), false);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        TextView placeholder = (TextView) findViewById(R.id.placeholder);
        placeholder.setX(getScreenWidth());
        placeholder.setY(height / 2);
        if (!displayedTutorial) {
            new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(placeholder))
                    .setContentTitle("Drawer position changed!")
                    .setContentText("Drawer now moved to the right edge of the screen!")
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .hideOnTouchOutside()
                    .build();
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putBoolean(getString(R.string.tutorial_displayed), true).apply();
        }

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        try {

            // get dragger responsible for the dragging of the left drawer
            Field draggerField = DrawerLayout.class.getDeclaredField("mRightDragger");
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

        ImageButton lang_selector = (ImageButton) findViewById(R.id.language_selector);
        lang_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListAdapter adapter = new ArrayAdapterWithIcon(context, R.array.languages_array, R.array.flags_array);

                new AlertDialog.Builder(context)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                String[] langs = getResources().getStringArray(R.array.locale_array);
                                String locale = langs[item];
                                SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_name), 0);
                                prefs.edit().putString(locale_pref, locale).commit();
                                crossfade(300);
                            }
                        }).show();
            }
        });
    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private int getSideTotalMargin() {
        LinearLayout main = (LinearLayout) findViewById(R.id.maincontent);
        int left = main.getPaddingLeft();
        int right = main.getPaddingRight();
        return left + right;
    }

    protected void onResume() {
        super.onResume();
        int width = getScreenWidth();

        LinearLayout list_size = (LinearLayout) findViewById(R.id.list_size_layout);

        if (width > dpToPx(600))
            params.width = width - getSideTotalMargin();
        else params.width = dpToPx(550);
        list_size.setLayoutParams(params);

    }

    private void crossfade(int mShortAnimationDuration) {

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        final View v2 = findViewById(R.id.drawer_layout);
        v2.animate()
                .alpha(0f)
                .translationY(v2.getHeight())
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((DrawerLayout) v2).closeDrawer(Gravity.RIGHT);
                        v2.setVisibility(View.GONE);
                        activity.recreate();
                    }
                });
    }

    private void prepareListData() {
        explistDataHeader = new ArrayList<>();
        explistDataChild = new HashMap<>();

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
        hideKeyboard();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.onResume();
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

    public String convertID(Integer ID) {
        if (ID < 10) return ("000" + ID.toString());
        else if (ID < 100) return ("00" + ID.toString());
        else if (ID < 1000) return ("0" + ID.toString());
        else return ID.toString();
    }

    public static class MapComparator implements Comparator<HashMap> {
        private final String key;

        public MapComparator(String key) {
            this.key = key;
        }

        public int compare(HashMap first,
                           HashMap second) {
            if ((first != null) && (second != null)) {
                if (first.get(key).getClass().equals(Integer.class)) {
                    return (int) second.get(key) - (int) first.get(key);
                } else {
                    String firstValue = String.valueOf((first.get(key) == null) ? "" : first.get(key));
                    String secondValue = String.valueOf((second.get(key) == null) ? "" : second.get(key));
                    return firstValue.compareTo(secondValue);
                }
            }
            return "".compareTo("");
        }
    }

    private class CacheImages extends Thread {
        public void run() {
            for (int n = 0; n < original_list.size(); n++) {
                //WORKAROUND TO PRE-CACHE ICONS
                try {
                    FutureTarget<GlideDrawable> future = Glide
                            .with(context)
                            .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(n + 1) + ".png")
                            .dontTransform()
                            .override(thumbnail_width, thumbnail_height)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(thumbnail_width, thumbnail_height);
                    GlideDrawable cacheFile = future.get();
                } catch (Exception e) {
                    Log.e("ERR", "Pic not found");
                }
            }
        }
    }

    private Double progress_val = 0.0;

    private void addProgress(String value) {
        if ((loading != null) && loading.isShowing()) {
            ArcProgress progress = (ArcProgress) loading.findViewById(R.id.loading_bar);
            ObjectAnimator anim = ObjectAnimator.ofInt(progress, "progress", progress_val.intValue(), progress_val.intValue() + 100/10);
            progress_val = progress_val + 100/10;
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(200);
            anim.start();
            TextView progtext = (TextView) loading.findViewById(R.id.loading_text);
            progtext.setText(value);
        }
    }

    class LoadFromDB extends AsyncTask<Void, Void, ArrayList<HashMap>> {

        protected void onPostExecute(ArrayList<HashMap> hashMaps) {
            list = original_list = hashMaps;

            (new CacheImages()).start();

            adapter = new listViewAdapter(activity, list);
            lview.setAdapter(adapter);
            lview.setOnItemClickListener(lvOnClick);
        }

        protected ArrayList<HashMap> doInBackground(Void... voids) {
            return getFromDB();
        }
    }

    class DownloadData extends AsyncTask<Void, String, Void> {
        boolean doDownload = false;
        boolean updateCheck = true;
        boolean isDownloaded = false;

        protected void onProgressUpdate(String... values) {
            addProgress(values[0]);
        }

        protected void onPreExecute() {
            if (doDownload) showLoading(context);
        }

        public DownloadData(boolean doDownload, boolean updateCheck) {
            this.doDownload = doDownload;
            this.updateCheck = updateCheck;
        }

        public DownloadData() {

        }

        protected Void doInBackground(Void... voids) {
            //android.os.Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
            if (doDownload) {
                //IF CONNECTION IS ON DOWNLOAD DATA :)
                if (isNetworkConnected())
                    downloadData();
            }
            return null;
        }

        protected void onPostExecute(Void voids) {
            (new LoadFromDB()).execute();

            if (isDownloaded) serializeDate(getLastUpdate());
            /// UPDATE CHECK
            if (updateCheck) (new CheckUpdates()).execute();
            if (doDownload) hideLoading();
        }

        private void downloadData() {
            DBHelper db = new DBHelper(context);

            SQLiteDatabase database = db.getWritableDatabase();
            DBHelper.dropAndCreateTables(database);

            List<List> characters = (List) parseJScript(UNITS_JS, "units");
            publishProgress(getString(R.string.loading_characters));

            if (characters == null) return;
            int char_size = characters.size();
            if (char_size > 0) {
                database.beginTransaction();
                try {
                    for (int i = 0; i < char_size; i++) {
                        List arr_2 = characters.get(i);
                        String name = (arr_2.get(0) == null) ? "" : (String) arr_2.get(0);
                        String type = (arr_2.get(1) == null) ? "" : (String) arr_2.get(1);
                        Integer stars = (arr_2.get(3) == null) ? 1 : ((Double) arr_2.get(3)).intValue();
                        Object classes = (arr_2.get(2) == null) ? null : arr_2.get(2);
                        Integer cost = (arr_2.get(4) == null) ? null : ((Double) arr_2.get(4)).intValue();
                        Integer combo = (arr_2.get(5) == null) ? null : ((Double) arr_2.get(5)).intValue();
                        Integer sockets = (arr_2.get(6) == null) ? null : ((Double) arr_2.get(6)).intValue();
                        Integer maxlvl = (arr_2.get(7) == null) ? null : ((Double) arr_2.get(7)).intValue();
                        Integer exptomax = (arr_2.get(8) == null) ? null : ((Double) arr_2.get(8)).intValue();
                        Integer lvl1hp = (arr_2.get(9) == null) ? null : ((Double) arr_2.get(9)).intValue();
                        Integer lvl1atk = (arr_2.get(10) == null) ? null : ((Double) arr_2.get(10)).intValue();
                        Integer lvl1rcv = (arr_2.get(11) == null) ? null : ((Double) arr_2.get(11)).intValue();
                        Integer maxhp = (arr_2.get(12) == null) ? null : ((Double) arr_2.get(12)).intValue();
                        Integer maxatk = (arr_2.get(13) == null) ? null : ((Double) arr_2.get(13)).intValue();
                        Integer maxrcv = (arr_2.get(14) == null) ? null : ((Double) arr_2.get(14)).intValue();

                        String class1, class2;
                        class1 = class2 = null;
                        if (classes != null) {
                            if (classes.getClass().equals(String.class)) {
                                class1 = (String) classes;
                                class2 = null;
                            } else if (classes.getClass().equals(NativeArray.class)) {
                                List<String> classes_list = (List<String>) classes;
                                class1 = (classes_list.size() > 0) ? classes_list.get(0) : null;
                                class2 = (classes_list.size() > 1) ? classes_list.get(1) : null;
                            }
                        }
                        DBHelper.insertIntoUnits(database, i + 1, name, type, class1, class2, stars, cost, combo, sockets,
                                maxlvl, exptomax, lvl1hp, lvl1atk, lvl1rcv, maxhp, maxatk, maxrcv);
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
            }
            publishProgress(getString(R.string.downloading_cooldowns));

            Map<String, String> directives_js = (Map<String, String>) parseDirectives(DIRECTIVES_JS, "notes");

            ParseAdditionalNotes notes_parser = new ParseAdditionalNotes(directives_js);

            List<Object> coolds = (List) parseJScript(COOLDOWNS_JS, "cooldowns");

            publishProgress(getString(R.string.loading_cooldowns));
            ArrayList<CoolDowns> cools_tmp = new ArrayList<>();
            if ((coolds != null) && (coolds.size() > 0)) {
                cools_tmp.add(new CoolDowns());
                for (int i = 0; i < coolds.size(); i++) {
                    Object entry = coolds.get(i);
                    if (entry == null) cools_tmp.add(new CoolDowns());
                    else if (entry.getClass().equals(Integer.class))
                        cools_tmp.add(new CoolDowns((Integer) coolds.get(i)));
                    else if (entry.getClass().equals(Double.class))
                        cools_tmp.add(new CoolDowns(((Double) coolds.get(i)).intValue()));
                    else if (entry.getClass().equals(NativeArray.class)) {
                        List<Double> entry_array = (List<Double>) coolds.get(i);
                        Integer a = entry_array.get(0).intValue();
                        Integer b = entry_array.get(1).intValue();
                        cools_tmp.add(new CoolDowns(a, b));
                    } else {
                        Log.d("DBG", "Object type determination failed!");
                        cools_tmp.add(new CoolDowns());
                    }
                }
            }
            publishProgress(getString(R.string.downloading_abilities));

            Map<Integer, Map> details_js = (Map<Integer, Map>) parseJScript(DETAILS_JS, "details");
            publishProgress(getString(R.string.loading_abilities));

            if ((details_js != null) && (details_js.size() > 0)) {
                database.beginTransaction();
                try {
                    for (Map.Entry<Integer, Map> entry : details_js.entrySet()) {
                        Map<String, Object> value = entry.getValue();

                        Object special = value.containsKey("special") ? value.get("special") : null;
                        String specialname = (String) (value.containsKey("specialName") ? value.get("specialName") : "");

                        String captain = "";
                        if (value.containsKey("captain")) {
                            Object captainObj = value.get("captain");
                            if (captainObj.getClass().equals(String.class))
                                captain = (String) value.get("captain");
                            else if (captainObj.getClass().equals(NativeObject.class)) {
                                Map<String, String> captainMap = (Map<String, String>) captainObj;
                                captain += "Global: " + captainMap.get("global") + System.getProperty("line.separator") +
                                        "Japan: " + captainMap.get("japan");
                            }
                        }
                        String captainnotes = notes_parser.parseNotes(value.containsKey("captainNotes") ? (String) value.get("captainNotes") : "");
                        String specialnotes = notes_parser.parseNotes(value.containsKey("specialNotes") ? (String) value.get("specialNotes") : "");

                        DBHelper.insertIntoCaptains(database, entry.getKey(), captain, captainnotes);

                        if ((special != null) && special.getClass().equals(NativeArray.class)) {
                            List<Map> specmulti = (List<Map>) special;
                            for (int n = 0; n < specmulti.size(); n++) {
                                Map<String, Object> currstep = specmulti.get(n);
                                String currspecial = (String) currstep.get("description");
                                CoolDowns cd = new CoolDowns(currstep.get("cooldown"));
                                DBHelper.insertIntoSpecials(database, entry.getKey(), specialname, currspecial, cd.init, cd.max, specialnotes);
                            }
                        } else if ((special != null) && special.getClass().equals(NativeObject.class)) {
                            String txt;
                            CoolDowns cooldwn = cools_tmp.get(entry.getKey());
                            Map<String, String> specials_localized = (Map<String, String>) special;
                            String japan = specials_localized.get("japan");
                            String global = specials_localized.get("global");
                            txt = "Jap: " + japan + System.getProperty("line.separator") +
                                    "Global: " + global;
                            DBHelper.insertIntoSpecials(database, entry.getKey(), specialname, txt, cooldwn.init, cooldwn.max, specialnotes);
                        } else if (special != null) {
                            CoolDowns cooldwn = new CoolDowns();
                            if (entry.getKey() < cools_tmp.size())
                                cooldwn = cools_tmp.get(entry.getKey());
                            DBHelper.insertIntoSpecials(database, entry.getKey(), specialname, (String) special, cooldwn.init, cooldwn.max, specialnotes);
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
            }
            publishProgress(getString(R.string.downloading_evolutions));

            Map<Integer, Map> evolutions = (Map<Integer, Map>) parseJScript(EVOLUTIONS_JS, "evolutions");
            publishProgress(getString(R.string.loading_evolutions));
            database.beginTransaction();
            try {
                for (Map.Entry<Integer, Map> entry : evolutions.entrySet()) {
                    Map<String, Object> value = entry.getValue();
                    Object evs = value.get("evolution");
                    if (evs.getClass().equals(Double.class)) {
                        //1 evolution
                        List<Double> evolvers = (List<Double>) value.get("evolvers");
                        Integer[] evolvers_int = new Integer[5];
                        for (int i = 0; i < 5; i++) {
                            if (i < evolvers.size())
                                evolvers_int[i] = evolvers.get(i).intValue();
                            else evolvers_int[i] = null;
                        }
                        DBHelper.insertIntoEvolutions(database, entry.getKey(), ((Double) evs).intValue(), evolvers_int[0],
                                evolvers_int[1], evolvers_int[2], evolvers_int[3], evolvers_int[4]);
                    } else if (evs.getClass().equals(NativeArray.class)) {
                        //multiple evolutions
                        List<Double> evs_list = (List<Double>) evs;
                        List<List<Double>> evolvers_list = (List<List<Double>>) value.get("evolvers");
                        for (int i = 0; i < evs_list.size(); i++) {
                            List<Double> evolvers = evolvers_list.get(i);
                            Integer[] evolvers_int = new Integer[5];
                            for (int n = 0; n < 5; n++) {
                                if (n < evolvers.size())
                                    evolvers_int[n] = evolvers.get(n).intValue();
                                else evolvers_int[n] = null;
                            }
                            DBHelper.insertIntoEvolutions(database, entry.getKey(), evs_list.get(i).intValue(), evolvers_int[0],
                                    evolvers_int[1], evolvers_int[2], evolvers_int[3], evolvers_int[4]);
                        }
                    }
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }

            publishProgress(getString(R.string.downloading_drops));
            Map<String, List<Map>> drops = (Map<String, List<Map>>) parseJScript(DROPS_JS, "drops");
            publishProgress(getString(R.string.loading_drops));
            Pattern pattern = Pattern.compile("-?[0-9]+");
            database.beginTransaction();
            try {
                List<Map> story_entries = drops.get(DROPS_STORY);
                for (Map<String, Object> element : story_entries)
                {
                    String location = (String)element.get("name");
                    Integer thumb = ((Double)element.get("thumb")).intValue();
                    Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                    Boolean isJapan = true;
                    for (Map.Entry<String, Object> entry : element.entrySet())
                    {
                        if (pattern.matcher(String.valueOf(entry.getKey())).matches()) {
                            // it's a chapter
                            List<Double> charIds = (List<Double>) entry.getValue();
                            for(Double charId : charIds)
                            {
                                if(charId>0)
                                    DBHelper.insertIntoDrops(database, charId.intValue(), location, String.valueOf(entry.getKey()), isGlobal, isJapan, thumb);
                            }
                        }
                    }
                    if(element.containsKey(DROP_COMPLETION))
                    {
                        List<Double> compl_units = (List<Double>)element.get(DROP_COMPLETION);
                        for(Double i : compl_units)
                        {
                            if(i>0)
                                DBHelper.insertIntoDrops(database, i.intValue(), location, DROP_COMPLETION, isGlobal, isJapan, thumb);
                        }
                    }
                }

                List<Map> weekly_entries = drops.get(DROPS_WEEKLY);
                for(Map<String, Object> element : weekly_entries)
                {
                    List<Double> charIds = (List<Double>)element.get(" ");
                    String drop_name = (String)element.get("name");
                    Integer drop_thumb = ((Double)element.get("thumb")).intValue();
                    Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                    Boolean isJapan = true;
                    for(Double charId : charIds)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "", isGlobal, isJapan, drop_thumb);
                    }
                }

                List<Map> forts_entries = drops.get(DROPS_FORTNIGHT);
                for(Map<String, Object> element : forts_entries)
                {
                    String drop_name = (String)element.get("name");
                    Integer drop_thumb = ((Double)element.get("thumb")).intValue();
                    Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                    Boolean isJapan = true;
                    if(element.containsKey("Elite"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Elite");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Elite", isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey("Expert"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Expert");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Expert", isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey(DROP_ALLDIFFS))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get(DROP_ALLDIFFS);
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, DROP_ALLDIFFS, isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey("Global"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Global");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, DROP_ALLDIFFS, true, false, drop_thumb);
                        }
                    }
                    if(element.containsKey("Japan"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Japan");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, DROP_ALLDIFFS, false, true, drop_thumb);
                        }
                    }
                }

                List<Map> raid_entries = drops.get(DROPS_RAID);
                for(Map<String, Object> element : raid_entries)
                {
                    String drop_name = (String)element.get("name");
                    Integer drop_thumb = ((Double)element.get("thumb")).intValue();
                    Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                    Boolean isJapan = true;
                    if(element.containsKey("Master"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Master");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Master", isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey("Expert"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Expert");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Expert", isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey("Ultimate"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Ultimate");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Ultimate", isGlobal, isJapan, drop_thumb);
                        }
                    }
                }

                List<Map> special_entries = drops.get(DROPS_SPECIAL);
                for(Map<String, Object> element : special_entries)
                {
                    String drop_name = (String)element.get("name");
                    Integer drop_thumb = element.containsKey("thumb") ? ((element.get("thumb")==null) ? 0 : ((Double)element.get("thumb")).intValue()) : 0;
                    Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                    Boolean isJapan = true;
                    if(element.containsKey(DROP_ALLDIFFS))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get(DROP_ALLDIFFS);
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, DROP_ALLDIFFS, isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey(DROP_COMPLETION))
                    {
                        List<Double> compl_units = (List<Double>)element.get(DROP_COMPLETION);
                        for(Double i : compl_units)
                        {
                            if(i>0)
                                DBHelper.insertIntoDrops(database, i.intValue(), drop_name, DROP_COMPLETION, isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey("Exhibition"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Exhibition");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Exhibition", isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey("Underground"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Underground");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Underground", isGlobal, isJapan, drop_thumb);
                        }
                    }
                    if(element.containsKey("Chaos"))
                    {
                        List<Double> eliteDrops = (List<Double>)element.get("Chaos");
                        for(Double charId : eliteDrops)
                        {
                            if(charId>0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Chaos", isGlobal, isJapan, drop_thumb);
                        }
                    }
                }
                database.setTransactionSuccessful();
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
                database.endTransaction();
            }

            database.close();
            db.close();
            isDownloaded = true;
            publishProgress("");
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
                        uri += "https://github.com/paolo-optc/optc-mobile-db/releases/download/" + version + "/app-release.apk";
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
                final Snackbar msg = Snackbar.make(findViewById(android.R.id.content), getString(R.string.update_msg), Snackbar.LENGTH_INDEFINITE);
                msg.setAction(getString(R.string.update_btn), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(result));
                        startActivity(i);*/
                        (new DownloadUpdate()).execute(result);
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

    private String getFileURLD(String uri) {
        try {
            URL url = new URL(uri);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder bdr = new StringBuilder();
            String endLine = System.getProperty("line.separator");
            br.readLine();
            while ((line = br.readLine()) != null) {
                bdr.append(line + endLine);
                if (line.equals("};")) break;
            }

            br.close();
            is.close();
            return bdr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Object parseDirectives(String uri, String objname) {

        String dump = getFileURLD(uri);

        // Every Rhino VM begins with the enter()
        // This Context is not Android's Context

        org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();

        // Turn off optimization to make Rhino Android compatible

        rhino.setOptimizationLevel(-1);

        try {
            Scriptable scope = rhino.initStandardObjects();

            // Note the forth argument is 1, which means the JavaScript source has
            // been compressed to only one line using something like YUI
            rhino.evaluateString(scope, dump, "JavaScript", 1, null);
            // Get the functionName defined in JavaScriptCode
            return scope.get(objname, scope);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            org.mozilla.javascript.Context.exit();
        }
        return null;
    }

    private ArrayList<HashMap> getFromDB() {
        DBHelper dbh = new DBHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ArrayList<HashMap> storage = DBHelper.getAllCharacters(db);
        db.close();
        dbh.close();
        return storage;
    }

    private Date getLastUpdate() {
        Date lastupdate = new Date(0);
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
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.pref_name), 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putLong(getString(R.string.lastupdate), value.getTime()).apply();
    }

    private Date getSerializedDate() {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.pref_name), 0);
        Long date_long = mPrefs.getLong(getString(R.string.lastupdate), 0);
        return new Date(date_long);
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

            hideKeyboard();
            return false;
        }
    };

    private void updateList() {
        adapter = new listViewAdapter(activity, list);
        lview.setAdapter(adapter);

        sortName.setBackgroundResource(R.drawable.ic_circle);
        sortName.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortType.setBackgroundResource(R.drawable.ic_circle);
        sortType.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortStars.setBackgroundResource(R.drawable.ic_circle);
        sortStars.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortAtk.setBackgroundResource(R.drawable.ic_circle);
        sortAtk.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortHP.setBackgroundResource(R.drawable.ic_circle);
        sortHP.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);

        sortRCV.setBackgroundResource(R.drawable.ic_circle);
        sortRCV.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);
    }

    private void hideKeyboard() {
        //  UNFOCUS TEXTBOX AND HIDE KEYBOARD
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        lview.requestFocus();
    }

    private void rebuildList() {
        list = original_list;
        list = FilterClass.filterWithDB(context, TypeFlags, ClassFlags, StarsFlags, FilterText);
        updateList();
    }

    private boolean isDbOn() {
        File dbFile = context.getDatabasePath(DBHelper.DB_NAME);
        return dbFile.exists();
    }

    public static int getResIdFromAttribute(final Activity activity, final int attr) {
        if (attr == 0)
            return 0;
        final TypedValue typedvalueattr = new TypedValue();
        activity.getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }

    private Integer progress_val_2 = 0;

    private void setProgressInt(Integer value) {
        if ((loading != null) && loading.isShowing()) {
            ArcProgress progress = (ArcProgress) loading.findViewById(R.id.loading_bar);
            ObjectAnimator anim = ObjectAnimator.ofInt(progress, "progress", progress_val_2, value);
            progress_val_2 = value;
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(200);
            anim.start();
        }
    }

    private boolean isUnknownSourcesEnabled() {
        boolean result = false;
        try {
            result = (Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private class DownloadUpdate extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... strings) {
            String downloadPath = strings[0];
            String localPath = getExternalFilesDir(null).getAbsolutePath() + "/optcsmartdb_install.apk";
            File file = new File(localPath);
            if (file.exists())
                file.delete();

            try {
                URL url = new URL(downloadPath);
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(localPath);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("YourApp", e.getMessage());
            }
            return localPath;
        }

        protected void onPreExecute() {
            showLoading(context);
        }

        protected void onPostExecute(String s) {
            hideLoading();
            /*if (!isUnknownSourcesEnabled()) {
                apk_file = s;
                (new AlertDialog.Builder(context)).setMessage(getString(R.string.enable_unknown_sources_first))
                        .setPositiveButton(getString(R.string.enable_unknown_sources_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS), SETTINGS_UNKNOWN_SOURCES);
                            }
                        })
                        .create().show();
            } else {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(new File(s)), "application/vnd.android.package-archive");
                Log.d("Lofting", "About to install new .apk");
                context.startActivity(i);
            } ### Android should handle by himself asking user to enable unknown sources   */

            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(s)), "application/vnd.android.package-archive");
            context.startActivity(i);

        }

        protected void onProgressUpdate(Integer... values) {
            setProgressInt(values[0]);
        }
    }

    private void showKeyboard() {
        //SHOW KEYBOARD
        filterText.requestFocus();
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(filterText, 0);
    }

    private static final int SETTINGS_UNKNOWN_SOURCES = 101;
    private String apk_file = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SETTINGS_UNKNOWN_SOURCES:
                if (!apk_file.equals("") && (new File(apk_file)).exists()) {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.fromFile(new File(apk_file)), "application/vnd.android.package-archive");
                    Log.d("Lofting", "About to install new .apk");
                    context.startActivity(i);
                }
                break;
        }
    }
}
