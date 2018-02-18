package it.instruman.treasurecruisedatabase;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

/**
 * Created by infan on 29/01/2018.
 */

public class SortImageHelper {
    public static Drawable getTintCircleDrawable(Context context) {
        Drawable drw = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.ic_circle));
        DrawableCompat.setTintMode(drw, PorterDuff.Mode.SRC_IN);
        DrawableCompat.setTint(drw, context.getResources().getColor(getResIdFromAttribute(context, R.attr.list_sort_btn_disabled)));
        return drw;
    }

    public static Drawable getTintDrawable(Context context, int drawableRes) {
        Drawable drw = DrawableCompat.wrap(context.getResources().getDrawable(drawableRes));
        DrawableCompat.setTintMode(drw, PorterDuff.Mode.SRC_IN);
        int colorAttr = 0;
        switch(drawableRes) {
            case R.drawable.ic_circle:
            default:
                colorAttr = R.attr.list_sort_btn_disabled;
                break;
            case R.drawable.ic_arrow_down:
            case R.drawable.ic_arrow_up:
                colorAttr = R.attr.list_sort_btn;
                break;
        }
        int colorRes = getResIdFromAttribute(context, colorAttr);
        DrawableCompat.setTint(drw, context.getResources().getColor(colorRes));
        return drw;
    }

    private static int getResIdFromAttribute(final Context activity, final int attr) {
        if (attr == 0)
            return 0;
        final TypedValue typedvalueattr = new TypedValue();
        activity.getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }
}
