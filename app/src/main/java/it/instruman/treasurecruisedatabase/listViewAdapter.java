package it.instruman.treasurecruisedatabase;

/**
 * Created by Paolo on 05/10/2016.
 */

import android.app.Service;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class listViewAdapter extends BaseAdapter {
    Drawable drawable;
    private ArrayList<HashMap> list;
    private Context activity;

    public listViewAdapter(Context activity, ArrayList<HashMap> list) {
        super();
        this.activity = activity;
        this.list = list;
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
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

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

        Glide
                .with(activity)
                .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID((Integer) map.get(Constants.ID)) + ".png")
                .override(96, 96)
                .fitCenter()
                .into(holder.smallImg);

        holder.txtFirst.setText((String) map.get(Constants.NAME));
        String SEC_COL = (String) map.get(Constants.TYPE);
        holder.txtSecond.setText(SEC_COL);
        switch (SEC_COL) {
            case "STR":
                holder.txtSecond.setBackgroundColor(activity.getResources().getColor(R.color.str_bg));
                holder.txtSecond.setTextColor(activity.getResources().getColor(R.color.str_txt));
                break;
            case "QCK":
                holder.txtSecond.setBackgroundColor(activity.getResources().getColor(R.color.qck_bg));
                holder.txtSecond.setTextColor(activity.getResources().getColor(R.color.qck_txt));
                break;
            case "DEX":
                holder.txtSecond.setBackgroundColor(activity.getResources().getColor(R.color.dex_bg));
                holder.txtSecond.setTextColor(activity.getResources().getColor(R.color.dex_txt));
                break;
            case "PSY":
                holder.txtSecond.setBackgroundColor(activity.getResources().getColor(R.color.psy_bg));
                holder.txtSecond.setTextColor(activity.getResources().getColor(R.color.psy_txt));
                break;
            case "INT":
                holder.txtSecond.setBackgroundColor(activity.getResources().getColor(R.color.int_bg));
                holder.txtSecond.setTextColor(activity.getResources().getColor(R.color.int_txt));
                break;
            default:
                break;
        }
        holder.txtThird.setText(map.get(Constants.STARS).toString());
        switch ((Integer) map.get(Constants.STARS)) {
            case 1:
            case 2:
                holder.txtThird.setBackgroundColor(activity.getResources().getColor(R.color.bronze_bg));
                holder.txtThird.setTextColor(activity.getResources().getColor(R.color.bronze_txt));
                break;
            case 3:
                holder.txtThird.setBackgroundColor(activity.getResources().getColor(R.color.silver_bg));
                holder.txtThird.setTextColor(activity.getResources().getColor(R.color.silver_txt));
                break;
            case 4:
            case 5:
                holder.txtThird.setBackgroundColor(activity.getResources().getColor(R.color.gold_bg));
                holder.txtThird.setTextColor(activity.getResources().getColor(R.color.gold_txt));
                break;
            case 6:
                holder.txtThird.setBackgroundColor(activity.getResources().getColor(R.color.red_bg));
                holder.txtThird.setTextColor(activity.getResources().getColor(R.color.red_txt));
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
    }
}
