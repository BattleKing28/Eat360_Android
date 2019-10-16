package th.co.apps360.eat360.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import th.co.apps360.eat360.APIs.AutocompleteRestaurantAPI;
import th.co.apps360.eat360.APIs.GetTopCategoryAPI;
import th.co.apps360.eat360.APIs.GetTopFacilityAPI;
import th.co.apps360.eat360.Model.AutocompleteResultModel;
import th.co.apps360.eat360.Model.CategoryModel;
import th.co.apps360.eat360.Model.FacilityModel;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.MainActivity;
import th.co.apps360.eat360.activity.NFCActivity;
import th.co.apps360.eat360.activity.QRActivity;
import th.co.apps360.eat360.adapter.MarkedRIconsAdapter;
import th.co.apps360.eat360.adapter.PlaceAutocompleteAdapter;
import th.co.apps360.eat360.adapter.SuggestSearchAdapter;


public class FragmentRestaurant extends Fragment
        implements
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener,
        SuggestSearchAdapter.Callback {

    private static FragmentRestaurant mInstance;

    public static FragmentRestaurant getInstance() {
        if (mInstance == null)
            mInstance = new FragmentRestaurant();
        return mInstance;
    }

    private OnMoreActionListener mListener;

    // ui vars
    private View rootView;
    private MainActivity mActivity;
    private AppCompatAutoCompleteTextView searchView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout tempSwipeLayout;

    private LinearLayout tagSearch;
    private TextView tagType;
    private ImageButton closeTagButton;

    private ImageButton searchButton;
    private ImageButton clearButton;
    private ToggleButton walkButton;
    private ToggleButton driveButton;
    private ToggleButton advanceButton;
    private ToggleButton moreButton;
    private MoreMainDrawerFragment moreMainDrawerFragment;
    private SuggestSearchAdapter adapter;

    private RecyclerView facilitiesView;
    private TextView cuisineTextView;
    private ImageButton timerButton;

    private ImageButton nfcButton;
    private ImageButton qrButton;
    private RecyclerView markedCategoryIconsView;
    private TextView markedCuisineTextView;

    // local vars
    private Boolean showMoreDrawer = false;

//    private GoogleApiClient googleApiClient;
//    private LocationRequest mLocationRequest;
    private LatLng latLngAdvanceSearch;
    private String locationAdvanceSearch;
//    private int typeDistance;
    private int advanceKm;
//    private double distanceKm;
    private boolean checkShowDropdown;
    private ArrayList<String> markedCategoryIconList = new ArrayList<>();
    private ArrayList<Integer> markedCategoryPosList = new ArrayList<>();

    private ArrayList<AsyncTask> allAsyncTask = new ArrayList<>();
    private Toast toast;
    private static int UPDATE_INTERVAL = 1000; // 1 sec
    private static int FASTEST_INTERVAL = 500; // 0.5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    public interface OnMoreActionListener {
        void setMoreRestaurantCallback();
        void showMoreRestaurantCallback();
        void passParamsToResResultCallback(Bundle bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_restaurant, container, false);

        mActivity = (MainActivity)getActivity();
        searchView = (AppCompatAutoCompleteTextView) rootView.findViewById(R.id.r_search_view);
        searchButton = (ImageButton) rootView.findViewById(R.id.search_button);
        clearButton = (ImageButton) rootView.findViewById(R.id.clear_button);
        tagSearch = (LinearLayout) rootView.findViewById(R.id.r_tag_search);
        tagType = (TextView) rootView.findViewById(R.id.r_tag_type);
        closeTagButton = (ImageButton) rootView.findViewById(R.id.r_close_tag_button);

        walkButton = (ToggleButton) rootView.findViewById(R.id.r_walk_button);
        driveButton = (ToggleButton) rootView.findViewById(R.id.r_drive_button);
        advanceButton = (ToggleButton) rootView.findViewById(R.id.r_advance_button);
        moreButton = (ToggleButton) rootView.findViewById(R.id.r_more_button);

        facilitiesView = (RecyclerView) rootView.findViewById(R.id.r_marked_facilities);
        cuisineTextView = (TextView) rootView.findViewById(R.id.r_marked_cuisine);
        timerButton = (ImageButton) rootView.findViewById(R.id.r_timer_button);

        nfcButton = (ImageButton) rootView.findViewById(R.id.r_nfc_button);
        qrButton = (ImageButton) rootView.findViewById(R.id.r_qr_button);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.r_swipeRefresh_layout);
        tempSwipeLayout = (LinearLayout) rootView.findViewById(R.id.r_temp_layout);
        moreMainDrawerFragment = (MoreMainDrawerFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.more_drawer);

        markedCategoryIconsView = (RecyclerView) rootView.findViewById(R.id.r_marked_facilities);
        markedCuisineTextView = (TextView) rootView.findViewById((R.id.r_marked_cuisine));

        initData();
        setButtonStatus();
        initListener();
        downloadMoreData();
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

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to qrActivity
//                mActivity.currentFragment = new FragmentQrCode();
//                Bundle bundle = new Bundle();
//                bundle.putDouble("userLatitude", Utils.getLocationDetails().getLatitude());
//                bundle.putDouble("userLongitude", Utils.getLocationDetails().getLongitude());
//                mActivity.currentFragment.setArguments(bundle);
//                FragmentManager manager = mActivity.getSupportFragmentManager();
//                manager.beginTransaction().replace(R.id.restaurant_fragment, mActivity.currentFragment)
//                        .addToBackStack(null).commit();

                Intent resultIntent = new Intent(mActivity, QRActivity.class);
                resultIntent.putExtra("Latitude", Utils.getLocationDetails().getLatitude());
                resultIntent.putExtra("Longitude", Utils.getLocationDetails().getLongitude());
                mActivity.startActivity(resultIntent);

            }
        });

        nfcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent(mActivity, NFCActivity.class);
                mActivity.startActivity(resultIntent);
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

        changeLanguage();
    }

    @Override
    public void onDestroy() {
        try {
            for (AsyncTask asyncItem : allAsyncTask) {
                if (asyncItem != null && !asyncItem.isCancelled()) {
                    asyncItem.cancel(true);
                }
            }
        }
        catch (Exception e) {
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
                    + " must implement OnFragmentInteractionListener");
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
        mActivity.showLogo();
//        MoreDataModel.radius = 5.0;
        advanceKm = 5;
        checkShowDropdown = true;
        showMoreDrawer = false;
        mSwipeRefreshLayout.setColorSchemeResources (
                R.color.white);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) Utils.convertDpToPx(getContext(), 10));
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.blue));
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.bringToFront();
        initMoreDataModel();
    }

    private void doSearch() {
        MoreRestaurantDataModel.searchText = searchView.getText().toString()
                .replace(getString(R.string.contain)+" \"","").replace("\"","");

        if ("".equals(MoreRestaurantDataModel.searchText)
                && MoreRestaurantDataModel.restaurantSelCuisine == null)
            Utils.showDialog(getActivity(),
                    getResources().getString(R.string.title_select_autocomplete),
                    getResources().getString(R.string.select_autocomplete));
        else
            setLatLonAndIntentResult();
    }

    public void setLatLonAndIntentResult() {

        Utils.hideSoftKeyboard(this.getActivity(), searchView);
        double latitude = 0;
        double longitude = 0;

        if (MoreDataModel.typeDistance == MoreDataModel.Distance_Type.ADVANCED) { // advance search

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

        String selFacilityIDs = "", selCuisineIDs = "", topCategoryName = "";
        for (int i = 0; i < MoreRestaurantDataModel.restaurantSelFacilityIndexList.size(); i++) {
            int idx = MoreRestaurantDataModel.restaurantSelFacilityIndexList.get(i);
            if (!"".equals(selFacilityIDs)) {
                selFacilityIDs += ",";
            }
            selFacilityIDs += MoreRestaurantDataModel.restaurantFacilityList.get(idx).getId().toString();
        }
        if (null != MoreRestaurantDataModel.restaurantSelCuisine) {
            selCuisineIDs = MoreRestaurantDataModel.restaurantSelCuisine.getId().toString();
            if (!"".equals(selCuisineIDs)) {
                if (!"".equals(topCategoryName)) {
                    topCategoryName += ",";
                }
                topCategoryName += MoreRestaurantDataModel.restaurantSelCuisine.getName();
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString("SearchText", MoreRestaurantDataModel.searchText);
        bundle.putString("SearchLang", language);
        bundle.putDouble("Latitude", latitude);
        bundle.putDouble("Longitude", longitude);
        bundle.putDouble("Radius", MoreDataModel.radius);
        bundle.putString("CategoryIDs", selFacilityIDs);
        bundle.putString("MealIDs", "");
        bundle.putString("CuisineIDs", selCuisineIDs);

        bundle.putString("SearchType", "restaurant");
        bundle.putString("TopCategoryName", topCategoryName);
        bundle.putInt("TypeDistance", MoreDataModel.typeDistance.ordinal());

        mListener.passParamsToResResultCallback(bundle);
        if (moreMainDrawerFragment != null && moreMainDrawerFragment.isDrawerOpen()) {
            moreMainDrawerFragment.closeDrawer();
        }
    }

    private void initMoreDataModel() {
        Utils.initRestaurantData();
    }

    private void downloadMoreData() {
        if (MoreRestaurantDataModel.hasTopCuisines && MoreRestaurantDataModel.hasTopFacilities) {
            setMoreData();
        } else {
            showDownloadingAnimation();
        }
//        allAsyncTask.add(new GetTopFacilityAPI(getActivity()).execute());
//        allAsyncTask.add(new GetTopCategoryAPI(getActivity(),
//                MoreDataModel.Search_Tab.RESTAURANT).execute());
    }

    public void setTopFacilities(String sJson) {
        hideDownloadingAnimation();
        initMoreDataModel();
        try {
            JSONObject mainObject = new JSONObject(sJson);
            JSONArray facilityArray = mainObject.getJSONArray("Results");
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
//            hasTopFacilities = true;
//            if (hasTopCuisines) {
//                setMoreData();
//            }
//            else {
//                showDownloadingAnimation();
//            }
        } catch (JSONException e) {
            Utils.showConnectionProblemDialog(getActivity());
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    public void setTopCuisines(String sJson) {
        hideDownloadingAnimation();
        MoreRestaurantDataModel.restaurantCuisineList.clear();
        try {
            JSONObject mainObject = new JSONObject(sJson);
            JSONObject resultObject = mainObject.getJSONObject("Results");
            JSONArray cuisineArray = resultObject.getJSONArray("food_cuisine");
            for (int i = 0; i < cuisineArray.length(); i ++) {
                JSONObject aCuisineObject = cuisineArray.getJSONObject(i);
                Integer id = Integer.valueOf(aCuisineObject.getString("id"));
                String langcode = aCuisineObject.getString("lang_code");
                String name = aCuisineObject.getString("name");
                String icon = aCuisineObject.getString("icon");
                String type = aCuisineObject.getString("type");
                CategoryModel aCuisine = new CategoryModel(id, langcode, name, icon, type);
                MoreRestaurantDataModel.restaurantCuisineList.add(aCuisine);
            }
//            hasTopCuisines = true;
//            if (hasTopFacilities) {
//                setMoreData();
//            }
//            else {
//                showDownloadingAnimation();
//            }
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void onShowMoreDrawer() {
        showMoreDrawer = true;

        if (MoreRestaurantDataModel.restaurantFacilityList.size() > 0
                && MoreRestaurantDataModel.restaurantCuisineList.size() > 0) {
            if (moreMainDrawerFragment != null) {
                setMoreData();
            }
        } else {
            if (!Utils.isNetworkOnline(getActivity())) {
                Utils.showCannotConnectInternet(getActivity());
            } else {
                showDownloadingAnimation();
//                downloadMoreData();
            }
        }
    }

    private void setMoreData() {
        if (getActivity() == null)
            return;

        mListener.setMoreRestaurantCallback();
        hideDownloadingAnimation();

        if (showMoreDrawer) {
            mListener.showMoreRestaurantCallback();
            showMoreDrawer = false;
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
        for (int i = 0; i < MoreRestaurantDataModel.restaurantSelFacilityIndexList.size(); i ++) {
            int idx = MoreRestaurantDataModel.restaurantSelFacilityIndexList.get(i);
            markedCategoryIconList.add(MoreRestaurantDataModel.restaurantFacilityList.get(idx).getIcon());
            markedCategoryPosList.add(i);
        }
        markedCategoryIconsView.setAdapter(new MarkedRIconsAdapter(getActivity(), markedCategoryIconList, markedCategoryPosList));
        if (null != MoreRestaurantDataModel.restaurantSelCuisine) {
            markedCuisineTextView.setText(MoreRestaurantDataModel.restaurantSelCuisine.getName());
            markedCuisineTextView.setVisibility(View.VISIBLE);
            markedCuisineTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    MoreRestaurantDataModel.restaurantSelCuisine = null;
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
                            MoreRestaurantDataModel.searchText = editable.toString();
                            doSearch();
                            return;
                        }
                        break;
                    }
                }
                MoreRestaurantDataModel.searchText = editable.toString();
                searchView.setAdapter(null);
                removeAllAutocompleteAsyncTask();
                if (searchView.isPerformingCompletion()) { // prevent show autocomplete again when selected item
                    return;
                }

                if (MoreRestaurantDataModel.searchText.length() > 3 && checkShowDropdown) {
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
                onShowMoreDrawer();
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

////////////////////////////////////////////////////////////////////////////////////////////////////
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
                        && asyncItem instanceof AutocompleteRestaurantAPI) {
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
            latitude = String.valueOf(Utils.getLocationDetails().getLatitude()) ;
            longitude = String.valueOf(Utils.getLocationDetails().getLongitude()) ;
        }

        String distance = String.valueOf(MoreDataModel.radius);

        String selFacilityIDs = "", selCuisineIDs = "";
        for (int i = 0; i < MoreRestaurantDataModel.restaurantSelFacilityIndexList.size(); i++) {
            int idx = MoreRestaurantDataModel.restaurantSelFacilityIndexList.get(i);
            if (!"".equals(selFacilityIDs)) {
                selFacilityIDs += ",";
            }
            selFacilityIDs += MoreRestaurantDataModel.restaurantFacilityList.get(idx).getId().toString();
        }
        if (null != MoreRestaurantDataModel.restaurantSelCuisine) {
            selCuisineIDs = MoreRestaurantDataModel.restaurantSelCuisine.getId().toString();
        }

        allAsyncTask.add(new AutocompleteRestaurantAPI(getActivity(), MoreRestaurantDataModel.searchText,
                latitude, longitude, distance, selFacilityIDs, selCuisineIDs).execute());
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
                Utils.showNoResultDialog(mActivity, MoreRestaurantDataModel.searchText, String.valueOf(MoreDataModel.radius), lat, lon);
                hideDownloadingAnimation();
                searchView.dismissDropDown();
                return;
            }

            JSONObject obj = objResult.getJSONObject("Results");
            JSONObject data = obj.getJSONObject("restaurant_name");
            JSONArray list = data.getJSONArray("results");
            for (int i = 0; i < list.length(); i ++) {
                JSONObject tempData = list.getJSONObject(i);
                int id = tempData.getInt("id");
                String name= tempData.getString("name");
                String type = tempData.getString("type");
                AutocompleteResultModel aResult = new AutocompleteResultModel(id, name, type);
                resultArray.add(aResult);
            }

            if (resultArray.size() <= 0) {
                if(MoreRestaurantDataModel.searchText.length() >3) {
                    Utils.showNoResultDialog(mActivity, MoreRestaurantDataModel.searchText, String.valueOf(MoreDataModel.radius), lat, lon);
                }
            } else {
                cancelToast();
                searchView.dismissDropDown();
                setSuggestAdapter(resultArray);
            }
        } catch (JSONException e) {
            searchView.dismissDropDown();
            Utils.showNoResultDialog(mActivity, MoreRestaurantDataModel.searchText, String.valueOf(MoreDataModel.radius), lat, lon);
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
        MoreRestaurantDataModel.searchType = "";
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
                + this.getResources().getString(R.string.restaurant));
        searchView.requestLayout();
        if (!Utils.isNetworkOnline(getActivity())) {
            Utils.showCannotConnectInternet(getActivity());
        }
        else {
            showDownloadingAnimation();
            downloadMoreData();
        }
    }

    private void setSuggestAdapter(final ArrayList <AutocompleteResultModel> resultArray) {

        adapter = new SuggestSearchAdapter ( getActivity(),R.layout.item_suggest_layout, resultArray);
        searchView.setAdapter(adapter);
        searchView.showDropDown();

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MoreRestaurantDataModel.searchText = searchView.getText().toString().replace(getString(R.string.contain) + " \"", "").replace("\"", "");
                checkShowDropdown = false;
                searchView.setText(MoreRestaurantDataModel.searchText);
                searchView.setSelection(MoreRestaurantDataModel.searchText.length());
            }
        });
    }

    private void showAdvanceSearchDialog(boolean changeBorder){

        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View languageLayout = inflater.inflate(R.layout.advance_search_layout, null);

        ImageButton searchButton = (ImageButton)languageLayout.findViewById(R.id.search_button);
        final ImageButton clearButton = (ImageButton)languageLayout.findViewById(R.id.clear_button);


        final AppCompatAutoCompleteTextView searchView =  (AppCompatAutoCompleteTextView)languageLayout.findViewById(R.id.search_view);
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
        builder.setView(languageLayout);
        builder.setCancelable(true);

        final AlertDialog alert = builder.create();
        final DiscreteSeekBar discreteSeekBar = (DiscreteSeekBar)languageLayout.findViewById(R.id.seekBar);
        final TextView currentKm = (TextView) languageLayout.findViewById(R.id.current_km);


        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                advanceButton.setEnabled(true);
            }
        });


        discreteSeekBar.setProgress(advanceKm);
        currentKm.setText(advanceKm);

        ImageView okButton = (ImageView) languageLayout.findViewById(R.id.ok);
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

