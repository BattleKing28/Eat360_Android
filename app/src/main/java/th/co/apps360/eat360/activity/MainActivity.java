package th.co.apps360.eat360.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
//import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.loopj.android.http.RequestParams;
import com.yayandroid.locationmanager.LocationBaseActivity;
import com.yayandroid.locationmanager.LocationConfiguration;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.LogType;
import com.yayandroid.locationmanager.constants.ProviderType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import th.co.apps360.eat360.APIManager;
import th.co.apps360.eat360.APIs.AutocompleteMapAPI;
import th.co.apps360.eat360.APIs.AutocompleteRestaurantAPI;
import th.co.apps360.eat360.APIs.DownloadLanguagesAPI;
import th.co.apps360.eat360.APIs.FoodDetailAPI;
import th.co.apps360.eat360.APIs.FoodImageAPI;
import th.co.apps360.eat360.APIs.FoodIngredientsAPI;
import th.co.apps360.eat360.APIs.GetTopFacilityAPI;
import th.co.apps360.eat360.APIs.GetUserInfoAPI;
import th.co.apps360.eat360.APIs.LogoutAPI;
import th.co.apps360.eat360.APIs.RestaurantMultiDetailAPI;
import th.co.apps360.eat360.APIs.ReverseGeoCodingAPI;
import th.co.apps360.eat360.APIs.SearchFoodAPI;
import th.co.apps360.eat360.APIs.SearchRestaurantAPI;
import th.co.apps360.eat360.APIs.UpdateUsernameAPI;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.ConfigURLs;
import th.co.apps360.eat360.GCM.GcmRegistration;
import th.co.apps360.eat360.Model.CategoryModel;
import th.co.apps360.eat360.Model.FacilityModel;
import th.co.apps360.eat360.Model.LocationModel;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.SetDefaultFont;
import th.co.apps360.eat360.adapter.CardViewFoodResultAdapter;
import th.co.apps360.eat360.adapter.SuggestSearchAdapter;
import th.co.apps360.eat360.APIs.AutocompleteFoodAPI;
import th.co.apps360.eat360.APIs.DownloadCurrencyAPI;
import th.co.apps360.eat360.APIs.GetTopCategoryAPI;
import th.co.apps360.eat360.APIs.SearchByLocationAPI;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.fragment.FragmentFood;
import th.co.apps360.eat360.fragment.FragmentFoodDetail;
import th.co.apps360.eat360.fragment.FragmentFoodResult;
import th.co.apps360.eat360.fragment.FragmentMap;
import th.co.apps360.eat360.fragment.FragmentRestaurant;
import th.co.apps360.eat360.fragment.FragmentRestaurantResult;
import th.co.apps360.eat360.fragment.MoreMainDrawerFragment;
import th.co.apps360.eat360.fragment.NavigationDrawerFragment;
import th.co.apps360.eat360.R;

public class MainActivity extends LocationBaseActivity
        implements
