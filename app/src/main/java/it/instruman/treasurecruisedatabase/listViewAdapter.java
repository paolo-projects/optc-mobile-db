package it.instruman.treasurecruisedatabase;

/**
 * Created by Paolo on 05/10/2016.
 */

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class listViewAdapter extends BaseAdapter {
    private ArrayList<HashMap> list;
    private Context activity;

    public listViewAdapter(Context activity, ArrayList<HashMap> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    public Integer getIDfromPosition(int position) {
        return (Integer) list.get(position).get(Constants.ID);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.column_row, parent, false);
            holder = new ViewHolder();
            holder.smallImg = (ImageView) convertView.findViewById(R.id.small_img);
            holder.txtName = (TextView) convertView.findViewById(R.id.name);
            holder.txtType = (TextView) convertView.findViewById(R.id.type);
            holder.txtStars = (TextView) convertView.findViewById(R.id.stars);
            holder.txtAtk = (TextView) convertView.findViewById(R.id.atk);
            holder.txtHP = (TextView) convertView.findViewById(R.id.hp);
            holder.txtRCV = (TextView) convertView.findViewById(R.id.rcv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap map = list.get(position);

        Glide
                .with(activity)
                .load("http://onepiece-treasurecruise.com/wp-content/uploads/f" + convertID((Integer) map.get(Constants.ID)) + ".png")
                .dontTransform()
                .override(MainActivity.thumbnail_width, MainActivity.thumbnail_height)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.smallImg);

        holder.txtName.setText((String) map.get(Constants.NAME));
        String SEC_COL = (String) map.get(Constants.TYPE);
        holder.txtType.setText(SEC_COL);
        switch (SEC_COL) {
            case "STR":
                holder.txtType.setBackgroundColor(activity.getResources().getColor(R.color.str_bg));
                holder.txtType.setTextColor(activity.getResources().getColor(R.color.str_txt));
                break;
            case "QCK":
                holder.txtType.setBackgroundColor(activity.getResources().getColor(R.color.qck_bg));
                holder.txtType.setTextColor(activity.getResources().getColor(R.color.qck_txt));
                break;
            case "DEX":
                holder.txtType.setBackgroundColor(activity.getResources().getColor(R.color.dex_bg));
                holder.txtType.setTextColor(activity.getResources().getColor(R.color.dex_txt));
                break;
            case "PSY":
                holder.txtType.setBackgroundColor(activity.getResources().getColor(R.color.psy_bg));
                holder.txtType.setTextColor(activity.getResources().getColor(R.color.psy_txt));
                break;
            case "INT":
                holder.txtType.setBackgroundColor(activity.getResources().getColor(R.color.int_bg));
                holder.txtType.setTextColor(activity.getResources().getColor(R.color.int_txt));
                break;
            default:
                break;
        }
        Double stars = (Double)map.get(Constants.STARS);
        DecimalFormat df = new DecimalFormat("0");
        df.setRoundingMode(RoundingMode.DOWN);
        String stars_p = df.format(stars);
        if(stars==5.5)
            holder.txtStars.setText("5+");
        else if (stars==6.5)
            holder.txtStars.setText("6+");
        else
            holder.txtStars.setText(stars_p);
        switch (stars_p) {
            case "1":
            case "2":
                holder.txtStars.setBackgroundColor(activity.getResources().getColor(R.color.bronze_bg));
                holder.txtStars.setTextColor(activity.getResources().getColor(R.color.bronze_txt));
                break;
            case "3":
                holder.txtStars.setBackgroundColor(activity.getResources().getColor(R.color.silver_bg));
                holder.txtStars.setTextColor(activity.getResources().getColor(R.color.silver_txt));
                break;
            case "4":
            case "5":
                holder.txtStars.setBackgroundColor(activity.getResources().getColor(R.color.gold_bg));
                holder.txtStars.setTextColor(activity.getResources().getColor(R.color.gold_txt));
                break;
            case "6":
                holder.txtStars.setBackgroundColor(activity.getResources().getColor(R.color.red_bg));
                holder.txtStars.setTextColor(activity.getResources().getColor(R.color.red_txt));
                break;
        }
        holder.txtAtk.setText(map.containsKey(Constants.MAXATK) ? String.valueOf(map.get(Constants.MAXATK)) : "");
        holder.txtHP.setText(map.containsKey(Constants.MAXHP) ? String.valueOf(map.get(Constants.MAXHP)) : "");
        holder.txtRCV.setText(map.containsKey(Constants.MAXRCV) ? String.valueOf(map.get(Constants.MAXRCV)) : "");
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
        TextView txtName;
        TextView txtType;
        TextView txtStars;
        TextView txtAtk;
        TextView txtHP;
        TextView txtRCV;
    }
}
