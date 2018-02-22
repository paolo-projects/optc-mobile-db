package it.instruman.treasurecruisedatabase;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.instruman.treasurecruisedatabase.nakama.network.BitmapComposer;
import it.instruman.treasurecruisedatabase.nakama.network.NNHelper;
import it.instruman.treasurecruisedatabase.nakama.network.QueryTeamsByStageTask;
import it.instruman.treasurecruisedatabase.nakama.network.QueryTeamsTask;
import it.instruman.treasurecruisedatabase.nakama.network.Team;
import it.instruman.treasurecruisedatabase.nakama.network.Unit;

/**
 * Created by infan on 19/02/2018.
 */

public class CharacterDetailsDialog extends Dialog {
    Context mContext;
    int charId;
    private ProgressBar progressBar;
    ImageButton searchButton;
    SearchView searchView;
    private int mThemeResId;

    public static final int thumbnail_width = 96;
    public static final int thumbnail_height = 96;

    public static int getResIdFromAttribute(final Context context, final int attr) {
        if (attr == 0)
            return 0;
        final TypedValue typedvalueattr = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }

    public String convertID(Integer ID) {
        if ((ID == 574) || (ID == 575)) {
            return ("00" + ID.toString());
        }
        if (ID < 10) return ("000" + ID.toString());
        else if (ID < 100) return ("00" + ID.toString());
        else if (ID < 1000) return ("0" + ID.toString());
        else return ID.toString();
    }

