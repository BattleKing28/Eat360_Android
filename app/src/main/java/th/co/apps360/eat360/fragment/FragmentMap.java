package th.co.apps360.eat360.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import th.co.apps360.eat360.APIs.AutocompleteMapAPI;
import th.co.apps360.eat360.APIs.AutocompleteRestaurantAPI;
import th.co.apps360.eat360.APIs.ReverseGeoCodingAPI;
import th.co.apps360.eat360.APIs.SearchByLocationAPI;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.AutocompleteResultModel;
import th.co.apps360.eat360.Model.LocationModel;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.activity.MainActivity;
import th.co.apps360.eat360.activity.MenuActivity;
//import th.co.apps360.eat360.activity.RestaurantActivity;
import th.co.apps360.eat360.adapter.PlaceAutocompleteAdapter;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.adapter.SuggestSearchAdapter;

/**
 * Created by jakkrit.p on 2/07/2015.
 */
public class FragmentMap extends Fragment
        implements
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static FragmentMap mInstance;

    public static FragmentMap getInstance() {
        if (mInstance == null)
            mInstance = new FragmentMap();
        return mInstance;
    }

    private MainActivity mActivity;
    private GoogleMap map;
    private ImageButton myLocationButton;
    private ImageButton searchButton;
    private LatLngBounds currentCameraBounds = new LatLngBounds(new LatLng(0, 0), new LatLng(0, 0));
    private String minLatitude;
    private String minLongitude;
    private String maxLatitude;
    private String maxLongitude;
    private ImageButton clearButton;
    private Utils.callbackShowDialog showDialog;
    private HashMap<String, DataMarker> markerData = new HashMap<>();
    private AppCompatAutoCompleteTextView searchView;
    private PlaceAutocompleteAdapter mAutoAdapter;
    private SuggestSearchAdapter mSuggestAdapter;
    private GoogleApiClient mGoogleApiClient;
    private boolean checkFirstShowPopup;
    private SupportMapFragment mapView;
    private CardView searchBarContainer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<AsyncTask> allAsyncTask = new ArrayList<>();
    private boolean googleMapInstalled;
    private ImageButton directions;
    private LatLng myPosition;
    private String languageTemp;
    private Integer mTotalCount = 0;
    private Toast toast;
    private LatLngBounds mBounds;
    private LatLng mOrigin;
    private String mRadius;
    private String mSearchText = "";
    private boolean checkShowDropdown;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //setGlobalException();
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mActivity = (MainActivity) getActivity();
        myLocationButton = (ImageButton) view.findViewById(R.id.my_location);
        searchButton = (ImageButton) view.findViewById(R.id.search_button);
        searchView = (AppCompatAutoCompleteTextView) view.findViewById(R.id.search_view);
        searchBarContainer = (CardView) view.findViewById(R.id.searchview_container);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_layout);
        clearButton = (ImageButton) view.findViewById(R.id.clear_button);
        directions = (ImageButton) view.findViewById(R.id.directions);

        initData();
        initMap();
        initListener();
        initLocationManager();
        myPosition = getMyLocation();
        buildGoogleAPIClient();
        return view;
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            showDialog = (Utils.callbackShowDialog) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.currentFragment = this;

        if (mapView != null)
            mapView.onResume();

        String searchLang = Utils.getCurrentLanguage(getActivity());
        if (!searchLang.equals(languageTemp)) {
            refreshMap();
            languageTemp = searchLang;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.addMarker(new MarkerOptions().position(myPosition).title("my location"));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18));
        map.setInfoWindowAdapter(new MyInfoWindowAdapter());
        map.setOnInfoWindowClickListener(this);
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            private int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
            private long lastCallMs = Long.MIN_VALUE;

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

                final long snap = System.currentTimeMillis();
                if (lastCallMs + CAMERA_MOVE_REACT_THRESHOLD_MS > snap) {
                    lastCallMs = snap;
                    return;
                }

                mBounds = bounds;
                mOrigin = bounds.getCenter();
                LatLng northeast = bounds.northeast;
                mRadius = Utils.getDistance(mOrigin.latitude, mOrigin.longitude, northeast.latitude, northeast.longitude);
                downloadAndShowRestaurants(bounds);
                lastCallMs = snap;
                currentCameraBounds = bounds;
            }
        });
        refreshMap();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getActivity(), MenuActivity.class);
        intent.putExtra("target", "menu");
        intent.putExtra("restaurantId", markerData.get(marker.getId()).restaurantId);
        intent.putExtra("restaurantName", markerData.get(marker.getId()).restaurantName);
        intent.putExtra("restaurantImagePath", markerData.get(marker.getId()).imageUrl);
        intent.putExtra("restaurantAddress", "");
        intent.putExtra("userLatitude", myPosition.latitude);
        intent.putExtra("userLongitude", myPosition.longitude);
        getActivity().startActivity(intent);
    }


    @Override
    public void onDestroy() {  // cancel all asynctask before close activity
        for (AsyncTask asyncItem : allAsyncTask ){
            if(asyncItem != null && !asyncItem.isCancelled()){
                asyncItem.cancel(true);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
//                .enableAutoManage(getActivity(), this)
//                .addConnectionCallbacks(this)
//                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API).build();
        mGoogleApiClient.connect();
    }

    private void setAdapterSuggest(LatLngBounds bounds) {
        mAutoAdapter = new PlaceAutocompleteAdapter(this.getActivity(),
                R.layout.suggest_item, mGoogleApiClient, bounds, null);
        searchView.setAdapter(mAutoAdapter);
    }

    private void downloadAutocomplete() {

        allAsyncTask.add(new AutocompleteRestaurantAPI(getActivity(), mSearchText,
                String.valueOf(mOrigin.latitude), String.valueOf(mOrigin.longitude), mRadius, "", "").execute());

    }

    public void setAutocomplete(String jsonString) {
        hideDownloadingAnimation();
        ArrayList<AutocompleteResultModel> resultArray = new ArrayList<>();
        double lat = Utils.getLocationDetails().getLatitude();
        double lon = Utils.getLocationDetails().getLongitude();
        try {
            JSONObject objResult = new JSONObject(jsonString);
            Boolean notfound = objResult.getBoolean("NotFound");
            if (notfound) {
                Utils.showNoResultDialog(mActivity, mSearchText, mRadius, lat, lon);
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
                    Utils.showNoResultDialog(mActivity, mSearchText, mRadius, lat, lon);
                }
            } else {
                cancelToast();
                searchView.dismissDropDown();
                setSuggestAdapter(resultArray);
            }
        } catch (JSONException e) {
            searchView.dismissDropDown();
            Utils.showNoResultDialog(mActivity, mSearchText, mRadius, lat, lon);
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void setSuggestAdapter(final ArrayList <AutocompleteResultModel> resultArray) {
        mSuggestAdapter = new SuggestSearchAdapter(getActivity(), R.layout.item_suggest_layout, resultArray);
        searchView.setAdapter(mSuggestAdapter);
        searchView.showDropDown();
    }

    private void initData() {
        Utils.initPicasso(getActivity());
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 5);
        googleMapInstalled = Utils.isGoogleMapsInstalled(getActivity());
        languageTemp = Utils.getCurrentLanguage(getContext());
        toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
    }

    private void showDownloadingAnimation() {
        mSwipeRefreshLayout.setEnabled(true);
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
                mSwipeRefreshLayout.setEnabled(false);
            }
        }, 1000);
    }

    private void initMap() {
        try {
            int googlePlayServiceVersion = this.getActivity().getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
            if (googlePlayServiceVersion < 7000000) { // required Google play Service version 7 or up
                showDialog.showDialogUpdatePlayService();
                searchBarContainer.setVisibility(View.GONE);
            } else {
                mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                mapView.getMapAsync(this);
//                if (ActivityCompat.checkSelfPermission(this.getActivity(),
//                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(this.getActivity(),
//                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }

                checkFirstShowPopup = true;

                if (!googleMapInstalled)
                    map.getUiSettings().setMapToolbarEnabled(false);
            }
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

    }

    private void initLocationManager() {
        statusCheck();
    }

    private void statusCheck() {
        if (Utils.getLocationDetails() == null) {
            if (!Utils.isLocationEnabled(mActivity)) {
                // notify user
                mActivity.showSettingsAlert(mActivity);
            } else {
                if (ActivityCompat.checkSelfPermission(this.getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this.getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mActivity.showSettingsAlert(mActivity);
                    return;
                }
                LocationManager locationManager = (LocationManager)
                        getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location aLocation = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(new Criteria(), false));
                if (aLocation == null) {
                    mActivity.getLocationFromIP(mActivity);
                }
            }
        }
    }

    private void initListener() {
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.checkGPSEnabled(getActivity())) {
                    mActivity.showSettingsAlert(mActivity);
                } else
                    moveToCurrentLocation();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAutoAdapter != null && !mAutoAdapter.isEmpty() && searchView.getText().length() > 0) {
                    String placeId = String.valueOf(mAutoAdapter.getItem(0).placeId);
                    setLocationFromSuggest(placeId);
                }
            }
        });