//        ConnectionCallbacks,
//        OnConnectionFailedListener,
//        LocationListener,
        Utils.callbackShowDialog,
        SuggestSearchAdapter.Callback,
        CardViewFoodResultAdapter.OnFoodInfoActionListener,
        AutocompleteFoodAPI.ResultCallback,
        AutocompleteMapAPI.ResultCallback,
        AutocompleteRestaurantAPI.ResultCallback,
        GetTopCategoryAPI.ResultCallback,
        GetTopFacilityAPI.ResultCallback,
        DownloadCurrencyAPI.ResultCallback,
        DownloadLanguagesAPI.ResultCallback,
        ReverseGeoCodingAPI.ResultCallback,
        SearchByLocationAPI.ResultCallback,
        SearchFoodAPI.ResultCallback,
        SearchRestaurantAPI.ResultCallback,
        RestaurantMultiDetailAPI.ResultCallback,
        LogoutAPI.ResultCallback,
        UpdateUsernameAPI.ResultCallback,
        GetUserInfoAPI.ResultCallback,
        FragmentFood.OnMoreActionListener,
        FragmentRestaurant.OnMoreActionListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        MoreMainDrawerFragment.OnDrawerActionListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private MoreMainDrawerFragment mMoreMainDrawerFragment;
    private ImageButton foodSearchBt;
    private ImageButton mapSearchBt;
    private ImageButton restaurantSearchBt;
    private DrawerLayout drawerLayout;
    public Fragment currentFragment;
    private ActionBar mActionBar;
    private SwipeRefreshLayout swipeRefresh;
    public static int REQUEST_CODE = 24;
    public static String MAP_SEARCH_MENU = "map";
    public static String SEARCH_MENU = "menu";
    public MoreDataModel.Search_Tab mainpageIndex = MoreDataModel.Search_Tab.FOOD;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetDefaultFont.setDefaultFont(this, "MONOSPACE", "OpenSans-Regular.ttf"); // set font
        setContentView(R.layout.activity_main);

        foodSearchBt = (ImageButton) findViewById(R.id.food_search);
        mapSearchBt = (ImageButton) findViewById(R.id.map_search);
        restaurantSearchBt = (ImageButton) findViewById(R.id.restaurant_search);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_layout);

        if (checkGooglePlayService()) {
            buildGoogleApiClient();
        }

        LocationManager.setLogType(LogType.GENERAL);
        getLocation();

        initData();
        initSwipeRefresh();
        initFragment();
        initActionbar();
        setOnTouchButton();

        mMoreMainDrawerFragment = (MoreMainDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.more_drawer);
        mMoreMainDrawerFragment.setUp(R.id.more_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mainpageIndex);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.downloadLanguage();
            mNavigationDrawerFragment.downloadCurrency();
        }
    }

    @Override
    public void onStart() {
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getLocationManager().isWaitingForLocation()
                && !getLocationManager().isAnyDialogShowing()) {
            showDownloadingAnimation();
        }

        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setLanguage();
            mNavigationDrawerFragment.setCurrency();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideDownloadingAnimation();
    }


    @Override
    public LocationConfiguration getLocationConfiguration() {
        return new LocationConfiguration()
                .keepTracking(true)
                .askForGooglePlayServices(true)
                .setMinAccuracy(200.0f)
                .setWaitPeriod(ProviderType.GOOGLE_PLAY_SERVICES, 5 * 1000)
                .setWaitPeriod(ProviderType.GPS, 10 * 1000)
                .setWaitPeriod(ProviderType.NETWORK, 5 * 1000)
                .setGPSMessage("Would you mind to turn GPS on?")
                .setRationalMessage("Gimme the permission!");
    }

    @Override
    public void onLocationFailed(int failType) {
        hideDownloadingAnimation();

        switch (failType) {
            case FailType.PERMISSION_DENIED: {
                Utils.showToast(this, "Couldn't get location, because user didn't give permission!");
                break;
            }
            case FailType.GP_SERVICES_NOT_AVAILABLE:
            case FailType.GP_SERVICES_CONNECTION_FAIL: {
                Utils.showToast(this, "Couldn't get location, because Google Play Services not available!");
                break;
            }
            case FailType.NETWORK_NOT_AVAILABLE: {
                Utils.showToast(this, "Couldn't get location, because network is not accessible!");
                break;
            }
            case FailType.TIMEOUT: {
                Utils.showToast(this, "Couldn't get location, and timeout!");
                break;
            }
            case FailType.GP_SERVICES_SETTINGS_DENIED: {
                Utils.showToast(this, "Couldn't get location, because user didn't activate providers via settingsApi!");
                break;
            }
            case FailType.GP_SERVICES_SETTINGS_DIALOG: {
                Utils.showToast(this, "Couldn't display settingsApi dialog!");
                break;
            }
        }
        getLocationFromIP(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        hideDownloadingAnimation();
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        displayLocation();
    }


    protected synchronized void buildGoogleApiClient() {
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(AppIndex.API)
                    .build();
        }
    }


    private void displayLocation() {
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Utils.setDebug("MainActivity", latitude + ", " + longitude);
            getLocationDetailFromLatLon(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            Utils.setDebug("MainActivity", "(Couldn't get the location. Make sure location is enabled on the device)");
            if (Utils.isLocationEnabled(this)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    showSettingsAlert(this);
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation == null) {
                    getLocationFromIP(this);
                }
            } else {
                showSettingsAlert(this);
            }
        }
    }

    public void showSettingsAlert(final Context context) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        alertDialog.setCancelable(true);
        //Setting Dialog Title
        alertDialog.setTitle(R.string.enable_location_title);

        //Setting Dialog Message
        alertDialog.setMessage(R.string.enable_location_detail);

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getLocationFromIP(context);
            }
        });

        alertDialog.show();
    }

    public void getLocationFromIP(final Context context) {
        showDownloadingAnimation();
        String url = ConfigURL.server1 + "/get_address_by_ip";
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    hideDownloadingAnimation();
                    if (response.has("StatusCode")) {
                        String status_code = response.getString("StatusCode");
                        String message = response.getString("StatusMsg");
                        JSONObject locationObj = response.getJSONObject("Location");
                        if (locationObj == null) {
                            showSettingsAlert(context);
                        } else {
                            String country_code = locationObj.getString("country_code");
                            String country_name = locationObj.getString("country_name");
                            String region_name = locationObj.getString("region_name");
                            String city_name = locationObj.getString("city_name");
                            double lat = locationObj.getDouble("latitude");
                            double lon = locationObj.getDouble("longitude");
                            LocationModel locationDetails = new LocationModel(lat, lon, city_name, country_name, "", "");
                            Utils.setLocationDetails(locationDetails);
                        }
                    } else if (response.has("status_code")) {
                        String status_code = response.getString("status_code");
                        String message = response.getString("status_msg");
                        showSettingsAlert(context);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showSettingsAlert(context);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                hideDownloadingAnimation();
                Utils.setDebug("iptolocation", "error: " + res);
                showSettingsAlert(context);
            }

        });
    }

    public void getLocationDetailFromLatLon(final double lat, final double lon) {
        showDownloadingAnimation();
        String url = ConfigURL.reverseGeoCodingURL;
        String latlngParam = "latlng=" + lat + "," + lon;
        String keyParam =  "key=" + this.getString(R.string.googlemap_geocoding_apikey);
        url += "?" + latlngParam + "&" + keyParam;

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    hideDownloadingAnimation();
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

                    LocationModel locationDetails = new LocationModel(lat, lon, city, country, postal_code, "");
                    Utils.setLocationDetails(locationDetails);
                } catch (JSONException e) {
                    hideDownloadingAnimation();
                    Utils.setDebug("crash", e.getLocalizedMessage());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                hideDownloadingAnimation();
                Utils.setDebug("failure", res);
            }
        });

    }

    private void initSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(
                R.color.white
        );
        swipeRefresh.setProgressViewOffset(false, 0, (int) Utils.convertDpToPx(this, 10));
        swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.blue));
        swipeRefresh.setEnabled(false);
        swipeRefresh.bringToFront();
    }

    public void showDownloadingAnimation() {
        swipeRefresh.setEnabled(true);
        swipeRefresh.bringToFront();
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
            }
        });

    }

    public void hideDownloadingAnimation() {
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);
                swipeRefresh.setEnabled(false);
            }
        });
    }


    private void initData() {
        new GcmRegistration().execute(this);
        foodSearchBt.setActivated(true);
        APIManager.downloadAllCategories(this);
        APIManager.downloadAllCuisines(this);
        APIManager.downloadAllFacilities(this);
    }



    private void initFragment() {
        currentFragment = FragmentFood.getInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, currentFragment)
                .addToBackStack(null).commit();
    }

    // TODO: actionbar methods
    private void initActionbar() {
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
                if (drawerLayout.isDrawerOpen(GravityCompat.END))
                    drawerLayout.closeDrawers();
                else
                    drawerLayout.openDrawer(GravityCompat.END);
            }
        });
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

    // TODO: tab buttons
    private void setOnTouchButton() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        foodSearchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentFragment instanceof FragmentFood)
                    return;

                mainpageIndex = MoreDataModel.Search_Tab.FOOD;
                mMoreMainDrawerFragment = (MoreMainDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.more_drawer);
                mMoreMainDrawerFragment.setUp(R.id.more_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mainpageIndex);

                foodSearchBt.setActivated(true);
                mapSearchBt.setActivated(false);
                restaurantSearchBt.setActivated(false);

                foodSearchBt.setImageResource(R.drawable.search_food_hover);
                mapSearchBt.setImageResource(R.drawable.search_map);
                restaurantSearchBt.setImageResource(R.drawable.search_rest);

                currentFragment = FragmentFood.getInstance();
                fragmentManager.beginTransaction().replace(R.id.container, currentFragment).commit();
                showLogo();
            }
        });

        mapSearchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentFragment instanceof FragmentMap)
                    return;

                foodSearchBt.setActivated(false);
                mapSearchBt.setActivated(true);
                restaurantSearchBt.setActivated(false);

                foodSearchBt.setImageResource(R.drawable.search_food);
                mapSearchBt.setImageResource(R.drawable.search_map_hover);
                restaurantSearchBt.setImageResource(R.drawable.search_rest);
                currentFragment = FragmentMap.getInstance();
                fragmentManager.beginTransaction().replace(R.id.container, currentFragment).commit();
                showLogo();
            }
        });

        restaurantSearchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentFragment instanceof FragmentRestaurant)
                    return;

                mainpageIndex = MoreDataModel.Search_Tab.RESTAURANT;
                mMoreMainDrawerFragment = (MoreMainDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.more_drawer);
                mMoreMainDrawerFragment.setUp(R.id.more_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mainpageIndex);

                foodSearchBt.setActivated(false);
                mapSearchBt.setActivated(false);
                restaurantSearchBt.setActivated(true);

                foodSearchBt.setImageResource(R.drawable.search_food);
                mapSearchBt.setImageResource(R.drawable.search_map);
                restaurantSearchBt.setImageResource(R.drawable.search_rest_hover);
                currentFragment = FragmentRestaurant.getInstance();
                fragmentManager.beginTransaction().replace(R.id.container, currentFragment).commit();
                showLogo();
            }
        });
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
    public void onBackPressed() {
        if (mMoreMainDrawerFragment.isDrawerOpen()) {
            mMoreMainDrawerFragment.closeDrawer();
        } else {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            super.onBackPressed();
            if (backStackEntryCount <= 1) {
                Utils.initAllGlobalData();
                MainActivity.this.finish();
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle(this.getResources().getString(R.string.confirm_quit));
//                builder.setCancelable(true);
//                builder.setPositiveButton(this.getResources().getString(R.string.yes),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//                builder.setNegativeButton(this.getResources().getString(R.string.no),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
            }
        }
    }

    @Override
    public void showDialogUpdatePlayService() {
        showDialogPlayService();
    }

    private void showDialogPlayService() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View languageLayout = inflater.inflate(R.layout.dialog_download_playservice_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(languageLayout);
        builder.setTitle(this.getResources().getString(R.string.google_play_service));
        builder.setCancelable(true);
        builder.setPositiveButton(this.getResources().getString(R.string.update),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        String packageName = "com.google.android.gms";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));

                        } catch (Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }

    public void setTitle(int option) {
        if (option == 0) {
            showLogo();
        } else if (option == 1) {
            showLogo();
        } else if (option == 2) {
            showLogo();
        }
    }

    private boolean checkGooglePlayService() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