    private String replaceBr(String input) {
        String output = input.replace(" <br> ", System.getProperty("line.separator"));
        output = output.replace("<br> ", System.getProperty("line.separator"));
        output = output.replace("<br>", System.getProperty("line.separator"));
        return output;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public interface LaunchDialogInterface {
        void launch(int charId);
    }

    private LaunchDialogInterface mLaunchDialogInterface;

    public void setLaunchDialogInterface(LaunchDialogInterface launchDialogInterface) {
        mLaunchDialogInterface = launchDialogInterface;
    }

    public CharacterDetailsDialog(@NonNull Context context, int themeResId, int id) {
        super(context, themeResId);

        setContentView(R.layout.dialog_main);//setContentView(R.layout.character_info);

        mThemeResId = themeResId;
        mContext = context;
        charId = id;
        progressBar = findViewById(R.id.progressBar_cyclic);

        boolean daynightTheme = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_daynight_theme), false);
        if (daynightTheme) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            LinearLayout mainContent = findViewById(R.id.charInfoMainContent);
            /*if((8<=hour)&&(hour<20))
            {
                //DAYLIGHT
                KenBurnsView charInfoBgImg = (KenBurnsView)findViewById(R.id.charInfoBgImg);
                charInfoBgImg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ic_sunny_sky));
                RandomTransitionGenerator generator = new RandomTransitionGenerator(15000, new LinearInterpolator());
                charInfoBgImg.setTransitionGenerator(generator);
                charInfoBgImg.resume();
            } else {
                //NIGHT
                animateColor(mainContent, anim_charlist, "#333333", "#504e3c");
            }*/
        }

        searchButton = findViewById(R.id.nnteam_searchBtn);
        searchView = findViewById(R.id.nnteam_searchView);

        final TabHost tabs = findViewById(R.id.tabs_host);
        tabs.setup();

        TabHost.TabSpec main_info = tabs.newTabSpec("MAIN_INFO");
        main_info.setIndicator(context.getString(R.string.tab_maininfo));
        main_info.setContent(R.id.tab_maininfo);
        tabs.addTab(main_info);

        TabHost.TabSpec abilities = tabs.newTabSpec("ABILITIES");
        abilities.setIndicator(context.getString(R.string.tab_abilities));
        abilities.setContent(R.id.tab_abilities);
        tabs.addTab(abilities);

        TabHost.TabSpec limitbreak_tab = tabs.newTabSpec("LIMIT_BREAK");
        limitbreak_tab.setIndicator(context.getString(R.string.tab_limitbreak));
        limitbreak_tab.setContent(R.id.tab_limitbreak);
        tabs.addTab(limitbreak_tab);
        tabs.getTabWidget().getChildTabViewAt(2).setVisibility(View.GONE);

        TabHost.TabSpec evolutions_tab = tabs.newTabSpec("EVOLUTIONS");
        evolutions_tab.setIndicator(context.getString(R.string.tab_evolutions));
        evolutions_tab.setContent(R.id.tab_evolutions);
        tabs.addTab(evolutions_tab);
        tabs.getTabWidget().getChildTabViewAt(3).setVisibility(View.GONE);

        TabHost.TabSpec drops_tab = tabs.newTabSpec("DROPS");
        drops_tab.setIndicator(context.getString(R.string.tab_drops));
        drops_tab.setContent(R.id.tab_drops);
        tabs.addTab(drops_tab);
        tabs.getTabWidget().getChildTabViewAt(4).setVisibility(View.GONE);

        TabHost.TabSpec manuals_tab = tabs.newTabSpec("MANUALS");
        manuals_tab.setIndicator(context.getString(R.string.tab_manuals));
        manuals_tab.setContent(R.id.tab_manuals);
        tabs.addTab(manuals_tab);
        tabs.getTabWidget().getChildTabViewAt(5).setVisibility(View.GONE);

        boolean isNNTeamsEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.prefShowNNIntegration), true);
        TabHost.TabSpec nnteams_tab = tabs.newTabSpec("NNTEAMS");
        nnteams_tab.setIndicator(context.getString(R.string.tab_nnteams));
        nnteams_tab.setContent(R.id.tab_nnteams);
        tabs.addTab(nnteams_tab);
        if (!isNNTeamsEnabled)
            tabs.getTabWidget().getChildTabViewAt(6).setVisibility(View.GONE);

        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(tabChangeListener);

        for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
            TextView tv = tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
        }

        TextView title = findViewById(R.id.titleText);

        // set the custom dialog components - text, image and button
        ImageView image = findViewById(R.id.char_img_big);

        Glide
                .with(context)
                .load("http://onepiece-treasurecruise.com/wp-content/uploads/c" + convertID(id) + ".png")
                .into(image);

        setCanceledOnTouchOutside(true);

        //NOW WE SHOULD SET EVERYTHING (OUCH!)
        TextView class1 = findViewById(R.id.class1Text);
        TextView class2 = findViewById(R.id.class2Text);
        TextView type = findViewById(R.id.typeText);
        TextView stars = findViewById(R.id.starsText);
        TextView cost = findViewById(R.id.costText);

        TextView combo = findViewById(R.id.comboText);
        TextView slots = findViewById(R.id.slotsText);
        TextView maxlevel = findViewById(R.id.maxlevelText);
        TextView exptomax = findViewById(R.id.exptomaxText);

        TextView lvl1hp = findViewById(R.id.lvl1hpText);
        TextView lvl1atk = findViewById(R.id.lvl1atkText);
        TextView lvl1rcv = findViewById(R.id.lvl1rcvText);

        TextView maxhp = findViewById(R.id.lvlmaxhpText);
        TextView maxatk = findViewById(R.id.lvlmaxatkText);
        TextView maxrcv = findViewById(R.id.lvlmaxrcvText);
        TextView lvlmax = findViewById(R.id.lvlmaxtext);

        TextView lbhp = findViewById(R.id.lvllbhpText);
        TextView lbatk = findViewById(R.id.lvllbatkText);
        TextView lbrcv = findViewById(R.id.lvllbrcvText);

        HtmlTextView captability = findViewById(R.id.captabilityText);
        TextView captnotes = findViewById(R.id.capt_notes);
        TextView specname = findViewById(R.id.specnameText);

        DBHelper db = new DBHelper(context);
        SQLiteDatabase database = db.getReadableDatabase();
        CharacterInfo charInfo = DBHelper.getCharacterInfo(database, id);
        database.close();
        db.close();

        if (charInfo == null) return;

        title.setText(charInfo.getName());
        title.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));

        class1.setText(charInfo.getClass1());
        class2.setText(charInfo.getClass2());

        String ch_type = charInfo.getType();
        type.setText(ch_type);
        switch (ch_type.toLowerCase()) {
            case "str":
                type.setBackgroundColor(context.getResources().getColor(R.color.str_bg));
                type.setTextColor(context.getResources().getColor(R.color.str_txt));
                break;
            case "dex":
                type.setBackgroundColor(context.getResources().getColor(R.color.dex_bg));
                type.setTextColor(context.getResources().getColor(R.color.dex_txt));
                break;
            case "qck":
                type.setBackgroundColor(context.getResources().getColor(R.color.qck_bg));
                type.setTextColor(context.getResources().getColor(R.color.qck_txt));
                break;
            case "psy":
                type.setBackgroundColor(context.getResources().getColor(R.color.psy_bg));
                type.setTextColor(context.getResources().getColor(R.color.psy_txt));
                break;
            case "int":
                type.setBackgroundColor(context.getResources().getColor(R.color.int_bg));
                type.setTextColor(context.getResources().getColor(R.color.int_txt));
                break;
        }

        Double ch_stars = charInfo.getStars();
        DecimalFormat df = new DecimalFormat("0");
        df.setRoundingMode(RoundingMode.DOWN);
        String stars_p = df.format(ch_stars);

        if (ch_stars == 5.5)
            stars.setText("5+");
        else if (ch_stars == 6.5)
            stars.setText("6+");
        else
            stars.setText(stars_p);

        switch (stars_p) {
            case "1":
            case "2":
                stars.setBackgroundColor(context.getResources().getColor(R.color.bronze_bg));
                stars.setTextColor(context.getResources().getColor(R.color.bronze_txt));
                break;
            case "3":
                stars.setBackgroundColor(context.getResources().getColor(R.color.silver_bg));
                stars.setTextColor(context.getResources().getColor(R.color.silver_txt));
                break;
            case "4":
            case "5":
                stars.setBackgroundColor(context.getResources().getColor(R.color.gold_bg));
                stars.setTextColor(context.getResources().getColor(R.color.gold_txt));
                break;
            case "6":
                stars.setBackgroundColor(context.getResources().getColor(R.color.red_bg));
                stars.setTextColor(context.getResources().getColor(R.color.red_txt));
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

        Limits charLimits = charInfo.getCharLimits();
        Potentials charPotentials = charInfo.getCharPotentials();

        int additionalHP = 0;
        int additionalATK = 0;
        int additionalRCV = 0;
        if (charLimits != null) {
            String pattern = "Boosts base %s by (\\d+)";
            Pattern atkP = Pattern.compile(String.format(pattern, "ATK"));
            Pattern hpP = Pattern.compile(String.format(pattern, "HP"));
            Pattern rcvP = Pattern.compile(String.format(pattern, "RCV"));
            for (String lE : charLimits.getLimitEntries()) {
                try {
                    Matcher hpM = hpP.matcher(lE);
                    if (hpM.matches()) {
                        additionalHP += Integer.parseInt(hpM.group(1));
                        continue;
                    }
                    Matcher atkM = atkP.matcher(lE);
                    if (atkM.matches()) {
                        additionalATK += Integer.parseInt(atkM.group(1));
                        continue;
                    }
                    Matcher rcvM = rcvP.matcher(lE);
                    if (rcvM.matches())
                        additionalRCV += Integer.parseInt(rcvM.group(1));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        if (additionalHP > 0)
            lbhp.setText(String.valueOf(charInfo.getMaxHP() + additionalHP));
        else
            lbhp.setText(R.string.noLbStatsIncrease);
        if (additionalATK > 0)
            lbatk.setText(String.valueOf(charInfo.getMaxATK() + additionalATK));
        else
            lbatk.setText(R.string.noLbStatsIncrease);
        if (additionalRCV > 0)
            lbrcv.setText(String.valueOf(charInfo.getMaxRCV() + additionalRCV));
        else
            lbrcv.setText(R.string.noLbStatsIncrease);

        captability.setText(charInfo.getCaptainDescription());
        String capt_notes = charInfo.getCaptainNotes();
        if (!capt_notes.equals("")) {
            capt_notes = replaceBr(capt_notes);
            captnotes.setText(context.getString(R.string.notes_text) + capt_notes);
            captnotes.setVisibility(View.VISIBLE);
        }
        List<CharacterSpecials> char_specials = charInfo.getSpecials();
        if (char_specials.size() > 0) {
            specname.setText(charInfo.getSpecialName());
            LinearLayout specials_container = findViewById(R.id.specials_container);
            for (CharacterSpecials special : char_specials) {
                TextView special_description = new TextView(context);
                special_description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                special_description.setText(special.getSpecialDescription());
                special_description.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                specials_container.addView(special_description);

                LinearLayout coold_layout = new LinearLayout(context);
                coold_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView coold_title = new TextView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, dpToPx(4), 0);
                coold_title.setLayoutParams(params);
                coold_title.setText(context.getString(R.string.speccooldown));
                coold_title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                coold_title.setBackgroundColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_bg)));
                coold_title.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));
                coold_title.setText(context.getString(R.string.speccooldown));
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
                coold_content.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));

                coold_layout.addView(coold_content);

                String specialnotes = special.getSpecialNotes();
                if (!specialnotes.equals("")) {
                    specialnotes = replaceBr(specialnotes);
                    TextView special_notes = new TextView(context);
                    special_notes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    special_notes.setText(context.getString(R.string.notes_text) + specialnotes);
                    special_notes.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));
                    specials_container.addView(special_notes);
                }

                coold_layout.setPadding(2, 8, 2, 8);

                specials_container.addView(coold_layout);
            }
        }

        String cwDesc = charInfo.getCrewmateDescription();
        String cwNotes = charInfo.getCrewmateNotes();
        if (cwDesc != null) {

            LinearLayout specials_container = findViewById(R.id.specials_container);

            View sep = new View(context);
            LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
            sepParams.setMargins(0, dpToPx(5), 0, dpToPx(5));
            sep.setLayoutParams(sepParams);
            sep.setBackgroundColor(Color.parseColor("#aaaaaa"));

            specials_container.addView(sep);

            TextView cw_title = new TextView(context);
            cw_title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            cw_title.setBackgroundColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_bg)));
            cw_title.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));
            cw_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            cw_title.setText(context.getString(R.string.cw_text));
            specials_container.addView(cw_title);

            HtmlTextView cw_description = new HtmlTextView(context);
            cw_description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            cw_description.setText(cwDesc);
            cw_description.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
            specials_container.addView(cw_description);

            if (cwNotes != null) {
                HtmlTextView cw_notes = new HtmlTextView(context);
                cw_notes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                cw_notes.setText(context.getString(R.string.notes_text) + cwNotes);
                cw_notes.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));
                specials_container.addView(cw_notes);
            }
        }

        if (charLimits != null) {
            LinearLayout limitbreak_content = findViewById(R.id.limitbreak_content);
            /*ScrollView limitbreakScroll = new ScrollView(context);
            limitbreakScroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));*/
            LinearLayout limitbreakContent = new LinearLayout(context);
            limitbreakContent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            limitbreakContent.setOrientation(LinearLayout.VERTICAL
            );
            ArrayList<String> limEntries = charLimits.getLimitEntries();
            for (String entry : limEntries) {
                TextView limitRow = new TextView(context); //ROW WITH LIMIT BREAK TEXT
                limitRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                limitRow.setText(entry);
                limitRow.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                limitbreakContent.addView(limitRow);
            }
            String limitNotes = charLimits.getLimitNotes();
            if ((limitNotes != null) && (!limitNotes.equals(""))) {
                TextView limitRow = new TextView(context); //ROW WITH LIMIT BREAK TEXT
                limitRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                limitRow.setText(context.getResources().getString(R.string.limitnotes) + " " + limitNotes);
                limitRow.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));
                limitbreakContent.addView(limitRow);
            }

            if (charPotentials != null) {
                TextView limitRow = new TextView(context); //ROW WITH LIMIT BREAK TEXT
                limitRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                limitRow.setText(context.getResources().getString(R.string.potentials));
                limitRow.setTypeface(limitRow.getTypeface(), Typeface.BOLD);
                limitRow.setGravity(Gravity.CENTER);
                limitRow.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                limitbreakContent.addView(limitRow);
                LinkedHashMap<String, ArrayList<String>> potEntries = charPotentials.getPotentialEntries();
                for (Map.Entry<String, ArrayList<String>> potEntry : potEntries.entrySet()) {
                    TextView limitPRow = new TextView(context); //ROW WITH LIMIT BREAK TEXT
                    limitPRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    limitPRow.setText(potEntry.getKey());
                    limitPRow.setTypeface(limitPRow.getTypeface(), Typeface.BOLD);
                    limitPRow.setBackgroundColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_bg)));
                    limitPRow.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));
                    limitbreakContent.addView(limitPRow);

                    ArrayList<String> potEntryList = potEntry.getValue();
                    for (String potValue : potEntryList) {
                        TextView limitP2Row = new TextView(context); //ROW WITH LIMIT BREAK TEXT
                        limitP2Row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        limitP2Row.setText(potValue);
                        limitP2Row.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                        limitbreakContent.addView(limitP2Row);
                    }
                }
                String potentialNotes = charPotentials.getPotentialNotes();
                if ((potentialNotes != null) && (!potentialNotes.equals(""))) {
                    TextView limitNRow = new TextView(context); //ROW WITH LIMIT BREAK TEXT
                    limitNRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    limitNRow.setText(context.getResources().getString(R.string.limitnotes) + " " + potentialNotes);
                    limitNRow.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));
                    limitbreakContent.addView(limitNRow);
                }
            }
            /*limitbreakScroll.addView(limitbreakContent);*/
            limitbreak_content.addView(limitbreakContent);
            tabs.getTabWidget().getChildTabViewAt(2).setVisibility(View.VISIBLE);
        }

        LinearLayout evolutions_content = findViewById(R.id.evolutions_content);
        List<CharacterEvolutions> evos = charInfo.getEvolutions();

        if (evos.size() > 0) {
            //MULTIPLE EVOLUTIONS
            for (int i = 0; i < evos.size(); i++) {
                final Integer context_id = evos.get(i).getEvolutionCharacter();
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
                        .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(context_id) + ".png")
                        .dontTransform()
                        .override(thumbnail_width, thumbnail_height)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(evo_pic); //ADD PIC
                evo_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLaunchDialogInterface.launch(context_id);
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
                Drawable imgDrw = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.ic_left_arrow));
                DrawableCompat.setTintMode(imgDrw, PorterDuff.Mode.SRC_IN);
                DrawableCompat.setTint(imgDrw, context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                evo_text.setImageDrawable(imgDrw);
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
                        if (evolver > 0) {
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
                                    mLaunchDialogInterface.launch(evolver);
                                }
                            });
                        } else {
                            Glide
                                    .with(context)
                                    .load(SkullsHelper.getThumbFromId(evolver))
                                    .dontTransform()
                                    .override(thumbnail_width, thumbnail_height)
                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                    .into(evolver_pic); //ADD PIC
                            evolver_pic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //null
                                }
                            });
                        }
                        evolution_row.addView(evolver_pic);
                    }
                }
                evolution_scroll.addView(evolution_row);
                evolutions_content.addView(evolution_scroll);
            }
            tabs.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
        }

        LinearLayout drops_content = findViewById(R.id.drops_content);
        List<DropInfo> drops = charInfo.getDropInfo();

        if (drops.size() > 0) {
            for (int i = 0; i < drops.size(); i++) {
                DropInfo context_drops = drops.get(i);

                LinearLayout drops_row = new LinearLayout(context); //CREATE ROW TO SHOW EVOLUTION AND EVOLVERS
                drops_row.setOrientation(LinearLayout.HORIZONTAL); //SET ORIENTATION TO HORIZONTAL
                drops_row.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )); // SET WIDTH AND HEIGHT
                drops_row.setGravity(Gravity.CENTER_VERTICAL);

                //############# SMALL ICON ###############
                ImageButton evo_pic = new ImageButton(context); //CREATE PIC
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dpToPx(48), dpToPx(48)
                );
                params.setMargins(2, 5, 2, 5);
                params.gravity = Gravity.CENTER;
                evo_pic.setLayoutParams(params); // SET WIDTH AND HEIGHT OF PIC
                evo_pic.setPadding(0, 0, 0, 0);
                evo_pic.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                Integer cont_id = context_drops.getDropThumbnail();
                if (cont_id > 0) {
                    Glide
                            .with(context)
                            .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(cont_id) + ".png")
                            .dontTransform()
                            .override(thumbnail_width, thumbnail_height)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(evo_pic); //ADD PIC
                } else {
                    Glide
                            .with(context)
                            .load(SkullsHelper.getThumbFromId(cont_id))
                            .dontTransform()
                            .override(thumbnail_width, thumbnail_height)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(evo_pic); //ADD PIC
                }
                drops_row.addView(evo_pic);

                TextView drop_name = new TextView(context);
                LinearLayout.LayoutParams txt_params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                txt_params.setMargins(dpToPx(10), 0, 5, 0);
                drop_name.setLayoutParams(txt_params);
                drop_name.setText(context_drops.getDropLocation());
                drop_name.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                drop_name.setGravity(Gravity.CENTER);

                drops_row.addView(drop_name);

                TextView drop_det = new TextView(context);
                LinearLayout.LayoutParams txt2_params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                txt2_params.setMargins(5, 0, 5, 0);
                drop_det.setLayoutParams(txt2_params);
                drop_det.setText(context_drops.getDropChapterOrDifficulty());
                drop_det.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                drop_det.setGravity(Gravity.CENTER);

                drops_row.addView(drop_det);

                TextView drop_notes = new TextView(context);
                drop_notes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                drop_notes.setGravity(Gravity.CENTER);
                drop_notes.setTypeface(drop_notes.getTypeface(), Typeface.BOLD);
                drop_notes.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));

                if (context_drops.isGlobal() && !context_drops.isJapan())
                    drop_notes.setText(context.getString(R.string.drops_global));
                else if (!context_drops.isGlobal() && context_drops.isJapan())
                    drop_notes.setText(context.getString(R.string.drops_japan));

                drops_content.addView(drops_row);
                if (!drop_notes.getText().equals("")) drops_content.addView(drop_notes);
            }
            tabs.getTabWidget().getChildTabViewAt(4).setVisibility(View.VISIBLE);
        }

        LinearLayout manuals_content = findViewById(R.id.manuals_content);
        List<DropInfo> manuals = charInfo.getManualsInfos();

        if (manuals.size() > 0) {
            for (int i = 0; i < manuals.size(); i++) {
                DropInfo context_manuals = manuals.get(i);

                LinearLayout manuals_row = new LinearLayout(context); //CREATE ROW TO SHOW EVOLUTION AND EVOLVERS
                manuals_row.setOrientation(LinearLayout.HORIZONTAL); //SET ORIENTATION TO HORIZONTAL
                manuals_row.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )); // SET WIDTH AND HEIGHT
                manuals_row.setGravity(Gravity.CENTER_VERTICAL);

                //############# SMALL ICON ###############
                ImageButton evo_pic = new ImageButton(context); //CREATE PIC
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dpToPx(48), dpToPx(48)
                );
                params.setMargins(2, 5, 2, 5);
                params.gravity = Gravity.CENTER;
                evo_pic.setLayoutParams(params); // SET WIDTH AND HEIGHT OF PIC
                evo_pic.setPadding(0, 0, 0, 0);
                evo_pic.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                Integer cont_id = context_manuals.getDropThumbnail();
                Glide
                        .with(context)
                        .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(cont_id) + ".png")
                        .dontTransform()
                        .override(thumbnail_width, thumbnail_height)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(evo_pic); //ADD PIC
                manuals_row.addView(evo_pic);

                TextView manual_name = new TextView(context);
                LinearLayout.LayoutParams txt_params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                txt_params.setMargins(dpToPx(10), 0, 5, 0);
                manual_name.setLayoutParams(txt_params);
                manual_name.setText(context_manuals.getDropLocation());
                manual_name.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                manual_name.setGravity(Gravity.CENTER);

                manuals_row.addView(manual_name);

                TextView manual_det = new TextView(context);
                LinearLayout.LayoutParams txt2_params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                txt2_params.setMargins(5, 0, 5, 0);
                manual_det.setLayoutParams(txt2_params);
                manual_det.setText(context_manuals.getDropChapterOrDifficulty());
                manual_det.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_txt)));
                manual_det.setGravity(Gravity.CENTER);

                manuals_row.addView(manual_det);

                TextView manual_notes = new TextView(context);
                manual_notes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                manual_notes.setGravity(Gravity.CENTER);
                manual_notes.setTypeface(manual_notes.getTypeface(), Typeface.BOLD);
                manual_notes.setTextColor(context.getResources().getColor(getResIdFromAttribute(context, R.attr.char_info_header_txt)));

                if (context_manuals.isGlobal() && !context_manuals.isJapan())
                    manual_notes.setText(context.getString(R.string.drops_global));
                else if (!context_manuals.isGlobal() && context_manuals.isJapan())
                    manual_notes.setText(context.getString(R.string.drops_japan));

                manuals_content.addView(manuals_row);
                if (!manual_notes.getText().equals("")) manuals_content.addView(manual_notes);
            }
            tabs.getTabWidget().getChildTabViewAt(5).setVisibility(View.VISIBLE);
        }

        HorizontalScrollView scr = findViewById(R.id.tabs_scrollview);
        scr.invalidate();
        scr.requestLayout();

        ImageButton backbtn = findViewById(R.id.backBtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.97);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.85);
        if (getWindow() != null) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.dimAmount = .7f;
            getWindow().setAttributes(layoutParams);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(width, height);
        }
        show();
    }

    /**
     * Builds the list with team data.
     *
     * @param nnTeams      List of teams to be displayed
     * @param totalEntries Total entries inside the web server. If this number is higher that the displayed items a View more button is displayed.
     * @param viewMoreUrl  Url pointing to the Nakama.network teams list
     */
    private void insertNNTeamsData(List<Team> nnTeams, int totalEntries, final String viewMoreUrl) {
        LinearLayout teamsContainer = findViewById(R.id.nnteams_content);
        teamsContainer.removeAllViews();
        if (nnTeams != null && nnTeams.size() > 0) {
            for (final Team team : nnTeams) {
                LinearLayout clickWrapper = new LinearLayout(mContext);
                clickWrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                clickWrapper.setOrientation(LinearLayout.VERTICAL);

                HorizontalScrollView team_scroll = new HorizontalScrollView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                );
                lp.setMargins(0, dpToPx(8), 0, dpToPx(8));
                team_scroll.setLayoutParams(lp);
                LinearLayout team_row = new LinearLayout(mContext); //CREATE ROW TO SHOW THE TEAM COMPONENTS
                team_row.setOrientation(LinearLayout.HORIZONTAL); //SET ORIENTATION TO HORIZONTAL
                team_row.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )); // SET WIDTH AND HEIGHT

                TreeMap<Integer, Unit> teamMembers = NNHelper.getOrderedUnitList(team.getTeamUnits());

                //########## TEAM PICS ###########
                for (Map.Entry<Integer, Unit> entry : teamMembers.entrySet()) {
                    Unit unit = entry.getValue();
                    if (entry.getKey() == 2) {
                        View v = new View(mContext);
                        v.setBackgroundResource(R.color.div_color);
                        LinearLayout.LayoutParams vlP = new LinearLayout.LayoutParams(dpToPx(4), dpToPx(2));
                        vlP.setMargins(dpToPx(2), 0, dpToPx(2), 0);
                        v.setLayoutParams(vlP);
                        team_row.addView(v);
                        v.setClickable(false);
                        v.setDuplicateParentStateEnabled(true);
                    }
                    if (!unit.isGeneric()) {
                        int unitId = unit.getUnitId();
                        if (unitId != 0) {
                            ImageButton unit_pic = new ImageButton(mContext); //CREATE EVOLUTION PIC
                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                                    dpToPx(48), dpToPx(48)
                            );
                            params2.setMargins(0, 5, 2, 5);
                            params2.gravity = Gravity.CENTER;
                            unit_pic.setLayoutParams(params2); // SET WIDTH AND HEIGHT OF PIC
                            unit_pic.setPadding(0, 0, 0, 0);
                            unit_pic.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                            if (unitId > 0) {
                                Glide
                                        .with(mContext)
                                        .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(unitId) + ".png")
                                        .dontTransform()
                                        .override(thumbnail_width, thumbnail_height)
                                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                        .into(unit_pic); //ADD PIC
                            }
                            team_row.addView(unit_pic);
                        }
                    } else {
                        //GENERIC UNIT
                        ImageButton unit_pic = new ImageButton(mContext); //CREATE EVOLUTION PIC
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                                dpToPx(48), dpToPx(48)
                        );
                        params2.setMargins(0, 5, 2, 5);
                        params2.gravity = Gravity.CENTER;
                        unit_pic.setLayoutParams(params2); // SET WIDTH AND HEIGHT OF PIC
                        unit_pic.setPadding(0, 0, 0, 0);
                        unit_pic.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                        unit_pic.setImageBitmap(BitmapComposer.composeBitmap(mContext, unit.getGenericRole(), unit.getGenericType(), unit.getGenericClass()));

                        team_row.addView(unit_pic);
                    }
                }
                team_scroll.addView(team_row);
                clickWrapper.addView(team_scroll);

                LinearLayout teamDescription = new LinearLayout(mContext);
                teamDescription.setOrientation(LinearLayout.HORIZONTAL);
                teamDescription.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.setMargins(0, 0, 0, dpToPx(12));
                teamDescription.setLayoutParams(lp2);

                TextView teamNameTitle = new TextView(mContext);
                teamNameTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                teamNameTitle.setText(R.string.nnTeamsTeamName);
                teamNameTitle.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                teamNameTitle.setTypeface(teamNameTitle.getTypeface(), Typeface.BOLD);
                teamNameTitle.setPadding(0, 0, dpToPx(6), 0);

                teamDescription.addView(teamNameTitle);

                TextView teamName = new TextView(mContext);
                teamName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                teamName.setText(team.getName());
                teamName.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                teamName.setPadding(0, 0, dpToPx(12), 0);

                teamDescription.addView(teamName);

                TextView teamShipTitle = new TextView(mContext);
                teamShipTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                teamShipTitle.setText(R.string.nnTeamsShip);
                teamShipTitle.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                teamShipTitle.setTypeface(teamShipTitle.getTypeface(), Typeface.BOLD);
                teamShipTitle.setPadding(0, 0, dpToPx(6), 0);

                teamDescription.addView(teamShipTitle);

                TextView teamShip = new TextView(mContext);
                teamShip.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                teamShip.setText(team.getShipName());
                teamShip.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                teamShip.setPadding(0, 0, dpToPx(16), 0);

                teamDescription.addView(teamShip);

                clickWrapper.addView(teamDescription);

                LinearLayout teamDescription2 = new LinearLayout(mContext);
                teamDescription2.setOrientation(LinearLayout.HORIZONTAL);
                teamDescription2.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp3.setMargins(0, 0, 0, dpToPx(12));
                teamDescription2.setLayoutParams(lp3);

                if (team.getStageId() != null) {
                    TextView stageNameTitle = new TextView(mContext);
                    stageNameTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    stageNameTitle.setText(R.string.nnTeamsStage);
                    stageNameTitle.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                    stageNameTitle.setTypeface(stageNameTitle.getTypeface(), Typeface.BOLD);
                    stageNameTitle.setPadding(0, 0, dpToPx(6), 0);

                    teamDescription2.addView(stageNameTitle);

                    TextView stageName = new TextView(mContext);
                    stageName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                    stageName.setText(NNHelper.getStageName(team.getStageId()));
                    stageName.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                    stageName.setPadding(0, 0, dpToPx(12), 0);

                    teamDescription2.addView(stageName);
                }

                TextView authorNameTitle = new TextView(mContext);
                authorNameTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                authorNameTitle.setText(R.string.nnTeamsUser);
                authorNameTitle.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                authorNameTitle.setTypeface(authorNameTitle.getTypeface(), Typeface.BOLD);
                authorNameTitle.setPadding(0, 0, dpToPx(6), 0);

                teamDescription2.addView(authorNameTitle);

                TextView authorName = new TextView(mContext);
                authorName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                authorName.setText(team.getSubmittedBy());
                authorName.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                authorName.setPadding(0, 0, dpToPx(16), 0);

                teamDescription2.addView(authorName);

                clickWrapper.addView(teamDescription2);

                clickWrapper.setBackgroundResource(getResIdFromAttribute(mContext, android.R.attr.selectableItemBackground));
                clickWrapper.setClickable(true);
                clickWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                        viewIntent.setData(Uri.parse(NNHelper.getViewTeamURL(team.getID())));
                        mContext.startActivity(viewIntent);
                    }
                });

                teamsContainer.addView(clickWrapper);

                View v = new View(mContext);
                v.setBackgroundColor(mContext.getResources().getColor(R.color.div_color));
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(2)));

                teamsContainer.addView(v);
            }

            if (totalEntries > nnTeams.size()) {
                TextView viewMoreTextView = new TextView(mContext);
                viewMoreTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                int _6dp = dpToPx(6);
                viewMoreTextView.setPadding(_6dp, _6dp, _6dp, _6dp);
                viewMoreTextView.setText(R.string.nnTeamsViewMore);
                viewMoreTextView.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
                viewMoreTextView.setBackgroundResource(getResIdFromAttribute(mContext, android.R.attr.selectableItemBackground));
                viewMoreTextView.setGravity(Gravity.CENTER);
                viewMoreTextView.setTypeface(viewMoreTextView.getTypeface(), Typeface.BOLD);

                viewMoreTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewMore = new Intent(Intent.ACTION_VIEW);
                        viewMore.setData(Uri.parse(viewMoreUrl));
                        mContext.startActivity(viewMore);
                    }
                });

                teamsContainer.addView(viewMoreTextView);

                searchButton.setVisibility(View.VISIBLE);
                searchButton.setOnClickListener(searchBtnShowBar);

                searchView.setOnQueryTextListener(searchTextListener);
            }
        } else {
            TextView noEntriesTextView = new TextView(mContext);
            noEntriesTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int _6dp = dpToPx(6);
            noEntriesTextView.setPadding(_6dp, _6dp, _6dp, _6dp);
            noEntriesTextView.setText(R.string.nnTeamsNoEntries);
            noEntriesTextView.setTextColor(mContext.getResources().getColor(getResIdFromAttribute(mContext, R.attr.char_info_txt)));
            noEntriesTextView.setGravity(Gravity.CENTER);
            noEntriesTextView.setTypeface(noEntriesTextView.getTypeface(), Typeface.BOLD);

            teamsContainer.addView(noEntriesTextView);
        }
        progressBar.setVisibility(View.GONE);
        teamsContainer.invalidate();
    }

    private boolean teamsTabFirstOpen = true;
    private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            if (tabId.equals("NNTEAMS")) {
                if (teamsTabFirstOpen) {
                    progressBar.setVisibility(View.VISIBLE);
                    QueryTeamsTask queryTeamsTask = new QueryTeamsTask(queryTeamsTaskResult);
                    queryTeamsTask.execute(charId);
                    teamsTabFirstOpen = false;
                }
            }
        }
    };

    private QueryTeamsTask.TaskResult queryTeamsTaskResult = new QueryTeamsTask.TaskResult() {
        @Override
        public void onResultsAvailable(List<Team> results, int totalEntries) {
            isStageFiltered = false;
            insertNNTeamsData(results, totalEntries, NNHelper.getViewMoreURL(charId));
        }
    };

    private View.OnClickListener searchBtnShowBar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchView.setVisibility(View.VISIBLE);
            searchButton.setImageResource(R.drawable.ic_expand_less);
            searchButton.setOnClickListener(searchBtnHideBar);

            ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setQuery("", false);
                    searchView.setVisibility(View.GONE);
                    searchButton.setOnClickListener(searchBtnShowBar);
                    searchButton.setImageResource(R.drawable.ic_search);
                    if (isStageFiltered) {
                        progressBar.setVisibility(View.VISIBLE);
                        QueryTeamsTask queryTeamsTask = new QueryTeamsTask(queryTeamsTaskResult);
                        queryTeamsTask.execute(charId);
                    }
                }
            });
        }
    };

    private View.OnClickListener searchBtnHideBar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchView.setQuery("", false);
            searchView.setVisibility(View.GONE);
            searchButton.setImageResource(R.drawable.ic_search);
            searchButton.setOnClickListener(searchBtnShowBar);
            if (isStageFiltered) {
                progressBar.setVisibility(View.VISIBLE);
                QueryTeamsTask queryTeamsTask = new QueryTeamsTask(queryTeamsTaskResult);
                queryTeamsTask.execute(charId);
            }
        }
    };

    boolean isStageFiltered = false;
    private SearchView.OnQueryTextListener searchTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchView.clearFocus();
            StagesDialog stagesDialog = new StagesDialog(mContext, mThemeResId, onStageSelected, query);
            stagesDialog.show();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    StagesDialog.OnStageSelected onStageSelected = new StagesDialog.OnStageSelected() {
        @Override
        public boolean stageSelected(int stageId, String stageName) {
            progressBar.setVisibility(View.VISIBLE);
            QueryTeamsByStageTask queryTeamsTask = new QueryTeamsByStageTask(queryTeamsByStageTaskResult);
            queryTeamsTask.execute(charId, stageId);
            return false;
        }
    };

    QueryTeamsByStageTask.TaskResult queryTeamsByStageTaskResult = new QueryTeamsByStageTask.TaskResult() {
        @Override
        public void onResultsAvailable(List<Team> results, int totalEntries, int stageId) {
            isStageFiltered = true;
            insertNNTeamsData(results, totalEntries, NNHelper.getViewMoreWithStageURL(charId, stageId));
            searchView.clearFocus();
        }
    };
}