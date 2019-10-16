package th.co.apps360.eat360;

/**
 * Created by Dan on 3/6/17.
 */

public class ConfigURLs {
    static String baseURL = "https://api.eat360app.com";
    static String frontURL =  "https://eat360app.com/";

    static String loginURL = baseURL + "/login";
    static String registerURL = baseURL + "/register";
    static String getUserInfoURL = baseURL + "/get_user_info";
    static String updateTokenURL = baseURL + "/update_token";
    static String updateUserURL = baseURL + "/update_user";
    static String uploadAvatarURL = baseURL + "/upload_avatar";
    static String logoutURL = baseURL + "/logout";

    static String listLanguageURL = baseURL +"/get_available_language";
    static String listCurrencyURL = baseURL +"/get_all_currency";
    static String allCategoryURL = baseURL +"/get_top_category";
    static String allCuisineURL = baseURL +"/get_all_cuisines";
    static String allFacilityURL = baseURL +"/facilities";

    static String autocompleteSearch_food = baseURL +"/search_food";
    static String autocompleteSearch_restaurant = baseURL +"/search_restaurant";

    static String searchFoodURL = baseURL +"/search_food";
    static String searchRestaurantURL = baseURL +"/search_restaurant";
    static String searchByLocationURL = baseURL +"/search_by_location";
    static String searchByQrURL = baseURL +"/search_by_qr";

    static String foodDetailURL = baseURL +"/food";
    static String foodImageURL = baseURL +"/food_imgs";
    static String foodIngredientsURL = baseURL +"/food_ingredients";

    static String restaurantDetailURL = baseURL + "/restaurant";
    static String restaurantFoodURL = baseURL +"/search_restaurant_food";
    static String restaurantDrinkURL = baseURL +"/search_restaurant_drink";
    static String restaurantMenuCategoryURL = baseURL +"/search_restaurant_menu_category";
    static String restaurantMultiDetailURL = baseURL +"/multiple_restaurant_details";

    static String reverseGeoCodingURL = "https://maps.googleapis.com/maps/api/geocode/json";

    static String foodMealsURL = baseURL +"/food_meals";
    static String allRestaurantMenuUrl = baseURL +"/search_restaurant_menus";
    static String searchMenuByCategoryURL = baseURL +"/search_restaurant_menu_by_category";
    static String foodByMealUrl = baseURL +"/search_food_by_meal_id";
    static String foodByCuisineUrl = baseURL +"/search_food_by_cuisine_id";
    static String restaurantMenuByFoodKeywordUrl = baseURL +"/search_restaurant_menu_by_food_keyword";
    static String searchKeywordURL = baseURL +"/search_by_keyword";
    static String listImageNamedURL = baseURL +"/restaurant_imgs";
    static String menuByCuisineUrl = baseURL +"/search_menus_by_cuisine_id";
    static String listCurrencyConverterURL = baseURL +"/currency_converter";

}
