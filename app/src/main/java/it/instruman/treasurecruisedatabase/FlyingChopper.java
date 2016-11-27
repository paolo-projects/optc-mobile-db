package it.instruman.treasurecruisedatabase;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.analytics.HitBuilders;

import org.mozilla.javascript.tools.debugger.Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static it.instruman.treasurecruisedatabase.MainActivity.getResIdFromAttribute;
import static it.instruman.treasurecruisedatabase.MainActivity.thumbnail_height;
import static it.instruman.treasurecruisedatabase.MainActivity.thumbnail_width;

/**
 * Created by Paolo on 26/11/2016.
 */

class AnimationLayoutParams extends WindowManager.LayoutParams
{
    public void setX(int x)
    {
        this.x = x;
    }
    public void setY(int y)
    {
        this.y = y;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
    }

    public float getAlpha()
    {
        return alpha;
    }

    public AnimationLayoutParams() {
        super();
    }

    public AnimationLayoutParams(int _type) {
        super(_type);
    }

    public AnimationLayoutParams(int _type, int _flags) {
        super(_type, _flags);
    }

    public AnimationLayoutParams(int _type, int _flags, int _format) {
        super(_type, _flags, _format);
    }

    public AnimationLayoutParams(Parcel in) {
        super(in);
    }

    public AnimationLayoutParams(int w, int h, int _type, int _flags, int _format) {
        super(w, h, _type, _flags, _format);
    }

    public AnimationLayoutParams(int w, int h, int xpos, int ypos, int _type, int _flags, int _format) {
        super(w, h, xpos, ypos, _type, _flags, _format);
    }
}

public class FlyingChopper extends Service {


    private WindowManager windowManager;
    private ImageView chatHead;
    private boolean isClick = false;
    private boolean isCloseVisible = false;
    private Context context = this;
    private Service service = this;
    private View oView = null;
    private ImageView closeBtn;
    private ListView dbList;
    private ArrayList<HashMap> dbListItems, dbOriginalListItems;
    private listViewAdapterOverlay dbListAdapter;
    private ImageView sortName, sortType, sortHP, sortAtk, sortRCV, sortStars;

    AnimationLayoutParams paramsA, paramsB, paramsC;
    int panelState = 0;  // state 0 = close
                    // state 1 = open


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private int getScreenWidth() {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private int getScreenHeight() {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    private void thumbClick()
    {
        if(panelState == 0)
            clickOpen();
        else
            clickClose();
    }

    private void clickClose()
    {
        ObjectAnimator animation = ObjectAnimator.ofFloat(paramsC, "alpha", 0.0f);
        animation.setDuration(100);
        animation.setInterpolator(new LinearInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                try {
                    windowManager.updateViewLayout(oView, paramsC);
                } catch (Exception e) {
                    e.printStackTrace();
                    hasView = false;
                }
            }
        });
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                try {
                    windowManager.removeView(oView);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    hasView = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
        panelState = 0;
        chatHead.setOnTouchListener(thumbClickListener);
    }

    private boolean hasView = false;
    private void clickOpen()
    {
        if(oView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            oView = layoutInflater.inflate(R.layout.activity_main_overlay, null);
        }
        paramsC = new AnimationLayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        boolean left = (paramsA.x < getScreenWidth()/2);
        if(left) {
            paramsC.gravity = Gravity.CENTER_VERTICAL;
            paramsC.x = dpToPx(64);
        } else {
            paramsC.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            paramsC.x = 0;
        }
        paramsC.width = (getScreenWidth()-dpToPx(64));
        paramsC.alpha = 0.0f;
        paramsC.dimAmount = 0.5f;
        if(!hasView) {
            windowManager.addView(oView, paramsC);
            hasView = true;
        }

        ObjectAnimator animation = ObjectAnimator.ofFloat(paramsC, "alpha", 1.0f);
        animation.setDuration(300);
        animation.setInterpolator(new LinearInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                try {
                    windowManager.updateViewLayout(oView, paramsC);
                } catch (Exception e) {
                    e.printStackTrace();
                    hasView = false;
                }
            }
        });
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                try {
                    windowManager.updateViewLayout(oView, paramsC);
                } catch (Exception e) {
                    e.printStackTrace();
                    hasView = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
        panelState = 1;
        chatHead.setOnTouchListener(thumbClickSimple);

        initializeDbDialog();
        populateDbList();
        /*Intent i= new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        getApplication().startActivity(i);*/
    }

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

