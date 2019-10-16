package th.co.apps360.eat360.Model;

/**
 * Created by dan on 11/25/16.
 */

public class FoodSearchResultModel {

    private String food_id;
    private String food_name;
    private String food_desc;
    private String restaurant_id;
    private String restaurant_name;
    private String lang_code;
    private String price;
    private String currency;
    private String img_path;

    public FoodSearchResultModel(String food_id, String food_name, String food_desc,
                                 String restaurant_id, String restaurant_name, String lang_code,
                                 String price, String currency, String img_path) {
        this.food_id = food_id;
        this.food_name = food_name;
        this.food_desc = food_desc;
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
        this.lang_code = lang_code;
        this.price = price;
        this.currency = currency;
        this.img_path = img_path;
    }

    public String getFood_id() {
        return food_id;
    }

    public String getFood_name() {
        return food_name;
    }

    public String getFood_desc() {
        return food_desc;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public String getLang_code() {
        return lang_code;
    }

    public String getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getImg_path() {
        return img_path;
    }

}
