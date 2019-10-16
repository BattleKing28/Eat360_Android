package th.co.apps360.eat360.Model;

import java.util.ArrayList;

/**
 * Created by dan on 12/11/16.
 */

public class MoreMenuModel {

    public enum Menu_Tab {
        FOOD,
        DRINK,
    }

    public static RestaurantInfoModel restaurantInfo = null;
    public static ArrayList<MenuCategory> foodCategories = new ArrayList<>();
    public static ArrayList<MenuCategory> drinkCategories = new ArrayList<>();

}
