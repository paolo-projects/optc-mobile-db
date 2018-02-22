package it.instruman.treasurecruisedatabase.nakama.network;

import java.util.EnumSet;

import io.realm.RealmObject;

import static it.instruman.treasurecruisedatabase.nakama.network.CommunicationHandler.UnitClass;
import static it.instruman.treasurecruisedatabase.nakama.network.CommunicationHandler.UnitRole;
import static it.instruman.treasurecruisedatabase.nakama.network.CommunicationHandler.UnitType;

/**
 * Created by infan on 18/02/2018.
 */

public class Unit extends RealmObject {

    private int position;
    private boolean isGeneric;
    private Integer unitId;
    private int genericRole;
    private int genericType;
    private int genericClass;

    public Unit() {
    }

    public Unit(boolean isGeneric, Integer unitId, UnitRole genericRole, UnitType genericType, EnumSet<UnitClass> genericClass, int position) {
        this.isGeneric = isGeneric;
        this.unitId = unitId;
        this.genericRole = genericRole != null ? genericRole.getValue() : 0;
        this.genericType = genericType != null ? genericType.getValue() : 0;
        int result = 0;
        if (genericClass != null) {
            for (UnitClass u : genericClass)
                result |= u.getValue();
        }
        this.genericClass = result;
        this.position = position;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    public void setGeneric(boolean generic) {
        isGeneric = generic;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public UnitRole getGenericRole() {
        return UnitRole.getEntry(genericRole);
    }

    public void setGenericRole(UnitRole genericRole) {
        this.genericRole = genericRole.getValue();
    }

    public UnitType getGenericType() {
        return UnitType.getEntry(genericType);
    }

    public void setGenericType(UnitType genericType) {
        this.genericType = genericType.getValue();
    }

    public EnumSet<UnitClass> getGenericClass() {
        return UnitClass.getEntries(genericClass);
    }

    public void setGenericClass(EnumSet<UnitClass> genericClass) {
        int result = 0;
        for (UnitClass u : genericClass)
            result |= u.getValue();
        this.genericClass = result;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
