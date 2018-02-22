package it.instruman.treasurecruisedatabase.nakama.network;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by infan on 18/02/2018.
 */

public class ContentRetriever {
    private String searchQuery;
    private String resultData;

    private ContentRetriever() {

    }

    /**
     * Initialize the ContentRetriever with a search query and then call the query() method to retrieve data from the server
     * @param searchQuery Search object with your query
     * @return An instance of ContentRetriever object
     */
    public static ContentRetriever with(Search searchQuery) {
        ContentRetriever contentRetriever = new ContentRetriever();
        contentRetriever.searchQuery = searchQuery.buildQuery().getQuery();
        return contentRetriever;
    }

    public static ContentRetriever with(CommunicationHandler.BuildQuery buildQuery) {
        ContentRetriever contentRetriever = new ContentRetriever();
        contentRetriever.searchQuery = buildQuery.getQuery();
        return contentRetriever;
    }

    /**
     * Send the HTTP query and retrieve the results. Call getResultData() to retrieve JSON encoded results
     * @return The instance of this object
     */
    public ContentRetriever query() {
        if(!searchQuery.isEmpty()) {
            try {
                URL connectionURL = new URL(searchQuery);
                InputStream is = connectionURL.openStream();
                resultData = CharStreams.toString(new InputStreamReader(is));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("The URL passed is invalid");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            throw new UnsupportedOperationException("You need to provide a Search query first");
        return this;
    }

    /**
     * Get JSON-encoded result string from the url
     * @return JSON encoded String
     */
    public String getResultData() {
        return resultData;
    }
}
