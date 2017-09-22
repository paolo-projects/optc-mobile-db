package it.instruman.treasurecruisedatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
	New Parser based on online DB regex replacements
*/

public class ParseAdditionalNotes {
    private HashMap<String, String> notes = new HashMap<>();

    public ParseAdditionalNotes(Map<String,String> map) {
        for (Map.Entry<String,String> entry : map.entrySet()) {
            notes.put(entry.getKey(), entry.getValue());
        }
    }

    public String parseNotes(String value) {
        if((value==null) || value.equals("")) return value;
        Pattern patt = Pattern.compile("#\\{(.+?)\\}");
        Matcher matcher = patt.matcher(value.trim());
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            String[] toks = matcher.group(1).trim().split(":");
            if ((toks.length>0) && notes.containsKey(toks[0].trim())) {
                StringBuffer internal = new StringBuffer();
                matcher.appendReplacement(internal, notes.get(toks[0].trim()));
                Pattern patt2 = Pattern.compile("#(\\d+)");
                Matcher matcher2 = patt2.matcher(internal);
                while(matcher2.find()) {
                    if(Integer.parseInt(matcher2.group(1))<toks.length)
                        matcher2.appendReplacement(sb, toks[Integer.parseInt(matcher2.group(1))].trim());
                    else
                        matcher2.appendReplacement(sb, "");
                }
                matcher2.appendTail(sb);
            }
        }
        matcher.appendTail(sb);
        return convertTags(sb.toString());
    }

    private static String convertTags(String input) {
        return input.replaceAll("(?i)</? ?/?br ?>", System.getProperty("line.separator"));
    }
}