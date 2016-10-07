package it.instruman.treasurecruisedatabase;

/**
 * Created by Paolo on 05/10/2016.
 */

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;

public class listViewAdapter extends BaseAdapter {
    private DrawableBackgroundDownloader Downloader;
    private ArrayList<HashMap> list;
    private Activity activity;
    private ScaleDrawable sd;

    public listViewAdapter(Activity activity, ArrayList<HashMap> list) {
        super();
        this.activity = activity;
        this.list = list;
        Downloader = new DrawableBackgroundDownloader();
        Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_refresh);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.5),
                (int) (drawable.getIntrinsicHeight() * 0.5));
        sd = new ScaleDrawable(drawable, 0, scaleWidth, scaleHeight);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.column_row, null);
            holder = new ViewHolder();
            holder.smallImg = (ImageView) convertView.findViewById(R.id.small_img);
            holder.txtFirst = (TextView) convertView.findViewById(R.id.name);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.type);
            holder.txtThird = (TextView) convertView.findViewById(R.id.stars);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap map = list.get(position);
        //holder.smallImg.setImageDrawable(LoadImageFromWebOperations("http://onepiece-treasurecruise.com/wp-content/uploads/f"+convertID((Integer)map.get(Constants.ID))+".png"));
        Downloader.loadDrawable("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID((Integer) map.get(Constants.ID)) + ".png", holder.smallImg, sd.getDrawable());
        holder.txtFirst.setText((String) map.get(Constants.NAME));
        String SEC_COL = (String) map.get(Constants.TYPE);
        holder.txtSecond.setText(SEC_COL);
        switch (SEC_COL) {
            case "STR":
                holder.txtSecond.setTextColor(Color.RED);
                break;
            case "QCK":
                holder.txtSecond.setTextColor(Color.BLUE);
                break;
            case "DEX":
                holder.txtSecond.setTextColor(Color.GREEN);
                break;
            case "PSY":
                holder.txtSecond.setTextColor(Color.YELLOW);
                break;
            case "INT":
                holder.txtSecond.setTextColor(Color.argb(255, 204, 0, 102));
                break;
            default:
                break;
        }
        holder.txtThird.setText((String) map.get(Constants.STARS));
        switch (Integer.parseInt((String) map.get(Constants.STARS))) {
            case 1:
            case 2:
                holder.txtThird.setTextColor(Color.argb(255, 124, 35, 0));
                break;
            case 3:
                holder.txtThird.setTextColor(Color.argb(255, 90, 90, 90));
                break;
            case 4:
            case 5:
                holder.txtThird.setTextColor(Color.argb(255, 199, 199, 0));
                break;
            case 6:
                holder.txtThird.setTextColor(Color.RED);
                break;
        }
        return convertView;
    }

    private String convertID(Integer ID) {
        if (ID < 10) return ("000" + ID.toString());
        else if (ID < 100) return ("00" + ID.toString());
        else if (ID < 1000) return ("0" + ID.toString());
        else return ID.toString();
    }

    private class ViewHolder {
        ImageView smallImg;
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        TextView txtFourth;
    }
}
