package it.instruman.treasurecruisedatabase.nakama.network;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by infan on 19/02/2018.
 */

public class Ship extends RealmObject{
    @PrimaryKey
    private int Id ;
    private String Name ;
    private Boolean EventShip ;
    private Boolean EventShipActive ;

    public Ship() {
    }

    public Ship(int id, String name, Boolean eventShip, Boolean eventShipActive) {
        Id = id;
        Name = name;
        EventShip = eventShip;
        EventShipActive = eventShipActive;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Boolean getEventShip() {
        return EventShip;
    }

    public void setEventShip(Boolean eventShip) {
        EventShip = eventShip;
    }

    public Boolean getEventShipActive() {
        return EventShipActive;
    }

    public void setEventShipActive(Boolean eventShipActive) {
        EventShipActive = eventShipActive;
    }
}
