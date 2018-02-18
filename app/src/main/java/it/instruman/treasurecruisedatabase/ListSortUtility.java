package it.instruman.treasurecruisedatabase;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Paolo on 05/11/2016.
 */

public class ListSortUtility {

    private static final int SORT_ASCENDING = 0;
    private static final int SORT_DESCENDING = 1;

    private HashMap<Integer, Integer> tagToSortType = new HashMap<>();
    private HashMap<Integer, String> tagToListColumn = new HashMap<>();

    private List<Integer> sortColumns = new ArrayList<>();

    public ListSortUtility() {
        //SORT TYPES
        tagToSortType.put(R.drawable.ic_circle, SORT_DESCENDING); // IF NEUTRAL THEN SORT DESCENDING
        tagToSortType.put(R.drawable.ic_arrow_down, SORT_ASCENDING); // IF DESCENDING THEN SORT ASCENDING
        tagToSortType.put(R.drawable.ic_arrow_up, SORT_DESCENDING); // IF ASCENDING THEN SORT DESCENDING

        tagToListColumn.put(R.id.sortName, Constants.NAME);
        tagToListColumn.put(R.id.sortType, Constants.TYPE);
        tagToListColumn.put(R.id.sortStars, Constants.STARS);
        tagToListColumn.put(R.id.sortAtk, Constants.MAXATK);
        tagToListColumn.put(R.id.sortHp, Constants.MAXHP);
        tagToListColumn.put(R.id.sortRcv, Constants.MAXRCV);

        sortColumns.add(R.id.sortName);
        sortColumns.add(R.id.sortType);
        sortColumns.add(R.id.sortStars);
        sortColumns.add(R.id.sortAtk);
        sortColumns.add(R.id.sortHp);
        sortColumns.add(R.id.sortRcv);
    }

    public ArrayList<HashMap> sortList(Activity activity, View v, ArrayList<HashMap> list) {
        int viewId = (int) v.getTag(R.id.TAG_SORT_ID);
        int sortType = (int) v.getTag(R.id.TAG_SORT_STATE);

        String COLUMN_TAG = tagToListColumn.get(viewId);
        int SORT_TYPE = tagToSortType.get(sortType);

        ArrayList<HashMap> sorted = sortBy(list, SORT_TYPE, COLUMN_TAG);

        ImageView finalView = activity.findViewById(viewId);
        int finalState = R.drawable.ic_circle;
        switch (SORT_TYPE) {
            case SORT_ASCENDING:
                finalState = R.drawable.ic_arrow_up;
                break;
            case SORT_DESCENDING:
                finalState = R.drawable.ic_arrow_down;
        }
        finalView.setImageDrawable(SortImageHelper.getTintDrawable(activity, finalState));
        finalView.setTag(R.id.TAG_SORT_STATE, finalState);

        sortColumns.remove((Integer) viewId);
        for (int this_id : sortColumns) {
            ImageView this_view = activity.findViewById(this_id);
            this_view.setImageDrawable(SortImageHelper.getTintCircleDrawable(activity));
            this_view.setTag(R.id.TAG_SORT_STATE, R.drawable.ic_circle);
        }
        return sorted;
    }

    private ArrayList<HashMap> sortBy(ArrayList<HashMap> list, int SORT_TYPE, String COLUMN_TAG) {
        switch (SORT_TYPE) {
            case SORT_DESCENDING:
                Collections.sort(list, new MainActivity.MapComparator(COLUMN_TAG));
                break;
            case SORT_ASCENDING:
                Collections.sort(list, Collections.reverseOrder(new MainActivity.MapComparator(COLUMN_TAG)));
        }
        return list;
    }
}
