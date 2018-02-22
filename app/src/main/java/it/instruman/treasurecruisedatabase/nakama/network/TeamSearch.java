package it.instruman.treasurecruisedatabase.nakama.network;

/**
 * Created by infan on 18/02/2018.
 */

class TeamSearch extends Search implements CommunicationHandler.ITeamSearchModel {

    public static TeamSearch with(CommunicationHandler.BuildQuery buildQuery) {
        TeamSearch teamSearch = new TeamSearch();
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
    public CommunicationHandler.ITeamSearchModel submittedBy(String value) {
        buildQuery.appendQuery("submittedBy=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel leaderId(Integer value) {
        buildQuery.appendQuery("leaderId=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel noHelp(Boolean value) {
        buildQuery.appendQuery("noHelp=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel stageId(Integer value) {
        buildQuery.appendQuery("stageId=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel freeToPlay(CommunicationHandler.FreeToPlayStatus value) {
        buildQuery.appendQuery("freeToPlay=" + value.getValue());
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel deleted(Boolean value) {
        buildQuery.appendQuery("deleted=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel draft(Boolean value) {
        buildQuery.appendQuery("draft=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel reported(Boolean value) {
        buildQuery.appendQuery("reported=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel bookmark(Boolean value) {
        buildQuery.appendQuery("bookmark=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel term(String value) {
        buildQuery.appendQuery("term=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel boxId(Integer value) {
        buildQuery.appendQuery("boxId=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel blacklist(Boolean value) {
        buildQuery.appendQuery("blacklist=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel global(Boolean value) {
        buildQuery.appendQuery("global=" + value);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel classes(CommunicationHandler.UnitClass value) {
        buildQuery.appendQuery("classes=" + value.getValue());
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel classes(CommunicationHandler.UnitClass... values) {
        int result = 0;
        for(CommunicationHandler.UnitClass c : values)
            result |= c.getValue();
        buildQuery.appendQuery("classes=" + result);
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel types(CommunicationHandler.UnitType value) {
        buildQuery.appendQuery("types=" + value.getValue());
        buildQuery.appendQuery("&");
        return this;
    }

    @Override
    public CommunicationHandler.ITeamSearchModel types(CommunicationHandler.UnitType... values) {
        int result = 0;
        for(CommunicationHandler.UnitType t : values)
            result |= t.getValue();
        buildQuery.appendQuery("classes=" + result);
        buildQuery.appendQuery("&");
        return this;
    }
}
