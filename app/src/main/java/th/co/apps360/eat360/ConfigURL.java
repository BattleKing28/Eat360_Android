package th.co.apps360.eat360;

import android.content.Context;

/**
 * Created by jakkrit.p on 2/07/2015.
 */
public  class ConfigURL {

    public static String shareFacebookUrl =  "https://eat360app.com/";
    public static String server1 = "https://api.eat360app.com";

    public static String about_assets1 = server1 + "/assets/new/img/busines_img1.jpg";
    public static String about_assets2 = server1 + "/assets/new/img/busines_img2.jpg";
    public static String about_assets3 = server1 + "/assets/new/img/busines_img3.jpg";

    public String defaultURL = "https://api.eat360app.com";

    public String loginURL = defaultURL + "/login";
    public String registerURL = defaultURL + "/register";
    public String getUserInfoURL = defaultURL + "/get_user_info";
    public String updateTokenURL = defaultURL + "/update_token";
    public String updateUserURL = defaultURL + "/update_user";
    public String uploadAvatarURL = defaultURL + "/upload_avatar";
    public String logoutURL = defaultURL + "/logout";

    public String listLanguageURL = defaultURL+"/get_available_language";
    public String listCurrencyURL = defaultURL+"/get_all_currency";
    public String topCategoryURL = defaultURL+"/get_top_category";
    public String topFacilityURL = defaultURL+"/facilities";
    public String autocompleteSearch_food = defaultURL+"/search_food";
    public String autocompleteSearch_restaurant = defaultURL+"/search_restaurant";

    public String searchFoodURL = defaultURL+"/search_food";
    public String searchRestaurantURL = defaultURL+"/search_restaurant";
    public String searchByLocationURL = defaultURL+"/search_by_location";
    public String searchByQrURL = defaultURL+"/search_by_qr";

    public String foodDetailURL = defaultURL+"/food";
    public String foodImageURL = defaultURL+"/food_imgs";
    public String foodIngredientsURL = defaultURL+"/food_ingredients";

    public String restaurantDetailURL = defaultURL+ "/restaurant";
    public String restaurantFoodURL = defaultURL+"/search_restaurant_food";
    public String restaurantDrinkURL = defaultURL+"/search_restaurant_drink";
    public String restaurantMenuCategoryURL = defaultURL+"/search_restaurant_menu_category";
    public String restaurantMultiDetailURL = defaultURL+"/multiple_restaurant_details";

    public static String reverseGeoCodingURL = "https://maps.googleapis.com/maps/api/geocode/json";

    public String foodMealsURL = defaultURL+"/food_meals";
    public String allRestaurantMenuUrl = defaultURL+"/search_restaurant_menus";
    public String searchMenuByCategoryURL = defaultURL+"/search_restaurant_menu_by_category";
    public String foodByMealUrl = defaultURL+"/search_food_by_meal_id";
    public String foodByCuisineUrl = defaultURL+"/search_food_by_cuisine_id";
    public String restaurantMenuByFoodKeywordUrl = defaultURL +"/search_restaurant_menu_by_food_keyword";
    public String searchKeywordURL = defaultURL+"/search_by_keyword";
    public String listImageNamedURL = defaultURL+"/restaurant_imgs";
    public String menuByCuisineUrl = defaultURL+"/search_menus_by_cuisine_id";
    public String listCurrencyConverterURL = defaultURL+"/currency_converter";

    public ConfigURL() {
        defaultURL = server1;
        refreshData();
    }

    public ConfigURL(Context context) {
        int selectedServer = Utils.getCurrentServer(context);
        switch (selectedServer){
            case 0:
                defaultURL = server1;
                break;
            case 1:
                defaultURL = server1;
                break;
            case 2:
                defaultURL = server1;
                break;
            default:
                break;
        }
        refreshData();
    }

    private void refreshData() {
        loginURL = defaultURL + "/login";
        registerURL = defaultURL + "/register";
        getUserInfoURL = defaultURL + "/get_user_info";
        updateTokenURL = defaultURL + "/update_token";
        updateUserURL = defaultURL + "/update_user";
        uploadAvatarURL = defaultURL + "/upload_avatar";
        logoutURL = defaultURL + "/logout";

        listLanguageURL = defaultURL+"/get_available_language";
        listCurrencyURL = defaultURL+"/get_all_currency";
        topCategoryURL = defaultURL+"/get_top_category";
        topFacilityURL = defaultURL+"/facilities";
        autocompleteSearch_food = defaultURL+"/search_food";
        autocompleteSearch_restaurant = defaultURL+"/search_restaurant";

        searchFoodURL = defaultURL+"/search_food";
        searchRestaurantURL = defaultURL+"/search_restaurant";
        searchByLocationURL = defaultURL+"/search_by_location";
        searchByQrURL = defaultURL+"/search_by_qr";

        foodDetailURL = defaultURL+"/food";
        foodImageURL = defaultURL+"/food_imgs";
        foodIngredientsURL = defaultURL+"/food_ingredients";

        restaurantDetailURL = defaultURL+ "/restaurant";
        restaurantFoodURL = defaultURL+"/search_restaurant_food";
        restaurantDrinkURL = defaultURL+"/search_restaurant_drink";
        restaurantMenuCategoryURL = defaultURL+"/search_restaurant_menu_category";

        restaurantMultiDetailURL = defaultURL+"/multiple_restaurant_details";
        allRestaurantMenuUrl = defaultURL+"/search_restaurant_menus" ;
        menuByCuisineUrl = defaultURL+"/search_menus_by_cuisine_id" ;
        foodMealsURL = defaultURL+"/food_meals";
        foodByMealUrl = defaultURL+"/search_food_by_meal_id";
        foodByCuisineUrl = defaultURL+"/search_food_by_cuisine_id";
        restaurantMenuByFoodKeywordUrl = defaultURL +"/search_restaurant_menu_by_food_keyword";
        searchMenuByCategoryURL = defaultURL+"/search_restaurant_menu_by_category";
        searchKeywordURL = defaultURL+"/search_by_keyword";
        listCurrencyConverterURL = defaultURL+"/currency_converter";
        listImageNamedURL = defaultURL+"/restaurant_imgs";
    }

}
