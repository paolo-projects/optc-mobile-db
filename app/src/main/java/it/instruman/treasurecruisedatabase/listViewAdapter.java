package it.instruman.treasurecruisedatabase;

/**
 * Created by Paolo on 05/10/2016.
 */

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
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
        holder.txtThird.setText(map.get(Constants.STARS).toString());
        switch ((Integer) map.get(Constants.STARS)) {
            case 1:
            case 2:
                holder.txtThird.setTextColor(Color.argb(255, 160, 45, 0));
                break;
            case 3:
                holder.txtThird.setTextColor(Color.argb(255, 120, 120, 120));
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
    }
}
