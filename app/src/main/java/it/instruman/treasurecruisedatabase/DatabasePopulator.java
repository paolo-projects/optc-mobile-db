package it.instruman.treasurecruisedatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Paolo on 03/12/2016.
 */

public class DatabasePopulator {

    static void populateCharacters(SQLiteDatabase database, List<List> characters) {
        if (characters == null) return;
        int char_size = characters.size();
        if (char_size > 0) {
            database.beginTransaction();
            try {
                for (int i = 0; i < char_size; i++) {
                    List arr_2 = characters.get(i);
                    String name = (arr_2.get(0) == null) ? "" : (String) arr_2.get(0);
                    String type = (arr_2.get(1) == null) ? "" : (String) arr_2.get(1);
                    //Integer stars = (arr_2.get(3) == null) ? 1 : ((arr_2.get(3))?((Double) arr_2.get(3)).intValue();
                    Double stars = 1.0;
                    Object stars_o = arr_2.get(3);
                    if(stars_o != null) {
                        if(stars_o.equals("5+"))
                            stars = 5.5;
                        else if (stars_o.equals("6+"))
                            stars = 6.5;
                        else stars = (Double)stars_o;
                    }
                    Object classes = arr_2.get(2);
                    Integer cost = (arr_2.get(4) == null) ? null : ((Double) arr_2.get(4)).intValue();
                    Integer combo = (arr_2.get(5) == null) ? null : ((Double) arr_2.get(5)).intValue();
                    Integer sockets = (arr_2.get(6) == null) ? null : ((Double) arr_2.get(6)).intValue();
                    Integer maxlvl = (arr_2.get(7) == null) ? null : ((Double) arr_2.get(7)).intValue();
                    Integer exptomax = (arr_2.get(8) == null) ? null : ((Double) arr_2.get(8)).intValue();
                    Integer lvl1hp = (arr_2.get(9) == null) ? null : ((Double) arr_2.get(9)).intValue();
                    Integer lvl1atk = (arr_2.get(10) == null) ? null : ((Double) arr_2.get(10)).intValue();
                    Integer lvl1rcv = (arr_2.get(11) == null) ? null : ((Double) arr_2.get(11)).intValue();
                    Integer maxhp = (arr_2.get(12) == null) ? null : ((Double) arr_2.get(12)).intValue();
                    Integer maxatk = (arr_2.get(13) == null) ? null : ((Double) arr_2.get(13)).intValue();
                    Integer maxrcv = (arr_2.get(14) == null) ? null : ((Double) arr_2.get(14)).intValue();

                    String class1, class2;
                    class1 = class2 = null;
                    if (classes != null) {
                        if (classes.getClass().equals(String.class)) {
                            class1 = (String) classes;
                            class2 = null;
                        } else if (classes.getClass().equals(NativeArray.class)) {
                            List<String> classes_list = (List<String>) classes;
                            class1 = (classes_list.size() > 0) ? classes_list.get(0) : null;
                            class2 = (classes_list.size() > 1) ? classes_list.get(1) : null;
                        }
                    }
                    DBHelper.insertIntoUnits(database, i + 1, name, type, class1, class2, stars, cost, combo, sockets,
                            maxlvl, exptomax, lvl1hp, lvl1atk, lvl1rcv, maxhp, maxatk, maxrcv);
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    }

    static void populateAliases(SQLiteDatabase database, Map<Integer, List> aliases) {
        if(aliases != null && aliases.size()>0) {
            database.beginTransaction();
            try {
                for (Map.Entry<Integer, List> entry : aliases.entrySet()) {
                    Integer charId = entry.getKey();
                    List<String> aliasesList = entry.getValue();
                    for(String alias: aliasesList) {
                        if(!TextUtils.isEmpty(alias))
                            DBHelper.insertIntoAliases(database, charId, alias);
                    }
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    }

    static void populateAbilities(Context context, SQLiteDatabase database, Map<Integer, Map> details_js, ParseAdditionalNotes notes_parser, ArrayList<CoolDowns> cools_tmp) {
        if ((details_js != null) && (details_js.size() > 0)) {
            database.beginTransaction();
            try {
                for (Map.Entry<Integer, Map> entry : details_js.entrySet()) {
                    Map<String, Object> value = entry.getValue();

                    Object special = value.containsKey("special") ? value.get("special") : null;
                    String specialname = (String) (value.containsKey("specialName") ? value.get("specialName") : "");

                    String captain = "";
                    if (value.containsKey("captain")) {
                        Object captainObj = value.get("captain");
                        if (captainObj.getClass().equals(String.class))
                            captain = (String) value.get("captain");
                        else if (captainObj.getClass().equals(NativeObject.class)) {
                            Map<String, String> captainMap = (Map<String, String>) captainObj;
                            if(captainMap.get("global") != null) {
                                captain += "Global: " + captainMap.get("global") + System.getProperty("line.separator") +
                                        "Japan: " + captainMap.get("japan");
                            } else if (captainMap.get("base") != null) {
                                captain += String.format("<b>%s:</b> %s", context.getString(R.string.charpageBaseAbility), captainMap.get("base"));
                                ArrayList<String> captLevels = new ArrayList<>();
                                for (Map.Entry<String,String> e : captainMap.entrySet()) {
                                    if(e.getKey().contains("level")) {
                                        int lvl = Integer.parseInt(e.getKey().substring(5,6))-1;
                                        captLevels.add(lvl, e.getValue());
                                    }
                                }
                                for(int i = 0; i<captLevels.size(); i++) {
                                    captain += "<br><b>"+(i+1)+":</b> "+captLevels.get(i);
                                }
                            }
                        }
                    }
                    String captainnotes = notes_parser.parseNotes(value.containsKey("captainNotes") ? (String) value.get("captainNotes") : "");
                    String specialnotes = notes_parser.parseNotes(value.containsKey("specialNotes") ? (String) value.get("specialNotes") : "");

                    DBHelper.insertIntoCaptains(database, entry.getKey(), captain, captainnotes);

                    String cwDesc = "";
                    Object cwDescO = value.containsKey("sailor") ? value.get("sailor") : null;
                    if(cwDescO!=null) {
                        if (cwDescO.getClass().equals(String.class))
                        {
                            cwDesc = (String)cwDescO;
                        } else if (cwDescO.getClass().equals(NativeObject.class)) {
                            try {
                                Map<String, String> cwDescH = (Map<String, String>) cwDescO;
                                cwDesc = cwDescH.containsKey("base") ?
                                        !cwDescH.get("base").equals("None") ? cwDescH.get("base") : String.format("<b>%s:</b> none",context.getString(R.string.charpageBaseAbility))
                                        : String.format("<b>%s:</b> none",context.getString(R.string.charpageBaseAbility));
                                for (Map.Entry<String, String> e : cwDescH.entrySet()) {
                                    if (e.getKey().equals("base"))
                                        continue;
                                    cwDesc += String.format("<br><b>%s:</b> %s", e.getKey().replace("level",""), e.getValue());
                                }
                            } catch (ClassCastException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    String cwNotes = notes_parser.parseNotes(value.containsKey("sailorNotes") ? (String)value.get("sailorNotes") : null);

                    if(!cwDesc.equals("")) {
                        DBHelper.insertIntoCrewmate(database, entry.getKey(), cwDesc, cwNotes);
                    }

                    if ((special != null) && special.getClass().equals(NativeArray.class)) {
                        List<Map> specmulti = (List<Map>) special;
                        for (int n = 0; n < specmulti.size(); n++) {
                            Map<String, Object> currstep = specmulti.get(n);
                            String currspecial = (String) currstep.get("description");
                            CoolDowns cd = new CoolDowns(currstep.get("cooldown"));
                            DBHelper.insertIntoSpecials(database, entry.getKey(), specialname, currspecial, cd.init, cd.max, specialnotes);
                        }
                    } else if ((special != null) && special.getClass().equals(NativeObject.class)) {
                        String txt;
                        CoolDowns cooldwn = cools_tmp.get(entry.getKey());
                        Map<String, String> specials_localized = (Map<String, String>) special;
                        String japan = specials_localized.get("japan");
                        String global = specials_localized.get("global");
                        txt = "Jap: " + japan + System.getProperty("line.separator") +
                                "Global: " + global;
                        DBHelper.insertIntoSpecials(database, entry.getKey(), specialname, txt, cooldwn.init, cooldwn.max, specialnotes);
                    } else if (special != null) {
                        CoolDowns cooldwn = new CoolDowns();
                        if (entry.getKey() < cools_tmp.size())
                            cooldwn = cools_tmp.get(entry.getKey());
                        DBHelper.insertIntoSpecials(database, entry.getKey(), specialname, (String) special, cooldwn.init, cooldwn.max, specialnotes);
                    }

                    if(value.containsKey("limit")) {
                        if(value.get("limit").getClass().equals(NativeArray.class)) {
                            List<Map<String, String>> limit = (List<Map<String, String>>) value.get("limit");
                            String limitNotes = value.containsKey("limitnotes") ? (String) value.get("limitnotes") : "";
                            boolean alreadyAdded = false;
                            for (Map<String, String> limitEntry : limit) {
                                try {
                                    String description = limitEntry.get("description");
                                    if (!alreadyAdded) {
                                        DBHelper.insertIntoLimit(database, entry.getKey(), description, limitNotes);
                                        alreadyAdded = true;
                                    } else {
                                        DBHelper.insertIntoLimit(database, entry.getKey(), description, "");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (value.get("limit").getClass().equals(String.class)) {
                            String limit = (String) value.get("limit");
                            String limitNotes = notes_parser.parseNotes(value.containsKey("limitnotes") ? (String) value.get("limitnotes") : "");
                            DBHelper.insertIntoLimit(database, entry.getKey(), limit, limitNotes);
                        }
                    }

                    if(value.containsKey("potential")) {
                        List<HashMap<String,Object>> potential = (List<HashMap<String,Object>>)value.get("potential");
                        String potentialNotes = notes_parser.parseNotes(value.containsKey("potentialNotes") ? (String)value.get("potentialNotes") : "");
                        boolean alreadyAdded = false;
                        for(Map<String,Object> potentialEntry : potential) {
                            try {
                                String name = (String) potentialEntry.get("Name");
                                List<String> lvlS = (List<String>) potentialEntry.get("description");
                                String lvl1 = lvlS.get(0);
                                String lvl2 = lvlS.get(1);
                                String lvl3 = lvlS.get(2);
                                String lvl4 = lvlS.get(3);
                                String lvl5 = lvlS.get(4);
                                if (!alreadyAdded) {
                                    DBHelper.insertIntoPotential(database, entry.getKey(), name, lvl1, lvl2, lvl3,
                                            lvl4, lvl5, potentialNotes);
                                    alreadyAdded = true;
                                } else {
                                    DBHelper.insertIntoPotential(database, entry.getKey(), name, lvl1, lvl2, lvl3,
                                            lvl4, lvl5, "");
                                }
                            } catch (Exception e) {e.printStackTrace(); }
                        }
                    }
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    }

    static void populateEvolutions(SQLiteDatabase database, Map<Integer, Map> evolutions) {
        database.beginTransaction();
        try {
            for (Map.Entry<Integer, Map> entry : evolutions.entrySet()) {
                Map<String, Object> value = entry.getValue();
                Object evs = value.get("evolution");
                if (evs.getClass().equals(Double.class)) {
                    //1 evolution
                    List<Object> evolvers = (List<Object>) value.get("evolvers");
                    Integer[] evolvers_int = new Integer[5];
                    for (int i = 0; i < 5; i++) {
                        if (i < evolvers.size())
                            if(evolvers.get(i).getClass().equals(Double.class))
                                evolvers_int[i] = ((Double)evolvers.get(i)).intValue();
                            else if (evolvers.get(i).getClass().equals(String.class)) {
                                try {
                                    evolvers_int[i] = SkullsHelper.getSkullId((String) evolvers.get(i));
                                } catch (IOException e) {
                                    evolvers_int[i] = null;
                                    e.printStackTrace(); }
                            }
                        else evolvers_int[i] = null;
                    }
                    DBHelper.insertIntoEvolutions(database, entry.getKey(), ((Double) evs).intValue(), evolvers_int[0],
                            evolvers_int[1], evolvers_int[2], evolvers_int[3], evolvers_int[4]);
                } else if (evs.getClass().equals(NativeArray.class)) {
                    //multiple evolutions
                    List<Double> evs_list = (List<Double>) evs;
                    List<List<Double>> evolvers_list = (List<List<Double>>) value.get("evolvers");
                    for (int i = 0; i < evs_list.size(); i++) {
                        List<Double> evolvers = evolvers_list.get(i);
                        Integer[] evolvers_int = new Integer[5];
                        for (int n = 0; n < 5; n++) {
                            if (n < evolvers.size())
                                evolvers_int[n] = evolvers.get(n).intValue();
                            else evolvers_int[n] = null;
                        }
                        DBHelper.insertIntoEvolutions(database, entry.getKey(), evs_list.get(i).intValue(), evolvers_int[0],
                                evolvers_int[1], evolvers_int[2], evolvers_int[3], evolvers_int[4]);
                    }
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    static void populateDropLocations(SQLiteDatabase database, Map<String, List<Map>> drops, DropTypes dropTypes) {
        Pattern pattern = Pattern.compile("-?[0-9]+");
        database.beginTransaction();
        try {
            List<Map> story_entries = drops.get(dropTypes.DROPS_STORY);
            for (Map<String, Object> element : story_entries)
            {
                String location = (String)element.get("name");
                Integer thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                for (Map.Entry<String, Object> entry : element.entrySet())
                {
                    if (pattern.matcher(String.valueOf(entry.getKey())).matches()) {
                        // it's a chapter
                        List<Object> charIds = (List<Object>) entry.getValue();
                        for(Object charId_o : charIds)
                        {
                            if(charId_o.getClass().equals(Double.class)) {
                                Double charId = (Double)charId_o;
                                if (charId > 0)
                                    DBHelper.insertIntoDrops(database, charId.intValue(), location, String.valueOf(entry.getKey()), isGlobal, isJapan, thumb);
                            } else if (charId_o.getClass().equals(String.class)) {
                                String charId = (String)charId_o;
                                try {
                                    DBHelper.insertIntoDrops(database, SkullsHelper.getSkullId(charId), location, String.valueOf(entry.getKey()), isGlobal, isJapan, thumb);
                                } catch (IOException e) { DBHelper.insertIntoDrops(database, null, location, String.valueOf(entry.getKey()), isGlobal, isJapan, thumb); e.printStackTrace(); }
                            }
                        }
                    }
                }
                if(element.containsKey(dropTypes.DROP_COMPLETION))
                {
                    List<Double> compl_units = (List<Double>)element.get(dropTypes.DROP_COMPLETION);
                    for(Double i : compl_units)
                    {
                        if(i>0)
                            DBHelper.insertIntoDrops(database, i.intValue(), location, dropTypes.DROP_COMPLETION, isGlobal, isJapan, thumb);
                    }
                }
            }

            List<Map> weekly_entries = drops.get(dropTypes.DROPS_WEEKLY);
            for(Map<String, Object> element : weekly_entries)
            {
                List<Double> charIds = (List<Double>)element.get(" ");
                String drop_name = (String)element.get("name");
                Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                for(Double charId : charIds)
                {
                    if(charId>0)
                        DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "", isGlobal, isJapan, drop_thumb);
                }
            }

            List<Map> forts_entries = drops.get(dropTypes.DROPS_FORTNIGHT);
            for(Map<String, Object> element : forts_entries)
            {
                String drop_name = (String)element.get("name");
                Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                if(element.containsKey("Elite"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Elite");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Elite", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Expert"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Expert");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Expert", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey(dropTypes.DROP_ALLDIFFS))
                {
                    List<Double> eliteDrops = (List<Double>)element.get(dropTypes.DROP_ALLDIFFS);
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, dropTypes.DROP_ALLDIFFS, isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Global"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Global");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, dropTypes.DROP_ALLDIFFS, true, false, drop_thumb);
                    }
                }
                if(element.containsKey("Japan"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Japan");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, dropTypes.DROP_ALLDIFFS, false, true, drop_thumb);
                    }
                }
            }

            List<Map> raid_entries = drops.get(dropTypes.DROPS_RAID);
            for(Map<String, Object> element : raid_entries)
            {
                String drop_name = (String)element.get("name");
                Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                if(element.containsKey("Master"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Master");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Master", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Expert"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Expert");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Expert", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Ultimate"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Ultimate");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Ultimate", isGlobal, isJapan, drop_thumb);
                    }
                }
            }

            List<Map> special_entries = drops.get(dropTypes.DROPS_SPECIAL);
            for(Map<String, Object> element : special_entries)
            {
                String drop_name = (String)element.get("name");
                Integer drop_thumb = element.containsKey("thumb") ? ((element.get("thumb")==null) ? 0 : ((Double)element.get("thumb")).intValue()) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                if(element.containsKey(dropTypes.DROP_ALLDIFFS))
                {
                    List<Object> eliteDrops = (List<Object>)element.get(dropTypes.DROP_ALLDIFFS);
                    for(Object charId_o : eliteDrops)
                    {
                        if(charId_o.getClass().equals(Double.class)) {
                            Double charId = (Double)charId_o;
                            if (charId > 0)
                                DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, dropTypes.DROP_ALLDIFFS, isGlobal, isJapan, drop_thumb);
                        } else if (charId_o.getClass().equals(String.class)) {
                            String charId = (String)charId_o;
                            try {
                                DBHelper.insertIntoDrops(database, SkullsHelper.getSkullId(charId), drop_name, dropTypes.DROP_ALLDIFFS, isGlobal, isJapan, drop_thumb);
                            } catch (IOException e) { DBHelper.insertIntoDrops(database, null, drop_name, dropTypes.DROP_ALLDIFFS, isGlobal, isJapan, drop_thumb); e.printStackTrace(); }
                        }
                    }
                }
                if(element.containsKey(dropTypes.DROP_COMPLETION))
                {
                    List<Double> compl_units = (List<Double>)element.get(dropTypes.DROP_COMPLETION);
                    for(Double i : compl_units)
                    {
                        if(i>0)
                            DBHelper.insertIntoDrops(database, i.intValue(), drop_name, dropTypes.DROP_COMPLETION, isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Exhibition"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Exhibition");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Exhibition", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Underground"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Underground");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Underground", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Chaos"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Chaos");
                    for(Double charId : eliteDrops)
                    {
                        if(charId>0)
                            DBHelper.insertIntoDrops(database, charId.intValue(), drop_name, "Chaos", isGlobal, isJapan, drop_thumb);
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    static void populateManualLocations(SQLiteDatabase database, Map<String, List<Map>> drops, DropTypes dropTypes) {
        Pattern pattern = Pattern.compile("-?[0-9]+");
        database.beginTransaction();
        try {
            List<Map> story_entries = drops.get(dropTypes.DROPS_STORY);
            for (Map<String, Object> element : story_entries)
            {
                String location = (String)element.get("name");
                Integer thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                for (Map.Entry<String, Object> entry : element.entrySet())
                {
                    if (pattern.matcher(String.valueOf(entry.getKey())).matches()) {
                        // it's a chapter
                        List<Object> charIds = (List<Object>) entry.getValue();
                        for(Object charId_o : charIds)
                        {
                            if(charId_o.getClass().equals(Double.class)) {
                                Double charId = (Double) charId_o;
                                if (charId < 0)
                                    DBHelper.insertIntoManuals(database, -charId.intValue(), location, String.valueOf(entry.getKey()), isGlobal, isJapan, thumb);
                            } else if (charId_o.getClass().equals(String.class)) {
                                String charId = (String)charId_o;
                                //TODO: insert skull code
                            }
                        }
                    }
                }
                if(element.containsKey(dropTypes.DROP_COMPLETION))
                {
                    List<Double> compl_units = (List<Double>)element.get(dropTypes.DROP_COMPLETION);
                    for(Double i : compl_units)
                    {
                        if(i<0)
                            DBHelper.insertIntoManuals(database, -i.intValue(), location, dropTypes.DROP_COMPLETION, isGlobal, isJapan, thumb);
                    }
                }
            }

            List<Map> weekly_entries = drops.get(dropTypes.DROPS_WEEKLY);
            for(Map<String, Object> element : weekly_entries)
            {
                List<Double> charIds = (List<Double>)element.get(" ");
                String drop_name = (String)element.get("name");
                Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                for(Double charId : charIds)
                {
                    if(charId<0)
                        DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "", isGlobal, isJapan, drop_thumb);
                }
            }

            List<Map> forts_entries = drops.get(dropTypes.DROPS_FORTNIGHT);
            for(Map<String, Object> element : forts_entries)
            {
                String drop_name = (String)element.get("name");
                Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                if(element.containsKey("Elite"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Elite");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "Elite", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Expert"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Expert");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "Expert", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey(dropTypes.DROP_ALLDIFFS))
                {
                    List<Double> eliteDrops = (List<Double>)element.get(dropTypes.DROP_ALLDIFFS);
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, dropTypes.DROP_ALLDIFFS, isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Global"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Global");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, dropTypes.DROP_ALLDIFFS, true, false, drop_thumb);
                    }
                }
                if(element.containsKey("Japan"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Japan");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, dropTypes.DROP_ALLDIFFS, false, true, drop_thumb);
                    }
                }
            }

            List<Map> raid_entries = drops.get(dropTypes.DROPS_RAID);
            for(Map<String, Object> element : raid_entries)
            {
                String drop_name = (String)element.get("name");
                Integer drop_thumb = element.containsKey("thumb")? (element.get("thumb")!=null?((Double)element.get("thumb")).intValue():0) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                if(element.containsKey("Master"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Master");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "Master", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Expert"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Expert");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "Expert", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Ultimate"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Ultimate");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "Ultimate", isGlobal, isJapan, drop_thumb);
                    }
                }
            }

            List<Map> special_entries = drops.get(dropTypes.DROPS_SPECIAL);
            for(Map<String, Object> element : special_entries)
            {
                String drop_name = (String)element.get("name");
                Integer drop_thumb = element.containsKey("thumb") ? ((element.get("thumb")==null) ? 0 : ((Double)element.get("thumb")).intValue()) : 0;
                Boolean isGlobal = element.containsKey("global")? (Boolean)element.get("global") : false;
                Boolean isJapan = true;
                if(element.containsKey(dropTypes.DROP_ALLDIFFS))
                {
                    List<Object> eliteDrops = (List<Object>)element.get(dropTypes.DROP_ALLDIFFS);
                    for(Object charId_o : eliteDrops)
                    {
                        if(charId_o.getClass().equals(Double.class)) {
                            Double charId = (Double) charId_o;
                            if (charId < 0)
                                DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, dropTypes.DROP_ALLDIFFS, isGlobal, isJapan, drop_thumb);
                        } else if (charId_o.getClass().equals(String.class)) {
                            String charId = (String)charId_o;
                            //TODO: insert skull code
                        }
                    }
                }
                if(element.containsKey(dropTypes.DROP_COMPLETION))
                {
                    List<Double> compl_units = (List<Double>)element.get(dropTypes.DROP_COMPLETION);
                    for(Double i : compl_units)
                    {
                        if(i<0)
                            DBHelper.insertIntoManuals(database, -i.intValue(), drop_name, dropTypes.DROP_COMPLETION, isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Exhibition"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Exhibition");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "Exhibition", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Underground"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Underground");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "Underground", isGlobal, isJapan, drop_thumb);
                    }
                }
                if(element.containsKey("Chaos"))
                {
                    List<Double> eliteDrops = (List<Double>)element.get("Chaos");
                    for(Double charId : eliteDrops)
                    {
                        if(charId<0)
                            DBHelper.insertIntoManuals(database, -charId.intValue(), drop_name, "Chaos", isGlobal, isJapan, drop_thumb);
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }
}
