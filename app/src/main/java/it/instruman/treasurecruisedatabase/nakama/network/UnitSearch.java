package it.instruman.treasurecruisedatabase.nakama.network;

/**
 * Created by infan on 18/02/2018.
 */

class UnitSearch extends Search implements CommunicationHandler.IUnitSearchModel {

    public static UnitSearch with(CommunicationHandler.BuildQuery buildQuery) {
        UnitSearch unitSearch = new UnitSearch();
        unitSearch.buildQuery = buildQuery;
        return unitSearch;
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
    public CommunicationHandler.IUnitSearchModel term(String value) {
        buildQuery.appendQuery("term=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel classes(CommunicationHandler.UnitClass value) {
        buildQuery.appendQuery("classes=" + value.getValue());
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel classes(CommunicationHandler.UnitClass... values) {
        int result = 0;
        for (CommunicationHandler.UnitClass u : values)
            result |= u.getValue();
        buildQuery.appendQuery("classes=" + result);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel types(CommunicationHandler.UnitType value) {
        buildQuery.appendQuery("types=" + value.getValue());
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel types(CommunicationHandler.UnitType... values) {
        int result = 0;
        for (CommunicationHandler.UnitType u : values)
            result |= u.getValue();
        buildQuery.appendQuery("types=" + result);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel forceClass(Boolean value) {
        buildQuery.appendQuery("forceClass=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel freeToPlay(Boolean value) {
        buildQuery.appendQuery("freeToPlay=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel global(Boolean value) {
        buildQuery.appendQuery("global=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel boxId(Integer value) {
        buildQuery.appendQuery("boxId=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IUnitSearchModel blacklist(Boolean value) {
        buildQuery.appendQuery("blacklist=" + value);
        buildQuery.appendQuery("&");
        return this;
    }
}
