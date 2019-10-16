package th.co.apps360.eat360.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import th.co.apps360.eat360.APIs.RestaurantDrinkAPI;
import th.co.apps360.eat360.APIs.RestaurantFoodAPI;
import th.co.apps360.eat360.APIs.RestaurantMenuCategoryAPI;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.MenuCategory;
import th.co.apps360.eat360.Model.MenuDish;
import th.co.apps360.eat360.Model.MoreMenuModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.MenuActivity;
import th.co.apps360.eat360.adapter.CardViewDishAdapter;
import th.co.apps360.eat360.adapter.MenuCategoryAdapter;

public class FragmentRestaurantMenu extends Fragment {

    private FragmentRestaurantMenu mInstance;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private ImageView mSplashImageView;
    private ImageView mPinButton;
    private ImageView mCallButton;
    private ImageView mMoreButton;
    private TextView mDistanceTextView;
    private TextView mAddressTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mMenuRecyclerView;
    private TextView mNoResult;
    private ImageButton mFoodTabButton;
    private ImageButton mDrinkTabButton;
    private ImageButton mMoreTabButton;
    private ImageView mBackButton;
    private MoreMenuDrawerFragment mMoreDrawerFragment;

    private ArrayList<MenuDish> foods = new ArrayList<>();
    private ArrayList<MenuDish> drinks = new ArrayList<>();
    private ArrayList<AsyncTask> allAsyncTask = new ArrayList<>();
    private MenuCategoryAdapter mFoodAdapter;

    private String mRestaurantId;
    private String mRestaurantName;
    private String mRestaurantImagePath;
    private String mRestaurantAddress;
    private String mRestaurantDistance;
    private Double mUserLatitude;
    private Double mUserLongitude;
    private Boolean showMoreDrawer = false;
    private Boolean isLoadedCategory = false;
    private Boolean isLoadedFood = false;
    private Boolean isLoadedDrink = false;
    private MenuActivity mActivity;
    public MoreMenuModel.Menu_Tab menupageIndex = MoreMenuModel.Menu_Tab.FOOD;


    public FragmentRestaurantMenu() {
        // Required empty public constructor
    }

    public FragmentRestaurantMenu getInstance() {
        if (mInstance == null) {
            mInstance = new FragmentRestaurantMenu();
        }
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        mActivity = (MenuActivity)getActivity();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_layout);
        mMenuRecyclerView = (RecyclerView) rootView.findViewById(R.id.menu_recycler_view);
        mNoResult = (TextView) rootView.findViewById(R.id.no_result);

        mSplashImageView = (ImageView) rootView.findViewById(R.id.rest_menu_splashimage);
        mPinButton = (ImageView) rootView.findViewById(R.id.rest_menu_pin_button);
        mCallButton = (ImageView) rootView.findViewById(R.id.rest_menu_call_button);
        mMoreButton = (ImageView) rootView.findViewById(R.id.rest_menu_more_button);
        mDistanceTextView = (TextView) rootView.findViewById(R.id.rest_menu_distance_text);
        mAddressTextView = (TextView) rootView.findViewById(R.id.rest_menu_address_text);
        mFoodTabButton = (ImageButton) rootView.findViewById(R.id.food_tab);
        mFoodTabButton.setActivated(true);
        mDrinkTabButton = (ImageButton) rootView.findViewById(R.id.drink_tab);
        mMoreTabButton = (ImageButton) rootView.findViewById(R.id.more_tab);
        mMoreDrawerFragment = (MoreMenuDrawerFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.more_menu_drawer);

