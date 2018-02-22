package it.instruman.treasurecruisedatabase.nakama.network;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by infan on 20/02/2018.
 */

public class QueriedId extends RealmObject {

    @PrimaryKey
    private int leaderId;

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public int getLeaderId() {
        return leaderId;
    }
}
