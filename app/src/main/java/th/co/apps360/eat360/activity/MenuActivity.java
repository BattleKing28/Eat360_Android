package th.co.apps360.eat360.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import th.co.apps360.eat360.APIs.FoodDetailAPI;
import th.co.apps360.eat360.APIs.FoodImageAPI;
import th.co.apps360.eat360.APIs.FoodIngredientsAPI;
import th.co.apps360.eat360.APIs.GetUserInfoAPI;
import th.co.apps360.eat360.APIs.LogoutAPI;
import th.co.apps360.eat360.APIs.RestaurantDetailAPI;
import th.co.apps360.eat360.APIs.RestaurantDrinkAPI;
import th.co.apps360.eat360.APIs.RestaurantFoodAPI;
import th.co.apps360.eat360.APIs.RestaurantMenuByCategoryAPI;
import th.co.apps360.eat360.APIs.RestaurantMenuCategoryAPI;
import th.co.apps360.eat360.APIs.UpdateUsernameAPI;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.FacilityModel;
import th.co.apps360.eat360.Model.MoreMenuModel;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.Model.RestaurantClosedayModel;
import th.co.apps360.eat360.Model.RestaurantInfoModel;
import th.co.apps360.eat360.Model.RestaurantTimeModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.adapter.CardViewDishAdapter;
import th.co.apps360.eat360.adapter.MenuCategoryAdapter;
import th.co.apps360.eat360.fragment.FragmentFoodDetail;
import th.co.apps360.eat360.fragment.FragmentRestaurantDetail;
import th.co.apps360.eat360.fragment.FragmentRestaurantMenu;
import th.co.apps360.eat360.fragment.MoreMenuDrawerFragment;
import th.co.apps360.eat360.fragment.NavigationDrawerFragment;

