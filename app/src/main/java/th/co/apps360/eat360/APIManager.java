package th.co.apps360.eat360;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import th.co.apps360.eat360.Model.CategoryModel;
import th.co.apps360.eat360.Model.CurrencyModel;
import th.co.apps360.eat360.Model.FacilityModel;
import th.co.apps360.eat360.Model.LanguageModel;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.activity.MainActivity;

/**
 * Created by Dan on 3/7/17.
 */

public class APIManager {

    public static void downloadAllLanguages(final Context context) {
        String url = ConfigURLs.listLanguageURL;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject jObject = response.getJSONObject("Results");
                    Iterator<String> keys = jObject.keys();
                    int size = jObject.length();
                    MoreDataModel.languageObjectList.clear();
                    for(int i=0;i<size;i++) {
                        String object = keys.next();
                        JSONObject langObject = jObject.getJSONObject(object);
                        int id = langObject.getInt("id");
                        String lang_code = langObject.getString("lang_code");
                        String keyName  = langObject.getString("name");
                        keyName =  keyName.substring(0,1).toUpperCase()+ keyName.substring(1).toLowerCase(); //capital first char
                        String name = keyName;
                        String short_name = langObject.getString("short_name");
                        String icon = langObject.getString("icon");
                        LanguageModel aLanguageObject = new LanguageModel(name, id, short_name, lang_code, icon);
                        MoreDataModel.languageObjectList.add(aLanguageObject);
                    }
                    Collections.sort(MoreDataModel.languageObjectList);
                } catch (JSONException e) {
                    Utils.setDebug("MainActivity", "failed to get all language: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.setDebug("MainActivity", "failed to get all language: " + res);
                Utils.showConnectionProblemDialog(context);
            }
        });
    }

    public static void downloadAllCurrencies(final Context context) {
        String url = ConfigURLs.listCurrencyURL;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject jObject = response.getJSONObject("results");
                    Iterator<String> keys = jObject.keys();
                    int size = jObject.length();
                    Utils.setDebug("APIManager", "currency size: " + size);
                    size = 32;
                    MoreDataModel.currencyObjectList.clear();
                    for(int i=0;i<size;i++) {
                        String object = keys.next();
                        JSONObject langObject  = jObject.getJSONObject(object);
                        String currency = langObject.getString("currency");
                        int id = langObject.getInt("id");
                        double rate = langObject.getDouble("rate");
                        int is_based = langObject.getInt("is_based");
                        CurrencyModel aCurrencyObject = new CurrencyModel(currency, id, rate, is_based);
                        MoreDataModel.currencyObjectList.add(aCurrencyObject);
                    }
                    Collections.sort(MoreDataModel.currencyObjectList);
                } catch (JSONException e) {
                    Utils.setDebug("MainActivity", "failed to get all currency: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.setDebug("MainActivity", "failed to get all currency: " + res);
                Utils.showConnectionProblemDialog(context);
            }
        });
    }

    public static void downloadAllCategories(final Context context) {
        String url = ConfigURLs.allCategoryURL;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        params.put("LanguageID", Utils.getCurrentLanguage(context));
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject resultObject = response.getJSONObject("Results");
                    JSONArray categoryArray = resultObject.getJSONArray("food_category");
                    for (int i = 0; i < categoryArray.length(); i ++) {
                        JSONObject aCategory = categoryArray.getJSONObject(i);
                        Integer id = Integer.valueOf(aCategory.getString("id"));
                        String langcode = aCategory.getString("lang_code");
                        String name = aCategory.getString("name");
                        String icon = aCategory.getString("icon");
                        String type = aCategory.getString("type");
                        CategoryModel aFoodCategory = new CategoryModel(id, langcode, name, icon, type);
                        MoreFoodDataModel.foodCategoryList.add(aFoodCategory);
                    }
                    JSONArray mealArray = resultObject.getJSONArray("food_meal");
                    for (int i = 0; i < mealArray.length(); i ++) {
                        JSONObject aMeal = mealArray.getJSONObject(i);
                        Integer id = Integer.valueOf(aMeal.getString("id"));
                        String langcode = aMeal.getString("lang_code");
                        String name = aMeal.getString("name");
                        String icon = aMeal.getString("icon");
                        String type = aMeal.getString("type");
                        CategoryModel aFoodMeal = new CategoryModel(id, langcode, name, icon, type);
                        MoreFoodDataModel.foodCategoryList.add(aFoodMeal);
                    }
                    MoreFoodDataModel.hasTopCategories = true;
                } catch (JSONException e) {
                    Utils.setDebug("MainActivity", "failed to get all category: " + e.getLocalizedMessage());
                    Utils.showConnectionProblemDialog(context);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.setDebug("MainActivity", "failed to get all category: " + res);
                Utils.showConnectionProblemDialog(context);
            }
        });
    }

    public static void downloadAllCuisines(final Context context) {
        String url = ConfigURLs.allCuisineURL;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        params.put("LanguageID", Utils.getCurrentLanguage(context));
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray cuisineArray = response.getJSONArray("Results");
                    for (int i = 0; i < cuisineArray.length(); i ++) {
                        JSONObject aCuisine = cuisineArray.getJSONObject(i);
                        Integer id = Integer.valueOf(aCuisine.getString("cuisine_id"));
                        String langcode = Utils.getCurrentLanguage(context);
                        String name = aCuisine.getString("description");
                        String icon = "";
                        String type = "food_cuisine";
                        CategoryModel aFoodCuisine = new CategoryModel(id, langcode, name, icon, type);
                        MoreFoodDataModel.foodCuisineList.add(aFoodCuisine);
                        MoreRestaurantDataModel.restaurantCuisineList.add(aFoodCuisine);
                    }
                    MoreFoodDataModel.hasTopCuisines = true;
                    MoreRestaurantDataModel.hasTopCuisines = true;
                } catch (JSONException e) {
                    Utils.setDebug("MainActivity", "failed to get all cuisine: " + e.getLocalizedMessage());
                    Utils.showConnectionProblemDialog(context);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.setDebug("MainActivity", "failed to get all cuisine: " + res);
                Utils.showConnectionProblemDialog(context);
            }
        });
    }


    public static void downloadAllFacilities(final Context context) {
        String url = ConfigURLs.allFacilityURL;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        params.put("LanguageID", Utils.getCurrentLanguage(context));
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray facilityArray = response.getJSONArray("Results");
                    for (int i = 0; i < facilityArray.length(); i ++) {
                        JSONObject aFacilityObject = facilityArray.getJSONObject(i);
                        Integer id = Integer.valueOf(aFacilityObject.getString("id"));
                        String langcode = aFacilityObject.getString("lang_code");
                        String name = aFacilityObject.getString("facility_name");
                        String icon = aFacilityObject.getString("facility_icon");
                        String desc = aFacilityObject.getString("facility_description");
                        FacilityModel aFacility = new FacilityModel(id, langcode, name, icon, desc);
                        MoreRestaurantDataModel.restaurantFacilityList.add(aFacility);
                    }
                    MoreRestaurantDataModel.hasTopFacilities = true;
                } catch (JSONException e) {
                    Utils.showConnectionProblemDialog(context);
                    Utils.setDebug("MainActivity", "failed to get all facility: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.setDebug("MainActivity", "failed to get all facility: " + res);
                Utils.showConnectionProblemDialog(context);
            }
        });
    }

}
