package it.instruman.treasurecruisedatabase;

/**
 * Created by Paolo on 12/11/2016.
 */

public class DropInfo {
    private Integer charID = null;
    private String dropLocation = null;
    private String dropChapterOrDifficulty = null;
    private boolean isGlobal = false;
    private boolean isJapan = false;
    private Integer dropThumbnail = null;

    public Integer getCharID() {
        return charID;
    }

    public void setCharID(Integer charID) {
        this.charID = charID;
    }

    public String getDropChapterOrDifficulty() {
        return dropChapterOrDifficulty;
    }

    public void setDropChapterOrDifficulty(String dropChapterOrDifficulty) {
        this.dropChapterOrDifficulty = dropChapterOrDifficulty;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public Integer getDropThumbnail() {
        return dropThumbnail;
    }

    public void setDropThumbnail(Integer dropThumbnail) {
        this.dropThumbnail = dropThumbnail;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public boolean isJapan() {
        return isJapan;
    }

    public void setJapan(boolean japan) {
        isJapan = japan;
    }

    public DropInfo(Integer charID, String dropChapterOrDifficulty, String dropLocation, Integer dropThumbnail, boolean isGlobal, boolean isJapan) {
        this.charID = charID;
        this.dropChapterOrDifficulty = dropChapterOrDifficulty;
        this.dropLocation = dropLocation;
        this.dropThumbnail = dropThumbnail;
        this.isGlobal = isGlobal;
        this.isJapan = isJapan;
    }

    public DropInfo() {
    }
}
