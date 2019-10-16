package th.co.apps360.eat360;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import th.co.apps360.eat360.Model.LanguageModel;
import th.co.apps360.eat360.Model.LocationModel;
import th.co.apps360.eat360.Model.MoreDataModel;
//import th.co.apps360.eat360.activity.SearchResultActivity;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.Model.MoreMenuModel;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.activity.MainActivity;

/**
 * Created by jakkrit.p on 23/07/2015.
 */
public class Utils {

    public static final String ID_PREFERENCE = "apps360";
    public static final int TIMEOUT_CONNECTION = 20000;
    public static final int TIMEOUT_SOCKET = 20000;
    public static final String TIMEOUT_ERROR = "timeout_error";
    public static final String CONNECTION_ERROR = "connection_error";

    public static final String AUTOCOMPLETE_FILTER_MODE = "autocomplete_mode";
    public static final String CATEGORY_FILTER_MODE = "category_mode";


    public static String getCurrentLanguage(Context context){

        String languageName = null;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            languageName = prefs.getString("language", "en");
        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

        return languageName;
    }

    public static void setCurrentLanguage(Context context,String languageCode){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("language", languageCode);
            preferences.apply();
            setLanguageApp(context, languageCode);
        }catch (Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
   }

    public static String getCurrentLanguageIcon(Context context){

        String languageIcon = null;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            languageIcon = prefs.getString("language_icon", null);
        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

        return languageIcon;
    }

