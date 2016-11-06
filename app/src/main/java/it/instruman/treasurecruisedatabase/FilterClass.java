package it.instruman.treasurecruisedatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.TimingLogger;

import org.mozilla.javascript.NativeArray;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Paolo on 10/10/2016.
 */

public class FilterClass {

    public static ArrayList<HashMap> filterWithDB(Context context, EnumSet<MainActivity.FL_TYPE> type_filters, EnumSet<MainActivity.FL_CLASS> class_filters,
                                                  EnumSet<MainActivity.FL_STARS> stars_filters, String filterText) {
        TimingLogger timing = new TimingLogger("TIME", "Init");
        DBHelper dbhelper = new DBHelper(context);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String type_condition = "";
        ArrayList<String> values = new ArrayList<>();
        if (type_filters.size() > 0) {
            type_condition += "( ";
            if (type_filters.contains(MainActivity.FL_TYPE.STR)) {
                type_condition += DBHelper.UNITS_TYPE + " = ? OR ";
                values.add("STR");
            }
            if (type_filters.contains(MainActivity.FL_TYPE.DEX)) {
                type_condition += DBHelper.UNITS_TYPE + " = ? OR ";
                values.add("DEX");
            }
            if (type_filters.contains(MainActivity.FL_TYPE.QCK)) {
                type_condition += DBHelper.UNITS_TYPE + " = ? OR ";
                values.add("QCK");
            }
            if (type_filters.contains(MainActivity.FL_TYPE.PSY)) {
                type_condition += DBHelper.UNITS_TYPE + " = ? OR ";
                values.add("PSY");
            }
            if (type_filters.contains(MainActivity.FL_TYPE.INT)) {
                type_condition += DBHelper.UNITS_TYPE + " = ? OR ";
                values.add("INT");
            }
            type_condition = type_condition.substring(0, type_condition.length() - 3);
            type_condition += ") AND ";
        }

        if (stars_filters.size() > 0) {
            type_condition += "( ";
            if (stars_filters.contains(MainActivity.FL_STARS.ONE)) {
                type_condition += DBHelper.UNITS_STARS + " = ? OR ";
                values.add("1");
            }
            if (stars_filters.contains(MainActivity.FL_STARS.TWO)) {
                type_condition += DBHelper.UNITS_STARS + " = ? OR ";
                values.add("2");
            }
            if (stars_filters.contains(MainActivity.FL_STARS.THREE)) {
                type_condition += DBHelper.UNITS_STARS + " = ? OR ";
                values.add("3");
            }
            if (stars_filters.contains(MainActivity.FL_STARS.FOUR)) {
                type_condition += DBHelper.UNITS_STARS + " = ? OR ";
                values.add("4");
            }
            if (stars_filters.contains(MainActivity.FL_STARS.FIVE)) {
                type_condition += DBHelper.UNITS_STARS + " = ? OR ";
                values.add("5");
            }
            if (stars_filters.contains(MainActivity.FL_STARS.SIX)) {
                type_condition += DBHelper.UNITS_STARS + " = ? OR ";
                values.add("6");
            }
            type_condition = type_condition.substring(0, type_condition.length() - 3);
            type_condition += ") AND ";
        }

        if (class_filters.size() > 0) {
            type_condition += "( ";
            if (class_filters.contains(MainActivity.FL_CLASS.FIGHTER)) {
                type_condition += "( " + DBHelper.UNITS_CLASS1 + " = ? OR " +
                        DBHelper.UNITS_CLASS2 + " = ? ) AND ";
                values.add("Fighter");
                values.add("Fighter");
            }
            if (class_filters.contains(MainActivity.FL_CLASS.SLASHER)) {
                type_condition += "( " + DBHelper.UNITS_CLASS1 + " = ? OR " +
                        DBHelper.UNITS_CLASS2 + " = ? ) AND ";
                values.add("Slasher");
                values.add("Slasher");
            }
            if (class_filters.contains(MainActivity.FL_CLASS.STRIKER)) {
                type_condition += "( " + DBHelper.UNITS_CLASS1 + " = ? OR " +
                        DBHelper.UNITS_CLASS2 + " = ? ) AND ";
                values.add("Striker");
                values.add("Striker");
            }
            if (class_filters.contains(MainActivity.FL_CLASS.SHOOTER)) {
                type_condition += "( " + DBHelper.UNITS_CLASS1 + " = ? OR " +
                        DBHelper.UNITS_CLASS2 + " = ? ) AND ";
                values.add("Shooter");
                values.add("Shooter");
            }
            if (class_filters.contains(MainActivity.FL_CLASS.FREESPIRIT)) {
                type_condition += "( " + DBHelper.UNITS_CLASS1 + " = ? OR " +
                        DBHelper.UNITS_CLASS2 + " = ? ) AND ";
                values.add("Free Spirit");
                values.add("Free Spirit");
            }
            if (class_filters.contains(MainActivity.FL_CLASS.CEREBRAL)) {
                type_condition += "( " + DBHelper.UNITS_CLASS1 + " = ? OR " +
                        DBHelper.UNITS_CLASS2 + " = ? ) AND ";
                values.add("Cerebral");
                values.add("Cerebral");
            }
            if (class_filters.contains(MainActivity.FL_CLASS.POWERHOUSE)) {
                type_condition += "( " + DBHelper.UNITS_CLASS1 + " = ? OR " +
                        DBHelper.UNITS_CLASS2 + " = ? ) AND ";
                values.add("Powerhouse");
                values.add("Powerhouse");
            }
            if (class_filters.contains(MainActivity.FL_CLASS.DRIVEN)) {
                type_condition += "( " + DBHelper.UNITS_CLASS1 + " = ? OR " +
                        DBHelper.UNITS_CLASS2 + " = ? ) AND ";
                values.add("Driven");
                values.add("Driven");
            }
            type_condition = type_condition.substring(0, type_condition.length() - 4);
            type_condition += " ) AND ";
        }
        if (!filterText.equals("")) {
            if (filterText.matches("\\d+")) {
                type_condition += DBHelper.UNITS_CHARID + " = ? AND ";
                values.add(filterText);
            } else {
                type_condition += DBHelper.UNITS_NAME + " LIKE ? AND ";
                values.add("%" + filterText + "%");
            }
        }
        if (type_condition.equals("")) {
            db.close();
            dbhelper.close();
            return new ArrayList<>();
        }
        type_condition = type_condition.substring(0, type_condition.length() - 4);
        String[] values_array = new String[values.size()];
        values.toArray(values_array);
        ArrayList<HashMap> filtered = new ArrayList<>();
        Cursor result = db.query(DBHelper.UNITS_TABLE, new String[]{DBHelper.UNITS_NAME, DBHelper.UNITS_TYPE, DBHelper.UNITS_STARS, DBHelper.UNITS_CHARID, DBHelper.UNITS_MAXATK, DBHelper.UNITS_MAXHP, DBHelper.UNITS_MAXRCV,}, type_condition, values_array, null, null, null, null);

        result.moveToFirst();
        while (!result.isAfterLast()) {
            HashMap element = new HashMap();
            element.put(Constants.NAME, result.getString(0));
            element.put(Constants.TYPE, result.getString(1));
            element.put(Constants.STARS, result.getInt(2));
            element.put(Constants.ID, result.getInt(3));
            element.put(Constants.MAXATK, result.getInt(4));
            element.put(Constants.MAXHP, result.getInt(5));
            element.put(Constants.MAXRCV, result.getInt(6));
            filtered.add(element);
            result.moveToNext();
        }
        result.close();
        db.close();
        dbhelper.close();
        timing.addSplit("END");
        timing.dumpToLog();
        return filtered;
    }

