package it.instruman.treasurecruisedatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by infan on 06/10/2017.
 */

/*
    EXAMPLE of LinkedHashMap

    0: "Enrage" -> 0:| "Level 1: blabla"
                   1:| "Level 2: blabla"
                   2:| "Level 3: blabla"
                   3:| "Level 4: blabla"
                   4:| "Level 5: blabla"

    1: "Reduce No Healing" -> SAME AS BEFORE
 */

public class Potentials {
    private LinkedHashMap<String, ArrayList<String>> potentialEntries = null;
    private String potentialNotes = null;

    public Potentials(LinkedHashMap<String, ArrayList<String>> potentialEntries, String potentialNotes) {
        this.potentialEntries = potentialEntries;
        this.potentialNotes = potentialNotes;
    }

    public Potentials() {
    }

    public LinkedHashMap<String, ArrayList<String>> getPotentialEntries() {
        return potentialEntries;
    }

    public void setPotentialEntries(LinkedHashMap<String, ArrayList<String>> potentialEntries) {
        this.potentialEntries = potentialEntries;
    }

    public String getPotentialNotes() {
        return potentialNotes;
    }

    public void setPotentialNotes(String potentialNotes) {
        this.potentialNotes = potentialNotes;
    }
}
