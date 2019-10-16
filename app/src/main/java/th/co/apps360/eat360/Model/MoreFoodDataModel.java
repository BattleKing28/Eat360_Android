package th.co.apps360.eat360.Model;

import java.util.ArrayList;

/**
 * Created by dan on 11/25/16.
 */

public class MoreFoodDataModel {

    public static ArrayList<CategoryModel> foodCategoryList = new ArrayList<>();
    public static ArrayList<CategoryModel> foodCuisineList = new ArrayList<>();

//    public static ArrayList <Boolean> foodCategorySelectionList = new ArrayList<>();
    public static Integer foodCategorySelectedCount = 0;
    public static CategoryModel foodSelCuisine;
    public static ArrayList<Integer> foodSelCategoryIndexList = new ArrayList<>();

    public static String searchText = "";
    public static String searchType = "";

    public static Integer tabNo = 0;

    public static Boolean hasTopCategories = false;
    public static Boolean hasTopCuisines = false;

}
