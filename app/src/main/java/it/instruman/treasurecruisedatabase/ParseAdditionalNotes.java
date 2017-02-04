package it.instruman.treasurecruisedatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paolo on 11/10/2016.
 */

public class ParseAdditionalNotes {
    private HashMap<String, String> notes = new HashMap<>();
    private boolean isMixed = false;
    private String stringPart = "";

    public ParseAdditionalNotes(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            notes.put(entry.getKey(), entry.getValue());
        }
    }

    public String parseNotes(String value) {
        if (!hasTag(value)) return value;
        String result_string = isMixed ? stringPart+System.getProperty("line.separator") : "";
        ArrayList<String> tags = getTags(value);

        for (String tag : tags) {
            ArrayList<String> flags = getFlags(tag);
            String tmp_result = "";
            if (flags.size() > 1) {
                int size = flags.size();
                tmp_result = notes.containsKey(flags.get(0)) ? notes.get(flags.get(0)) : "";
                for (int i = 1; i < size; i++) {
                    String placeholder = "#" + i;
                    tmp_result = tmp_result.replace(placeholder, flags.get(i));
                }
            } else if (flags.size() == 1) {
                tmp_result = notes.get(flags.get(0));
            }
            result_string += tmp_result + System.getProperty("line.separator");
        }
        return result_string.substring(0, result_string.length() - 1);
    }

    private int tagNumber(String value) {
        if (!value.equals("") && value.replaceAll("^\\s+|\\s+$", "").substring(0, 1).equals("#")) {
            int result = 1;
            int index_start = value.indexOf("}", 0);
            int index;
            index = value.indexOf("#", index_start);
            if (index != -1) {
                do {
                    result += 1;
                    index_start = value.indexOf("}", index);
                    index = value.indexOf("#", index_start);
                } while (index != -1);
            }
            return result;
        } else if (!value.equals("") && value.contains("#")) {
            isMixed = true;
            stringPart = value.substring(0, value.indexOf("#"));
            String nValue = value.replace(stringPart,"");
            int result = 1;
            int index_start = nValue.indexOf("}", 0);
            int index;
            index = nValue.indexOf("#", index_start);
            if (index != -1) {
                do {
                    result += 1;
                    index_start = nValue.indexOf("}", index);
                    index = nValue.indexOf("#", index_start);
                } while (index != -1);
            }
            return result;
        } else
            return 0;
    }

    private static boolean hasTag(String value) {
        return !value.equals("") && value.contains("#"); //value.replaceAll("^\\s+|\\s+$", "").substring(0, 1).equals("#");
    }

    private static ArrayList<String> getFlags(String value) {
        ArrayList<String> mList = new ArrayList<>();

        value = value.replace("#{", "");
        value = value.replace("}", "");
        int index = value.indexOf(":");
        if (index == -1) {
            mList.add(value.replaceAll("^\\s+|\\s+$", ""));
        } else {
            mList.add(value.substring(0, index).replaceAll("^\\s+|\\s+$", ""));
            int index2;
            while ((index2 = value.indexOf(":", index + 1)) != -1) {
                mList.add(value.substring(index + 1, index2).replaceAll("^\\s+|\\s+$", ""));
                index = index2;
            }
            mList.add(value.substring(index + 1).replaceAll("^\\s+|\\s+$", ""));
        }
        return mList;
    }

    private ArrayList<String> getTags(String value) {
        ArrayList<String> tags_list = new ArrayList<>();
        int tagNumber = tagNumber(value);
        int[] tags = new int[50];
        tags[0] = value.indexOf("#");

        int index_start = value.indexOf("}", 0);
        int index;
        index = value.indexOf("#", index_start);

        if (index != -1) {
            int curr = 1;
            do {
                tags[curr] = index;
                index_start = value.indexOf("}", index);
                curr += 1;
                index = value.indexOf("#", index_start);
            } while (index != -1);
        }
        for (int i = 0; i < tagNumber; i++) {
            if (i != (tagNumber - 1))
                tags_list.add(value.substring(tags[i], tags[i + 1]));
            else tags_list.add(value.substring(tags[i]));
        }
        return tags_list;
    }
}
