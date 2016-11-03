package it.instruman.treasurecruisedatabase;

/**
 * Created by Paolo on 01/11/2016.
 */

public class CharacterSpecials {
    private Integer SpecialNumber = null;
    private String SpecialDescription = null;
    private Integer MaxCooldown = null;
    private Integer MinCooldown = null;

    public Integer getMaxCooldown() {
        return MaxCooldown;
    }

    public void setMaxCooldown(Integer maxCooldown) {
        MaxCooldown = maxCooldown;
    }

    public Integer getMinCooldown() {
        return MinCooldown;
    }

    public void setMinCooldown(Integer minCooldown) {
        MinCooldown = minCooldown;
    }

    public String getSpecialDescription() {
        return SpecialDescription;
    }

    public void setSpecialDescription(String specialDescription) {
        SpecialDescription = specialDescription;
    }

    public Integer getSpecialNumber() {
        return SpecialNumber;
    }

    public void setSpecialNumber(Integer specialNumber) {
        SpecialNumber = specialNumber;
    }

    public CharacterSpecials(Integer maxCooldown, Integer minCooldown, String specialDescription, Integer specialNumber) {
        MaxCooldown = maxCooldown;
        MinCooldown = minCooldown;
        SpecialDescription = specialDescription;
        SpecialNumber = specialNumber;
    }

    public CharacterSpecials() {

    }


}
