package it.instruman.treasurecruisedatabase.nakama.network;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by infan on 18/02/2018.
 */

public class NNHelper {
    Integer totalEntries;

    public static final int ENTRIES_MAX_NUMBER = 20;

    /**
     * Queries and returns a list of teams that match the given parameters
     *
     * @param leaderId the unit ID of the team captain
     * @return A list of Team objects
     */
    public ArrayList<Team> getTeamsByLeaderIdUsingNetwork(Integer leaderId) {
        CommunicationHandler.BuildQuery bQ = CommunicationHandler.BuildQuery.newInstance(CommunicationHandler.Controller.team, CommunicationHandler.EndPoint.search, null);
        String result = ContentRetriever.with((Search) TeamSearch.with(bQ)
                .leaderId(leaderId)
                .pageSize(ENTRIES_MAX_NUMBER)
                .sortBy("Date")
                .sortDesc(false))
                .query().getResultData();
        Realm realm = Realm.getDefaultInstance();
        try {
            JSONObject mainObject = new JSONObject(result);
            totalEntries = mainObject.getInt("totalResults");
            JSONArray mainArray = mainObject.getJSONArray("results");
            ArrayList<Team> returnList = new ArrayList<>();
            for (int i = 0; i < mainArray.length(); i++) {
                JSONObject entry = mainArray.getJSONObject(i);
                Team entryTeam = new Team();
                entryTeam.setID(entry.getInt("id"));
                entryTeam.setName(entry.getString("name"));
                entryTeam.setSubmittedBy(entry.getString("submittedByName"));

                if (!entry.isNull("shipId")) {
                    int shipId = entry.getInt("shipId");
                    entryTeam.setShipId(shipId);
                    Ship shipResult = realm.where(Ship.class).equalTo("Id", shipId).findFirst();
                    entryTeam.setShipName(shipResult != null ? shipResult.getName() : "");
                }
                entryTeam.setStageId(entry.isNull("stageId") ? null : entry.getInt("stageId"));
                TreeMap<Integer, Unit> units = new TreeMap<>();

                JSONArray teamUnits = entry.getJSONArray("teamUnits");
                JSONArray teamGenericSlots = entry.getJSONArray("teamGenericSlots");

                for (int n = 0; n < teamUnits.length(); n++) {
                    JSONObject unitEntry = teamUnits.getJSONObject(n);
                    Unit unit = new Unit(false, unitEntry.getInt("unitId"), null, null, null, unitEntry.getInt("position"));
                    units.put(unitEntry.getInt("position"), unit);
                }
                for (int n = 0; n < teamGenericSlots.length(); n++) {
                    JSONObject genericSlotsEntry = teamGenericSlots.getJSONObject(n);
                    EnumSet<CommunicationHandler.UnitClass> genericSlotClasses = CommunicationHandler.UnitClass.getEntries(genericSlotsEntry.getInt("class"));
                    Unit unit = new Unit(true, null, CommunicationHandler.UnitRole.getEntry(genericSlotsEntry.getInt("role")),
                            CommunicationHandler.UnitType.getEntry(genericSlotsEntry.getInt("type")),
                            genericSlotClasses, genericSlotsEntry.getInt("position"));
                    units.put(genericSlotsEntry.getInt("position"), unit);
                }

                entryTeam.setTeamUnits(getRealmListFromMap(units));
                returnList.add(entryTeam);
            }
            return returnList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public ArrayList<Team> getTeamsByLeaderIdAndStageId(Integer leaderId, Integer stageId) {
        CommunicationHandler.BuildQuery bQ = CommunicationHandler.BuildQuery.newInstance(CommunicationHandler.Controller.team, CommunicationHandler.EndPoint.search, null);
        String result = ContentRetriever.with((Search) TeamSearch.with(bQ)
                .leaderId(leaderId)
                .stageId(stageId)
                .pageSize(ENTRIES_MAX_NUMBER)
                .sortBy("Date")
                .sortDesc(false))
                .query().getResultData();
        Realm realm = Realm.getDefaultInstance();
        try {
            JSONObject mainObject = new JSONObject(result);
            totalEntries = mainObject.getInt("totalResults");
            JSONArray mainArray = mainObject.getJSONArray("results");
            ArrayList<Team> returnList = new ArrayList<>();
            for (int i = 0; i < mainArray.length(); i++) {
                JSONObject entry = mainArray.getJSONObject(i);
                Team entryTeam = new Team();
                entryTeam.setID(entry.getInt("id"));
                entryTeam.setName(entry.getString("name"));
                entryTeam.setSubmittedBy(entry.getString("submittedByName"));

                if (!entry.isNull("shipId")) {
                    int shipId = entry.getInt("shipId");
                    entryTeam.setShipId(shipId);
                    Ship shipResult = realm.where(Ship.class).equalTo("Id", shipId).findFirst();
                    entryTeam.setShipName(shipResult != null ? shipResult.getName() : "");
                }
                entryTeam.setStageId(entry.isNull("stageId") ? null : entry.getInt("stageId"));
                TreeMap<Integer, Unit> units = new TreeMap<>();

                JSONArray teamUnits = entry.getJSONArray("teamUnits");
                JSONArray teamGenericSlots = entry.getJSONArray("teamGenericSlots");

                for (int n = 0; n < teamUnits.length(); n++) {
                    JSONObject unitEntry = teamUnits.getJSONObject(n);
                    Unit unit = new Unit(false, unitEntry.getInt("unitId"), null, null, null, unitEntry.getInt("position"));
                    units.put(unitEntry.getInt("position"), unit);
                }
                for (int n = 0; n < teamGenericSlots.length(); n++) {
                    JSONObject genericSlotsEntry = teamGenericSlots.getJSONObject(n);
                    EnumSet<CommunicationHandler.UnitClass> genericSlotClasses = CommunicationHandler.UnitClass.getEntries(genericSlotsEntry.getInt("class"));
                    Unit unit = new Unit(true, null, CommunicationHandler.UnitRole.getEntry(genericSlotsEntry.getInt("role")),
                            CommunicationHandler.UnitType.getEntry(genericSlotsEntry.getInt("type")),
                            genericSlotClasses, genericSlotsEntry.getInt("position"));
                    units.put(genericSlotsEntry.getInt("position"), unit);
                }

                entryTeam.setTeamUnits(getRealmListFromMap(units));
                returnList.add(entryTeam);
            }
            return returnList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public static List<Team> getTeamsByLeaderIdUsingCache(Integer leaderId) {
        List<Team> resultsList;
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<Team> results = realm.where(Team.class).equalTo("teamUnits.unitId", leaderId).
                    and().
                    beginGroup().equalTo("teamUnits.position", 0).or().equalTo("teamUnits.position", 1).endGroup().
                    findAll();
            resultsList = realm.copyFromRealm(results);
        } finally {
            if (realm != null)
                realm.close();
        }
        return resultsList;
    }

    public static void addToRealm(List<Team> entries, int leaderId) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(entries);
            QueriedId queriedId = new QueriedId();
            queriedId.setLeaderId(leaderId);
            realm.insertOrUpdate(queriedId);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
            e.printStackTrace();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public Integer getTotalEntries() {
        return totalEntries;
    }

    public static List<Ship> getShipList() {
        CommunicationHandler.BuildQuery bQ = CommunicationHandler.BuildQuery.newInstance(CommunicationHandler.Controller.ship, CommunicationHandler.EndPoint.search, null);
        String results = ContentRetriever.with(bQ).query().getResultData();
        try {
            JSONArray shipsResult = new JSONArray(results);
            List<Ship> shipList = new ArrayList<Ship>();
            for (int i = 0; i < shipsResult.length(); i++) {
                JSONObject ship = shipsResult.getJSONObject(i);
                Ship newShip = new Ship(ship.getInt("id"), ship.getString("name"), ship.getBoolean("eventShip"), ship.getBoolean("eventShipActive"));
                shipList.add(newShip);
            }
            return shipList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static String getStageName(int stageId) {
        Realm realm = Realm.getDefaultInstance();
        String stageName = null;
        try {
            RealmQuery<Stage> query = realm.where(Stage.class).equalTo("stageId", stageId);
            if (query.count() > 0) {
                stageName = query.findFirst().getStageName();
            } else {
                CommunicationHandler.BuildQuery bQ = CommunicationHandler.BuildQuery.newInstance(CommunicationHandler.Controller.stage, CommunicationHandler.EndPoint.get, stageId);
                String results = ContentRetriever.with(bQ).query().getResultData();
                try {
                    JSONObject resObj = new JSONObject(results);
                    if (!resObj.isNull("name")) {
                        stageName = resObj.getString("name");
                        realm.beginTransaction();
                        Stage stage = new Stage(stageId, stageName);
                        realm.insertOrUpdate(stage);
                        realm.commitTransaction();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (realm != null)
                realm.close();
        }
        return stageName;
    }

    public static String getViewTeamURL(int teamId) {
        return "https://www.nakama.network/teams/" + String.valueOf(teamId) +
                "/details";
    }

    public static String getViewMoreURL(int leaderId) {
        return "https://www.nakama.network/teams?leaderId=" + String.valueOf(leaderId);
    }

    public static String getViewMoreWithStageURL(int leaderId, int stageId) {
        return "https://www.nakama.network/teams?leaderId=" + String.valueOf(leaderId) + "&stageId=" + String.valueOf(stageId);
    }

    public static TreeMap<Integer, Unit> getOrderedUnitList(RealmList<Unit> list) {
        TreeMap<Integer, Unit> result = new TreeMap<>();
        for (Unit unit : list) {
            int pos = unit.getPosition();
            result.put(pos, unit);
        }
        return result;
    }

    public static void buildShipsIfNeeded() {
        Realm realm = Realm.getDefaultInstance();
        try {
            long resCount = realm.where(Ship.class).count();
            if (resCount == 0) {
                List<Ship> shipList = getShipList();
                if (shipList != null && shipList.size() > 0) {
                    realm.beginTransaction();
                    try {
                        realm.insertOrUpdate(shipList);
                        realm.commitTransaction();
                    } catch (Exception e) {
                        realm.cancelTransaction();
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private static RealmList<Unit> getRealmListFromMap(TreeMap<Integer, Unit> unitsMap) {
        RealmList<Unit> resultList = new RealmList<>();
        for (Map.Entry<Integer, Unit> entry : unitsMap.entrySet()) {
            resultList.add(entry.getValue());
        }
        return resultList;
    }

    public static boolean hasIdBeenQueried(int leaderId) {
        Realm realm = Realm.getDefaultInstance();
        try {
            return realm.where(QueriedId.class).equalTo("leaderId", leaderId).count()>0;
        } finally {
            if(realm!=null)
                realm.close();
        }
    }


    public static List<Stage> getStagesByName(String stageNameQuery) {
        CommunicationHandler.BuildQuery buildQuery = CommunicationHandler.BuildQuery.newInstance(CommunicationHandler.Controller.stage, CommunicationHandler.EndPoint.search, null);
        String stagesJSON = ContentRetriever.with((Search)StageSearch.with(buildQuery).term(stageNameQuery)).query().getResultData();
        List<Stage> stagesList = new ArrayList<>();
        try {
            JSONObject stages = new JSONObject(stagesJSON);
            int totalResults = stages.getInt("totalResults");
            if(totalResults>0) {
                JSONArray stagesArray = stages.getJSONArray("results");
                for(int i = 0; i<stagesArray.length(); i++) {
                    JSONObject stage = stagesArray.getJSONObject(i);
                    int stageId = stage.getInt("id");
                    String stageName = stage.getString("name");
                    Stage stageEntry = new Stage(stageId, stageName);
                    stagesList.add(stageEntry);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return stagesList;
    }
}
