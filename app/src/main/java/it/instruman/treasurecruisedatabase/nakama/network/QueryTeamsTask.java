package it.instruman.treasurecruisedatabase.nakama.network;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by infan on 20/02/2018.
 */

public class QueryTeamsTask extends AsyncTask<Integer, Void, QueryTeamsTask.QueryResults> {
    public interface TaskResult {
        void onResultsAvailable(List<Team> results, int totalEntries);
    }

    private TaskResult taskResult;

    /**
     * Queries the web server for a list of teams where the first parameter of the execute(Integer ...) method is the leader id to search for
     *
     * @param taskResult A callback fired as soon the results are available
     */
    public QueryTeamsTask(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    @Override
    protected void onPostExecute(QueryResults teams) {
        taskResult.onResultsAvailable(teams.getTeamResults(), teams.getTotalEntries());
    }

    @Override
    protected QueryResults doInBackground(Integer... leaderId) {
        NNHelper.buildShipsIfNeeded();
        List<Team> cachedResults = NNHelper.getTeamsByLeaderIdUsingCache(leaderId[0]);
        if(cachedResults!=null && cachedResults.size()>0 && NNHelper.hasIdBeenQueried(leaderId[0]))
            return new QueryResults(cachedResults, cachedResults.size()+1);
        else {
            NNHelper nnHelper = new NNHelper();
            List<Team> results = nnHelper.getTeamsByLeaderIdUsingNetwork(leaderId[0]);
            NNHelper.addToRealm(results, leaderId[0]);
            return new QueryResults(results, nnHelper.getTotalEntries());
        }
    }

    class QueryResults {
        List<Team> teamResults;
        int totalEntries;

        QueryResults(List<Team> teamResults, int totalEntries) {
            this.teamResults = teamResults;
            this.totalEntries = totalEntries;
        }

        List<Team> getTeamResults() {
            return teamResults;
        }

        int getTotalEntries() {
            return totalEntries;
        }
    }
}
