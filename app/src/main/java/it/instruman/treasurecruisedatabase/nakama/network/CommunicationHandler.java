package it.instruman.treasurecruisedatabase.nakama.network;

import android.support.annotation.Nullable;

import java.util.EnumSet;

/**
 * Created by infan on 18/02/2018.
 */

public class CommunicationHandler {

    public enum Controller {
        unit,
        stage,
        team,
        box,
        profile,
        ship
    }

    public enum EndPoint {
        get,
        stub,
        detail,
        search
    }

    public enum FreeToPlayStatus {
        None(0),
        All(1),
        Crew(2);

        int value;

        FreeToPlayStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum StageType {
        Unknown(0),
        Story(1),
        Fortnight(2),
        Weekly(3),
        Raid(4),
        Coliseum(5),
        Special(6),
        TrainingForest(7),
        TreasureMap(8);

        int value;

        StageType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum UnitClass {
        Unknown(0),
        Shooter(1),
        Fighter(2),
        Striker(4),
        Slasher(8),
        Cerebral(16),
        Driven(32),
        Powerhouse(64),
        FreeSpirit(128),
        Evolver(256),
        Booster(512);

        int value;

        UnitClass(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static UnitClass getEntry(int value) {
            for (UnitClass l : UnitClass.values()) {
                if (l.getValue() == value) return l;
            }
            throw new IllegalArgumentException("Entry not found.");
        }

        public static EnumSet<UnitClass> getEntries(int flags) {
            EnumSet<UnitClass> result = EnumSet.noneOf(UnitClass.class);
            for (UnitClass e : UnitClass.values()) {
                if((e.getValue()&flags)>0) {
                    result.add(e);
                }
            }
            return result;
        }
    }

    public enum UnitType {
        Unknown(0),
        STR(1),
        DEX(2),
        QCK(4),
        INT(8),
        PSY(16);

        int value;

        UnitType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static UnitType getEntry(int value) {
            for (UnitType l : UnitType.values()) {
                if (l.getValue() == value) return l;
            }
            throw new IllegalArgumentException("Entry not found.");
        }
    }

    public enum UnitRole {
        Unknown(0),
        Beatstick(1),
        DamageReducer(2),
        DefenseReducer(4),
        Delayer(8),
        AttackBooster(16),
        OrbBooster(32),
        FixedDamage(64),
        HealthCutter(128),
        OrbShuffler(256),
        Healer(512);

        int value;

        UnitRole(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static UnitRole getEntry(int value) {
            for (UnitRole l : UnitRole.values()) {
                if (l.getValue() == value) return l;
            }
            throw new IllegalArgumentException("Entry not found.");
        }
    }

    public enum UnitFlag {
        Unknown(0),
        Global(1),
        RareRecruit(2),
        RareRecruitExclusive(4),
        RareRecruitLimited(8),
        Promotional(16),
        Shop(32);

        int value;

        UnitFlag(int value) {
            this.value = value;
        }
    }

    public interface ISearchModel {
        ISearchModel page(Integer value);

        ISearchModel pageSize(Integer value);

        ISearchModel sortBy(String value);

        ISearchModel sortDesc(Boolean value);
    }

    public interface IUnitSearchModel extends ISearchModel {
        IUnitSearchModel term(String value);

        IUnitSearchModel classes(UnitClass value);

        IUnitSearchModel classes(UnitClass... values);

        IUnitSearchModel types(UnitType value);

        IUnitSearchModel types(UnitType... values);

        IUnitSearchModel forceClass(Boolean value);

        IUnitSearchModel freeToPlay(Boolean value);

        IUnitSearchModel global(Boolean value);

        IUnitSearchModel boxId(Integer value);

        IUnitSearchModel blacklist(Boolean value);
    }

    public interface ITeamSearchModel extends ISearchModel {
        ITeamSearchModel term(String value);

        ITeamSearchModel submittedBy(String value);

        ITeamSearchModel leaderId(Integer value);

        ITeamSearchModel noHelp(Boolean value);

        ITeamSearchModel stageId(Integer value);

        ITeamSearchModel boxId(Integer value);

        ITeamSearchModel blacklist(Boolean value);

        ITeamSearchModel global(Boolean value);

        ITeamSearchModel freeToPlay(FreeToPlayStatus value);

        ITeamSearchModel classes(UnitClass value);

        ITeamSearchModel classes(UnitClass... values);

        ITeamSearchModel types(UnitType value);

        ITeamSearchModel types(UnitType... values);

        ITeamSearchModel deleted(Boolean value);

        ITeamSearchModel draft(Boolean value);

        ITeamSearchModel reported(Boolean value);

        ITeamSearchModel bookmark(Boolean value);
    }

    public interface IStageSearchModel extends ISearchModel {
        IStageSearchModel term(String value);

        IStageSearchModel type(StageType value);

        IStageSearchModel global(Boolean value);
    }

    public interface IProfileSearchModel extends ISearchModel {
        IProfileSearchModel term(String value);

        IProfileSearchModel roles(String[] value);
    }

    public interface IBoxSearchModel extends ISearchModel {
        IBoxSearchModel userId(String value);

        IBoxSearchModel blacklist(Boolean value);
    }

    public interface IShipSearchModel {
        IShipSearchModel ID ( int value );
        IShipSearchModel name ( String value );
        IShipSearchModel eventShip ( Boolean value );
        IShipSearchModel eventShipActive ( Boolean value );
    }

    public static class BuildQuery {
        String query;

        /**
         * Creates an instance of a BuildQuery object and returns it
         *
         * @param c  The controller to query
         * @param e  The endpoint to query
         * @param id Optional id for the controller
         * @return A BuildQuery object which has to be further accessed to build a functional query
         */
        public static BuildQuery newInstance(Controller c, EndPoint e, @Nullable Integer id) {
            BuildQuery buildQuery = new BuildQuery();
            buildQuery.setQuery("https://www.nakama.network/api/");
            if (id != null) {
                buildQuery.appendQuery(c.name() + "/" + String.valueOf(id) + "/");
                buildQuery.appendQuery(e.name() + "?");
            } else {
                buildQuery.appendQuery(c.name() + "/");
                buildQuery.appendQuery(e.name() + "?");
            }
            return buildQuery;
        }

        BuildQuery setQuery(String value) {
            query = value;
            return this;
        }

        BuildQuery appendQuery(String value) {
            query += value;
            return this;
        }

        String getQuery() {
            return query;
        }
    }
}