        receiveParams();
        initRecyclerView();
        setTitleActionbar();
        setOnTapButtons();
        downloadData();
        initListener();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.currentFragment = new FragmentRestaurantDetail();
                Bundle args = new Bundle();
                args.putString("restaurantId", mRestaurantId);
                args.putString("restaurantName", mRestaurantName);
                args.putString("restaurantImagePath", mRestaurantImagePath);
                args.putString("restaurantAddress", mRestaurantAddress);
                mActivity.currentFragment.setArguments(args);
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.menu_fragment, mActivity.currentFragment)
                        .addToBackStack(null).commit();
            }
        });

    }

    private void receiveParams() {
        mRestaurantId = mActivity.getRestaurantId();
        mRestaurantName = mActivity.getRestaurantName();
        mRestaurantImagePath = mActivity.getRestaurantImagePath();
        mRestaurantAddress = mActivity.getRestaurantAddress();
        mUserLatitude = mActivity.getUserLatitude();
        mUserLongitude = mActivity.getUserLongitude();
        showRestaurantInfo();
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager  = new LinearLayoutManager(this.getActivity());
        mMenuRecyclerView.setLayoutManager(mLayoutManager);
        mMenuRecyclerView.setHasFixedSize(true);
    }

    private void initListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadedCategory = false;
                isLoadedFood = false;
                isLoadedDrink = false;
                downloadData();
            }
        });
    }

    private void setOnTapButtons() {
        mFoodTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.menupageIndex = MoreMenuModel.Menu_Tab.FOOD;
                showRestaurantFood();
                mFoodTabButton.setActivated(true);
                mDrinkTabButton.setActivated(false);
                mMoreTabButton.setActivated(false);
                mFoodTabButton.setImageResource(R.drawable.food_hover);
                mDrinkTabButton.setImageResource(R.drawable.drink);
                mMoreTabButton.setImageResource(R.drawable.more_button);
            }
        });
        mDrinkTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.menupageIndex = MoreMenuModel.Menu_Tab.DRINK;
                showRestaurantDrink();
                mFoodTabButton.setActivated(false);
                mDrinkTabButton.setActivated(true);
                mMoreTabButton.setActivated(false);
                mFoodTabButton.setImageResource(R.drawable.food);
                mDrinkTabButton.setImageResource(R.drawable.drink_hover);
                mMoreTabButton.setImageResource(R.drawable.more_button);
            }
        });
        mMoreTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMoreDrawerFragment.isDrawerOpen()) {
                    mMoreDrawerFragment.closeDrawer();
                    mMoreTabButton.setActivated(false);
                    mMoreTabButton.setImageResource(R.drawable.more_button);
                }
                else {
                    onShowMoreDrawer();
                    mMoreDrawerFragment.openDrawer();
                    mMoreTabButton.setActivated(true);
                    mMoreTabButton.setImageResource(R.drawable.more_hover_button);
                }
            }
        });
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dial = new Intent();
                dial.setAction(Intent.ACTION_DIAL);
                dial.setData(Uri.parse("tel:" + MoreMenuModel.restaurantInfo.getPhone()));
                mActivity.startActivity(dial);
            }
        });
    }

    public void setTitleActionbar() {
        TextView mTitleTextView;
        mTitleTextView = ((MenuActivity) getActivity()).showTitle();
        String sTitle = "";
        if (null != mRestaurantName && !"".equals(mRestaurantName)) {
            sTitle = mRestaurantName;
        }
        mTitleTextView.setText(sTitle);
        mBackButton = ((MenuActivity) getActivity()).showBackButton();
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
    }