    public static void setCurrentLanguageIcon(Context context,String languageIcon){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("language_icon", languageIcon);
            preferences.apply();
        }catch (Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public static int getCurrentLanguageIdx(Context context){

        int languageIdx = 0;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            languageIdx = prefs.getInt("language_idx", 2);
        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

        return languageIdx;
    }

    public static void setCurrentLanguageIdx(Context context, int languageIdx){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putInt("language_idx", languageIdx);
            preferences.apply();
//            setLanguageApp(context, languageIdx);
        }catch (Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public static int getCurrentCurrencyIdx(Context context){

        int currencyIdx = 0;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            currencyIdx = prefs.getInt("currency_idx", 7);
        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

        return currencyIdx;
    }

    public static void setCurrentCurrencyIdx(Context context, int currencyIdx){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putInt("currency_idx", currencyIdx);
            preferences.apply();
//            setLanguageApp(context, currencyIdx);
        }catch (Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public static void saveToken(Context context, String token) {
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("token", token);
            preferences.apply();
        }catch (Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public static String loadToken(Context context) {
        String token = null;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            token = prefs.getString("token", null);
        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
            return null;
        }
        return token;
    }

    public static void setTextFilter(Context context,String textFilter){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("textFilter", textFilter);

            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static String getTextFilter(Context context){

        String languageString ="";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            languageString = prefs.getString("textFilter","");
        }catch(Exception e){
            ;
        }

        return languageString;
    }

    ////////////


    public static void checkAndSetFilter(Context context,String searchType){


        if(searchType.contains("food_") && !searchType.contains("cuisine") ){

            Utils.setFoodFilterType(context, searchType);
            Utils.setUseFoodFilter(context, true);
            Utils.setUseFoodFilterTemp(context, true);

            Utils.setUseRestaurantFilter(context, false);
            Utils.setUseRestaurantFilterTemp(context, false);


        }else{

            Utils.setRestaurantFilterType(context,searchType);
            Utils.setUseRestaurantFilter(context, true);
            Utils.setUseRestaurantFilterTemp(context, true);

            Utils.setUseFoodFilter(context, false);
            Utils.setUseFoodFilterTemp(context, false);


            //setFilterFood(false);
        }
        
    }
    
    
    
    //////
    public static void setUseFoodFilter(Context context,Boolean useFoodFilter){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putBoolean("useFoodFilter", useFoodFilter);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static boolean  getUseFoodFilter(Context context){

        boolean useFoodFilter = false;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            useFoodFilter = prefs.getBoolean("useFoodFilter", false);
        }catch(Exception e){
            ;
        }

        return useFoodFilter;
    }


    public static void setUseFoodFilterTemp(Context context,Boolean useFoodFilter){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putBoolean("useFoodFilterTemp", useFoodFilter);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static boolean  getUseFoodFilterTemp(Context context){

        boolean useFoodFilter = false;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            useFoodFilter = prefs.getBoolean("useFoodFilterTemp", false);
        }catch(Exception e){
            ;
        }

        return useFoodFilter;
    }






    public static void setFoodFilterType(Context context,String foodFilterType){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("foodFilterType", foodFilterType);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static String  getFoodFilterType(Context context){

        String foodFilterType = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            foodFilterType = prefs.getString("foodFilterType", "");
        }catch(Exception e){
            ;
        }

        return foodFilterType;
    }


    public static void setTopCategoryOrAutocompleteFilterMode(Context context,String filterMode){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("filterMode", filterMode);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static String  getTopCategoryOrAutocompleteFilterMode(Context context){

        String filterMode = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            filterMode = prefs.getString("filterMode", "");
        }catch(Exception e){
            ;
        }

        return filterMode;
    }


    public static void setFoodCategoryId(Context context,String foodCategoryId){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("foodCategoryId", foodCategoryId);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public  static  String  getFoodCategoryId(Context context){

        String foodFilterType = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            foodFilterType = prefs.getString("foodCategoryId", "");
        }catch(Exception e){
            ;
        }

        return foodFilterType;
    }

    
    
    //


    public static void setUseRestaurantFilter(Context context,Boolean useRestaurantFilter){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putBoolean("useRestaurantFilter", useRestaurantFilter);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static boolean  getUseRestaurantFilter(Context context){

        boolean useRestaurantFilter = false;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            useRestaurantFilter = prefs.getBoolean("useRestaurantFilter", false);
        }catch(Exception e){
            ;
        }

        return useRestaurantFilter;
    }


    public static void setUseRestaurantFilterTemp(Context context,Boolean useRestaurantFilter){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putBoolean("useRestaurantFilterTemp", useRestaurantFilter);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static boolean  getUseRestaurantFilterTemp(Context context){

        boolean useRestaurantFilter = false;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            useRestaurantFilter = prefs.getBoolean("useRestaurantFilterTemp", false);
        }catch(Exception e){
            ;
        }

        return useRestaurantFilter;
    }



    public static void setRestaurantFilterType(Context context,String RestaurantFilterType){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("RestaurantFilterType", RestaurantFilterType);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public  static String  getRestaurantFilterType(Context context){

        String RestaurantFilterType = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            RestaurantFilterType = prefs.getString("RestaurantFilterType", "");
        }catch(Exception e){
            ;
        }

        return RestaurantFilterType;
    }





    public static void setTextFoodListCache(Context context,String foodListCache){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("foodListCache", foodListCache);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static String  getTextFoodListCache(Context context){

        String foodFilterType = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            foodFilterType = prefs.getString("foodListCache", null);
        }catch(Exception e){
            ;
        }

        return foodFilterType;
    }


    public static void setUseFoodListCache(Context context,Boolean useFoodListCache){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putBoolean("useFoodListCache", useFoodListCache);
            preferences.commit();
        }catch (Exception e){
            ;
        }
    }

    public static boolean  getUseFoodListCache(Context context){

        boolean useFoodListCache = false;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            useFoodListCache = prefs.getBoolean("useFoodListCache", false);
        }catch(Exception e){
            ;
        }

        return useFoodListCache;
    }




        public static String getCurrentCurrency(Context context){

            String languageName = null;
            try{
                SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
                languageName = prefs.getString("currency", null);
            }catch(Exception e){
                ;
            }

            return languageName;
        }

        public static void setCurrentCurrency(Context context,String currency){
            try {
                SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();

                preferences.putString("currency", currency);
                preferences.commit();
            }catch (Exception e){
                Utils.setDebug("crash", e.getLocalizedMessage());
            }

        }


        public static void setStringCurrency(Context context,String languageCode){
            try {
                SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
                preferences.putString("stringCurrency", languageCode);
                preferences.commit();
            }catch (Exception e){
                ;
            }
        }

        public static String getStringCurrency(Context context){

            String currencyString = null;
            try{
                SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
                currencyString = prefs.getString("stringCurrency", null);
            }catch(Exception e){
                ;
            }

            return currencyString;
        }



        public static void hideSoftKeyboard(Context context,View view){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(MainActivity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        public static void showSoftKeyboard(Context context,View view){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(MainActivity.INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
        }

        public static boolean checkGPSEnabled(Context context){
            LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) |  mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return enabled;
        }

        public static void intentLocationSetting(Context context){
            Intent intent =  new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        }


    public static void showEnableLocationDialog(final Context context,String title,String detail){
        try{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View languageLayout = inflater.inflate(R.layout.dialog_layout, null);

            TextView detailView = (TextView) languageLayout.findViewById(R.id.detail);
            detailView.setText(detail);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(languageLayout);

            builder.setTitle(title);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.enable),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Utils.intentLocationSetting(context);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }catch(Exception e){
            ;
        }

    }


    public static void showDialog(Context context,String title,String detail){

        try{

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View languageLayout = inflater.inflate(R.layout.dialog_layout, null);

            TextView detailView = (TextView) languageLayout.findViewById(R.id.detail);
            detailView.setText(detail);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(languageLayout);

            builder.setTitle(title);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }catch(Exception e){
            ;
        }

    }

    public static void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }



    public static void showConnectionProblemDialog(Context context){

        try{

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View languageLayout = inflater.inflate(R.layout.dialog_layout, null);

            TextView detailView = (TextView) languageLayout.findViewById(R.id.detail);
            detailView.setText(context.getResources().getString(R.string.Connection_Problem_Detail));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(languageLayout);

            builder.setTitle(context.getResources().getString(R.string.Connection_Problem));
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }catch(Exception e){
            ;
        }

    }



    public static void showCannotConnectInternet(Context context){

        try{

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View languageLayout = inflater.inflate(R.layout.dialog_layout, null);

            TextView detailView = (TextView) languageLayout.findViewById(R.id.detail);
            detailView.setText(context.getResources().getString(R.string.cannot_connect_internet));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(languageLayout);

            builder.setTitle(context.getResources().getString(R.string.Connection_Problem));
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }catch(Exception e){
            ;
        }

    }



        public static void writeToFile(String txtData) {
            try {
                File myFile = new File(Environment.getExternalStorageDirectory()+"/apps.txt");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(txtData);
                myOutWriter.close();
                fOut.close();

            }catch(Exception e) {
                ;
            }
        }


        public static HttpParams httpParams(){

            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = Utils.TIMEOUT_CONNECTION;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket =  Utils.TIMEOUT_SOCKET;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            return httpParameters;
        }


        public static float convertDpToPx(Context context,int dp){
            Resources resources = context.getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
            return px;
        }

        public static void initPicasso(Context context){
            Picasso picasso = Picasso.with(context);
            picasso.setLoggingEnabled(true);
        }

        public static void setImageWithPicasso(Context context,String imageUrl,ImageView imageView){
            Picasso.with(context).load(imageUrl).placeholder(R.drawable.no_image_small).error(R.drawable.no_image_small).into(imageView);
        }

        public static void setImageWithPicassoCallback(Context context,String imageUrl,ImageView imageView,Callback callback){
            Picasso.with(context).load(imageUrl).placeholder(R.drawable.no_image_small).error(R.drawable.no_image_small).into(imageView, callback);
        }


        /*public static void clearCacheImage(Context context){
            Glide.get(context).clearDiskCache();
        }*/

    public static String getDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2) / 1000;
        String distance = String.format("%.2f", distanceInMeters);
        return String.valueOf(distance);
    }

        public static boolean isNetworkOnline(Context context) {
            boolean status=false;
            try{
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getNetworkInfo(0);
                if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                    status= true;
                }else {
                    netInfo = cm.getNetworkInfo(1);
                    if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                        status= true;
                }
            }catch(Exception e){
                Utils.setDebug("crash", e.getLocalizedMessage());
                return false;
            }
            return status;

        }




        public static void setLanguageApp(Context context,String langCode){

            Locale locale = new Locale(langCode);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getApplicationContext().getResources().updateConfiguration(config, null);
        }


        public static String  mapTypeTag(Context mContext,String type){
            switch (type){

                case "restaurant_name":
                    type = mContext.getString(R.string.restaurant_name);
                    break;
                case "restaurant_type":
                    type = mContext.getString(R.string.restaurant_type);
                    break;
                case "restaurant_service":
                    type = mContext.getString(R.string.restaurant_service);
                    break;
                case "food_name":
                    type = mContext.getString(R.string.food_name)+"/"+mContext.getString(R.string.Description);
                    break;
                case "food_category":
                    type = mContext.getString(R.string.food_category);
                    break;
                case "food_cuisine":
                    type = mContext.getString(R.string.food_cuisine);
                    break;
                case "food_ingredient":
                    type = mContext.getString(R.string.food_ingredient);
                    break;
                case "food_meal":
                    type = mContext.getString(R.string.food_meal);
                    break;

                default:
                    type ="";
                    break;
            }

            return type;
        }



    public static void setLanguageTextCache(Context context, String languageJson){

        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("languageString", languageJson);
            preferences.apply();
        }catch (Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public static String getLanguageTextCache(Context context){

        String languageString = null;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            languageString = prefs.getString("languageString", null);
        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

        return languageString;
    }



    public static HashMap<String,String> getHashMapLanguage(Context context){

        HashMap<String,String> language =  new HashMap<>();
        language.put("English","en");
        language.put("Arabic","ar");
        language.put("German","de");
        language.put("Spanish","es");
        language.put("Hindi","hi");
        language.put("Italian","it");
        language.put("Hebrew","iw");
        language.put("Russian","ru");
        language.put("Chinese","zh");
        language.put("Greek","el");
        language.put("French","fr");
        language.put("Japanese","ja");
        language.put("Korean","ko");
        language.put("Polish","pl");
        language.put("Thai","th");
        language.put("Turkish","tr");

        SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
        String languageString = prefs.getString("languageString", null);

        if(languageString!=null){  // update language from cache

            try {
                JSONObject jObjectResult = new JSONObject(languageString);
                JSONObject jObject = jObjectResult.getJSONObject("Results");
                Iterator<String> keys = jObject.keys();

                for(int i=0;i<jObject.length();i++) {
                    String name = keys.next();
                    JSONObject langObject = jObject.getJSONObject(name);
                    String keyName  = langObject.getString("name");
                    keyName =  keyName.substring(0,1).toUpperCase()+ keyName.substring(1).toLowerCase(); //capital first char
                    language.put(keyName, name);
                    LanguageModel aLanguageObject = new LanguageModel(langObject.getString("name"),
                            langObject.getInt("id"),
                            langObject.getString("short_name"),
                            langObject.getString("lang_code"),
                            langObject.getString("icon"));
                    MoreDataModel.languageObjectList.add(aLanguageObject);
                }
            } catch (JSONException e) {
                Utils.setDebug("crash", e.getLocalizedMessage());
            }
        }

        return language;
    }



    public static void setCurrentServer(Context context,int server){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putInt("server", server);
            preferences.commit();

        }catch (Exception e){
            ;
        }

    }

    public static int getCurrentServer(Context context){

        int server = 0;
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            server = prefs.getInt("server",0);
        }catch(Exception e){
            ;
        }

        return server;
    }




    public static void setTitleSearchResultActionbar(Context context,ActionBar actionBar,String typeTitle,String data) {

        actionBar.setDisplayHomeAsUpEnabled(true);

        String filterMode = Utils.getTopCategoryOrAutocompleteFilterMode(context);
        String title = "";
        if(filterMode.equals(Utils.CATEGORY_FILTER_MODE)){
            title = context.getResources().getString(R.string.category) + " \"" + data + "\"" ;
        }else if(filterMode.equals(Utils.AUTOCOMPLETE_FILTER_MODE)){

            String category = Utils.mapTypeTag(context,typeTitle);
            title = category + " \"" + data + "\"" ;
        }
        actionBar.setTitle(title);

    }


    public  static void setDebug(String debugKey,String debugValue){
        try{

            Log.d(debugKey,debugValue);

        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

    }


    public static boolean isGoogleMapsInstalled(Context context)
    {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            if(info.enabled)
                return true;
            else
                return false;

        }catch(PackageManager.NameNotFoundException e){
            return false;
        }
    }

    public interface callbackShowDialog{
            public void showDialog(Context context,String title,String detail);
            public void showDialogUpdatePlayService();
        }