//        searchView.setAdapter(mAutoAdapter);
//        mAutoAdapter.notifyDataSetChanged();

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PlaceAutocompleteAdapter.PlaceAutocomplete item = mAutoAdapter.getItem(position);
                String placeId = String.valueOf(item.placeId);
                setLocationFromSuggest(placeId);

            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mAutoAdapter != null && !mAutoAdapter.isEmpty() && searchView.getText().length() > 0) {
                        String placeId = String.valueOf(mAutoAdapter.getItem(0).placeId);
                        setLocationFromSuggest(placeId);
                    }
                    return true;
                }
                return false;
            }
        });

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
                        break;
                    }
                }
//                mSearchText = editable.toString();
//                searchView.setAdapter(null);
//                removeAllAutocompleteAsyncTask();
//                if (searchView.isPerformingCompletion()) { // prevent show autocomplete again when selected item
//                    return;
//                }
//
//                if (mSearchText.length() > 3 && checkShowDropdown) {
//                    showDownloadingAnimation();
//                    downloadAutocomplete();
//
//                } else {
//                    hideDownloadingAnimation();
//                }

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
            }
        });

        directions.setOnClickListener(new View.OnClickListener() {  //use this if Google Map not installed or disabled
            @Override
            public void onClick(View view) {
                try {
                    LatLng latLong = new LatLng(Utils.getLocationDetails().getLatitude(),
                            Utils.getLocationDetails().getLongitude());
                    String url = "https://www.google.com/maps/dir//" + latLong.latitude + ","
                            + latLong.longitude + "/@" + latLong.latitude + ","
                            + latLong.longitude + ",5z";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                } catch (Exception e) {
                    Utils.setDebug("crash", e.getLocalizedMessage());
                }
            }
        });
    }

    private void removeAllAutocompleteAsyncTask() {
        try {
            for (AsyncTask asyncItem : allAsyncTask) {
                if (asyncItem != null && !asyncItem.isCancelled()
                        && asyncItem instanceof AutocompleteMapAPI) {
                    asyncItem.cancel(true);
                }
            }
        } catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }


    private void setLocationFromSuggest(String placeId) {
        showDownloadingAnimation();
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        Utils.hideSoftKeyboard(this.getActivity(), searchView);
        searchView.dismissDropDown();
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {

            hideDownloadingAnimation();
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            Utils.setDebug("Autocomplete Places result", place.getName().toString());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18));

            String imageURL = new ConfigURL(getContext()).defaultURL + "/assets/img/~temp/info_img.jpg";

            DataMarker dataMarker = new DataMarker();
            dataMarker.restaurantName = place.getName().toString();
            dataMarker.restaurantType = "Food & Drinks";
            dataMarker.imageUrl = imageURL;
            dataMarker.restaurantId = place.getId();
            addMarker(place.getLatLng(), dataMarker);

            Utils.hideSoftKeyboard(getActivity(), searchView);
            places.release();

        }
    };


    private void moveToCurrentLocation() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(), map.getCameraPosition().zoom));
    }

    private LatLng getMyLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mActivity.showSettingsAlert(getActivity());
            LatLng unknown = new LatLng(0, 0);
            return unknown;
        }

        if (Utils.getLocationDetails() != null) {
            double latitude = Utils.getLocationDetails().getLatitude();
            double longitude = Utils.getLocationDetails().getLongitude();

            LatLng myLocation = new LatLng(latitude, longitude);
            allAsyncTask.add(new ReverseGeoCodingAPI(getActivity(), latitude, longitude).execute());
            return myLocation;
        } else {
            if (Utils.isLocationEnabled(mActivity)) {
                LocationManager locationManager = (LocationManager)
                        getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location aLocation = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(new Criteria(), false));
                if (aLocation != null) {
                    LocationModel aLocationModel = new LocationModel(aLocation.getLatitude(),
                            aLocation.getLongitude(), "", "", "", "");
                    Utils.setLocationDetails(aLocationModel);
                    double latitude = Utils.getLocationDetails().getLatitude();
                    double longitude = Utils.getLocationDetails().getLongitude();

                    LatLng myLocation = new LatLng(latitude,  longitude);
                    mActivity.getLocationDetailFromLatLon(latitude, longitude);
//                    allAsyncTask.add(new ReverseGeoCodingAPI(getActivity(), latitude, longitude).execute());
                    return myLocation;
                }
            }
            showDownloadingAnimation();
            mActivity.getLocationFromIP(mActivity);
            return null;
        }
    }

    public void setMyLocationData(String jsonString) {
        String address = "";
        try {
            JSONObject mainObject = new JSONObject(jsonString);
            JSONArray resultArray = mainObject.getJSONArray("results");
            if (resultArray.length() > 0) {
                JSONObject aResult = resultArray.getJSONObject(0);
                address = aResult.getString("formatted_address");
                map.addMarker(new MarkerOptions().position(myPosition).title(address));
            }
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void downloadAndShowRestaurants(LatLngBounds bounds) {
        showDownloadingAnimation();
        minLatitude =  String.valueOf(bounds.southwest.latitude );
        minLongitude = String.valueOf(bounds.southwest.longitude);
        maxLatitude = String.valueOf(bounds.northeast.latitude);
        maxLongitude = String.valueOf(bounds.northeast.longitude);
        setAdapterSuggest(bounds);
        String urlSearchByLocation = new ConfigURL(getContext()).searchByLocationURL;
        allAsyncTask.add(new SearchByLocationAPI(getActivity(), minLatitude, minLongitude, maxLatitude, maxLongitude)
                .execute(urlSearchByLocation));
    }

    public void refreshMap(){
        map.clear();
        markerData.clear();
        searchView.setHint(this.getResources().getString(R.string.Search_for) + " "
                + this.getResources().getString(R.string.restaurant));
    }


    public void setResultMaker(String jsonString){
        hideDownloadingAnimation();
        if(jsonString.equals(Utils.TIMEOUT_ERROR) || jsonString.equals(Utils.CONNECTION_ERROR)){
            toast.makeText(getContext(), getString(R.string.Connection_Problem_Detail),
                    Toast.LENGTH_SHORT).show();
        }else{
            if(jsonString != null && jsonString.length() > 0 ){
                showRestaurantMarker(jsonString);
                Utils.setDebug("map results", jsonString);
            }
        }
    }


    private void showRestaurantMarker(String jsonString){

        try {
            JSONObject jObjectResult = new JSONObject(jsonString);
            Integer count = jObjectResult.getInt("total_count");
            mTotalCount = count;
            JSONArray jArray = jObjectResult.getJSONArray("Results");
            if(jArray.length() <= 0){
                toast.makeText(getContext(), getString(R.string.No_Search_Result),Toast.LENGTH_SHORT).show();
                return;
            }
            for(int i=0;i< jArray.length();i++){

                JSONObject jObject = jArray.getJSONObject(i);
                String restaurantId = jObject.getString("restaurant_id");

                boolean checkHaveData = false;
                for (DataMarker value: markerData.values()) {

                    if(value.restaurantId.equals(restaurantId)){
                        checkHaveData =true;
                        break;
                    }
                }
                if(checkHaveData)
                    continue;


                String name = jObject.getString("name");
                Double latitude = jObject.getDouble("latitude");
                Double longitude = jObject.getDouble("longitude");
                LatLng latLon = new LatLng(latitude,longitude);

                String typeRestaurant = "";
                JSONArray typeArray = jObject.getJSONArray("restaurant_types");
                if (typeArray.length() == 0) {
                    typeRestaurant = "Food & Drinks";
                }
                else {
                    for(int j =0;j< typeArray.length();j++){
                        JSONObject typeObject = typeArray.getJSONObject(j);
                        if (typeArray.length() == 1) {
                            typeRestaurant = typeObject.getString("restaurant_type_name");
                            if ("Uncategorized".equals(typeRestaurant)) {
                                typeRestaurant = "Food & Drinks";
                            }
                        }
                        else {
                            typeRestaurant += typeObject.getString("restaurant_type_name");
                            if(j != typeArray.length()-1)
                                typeRestaurant += " & ";
                            break;
                        }
                    }
                }

                String imagePath = "";
                JSONArray imageArray = jObject.getJSONArray("images");
                for(int j =0;j< imageArray.length();j++){
                    imagePath = imageArray.getString(j);
                    break;
                }

                DataMarker dataMarker = new DataMarker();
                dataMarker.restaurantName = name ;
                dataMarker.restaurantType = typeRestaurant;
                dataMarker.imageUrl = imagePath;
                dataMarker.restaurantId = restaurantId;
                addMarker(latLon, dataMarker);
            }
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
        hideDownloadingAnimation();
    }

    private void addMarker(LatLng latLon,DataMarker dataMarker){
        Marker marker = map.addMarker(new MarkerOptions().position(latLon)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)));
        markerData.put(marker.getId(),dataMarker);
    }


    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private Marker mMarker;

        @Override
        public View getInfoContents(final Marker marker) {
            mMarker = marker;
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View getInfoWindow(final Marker marker) {
            mMarker = marker;
            if(getActivity() == null) {  // must use this for prevent crash
                Utils.setDebug("getActivity","null");
                return null;
            }

            if ("m1".equals(marker.getId())) {
                Utils.setDebug("marker id", marker.getId());
                return null;
            }

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View myContentsView  = inflater.inflate(R.layout.infowindow, null);
            final ImageView restaurantImage = (ImageView) myContentsView.findViewById(R.id.image_restaurant);
            String imageUrl =  markerData.get(marker.getId()).imageUrl;
            if ("".equals(imageUrl)) {
                restaurantImage.setImageResource(R.drawable.slid3);
            } else {
//                Utils.setImageWithPicasso(getActivity(), imageUrl, restaurantImage);
                Picasso.with(mActivity).load(imageUrl).placeholder(R.drawable.slid3).error(R.drawable.slid2).into(restaurantImage);
            }
            TextView restaurantNameView = (TextView) myContentsView.findViewById(R.id.restaurant_name);
            TextView restaurantTypeView = (TextView) myContentsView.findViewById(R.id.restaurant_type);
            restaurantNameView.setText(markerData.get(marker.getId()).restaurantName);
            restaurantTypeView.setText(markerData.get(marker.getId()).restaurantType);
            TextView gotoMenu = (TextView) myContentsView.findViewById(R.id.goto_menu);
            gotoMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MenuActivity.class);
                    intent.putExtra("target", "menu");
                    intent.putExtra("restaurantId", markerData.get(marker.getId()).restaurantId);
                    intent.putExtra("restaurantName", markerData.get(marker.getId()).restaurantName);
                    intent.putExtra("restaurantImagePath", markerData.get(marker.getId()).imageUrl);
                    intent.putExtra("restaurantAddress", "");
                    intent.putExtra("userLatitude", myPosition.latitude);
                    intent.putExtra("userLongitude", myPosition.longitude);
                    getActivity().startActivity(intent);
                }
            });

            return myContentsView;
        }

        // For fix image not show at first time
        private class InfoWindowRefresher implements Callback {
            private Marker markerToRefresh;

            private InfoWindowRefresher(Marker markerToRefresh) {
                this.markerToRefresh = markerToRefresh;

            }

            @Override
            public void onSuccess() {
                checkFirstShowPopup = false;
                markerToRefresh.showInfoWindow();
            }

            @Override
            public void onError() {
                ;
            }
        }

    }

    private class DataMarker{

        String restaurantId;
        String restaurantName;
        String restaurantType;
        String imageUrl;

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

}
