package it.instruman.treasurecruisedatabase.nakama.network;

/**
 * Created by infan on 18/02/2018.
 */

class BoxSearch extends Search implements CommunicationHandler.IBoxSearchModel {
    public static BoxSearch with(CommunicationHandler.BuildQuery buildQuery) {
        BoxSearch teamSearch = new BoxSearch();
        teamSearch.buildQuery = buildQuery;
        return teamSearch;
    }

    @Override
    public CommunicationHandler.ISearchModel page(Integer value) {
        buildQuery.appendQuery("page=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ISearchModel pageSize(Integer value) {
        buildQuery.appendQuery("pageSize=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ISearchModel sortBy(String value) {
        buildQuery.appendQuery("sortBy=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ISearchModel sortDesc(Boolean value) {
        buildQuery.appendQuery("sortDesc=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IBoxSearchModel userId(String value) {
        buildQuery.appendQuery("userId=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IBoxSearchModel blacklist(Boolean value) {
        buildQuery.appendQuery("blacklist=" + value);
        buildQuery.appendQuery("&");
        return this;
    }
}
