package it.instruman.treasurecruisedatabase.nakama.network;

/**
 * Created by infan on 18/02/2018.
 */

class ProfileSearch extends Search implements CommunicationHandler.IProfileSearchModel {
    public static ProfileSearch with(CommunicationHandler.BuildQuery buildQuery) {
        ProfileSearch teamSearch = new ProfileSearch();
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
    public CommunicationHandler.IProfileSearchModel term(String value) {
        buildQuery.appendQuery("term=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IProfileSearchModel roles(String[] value) {
        buildQuery.appendQuery("roles=" + value);
        buildQuery.appendQuery("&");
        return this;
    }
}
