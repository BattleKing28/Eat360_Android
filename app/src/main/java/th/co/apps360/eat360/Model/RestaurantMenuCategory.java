package th.co.apps360.eat360.Model;

import java.util.ArrayList;

/**
 * Created by dan on 12/11/16.
 */

public class RestaurantMenuCategory {

    private String category_id;
    private String lang_code;
    private String description;
    private String icon;
    private ArrayList<RestaurantDishModel> dishes;

    public RestaurantMenuCategory(String category_id, String lang_code, String description, String icon) {
        this.category_id = category_id;
        this.lang_code = lang_code;
        this.description = description;
        this.icon = icon;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getLang_code() {
        return lang_code;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public ArrayList<RestaurantDishModel> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<RestaurantDishModel> dishes) {
        this.dishes = dishes;
    }

}