public class MenuActivity extends AppCompatActivity
        implements
        RestaurantDetailAPI.ResultCallback,
        RestaurantFoodAPI.ResultCallback,
        RestaurantDrinkAPI.ResultCallback,
        RestaurantMenuCategoryAPI.ResultCallback,
        RestaurantMenuByCategoryAPI.ResultCallback,
        GetUserInfoAPI.ResultCallback,
        UpdateUsernameAPI.ResultCallback,
        LogoutAPI.ResultCallback,
        CardViewDishAdapter.OnDishInfoActionListener,
        MenuCategoryAdapter.OnFoodInfoActionListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        MoreMenuDrawerFragment.OnFragmentInteractionListener,
        FragmentRestaurantDetail.OnFragmentInteractionListener,
        FragmentRestaurantMenu.OnFragmentInteractionListener {

    public static final String MEAL_TYPE= "meal_type" ;
    public static final String CATEGORY_TYPE = "category_type";
    public static final String FOOD_TYPE= "food_type" ;
    public static final String DRINK_TYPE = "drink_type";

    private String mTarget;
    private String mRestaurantId;
    private String mRestaurantName;
    private String mRestaurantImagePath;
    private String mRestaurantAddress;
//    private String mRestaurantDistance;
    private Double mUserLatitude;
    private Double mUserLongitude;

    private ActionBar mActionBar;

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private MoreMenuDrawerFragment mMoreDrawerFragment;
    private FragmentRestaurantMenu menuFragment;
    private FragmentRestaurantDetail infoFragment;
    public Fragment currentFragment;

    private ArrayList<AsyncTask> allAsyncTask = new ArrayList<>();
    public MoreMenuModel.Menu_Tab menupageIndex = MoreMenuModel.Menu_Tab.FOOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        receiveParamsFromIntent();
        initActionBar();
        initResources();
        initData();
        initFragment();
    }

    private void receiveParamsFromIntent() {
        mTarget = this.getIntent().getStringExtra("target");
        mRestaurantId = this.getIntent().getStringExtra("restaurantId");
        mRestaurantName = this.getIntent().getStringExtra("restaurantName");
        mRestaurantImagePath = this.getIntent().getStringExtra("restaurantImagePath");
        mRestaurantAddress = this.getIntent().getStringExtra("restaurantAddress");
        mUserLatitude = this.getIntent().getDoubleExtra("userLatitude", 0.0);
        mUserLongitude = this.getIntent().getDoubleExtra("userLongitude", 0.0);
//        mRestaurantDistance = this.getIntent().getStringExtra("restaurantDistance");
    }

    public String getRestaurantId() {
        return mRestaurantId;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public String getRestaurantImagePath() {
        return mRestaurantImagePath;
    }

    public String getRestaurantAddress() {
        return mRestaurantAddress;
    }

//    public String getRestaurantDistance() {
//        return mRestaurantDistance;
//    }

    public Double getUserLatitude() {
        return mUserLatitude;
    }

    public Double getUserLongitude() {
        return mUserLongitude;
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setCustomView(R.layout.actionbar);
        }

        ImageButton nav_menu = (ImageButton) findViewById(R.id.nav_menu);
        nav_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
                    mDrawerLayout.closeDrawers();
                else
                    mDrawerLayout.openDrawer(GravityCompat.END);
            }
        });

        ImageView nav_back = (ImageView) findViewById(R.id.nav_back);
        nav_back.setVisibility(View.VISIBLE);
        nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // return MainActivity
                onBackPressed();
            }
        });

        TextView nav_title = (TextView) findViewById(R.id.nav_title);
        nav_title.setVisibility(View.VISIBLE);
        nav_title.setText(mRestaurantName);
        ImageView nav_logo = (ImageView) findViewById(R.id.nav_logo);
        nav_logo.setVisibility(View.GONE);
    }

    public ImageView showBackButton() {
        ImageView backButton = (ImageView) mActionBar.getCustomView().findViewById(R.id.nav_back);
        backButton.setVisibility(View.VISIBLE);
        return backButton;
    }

    public void hideBackButton() {
        ImageView backButton = (ImageView) mActionBar.getCustomView().findViewById(R.id.nav_back);
        backButton.setVisibility(View.GONE);
    }

    public TextView showTitle() {
        ImageView logoImageView = (ImageView) mActionBar.getCustomView().findViewById(R.id.nav_logo);
        logoImageView.setVisibility(View.GONE);

        TextView titleTextView = (TextView) mActionBar.getCustomView().findViewById(R.id.nav_title);
        titleTextView.setVisibility(View.VISIBLE);
        return titleTextView;
    }

    public void showLogo() {
        ImageView logoImageView = (ImageView) mActionBar.getCustomView().findViewById(R.id.nav_logo);
        logoImageView.setVisibility(View.VISIBLE);

        TextView titleTextView = (TextView) mActionBar.getCustomView().findViewById(R.id.nav_title);
        titleTextView.setVisibility(View.GONE);
    }

    private void initResources() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMoreDrawerFragment = (MoreMenuDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.more_menu_drawer);
        mMoreDrawerFragment.setUp(R.id.more_menu_drawer, (DrawerLayout)findViewById(R.id.drawer_layout), menupageIndex);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));