///////////////////////////





    public static void setKeyword(Context context,String data){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("keywordTemp", data);
            preferences.commit();

        }catch (Exception e){
            ;
        }

    }

    public static String getKeyword(Context context){

        String data = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            data = prefs.getString("keywordTemp", "");
        }catch(Exception e){
            ;
        }

        return data;
    }



    public static void setCategory(Context context,String data){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("categoryTemp", data);
            preferences.commit();

        }catch (Exception e){
            ;
        }

    }

    public static String getCategory(Context context){

        String data = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            data = prefs.getString("categoryTemp", "");
        }catch(Exception e){
            ;
        }

        return data;
    }


    public static void setRadius(Context context,String data){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("radiusTemp", data);
            preferences.commit();

        }catch (Exception e){
            ;
        }

    }

    public static String getRadius(Context context){

        String data = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            data = prefs.getString("radiusTemp", "");
        }catch(Exception e){
            ;
        }

        return data;
    }


    public static void setLocation(Context context,String data){
        try {
            SharedPreferences.Editor preferences = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE).edit();
            preferences.putString("locationTemp", data);
            preferences.commit();

        }catch (Exception e){
            ;
        }

    }

    public static String getLocation(Context context){

        String data = "";
        try{
            SharedPreferences prefs = context.getSharedPreferences(ID_PREFERENCE, context.MODE_PRIVATE);
            data = prefs.getString("locationTemp", "");
        }catch(Exception e){
            ;
        }

        return data;
    }

    /**
     * Convert byte array to hex string
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for(int idx=0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     * @param str
     * @return  array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try { return str.getBytes("UTF-8"); } catch (Exception ex) { return null; }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN=1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8=false;
            int read,count=0;
            while((read=is.read(bytes)) != -1) {
                if (count==0 && bytes[0]==(byte)0xEF && bytes[1]==(byte)0xBB && bytes[2]==(byte)0xBF ) {
                    isUTF8=true;
                    baos.write(bytes, 3, read-3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count+=read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try{ is.close(); } catch(Exception ex){}
        }
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     * @param //ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }


    public static boolean isEmailValid(String email) {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        if (email.length() > 0 && email.matches(emailPattern)) {
//            return true;
//        }
        if (email.length() > 0 && email.contains("@")) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    public static boolean isUsernameValid(String name) {
        return name.length() >= 3;
    }


    public static void showNoResultDialog(final Context context, final String searchText, final String distance, final double lat, final double lon) {
        String url = ConfigURL.reverseGeoCodingURL;
        String latlngParam = "latlng=" + lat + "," + lon;
        String keyParam =  "key=" + context.getString(R.string.googlemap_geocoding_apikey);
        url += "?" + latlngParam + "&" + keyParam;

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String address = "";
                try {
                    String country = "", state = "", county = "", city = "", town = "", postal_code = "";
                    JSONArray resultArray = response.getJSONArray("results");
                    if (resultArray.length() > 0) {
                        JSONObject aResult = resultArray.getJSONObject(0);
                        JSONArray address_componentsArray = aResult.getJSONArray("address_components");
                        for (int i = 0; i < address_componentsArray.length(); i ++) {
                            JSONObject aComponent = address_componentsArray.getJSONObject(i);
                            String longname = aComponent.getString("long_name");
                            String shortname = aComponent.getString("short_name");
                            JSONArray typesArray = aComponent.getJSONArray("types");
                            String type1 = typesArray.getString(0);

                            if ("sublocality".equals(type1)) {
                                town = shortname;
                            }
                            else if ("locality".equals(type1)) {
                                city = shortname;
                            }
                            else if ("administrative_area_level_2".equals(type1)) {
                                county = shortname;
                            }
                            else if ("administrative_area_level_1".equals(type1)) {
                                state = shortname;
                            }
                            else if ("country".equals(type1)) {
                                country = longname;
                            }
                            else if ("postal_code".equals(type1)) {
                                postal_code = shortname;
                            }
                        }
                    }

                    if (!"".equals(city)) {
                        address = city;
                    }
                    else if (!"".equals(town)) {
                        address = town;
                    }
                    address += ", ";
                    if (!"".equals(state)) {
                        address += state;
                    }
                    else if (!"".equals(county)) {
                        address += county;
                    }
                    address += " ";
                    if (!"".equals(postal_code)) {
                        address += postal_code;
                    }
                    address += ", ";
                    if (!"".equals(country)) {
                        address += country;
                    }

                    String message = "You are looking for: " + searchText + "\n"
                            + "in " + address + " < " + distance + "km\n\n"
                            + context.getString(R.string.No_Search_Result);
                    showDialog(context, context.getString(R.string.Search_Result), message);

                } catch (JSONException e) {
                    Utils.setDebug("crash", e.getLocalizedMessage());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.setDebug("failure", res);
            }
        });
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    public static LocationModel mLocationDetails = null;
    public static void setLocationDetails(LocationModel aLocation) {
        if (aLocation != null) {
            mLocationDetails = aLocation;
        }
    }

    public static LocationModel getLocationDetails() {
        return mLocationDetails;
    }

    public static void initAllGlobalData() {
        initNavData();
        initFoodData();
        initRestaurantData();
        initMenuData();
    }

    public static void initNavData() {
        MoreDataModel.languageObjectList.clear();
        MoreDataModel.currencyObjectList.clear();
        MoreDataModel.userAvatarUrl = "";
        MoreDataModel.userEmail = "";
        MoreDataModel.userFirstname = "";
        MoreDataModel.userLastname = "";
    }

    public static void initFoodData() {
        MoreFoodDataModel.foodCategoryList.clear();
        MoreFoodDataModel.foodCuisineList.clear();
        MoreFoodDataModel.foodSelCategoryIndexList.clear();
        MoreFoodDataModel.foodCategorySelectedCount = 0;
        MoreFoodDataModel.foodSelCuisine = null;
        MoreFoodDataModel.searchText = "";
        MoreFoodDataModel.searchType = "";
        MoreFoodDataModel.tabNo = 0;
    }

    public static void initRestaurantData() {
        MoreRestaurantDataModel.restaurantFacilityList.clear();
        MoreRestaurantDataModel.restaurantCuisineList.clear();
        MoreRestaurantDataModel.restaurantSelFacilityIndexList.clear();
        MoreRestaurantDataModel.restaurantFacilitySelectedCount = 0;
        MoreRestaurantDataModel.restaurantSelCuisine = null;
        MoreRestaurantDataModel.searchText = "";
        MoreRestaurantDataModel.searchType = "";
    }

    public static void initMenuData() {
        MoreMenuModel.restaurantInfo = null;
        MoreMenuModel.foodCategories.clear();
        MoreMenuModel.drinkCategories.clear();
    }

}
