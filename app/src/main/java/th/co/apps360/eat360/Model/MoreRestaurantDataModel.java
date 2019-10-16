package th.co.apps360.eat360.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 11/24/16.
 */

public class MoreRestaurantDataModel {

    public static ArrayList<FacilityModel> restaurantFacilityList = new ArrayList<>();
    public static ArrayList<CategoryModel> restaurantCuisineList = new ArrayList<>();

//    public static ArrayList <Boolean> restaurantFacilitySelectionList = new ArrayList<>();
    public static Integer restaurantFacilitySelectedCount = 0;
    public static CategoryModel restaurantSelCuisine;
    public static ArrayList<Integer> restaurantSelFacilityIndexList = new ArrayList<>();

    public static String searchText = "";
    public static String searchType = "";

    public static Boolean hasTopFacilities = false;
    public static Boolean hasTopCuisines = false;

}
