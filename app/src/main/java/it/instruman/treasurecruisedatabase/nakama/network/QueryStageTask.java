package it.instruman.treasurecruisedatabase.nakama.network;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by infan on 20/02/2018.
 */

public class QueryStageTask extends AsyncTask<String, Void, List<Stage>> {
    public interface TaskResult {
        void onResultsAvailable(List<Stage> results);
    }

    private TaskResult taskResult;

    /**
     * Instantiate an object to asynchronously query a list of stages corresponding to the name provided as the first argument of the execute(String...) method
     * @param taskResultCallback A callback to retrieve the list of stages when the query is done.
     */
    public QueryStageTask(TaskResult taskResultCallback) {
        taskResult = taskResultCallback;
    }

    @Override
    protected void onPostExecute(List<Stage> stages) {
        taskResult.onResultsAvailable(stages);
    }

    @Override
    protected List<Stage> doInBackground(String... stageName) {
        return NNHelper.getStagesByName(stageName[0]);
    }
}