//        TextView cancelButton = (TextView) languageLayout.findViewById(R.id.cancel);
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

//    private void dialogEnableLocationService() {
//        if (!Utils.checkGPSEnabled(getActivity())) {
//            mActivity.showSettingsAlert(mActivity);
//        }
//    }
//    protected synchronized void buildGoogleApiClient() {
//        googleApiClient = new GoogleApiClient.Builder(this.getActivity())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API).build();
//    }
//
//
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
//    }


//    protected void startLocationUpdates() {
//
//        if (ActivityCompat.checkSelfPermission(this.getActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this.getActivity(),
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
//        statusCheck();
//    }

//    private void statusCheck() {
//        if (Utils.getLastLocation() == null) {
//            if (!Utils.isLocationEnabled(mActivity)) {
//                // notify user
//                Utils.showSettingScreen(mActivity);
//            } else {
//                if (ActivityCompat.checkSelfPermission(this.getActivity(),
//                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(this.getActivity(),
//                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                Location aLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                if (aLocation == null) {
//                    Utils.getLocationFromIP(mActivity);
//                }
//            }
//        }
//    }


//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
////        startLocationUpdates();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        try {
//            if (location != null) {
//                hideDownloadingAnimation();
//                Utils.setLastLocation(location);
//                if (gettingLocation) {
//                    gettingLocation = false;
//                    if (Utils.getLastLocation() != null) {
//                        transferParamsToResultFragment(Utils.getLastLocation().getLatitude(),Utils.getLastLocation().getLongitude());
//                    } else {
//                        Utils.showSettingScreen(mActivity);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Utils.setDebug("crash", e.getLocalizedMessage());
//        }
//    }

}