//        if (mNavigationDrawerFragment != null) {
//            mNavigationDrawerFragment.downloadLanguage();
//            mNavigationDrawerFragment.downloadCurrency();
//        }
    }

    private void initFragment() {
        if (mTarget.equals("menu")) {
//            menuFragment = new FragmentRestaurantMenu();
            currentFragment = new FragmentRestaurantMenu();
            Bundle args = new Bundle();
            args.putString("restaurantId", mRestaurantId);
            args.putString("restaurantName", mRestaurantName);
            args.putString("restaurantImagePath", mRestaurantImagePath);
            args.putString("restaurantAddress", mRestaurantAddress);
//            args.putString("restaurantDistance", mRestaurantDistance);
            currentFragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.container, currentFragment)
                    .addToBackStack(null).commit();
//            initData();
        }
        else if (mTarget.equals("info")) {
            currentFragment = new FragmentRestaurantDetail();
            Bundle args = new Bundle();
            args.putString("restaurantId", mRestaurantId);
            args.putString("restaurantName", mRestaurantName);
            args.putString("restaurantImagePath", mRestaurantImagePath);
            args.putString("restaurantAddress", mRestaurantAddress);
//            args.putString("restaurantDistance", mRestaurantDistance);
            currentFragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.container, currentFragment)
                    .addToBackStack(null).commit();
        }
    }

    private void setCurrentToMenuFragment() {
        currentFragment = (FragmentRestaurantMenu) getSupportFragmentManager().findFragmentById(R.id.menu_fragment);
        if (currentFragment == null) {
            currentFragment = new FragmentRestaurantMenu();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFragment)
                    .addToBackStack(null).commit();
        }
    }

    private void initData() {
        allAsyncTask.add(new RestaurantDetailAPI(this, mRestaurantId).execute(new ConfigURL(this).restaurantDetailURL));
//        allAsyncTask.add(new RestaurantMenuCategoryAPI(this, mRestaurantId).execute(new ConfigURL(this).restaurantMenuCategoryURL));
//        allAsyncTask.add(new RestaurantFoodAPI(this, mRestaurantId).execute(new ConfigURL(this).restaurantFoodURL));
//        allAsyncTask.add(new RestaurantDrinkAPI(this, mRestaurantId).execute(new ConfigURL(this).restaurantDrinkURL));
    }

    public void loadRestaurantInfo(String jsonString) {
        MoreMenuModel.restaurantInfo = null;
        try {
            JSONObject JSON = new JSONObject(jsonString);
            JSONArray results = JSON.getJSONArray("Results");
            if (results.length() > 0) {
                JSONObject anInfo = results.getJSONObject(0);
                String desc = anInfo.getString("description");
                JSONArray type = anInfo.getJSONArray("restaurant_types");
                String type_id = "";
                String type_name = "";
                if (type.length() > 0) {
                    type_id = type.getJSONObject(0).getString("id");
                    type_name = type.getJSONObject(0).getString("restaurant_type_name");
                }
                Double latitude = anInfo.getDouble("latitude");
                Double longitude = anInfo.getDouble("longitude");
                String postcode = anInfo.getString("postcode");
                String url = anInfo.getString("url");
                String email = anInfo.getString("email");
                String phone = anInfo.getString("phone");
                String fax = anInfo.getString("fax");
                String rest_id = anInfo.getString("restaurant_id");
                String rest_owner_id = anInfo.getString("restaurant_owner_id");
                String rest_name = anInfo.getString("name");
                String color_title = anInfo.getString("color_title");
                String address = anInfo.getString("address");
                String city = anInfo.getString("city");
                String state = anInfo.getString("state");
                String country = anInfo.getString("country");
                JSONArray openinghourArray = anInfo.getJSONArray("opening_hours");
                ArrayList<RestaurantTimeModel> openinghours = new ArrayList<>();
                for (int i = 0; i < openinghourArray.length(); i++) {
                    JSONObject anObject = openinghourArray.getJSONObject(i);
                    RestaurantTimeModel aTime = new RestaurantTimeModel(anObject.getBoolean("isActive"), i,
                            anObject.getString("timeFrom"), anObject.getString("timeTill"),
                            anObject.getString("breakFrom"), anObject.getString("breakTill"));
                    openinghours.add(aTime);
                }
                JSONArray weekendArray = anInfo.getJSONArray("weekends");
                ArrayList<RestaurantClosedayModel> weekenddates = new ArrayList<>();
                for (int i = 0; i < weekendArray.length(); i++) {
                    JSONObject anObject = weekendArray.getJSONObject(i);
                    String aTitle = anObject.getString("title");
                    String aDay = anObject.getString("date");
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                    Date tempDate = null;
                    try {
                        tempDate = formatter.parse(aDay);
                    } catch (ParseException e) {
                        Utils.setDebug("crash", e.getLocalizedMessage());
                    }
                    formatter = new SimpleDateFormat("MMMM");
                    String month = formatter.format(tempDate);
                    formatter = new SimpleDateFormat("dd");
                    String day = formatter.format(tempDate);
                    Utils.setDebug("MenuActivity", String.format("%s %s", month, day));
                    RestaurantClosedayModel aDate = new RestaurantClosedayModel(day, month, anObject.getString("title"));
                    weekenddates.add(aDate);
                }
                String paid_to = anInfo.getString("paid_to");
                Boolean is_active = Boolean.valueOf(anInfo.getString("is_active"));
                String owner_id = anInfo.getString("owner_id");
                String image_path = anInfo.getString("img_path");
                JSONObject image = anInfo.getJSONObject("images");
                ArrayList<String> imageList = new ArrayList<>();
                if (null != image) {
                    String images = image.getString("images");
                    String mainimage = image.getString("main_image");
                    imageList.add(mainimage);
                    imageList.add(images);
                }
                String facebooklink = anInfo.getString("facebook");
                String instagramlink = anInfo.getString("instagram");
                String twitterlink = anInfo.getString("twitter");
                String googlelink = anInfo.getString("google");

                ArrayList<FacilityModel> facilities = new ArrayList<>();
                if (anInfo.has("facilities")) {
                    Object json = anInfo.get("facilities");
                    if (json instanceof JSONArray) {
                        JSONArray facilityArray = anInfo.getJSONArray("facilities");
                        for (int j = 0; j < facilityArray.length(); j++) {
                            JSONObject aFacilityObject = facilityArray.getJSONObject(j);
                            Integer id = Integer.valueOf(aFacilityObject.getString("id"));
                            String name = aFacilityObject.getString("name");
                            String icon = aFacilityObject.getString("icon");
                            FacilityModel aFacilityModel = new FacilityModel(id, "en", name, icon, "");
                            facilities.add(aFacilityModel);
                        }
                    }
                    else if (json instanceof JSONObject) {
                        JSONObject facilityObject = anInfo.getJSONObject("facilities");
                        Iterator iterator = facilityObject.keys();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Integer id = Integer.valueOf(facilityObject.getString(key));
                            for (int i = 0; i < MoreRestaurantDataModel.restaurantFacilityList.size(); i ++) {
                                FacilityModel aFacilityModel = MoreRestaurantDataModel.restaurantFacilityList.get(i);
                                if (id.equals(aFacilityModel.getId())) {
                                    facilities.add(aFacilityModel);
                                    break;
                                }
                            }
                        }
                    }
                }

                MoreMenuModel.restaurantInfo = new RestaurantInfoModel(rest_id, rest_name, desc, url,
                        email, phone, fax, latitude, longitude, postcode, address, city,
                        state, country, openinghours, weekenddates, facilities, paid_to, is_active,
                        color_title, owner_id, type_id, type_name, image_path, imageList,
                        facebooklink, instagramlink, twitterlink, googlelink);
                if (currentFragment instanceof FragmentRestaurantMenu)
                    ((FragmentRestaurantMenu)currentFragment).showRestaurantInfo();
                else if (currentFragment instanceof FragmentRestaurantDetail)
                    ((FragmentRestaurantDetail)currentFragment).showRestaurantInfo();
            }
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public void requestRestaurantFoodData() {
        for (int i = 0; i < MoreMenuModel.foodCategories.size(); i ++) {
            String category_id = MoreMenuModel.foodCategories.get(i).getId();
            if (!"49".equals(category_id)) {
                // non beverages
                allAsyncTask.add(new RestaurantMenuByCategoryAPI(this, mRestaurantId, category_id));
                break;
            }
        }
    }

    @Override
    public void onDestroy() {  // cancel all asynctask before close activity
        for (AsyncTask asyncItem : allAsyncTask ) {
            if (asyncItem != null && !asyncItem.isCancelled()) {
                asyncItem.cancel(true);
            }
        }
        MoreMenuModel.foodCategories.clear();
        MoreMenuModel.drinkCategories.clear();
        MoreMenuModel.restaurantInfo = null;
        super.onDestroy();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setLanguage();
            mNavigationDrawerFragment.setCurrency();
        }
    }


    @Override
    public void onBackPressed() {
        if (mMoreDrawerFragment.isDrawerOpen()) {
            mMoreDrawerFragment.closeDrawer();
        }
        else {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            super.onBackPressed();
            if (backStackEntryCount <= 1) {
                this.finish();
                Utils.initMenuData();
            }
        }
    }

    @Override
    public void restaurantAPIResultCallback(String jsonStringResult) {
        loadRestaurantInfo(jsonStringResult);
    }

    @Override
    public void searchRestaurantFoodResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentRestaurantMenu)
            ((FragmentRestaurantMenu)currentFragment).setFoodData(jsonStringResult);
