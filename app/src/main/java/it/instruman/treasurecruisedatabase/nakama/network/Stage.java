package it.instruman.treasurecruisedatabase.nakama.network;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by infan on 20/02/2018.
 */

public class Stage extends RealmObject {
    @PrimaryKey
    private int stageId;
    private String stageName;

    public Stage(int stageId, String stageName) {
        this.stageId = stageId;
        this.stageName = stageName;
    }

    public Stage() {
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
}
