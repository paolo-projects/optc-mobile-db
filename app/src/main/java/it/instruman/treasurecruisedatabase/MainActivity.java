package it.instruman.treasurecruisedatabase;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
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
import java.util.Calendar;
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
private final static Double APP_VERSION = 3.6;
/*
    ##################################################
*/

    public static final int thumbnail_width = 96;
    public static final int thumbnail_height = 96;

    private static final String locale_pref = "locale-set";
    private boolean goingToSettings = false;

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

    public enum FL_CAPT_FLAGS {
        TYPE_BOOSTING_CAPTAINS("Type-boosting captains", "Boosts (ATK|HP|RCV|ATK and HP|ATK and RCV|HP and RCV|ATK, HP and RCV) of[^,]+(STR|DEX|QCK|PSY|INT)\\b", "Aumenta (ATK|HP|RCV|ATK e HP|ATK e RCV|HP e RCV|ATK, HP e RCV) dei[^,]+(STR|DEX|QCK|PSY|INT)\\b"),
        CLASS_BOOSTING_CAPTAINS("Class-boosting captains", "Boosts (ATK|HP|RCV|ATK and HP|ATK and RCV|HP and RCV|ATK, HP and RCV) of[^,]+(Slasher|Striker|Fighter|Shooter|Free Spirit|Cerebral|Powerhouse|Driven)", "Aumenta (ATK|HP|RCV|ATK e HP|ATK e RCV|HP e RCV|ATK, HP e RCV) dei[^,]+(Slasher|Striker|Fighter|Shooter|Free Spirit|Cerebral|Powerhouse|Driven)"),
        ATK_BOOSTING_CAPTAINS("ATK boosting captains", "Boosts ATK", "Aumenta ATK"),
        HP_BOOSTING_CAPTAINS("HP boosting captains", "Boosts (HP|ATK and HP|ATK, HP)|Boosts.+and their HP","Aumenta (HP|ATK e HP|ATK, HP)|Aumenta.+e i loro HP"),
        RCV_BOOSTING_CAPTAINS("RCV boosting captains", "Boosts (RCV|ATK and RCV|HP and RCV|ATK, HP and RCV)|Boosts.+and their RCV", "Aumenta (RCV|ATK e RCV|HP e RCV|ATK, HP e RCV)|Aumenta.+e i loro RCV"),
        SPECIAL_BOOSTING_CAPTAINS("Special boosting captains", "Boosts damage.+specials", "Aumenta il danno.+attacchi speciali"),
        _2X_ATK_AND_HP_CAPTAINS("2x ATK and HP captains", "Boosts (ATK and HP|ATK, HP).+by 2x", "Aumenta (ATK e HP|ATK, HP).+di 2x"),
        _2X_ATK_AND_RCV_CAPTAINS("2x ATK and RCV captains", "Boosts ATK and RCV.+by 2x", "Aumenta ATK e RCV.+di 2x"),
        _2_5X_ATK_CAPTAINS("2.5x ATK captains", "Boosts (their )?ATK.+by 2.5x", "Aumenta (il loro )?ATK.+di 2.5x"),
        _2_75X_ATK_CAPTAINS("2.75x ATK captains", "Boosts (their )?ATK.+by 2.75x", "Aumenta (il loro )?ATK.+di 2.75x", new Integer[] { 529, 530, 668, 669 }),
        _3X_ATK_CAPTAINS("3x ATK captains", "Boosts (their )?ATK.+by 3x", "Aumenta (il loro )?ATK.+di 3x"),
        HP_BASED_ATK_CAPTAINS("HP-based ATK captains", "Boosts ATK.+proportionally to", "Aumenta ATK.+proporzionalmente a"),
        POSITIONAL_CAPTAINS("Positional captains", "(after scoring|following a chain|perfect|great|good)", "(dopo aver fatto|seguendo l'ordine|Dopo aver fatto.+ Good|Dopo aver fatto.+ Great|Dopo aver fatto.+ Perfect)"),
        BENEFICIAL_ORB_CAPTAINS("\"Beneficial\" Orb captains", "\"beneficial", "Gli orb ((STR|DEX|QCK|PSY|INT|\\[RCV\\]|\\[TND\\])|((STR|DEX|QCK|PSY|INT|\\[RCV\\]|\\[TND\\]) e (STR|DEX|QCK|PSY|INT|\\[RCV\\]|\\[TND\\]))) funzionano come se fossero positivi"),
        CHAIN_MULTIPLIERS("Chain multipliers", "Boosts.+chain multiplier", "Aumenta.+moltiplicatore.+della catena"),
        COOLDOWN_REDUCERS("Cooldown reducers", "reduces.+cooldown", "Riduce.+turn.+attacchi speciali"),
        DAMAGE_REDUCERS("Damage reducers", "Reduces (any )?damage received", "Riduce (qualsiasi )?il danno ricevuto"),
        HEALERS("Healers", "Recovers", "Recupera"),
        TANKERS("Tankers", "Reduces (any )?damage.+if HP.+99", "Riduce (qualsiasi )?il danno.+se gli HP.+99"),
        ZOMBIES("Zombies", "Protects from defeat", "Protegge dalla sconfitta"),
        END_OF_TURN_DAMAGE_DEALER("End of Turn Damage Dealer", "deals.+end of each turn", "Infligge.+alla fine di ogni turno");

        private String text;
        private String matcher, matcher_it;
        private Integer[] include = {};

        FL_CAPT_FLAGS(String text, String matcher, String matcher_it) { this.text = text; this.matcher = matcher; this.matcher_it = matcher_it; }
        FL_CAPT_FLAGS(String text, String matcher, String matcher_it, Integer[] include) { this.text = text; this.matcher = matcher; this.matcher_it = matcher_it; this.include = include;}

        public String getText() {
            return this.text;
        }
        public String getMatcher() { return this.matcher; }
        public String getMatcherIt() { return this.matcher_it; }
        public Integer[] getInclude() { return this.include; }

        public boolean textEqualsCaseInsensitive(String what) {
            if (what != null) {
                return this.text.equalsIgnoreCase(what);
            }
            throw new IllegalArgumentException("Argument can't be null");
        }

        public boolean matcherEqualsCaseInsensitive(String what) {
            if (what != null) {
                return this.matcher.equalsIgnoreCase(what);
            }
            throw new IllegalArgumentException("Argument can't be null");
        }

        public static FL_CAPT_FLAGS fromString(String text) {
            if (text != null) {
                for (FL_CAPT_FLAGS b : FL_CAPT_FLAGS.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No constant named " + text);
        }

        public static FL_CAPT_FLAGS fromMatcher(String matcher) {
            if (matcher != null) {
                for (FL_CAPT_FLAGS b : FL_CAPT_FLAGS.values()) {
                    if (matcher.equalsIgnoreCase(b.matcher)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No constant named " + matcher);
        }
    }

    public enum FL_SPEC_FLAGS {
        TYPE_BOOSTING_SPECIALS("Type-boosting specials", "Boosts (ATK|HP|RCV|ATK and HP|ATK and RCV|HP and RCV|ATK, HP and RCV) of[^,]+(STR|DEX|QCK|PSY|INT)\\b", "Aumenta (ATK|HP|RCV|ATK e HP|ATK e RCV|HP e RCV|ATK, HP e RCV) dei[^,]+(STR|DEX|QCK|PSY|INT)\\b"),
        CLASS_BOOSTING_SPECIALS("Class-boosting specials", "Boosts (ATK|HP|RCV|ATK and HP|ATK and RCV|HP and RCV|ATK, HP and RCV) of[^,]+(Slasher|Striker|Fighter|Shooter|Free Spirit|Cerebral|Powerhouse|Driven)", "Aumenta (ATK|HP|RCV|ATK e HP|ATK e RCV|HP e RCV|ATK, HP e RCV) dei[^,]+(Slasher|Striker|Fighter|Shooter|Free Spirit|Cerebral|Powerhouse|Driven)"),
        x1_5_ATK_SPECIALS("1.5x ATK specials", "Boosts ATK.+by 1.5x", "Aumenta ATK.+di 1.5x"),
        x1_75_ATK_SPECIALS("1.75x ATK specials", "Boosts ATK.+by 1.75x", "Aumenta ATK.+di 1.75x"),
        x2_ATK_SPECIALS("2x ATK specials", "Boosts ATK.+by 2x", "Aumenta ATK.+di 2x"),
        CONDITIONAL_ATK_BOOSTERS("Conditional ATK boosters", "Boosts ATK.+against.+enemies", "Aumenta ATK.+contro.+nemici"),
        DELAYED_ATK_BOOSTERS("Delayed ATK boosters", "(Following the activation.+boosts.+ATK|If during that turn.+boosts.+ATK)", "(Dopo l'attivazione.+aumenta.+ATK|Se durante il turno.+aumenta.+ATK)"),
        COLOR_AFFINITY_BOOSTERS("Color Affinity boosters", "Boosts the Color Affinity", "\\\"l\\'affinità di colore\\\""),
        RCV_BOOSTERS("RCV boosters", "Boosts RCV", "Aumenta RCV"),
        ORB_LOCKERS("Orb lockers", "locks.+orbs", "Blocca.+orbs"),
        ORB_BOOSTERS("Orb boosters", "amplifies.+orb", "Amplifica.+orb"),
        ORB_CHANCE_BOOSTERS("Orb chance boosters", "boosts chances of getting.+orbs", "Aumenta le probabilità di ottenere.+orbs"),
        ORB_CONTROLLERS("Orb controllers", "(Changes.+(orb|orbs))", "(Cambia.+(orb|orbs))"),
        FULL_BOARD_ORB_CONTROLLERS("Full-board orb controllers", "(Changes[^,]+all orbs|Changes the orbs in|Changes[^,]*every other orb)", "(Cambia[^,]+tutti gli orbs|Cambia gli orbs della|Cambia[^,]*gli altri orb|Cambia casualmente gli orbs della|Sostituisce casualmente ogni altro orb)"),
        SELF_ORB_CONTROLLERS("Self-orb controllers", "Changes.+own orb.+into", "Cambia.+il proprio orb.+in"),
        ORB_RANDOMIZERS("Orb randomizers", "randomizes.+orb", "Sostituisce casualmente.+orb"),
        ORB_SWITCHERS("Orb switchers", "switches orbs", "Sposta gli orbs tra loro"),
        ORB_MATCHERS("Orb matchers", "(Changes.+(orb|orbs))[^,]+Matching", "(Cambia.+(orb|orbs))[^,]+Positiv", new Integer[] { 1036, 1037 }),
        SLOT_EMPTIERS("Slot emptiers", "(Empties|Changes.+into.+\\[EMPTY\\])", "(Svuota|Cambia.+in.+\\[EMPTY\\])"),
        SLOT_FILLERS("Slot fillers", "(Fills\\b|\\[EMPTY\\] orbs into|Changes.+\\[EMPTY\\].+into)", "(Riempie\\b|orbs \\[EMPTY\\] in|Cambia.+\\[EMPTY\\].+in)"),
        DELAYERS("Delayers", "delays", "Ritarda "),
        SINGLE_TARGET_DAMAGE_DEALER("Single-target damage dealer", "deals.+to one enemy", "Infligge.+a un nemico"),
        MULTI_TARGET_DAMAGE_DEALERS("Multi-target damage dealers", "Deals.+to (all|random) enemies", "Infligge.+(a tutti i nemici|a nemici casuali)"),
        MULTI_HIT_DAMAGE_DEALERS("Multi-hit damage dealers", "Deals \\d+ hits", "Infligge \\d+ colpi"),
        FIXED_DAMAGE_DEALERS("Fixed damage dealers", "Deals.+fixed damage", "Infligge.+danno fisso"),
        HEALTH_CUTTERS("Health cutters", "Cuts.+current HP.+enem", "Taglia.+gli HP.+nemic"),
        HP_BASED_DAMAGE_DEALERS("HP-based damage dealers", "specialProportional", "specialProportional"),
        DEFENSE_REDUCERS("Defense reducers", "Reduces.+defense", "Riduce.+difesa"),
        MEAT_PRODUCERS("Meat producers", "into( either)?[\\s,\\[\\]A-Zor]+\\[RCV\\]", "(in orbs( o)?[\\s,\\[\\]A-Zor]+\\[RCV\\]|adiacenti in \\[RCV\\]|riga .+ in \\[RCV\\]|capitano in \\[RCV\\])"),
        MEAT_CONVERTERS("Meat converters", "\\[RCV\\].+into", "\\[RCV\\].+in "),
        DAMAGE_REDUCERS("Damage reducers", "Reduces (any )?damage received", "Riduce (qualsiasi )?il danno ricevuto"),
        DAMAGE_NULLIFIERS("Damage nullifiers", "Reduces (any )?damage received.+100%", "Riduce (qualsiasi )?il danno ricevuto.+100%"),
        BIND_REDUCERS("Bind reducers", "(reduces|removes).+bind.+duration", "(Riduce|Rimuove).+durata.+Bind"),
        DESPAIR_REDUCERS("Despair reducers", "(reduces|removes).+despair.+duration", "(Riduce|Rimuove).+durata.+Despair"),
        SILENCE_REDUCERS("Silence reducers", "(reduces|removes).+silence.+duration", "(Rimuove|Riduce).+durata.+Silence"),
        BLOCK_ORB_REMOVERS("Block orb removers", "(empties.+with \\[BLOCK\\]|changes.+\\[BLOCK\\].+into|including.+\\[BLOCK\\])", "(Svuota.+con.+\\[BLOCK\\]|Cambia.+\\[BLOCK\\].+in|incluso.+\\[BLOCK\\])"),
        BLINDNESS_REMOVERS("Blindness removers", "(reduces|removes).+blindness", "(Rimuove|Riduce).+Blindness"),
        HEALERS("Healers", "Recovers", "Recupera"),
        HEALTH_REDUCERS("Health reducers", "Reduces crew\'s (current )?HP", "(Riduce gli (correnti )?HP della ciurma|Riduce gli HP della ciurma a)"),
        POISONERS("Poisoners", "poisons", "Avvelena "),
        POISON_REMOVERS("Poison removers", "removes.+poison", "Rimuove il veleno"),
        ZOMBIES("Zombies", "Protects from defeat", "Protegge dalla sconfitta"),
        SPECIAL_COOLDOWN_REDUCER("Special cooldown reducer", "reduces special cooldown", "Riduce di.+turn.+special"),
        PARALYSIS_REDUCERS("Paralysis reducers", "(reduces|removes).+Paralysis", "Riduce.+durata.+paralisi");

        private String text;
        private String matcher, matcher_it;
        private Integer[] include = {};

        FL_SPEC_FLAGS(String text, String matcher, String matcher_it) { this.text = text; this.matcher = matcher; this.matcher_it = matcher_it;}
        FL_SPEC_FLAGS(String text, String matcher, String matcher_it, Integer[] include) { this.text = text; this.matcher = matcher; this.matcher_it = matcher_it; this.include = include;}

        public String getText() {
            return this.text;
        }
        public String getMatcherIt() { return this.matcher_it; }
        public String getMatcher() { return this.matcher; }
        public Integer[] getInclude() { return this.include; }

        public boolean textEqualsCaseInsensitive(String what) {
            if (what != null) {
                return this.text.equalsIgnoreCase(what);
            }
            throw new IllegalArgumentException("Argument can't be null");
        }

        public boolean matcherEqualsCaseInsensitive(String what) {
            if (what != null) {
                return this.matcher.equalsIgnoreCase(what);
            }
            throw new IllegalArgumentException("Argument can't be null");
        }

        public static FL_SPEC_FLAGS fromString(String text) {
            if (text != null) {
                for (FL_SPEC_FLAGS b : FL_SPEC_FLAGS.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No constant named " + text);
        }

        public static FL_SPEC_FLAGS fromMatcher(String matcher) {
            if (matcher != null) {
                for (FL_SPEC_FLAGS b : FL_SPEC_FLAGS.values()) {
                    if (matcher.equalsIgnoreCase(b.matcher)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No constant named " + matcher);
        }
    }

    private Dialog loading;
    public EnumSet<FL_TYPE> TypeFlags = EnumSet.allOf(FL_TYPE.class);
    public EnumSet<FL_CLASS> ClassFlags = EnumSet.noneOf(FL_CLASS.class);
    public EnumSet<FL_STARS> StarsFlags = EnumSet.allOf(FL_STARS.class);
    public EnumSet<FL_CAPT_FLAGS> CaptFlags = EnumSet.noneOf(FL_CAPT_FLAGS.class);
    public EnumSet<FL_SPEC_FLAGS> SpecFlags = EnumSet.noneOf(FL_SPEC_FLAGS.class);
    public String FilterText = "";
    private Activity activity = this;

    ImageView sortName, sortType, sortStars, sortAtk, sortHP, sortRCV;
    ListView lview;
    listViewAdapter adapter;
    Map<String, String> directives;
    EditText filterText;
    Dialog dlg_hwnd = null;
    Dialog loadingdlg_hwnd = null;

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

        boolean daynightTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_daynight_theme), false);
        if(daynightTheme) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            LinearLayout mainContent = (LinearLayout)dialog.findViewById(R.id.charInfoMainContent);
            /*if((8<=hour)&&(hour<20))
            {
                //DAYLIGHT
                KenBurnsView charInfoBgImg = (KenBurnsView)dialog.findViewById(R.id.charInfoBgImg);
                charInfoBgImg.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_sunny_sky));
                RandomTransitionGenerator generator = new RandomTransitionGenerator(15000, new LinearInterpolator());
                charInfoBgImg.setTransitionGenerator(generator);
                charInfoBgImg.resume();
            } else {
                //NIGHT
                animateColor(mainContent, anim_charlist, "#333333", "#504e3c");
            }*/
        }

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
            for (int i = 0; i < drops.size(); i++) {
                DropInfo this_drops = drops.get(i);

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
        dialog.show();
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
        WindowManager.LayoutParams attrs = loading.getWindow().getAttributes();
        attrs.dimAmount = 0.5f;
        attrs.verticalMargin = 0.0f;
        attrs.horizontalMargin = 0.0f;
        int width = dpToPx(300);
        int height = dpToPx(400);
        loading.getWindow().setAttributes(attrs);
        loading.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        loading.getWindow().setLayout(width, height);
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

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        final SharedPreferences mPrefs = getSharedPreferences(getString(R.string.pref_name), 0);

        int currTheme;

        Boolean daynightTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_daynight_theme), false);
        if(daynightTheme) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if ((8 <= hour) && (hour < 20)) {
                currTheme =R.style.AppThemeLightDayNight;
                setTheme(currTheme);
            } else {
                currTheme =R.style.AppThemeDarkDayNight;
                setTheme(currTheme);
            }
        } else {
            String theme_str = mPrefs.getString(getResources().getString(R.string.theme_pref), "teal");
            switch (theme_str) {
                case "teal":
                    currTheme = R.style.AppThemeTeal;
                    setTheme(currTheme);
                    break;
                case "red":
                    currTheme = R.style.AppThemeRed;
                    setTheme(currTheme);
                    break;
                case "amber":
                    currTheme = R.style.AppThemeAmber;
                    setTheme(currTheme);
                    break;
                case "light":
                    currTheme = R.style.AppThemeLight;
                    setTheme(currTheme);
                    break;
                case "dark":
                    currTheme = R.style.AppThemeDark;
                    setTheme(currTheme);
                    break;
                default:
                    currTheme = R.style.AppThemeTeal;
                    setTheme(currTheme);
                    break;
            }
        }
        final int theme_ = currTheme;
        boolean firstLocaleSet = mPrefs.getBoolean(locale_pref, false);
        if (!firstLocaleSet) {
            Locale lan = Locale.getDefault();
            String locale = lan.getLanguage().toLowerCase();
            String country = lan.getCountry();
            if (locale.contains("-"))
            {
                String[] els = locale.split("-");
                if(!(els[1].toLowerCase().equals("pt") || els[1].toLowerCase().equals("br")))
                    locale = els[0];
            } else if (country.toLowerCase().equals("pt")||country.toLowerCase().equals("br")) {
                locale += "-" + country.toLowerCase();
            }
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(getString(R.string.pref_language), locale).commit();
            mPrefs.edit().putBoolean(locale_pref, true).apply();
        }

        final String locale = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_language), "");
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

        boolean isDownloadEnabled = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.download_db), true);
        boolean isUpdatesCheckEnabled = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.check_update), true);



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

        ImageButton dmgCalc = (ImageButton) findViewById(R.id.dmg_calculator);
        dmgCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DmgCalcActivity.class);
                Bundle b = new Bundle();
                b.putString("lan", locale.toLowerCase());
                b.putInt("theme", theme_);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        ImageButton settBtn = (ImageButton) findViewById(R.id.settings_btn);
        final int currThemeGlobal = currTheme;
        settBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goingToSettings = true;
                Intent intent = new Intent(context, SettingsActivity.class);
                Bundle b = new Bundle();
                b.putInt("appTheme", currThemeGlobal);
                intent.putExtras(b);
                startActivityForResult(intent, PREFERENCES_ACTIVITY);
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
        if(!mPrefs.getBoolean(getString(R.string.rebuild_db), true)) {
            // run async task to download or load DB
            if (mPrefs.contains(getString(R.string.lastupdate)) && isDbOn()) {
                //There's cached data, so check if it's old
                Date lastupdateDB = getLastUpdate();
                Date datecachedDB = getSerializedDate();
                if (lastupdateDB.after(datecachedDB)) {
                    //if last db update is earlier than cached data then download new data
                    (new DownloadData(isDownloadEnabled, isUpdatesCheckEnabled)).execute();
                    //else load cached data
                } else (new DownloadData(false, isUpdatesCheckEnabled)).execute();
                //if no cached data download new data
            } else (new DownloadData(true, isUpdatesCheckEnabled)).execute();
        } else {
            (new DownloadData(true, true)).execute();
            mPrefs.edit().putBoolean(getString(R.string.rebuild_db), false).commit();
        }

        TextView placeholder = (TextView) findViewById(R.id.placeholder);
        placeholder.setX(getScreenWidth());
        placeholder.setY(getScreenHeight() / 2);
        Boolean displayedTutorial = mPrefs.getBoolean(getString(R.string.tutorial_displayed), false);
        if (!displayedTutorial) {
            new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(placeholder))
                    .setContentTitle("New app overlay available")
                    .setContentText("\"Flying chopper\" is making his debut! And he's flying for real allowing you to use the app even when playing optc!")
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .hideOnTouchOutside()
                    .build();
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putBoolean(getString(R.string.tutorial_displayed), true).apply();
        }

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Boolean displayed_dmgcalc = mPrefs.getBoolean(getString(R.string.tutorial_drawer), false);
                if(!displayed_dmgcalc) {
                    new ShowcaseView.Builder(activity)
                            .setTarget(new ViewTarget(findViewById(R.id.settings_btn)))
                            .setContentTitle("Just one step away!")
                            .setContentText("Open app settings to free the flying beast! You'll need to reassure android that he won't mess things up, and then when you'll close the application you'll see him shining!")
                            .setStyle(R.style.CustomShowcaseTheme2)
                            .hideOnTouchOutside()
                            .build();
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putBoolean(getString(R.string.tutorial_drawer), true).apply();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

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

        /*ImageButton lang_selector = (ImageButton) findViewById(R.id.language_selector);
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
        });*/
    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }


    private int getSideTotalMargin() {
        LinearLayout main = (LinearLayout) findViewById(R.id.maincontent);
        int left = main.getPaddingLeft();
        int right = main.getPaddingRight();
        return left + right;
    }

    protected void onResume() {
        super.onResume();
        stopService(new Intent(context, FlyingChopper.class));
        int width = getScreenWidth();

        LinearLayout list_size = (LinearLayout) findViewById(R.id.list_size_layout);

        if (width > dpToPx(600))
            params.width = width - getSideTotalMargin();
        else params.width = dpToPx(550);
        list_size.setLayoutParams(params);

        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.pref_name), 0);
        String theme_str = mPrefs.getString(getResources().getString(R.string.theme_pref), "teal");
        boolean daynightTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_daynight_theme), false);
        if(daynightTheme) theme_str = "daynight_theme";


        if(daynightTheme) {
            LinearLayout themeBtns = (LinearLayout)findViewById(R.id.themeBtns);
            themeBtns.setVisibility(View.GONE);
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            LinearLayout mainContent = (LinearLayout)findViewById(R.id.maincontent);
            if((8<=hour)&&(hour<20))
            {
                //DAYLIGHT
                KenBurnsView bgImg = (KenBurnsView) findViewById(R.id.mainBgImage);
                bgImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_sunny_sky));
                RandomTransitionGenerator generator = new RandomTransitionGenerator(20000, new AccelerateDecelerateInterpolator());
                bgImg.setTransitionGenerator(generator);
                bgImg.resume();
            } else {
                //NIGHT
                KenBurnsView bgImg = (KenBurnsView) findViewById(R.id.mainBgImage);
                bgImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_night));
                RandomTransitionGenerator generator = new RandomTransitionGenerator(20000, new AccelerateDecelerateInterpolator());
                bgImg.setTransitionGenerator(generator);
                bgImg.resume();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_overlay), false) && !goingToSettings)
            if (isSystemAlertPermissionGranted(this)) {
                Intent i = new Intent(context, FlyingChopper.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(i);
            }
        boolean daynightTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_daynight_theme), false);
        if(daynightTheme)
        {
            KenBurnsView bgImg = (KenBurnsView) findViewById(R.id.mainBgImage);
            bgImg.pause();
        }
        goingToSettings = false;
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
        explistDataHeader.add(getString(R.string.capt_flags_title));
        explistDataHeader.add(getString(R.string.spec_flags_title));

        // Adding child data
        List<String> type_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.type_array)));
        List<String> class_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.class_array)));
        List<String> stars_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.stars_array)));
        List<String> captflags_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.capt_flags_array)));
        List<String> specflags_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spec_flags_array)));

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

        tmp = new LinkedHashMap<>();
        for (String entry : captflags_list) {
            tmp.put(entry, false);
        }
        explistDataChild.put(explistDataHeader.get(3), tmp);

        tmp = new LinkedHashMap<>();
        for (String entry : specflags_list) {
            tmp.put(entry, false);
        }
        explistDataChild.put(explistDataHeader.get(4), tmp);
    }

    private void resetFilters() {
        prepareListData();
        TypeFlags = EnumSet.allOf(FL_TYPE.class);
        ClassFlags = EnumSet.noneOf(FL_CLASS.class);
        StarsFlags = EnumSet.allOf(FL_STARS.class);
        CaptFlags = EnumSet.noneOf(FL_CAPT_FLAGS.class);
        SpecFlags = EnumSet.noneOf(FL_SPEC_FLAGS.class);
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
        if ((ID==574)||(ID==575)) return ("00" + ID.toString());
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
                    Log.v("CACHE", "Done: "+(n+1));
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
                    Integer thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
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
                    Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
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
                    Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
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
                    Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
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

    class CheckUpdates extends AsyncTask<Void, Void, String[]> {
        boolean autoDownload = false;
        public CheckUpdates()
        {

        }
        public CheckUpdates(boolean autoDownload)
        {
            this.autoDownload = autoDownload;
        }
        protected String[] doInBackground(Void... voids) {
            FeedParser parser = new FeedParser();
            String uri = "";
            String version = "";
            String content = "";
            try {
                InputStream releases = FeedParser.downloadUrl("https://github.com/paolo-optc/optc-mobile-db/releases.atom");
                List<FeedParser.Entry> feed = parser.parse(releases);
                for (FeedParser.Entry entry : feed) {
                    version = entry.id.replace("tag:github.com,2008:Repository/70237456/", "");
                    Double vrs = Double.valueOf(version);
                    content = entry.content;
                    if (vrs > APP_VERSION) {
                        uri += "https://github.com/paolo-optc/optc-mobile-db/releases/download/" + version + "/app-release.apk";
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new String[] {uri, version, content};
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(final String[] result) {
             updateUrl = result[0];
            if (!result[0].equals("")) {
                if(autoDownload)
                {
                    checkPermissionAndUpdate();
                } else {
                    /*final Snackbar msg = Snackbar.make(findViewById(android.R.id.content), String.format(getString(R.string.update_msg), result[1]), Snackbar.LENGTH_INDEFINITE);
                    msg.setAction(getString(R.string.update_btn), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkPermissionAndUpdate();
                        }
                    })
                            .setActionTextColor(Color.WHITE).setDuration(8000).show();
                    msg.getView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            msg.dismiss();
                        }
                    });*/
                    AlertDialog updDlg = new AlertDialog.Builder(context).setTitle(String.format(getString(R.string.update_msg), result[1]))
                            .setMessage(Html.fromHtml(result[2], null, new ListTagHandler())).setNegativeButton(getString(R.string.reset_db_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //DO NOTHING
                                }
                            }).setPositiveButton(getString(R.string.reset_db_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checkPermissionAndUpdate();
                                }
                            }).create();
                    updDlg.show();
                }
            } else {
                if (autoDownload)
                {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.no_updates), Snackbar.LENGTH_SHORT).show();
                }
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
                    case 3: //Capt flags
                    {
                        String[] captArray = getResources().getStringArray(R.array.capt_flags_array_link);
                        String flag = captArray[childPosition];
                        CaptFlags.remove(FL_CAPT_FLAGS.fromString(flag));
                        break;
                    }
                    case 4: //Spec flags
                    {
                        String[] specArray = getResources().getStringArray(R.array.spec_flags_array_link);
                        String flag = specArray[childPosition];
                        SpecFlags.remove(FL_SPEC_FLAGS.fromString(flag));
                        break;
                    }
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
                    case 3: //Capt flags
                    {
                        String[] captArray = getResources().getStringArray(R.array.capt_flags_array_link);
                        String flag = captArray[childPosition];
                        CaptFlags.add(FL_CAPT_FLAGS.fromString(flag));
                        break;
                    }
                    case 4: //Spec flags
                    {
                        String[] specArray = getResources().getStringArray(R.array.spec_flags_array_link);
                        String flag = specArray[childPosition];
                        SpecFlags.add(FL_SPEC_FLAGS.fromString(flag));
                        break;
                    }
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
        String locale = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_language), "");
        list = FilterClass.filterWithDB(context, TypeFlags, ClassFlags, StarsFlags, CaptFlags, SpecFlags, FilterText, locale);
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getApkPath(String apkName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), apkName);
        if (!file.mkdirs()) {
            Log.e("err", "Directory not created");
        }
        return file;
    }

    private String updateUrl = "";
    private class DownloadUpdate extends AsyncTask<Void, Integer, File> {

        protected File doInBackground(Void... voids) {
            String downloadPath = updateUrl;
            //String localPath = getExternalFilesDir(null).getAbsolutePath() + "/optcsmartdb_install.apk";
            File file = getApkPath("optcsmartdb_install.apk");
            if (file.exists())
                file.delete();

            try {
                URL url = new URL(downloadPath);
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(file);

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
                e.printStackTrace();
                this.cancel(true);
            }
            return file;
        }

        protected void onPreExecute() {
            if(isExternalStorageWritable())
                showLoading(context);
            else {
                Toast.makeText(context, "Error while accessing device memory", Toast.LENGTH_SHORT).show();
                this.cancel(false);
            }
        }

        protected void onPostExecute(File s) {
            hideLoading();

            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            if(Build.VERSION.SDK_INT >= 24) {
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setDataAndType(FileProvider.getUriForFile(context, getPackageName() + ".provider", s), "application/vnd.android.package-archive");
            } else
                i.setDataAndType(Uri.fromFile(s), "application/vnd.android.package-archive");
            context.startActivity(i);
        }

        protected void onProgressUpdate(Integer... values) {
            setProgressInt(values[0]);
        }
    }

    public void checkPermissionAndUpdate()
    {
        if(isStoragePermissionGranted())
        {
            (new DownloadUpdate()).execute();
        }
    }

    private static final int WRITE_EXT_STORAGE_PERM = 54;
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("perm","Permission is granted");
                return true;
            } else {

                Log.v("perm","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXT_STORAGE_PERM);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("perm","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case WRITE_EXT_STORAGE_PERM:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Log.v("perm", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    (new DownloadUpdate()).execute();
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isSystemAlertPermissionGranted(Context context) {
        final boolean result = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
        return result;
    }

    private void showKeyboard() {
        //SHOW KEYBOARD
        filterText.requestFocus();
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(filterText, 0);
    }

    private static final int SETTINGS_UNKNOWN_SOURCES = 101;
    private static final int PREFERENCES_ACTIVITY = 102;
    private static final int EXTERNAL_OVERLAY_PREFERENCE = 103;
    public static final String LANG_PREF_CHANGED = "result_lang_pref_changed";
    public static final String UPDATE_APP_PREF = "result_update_app_preference";
    public static final String THEMEDAYNIGHT_CHANGED = "result_daynight_theme_changed";
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
            case PREFERENCES_ACTIVITY:
                if(data!=null) {
                    if (data.getBooleanExtra(LANG_PREF_CHANGED, false)) {
                        getSharedPreferences(getString(R.string.pref_name), 0).edit().putBoolean(getString(R.string.rebuild_db), true).commit();
                        crossfade(300);
                    } else if (data.getBooleanExtra(THEMEDAYNIGHT_CHANGED, false)) {
                        crossfade(300);
                    }
                    if (data.getBooleanExtra(UPDATE_APP_PREF, false))
                        (new CheckUpdates(true)).execute();
                }
                break;
        }
    }
}
