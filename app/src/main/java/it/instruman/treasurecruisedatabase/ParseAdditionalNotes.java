package it.instruman.treasurecruisedatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Paolo on 11/10/2016.
 */

public class ParseAdditionalNotes {
    private HashMap<String, String> notes = new HashMap<>();

    public ParseAdditionalNotes() {
        notes.put("captainProportional", "The exact multiplier used to compute the damage is proportional to the " +
                "crew's remaining HP and is higher the #1 the HP is. The multiplier is calculated as #2. " +
                "At full health the boost is equal to #3x, with 1 HP left to #4x.");
        notes.put("captainFixed", "The multiplier is #1 unless #2, in which case it is #3.");
        notes.put("fixed", "Fixed damage means it entirely bypasses the enemy's defense.");
        notes.put("gOrbs", "Characters with [G] orbs will deal 1.5x their normal damage. [G] orbs are affected by orb boosters.");
        notes.put("noFixedPerc", "Specials that deal fixed damage or cut a percentage of the enemy's HP are not " +
                "affected by this captain ability");
        notes.put("orb", "Orb amplification only affects matching and opposite orbs and works both ways: " +
                "matching orbs will deal #1 more damage and opposite orbs will deal #1 less damage.");
        notes.put("poison", "Poison deals 0.5x character's ATK in fixed damage at the end of each turn.");
        notes.put("random", "Estimated random damage range: between #1 HP and #2 HP #3.");
        notes.put("randomHits", "The target of each of the #1 hits is chosen randomly.");
        notes.put("specialProportional", "The exact multiplier used to compute the damage is proportional to the " +
                "crew's remaining HP and is higher the #1 the HP is. The multiplier is calculated as: #2.");
        notes.put("stages", "The special can be used as soon as the first stage is reached.");
        notes.put("zombie", "The protection only works when attacked by one single enemy and will leave the team with at least 1 HP; " +
                "the effect will not work when attacked by multiples enemies at once.");
        notes.put("colorAffinity", "'Color Affinity' boosts color type advantages. For example, a STR unit normally deals 2x to a DEX unit " +
                "and .5x to QCK. With this Color Affinity boost, it will deal (2*#1)x to DEX and (0.5*(#1-1))x to QCK.");
    }

    public String parseNotes(String value) {
        if (!hasTag(value)) return value;
        String result_string = "";
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

    private static int tagNumber(String value) {
        if (!value.equals("") && value.replaceAll("^\\s+|\\s+$", "").substring(0, 1).equals("#")) {
            int result = 1;
            int index_start = value.indexOf("}", 0);
            int index = 0;
            index = value.indexOf("#", index_start);
            if (index != -1) {
                do {
                    result += 1;
                    index_start = value.indexOf("}", index);
                    index = value.indexOf("#", index_start);
                } while (index != -1);
            }
            return result;
        } else return 0;
    }

    private static boolean hasTag(String value) {
        return !value.equals("") && value.replaceAll("^\\s+|\\s+$", "").substring(0, 1).equals("#");
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
            int index2 = -1;
            while ((index2 = value.indexOf(":", index + 1)) != -1) {
                mList.add(value.substring(index + 1, index2).replaceAll("^\\s+|\\s+$", ""));
                index = index2;
            }
            mList.add(value.substring(index + 1).replaceAll("^\\s+|\\s+$", ""));
        }
        return mList;
    }

    private static ArrayList<String> getTags(String value) {
        ArrayList<String> tags_list = new ArrayList<>();
        int tagNumber = tagNumber(value);
        int[] tags = new int[50];
        tags[0] = value.indexOf("#");

        int index_start = value.indexOf("}", 0);
        int index = 0;
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
