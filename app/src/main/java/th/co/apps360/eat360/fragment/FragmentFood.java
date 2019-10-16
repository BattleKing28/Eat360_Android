package th.co.apps360.eat360.fragment;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import th.co.apps360.eat360.APIs.AutocompleteFoodAPI;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.AutocompleteResultModel;
import th.co.apps360.eat360.Model.CategoryModel;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.activity.MainActivity;
import th.co.apps360.eat360.adapter.MarkedFIconsAdapter;
import th.co.apps360.eat360.adapter.PlaceAutocompleteAdapter;
import th.co.apps360.eat360.adapter.SuggestSearchAdapter;
import th.co.apps360.eat360.APIs.GetTopCategoryAPI;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;


/**
 * Created by jakkrit.p on 2/07/2015.
 */
public class FragmentFood extends Fragment
        implements
        SuggestSearchAdapter.Callback {


    private static FragmentFood mInstance;

    public static FragmentFood getInstance() {
        if (mInstance == null)
            mInstance = new FragmentFood();
        return mInstance;
    }

    OnMoreActionListener mListener;

    private MainActivity mActivity;
    private View rootView;
    private AppCompatAutoCompleteTextView searchView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout tempSwipeLayout;

    private ImageButton searchButton;
    private ImageButton clearButton;
    private ToggleButton walkButton;
    private ToggleButton driveButton;
    private ToggleButton advanceButton;
    private ToggleButton moreButton;
    private MoreMainDrawerFragment moreMainDrawerFragment;
    private boolean isShownCategoryDialog;
    private ArrayList<AsyncTask> allAsyncTask = new ArrayList<>();
    private RecyclerView markedCategoryIconsView;
    private TextView markedCuisineTextView;

    private LatLng latLngAdvanceSearch;
    private String locationAdvanceSearch;
    private int advanceKm;
//    private double distanceKm;
    private boolean checkShowDropdown;
    private ArrayList<String> markedCategoryIconList = new ArrayList<>();
    private ArrayList<Integer> markedCategoryPosList = new ArrayList<>();

    private SuggestSearchAdapter adapter;
    private LinearLayout tagSearch;


    private Toast toast;

    public interface OnMoreActionListener {
        void setMoreFoodCallback();
        void showMoreFoodCallback();
        void passParamsToResultCallback(Bundle bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_food, container, false);

        mActivity = (MainActivity) getActivity();
        searchView = (AppCompatAutoCompleteTextView) rootView.findViewById(R.id.search_view);
        searchButton = (ImageButton) rootView.findViewById(R.id.search_button);
        clearButton = (ImageButton) rootView.findViewById(R.id.clear_button);
        tagSearch = (LinearLayout) rootView.findViewById(R.id.tag_search);

        walkButton = (ToggleButton) rootView.findViewById(R.id.walk_button);
        driveButton = (ToggleButton) rootView.findViewById(R.id.drive_button);
        advanceButton = (ToggleButton) rootView.findViewById(R.id.advance_button);
        moreButton = (ToggleButton) rootView.findViewById(R.id.category_button);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_layout);
        tempSwipeLayout = (LinearLayout) rootView.findViewById(R.id.temp_layout);
        moreMainDrawerFragment = (MoreMainDrawerFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.more_drawer);

        markedCategoryIconsView = (RecyclerView) rootView.findViewById(R.id.marked_categories);
        markedCuisineTextView = (TextView) rootView.findViewById((R.id.marked_cuisine));

        initData();
        setButtonStatus();
        initListener();
        downloadCategory();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        mActivity.currentFragment = this;
        moreButton.setChecked(false);
        if (searchView != null)
            searchView.dismissDropDown();
        if (MoreFoodDataModel.hasTopCategories && MoreFoodDataModel.hasTopCuisines) {
            hideDownloadingAnimation();
            setCategoryDialog();
        } else {
            showDownloadingAnimation();
        }

        changeLanguage();
    }

    @Override
    public void onDestroy() {  // cancel all asynctask before close activity
        try {
            for (AsyncTask asyncItem : allAsyncTask ) {
                if (asyncItem != null && !asyncItem.isCancelled()) {
                    asyncItem.cancel(true);
                }
            }
        } catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMoreActionListener) {
            mListener = (OnMoreActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void moreSuggestCallback() {

    }


    public void setMoreButtonStateforOpenedDrawer() {
        if (!moreButton.isChecked())
            moreButton.setChecked(true);
    }

    public void setMoreButtonStateforClosedDrawer() {
        if (moreButton.isChecked()) {
            moreButton.setChecked(false);
        }
    }

    private void initData() {
//        MoreDataModel.typeDistance = MoreDataModel.Distance_Type.WALK;
        mActivity.showLogo();
//        MoreDataModel.radius = 5;
        advanceKm = 5;
        checkShowDropdown = true;
        isShownCategoryDialog = false;
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.white
        );
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) Utils.convertDpToPx(getContext(), 10));
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.blue));
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.bringToFront();
        initMoreDataModel();
    }

    private void doSearch() {
        MoreFoodDataModel.searchText = searchView.getText().toString()
                .replace(getString(R.string.contain)+" \"","").replace("\"","");
        if ("".equals(MoreFoodDataModel.searchText)
                && MoreFoodDataModel.foodSelCategoryIndexList.size() == 0
                && MoreFoodDataModel.foodSelCuisine == null)
            Utils.showDialog(getActivity(),
                    getResources().getString(R.string.title_enter_keyword),
                    getResources().getString(R.string.body_enter_keyword));
        else
            setLatLonAndIntentResult();
    }

    public void setLatLonAndIntentResult() {

        Utils.hideSoftKeyboard(this.getActivity(), searchView);
        double latitude = 0;
        double longitude = 0;

        if (MoreDataModel.typeDistance == MoreDataModel.Distance_Type.ADVANCED ) { // advance search

            if (latLngAdvanceSearch == null) {
                showAdvanceSearchDialog(true);
                return;
            } else {
                latitude = latLngAdvanceSearch.latitude;
                longitude= latLngAdvanceSearch.longitude;
                transferParamsToResultFragment(latitude, longitude);
                return;
            }

        } else {
            latitude = Utils.getLocationDetails().getLatitude();
            longitude= Utils.getLocationDetails().getLongitude();
            transferParamsToResultFragment(latitude, longitude);
            return;
        }

    }

    private void transferParamsToResultFragment(double latitude, double longitude) {

//        clearGettingLocation();
        String language = Utils.getCurrentLanguage(getActivity());

        String selCategoryIDs = "", selMealIDs = "", selCuisineIDs = "", topCategoryName = "";
        for (int i = 0; i < MoreFoodDataModel.foodSelCategoryIndexList.size(); i++) {
            int idx = MoreFoodDataModel.foodSelCategoryIndexList.get(i);
            if ("food_category".equals(MoreFoodDataModel.foodCategoryList.get(idx).getType())) {
                if (!"".equals(selCategoryIDs)) {
                    selCategoryIDs += ",";
                }
                selCategoryIDs += MoreFoodDataModel.foodCategoryList.get(idx).getId().toString();
            }
            else if ("food_meal".equals(MoreFoodDataModel.foodCategoryList.get(idx).getType())) {
                if (!"".equals(selMealIDs)) {
                    selMealIDs += ",";
                }
                selMealIDs += MoreFoodDataModel.foodCategoryList.get(idx).getId().toString();
            }

            if (!"".equals(topCategoryName)) {
                topCategoryName += ",";
            }
            topCategoryName += MoreFoodDataModel.foodCategoryList.get(idx).getName();
        }
        if (null != MoreFoodDataModel.foodSelCuisine) {
            selCuisineIDs = MoreFoodDataModel.foodSelCuisine.getId().toString();
            if (!"".equals(selCuisineIDs)) {
                if (!"".equals(topCategoryName)) {
                    topCategoryName += ",";
                }
                topCategoryName += MoreFoodDataModel.foodSelCuisine.getName();
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString("SearchText", MoreFoodDataModel.searchText);
        bundle.putString("SearchLang", language);
        bundle.putDouble("Latitude", latitude);
        bundle.putDouble("Longitude", longitude);
        bundle.putDouble("Radius", MoreDataModel.radius);
        bundle.putString("CategoryIDs", selCategoryIDs);
        bundle.putString("MealIDs", selMealIDs);
        bundle.putString("CuisineIDs", selCuisineIDs);
        bundle.putString("IngredientIDs", "");

        bundle.putString("SearchType", "food_name");
        bundle.putString("TopCategoryName", topCategoryName);
        bundle.putInt("TypeDistance", MoreDataModel.typeDistance.ordinal());

        mListener.passParamsToResultCallback(bundle);
        if (moreMainDrawerFragment != null && moreMainDrawerFragment.isDrawerOpen()) {
            moreMainDrawerFragment.closeDrawer();
        }
    }

    private void initMoreDataModel() {
        Utils.initFoodData();
    }

    private void downloadCategory() {
//        showDownloadingAnimation();
//        allAsyncTask.add(new GetTopCategoryAPI(getActivity(),
//                MoreDataModel.Search_Tab.FOOD).execute());
    }

    public void setTopCategories(String sJson) {
        hideDownloadingAnimation();
        initMoreDataModel();
        try {
            JSONObject mainObject = new JSONObject(sJson);
            JSONObject resultObject = mainObject.getJSONObject("Results");
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
            JSONArray cuisineArray = resultObject.getJSONArray("food_cuisine");
            for (int i = 0; i < cuisineArray.length(); i ++) {
                JSONObject aCuisine = cuisineArray.getJSONObject(i);
                Integer id = Integer.valueOf(aCuisine.getString("id"));
                String langcode = aCuisine.getString("lang_code");
                String name = aCuisine.getString("name");
                String icon = aCuisine.getString("icon");
                String type = aCuisine.getString("type");
                CategoryModel aFoodCuisine = new CategoryModel(id, langcode, name, icon, type);
                MoreFoodDataModel.foodCuisineList.add(aFoodCuisine);
            }
            setCategoryDialog();

        } catch (JSONException e) {
            Utils.showConnectionProblemDialog(getActivity());
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void showCategoryDialog() {
        isShownCategoryDialog = true;

        if (MoreFoodDataModel.foodCategoryList.size() > 0) {
            if (moreMainDrawerFragment != null) {
                setCategoryDialog();
            }
        } else {
            if (!Utils.isNetworkOnline(getActivity())) {
                Utils.showCannotConnectInternet(getActivity());
            } else {
                showDownloadingAnimation();
                downloadCategory();
            }
        }
    }

    public void setCategoryDialog() {
        if (getActivity() == null)
            return;

        mListener.setMoreFoodCallback();
        hideDownloadingAnimation();

        if (isShownCategoryDialog) {
            mListener.showMoreFoodCallback();
            isShownCategoryDialog = false;
        }
    }

    public void goButtonPrevAction() {
        closeTag();
    }

    public void goButtonPostAction() {
        setLatLonAndIntentResult();
    }

    public void continueButtonPostAction() {
        markedCategoryIconList.clear();
        markedCategoryPosList.clear();
        for (int i = 0; i < MoreFoodDataModel.foodSelCategoryIndexList.size(); i ++) {
            int idx = MoreFoodDataModel.foodSelCategoryIndexList.get(i);
            markedCategoryIconList.add(MoreFoodDataModel.foodCategoryList.get(idx).getIcon());
            markedCategoryPosList.add(i);
        }
        markedCategoryIconsView.setAdapter(new MarkedFIconsAdapter(getActivity(),
                markedCategoryIconList, markedCategoryPosList));

        if (null != MoreFoodDataModel.foodSelCuisine) {
            markedCuisineTextView.setText(MoreFoodDataModel.foodSelCuisine.getName());
            markedCuisineTextView.setVisibility(View.VISIBLE);
            markedCuisineTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    MoreFoodDataModel.foodSelCuisine = null;
                }
            });
        }
    }

    private  void initListener() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    clearButton.setVisibility(View.VISIBLE);
                else
                    clearButton.setVisibility(View.GONE);

                for(int i = editable.length()-1; i >= 0; i--){
                    if(editable.charAt(i) == '\n'){
                        editable.delete(i, i + 1);
                        if (editable.length() > 3) {
                            MoreFoodDataModel.searchText = editable.toString();
                            doSearch();
                            return;
                        }
                        break;
                    }
                }
                MoreFoodDataModel.searchText = editable.toString();
                searchView.setAdapter(null);
                removeAllAutocompleteAsyncTask();
                if (searchView.isPerformingCompletion()) { // prevent show autocomplete again when selected item
                    return;
                }

                if (MoreFoodDataModel.searchText.length() > 3 && checkShowDropdown) {
                    showDownloadingAnimation();
                    getAutocomplete();

                } else {
                    hideDownloadingAnimation();
                }

                if (!checkShowDropdown) {
                    searchView.dismissDropDown();
                    checkShowDropdown = true;
                }
            }

        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideDownloadingAnimation();
                Utils.hideSoftKeyboard(getActivity(), rootView);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTag();
                searchView.setText("");
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryDialog();
            }
        });

        tempSwipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                clearGettingLocation();
            }
        });

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoreDataModel.typeDistance = MoreDataModel.Distance_Type.WALK;
                setRadius();
                driveButton.setChecked(false);
                advanceButton.setChecked(false);
                walkButton.setChecked(true);
            }
        });

        driveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoreDataModel.typeDistance = MoreDataModel.Distance_Type.CAR;
                setRadius();
                walkButton.setChecked(false);
                advanceButton.setChecked(false);
                driveButton.setChecked(true);
            }

        });

        advanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceButton.setEnabled(false);
                MoreDataModel.typeDistance = MoreDataModel.Distance_Type.ADVANCED;
                setRadius();
                walkButton.setChecked(false);
                driveButton.setChecked(false);
                advanceButton.setChecked(true);
                showAdvanceSearchDialog(false);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // TODO: sub methods

    public void showDownloadingAnimation() {
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

    }

    public void hideDownloadingAnimation() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
            }
        });
    }

    private void removeAllAutocompleteAsyncTask() {
        try {
            for (AsyncTask asyncItem : allAsyncTask) {
                if (asyncItem != null && !asyncItem.isCancelled()
                        && asyncItem instanceof AutocompleteFoodAPI) {
                    asyncItem.cancel(true);
                }
            }
        } catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void getAutocomplete() {
        if(MoreDataModel.typeDistance == MoreDataModel.Distance_Type.ADVANCED) {
            if(latLngAdvanceSearch == null){
                showAdvanceSearchDialog(true);
                hideDownloadingAnimation();
            } else {
                downloadAutocomplete();
            }
        } else {
            downloadAutocomplete();
            return;
        }
    }

    private void  downloadAutocomplete() {
        String latitude;
        String longitude;

        if(MoreDataModel.typeDistance == MoreDataModel.Distance_Type.ADVANCED){
            latitude = String.valueOf(latLngAdvanceSearch.latitude);
            longitude = String.valueOf(latLngAdvanceSearch.longitude);
        }else{
            latitude = String.valueOf(Utils.getLocationDetails().getLatitude());
            longitude = String.valueOf(Utils.getLocationDetails().getLongitude());
        }

        String distance = String.valueOf(MoreDataModel.radius);

        String selCategoryIDs = "", selMealIDs = "", selCuisineIDs = "";
        for (int i = 0; i < MoreFoodDataModel.foodSelCategoryIndexList.size(); i++) {
            int idx = MoreFoodDataModel.foodSelCategoryIndexList.get(i);
            if ("food_category".equals(MoreFoodDataModel.foodCategoryList.get(idx).getType())) {
                if (!"".equals(selCategoryIDs)) {
                    selCategoryIDs += ",";
                }
                selCategoryIDs += MoreFoodDataModel.foodCategoryList.get(idx).getId().toString();
            }
            else if ("food_meal".equals(MoreFoodDataModel.foodCategoryList.get(idx).getType())) {
                if (!"".equals(selMealIDs)) {
                    selMealIDs += ",";
                }
                selMealIDs += MoreFoodDataModel.foodCategoryList.get(idx).getId().toString();
            }
        }
        if (null != MoreFoodDataModel.foodSelCuisine) {
            selCuisineIDs = MoreFoodDataModel.foodSelCuisine.getId().toString();
        }

        allAsyncTask.add(new AutocompleteFoodAPI(getActivity(), MoreFoodDataModel.searchText,
                latitude, longitude, distance, selCategoryIDs, selMealIDs, selCuisineIDs, "").execute());
    }

    public void setAutocomplete(String jsonString) {
        ArrayList<AutocompleteResultModel> resultArray = new ArrayList<>();
        double lat, lon;
        if (MoreDataModel.typeDistance == MoreDataModel.Distance_Type.ADVANCED) {
            lat = latLngAdvanceSearch.latitude;
            lon = latLngAdvanceSearch.longitude;
        } else {
            lat = Utils.getLocationDetails().getLatitude();
            lon = Utils.getLocationDetails().getLongitude();
        }
        try {
            JSONObject objResult = new JSONObject(jsonString);
            Boolean notfound = objResult.getBoolean("NotFound");
            if (notfound) {
                Utils.showNoResultDialog(mActivity, MoreFoodDataModel.searchText, String.valueOf(MoreDataModel.radius), lat, lon);
                hideDownloadingAnimation();
                searchView.dismissDropDown();
                return;
            }
            JSONArray list = objResult.getJSONArray("Results");
            for (int i = 0; i < list.length(); i ++) {
                JSONObject tempData = list.getJSONObject(i);
                int id = tempData.getInt("id");
                String name = tempData.getString("food_name");
                String type = tempData.getString("food_type");
                if ("NULL".equals(type)) {
                    type = "FOOD & DRINK";
                }
                String rest_id = tempData.getString("restaurant_id");
                String rest_name = tempData.getString("restaurant_name");
                AutocompleteResultModel aResult = new AutocompleteResultModel(id, name, type);
                resultArray.add(aResult);
            }

            if (resultArray.size() <= 0) {
                if(MoreFoodDataModel.searchText.length() >3) {
                    Utils.showNoResultDialog(mActivity, MoreFoodDataModel.searchText, String.valueOf(MoreDataModel.radius), lat, lon);
                }
            } else {
                cancelToast();
                searchView.dismissDropDown();
                setSuggestAdapter(resultArray);
            }
        } catch (JSONException e) {
            searchView.dismissDropDown();
            Utils.showNoResultDialog(mActivity, MoreFoodDataModel.searchText, String.valueOf(MoreDataModel.radius), lat, lon);
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
        hideDownloadingAnimation();
    }

    void showToast(String text) {
        if (toast != null)  {
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    void cancelToast() {
        if(toast != null)  {
            toast.cancel();
        }
        toast = null;
    }

    public void closeTag() {
        tagSearch.setVisibility(View.GONE);
        MoreFoodDataModel.searchType = "";
        searchView.setText("");

        Utils.setUseFoodFilter(getActivity(), false);
        Utils.setUseFoodFilterTemp(getActivity(), false);
        Utils.setUseRestaurantFilter(getActivity(), false);
        Utils.setUseRestaurantFilterTemp(getActivity(), false);
    }

    private void setRadius() {
        switch (MoreDataModel.typeDistance) {
            case WALK:
                MoreDataModel.radius = 5.0;
                break;
            case CAR:
                MoreDataModel.radius = 10.0;
                break;
            case ADVANCED:
                MoreDataModel.radius = advanceKm;
                break;

            default:
                break;
        }
    }

    private void setButtonStatus() {
        switch (MoreDataModel.typeDistance) {
            case WALK:
                walkButton.setChecked(true);
                driveButton.setChecked(false);
                advanceButton.setChecked(false);
                break;
            case CAR:
                walkButton.setChecked(false);
                driveButton.setChecked(true);
                advanceButton.setChecked(false);
                break;
            case ADVANCED:
                walkButton.setChecked(false);
                driveButton.setChecked(false);
                advanceButton.setChecked(true);
                break;

            default:
                walkButton.setChecked(true);
                driveButton.setChecked(false);
                advanceButton.setChecked(false);
        }
    }

    public void changeLanguage() {
        searchView.setHint(this.getResources().getString(R.string.Search_for) + " "
                + this.getResources().getString(R.string.food));
        searchView.requestLayout();
        if (!Utils.isNetworkOnline(getActivity())) {
            Utils.showCannotConnectInternet(getActivity());
        } else {
            showDownloadingAnimation();
            downloadCategory();
        }
    }

    private void setSuggestAdapter(final ArrayList <AutocompleteResultModel> resultArray) {

        adapter = new SuggestSearchAdapter ( getActivity(),R.layout.item_suggest_layout, resultArray);
        searchView.setAdapter(adapter);
        searchView.showDropDown();

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MoreFoodDataModel.searchText = searchView.getText().toString().replace(getString(R.string.contain) + " \"", "").replace("\"", "");
                checkShowDropdown = false;
                searchView.setText(MoreFoodDataModel.searchText);
                searchView.setSelection(MoreFoodDataModel.searchText.length());
            }
        });
    }

    private void showAdvanceSearchDialog(boolean changeBorder){

        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View advanceLayout = inflater.inflate(R.layout.advance_search_layout, null);

        ImageButton searchButton = (ImageButton)advanceLayout.findViewById(R.id.search_button);
        final ImageButton clearButton = (ImageButton)advanceLayout.findViewById(R.id.clear_button);


        final AppCompatAutoCompleteTextView searchView =  (AppCompatAutoCompleteTextView)advanceLayout.findViewById(R.id.search_view);
        if(changeBorder){
            hideDownloadingAnimation();
            searchView.setBackgroundResource(R.drawable.rounded_border_redstroke);
            searchView.setError(getString(R.string.select_again));
        }


        final GoogleApiClient  mGoogleApiClient;
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity()).addApi(Places.GEO_DATA_API).build();
        mGoogleApiClient.connect();

        LatLngBounds bounds = new LatLngBounds(new LatLng(-85, 180), new LatLng(85, -180));
        final PlaceAutocompleteAdapter mAdapter = new PlaceAutocompleteAdapter(this.getActivity(), R.layout.suggest_item,  mGoogleApiClient, bounds, null);
        searchView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    clearButton.setVisibility(View.VISIBLE);
                else
                    clearButton.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!checkShowDropdown) {
                    searchView.dismissDropDown();
                    checkShowDropdown = true;
                }
                for(int i = editable.length()-1; i >= 0; i--){
                    if(editable.charAt(i) == '\n'){
                        editable.delete(i, i + 1);
                        return;
                    }
                }
            }
        });

        if (locationAdvanceSearch != null) {
            searchView.setText(locationAdvanceSearch);
            searchView.setSelection(locationAdvanceSearch.length());
        }

        final ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    places.release();
                    return;
                }

                final Place place = places.get(0);
                latLngAdvanceSearch = place.getLatLng();
                places.release();
            }
        };

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                String placeId = String.valueOf(item.placeId);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                Utils.hideSoftKeyboard(getActivity(), searchView);
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkShowDropdown = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mAdapter.getCount() > 0 && searchView.getText().length() > 0) {
                        searchView.setText(mAdapter.getItem(0).toString());
                        PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(0);
                        String placeId = String.valueOf(item.placeId);
                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                        Utils.hideSoftKeyboard(getActivity(), searchView);
                    }
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkShowDropdown = false;
                if (mAdapter.getCount() > 0) {
                    searchView.setText(mAdapter.getItem(0).toString());
                    PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(0);
                    String placeId = String.valueOf(item.placeId);
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                    placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                    Utils.hideSoftKeyboard(getActivity(), searchView);
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setView(advanceLayout);
        builder.setCancelable(true);

        final AlertDialog alert = builder.create();
        final DiscreteSeekBar discreteSeekBar = (DiscreteSeekBar)advanceLayout.findViewById(R.id.seekBar);
        final TextView currentKm = (TextView) advanceLayout.findViewById(R.id.current_km);


        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                advanceButton.setEnabled(true);
            }
        });


        discreteSeekBar.setProgress(advanceKm);
        currentKm.setText(advanceKm);

        ImageView okButton = (ImageView) advanceLayout.findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceKm = discreteSeekBar.getProgress();
                locationAdvanceSearch = searchView.getText().toString();
                if (locationAdvanceSearch.length() <= 0)
                    latLngAdvanceSearch = null;

                setRadius();
                alert.cancel();
            }
        });

//        TextView cancelButton = (TextView) advanceLayout.findViewById(R.id.cancel);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alert.cancel();
//            }
//        });


        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int km, boolean b) {
                currentKm.setText(km);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }
        });
        alert.show();
    }

    private void tempAPI() {
        String url = ConfigURL.server1 + "/temp";
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        params.put("Email", "");
        params.put("Password", "");

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
            }
        });
    }

}
