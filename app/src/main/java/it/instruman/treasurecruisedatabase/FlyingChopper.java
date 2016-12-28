package it.instruman.treasurecruisedatabase;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

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
    private ImageView mainIcon, listIcon, dmgcalcIcon, calculatorIcon, ellipsisIcon;
    private boolean isClick = false;
    private boolean isCloseVisible = false;
    private Context context = this;
    private Service service = this;
    private View listInterfaceView = null;
    private View dmgCalcInterfaceView = null;
    private View calcInterfaceView = null;
    private ImageView closeBtn;
    private ListView dbList;
    private ArrayList<HashMap> dbListItems, dbOriginalListItems;
    private listViewAdapterOverlay dbListAdapter;
    private ImageView sortName, sortType, sortHP, sortAtk, sortRCV, sortStars;

    AnimationLayoutParams paramsMainIcon, paramsCloseBtn, paramsListInterface, paramsListIcon, paramsDmgCalcInterface, paramsDmgCalcIcon, paramsCalcIcon, paramsCalcInterface, paramsEllipsisIcon;
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
    private int getSideTotalMargin() {
        LinearLayout main = (LinearLayout) listInterfaceView.findViewById(R.id.maincontent_overlay);
        int left = main.getPaddingLeft();
        int right = main.getPaddingRight();
        return left + right;
    }

    private void hideKeyboard() {
        //  UNFOCUS TEXTBOX AND HIDE KEYBOARD
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(listInterfaceView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard() {
        //SHOW KEYBOARD
        filterText.requestFocus();
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(filterText, 0);
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
        if(panelSelection==SELECT_LIST)
            animateFade(listInterfaceView, paramsListInterface, 0.0f, 100, true, new LinearInterpolator());
        if(panelSelection==SELECT_DMGCALC)
            animateFade(dmgCalcInterfaceView, paramsDmgCalcInterface, 0.0f, 100, true, new LinearInterpolator());
        if(panelSelection==SELECT_CALCULATOR)
            animateFade(calcInterfaceView, paramsCalcInterface, 0.0f, 100, true, new LinearInterpolator());
        int mainY = paramsMainIcon.y;
        View[] toShrink = {listIcon, dmgcalcIcon, calculatorIcon};
        AnimationLayoutParams[] paramsToShrink = {paramsListIcon, paramsDmgCalcIcon, paramsCalcIcon};
        animateSize(toShrink, paramsToShrink, mainIcon, paramsMainIcon);
        ObjectAnimator listAnim = getYAnimator(paramsListIcon, listIcon, mainY, 300, new LinearInterpolator(), true, null);
        ObjectAnimator dmgcalcAnim = getYAnimator(paramsDmgCalcIcon, dmgcalcIcon, mainY, 300, new LinearInterpolator(), true, null);
        ObjectAnimator calculatorAnim = getYAnimator(paramsCalcIcon, calculatorIcon, mainY, 300, new LinearInterpolator(), true, null);
        ObjectAnimator ellipsisAnim = getYAnimator(paramsEllipsisIcon, ellipsisIcon, mainY, 300, new LinearInterpolator(), true, null);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(listAnim, dmgcalcAnim, calculatorAnim, ellipsisAnim);
        animSet.start();
        panelState = 0;
        mainIcon.setOnTouchListener(mainiconClickAndMove);
    }

    private static final int SELECT_LIST = 1;
    private static final int SELECT_DMGCALC = 2;
    private static final int SELECT_CALCULATOR = 3;
    private int panelSelection = SELECT_LIST;
    private void clickOpen()
    {
        switch(panelSelection)
        {
            default:
            case SELECT_LIST: {
                listOpen();
                animateIconsDropping();
                View[] toShrink = {mainIcon, dmgcalcIcon, calculatorIcon};
                AnimationLayoutParams[] paramsToShrink = {paramsMainIcon, paramsDmgCalcIcon, paramsCalcIcon};
                animateSize(toShrink, paramsToShrink, listIcon, paramsListIcon);
                break;
            }
            case SELECT_DMGCALC: {
                dmgcalcOpen();
                animateIconsDropping();
                View[] toShrink = {mainIcon, listIcon, calculatorIcon};
                AnimationLayoutParams[] paramsToShrink = {paramsMainIcon, paramsListIcon, paramsCalcIcon};
                animateSize(toShrink, paramsToShrink, dmgcalcIcon, paramsDmgCalcIcon);
                break;
            }
            case SELECT_CALCULATOR: {
                calculatorOpen();
                animateIconsDropping();
                View[] toShrink = {mainIcon, listIcon, dmgcalcIcon};
                AnimationLayoutParams[] paramsToShrink = {paramsMainIcon, paramsListIcon, paramsDmgCalcIcon};
                animateSize(toShrink, paramsToShrink, calculatorIcon, paramsCalcIcon);
                break;
            }
        }
    }

    private ObjectAnimator getYAnimator(final AnimationLayoutParams params, final View view, int endY, long duration, Interpolator interpolator, final boolean toRemove, @Nullable final Callable<Void> runAtEnd)
    {
        ObjectAnimator animator = ObjectAnimator.ofInt(params, "y",endY);
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                windowManager.updateViewLayout(view, params);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                try {
                    if (runAtEnd != null) {
                        try {
                            runAtEnd.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(!toRemove)
                        windowManager.updateViewLayout(view, params);
                    else
                        windowManager.removeView(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return animator;
    }

    private void dmgcalcOpen()
    {
        if((listInterfaceView != null) && (listInterfaceView.getWindowToken() != null))
            windowManager.removeView(listInterfaceView);
        if((dmgCalcInterfaceView != null) && (dmgCalcInterfaceView.getWindowToken() != null))
            windowManager.removeView(dmgCalcInterfaceView);
        if((calcInterfaceView != null) && (calcInterfaceView.getWindowToken() != null))
            windowManager.removeView(calcInterfaceView);
        if(dmgCalcInterfaceView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            dmgCalcInterfaceView = layoutInflater.inflate(R.layout.activity_dmg_calc_overlay, null);
            initializeDmgCalcDialog();
        }
        paramsDmgCalcInterface = new AnimationLayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                //WindowManager.LayoutParams.FLAG_DIM_BEHIND |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        boolean left = (paramsMainIcon.x < getScreenWidth()/2);
        if(left) {
            paramsDmgCalcInterface.gravity = Gravity.CENTER_VERTICAL;
            paramsDmgCalcInterface.x = dpToPx(INTERFACE_SPACING);
        } else {
            paramsDmgCalcInterface.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            paramsDmgCalcInterface.x = 0;
        }
        paramsDmgCalcInterface.width = (getScreenWidth()-dpToPx(INTERFACE_SPACING));
        paramsDmgCalcInterface.alpha = 0.0f;

        if(dmgCalcInterfaceView.getWindowToken() == null)
            windowManager.addView(dmgCalcInterfaceView, paramsDmgCalcInterface);

        animateFade(dmgCalcInterfaceView, paramsDmgCalcInterface, 1.0f, 300, false, new LinearInterpolator());

        panelState = 1;
        mainIcon.setOnTouchListener(mainiconClickSimple);
    }

    private void calculatorOpen()
    {
        if((listInterfaceView != null) && (listInterfaceView.getWindowToken() != null))
            windowManager.removeView(listInterfaceView);
        if((dmgCalcInterfaceView != null) && (dmgCalcInterfaceView.getWindowToken() != null))
            windowManager.removeView(dmgCalcInterfaceView);
        if((calcInterfaceView != null) && (calcInterfaceView.getWindowToken() != null))
            windowManager.removeView(calcInterfaceView);
        if(calcInterfaceView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            calcInterfaceView = layoutInflater.inflate(R.layout.dialog_calculator, null);
            initializeCalcDialog();
        }
        paramsCalcInterface = new AnimationLayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                //WindowManager.LayoutParams.FLAG_DIM_BEHIND |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        boolean left = (paramsMainIcon.x < getScreenWidth()/2);
        if(left) {
            paramsCalcInterface.gravity = Gravity.CENTER_VERTICAL;
            paramsCalcInterface.x = dpToPx(INTERFACE_SPACING);
        } else {
            paramsCalcInterface.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            paramsCalcInterface.x = 0;
        }
        paramsCalcInterface.width = (getScreenWidth()-dpToPx(INTERFACE_SPACING));
        paramsCalcInterface.alpha = 0.0f;

        if(calcInterfaceView.getWindowToken() == null)
            windowManager.addView(calcInterfaceView, paramsCalcInterface);

        animateFade(calcInterfaceView, paramsCalcInterface, 1.0f, 300, false, new LinearInterpolator());

        panelState = 1;
        mainIcon.setOnTouchListener(mainiconClickSimple);
    }

    private void initializeCalcDialog()
    {
        Button btn10cut = (Button)calcInterfaceView.findViewById(R.id.btn10cut);
        Button btn20cut = (Button)calcInterfaceView.findViewById(R.id.btn20cut);
        Button btn30cut = (Button)calcInterfaceView.findViewById(R.id.btn30cut);
        Button btn40cut = (Button)calcInterfaceView.findViewById(R.id.btn40cut);

        Button btn1 = (Button)calcInterfaceView.findViewById(R.id.btn1);
        Button btn2 = (Button)calcInterfaceView.findViewById(R.id.btn2);
        Button btn3 = (Button)calcInterfaceView.findViewById(R.id.btn3);
        Button btn4 = (Button)calcInterfaceView.findViewById(R.id.btn4);
        Button btn5 = (Button)calcInterfaceView.findViewById(R.id.btn5);
        Button btn6 = (Button)calcInterfaceView.findViewById(R.id.btn6);
        Button btn7 = (Button)calcInterfaceView.findViewById(R.id.btn7);
        Button btn8 = (Button)calcInterfaceView.findViewById(R.id.btn8);
        Button btn9 = (Button)calcInterfaceView.findViewById(R.id.btn9);
        Button btn0 = (Button)calcInterfaceView.findViewById(R.id.btn0);
        Button btnPoint = (Button)calcInterfaceView.findViewById(R.id.btnPoint);
        Button btnEqual = (Button)calcInterfaceView.findViewById(R.id.btnEqual);

        Button delLastBtn = (Button)calcInterfaceView.findViewById(R.id.btnDeleteLast);
        Button btnDel = (Button)calcInterfaceView.findViewById(R.id.btnDel);
        Button btnC = (Button)calcInterfaceView.findViewById(R.id.btnC);

        Button btnPlus = (Button)calcInterfaceView.findViewById(R.id.btnPlus);
        Button btnMinus = (Button)calcInterfaceView.findViewById(R.id.btnMinus);
        Button btnTimes = (Button)calcInterfaceView.findViewById(R.id.btnTimes);
        Button btnDivision = (Button)calcInterfaceView.findViewById(R.id.btnDivision);

        Button btnX0_5 = (Button)calcInterfaceView.findViewById(R.id.btnX0_5);
        Button btnX1_5 = (Button)calcInterfaceView.findViewById(R.id.btnX1_5);
        Button btnX2 = (Button)calcInterfaceView.findViewById(R.id.btnX2);
        Button btnX2_25 = (Button)calcInterfaceView.findViewById(R.id.btnX2_25);
        Button btnX2_5 = (Button)calcInterfaceView.findViewById(R.id.btnX2_5);
        Button btnX2_75 = (Button)calcInterfaceView.findViewById(R.id.btnX2_75);
        Button btnX3 = (Button)calcInterfaceView.findViewById(R.id.btnX3);

        btn1.setOnClickListener(numberOnClickListener);
        btn2.setOnClickListener(numberOnClickListener);
        btn3.setOnClickListener(numberOnClickListener);
        btn4.setOnClickListener(numberOnClickListener);
        btn5.setOnClickListener(numberOnClickListener);
        btn6.setOnClickListener(numberOnClickListener);
        btn7.setOnClickListener(numberOnClickListener);
        btn8.setOnClickListener(numberOnClickListener);
        btn9.setOnClickListener(numberOnClickListener);
        btn0.setOnClickListener(numberOnClickListener);
        btnPoint.setOnClickListener(numberOnClickListener);

        delLastBtn.setOnClickListener(deleteLastOnClickListener);
        btnDel.setOnClickListener(btnDelOnClickListener);

        btnPlus.setOnClickListener(operationOnClickListener);
        btnMinus.setOnClickListener(operationOnClickListener);
        btnTimes.setOnClickListener(operationOnClickListener);
        btnDivision.setOnClickListener(operationOnClickListener);

        btnEqual.setOnClickListener(equalsOnClickListener);
        btnC.setOnClickListener(clearOnClickListener);

        btn10cut.setOnClickListener(cutOnClickListener);
        btn20cut.setOnClickListener(cutOnClickListener);
        btn30cut.setOnClickListener(cutOnClickListener);
        btn40cut.setOnClickListener(cutOnClickListener);

        btnX0_5.setOnClickListener(multiplyOnClickListener);
        btnX1_5.setOnClickListener(multiplyOnClickListener);
        btnX2.setOnClickListener(multiplyOnClickListener);
        btnX2_25.setOnClickListener(multiplyOnClickListener);
        btnX2_5.setOnClickListener(multiplyOnClickListener);
        btnX2_75.setOnClickListener(multiplyOnClickListener);
        btnX3.setOnClickListener(multiplyOnClickListener);

                mainValue = (EditText)calcInterfaceView.findViewById(R.id.calcValue);
    }

    EditText mainValue;

    private View.OnClickListener cutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String main = mainValue.getText().toString();
            if(!main.equals("0")) {
                Double actual = getDouble(main);
                Integer cut = Integer.parseInt((String)view.getTag());
                Double factor = 1.0-(cut.doubleValue()/100);
                mainValue.setText(formatNumber(actual*factor));
                toClear=true;
            }
        }
    };

    private View.OnClickListener multiplyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String main = mainValue.getText().toString();
            if(!main.equals("0")) {
                Double actual = getDouble(main);
                Double factor = Double.parseDouble((String)view.getTag());
                mainValue.setText(formatNumber(actual*factor));
                toClear=true;
            }
        }
    };

    private View.OnClickListener numberOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(toClear) {
                mainValue.setText("0");
                toClear = false;
            }
            if(mainValue.getText().toString().equals("0"))
                mainValue.setText("");
            try {
                String number = (String)view.getTag();
                if(number.equals(".") && mainValue.getText().toString().equals(""))
                    mainValue.setText("0.");
                else {
                    Editable actual = mainValue.getText();
                    actual.append(number);
                    mainValue.setText(actual);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener deleteLastOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String actual = mainValue.getText().toString();
            if(!actual.equals("0")) {
                if(actual.length()==1)
                    actual = "0";
                else
                    actual = actual.substring(0, actual.length() - 1);
                mainValue.setText(actual);
            }
        }
    };

    private View.OnClickListener btnDelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainValue.setText("0");
        }
    };

    private View.OnClickListener clearOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainValue.setText("0");
            VALUE_STACK = "";
            OPERATION_STACK = -1;
            toClear = false;
        }
    };

    private static final int OPERATION_SUM = 0;
    private static final int OPERATION_SUBTRACTION = 1;
    private static final int OPERATION_MULTIPLICATION = 2;
    private static final int OPERATION_DIVISION = 3;
    private int OPERATION_STACK = -1;
    private boolean toClear = false;
    private String VALUE_STACK = "";

    private Double getDouble()
    {
        String actual = mainValue.getText().toString();
        if (actual.substring(actual.length()-1, actual.length()).equals("."))
            actual = actual.substring(0, actual.length()-1);
        try {
            return Double.parseDouble(actual);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private Double getDouble(String actual)
    {
        if (actual.length()>0 && actual.substring(actual.length()-1, actual.length()).equals("."))
            actual = actual.substring(0, actual.length()-1);
        try {
            return Double.parseDouble(actual);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private View.OnClickListener operationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(VALUE_STACK.equals(""))
                VALUE_STACK = mainValue.getText().toString();
            Double VALUE_STACK_D = getDouble(VALUE_STACK);
            if(OPERATION_STACK != -1 && !toClear) {
                switch(OPERATION_STACK) {
                    case OPERATION_SUM:
                        VALUE_STACK_D += getDouble();
                        break;
                    case OPERATION_SUBTRACTION:
                        VALUE_STACK_D -= getDouble();
                        break;
                    case OPERATION_MULTIPLICATION:
                        VALUE_STACK_D *= getDouble();
                        break;
                    case OPERATION_DIVISION:
                        VALUE_STACK_D /= getDouble();
                        break;
                }
                VALUE_STACK = formatNumber(VALUE_STACK_D);
                mainValue.setText(VALUE_STACK);
                OPERATION_STACK = -1;
            }
            String tag = (String) view.getTag();
            switch (tag) {
                case "+":
                    OPERATION_STACK = OPERATION_SUM;
                    break;
                case "-":
                    OPERATION_STACK = OPERATION_SUBTRACTION;
                    break;
                case "*":
                    OPERATION_STACK = OPERATION_MULTIPLICATION;
                    break;
                case "/":
                    OPERATION_STACK = OPERATION_DIVISION;
                    break;
            }
            toClear = true;
        }
    };

    private String formatNumber(Double d)
    {
        Integer i = d.intValue();
        Double f = Math.abs(d - i);
        if(f>0) {
            String r = String.format(Locale.ENGLISH, "%.3f", d);
            boolean isLastZero = r.substring(r.length()-1, r.length()).equals("0");
            while(isLastZero) {
                r = r.substring(0, r.length()-1);
                isLastZero = r.substring(r.length()-1, r.length()).equals("0");
            }
            return r;
        } else
            return String.format(Locale.ENGLISH, "%.0f", d);
    }

    private View.OnClickListener equalsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Double VALUE_STACK_D = getDouble(VALUE_STACK);
            if(OPERATION_STACK != -1 && !toClear) {
                switch(OPERATION_STACK) {
                    case OPERATION_SUM:
                        VALUE_STACK_D += getDouble();
                        break;
                    case OPERATION_SUBTRACTION:
                        VALUE_STACK_D -= getDouble();
                        break;
                    case OPERATION_MULTIPLICATION:
                        VALUE_STACK_D *= getDouble();
                        break;
                    case OPERATION_DIVISION:
                        VALUE_STACK_D /= getDouble();
                        break;
                }
                VALUE_STACK = formatNumber(VALUE_STACK_D);
                mainValue.setText(VALUE_STACK);
                OPERATION_STACK = -1;
                VALUE_STACK = "";
            }
            toClear = true;
        }
    };

    private String DMGCALC_URL = "http://optc-db.github.io/damage/";
    private void initializeDmgCalcDialog() {
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

        switch(locale.toLowerCase())
        {
            case "it":
                DMGCALC_URL = "http://www.one-piece-treasure-cruise-italia.org/damage/";
                break;
            case "es":
                DMGCALC_URL = "https://optc-sp.github.io/damage/";
                break;
            case "":
            default:
                DMGCALC_URL = "https://optc-db.github.io/damage/";
                break;
        }

        final WebView webView = (WebView) dmgCalcInterfaceView.findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                return handleUri(uri);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return handleUri(uri);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            private boolean handleUri(final Uri uri) {
                final String host = uri.getHost();
                final String scheme = uri.getScheme();
                final Uri dmg_url = Uri.parse(DMGCALC_URL);
                // Based on some condition you need to determine if you are going to load the url
                // in your web view itself or in a browser.
                // You can use `host` or `scheme` or any part of the `uri` to decide.
                if (dmg_url.getHost().equals(host)) {
                    // Returning false means that you are going to load this url in the webView itself
                    return false;
                } else {
                    // Returning true means that you need to handle what to do with the url
                    // e.g. open web page in a Browser
                    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        if(Build.VERSION.SDK_INT >= 21)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
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
        CookieManager.allowFileSchemeCookies();
        CookieManager.getInstance().setAcceptCookie(true);

        ImageButton navigateBackBtn = (ImageButton)dmgCalcInterfaceView.findViewById(R.id.dmgcalc_navigateback);
        ImageButton navigateForwardBtn = (ImageButton)dmgCalcInterfaceView.findViewById(R.id.dmgcalc_navigateforward);
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

    private void animateIconsDropping()
    {
        int mainX = paramsMainIcon.x;
        int mainY = paramsMainIcon.y;
        Log.d("DBG", "x="+mainX+" y="+mainY);

        paramsListIcon.x = mainX;
        paramsListIcon.y = mainY;

        paramsDmgCalcIcon.x = mainX;
        paramsDmgCalcIcon.y = mainY;

        paramsCalcIcon.x = mainX;
        paramsCalcIcon.y = mainY;

        paramsEllipsisIcon.x = mainX;
        paramsEllipsisIcon.y = mainY;

        AnimatorSet animatorSet = new AnimatorSet();

        if(mainY<((getScreenHeight()-dpToPx(SIZE_NORMAL))/2)) {
            //ANIMATE FALLING DOWN
            ObjectAnimator listAnimator = getYAnimator(paramsListIcon, listIcon, mainY+dpToPx(SIZE_NORMAL), 800, new BounceInterpolator(), false, null);
            ObjectAnimator dmgcalcAnimator = getYAnimator(paramsDmgCalcIcon, dmgcalcIcon, mainY+dpToPx(SIZE_NORMAL*2), 800, new BounceInterpolator(), false, null);
            ObjectAnimator calculatorAnimator = getYAnimator(paramsCalcIcon, calculatorIcon, mainY+dpToPx(SIZE_NORMAL*3), 800, new BounceInterpolator(), false, null);
            ObjectAnimator ellipsisAnimator = getYAnimator(paramsEllipsisIcon, ellipsisIcon, mainY+dpToPx(SIZE_NORMAL*4), 800, new BounceInterpolator(), false, null);
            animatorSet.playTogether(listAnimator, dmgcalcAnimator, calculatorAnimator, ellipsisAnimator);
        } else {
            //ANIMATE JUMPING UP
            ObjectAnimator listAnimator = getYAnimator(paramsListIcon, listIcon, mainY-dpToPx(SIZE_NORMAL), 800, new BounceInterpolator(), false, null);
            ObjectAnimator dmgcalcAnimator = getYAnimator(paramsDmgCalcIcon, dmgcalcIcon, mainY-dpToPx(SIZE_NORMAL*2), 800, new BounceInterpolator(), false, null);
            ObjectAnimator calculatorAnimator = getYAnimator(paramsCalcIcon, calculatorIcon, mainY-dpToPx(SIZE_NORMAL*3), 800, new BounceInterpolator(), false, null);
            ObjectAnimator ellipsisAnimator = getYAnimator(paramsEllipsisIcon, ellipsisIcon, mainY-dpToPx(SIZE_NORMAL*4), 800, new BounceInterpolator(), false, null);
            animatorSet.playTogether(listAnimator, dmgcalcAnimator, calculatorAnimator, ellipsisAnimator);
        }
        try {
            windowManager.addView(listIcon, paramsListIcon);
            windowManager.addView(dmgcalcIcon, paramsDmgCalcIcon);
            windowManager.addView(calculatorIcon, paramsCalcIcon);
            windowManager.addView(ellipsisIcon, paramsEllipsisIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        animatorSet.start();
    }

    private void listOpen()
    {
        if((listInterfaceView != null) && (listInterfaceView.getWindowToken() != null))
            windowManager.removeView(listInterfaceView);
        if((dmgCalcInterfaceView != null) && (dmgCalcInterfaceView.getWindowToken() != null))
            windowManager.removeView(dmgCalcInterfaceView);
        if((calcInterfaceView != null) && (calcInterfaceView.getWindowToken() != null))
            windowManager.removeView(calcInterfaceView);
        if(listInterfaceView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            listInterfaceView = layoutInflater.inflate(R.layout.activity_main_overlay, null);
        }
        paramsListInterface = new AnimationLayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                //WindowManager.LayoutParams.FLAG_DIM_BEHIND |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        boolean left = (paramsMainIcon.x < getScreenWidth()/2);
        if(left) {
            paramsListInterface.gravity = Gravity.CENTER_VERTICAL;
            paramsListInterface.x = dpToPx(INTERFACE_SPACING);
        } else {
            paramsListInterface.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            paramsListInterface.x = 0;
        }
        paramsListInterface.width = (getScreenWidth()-dpToPx(INTERFACE_SPACING));
        paramsListInterface.alpha = 0.0f;

        if(listInterfaceView.getWindowToken() == null)
            windowManager.addView(listInterfaceView, paramsListInterface);

        animateFade(listInterfaceView, paramsListInterface, 1.0f, 300, false, new LinearInterpolator());

        panelState = 1;
        mainIcon.setOnTouchListener(mainiconClickSimple);

        initializeDbDialog();
        populateDbList();

        LinearLayout list_size = (LinearLayout) listInterfaceView.findViewById(R.id.list_size_layout_overlay);

        if (getScreenWidth() > dpToPx(600))
            params.width = getScreenWidth() - getSideTotalMargin();
        else params.width = dpToPx(550);
        list_size.setLayoutParams(params);
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
        dbListItems = utils.sortList(listInterfaceView, v, dbListItems);
        dbListAdapter = new listViewAdapterOverlay(this, dbListItems);
        dbList.setAdapter(dbListAdapter);
        hideKeyboard();
    }

    ImageButton resetBtn;
    EditText filterText;

    private void initializeDbDialog()
    {
        sortName = (ImageView) listInterfaceView.findViewById(R.id.sortName_overlay);
        sortName.setTag(R.id.TAG_SORT_ID, R.id.sortName_overlay);

        sortType = (ImageView) listInterfaceView.findViewById(R.id.sortType_overlay);
        sortType.setTag(R.id.TAG_SORT_ID, R.id.sortType_overlay);

        sortStars = (ImageView) listInterfaceView.findViewById(R.id.sortStars_overlay);
        sortStars.setTag(R.id.TAG_SORT_ID, R.id.sortStars_overlay);

        sortHP = (ImageView) listInterfaceView.findViewById(R.id.sortHp_overlay);
        sortHP.setTag(R.id.TAG_SORT_ID, R.id.sortHp_overlay);

        sortAtk = (ImageView) listInterfaceView.findViewById(R.id.sortAtk_overlay);
        sortAtk.setTag(R.id.TAG_SORT_ID, R.id.sortAtk_overlay);

        sortRCV = (ImageView) listInterfaceView.findViewById(R.id.sortRcv_overlay);
        sortRCV.setTag(R.id.TAG_SORT_ID, R.id.sortRcv_overlay);

        LinearLayout sortNamel = (LinearLayout) listInterfaceView.findViewById(R.id.sortName_l_overlay);
        LinearLayout sortTypel = (LinearLayout) listInterfaceView.findViewById(R.id.sortType_l_overlay);
        LinearLayout sortStarsl = (LinearLayout) listInterfaceView.findViewById(R.id.sortStars_l_overlay);
        LinearLayout sortAtkl = (LinearLayout) listInterfaceView.findViewById(R.id.sortAtk_l_overlay);
        LinearLayout sortHPl = (LinearLayout) listInterfaceView.findViewById(R.id.sortHp_l_overlay);
        LinearLayout sortRCVl = (LinearLayout) listInterfaceView.findViewById(R.id.sortRcv_l_overlay);

        sortNamel.setOnClickListener(sortNameOnClick);
        sortTypel.setOnClickListener(sortTypeOnClick);
        sortStarsl.setOnClickListener(sortStarsOnClick);
        sortAtkl.setOnClickListener(sortAtkOnClick);
        sortHPl.setOnClickListener(sortHPOnClick);
        sortRCVl.setOnClickListener(sortRCVOnClick);

        final ImageButton filterBtn = (ImageButton) listInterfaceView.findViewById(R.id.filterBtn_overlay);
        resetBtn = (ImageButton) listInterfaceView.findViewById(R.id.resetBtn_overlay);
        filterText = (EditText) listInterfaceView.findViewById(R.id.filterText_overlay);
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
        final LinearLayout F_TEXT = (LinearLayout) listInterfaceView.findViewById(R.id.filtertext_layout_overlay);
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
                hideKeyboard();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterText.setText("");
                rebuildList();
                showKeyboard();
            }
        });
        dbList = (ListView) listInterfaceView.findViewById(R.id.listView1_overlay);
        dbList.setOnItemClickListener(lvOnClick);
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
        if(listInterfaceView !=null) {
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

        mainIcon = new ImageView(this);

        mainIcon.setImageResource(R.mipmap.ic_flyingchopper);
        mainIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        paramsMainIcon = new AnimationLayoutParams(
                /*WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,*/
                dpToPx(SIZE_NORMAL),
                dpToPx(SIZE_NORMAL),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsMainIcon.gravity = Gravity.TOP | Gravity.START;
        paramsMainIcon.x = 0;
        paramsMainIcon.y = 100;

        listIcon = new ImageView(this);

        listIcon.setImageResource(R.mipmap.ic_list);
        listIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        paramsListIcon = new AnimationLayoutParams(
                /*WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,*/
                dpToPx(SIZE_NORMAL),
                dpToPx(SIZE_NORMAL),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        paramsListIcon.gravity = Gravity.TOP | Gravity.START;
        dmgcalcIcon = new ImageView(this);

        dmgcalcIcon.setImageResource(R.mipmap.ic_dmgcalc);
        dmgcalcIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        paramsDmgCalcIcon = new AnimationLayoutParams(
                /*WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,*/
                dpToPx(SIZE_NORMAL),
                dpToPx(SIZE_NORMAL),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        paramsDmgCalcIcon.gravity = Gravity.TOP | Gravity.START;

        calculatorIcon = new ImageView(this);

        calculatorIcon.setImageResource(R.mipmap.ic_calculator);
        calculatorIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        paramsCalcIcon = new AnimationLayoutParams(
                /*WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,*/
                dpToPx(SIZE_NORMAL),
                dpToPx(SIZE_NORMAL),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        paramsCalcIcon.gravity = Gravity.TOP | Gravity.START;

        ellipsisIcon = new ImageView(this);

        ellipsisIcon.setImageResource(R.mipmap.ic_ellipsis);
        ellipsisIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        paramsEllipsisIcon = new AnimationLayoutParams(
                /*WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,*/
                dpToPx(SIZE_SMALL),
                dpToPx(SIZE_SMALL),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsEllipsisIcon.gravity = Gravity.TOP | Gravity.START;

        ellipsisIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getBaseContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                getApplication().startActivity(i);
                if(listInterfaceView != null && listInterfaceView.getWindowToken() != null)
                    windowManager.removeView(listInterfaceView);
                if(dmgCalcInterfaceView != null && dmgCalcInterfaceView.getWindowToken() != null)
                    windowManager.removeView(dmgCalcInterfaceView);
                if(calcInterfaceView != null && calcInterfaceView.getWindowToken() != null)
                    windowManager.removeView(calcInterfaceView);
                if(listIcon != null && listIcon.getWindowToken() != null)
                    windowManager.removeView(listIcon);
                if(dmgcalcIcon != null && dmgcalcIcon.getWindowToken() != null)
                    windowManager.removeView(dmgcalcIcon);
                if(calculatorIcon != null && calculatorIcon.getWindowToken() != null)
                    windowManager.removeView(calculatorIcon);
                if(ellipsisIcon != null && ellipsisIcon.getWindowToken() != null)
                    windowManager.removeView(ellipsisIcon);
            }
        });

        windowManager.addView(mainIcon, paramsMainIcon);

        closeBtn = new ImageView(context);

        closeBtn.setImageResource(R.mipmap.ic_close);
        closeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);

        paramsCloseBtn = new AnimationLayoutParams(
                dpToPx(40),
                dpToPx(40),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT);

        paramsCloseBtn.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        paramsCloseBtn.dimAmount = 0.5f;
        paramsCloseBtn.setY(dpToPx(24));

        try {
            mainIcon.setOnTouchListener(mainiconClickAndMove);
            listIcon.setOnClickListener(listIconClick);
            dmgcalcIcon.setOnClickListener(dmgcalcIconClick);
            calculatorIcon.setOnClickListener(calculatorIconClick);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mainIcon != null) windowManager.removeView(mainIcon);
    }

    View.OnTouchListener mainiconClickSimple = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                thumbClick();
            }
            return false;
        }
    };

    View.OnTouchListener mainiconClickAndMove = new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    // Get current time in nano seconds.

                    initialX = paramsMainIcon.x;
                    initialY = paramsMainIcon.y;
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
                        animateFade(closeBtn, paramsCloseBtn, 0.0f, 300, true, null);
                        isCloseVisible = false;
                        if(toBreak) return false;
                    }
                    if(!isClick) {
                        int x = (paramsMainIcon.x > ((getScreenWidth() - dpToPx(SIZE_NORMAL)) / 2)) ? getScreenWidth() - dpToPx(SIZE_NORMAL) : 0;
                        ObjectAnimator animation = ObjectAnimator.ofInt(paramsMainIcon, "x", x);
                        animation.setDuration(600);
                        animation.setInterpolator(new BounceInterpolator());
                        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                windowManager.updateViewLayout(mainIcon, paramsMainIcon);
                            }
                        });
                        animation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                windowManager.updateViewLayout(mainIcon, paramsMainIcon);
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
                    int dpN = dpToPx(SIZE_NORMAL);
                    int scrH = getScreenHeight();
                    if((Math.abs(event.getRawX()-initialTouchX)>dpToPx(TOUCH_TRESHOLD)) || (Math.abs(event.getRawY()-initialTouchY)>dpToPx(TOUCH_TRESHOLD))) {
                        paramsMainIcon.x = initialX + (int) (event.getRawX() - initialTouchX);
                        paramsMainIcon.y = initialY + (int) (event.getRawY() - initialTouchY);
                        paramsMainIcon.y = (paramsMainIcon.y < 0) ? 0 : paramsMainIcon.y;
                        paramsMainIcon.y = (paramsMainIcon.y > (scrH- dpN)) ? (scrH- dpN) : paramsMainIcon.y;
                        windowManager.updateViewLayout(mainIcon, paramsMainIcon);
                        isClick = false;
                        if (!isCloseVisible) {
                            closeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    service.stopSelf();
                                }
                            });
                            paramsCloseBtn.alpha = 0.0f;
                            windowManager.addView(closeBtn, paramsCloseBtn);
                            animateFade(closeBtn, paramsCloseBtn, 1.0f, 300, false, null);
                            isCloseVisible = true;
                        }
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        int width = getScreenWidth();
                        int height = getScreenHeight();
                        if ((x > ((width / 2) - dpToPx(22))) && (x < ((width / 2) + dpToPx(22))) && (y > (height - dpToPx(64))) && (y < (height - dpToPx(24)))) {
                            if (isSmall) {
                                animateY(closeBtn, paramsCloseBtn, dpToPx(32), 100, false);
                                isSmall = false;
                            }
                        } else {
                            if (!isSmall) {
                                animateY(closeBtn, paramsCloseBtn, dpToPx(24), 100, false);
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
    private void animateFade(final View v, final AnimationLayoutParams params, float to, int ms, final boolean remove, @Nullable Interpolator interpolator)
    {
        ObjectAnimator animation = ObjectAnimator.ofFloat(params, "alpha", to);
        animation.setDuration(ms);
        if (interpolator!= null) animation.setInterpolator(interpolator);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                try {
                    windowManager.updateViewLayout(v, params);
                } catch (Exception e) {
                    e.printStackTrace();
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
                    if(remove)
                        windowManager.removeView(v);
                    else
                        windowManager.updateViewLayout(v, params);
                } catch(Exception e) {
                    e.printStackTrace();
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
    }
    private void animateY(final View v, final AnimationLayoutParams params, int toY, int ms, final boolean toRemove)
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
                try {
                    if(!toRemove)
                        windowManager.updateViewLayout(v, params);
                    else
                        windowManager.removeView(v);
                } catch (Exception e) {
                    e.printStackTrace();
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

    View.OnClickListener listIconClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(panelSelection != SELECT_LIST) {
                panelSelection = SELECT_LIST;
                View[] toShrink = {mainIcon, dmgcalcIcon, calculatorIcon};
                AnimationLayoutParams[] paramsToShrink = {paramsMainIcon, paramsDmgCalcIcon, paramsCalcIcon};
                animateSize(toShrink, paramsToShrink, listIcon, paramsListIcon);
                listOpen();
            }
        }
    };

    View.OnClickListener dmgcalcIconClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(panelSelection != SELECT_DMGCALC) {
                panelSelection = SELECT_DMGCALC;
                View[] toShrink = {mainIcon, listIcon, calculatorIcon};
                AnimationLayoutParams[] paramsToShrink = {paramsMainIcon, paramsListIcon, paramsCalcIcon};
                animateSize(toShrink, paramsToShrink, dmgcalcIcon, paramsDmgCalcIcon);
                dmgcalcOpen();
            }
        }
    };

    View.OnClickListener calculatorIconClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(panelSelection != SELECT_CALCULATOR) {
                panelSelection = SELECT_CALCULATOR;
                View[] toShrink = {mainIcon, listIcon, dmgcalcIcon};
                AnimationLayoutParams[] paramsToShrink = {paramsMainIcon, paramsListIcon, paramsDmgCalcIcon};
                animateSize(toShrink, paramsToShrink, calculatorIcon, paramsCalcIcon);
                calculatorOpen();
            }
        }
    };

    private void launchDialog(int id) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View dialog = layoutInflater.inflate(R.layout.dialog_main_overlay, null);

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

        TabHost.TabSpec manuals_tab = tabs.newTabSpec("MANUALS");
        manuals_tab.setIndicator(getString(R.string.tab_manuals));
        manuals_tab.setContent(R.id.tab_manuals);
        tabs.addTab(manuals_tab);
        tabs.getTabWidget().getChildTabViewAt(4).setVisibility(View.GONE);

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

        LinearLayout manuals_content = (LinearLayout) dialog.findViewById(R.id.manuals_content);
        List<DropInfo> manuals = charInfo.getManualsInfos();

        if (manuals.size() > 0) {
            for (int i = 0; i < manuals.size(); i++) {
                DropInfo this_manuals = manuals.get(i);

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
                Integer cont_id = this_manuals.getDropThumbnail();
                Glide
                        .with(context)
                        .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID(cont_id) + ".png")
                        .dontTransform()
                        .override(thumbnail_width, thumbnail_height)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(evo_pic); //ADD PIC
                manuals_row.addView(evo_pic);

                TextView manual_name = new TextView(context);
                LinearLayout.LayoutParams txt_params =  new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                txt_params.setMargins(dpToPx(10), 0, 5, 0);
                manual_name.setLayoutParams(txt_params);
                manual_name.setText(this_manuals.getDropLocation());
                manual_name.setTextColor(getResources().getColor(R.color.char_info_txt_teal));
                manual_name.setGravity(Gravity.CENTER);

                manuals_row.addView(manual_name);

                TextView manual_det = new TextView(context);
                LinearLayout.LayoutParams txt2_params =  new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                txt2_params.setMargins(5, 0, 5, 0);
                manual_det.setLayoutParams(txt2_params);
                manual_det.setText(this_manuals.getDropChapterOrDifficulty());
                manual_det.setTextColor(getResources().getColor(R.color.char_info_txt_teal));
                manual_det.setGravity(Gravity.CENTER);

                manuals_row.addView(manual_det);

                TextView manual_notes = new TextView(context);
                manual_notes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                manual_notes.setGravity(Gravity.CENTER);
                manual_notes.setTypeface(manual_notes.getTypeface(), Typeface.BOLD);
                manual_notes.setTextColor(getResources().getColor(R.color.char_info_header_txt_teal));

                if(this_manuals.isGlobal() && !this_manuals.isJapan())
                    manual_notes.setText(getString(R.string.drops_global));
                else if(!this_manuals.isGlobal() && this_manuals.isJapan())
                    manual_notes.setText(getString(R.string.drops_japan));

                manuals_content.addView(manuals_row);
                if(!manual_notes.getText().equals("")) manuals_content.addView(manual_notes);
            }
            tabs.getTabWidget().getChildTabViewAt(4).setVisibility(View.VISIBLE);
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

    private static final int SIZE_NORMAL = 72;
    private static final int SIZE_SMALL = 60;
    private static final int TOUCH_TRESHOLD = 15;
    private static final int INTERFACE_SPACING = 74;

    private void animateSize(final View[] toShrink, final AnimationLayoutParams[] paramsToShrink, View toEnlarge, AnimationLayoutParams paramsToEnlarge)
    {
        AnimatorSet animSet = new AnimatorSet();
        List<Animator> animList = new ArrayList<>();
        for(int i = 0; i < toShrink.length; i++) {
            PropertyValuesHolder width = PropertyValuesHolder.ofInt("width", dpToPx(SIZE_SMALL));
            PropertyValuesHolder height = PropertyValuesHolder.ofInt("height", dpToPx(SIZE_SMALL));
            final View v = toShrink[i];
            final AnimationLayoutParams pm = paramsToShrink[i];
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(pm, width, height);
            anim.setDuration(100);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try {
                        windowManager.updateViewLayout(v, pm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    try {
                        windowManager.updateViewLayout(v, pm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animList.add(anim);
        }
        PropertyValuesHolder width = PropertyValuesHolder.ofInt("width", dpToPx(SIZE_NORMAL));
        PropertyValuesHolder height = PropertyValuesHolder.ofInt("height", dpToPx(SIZE_NORMAL));
        final View v = toEnlarge;
        final AnimationLayoutParams pm = paramsToEnlarge;
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(pm, width, height);
        anim.setDuration(100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                try {
                    windowManager.updateViewLayout(v, pm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                try {
                    windowManager.updateViewLayout(v, pm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animList.add(anim);
        animSet.playTogether(animList);
        animSet.start();
    }
}