    public static ArrayList<HashMap> filterByType(ArrayList<HashMap> list, EnumSet<MainActivity.FL_TYPE> filters) {
        EnumSet<MainActivity.FL_TYPE> toRemoveT = EnumSet.allOf(MainActivity.FL_TYPE.class);
        toRemoveT.removeAll(filters);

        ArrayList<HashMap> tmp = new ArrayList<>();
        for (HashMap foo : list) {
            String toCompare = ((String) foo.get(Constants.TYPE)).toLowerCase();
            if (toRemoveT.contains(MainActivity.FL_TYPE.STR))
                if (toCompare.contains("str")) continue;
            if (toRemoveT.contains(MainActivity.FL_TYPE.DEX))
                if (toCompare.contains("dex")) continue;
            if (toRemoveT.contains(MainActivity.FL_TYPE.QCK))
                if (toCompare.contains("qck")) continue;
            if (toRemoveT.contains(MainActivity.FL_TYPE.PSY))
                if (toCompare.contains("psy")) continue;
            if (toRemoveT.contains(MainActivity.FL_TYPE.INT))
                if (toCompare.contains("int")) continue;
            tmp.add(foo);
        }
        return tmp;
    }

    public static ArrayList<HashMap> filterByClass(ArrayList<HashMap> list, EnumSet<MainActivity.FL_CLASS> filters) {
        //EnumSet<MainActivity.FL_CLASS> toRemoveC = EnumSet.allOf(MainActivity.FL_CLASS.class);
        //toRemoveC.removeAll(filters);
        if (filters.size() == 1) {
            ArrayList<HashMap> tmp = new ArrayList<>();
            for (HashMap foo : list) {
                Object res = foo.get(Constants.CLASSES);
                if (res.getClass().equals(String.class)) {
                    String res_compare = (String) res;
                    for (MainActivity.FL_CLASS c : filters) {
                        if (c.equalsCaseInsensitive(res_compare)) {
                            tmp.add(foo);
                        }
                    }
                } else if (res.getClass().equals(NativeArray.class)) {
                    NativeArray res_compare = (NativeArray) res;
                    if (res_compare.size() == 1) {
                        String class_ = res_compare.toString().toLowerCase().trim();
                        for (MainActivity.FL_CLASS c : filters) {
                            if (c.equalsCaseInsensitive(class_)) {
                                tmp.add(foo);
                            }
                        }
                    } else if (res_compare.size() == 2) {
                        String class1 = res_compare.get(0).toString().toLowerCase().trim();
                        String class2 = res_compare.get(1).toString().toLowerCase().trim();
                        for (MainActivity.FL_CLASS c : filters) {
                            if (c.equalsCaseInsensitive(class1) || c.equalsCaseInsensitive(class2)) {
                                tmp.add(foo);
                            }
                        }
                    }
                }
            }
            return tmp;
        } else if (filters.size() == 2) {
            ArrayList<HashMap> tmp = new ArrayList<>();
            for (HashMap foo : list) {
                Object res = foo.get(Constants.CLASSES);
                if (res.getClass().equals(NativeArray.class)) {
                    NativeArray res_compare = (NativeArray) res;
                    if (res_compare.size() == 2) {
                        String class1 = res_compare.get(0).toString().toLowerCase().trim();
                        String class2 = res_compare.get(1).toString().toLowerCase().trim();
                        Iterator<MainActivity.FL_CLASS> it = filters.iterator();
                        MainActivity.FL_CLASS a = it.next();
                        MainActivity.FL_CLASS b = it.next();
                        if ((a.equalsCaseInsensitive(class1) && b.equalsCaseInsensitive(class2)) ||
                                (a.equalsCaseInsensitive(class2) && b.equalsCaseInsensitive(class1))) {
                            tmp.add(foo);
                        }
                    }
                }
            }
            return tmp;
        } else {
            return list;
        }
    }

