package th.co.apps360.eat360.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import th.co.apps360.eat360.Model.CurrencyModel;

/**
 * Created by dan on 10/17/16.
 */

public class MoreDataModel {

    public enum Search_Tab {
        FOOD,
        RESTAURANT
    }

    public enum Distance_Type {
        WALK,
        CAR,
        ADVANCED
    }

    public static ArrayList<CurrencyModel> currencyObjectList = new ArrayList<>();
    public static Integer dispCurrencyID = 2; // usd
    public static ArrayList<LanguageModel> languageObjectList = new ArrayList<>();

    public static String userAvatarUrl = "";
    public static String userEmail = "";
    public static String userFirstname = "";
    public static String userLastname = "";

    public static Distance_Type typeDistance = Distance_Type.WALK;
    public static double radius = 5.0;

    public static String qrscancode = "";


}

