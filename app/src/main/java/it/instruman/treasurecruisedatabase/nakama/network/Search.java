package it.instruman.treasurecruisedatabase.nakama.network;

/**
 * Created by infan on 18/02/2018.
 */

public abstract class Search {
    CommunicationHandler.BuildQuery buildQuery;
    Search() {

    }

    public CommunicationHandler.BuildQuery buildQuery() {
        return buildQuery;
    }
}