    public static ArrayList<HashMap> filterByStars(ArrayList<HashMap> list, EnumSet<MainActivity.FL_STARS> filters) {
        EnumSet<MainActivity.FL_STARS> toRemoveS = EnumSet.allOf(MainActivity.FL_STARS.class);
        toRemoveS.removeAll(filters);

        ArrayList<HashMap> tmp = new ArrayList<>();
        for (HashMap foo : list) {
            Integer toCompare = ((Integer) foo.get(Constants.STARS));
            if (toRemoveS.contains(MainActivity.FL_STARS.ONE))
                if (toCompare == 1) continue;
            if (toRemoveS.contains(MainActivity.FL_STARS.TWO))
                if (toCompare == 2) continue;
            if (toRemoveS.contains(MainActivity.FL_STARS.THREE))
                if (toCompare == 3) continue;
            if (toRemoveS.contains(MainActivity.FL_STARS.FOUR))
                if (toCompare == 4) continue;
            if (toRemoveS.contains(MainActivity.FL_STARS.FIVE))
                if (toCompare == 5) continue;
            tmp.add(foo);
        }
        return tmp;
    }

    public static ArrayList<HashMap> filterByText(ArrayList<HashMap> list, String FilterText) {
        if (!FilterText.equals("")) {
            ArrayList<HashMap> result = new ArrayList<>();
            for (HashMap foo : list) {
                if (((String) foo.get(Constants.NAME)).toLowerCase().contains(FilterText.toLowerCase()))
                    result.add(foo);
            }
            return result;
        } else return list;
    }
}
