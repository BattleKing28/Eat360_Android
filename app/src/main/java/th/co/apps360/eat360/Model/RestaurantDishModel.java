package th.co.apps360.eat360.Model;

/**
 * Created by dan on 12/11/16.
 */

public class RestaurantDishModel {

    private String food_id;
    private String restaurant_id;
    private String lang_code;
    private String category_id;
    private String category_desc;
    private String category_icon;
    private String category_group;
    private String food_name;
    private String food_eng_name;
    private String price;
    private String currency;
    private String food_desc;
    private String cuisine_id;
    private String cuisine_desc;
    private String image_path;

    public RestaurantDishModel(String id, String restaurant_id, String lang_code, String category_id,
                               String category_desc, String category_icon, String category_group,
                               String food_name, String food_eng_name, String price, String currency,
                               String food_desc, String cuisine_id, String cuisine_desc, String image_path) {
        this.food_id = id;
        this.restaurant_id = restaurant_id;
        this.lang_code = lang_code;
        this.category_id = category_id;
        this.category_desc = category_desc;
        this.category_icon = category_icon;
        this.category_group = category_group;
        this.food_name = food_name;
        this.food_eng_name = food_eng_name;
        this.price = price;
        this.currency = currency;
        this.food_desc = food_desc;
        this.cuisine_id = cuisine_id;
        this.cuisine_desc = cuisine_desc;
        this.image_path = image_path;
    }

    public String getFood_id() {
        return food_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public String getLang_code() {
        return lang_code;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getCategory_desc() {
        return category_desc;
    }

    public String getCategory_icon() {
        return category_icon;
    }

    public String getCategory_group() {
        return category_group;
    }

    public String getFood_name() {
        return food_name;
    }

    public String getFood_eng_name() {
        return food_eng_name;
    }

    public String getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getFood_desc() {
        return food_desc;
    }

    public String getCuisine_id() {
        return cuisine_id;
    }

    public String getCuisine_desc() {
        return cuisine_desc;
    }

    public String getImage_path() {
        return image_path;
    }

}
