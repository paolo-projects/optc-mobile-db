package it.instruman.treasurecruisedatabase;

import java.io.IOException;

/**
 * Created by infan on 27/09/2017.
 */

public class SkullsHelper {
    /*
        ID ASSOCIATIONS:
            Luffy   -1
            Zoro    -2
            Nami    -3
            Usopp   -4
            Sanji   -5
            Chopper -6
            Robin   -7
            Franky  -8
            Brook   -9

            STR -11
            QCK -12
            PSY -13
            DEX -14
            INT -15
     */
    public static String getThumb(String name) {
        switch (name){
            case "skullLuffy":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_luffy.png";
            case "skullZoro":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_zoro.png";
            case "skullNami":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_nami.png";
            case "skullUsopp":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_usopp_f.png";
            case "skullSanji":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_sanji_f.png";
            case "skullChopper":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_chopper_f.png";
            case "skullRobin":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_robin_f.png";
            case "skullFranky":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_franky_f.png";
            case "skullBrook":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_brook_f.png";
            case "skullSTR":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/red_skull_f.png";
            case "skullQCK":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/blue_skull_f.png";
            case "skullPSY":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/yellow_skull2_f.png";
            case "skullDEX":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/green_skull2_f.png";
            case "skullINT":
                return "https://onepiece-treasurecruise.com/wp-content/uploads/black_skull_f.png";
            default:
                return "https://onepiece-treasurecruise.com/wp-content/themes/onepiece-treasurecruise/images/noimage.png";
        }
    }

    public static String getThumbFromId(Integer id) {
        switch (id){
            case -1:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_luffy.png";
            case -2:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_zoro.png";
            case -3:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_nami.png";
            case -4:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_usopp_f.png";
            case -5:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_sanji_f.png";
            case -6:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_chopper_f.png";
            case -7:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_robin_f.png";
            case -8:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_franky_f.png";
            case -9:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/skull_brook_f.png";
            case -11:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/red_skull_f.png";
            case -12:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/blue_skull_f.png";
            case -13:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/yellow_skull2_f.png";
            case -14:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/green_skull2_f.png";
            case -15:
                return "https://onepiece-treasurecruise.com/wp-content/uploads/black_skull_f.png";
            default:
                return "https://onepiece-treasurecruise.com/wp-content/themes/onepiece-treasurecruise/images/noimage.png";
        }
    }

    public static String getBigThumb(String name) {
        switch (name){
            case "skullLuffy":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_luffy_c.png";
            
            case "skullZoro":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_zoro_c.png";
            
            case "skullNami":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_nami_c.png";
            
            case "skullUsopp":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_usopp_c.png";
            
            case "skullSanji":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_sanji_c.png";
            
            case "skullChopper":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_chopper_c.png";
            
            case "skullRobin":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_robin_c.png";
            
            case "skullFranky":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_franky_c.png";
            
            case "skullBrook":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/skull_brook_c.png";
            
            case "skullSTR":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/red_skull_c.png";
            
            case "skullQCK":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/blue_skull_c.png";
            
            case "skullPSY":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/yellow_skull2_c.png";
            
            case "skullDEX":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/green_skull2_c.png";
            
            case "skullINT":
                return "http://onepiece-treasurecruise.com/wp-content/uploads/black_skull_c.png";

            default:
                return "https://onepiece-treasurecruise.com/wp-content/themes/onepiece-treasurecruise/images/noimage.png";
        }
    }

    public static Integer getSkullId(String name) throws IOException {
        switch (name){
            case "skullLuffy":
                return -1;

            case "skullZoro":
                return -2;

            case "skullNami":
                return -3;

            case "skullUsopp":
                return -4;

            case "skullSanji":
                return -5;

            case "skullChopper":
                return -6;

            case "skullRobin":
                return -7;

            case "skullFranky":
                return -8;

            case "skullBrook":
                return -9;

            case "skullSTR":
                return -11;

            case "skullQCK":
                return -12;

            case "skullPSY":
                return -13;

            case "skullDEX":
                return -14;

            case "skullINT":
                return -15;

            default:
                throw new IOException("Can't parse this skull name: "+name);
        }
    }

    public static String getSkullName(Integer id) throws IOException {
        switch(id) {
            case -1:
                return "skullLuffy";
            case -2:
                return "skullZoro";
            case -3:
                return "skullNami";
            case -4:
                return "skullUsopp";
            case -5:
                return "skullSanji";
            case -6:
                return "skullChopper";
            case -7:
                return "skullRobin";
            case -8:
                return "skullFranky";
            case -9:
                return "skullBrook";
            case -11:
                return "skullSTR";
            case -12:
                return "skullQCK";
            case -13:
                return "skullPSY";
            case -14:
                return "skullDEX";
            case -15:
                return "skullINT";
            default:
                throw new IOException("Can't recognize this skull id: "+id.toString());
        }
    }
}