//        if (MoreMenuModel.menuCategories.size() > 0) {
//            menuFragment.getInstance().showRestaurantFood();
//        }
    }

    @Override
    public void searchRestaurantDrinkResultCallback(String jsonStringResult) {
//        setCurrentToMenuFragment();
        if (currentFragment instanceof FragmentRestaurantMenu)
            ((FragmentRestaurantMenu)currentFragment).setDrinkData(jsonStringResult);
//        menuFragment.getInstance().showRestaurantDrink();
    }

    @Override
    public void searchRestaurantMenuCategoryCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentRestaurantMenu)
            ((FragmentRestaurantMenu)currentFragment).setCategoryData(jsonStringResult);
    }

    @Override
    public void searchRestaurantMenuByCategoryCallback(String jsonStringResult) {

    }

    @Override
    public void onLanguageSelected() {
        initData();
        if (currentFragment instanceof FragmentRestaurantMenu) {
            ((FragmentRestaurantMenu) currentFragment).changeLanguage();
        } else if (currentFragment instanceof FragmentRestaurantDetail) {
            ((FragmentRestaurantDetail) currentFragment).receiveParams();
        }
    }

    @Override
    public void onCurrencySelected() {
        if (currentFragment instanceof FragmentFoodDetail) {
            ((FragmentFoodDetail) currentFragment).changeCurrency();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void openMoreButtonStateCallback() {
        if (currentFragment instanceof FragmentRestaurantMenu)
            ((FragmentRestaurantMenu)currentFragment).setMoreButtonStateforOpenedDrawer();
    }

    @Override
    public void closeMoreButtonStateCallback() {
        if (currentFragment instanceof FragmentRestaurantMenu)
            ((FragmentRestaurantMenu)currentFragment).setMoreButtonStateforClosedDrawer();
    }

    @Override
    public void goButtonActionFoodCallback() {
        if (currentFragment instanceof FragmentRestaurantMenu)
            ((FragmentRestaurantMenu)currentFragment).showRestaurantFoodWithFilter();
    }

    @Override
    public void goButtonActionDrinkCallback() {
        if (currentFragment instanceof FragmentRestaurantMenu)
            ((FragmentRestaurantMenu)currentFragment).showRestaurantDrinkWithFilter();
    }

    @Override
    public void setMoreRestaurantCallback() {
        if (menupageIndex == MoreMenuModel.Menu_Tab.FOOD)
            mMoreDrawerFragment.loadMenuDrawerFoodData();
        else if (menupageIndex == MoreMenuModel.Menu_Tab.DRINK) {
            mMoreDrawerFragment.loadMenuDrawerDrinkData();
        }
    }

    @Override
    public void showMoreRestaurantCallback() {
        mMoreDrawerFragment.openDrawer();
    }

    @Override
    public void passParamsToDishInfoCallback(Bundle bundle) {
        currentFragment = new FragmentFoodDetail();
        currentFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, currentFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void passParamsToFoodInfoCallback(Bundle bundle) {
        currentFragment = new FragmentFoodDetail();
        currentFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, currentFragment)
                .addToBackStack(null).commit();
    }


    @Override
    public void getUserInfoCallback(String jsonStringResult) {
        mNavigationDrawerFragment.getUserInfoResult(jsonStringResult);
    }

    @Override
    public void logoutCallback(String jsonStringResult) {
        try {
            JSONObject mainObject = new JSONObject(jsonStringResult);
            if (!mainObject.has("Status")) {
                String message = mainObject.getString("StatusMsg");
                Utils.setDebug("logout", message);
            } else {
                String status = mainObject.getString("Status");
                if (status.equals("false")) {
                    String message = mainObject.getString("Message");
                    Utils.setDebug("logout", message);
                } else if (status.equals("true")) {
                    Utils.setDebug("logout", "logout ok");
                }
            }
            finish();
            Utils.initAllGlobalData();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        } catch (JSONException e) {
            finish();
//            Utils.setDebug("crash", e.getLocalizedMessage());
            Utils.initAllGlobalData();
            Utils.setDebug("crash", e.getLocalizedMessage());
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    @Override
    public void updateUsernameCallback(String jsonStringResult) {
        mNavigationDrawerFragment.replaceUsername(jsonStringResult);
    }
}
