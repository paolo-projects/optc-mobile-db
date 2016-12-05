package it.instruman.treasurecruisedatabase;

/**
 * Created by Paolo on 03/12/2016.
 */

public class DropTypes {
    public String DROPS_STORY = "Story Island";
    public String DROPS_WEEKLY = "Weekly Island";
    public String DROPS_FORTNIGHT = "Fortnight";
    public String DROPS_RAID = "Raid";
    public String DROPS_SPECIAL = "Special";

    public String DROP_COMPLETION = "Completion Units";
    public String DROP_ALLDIFFS = "All Difficulties";

    public DropTypes() { }

    public DropTypes( String DROPS_STORY, String DROPS_WEEKLY, String DROPS_FORTNIGHT,  String DROPS_RAID, String DROPS_SPECIAL, String DROP_COMPLETION, String DROP_ALLDIFFS ) {
        this.DROP_ALLDIFFS = DROP_ALLDIFFS;
        this.DROP_COMPLETION = DROP_COMPLETION;
        this.DROPS_FORTNIGHT = DROPS_FORTNIGHT;
        this.DROPS_RAID = DROPS_RAID;
        this.DROPS_SPECIAL = DROPS_SPECIAL;
        this.DROPS_STORY = DROPS_STORY;
        this.DROPS_WEEKLY = DROPS_WEEKLY;
    }
}
