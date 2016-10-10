package it.instruman.treasurecruisedatabase;

import org.mozilla.javascript.NativeArray;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Paolo on 10/10/2016.
 */

public class FilterClass {
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