//        if (resultCode == Activity.RESULT_OK) {
//            String menu = intent.getStringExtra(SEARCH_MENU);
//
////            if (menu.equals(MAP_SEARCH_MENU)) {
////                FragmentMap mapFragment = new FragmentMap();
////
////                foodSearchBt.setBackgroundColor(getResources().getColor(R.color.black));
////                mapSearchBt.setBackgroundColor(getResources().getColor(R.color.blue));
////                restaurantSearchBt.setBackgroundColor(getResources().getColor(R.color.black));
////
////                foodSearchBt.setImageResource(R.drawable.search_food);
////                mapSearchBt.setImageResource(R.drawable.search_map_hover);
////                restaurantSearchBt.setImageResource(R.drawable.search_rest);
////
////                FragmentManager fragmentManager = getSupportFragmentManager();
////                fragmentManager.beginTransaction().replace(R.id.container, mapFragment).commit();
////            }
//        }
    }

    @Override
    public void showDialog(Context context, String title, String detail) {
        Utils.showDialog(context, title, detail);
    }

    @Override
    public void moreSuggestCallback() {
        //((FragmentFood)currentFragment).updateSuggest();
    }

    @Override
    public void onCurrencySelected() {
        if (currentFragment instanceof FragmentFoodResult) {
            ((FragmentFoodResult) currentFragment).notifyDataSetChangedAdapter();
        } else if (currentFragment instanceof FragmentRestaurantResult) {
            ((FragmentRestaurantResult) currentFragment).notifyDataSetChangedAdapter();
        } else if (currentFragment instanceof FragmentFoodDetail) {
            ((FragmentFoodDetail) currentFragment).changeCurrency();
        }
    }

    @Override
    public void onLanguageSelected() {
        mMoreMainDrawerFragment.changeLanguage();
        if (currentFragment instanceof FragmentMap) {
            ((FragmentMap) currentFragment).refreshMap();
        } else if (currentFragment instanceof FragmentFood) {
            ((FragmentFood) currentFragment).changeLanguage();
        } else if (currentFragment instanceof FragmentRestaurant) {
            ((FragmentRestaurant) currentFragment).changeLanguage();
        } else if (currentFragment instanceof FragmentFoodResult) {
            ((FragmentFoodResult) currentFragment).downloadData();
        } else if (currentFragment instanceof FragmentRestaurantResult) {
            ((FragmentRestaurantResult) currentFragment).downloadData();
        } else if (currentFragment instanceof FragmentFoodDetail) {
            ((FragmentFoodDetail) currentFragment).downloadFoodDetailFromLanguage();
        }
    }

    @Override
    public void autocompleteResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentFood)
            ((FragmentFood) currentFragment).setAutocomplete(jsonStringResult);
        else if (currentFragment instanceof FragmentRestaurant)
            ((FragmentRestaurant) currentFragment).setAutocomplete(jsonStringResult);
        else if (currentFragment instanceof FragmentMap)
            ((FragmentMap) currentFragment).setAutocomplete(jsonStringResult);
    }

    @Override
    public void SearchByLocationResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentMap)
            ((FragmentMap) currentFragment).setResultMaker(jsonStringResult);
    }

    @Override
    public void downloadCurrencyResultCallback(String jsonStringResult) {
        mNavigationDrawerFragment.setStringCurrencyToVariable(jsonStringResult);
    }

    @Override
    public void downloadLanguageResultCallback(String jsonStringResult) {
        mNavigationDrawerFragment.setStringLanguageToVariable(jsonStringResult);
    }


    // TODO: food
    @Override
    public void setMoreFoodCallback() {
        mMoreMainDrawerFragment.loadDrawerFoodData();
    }

    @Override
    public void showMoreFoodCallback() {
        mMoreMainDrawerFragment.openDrawer();
    }

    @Override
    public void passParamsToResultCallback(Bundle bundle) {
        currentFragment = new FragmentFoodResult();
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
    public void getTopCategoryAPIResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentFood)
            ((FragmentFood) currentFragment).setTopCategories(jsonStringResult);
    }

    @Override
    public void openFoodMoreButtonStateCallback() {
        if (currentFragment instanceof FragmentFood) {
            ((FragmentFood) currentFragment).setMoreButtonStateforOpenedDrawer();
        }
    }

    @Override
    public void closeFoodMoreButtonStateCallback() {
        if (currentFragment instanceof FragmentFood) {
            ((FragmentFood) currentFragment).setMoreButtonStateforClosedDrawer();
        }
    }

    @Override
    public void goButtonPrevActionFoodCallback() {
        if (currentFragment instanceof FragmentFood) {
            ((FragmentFood) currentFragment).goButtonPrevAction();
        }
    }

    @Override
    public void goButtonPostActionFoodCallback() {
        if (currentFragment instanceof FragmentFood) {
            ((FragmentFood) currentFragment).goButtonPostAction();
        }
    }

    @Override
    public void continueButtonPostActionFoodCallback() {
        if (currentFragment instanceof FragmentFood) {
            ((FragmentFood) currentFragment).continueButtonPostAction();
        }
    }


    // TODO: restaurant
    @Override
    public void setMoreRestaurantCallback() {
        mMoreMainDrawerFragment.loadDrawerRestaurantData();
    }

    @Override
    public void showMoreRestaurantCallback() {
        mMoreMainDrawerFragment.openDrawer();
    }

    @Override
    public void passParamsToResResultCallback(Bundle bundle) {
        currentFragment = new FragmentRestaurantResult();
        currentFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, currentFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void getTopFacilityAPIResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentRestaurant)
            ((FragmentRestaurant) currentFragment).setTopFacilities(jsonStringResult);
    }

    @Override
    public void getTopCuisineAPIResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentRestaurant)
            ((FragmentRestaurant) currentFragment).setTopCuisines(jsonStringResult);
    }

    @Override
    public void openRestaurantMoreButtonStateCallback() {
        if (currentFragment instanceof FragmentRestaurant) {
            ((FragmentRestaurant) currentFragment).setMoreButtonStateforOpenedDrawer();
        }
    }

    @Override
    public void closeRestaurantMoreButtonStateCallback() {
        if (currentFragment instanceof FragmentRestaurant) {
            ((FragmentRestaurant) currentFragment).setMoreButtonStateforClosedDrawer();
        }
    }

    @Override
    public void goButtonPrevActionRestaurantCallback() {
        if (currentFragment instanceof FragmentRestaurant) {
            ((FragmentRestaurant) currentFragment).goButtonPrevAction();
        }
    }

    @Override
    public void goButtonPostActionRestaurantCallback() {
        if (currentFragment instanceof FragmentRestaurant) {
            ((FragmentRestaurant) currentFragment).goButtonPostAction();
        }
    }

    @Override
    public void continueButtonPostActionRestaurantCallback() {
        if (currentFragment instanceof FragmentRestaurant) {
            ((FragmentRestaurant) currentFragment).continueButtonPostAction();
        }
    }

    @Override
    public void locationResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentRestaurantResult) {
            ((FragmentRestaurantResult) currentFragment).setLocationName(jsonStringResult);
        } else if (currentFragment instanceof FragmentFoodResult) {
            ((FragmentFoodResult) currentFragment).setLocationName(jsonStringResult);
        }
    }

    @Override
    public void locationResponseCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentMap) {
            ((FragmentMap) currentFragment).setMyLocationData(jsonStringResult);
        }
    }

    @Override
    public void searchResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentFoodResult)
            ((FragmentFoodResult) currentFragment).setFoodResult(jsonStringResult);
        else if (currentFragment instanceof FragmentRestaurantResult)
            ((FragmentRestaurantResult) currentFragment).setRestaurantResult(jsonStringResult);
    }

    @Override
    public void multipleRestaurantDetailsResultCallback(String jsonStringResult) {
        if (currentFragment instanceof FragmentRestaurantResult)
            ((FragmentRestaurantResult) currentFragment).setMultipleRestaurantResult(jsonStringResult);
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

    @Override
    public void getUserInfoCallback(String jsonStringResult) {
        mNavigationDrawerFragment.getUserInfoResult(jsonStringResult);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("https://eat360app.com/"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


}
