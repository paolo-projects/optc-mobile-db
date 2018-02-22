package it.instruman.treasurecruisedatabase.nakama.network;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by infan on 18/02/2018.
 */

public class Team extends RealmObject {

    @PrimaryKey
    private Integer ID;

    private String name;
    private String submittedBy;
    private Integer shipId;
    private String shipName;
    private Integer stageId;
    private RealmList<Unit> teamUnits;

    public Team() {
    }

    public Team(Integer ID, String name, String submittedBy, Integer shipId, String shipName, Integer stageId, RealmList<Unit> teamUnits) {
        this.ID = ID;
        this.name = name;
        this.submittedBy = submittedBy;
        this.shipId = shipId;
        this.shipName = shipName;
        this.stageId = stageId;
        this.teamUnits = teamUnits;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Integer getShipId() {
        return shipId;
    }

    public void setShipId(Integer shipId) {
        this.shipId = shipId;
    }

    public Integer getStageId() {
        return stageId;
    }

    public void setStageId(Integer stageId) {
        this.stageId = stageId;
    }

    public RealmList<Unit> getTeamUnits() {
        return teamUnits;
    }

    public void setTeamUnits(RealmList<Unit> teamUnits) {
        this.teamUnits = teamUnits;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }
}
