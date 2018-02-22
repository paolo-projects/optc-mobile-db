package it.instruman.treasurecruisedatabase.nakama.network;

/**
 * Created by infan on 18/02/2018.
 */

class ShipSearch extends Search implements CommunicationHandler.IShipSearchModel {
    public static ShipSearch with(CommunicationHandler.BuildQuery buildQuery) {
        ShipSearch teamSearch = new ShipSearch();
        teamSearch.buildQuery = buildQuery;
        return teamSearch;
    }

    @Override
    public CommunicationHandler.IShipSearchModel ID(int value) {
        buildQuery.appendQuery("Id=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IShipSearchModel name(String value) {
        buildQuery.appendQuery("Name=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IShipSearchModel eventShip(Boolean value) {
        buildQuery.appendQuery("EventShip=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.IShipSearchModel eventShipActive(Boolean value) {
        buildQuery.appendQuery("EventShipActive=" + value);
        buildQuery.appendQuery("&");
        return this;
    }
}
