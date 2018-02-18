package it.instruman.treasurecruisedatabase;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.AttributeSet;

/**
 * Created by infan on 24/12/2017.
 */

public class HtmlTextView  extends AppCompatTextView {
    public HtmlTextView(Context context) {
        super(context);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(Html.fromHtml(text.toString(), null, new ListTagHandler()), type);
    }
}
