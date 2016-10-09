package it.instruman.treasurecruisedatabase;

import org.mozilla.javascript.NativeArray;

import java.io.Serializable;
import java.util.List;

class CoolDowns implements Serializable {
    Integer init, max = -1;
    Integer type = 0;

    public CoolDowns(Integer initiallvl, Integer maximumlvl) {
        init = initiallvl;
        max = maximumlvl;
        type = 2;
    }

    public CoolDowns(Integer initiallvl) {
        init = initiallvl;
        type = 1;
    }

    public CoolDowns() {
        type = 0;
    }

    public CoolDowns(Object cd) {
        if (cd != null) {
            try {
                if (cd.getClass().equals(NativeArray.class)) {
                    List<Double> cd_list = (List<Double>) cd;
                    switch (cd_list.size()) {
                        case 0:
                            type = 0;
                            break;
                        case 1:
                            init = cd_list.get(0).intValue();
                            type = 1;
                            break;
                        case 2:
                            init = cd_list.get(0).intValue();
                            max = cd_list.get(1).intValue();
                            type = 2;
                            break;
                        default:
                            type = 0;
                            break;
                    }
                } else if (cd.getClass().equals(Double.class)) {
                    init = ((Double) cd).intValue();
                    type = 1;
                } else type = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else type = 0;
    }

    public String print() {
        switch (type) {
            case 0:
                return "";
            case 1:
                return init.toString();
            case 2:
                String o = init + "/" + max;
                return o;
            default:
                return "";
        }
    }
}