    private void sortList(View v) {
        ListSortUtilityOverlay utils = new ListSortUtilityOverlay();
        dbListItems = utils.sortList(oView, v, dbListItems);
        dbListAdapter = new listViewAdapterOverlay(this, dbListItems);
        dbList.setAdapter(dbListAdapter);
    }

    ImageButton resetBtn;
    EditText filterText;

    private void initializeDbDialog()
    {
        sortName = (ImageView)oView.findViewById(R.id.sortName_overlay);
        sortName.setTag(R.id.TAG_SORT_ID, R.id.sortName_overlay);

        sortType = (ImageView)oView.findViewById(R.id.sortType_overlay);
        sortType.setTag(R.id.TAG_SORT_ID, R.id.sortType_overlay);

        sortStars = (ImageView)oView.findViewById(R.id.sortStars_overlay);
        sortStars.setTag(R.id.TAG_SORT_ID, R.id.sortStars_overlay);

        sortHP = (ImageView)oView.findViewById(R.id.sortHp_overlay);
        sortHP.setTag(R.id.TAG_SORT_ID, R.id.sortHp_overlay);

        sortAtk = (ImageView)oView.findViewById(R.id.sortAtk_overlay);
        sortAtk.setTag(R.id.TAG_SORT_ID, R.id.sortAtk_overlay);

        sortRCV = (ImageView)oView.findViewById(R.id.sortRcv_overlay);
        sortRCV.setTag(R.id.TAG_SORT_ID, R.id.sortRcv_overlay);

        LinearLayout sortNamel = (LinearLayout) oView.findViewById(R.id.sortName_l_overlay);
        LinearLayout sortTypel = (LinearLayout) oView.findViewById(R.id.sortType_l_overlay);
        LinearLayout sortStarsl = (LinearLayout) oView.findViewById(R.id.sortStars_l_overlay);
        LinearLayout sortAtkl = (LinearLayout) oView.findViewById(R.id.sortAtk_l_overlay);
        LinearLayout sortHPl = (LinearLayout) oView.findViewById(R.id.sortHp_l_overlay);
        LinearLayout sortRCVl = (LinearLayout) oView.findViewById(R.id.sortRcv_l_overlay);

        sortNamel.setOnClickListener(sortNameOnClick);
        sortTypel.setOnClickListener(sortTypeOnClick);
        sortStarsl.setOnClickListener(sortStarsOnClick);
        sortAtkl.setOnClickListener(sortAtkOnClick);
        sortHPl.setOnClickListener(sortHPOnClick);
        sortRCVl.setOnClickListener(sortRCVOnClick);

        final ImageButton filterBtn = (ImageButton) oView.findViewById(R.id.filterBtn_overlay);
        resetBtn = (ImageButton) oView.findViewById(R.id.resetBtn_overlay);
        filterText = (EditText) oView.findViewById(R.id.filterText_overlay);
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
        final LinearLayout F_TEXT = (LinearLayout) oView.findViewById(R.id.filtertext_layout_overlay);
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
                rebuildList();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterText.setText("");
                rebuildList();
            }
        });
        dbList = (ListView)oView.findViewById(R.id.listView1_overlay);
        dbList.setOnItemClickListener(lvOnClick);

        ImageButton goToMain = (ImageButton)oView.findViewById(R.id.goToMain);
        goToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getBaseContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                getApplication().startActivity(i);
                windowManager.removeView(oView);
            }
        });
    }

    private void rebuildList()
    {
        dbListItems = dbOriginalListItems;
        String locale = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_language), "");
        dbListItems = FilterClass.filterWithDB(context, EnumSet.allOf(MainActivity.FL_TYPE.class), EnumSet.noneOf(MainActivity.FL_CLASS.class),
                EnumSet.allOf(MainActivity.FL_STARS.class), EnumSet.noneOf(MainActivity.FL_CAPT_FLAGS.class),
                EnumSet.noneOf(MainActivity.FL_SPEC_FLAGS.class), filterText.getText().toString(), locale);
        updateList();
    }

    private void updateList() {
        dbListAdapter = new listViewAdapterOverlay(this, dbListItems);
        dbList.setAdapter(dbListAdapter);

        sortName.setBackgroundResource(R.drawable.ic_circle_overlay);
        sortName.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle_overlay);

        sortType.setBackgroundResource(R.drawable.ic_circle_overlay);
        sortType.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle_overlay);

        sortStars.setBackgroundResource(R.drawable.ic_circle_overlay);
        sortStars.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle_overlay);

        sortAtk.setBackgroundResource(R.drawable.ic_circle_overlay);
        sortAtk.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle_overlay);

        sortHP.setBackgroundResource(R.drawable.ic_circle_overlay);
        sortHP.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle_overlay);

        sortRCV.setBackgroundResource(R.drawable.ic_circle_overlay);
        sortRCV.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle_overlay);
    }

    private void populateDbList() {
        if(oView!=null) {
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            dbOriginalListItems = dbListItems = DBHelper.getAllCharacters(db);
            db.close();
            updateList();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);

        chatHead.setImageResource(R.mipmap.ic_flyingchopper);
        chatHead.setScaleType(ImageView.ScaleType.FIT_CENTER);

        paramsA = new AnimationLayoutParams(
                /*WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,*/
                dpToPx(60),
                dpToPx(60),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsA.gravity = Gravity.TOP | Gravity.START;
        paramsA.x = 0;
        paramsA.y = 100;

        windowManager.addView(chatHead, paramsA);

        closeBtn = new ImageView(context);

        closeBtn.setImageResource(R.mipmap.ic_close);
        closeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);

        paramsB = new AnimationLayoutParams(
                dpToPx(40),
                dpToPx(40),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT);

        paramsB.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        paramsB.dimAmount = 0.5f;
        paramsB.setY(dpToPx(24));

        try {
            chatHead.setOnTouchListener(thumbClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }

    View.OnTouchListener thumbClickSimple = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                thumbClick();
            }
            return false;
        }
    };

    View.OnTouchListener thumbClickListener = new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    // Get current time in nano seconds.

                    initialX = paramsA.x;
                    initialY = paramsA.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    isClick = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if(isClick)
                        thumbClick();
                    else if (isCloseVisible)
                    {
                        boolean toBreak = false;
                        int x = (int)event.getRawX();
                        int y = (int)event.getRawY();
                        int width = getScreenWidth();
                        int height = getScreenHeight();
                        if ((x > ((width / 2) - dpToPx(22))) && (x < ((width / 2) + dpToPx(22))) && (y > (height - dpToPx(64))) && (y < (height - dpToPx(24))))
                        {
                            service.stopSelf();
                            toBreak = true;
                        }
                        animateFade(closeBtn, paramsB, 0.0f, 300, true);
                        isCloseVisible = false;
                        if(toBreak) return false;
                    }
                    if(!isClick) {
                        int x = (paramsA.x > ((getScreenWidth() - dpToPx(60)) / 2)) ? getScreenWidth() - dpToPx(60) : 0;
                        ObjectAnimator animation = ObjectAnimator.ofInt(paramsA, "x", x);
                        animation.setDuration(600);
                        animation.setInterpolator(new BounceInterpolator());
                        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                windowManager.updateViewLayout(chatHead, paramsA);
                            }
                        });
                        animation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                windowManager.updateViewLayout(chatHead, paramsA);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        animation.start();
                    }
                    isClick = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if((Math.abs(event.getRawX()-initialTouchX)>dpToPx(15)) || (Math.abs(event.getRawY()-initialTouchY)>dpToPx(15))) {
                        paramsA.x = initialX + (int) (event.getRawX() - initialTouchX);
                        paramsA.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, paramsA);
                        isClick = false;
                        if (!isCloseVisible) {
                            closeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    service.stopSelf();
                                }
                            });
                            paramsB.alpha = 0.0f;
                            windowManager.addView(closeBtn, paramsB);
                            animateFade(closeBtn, paramsB, 1.0f, 300, false);
                            isCloseVisible = true;
                        }
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        int width = getScreenWidth();
                        int height = getScreenHeight();
                        if ((x > ((width / 2) - dpToPx(22))) && (x < ((width / 2) + dpToPx(22))) && (y > (height - dpToPx(64))) && (y < (height - dpToPx(24)))) {
                            if (isSmall) {
                                animateY(closeBtn, paramsB, dpToPx(32), 100);
                                isSmall = false;
                            }
                        } else {
                            if (!isSmall) {
                                animateY(closeBtn, paramsB, dpToPx(24), 100);
                                isSmall = true;
                            }
                        }
                    }
                    break;
            }
            return false;
        }
    };
    private boolean isSmall = true;
    private void animateFade(final View v, final AnimationLayoutParams params, float to, int ms, final boolean remove)
    {
        ObjectAnimator animation = ObjectAnimator.ofFloat(params, "alpha", to);
        animation.setDuration(ms);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                windowManager.updateViewLayout(v, params);
            }
        });
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(remove)
                    windowManager.removeView(v);
                else
                    windowManager.updateViewLayout(v, params);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
    }
    private void animateY(final View v, final AnimationLayoutParams params, int toY, int ms)
    {
        PropertyValuesHolder height = PropertyValuesHolder.ofInt("y", toY);
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(params,height);
        animation.setDuration(ms);
        animation.setInterpolator(new BounceInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                windowManager.updateViewLayout(v, params);
            }
        });
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                windowManager.updateViewLayout(v, params);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
    }

    public String convertID(Integer ID) {
        if ((ID==574)||(ID==575)) return ("00" + ID.toString());
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

    ListView.OnItemClickListener lvOnClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            launchDialog(dbListAdapter.getIDfromPosition(position));
        }
    };

    private void launchDialog(int id) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View dialog = layoutInflater.inflate(R.layout.dialog_main_overlay, null);

        final TabHost tabs = (TabHost) dialog.findViewById(R.id.tabs_host);
        tabs.setup();
        tabs.setBackgroundColor(getResources().getColor(R.color.char_info_bg_teal));

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
            tv.setTextColor(getResources().getColor(R.color.char_info_txt_teal));
        }

        TextView title = (TextView) dialog.findViewById(R.id.titleText);

        // set the custom dialog components - text, image and button
        ImageView image = (ImageView) dialog.findViewById(R.id.char_img_big);

        Glide
                .with(context)
                .load("http://onepiece-treasurecruise.com/wp-content/uploads/c" + convertID(id) + ".png")
                .into(image);


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
        title.setTextColor(getResources().getColor(R.color.char_info_txt_teal));

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
                special_description.setTextColor(getResources().getColor(R.color.char_info_txt_teal));
                specials_container.addView(special_description);

                LinearLayout coold_layout = new LinearLayout(context);
                coold_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView coold_title = new TextView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, dpToPx(4), 0);
                coold_title.setLayoutParams(params);
                coold_title.setText(getString(R.string.speccooldown));
                coold_title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                coold_title.setBackgroundColor(getResources().getColor(R.color.char_info_header_bg_teal));
                coold_title.setTextColor(getResources().getColor(R.color.char_info_header_txt_teal));
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
                coold_content.setTextColor(getResources().getColor(R.color.char_info_txt_teal));

                coold_layout.addView(coold_content);

                String specialnotes = special.getSpecialNotes();
                if (!specialnotes.equals("")) {
                    specialnotes = replaceBr(specialnotes);
                    TextView special_notes = new TextView(context);
                    special_notes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    special_notes.setText(getString(R.string.notes_text) + specialnotes);
                    special_notes.setTextColor(getResources().getColor(R.color.char_info_header_txt_teal));
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
                evo_text.setBackgroundResource(R.drawable.ic_left_arrow_overlay);
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
                drop_name.setTextColor(getResources().getColor(R.color.char_info_txt_teal));
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
                drop_det.setTextColor(getResources().getColor(R.color.char_info_txt_teal));
                drop_det.setGravity(Gravity.CENTER);

                drops_row.addView(drop_det);

                TextView drop_notes = new TextView(context);
                drop_notes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                drop_notes.setGravity(Gravity.CENTER);
                drop_notes.setTypeface(drop_notes.getTypeface(), Typeface.BOLD);
                drop_det.setTextColor(getResources().getColor(R.color.char_info_header_txt_teal));

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
                windowManager.removeView(dialog);
            }
        });

        WindowManager.LayoutParams paramsD = new AnimationLayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsD.width = (int)(getScreenWidth() * 0.97);
        paramsD.height = (int)(getScreenHeight() * 0.85);
        paramsD.gravity = Gravity.CENTER;
        windowManager.addView(dialog, paramsD);
    }
}