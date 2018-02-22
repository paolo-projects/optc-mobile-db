package it.instruman.treasurecruisedatabase.nakama.network;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by infan on 20/02/2018.
 */

public class QueryTeamsByStageTask extends AsyncTask<Integer, Void, QueryTeamsByStageTask.QueryResults> {
    public interface TaskResult {
        void onResultsAvailable(List<Team> results, int totalEntries, int stageId);
    }

    private TaskResult taskResult;

    /**
     * Queries the web server for a list of teams where the first parameter of the execute(Integer ...) method is the leader id to search for,
     * and the second parameter is the stage id to refine the search.
     *
     * @param taskResult A callback fired as soon the results are available
     */
    public QueryTeamsByStageTask(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    @Override
    protected void onPostExecute(QueryResults teams) {
        taskResult.onResultsAvailable(teams.getTeamResults(), teams.getTotalEntries(), teams.getStageId());
    }

    @Override
    protected QueryResults doInBackground(Integer... ids) {
        if(ids.length!=2)
            throw new IllegalArgumentException("Arguments passed through .execute method must be 2. The first one is the leader id and the second one is the stage id.");
        NNHelper.buildShipsIfNeeded();
        NNHelper nnHelper = new NNHelper();
        List<Team> results = nnHelper.getTeamsByLeaderIdAndStageId(ids[0], ids[1]);
        return new QueryResults(results, nnHelper.getTotalEntries(), ids[1]);
    }

    class QueryResults {
        List<Team> teamResults;
        int totalEntries;
        int stageId;

        QueryResults(List<Team> teamResults, int totalEntries, int stageId) {
            this.teamResults = teamResults;
            this.totalEntries = totalEntries;
            this.stageId = stageId;
        }

        List<Team> getTeamResults() {
            return teamResults;
        }

        int getTotalEntries() {
            return totalEntries;
        }

        public int getStageId() {
            return stageId;
        }
    }
}
