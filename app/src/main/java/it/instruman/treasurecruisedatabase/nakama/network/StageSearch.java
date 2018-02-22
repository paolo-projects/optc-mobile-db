package it.instruman.treasurecruisedatabase.nakama.network;

/**
 * Created by infan on 18/02/2018.
 */

class StageSearch extends Search implements CommunicationHandler.IStageSearchModel {
    public static StageSearch with(CommunicationHandler.BuildQuery buildQuery) {
        StageSearch teamSearch = new StageSearch();
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
    public CommunicationHandler.IStageSearchModel type(CommunicationHandler.StageType value) {
        buildQuery.appendQuery("type=" + value.getValue());
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IStageSearchModel term(String value) {
        buildQuery.appendQuery("term=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IStageSearchModel global(Boolean value) {
        buildQuery.appendQuery("global=" + value);
        buildQuery.appendQuery("&");
        return this;
    }
}
