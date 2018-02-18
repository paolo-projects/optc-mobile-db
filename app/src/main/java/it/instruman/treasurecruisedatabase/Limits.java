package it.instruman.treasurecruisedatabase;

import java.util.ArrayList;

/**
 * Created by infan on 06/10/2017.
 */

public class Limits {
    private ArrayList<String> limitEntries = null;
    private String limitNotes = null;

    public Limits(ArrayList<String> limitEntries, String limitNotes) {
        this.limitEntries = limitEntries;
        this.limitNotes = limitNotes;
    }

    public Limits() {
    }

    public ArrayList<String> getLimitEntries() {
        return limitEntries;
    }

    public void setLimitEntries(ArrayList<String> limitEntries) {
        this.limitEntries = limitEntries;
    }

    public String getLimitNotes() {
        return limitNotes;
    }

    public void setLimitNotes(String limitNotes) {
        this.limitNotes = limitNotes;
    }
}