//    private void onBackPressed() {
//        if (mMoreDrawerFragment.isDrawerOpen())
//            mMoreDrawerFragment.closeDrawer();
//        else
//            mActivity.onBackPressed();
//    }

    public void changeLanguage() {
        receiveParams();
        if (!Utils.isNetworkOnline(getActivity())) {
            Utils.showCannotConnectInternet(getActivity());
        }
        else {
            showDownloadingAnimation();
            downloadData();
        }
    }

    private void onShowMoreDrawer() {
        showMoreDrawer = true;
        if (MoreMenuModel.foodCategories.size() > 0 && mMoreDrawerFragment != null) {
            setMoreData();
        }
        else {
            if (!Utils.isNetworkOnline(getActivity())) {
                Utils.showCannotConnectInternet(getActivity());
            }
            else {
                showDownloadingAnimation();
                downloadData();
            }
        }
    }

    private void downloadData() {
        showResultView();
        showDownloadingAnimation();
        if (mMenuRecyclerView != null) {
            mMenuRecyclerView.setAdapter(null);
        }
        allAsyncTask.add(new RestaurantMenuCategoryAPI(mActivity, mRestaurantId).execute(new ConfigURL(mActivity).restaurantMenuCategoryURL));
        allAsyncTask.add(new RestaurantFoodAPI(mActivity, mRestaurantId).execute(new ConfigURL(mActivity).restaurantFoodURL));
        allAsyncTask.add(new RestaurantDrinkAPI(mActivity, mRestaurantId).execute(new ConfigURL(mActivity).restaurantDrinkURL));
    }

    public void setMoreData() {
        if (getActivity() == null)
            return;

        mListener.setMoreRestaurantCallback();
        hideDownloadingAnimation();

        if (showMoreDrawer) {
            mListener.showMoreRestaurantCallback();
            showMoreDrawer = false;
        }
    }

    public void setDrinkData(String jsonString) {
        if (jsonString.equals(Utils.TIMEOUT_ERROR) || jsonString.equals(Utils.CONNECTION_ERROR)) {
            Utils.showConnectionProblemDialog(getActivity());
            showConnectionError();
        } else if (jsonString.length() > 0) {
            isLoadedDrink = true;
            loadDrinkData(jsonString);
            hideDownloadingAnimation();
            if (menupageIndex == MoreMenuModel.Menu_Tab.DRINK) {
                showRestaurantDrink();
            }
        }
    }

    public void setFoodData(String jsonString) {
        if (jsonString.equals(Utils.TIMEOUT_ERROR) || jsonString.equals(Utils.CONNECTION_ERROR)) {
            Utils.showConnectionProblemDialog(getActivity());
            showConnectionError();
        } else if (jsonString.length() > 0) {
            isLoadedFood = true;
            loadFoodData(jsonString);
            hideDownloadingAnimation();
            if (menupageIndex == MoreMenuModel.Menu_Tab.FOOD) {
                showRestaurantFood();
            }
        }
    }

    public void setCategoryData(String jsonString) {
        if (jsonString.equals(Utils.TIMEOUT_ERROR) || jsonString.equals(Utils.CONNECTION_ERROR)) {
            Utils.showConnectionProblemDialog(getActivity());
            showConnectionError();
        } else if (jsonString.length() > 0) {
            isLoadedCategory = true;
            loadCategoryData(jsonString);
        }
    }

    public void loadCategoryData(String jsonString) {
        try {
            JSONObject JSON = new JSONObject(jsonString);
            JSONArray results = JSON.getJSONArray("Results");
            if (results.length() <= 0) {
                isLoadedCategory = false;
                return;
            }

            MoreMenuModel.foodCategories.clear();
            MoreMenuModel.drinkCategories.clear();
            for (int i = 0; i < results.length(); i++) {
                JSONObject aCategory = results.getJSONObject(i);
                String category_id = aCategory.getString("food_category_id");
                String lang_code = aCategory.getString("lang_code");
                String desc = aCategory.getString("description");
                String icon = aCategory.getString("icon");
                if ("".equals(icon)) {
                    icon = new ConfigURL(mActivity).defaultURL + "/" + "assets/img/icon/category/dinner.png";
                }
                else {
                    icon = new ConfigURL(mActivity).defaultURL + "/" + icon;
                }
                String type = aCategory.getString("type");
                MenuCategory aCategoryModel = new MenuCategory(desc, icon, category_id, lang_code, type, null);
                if ("FOOD".equals(type)) {
                    MoreMenuModel.foodCategories.add(aCategoryModel);
                }
                else if ("DRINK".equals(type)) {
                    MoreMenuModel.drinkCategories.add(aCategoryModel);
                }
            }
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public void loadFoodData(String jsonString) {
        try {
            JSONObject JSON = new JSONObject(jsonString);
            JSONArray results = JSON.getJSONArray("Results");

            if (results.length() <= 0) {
                isLoadedFood = false;
                return;
            }

            foods.clear();
            for (int i = 0; i < results.length(); i ++) {
                JSONObject aFood = results.getJSONObject(i);
                String id = aFood.getString("id");
                String food_name = aFood.getString("food_name");
                String food_eng_name = aFood.getString("food_eng_name");
                String food_desc = aFood.getString("description");
                String price = aFood.getString("price");
                String currency = aFood.getString("currency");
                String food_currency = aFood.getString("FoodCurrency");
                String lang_code = aFood.getString("lang_code");
                JSONObject images = aFood.getJSONObject("images");
                String image_path = images.getString("img_path");
                String sample_image_path = images.getString("sample_img_path");

                JSONArray category = aFood.getJSONArray("category");
                String category_id = "";
                String category_desc = "";
                String category_icon = "";
                String category_type = "";
                if (category.length() > 0) {
                    JSONObject aCategory = category.getJSONObject(0);
                    category_id = aCategory.getString("food_category_id");
                    category_desc = aCategory.getString("description");
                    category_icon = aCategory.getString("icon");
                    if ("".equals(category_icon)) {
                        category_icon = new ConfigURL(mActivity).defaultURL + "/" + "assets/img/icon/category/dinner.png";
                    }
                    else {
                        category_icon = new ConfigURL(mActivity).defaultURL + "/" + category_icon;
                    }
                    category_type = aCategory.getString("type");
                }
                String category_group = aFood.getString("category_group");

                JSONArray cuisine = aFood.getJSONArray("food_cuisine");
                String cuisine_id = "";
                String cuisine_desc = "";
                if (cuisine.length() > 0) {
                    JSONObject aCuisine = cuisine.getJSONObject(0);
                    cuisine_id = aCuisine.getString("cuisine_id");
                    cuisine_desc = aCuisine.getString("description");
                }

                String restaurant_id = aFood.getString("restaurant_id");
                String restaurant_name = aFood.getString("restaurant_name");

                MenuDish aFoodModel = new MenuDish(food_name, image_path, price, id, food_eng_name,
                        food_desc, sample_image_path, lang_code, currency, food_currency,
                        category_id, category_desc, category_icon, category_type, category_group,
                        cuisine_id, cuisine_desc, restaurant_id, restaurant_name);
                foods.add(aFoodModel);
            }
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public void loadDrinkData(String jsonString) {
        try {
            JSONObject JSON = new JSONObject(jsonString);
            JSONArray results = JSON.getJSONArray("Results");

            if (results.length() <= 0) {
                isLoadedDrink = false;
                return;
            }

            drinks.clear();
            for (int i = 0; i < results.length(); i ++) {
                JSONObject aDrink = results.getJSONObject(i);
                String id = aDrink.getString("id");
                String food_name = aDrink.getString("food_name");
                String food_eng_name = aDrink.getString("food_eng_name");
                String food_desc = aDrink.getString("description");
                String price = aDrink.getString("price");
                String currency = aDrink.getString("currency");
                String food_currency = aDrink.getString("FoodCurrency");
                String lang_code = aDrink.getString("lang_code");
                JSONObject images = aDrink.getJSONObject("images");
                String image_path = images.getString("img_path");
                String sample_image_path = images.getString("sample_img_path");

                JSONArray category = aDrink.getJSONArray("category");
                String category_id = "";
                String category_desc = "";
                String category_icon = "";
                String category_type = "";
                if (category.length() > 0) {
                    JSONObject aCategory = category.getJSONObject(0);
                    category_id = aCategory.getString("food_category_id");
                    category_desc = aCategory.getString("description");
                    category_icon = aCategory.getString("icon");
                    if ("".equals(category_icon)) {
                        category_icon = new ConfigURL(mActivity).defaultURL + "/" + "assets/img/icon/category/dinner.png";
                    }
                    else {
                        category_icon = new ConfigURL(mActivity).defaultURL + "/" + category_icon;
                    }
                    category_type = aCategory.getString("type");
                }
                String category_group = aDrink.getString("category_group");

                JSONArray cuisine = aDrink.getJSONArray("food_cuisine");
                String cuisine_id = "";
                String cuisine_desc = "";
                if (cuisine.length() > 0) {
                    JSONObject aCuisine = cuisine.getJSONObject(0);
                    cuisine_id = aCuisine.getString("cuisine_id");
                    cuisine_desc = aCuisine.getString("description");
                }

                String restaurant_id = aDrink.getString("restaurant_id");
                String restaurant_name = aDrink.getString("restaurant_name");

                MenuDish aDrinkModel = new MenuDish(food_name, image_path, price, id, food_eng_name,
                        food_desc, sample_image_path, lang_code, currency, food_currency,
                        category_id, category_desc, category_icon, category_type, category_group,
                        cuisine_id, cuisine_desc, restaurant_id, restaurant_name);
                drinks.add(aDrinkModel);
            }

        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public void showRestaurantInfo() {
        if (null != MoreMenuModel.restaurantInfo) {
            mRestaurantDistance = Utils.getDistance(MoreMenuModel.restaurantInfo.getLatitude(),
                    MoreMenuModel.restaurantInfo.getLongitude(),
                    mUserLatitude,
                    mUserLongitude) + " km";
            mDistanceTextView.setText(mRestaurantDistance);
            mRestaurantAddress = String.format("%s %s %s %s\n%s",
                    MoreMenuModel.restaurantInfo.getAddress(),
                    MoreMenuModel.restaurantInfo.getCity(),
                    MoreMenuModel.restaurantInfo.getState(),
                    MoreMenuModel.restaurantInfo.getPostcode(),
                    MoreMenuModel.restaurantInfo.getCountry());
            mAddressTextView.setText(mRestaurantAddress);
        }
    }

    public void showRestaurantDish() {
        showResultView();
        ArrayList<MenuCategory> categories = new ArrayList<>();
        ArrayList<MenuDish> allFoods = new ArrayList<>();
        for (int i = 0; i < MoreMenuModel.foodCategories.size(); i ++) {
            MenuCategory aCategory = MoreMenuModel.foodCategories.get(i);
            String selCategoryId = aCategory.getId();
            ArrayList<MenuDish> selFoods = new ArrayList<>();
            for (int j = 0; j < foods.size(); j++) {
                MenuDish aFood = foods.get(j);
                if (selCategoryId.equals(aFood.getCategoryId())) {
                    selFoods.add(aFood);
                }
            }
            aCategory.setDishes(selFoods);
            allFoods.addAll(selFoods);
            categories.add(aCategory);
        }
        CardViewDishAdapter adapter = new CardViewDishAdapter(mActivity, allFoods);
        mMenuRecyclerView.setAdapter(adapter);
    }

    public void showRestaurantFood() {
        if (foods.size() <= 0) {
            isLoadedFood = false;
            Utils.setDebug("No result", "Restaurant foods");
            mMenuRecyclerView.setAdapter(null);
            showNoResult();
            return;
        }
        isLoadedFood = true;
        showResultView();
        List<MenuCategory> categories = new ArrayList<>();
        for (int i = 0; i < MoreMenuModel.foodCategories.size(); i ++) {
            MenuCategory aCategory = MoreMenuModel.foodCategories.get(i);
            String selCategoryId = aCategory.getId();
            ArrayList<MenuDish> selFoods = new ArrayList<>();
            for (int j = 0; j < foods.size(); j++) {
                MenuDish aFood = foods.get(j);
                if (selCategoryId.equals(aFood.getCategoryId())) {
                    selFoods.add(aFood);
                }
            }
            aCategory.setDishes(selFoods);
            categories.add(aCategory);
        }
        mFoodAdapter = new MenuCategoryAdapter(getActivity(), categories);
        mFoodAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                Utils.setDebug("expanded", String.valueOf(parentPosition));
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {
                Utils.setDebug("collapsed", String.valueOf(parentPosition));
            }
        });
        mMenuRecyclerView.setAdapter(mFoodAdapter);
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void showRestaurantDrink() {
        if (drinks.size() <= 0) {
            isLoadedDrink = false;
            Utils.setDebug("No result", "Restaurant drinks");
            mMenuRecyclerView.setAdapter(null);
            showNoResult();
            return;
        }
        isLoadedDrink = true;
        showResultView();
        ArrayList<MenuCategory> categories = new ArrayList<>();
        for (int i = 0; i < MoreMenuModel.drinkCategories.size(); i ++) {
            MenuCategory aCategory = MoreMenuModel.drinkCategories.get(i);
            String selCategoryId = aCategory.getId();
            ArrayList<MenuDish> selDrinks = new ArrayList<>();
            for (int j = 0; j < drinks.size(); j ++) {
                MenuDish aDrink = drinks.get(j);
                if (selCategoryId.equals(aDrink.getCategoryId())) {
                    selDrinks.add(aDrink);
                }
            }
            aCategory.setDishes(selDrinks);
            categories.add(aCategory);
        }
        MenuCategoryAdapter adapter = new MenuCategoryAdapter(mActivity, categories);
        mMenuRecyclerView.setAdapter(adapter);
    }

    public void showRestaurantFoodWithFilter() {
        if (foods.size() <= 0) {
            isLoadedFood = false;
            Utils.setDebug("No result", "Restaurant foods");
            mMenuRecyclerView.setAdapter(null);
            showNoResult();
            return;
        }
        isLoadedFood = true;
        showResultView();
        List<MenuCategory> categories = new ArrayList<>();
        for (int i = 0; i < MoreMenuModel.foodCategories.size(); i ++) {
            MenuCategory aCategory = MoreMenuModel.foodCategories.get(i);
            if (aCategory.isChecked()) {
                String selCategoryId = aCategory.getId();
                ArrayList<MenuDish> selFoods = new ArrayList<>();
                for (int j = 0; j < foods.size(); j++) {
                    MenuDish aFood = foods.get(j);
                    if (selCategoryId.equals(aFood.getCategoryId())) {
                        selFoods.add(aFood);
                    }
                }
                aCategory.setDishes(selFoods);
                categories.add(aCategory);
            }
        }
        mFoodAdapter = new MenuCategoryAdapter(getActivity(), categories);
        mFoodAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                Utils.setDebug("expanded", String.valueOf(parentPosition));
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {
                Utils.setDebug("collapsed", String.valueOf(parentPosition));
            }
        });
        mMenuRecyclerView.setAdapter(mFoodAdapter);
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void showRestaurantDrinkWithFilter() {
        if (drinks.size() <= 0) {
            isLoadedDrink = false;
            Utils.setDebug("No result", "Restaurant drinks");
            mMenuRecyclerView.setAdapter(null);
            showNoResult();
            return;
        }
        isLoadedDrink = true;
        showResultView();
        ArrayList<MenuCategory> categories = new ArrayList<>();
        for (int i = 0; i < MoreMenuModel.drinkCategories.size(); i ++) {
            MenuCategory aCategory = MoreMenuModel.drinkCategories.get(i);
            if (aCategory.isChecked()) {
                String selCategoryId = aCategory.getId();
                ArrayList<MenuDish> selDrinks = new ArrayList<>();
                for (int j = 0; j < drinks.size(); j ++) {
                    MenuDish aDrink = drinks.get(j);
                    if (selCategoryId.equals(aDrink.getCategoryId())) {
                        selDrinks.add(aDrink);
                    }
                }
                aCategory.setDishes(selDrinks);
                categories.add(aCategory);
            }
        }
        MenuCategoryAdapter adapter = new MenuCategoryAdapter(mActivity, categories);
        mMenuRecyclerView.setAdapter(adapter);
    }

    public void setMoreButtonStateforOpenedDrawer() {
        if (!mMoreTabButton.isActivated()) {
//            onShowMoreDrawer();
            mMoreTabButton.setActivated(true);
            mMoreTabButton.setImageResource(R.drawable.more_hover_button);
        }
    }

    public void setMoreButtonStateforClosedDrawer() {
        mMoreTabButton.setActivated(false);
        mMoreTabButton.setImageResource(R.drawable.more_button);
    }

    private void showResultView() {
        mNoResult.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void showNoResult() {
        mNoResult.setVisibility(View.VISIBLE);
        mNoResult.setText(getActivity().getResources().getString(R.string.No_Search_Result));
        mSwipeRefreshLayout.setVisibility(View.GONE);
    }

    private void showConnectionError(){
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mNoResult.setVisibility(View.VISIBLE);
        mNoResult.setText(getResources().getString(R.string.Connection_Problem) + "\n" + getResources().getString(R.string.Connection_Problem_Detail));
    }

    private void showDownloadingAnimation() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void hideDownloadingAnimation() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 600);
    }

    @Override
    public void onResume() {
        mActivity.currentFragment = this;
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {  // cancel all asynctask before close activity
        for (AsyncTask asyncItem : allAsyncTask ) {
            if (asyncItem != null && !asyncItem.isCancelled()) {
                asyncItem.cancel(true);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void setMoreRestaurantCallback();
        void showMoreRestaurantCallback();
    }
}
