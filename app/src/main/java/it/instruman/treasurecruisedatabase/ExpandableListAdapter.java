package it.instruman.treasurecruisedatabase;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Paolo on 09/10/2016.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, LinkedHashMap<String, Boolean>> _listDataChild;

    private class VHolder {
        CheckedTextView itemView;
    }

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, LinkedHashMap<String, Boolean>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return getKeyByIndex(this._listDataChild.get(this._listDataHeader.get(groupPosition))
                , childPosititon);
    }

    public Boolean getChildValue(int groupPosition, int childPosititon) {
        return (Boolean) getValueByIndex(this._listDataChild.get(this._listDataHeader.get(groupPosition))
                , childPosititon);
    }

    public void setChildValue(int groupPosition, int childPosition, Boolean value) {
        LinkedHashMap<String, Boolean> entries = this._listDataChild.get(this._listDataHeader.get(groupPosition));
        String key = (String) getChild(groupPosition, childPosition);
        entries.put(key, value);
        this._listDataChild.put(this._listDataHeader.get(groupPosition), entries);
        notifyDataSetChanged();
    }

    public Object getKeyByIndex(LinkedHashMap map, int index) {
        return map.keySet().toArray()[index];
    }

    public Object getValueByIndex(LinkedHashMap map, int index) {
        return map.get(map.keySet().toArray()[index]);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        final Boolean childValue = getChildValue(groupPosition, childPosition);
        VHolder view;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exp_list_view_item, parent, false);

            view = new VHolder();
            view.itemView = (CheckedTextView) convertView
                    .findViewById(R.id.lblListItem);

            convertView.setTag(view);
        } else {
            view = (VHolder) convertView.getTag();
        }

        //CheckedTextView txtListChild = (CheckedTextView) convertView
        //        .findViewById(R.id.lblListItem);
        view.itemView.setText(childText);
        /*if(groupPosition==1) //if the filter is a Class filter
        {
            view.itemView.setCheckMarkDrawable(null);
            view.itemView.setChecked(childValue);
            if(childValue)
                view.itemView.setBackgroundColor(Color.argb(155, 129,135,145));
            else
                view.itemView.setBackgroundColor(Color.WHITE);
        } else {
            view.itemView.setChecked(childValue);
        }*/
        view.itemView.setChecked(childValue);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exp_list_view_gh, parent, false);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
