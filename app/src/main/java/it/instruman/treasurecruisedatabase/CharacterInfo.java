package it.instruman.treasurecruisedatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo on 01/11/2016.
 */

public class CharacterInfo {
    private Integer DB_ID = null;
    private Integer ID = null;
    private String Name = null;
    private String Type = null;
    private String Class1 = null;
    private String Class2 = null;
    private Integer Stars = null;
    private Integer Cost = null;
    private Integer Combo = null;
    private Integer Sockets = null;
    private Integer MaxLvl = null;
    private Integer ExpToMax = null;
    private Integer Lvl1HP = null;
    private Integer Lvl1ATK = null;
    private Integer Lvl1RCV = null;
    private Integer MaxHP = null;
    private Integer MaxATK = null;
    private Integer MaxRCV = null;
    private String SpecialName = null;
    private String SpecialNotes = null;
    private List<CharacterSpecials> Specials = new ArrayList<>();
    private String CaptainDescription = null;
    private String CaptainNotes = null;

    public String getCrewmateDescription() {
        return CrewmateDescription;
    }

    public void setCrewmateDescription(String crewmateDescription) {
        CrewmateDescription = crewmateDescription;
    }

    public String getCrewmateNotes() {
        return CrewmateNotes;
    }

    public void setCrewmateNotes(String crewmateNotes) {
        CrewmateNotes = crewmateNotes;
    }

    private String CrewmateDescription = null;
    private String CrewmateNotes = null;
    private List<CharacterEvolutions> Evolutions = new ArrayList<>();
    private ArrayList<DropInfo> dropInfos = new ArrayList<>();

    public ArrayList<DropInfo> getManualsInfos() {
        return manualsInfos;
    }

    public void setManualsInfos(ArrayList<DropInfo> manualsInfos) {
        this.manualsInfos = manualsInfos;
    }

    public ArrayList<DropInfo> getDropInfos() {
        return dropInfos;
    }

    public void setDropInfos(ArrayList<DropInfo> dropInfos) {
        this.dropInfos = dropInfos;
    }

    private ArrayList<DropInfo> manualsInfos = new ArrayList<>();

    public CharacterInfo(String captainDescription, String captainNotes, String class1,
                         String class2, Integer combo, Integer cost, Integer DB_ID,
                         List<CharacterEvolutions> evolutions, Integer expToMax, Integer ID,
                         Integer lvl1ATK, Integer lvl1HP, Integer lvl1RCV, Integer maxATK,
                         Integer maxHP, Integer maxLvl, Integer maxRCV, String name, Integer sockets,
                         String specialName, String specialNotes, List<CharacterSpecials> specials,
                         Integer stars, String type, ArrayList<DropInfo> DropInfos, ArrayList<DropInfo> ManualsInfos, String crewmateDescription, String crewmateNotes) {
        CaptainDescription = captainDescription;
        CaptainNotes = captainNotes;
        Class1 = class1;
        Class2 = class2;
        Combo = combo;
        Cost = cost;
        this.DB_ID = DB_ID;
        Evolutions = evolutions;
        ExpToMax = expToMax;
        this.ID = ID;
        Lvl1ATK = lvl1ATK;
        Lvl1HP = lvl1HP;
        Lvl1RCV = lvl1RCV;
        MaxATK = maxATK;
        MaxHP = maxHP;
        MaxLvl = maxLvl;
        MaxRCV = maxRCV;
        Name = name;
        Sockets = sockets;
        SpecialName = specialName;
        SpecialNotes = specialNotes;
        Specials = specials;
        Stars = stars;
        Type = type;
        dropInfos = DropInfos;
        manualsInfos = ManualsInfos;
        CrewmateDescription = crewmateDescription;
        CrewmateNotes = crewmateNotes;
    }

    public CharacterInfo() {
    }

    public ArrayList<DropInfo> getDropInfo() {
        return dropInfos;
    }

    public void setDropInfo(ArrayList<DropInfo> dropInfos) {
        this.dropInfos = dropInfos;
    }

    public String getCaptainDescription() {
        return CaptainDescription;
    }

    public void setCaptainDescription(String captainDescription) {
        CaptainDescription = captainDescription;
    }

    public String getCaptainNotes() {
        return CaptainNotes;
    }

    public void setCaptainNotes(String captainNotes) {
        CaptainNotes = captainNotes;
    }

    public String getClass1() {
        return Class1;
    }

    public void setClass1(String class1) {
        Class1 = class1;
    }

    public String getClass2() {
        return Class2;
    }

    public void setClass2(String class2) {
        Class2 = class2;
    }

    public Integer getCombo() {
        return Combo;
    }

    public void setCombo(Integer combo) {
        Combo = combo;
    }

    public Integer getCost() {
        return Cost;
    }

    public void setCost(Integer cost) {
        Cost = cost;
    }

    public Integer getDB_ID() {
        return DB_ID;
    }

    public void setDB_ID(Integer DB_ID) {
        this.DB_ID = DB_ID;
    }

    public List<CharacterEvolutions> getEvolutions() {
        return Evolutions;
    }

    public void setEvolutions(List<CharacterEvolutions> evolutions) {
        Evolutions = evolutions;
    }

    public Integer getExpToMax() {
        return ExpToMax;
    }

    public void setExpToMax(Integer expToMax) {
        ExpToMax = expToMax;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getLvl1ATK() {
        return Lvl1ATK;
    }

    public void setLvl1ATK(Integer lvl1ATK) {
        Lvl1ATK = lvl1ATK;
    }

    public Integer getLvl1HP() {
        return Lvl1HP;
    }

    public void setLvl1HP(Integer lvl1HP) {
        Lvl1HP = lvl1HP;
    }

    public Integer getLvl1RCV() {
        return Lvl1RCV;
    }

    public void setLvl1RCV(Integer lvl1RCV) {
        Lvl1RCV = lvl1RCV;
    }

    public Integer getMaxATK() {
        return MaxATK;
    }

    public void setMaxATK(Integer maxATK) {
        MaxATK = maxATK;
    }

    public Integer getMaxHP() {
        return MaxHP;
    }

    public void setMaxHP(Integer maxHP) {
        MaxHP = maxHP;
    }

    public Integer getMaxLvl() {
        return MaxLvl;
    }

    public void setMaxLvl(Integer maxLvl) {
        MaxLvl = maxLvl;
    }

    public Integer getMaxRCV() {
        return MaxRCV;
    }

    public void setMaxRCV(Integer maxRCV) {
        MaxRCV = maxRCV;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getSockets() {
        return Sockets;
    }

    public void setSockets(Integer sockets) {
        Sockets = sockets;
    }

    public String getSpecialName() {
        return SpecialName;
    }

    public void setSpecialName(String specialName) {
        SpecialName = specialName;
    }

    public String getSpecialNotes() {
        return SpecialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        SpecialNotes = specialNotes;
    }

    public List<CharacterSpecials> getSpecials() {
        return Specials;
    }

    public void setSpecials(List<CharacterSpecials> specials) {
        Specials = specials;
    }

    public Integer getStars() {
        return Stars;
    }

    public void setStars(Integer stars) {
        Stars = stars;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public void addSpecial(CharacterSpecials value) {
        Specials.add(value);
    }

    public void addEvolution(CharacterEvolutions value) {
        Evolutions.add(value);
    }

    public CharacterSpecials getSpecial(int position) {
        return Specials.get(position);
    }

    public CharacterEvolutions getEvolution(int position) {
        return Evolutions.get(position);
    }
}
