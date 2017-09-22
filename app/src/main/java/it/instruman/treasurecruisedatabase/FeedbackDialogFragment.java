package it.instruman.treasurecruisedatabase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Paolo on 25/12/2016.
 */

public class FeedbackDialogFragment extends DialogFragment {

    static FeedbackDialogFragment newInstance() {
        FeedbackDialogFragment f = new FeedbackDialogFragment();

        return f;
    }

    LockableViewPager mPager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_feedback, container, false);

        mPager = (LockableViewPager) v.findViewById(R.id.feedback_pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setCurrentItem(0);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.dimAmount=0.4f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        getDialog().getWindow().setAttributes(lp);
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.95);

        getDialog().getWindow().setLayout(width, height);
    }

    public static class ScreenSlideFeedback extends Fragment {
        ViewGroup rootView;
        EditText messageField;
        Integer numStars = 0;
        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.dialog_feedback_review, container, false);

            Button fbNext = (Button)rootView.findViewById(R.id.feedback_next);
            fbNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String messageText = messageField.getText().toString();
                    if(!(numStars==0 && messageText.equals("")))
                        (new PostTask()).execute(numStars.toString(), messageText);
                    ViewPager pager = (ViewPager)container.findViewById(R.id.feedback_pager);
                    pager.setCurrentItem(1);
                }
            });
            String star = "star";
            for(Integer i = 1; i<=5; i++)
            {
                View v = rootView.findViewWithTag(star+i.toString());
                v.setOnClickListener(starListener);
            }
            messageField = (EditText)rootView.findViewById(R.id.feedback_message);
            return rootView;
        }

        View.OnClickListener starListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = (String)view.getTag();
                Integer m = Integer.parseInt(tag.replace("star", ""));
                for(Integer i = 1; i<= m; i++)
                {
                    ImageButton v = (ImageButton)rootView.findViewWithTag("star"+i.toString());
                    v.setBackgroundResource(R.drawable.ic_star_full);
                }
                for(Integer i = m+1; i<=5; i++)
                {
                    ImageButton v = (ImageButton)rootView.findViewWithTag("star"+i.toString());
                    v.setBackgroundResource(R.drawable.ic_star_empty);
                }
                numStars = m;
            }
        };

        private class PostTask extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... message) {
                // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://www.instruman.it/assets/feedback/update.php");

                try {
                    //add data
                    List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                    nameValuePairs.add(new BasicNameValuePair("key", "d715f58d97ad1e66294944a9d874134f"));//gitignore
                    nameValuePairs.add(new BasicNameValuePair("stars", message[0]));
                    nameValuePairs.add(new BasicNameValuePair("message", message[1]));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    //execute http post
                    HttpResponse response = httpclient.execute(httppost);

                } catch (Exception e) {

                }
                return null;
            }
        }
    }

    public static class ScreenSlideDonate extends Fragment {
        private Dialog dialog;
        public void setDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.dialog_feedback_donate, container, false);

            ImageButton feedback_donate = (ImageButton)rootView.findViewById(R.id.feedback_donate);
            TextView feedback_nodonation = (TextView)rootView.findViewById(R.id.feedback_nodonation);

            feedback_donate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=NRLCLUHJ3QF9W";
                    Intent f = new Intent(Intent.ACTION_VIEW);
                    f.setData(Uri.parse(url));
                    startActivity(f);
                    dialog.dismiss();
                }
            });

            feedback_nodonation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            TextView readMore = (TextView)rootView.findViewById(R.id.feedback_moreinfo);
            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_feedback_readmore);
                    TextView viewFeedbacks = (TextView)dialog.findViewById(R.id.feedback_view_feedbacks);
                    viewFeedbacks.setPaintFlags(viewFeedbacks.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    TextView viewDonations = (TextView)dialog.findViewById(R.id.feedback_view_donations);
                    viewDonations.setPaintFlags(viewDonations.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    TextView confirm = (TextView)dialog.findViewById(R.id.feedback_ok);
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    viewFeedbacks.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = "http://www.instruman.it/assets/feedback";
                            Intent f = new Intent(Intent.ACTION_VIEW);
                            f.setData(Uri.parse(url));
                            startActivity(f);
                        }
                    });
                    viewDonations.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = "http://www.instruman.it/assets/donations";
                            Intent f = new Intent(Intent.ACTION_VIEW);
                            f.setData(Uri.parse(url));
                            startActivity(f);
                        }
                    });
                    dialog.show();
                    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                    lp.dimAmount=0.4f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
                    dialog.getWindow().setAttributes(lp);
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
                    int height = (int)(getResources().getDisplayMetrics().heightPixels*0.95);

                    dialog.getWindow().setLayout(width, height);
                }
            });

            return rootView;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private static final int NUM_PAGES = 2;

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                case 0:
                    return new ScreenSlideFeedback();
                case 1:
                    FeedbackDialogFragment.ScreenSlideDonate ssd = new ScreenSlideDonate();
                    ssd.setDialog(getDialog());
                    return ssd;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
