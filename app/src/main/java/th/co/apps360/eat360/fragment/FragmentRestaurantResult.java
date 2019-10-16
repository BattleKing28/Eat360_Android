package th.co.apps360.eat360.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import th.co.apps360.eat360.APIs.AutocompleteFoodAPI;
import th.co.apps360.eat360.APIs.RestaurantMultiDetailAPI;
import th.co.apps360.eat360.APIs.ReverseGeoCodingAPI;
import th.co.apps360.eat360.APIs.SearchRestaurantAPI;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.Model.RestaurantMultiDetailsModel;
import th.co.apps360.eat360.Model.RestaurantSearchResultModel;
import th.co.apps360.eat360.activity.MainActivity;
import th.co.apps360.eat360.adapter.MarkedRIconsAdapter;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.adapter.CardViewRestaurantResultAdapter;

import android.support.v4.app.Fragment;
import android.widget.Toast;

//import com.facebook.appevents.AppEventsLogger;

/**
 * Created by jakkrit.p on 28/07/2015.
 */
public class FragmentRestaurantResult extends Fragment {

    private MainActivity mActivity;
    private TextView noResult;
    private RecyclerView mRestaurantRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mTimerImageView;
    private TextView mLocationTextView;
    private TextView mRadiusTextView;
    private RecyclerView mCategoryRecyclerView;
    private TextView mCuisineTextView;
    private ImageView mBackButton;

    private String searchText;
    private String searchLang;
    private Double Latitude;
    private Double Longitude;
    private String radius;
    private String selCategoryIDs;
    private String selMealIDs;
    private String selCuisineIDs;
    private String selCurrency;
    private String topCategoryNames;

    private ArrayList<AsyncTask>  allAsyncTask = new ArrayList<>();
    private CardViewRestaurantResultAdapter mAdapter;
    private CallbackFoodFilter foodFilterCallback;
    private Toast toast;


    public interface CallbackFoodFilter {
        void callbackFoodFilter(String keyword,String searchType );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant_result, container, false);
        mRestaurantRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh_layout);

        mLocationTextView = (TextView)rootView.findViewById(R.id.location);
        mRadiusTextView = (TextView)rootView.findViewById(R.id.radius);
        mTimerImageView = (ImageView)rootView.findViewById(R.id.image_timer);

        mCategoryRecyclerView = (RecyclerView)rootView.findViewById(R.id.sel_categories);
        mCuisineTextView = (TextView)rootView.findViewById(R.id.sel_cuisine);
        noResult = (TextView)rootView.findViewById(R.id.no_result);

        mActivity = (MainActivity)getActivity();
        receiveParamsFromRestaurant();
        initRecyclerView();
        initMarkedView();
        downloadData();
        initListener();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBackButton = mActivity.showBackButton();
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void receiveParamsFromRestaurant() {
        searchText = this.getArguments().getString("SearchText");
        searchLang = this.getArguments().getString("SearchLang");
        Latitude = this.getArguments().getDouble("Latitude");
        Longitude= this.getArguments().getDouble("Longitude");
        radius = String.valueOf(this.getArguments().getDouble("Radius")) ;
        selCategoryIDs = this.getArguments().getString("CategoryIDs") == null
                ? "" : this.getArguments().getString("CategoryIDs");
        selMealIDs = this.getArguments().getString("MealIDs") == null
                ? "" : this.getArguments().getString("MealIDs");
        selCuisineIDs = this.getArguments().getString("CuisineIDs") == null
                ? "" : this.getArguments().getString("CuisineIDs");
        selCurrency = Utils.getCurrentCurrency(getActivity());
        topCategoryNames = this.getArguments().getString("TopCategoryName");

        allAsyncTask.add(new ReverseGeoCodingAPI(getActivity(), Latitude, Longitude).execute());
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager  = new LinearLayoutManager(this.getActivity());
        mRestaurantRecyclerView.setLayoutManager(mLayoutManager);
        mRestaurantRecyclerView.setHasFixedSize(true);
    }

    private void initMarkedView() {
        if (MoreRestaurantDataModel.restaurantSelCuisine != null) {
            mCuisineTextView.setText(MoreRestaurantDataModel.restaurantSelCuisine.getName());
            mCuisineTextView.setVisibility(View.VISIBLE);
            mCuisineTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    MoreRestaurantDataModel.restaurantSelCuisine = null;
                }
            });
        }
        if (MoreRestaurantDataModel.restaurantSelFacilityIndexList.size() > 0) {
            ArrayList<String> markedCategoryIconList = new ArrayList<>();
            ArrayList<Integer> markedCategoryPosList = new ArrayList<>();
            for (int i = 0; i < MoreRestaurantDataModel.restaurantSelFacilityIndexList.size(); i ++) {
                int idx = MoreRestaurantDataModel.restaurantSelFacilityIndexList.get(i);
                markedCategoryIconList.add(MoreRestaurantDataModel.restaurantFacilityList.get(idx).getIcon());
                markedCategoryPosList.add(i);
            }
            mCategoryRecyclerView.setAdapter(new MarkedRIconsAdapter(getActivity(), markedCategoryIconList, markedCategoryPosList));
        }
        mTimerImageView.setVisibility(View.VISIBLE);
    }

    public void downloadData() {
        setTitleActionbar();
        showResultView();
        showDownloadingAnimation();
        if (mRestaurantRecyclerView!=null) {
            mRestaurantRecyclerView.setAdapter(null);
        }
        searchRestaurantDownload();
    }

    public void setTitleActionbar() {
        TextView mTitleTextView = mActivity.showTitle();
        String sTitle = "";
        if (null != searchText && !"".equals(searchText)) {
            sTitle = searchText;
        }
        else if (!"".equals(topCategoryNames)) {
            sTitle = topCategoryNames;
        }
        mTitleTextView.setText(sTitle);
    }

    private void onBackPressed() {
        MoreRestaurantDataModel.restaurantSelCuisine = null;
        MoreRestaurantDataModel.restaurantSelFacilityIndexList.clear();
        getFragmentManager().popBackStack();
        mActivity.showLogo();
        mActivity.hideBackButton();
    }

    private void searchRestaurantDownload() {
        allAsyncTask.add(new SearchRestaurantAPI(getActivity(), searchText, searchLang,
                String.valueOf(Latitude), String.valueOf(Longitude),
                radius, selCategoryIDs, selMealIDs, selCuisineIDs, "false")
                .execute(new ConfigURL(getContext()).searchRestaurantURL));
    }

    public void setLocationName(String locationName) {
        if (locationName != null && locationName.length() > 0) {
            mLocationTextView.setText(locationName);
            String sRadius = "< " + radius + " km";
            mRadiusTextView.setText(sRadius);
        }
    }

    public  void setRestaurantResult(String jsonString) {
        if (jsonString.equals(Utils.TIMEOUT_ERROR) || jsonString.equals(Utils.CONNECTION_ERROR)) {
            Utils.showConnectionProblemDialog(getActivity());
            showConnectionError();
        } else {
            if (jsonString.length() > 0) {
                loadRestaurantSearchResult(jsonString);
            }
            else {
                showNoResult();
            }
        }
        hideDownloadingAnimation();
    }

    private void loadRestaurantSearchResult(String jsonString) {
        try {
            JSONObject objAll = new JSONObject(jsonString);
            Boolean notfound = objAll.getBoolean("NotFound");
            if (notfound) {
                showNoResult();
                return;
            }
            JSONObject rootObject = objAll.getJSONObject("Results");
            Integer total_count = rootObject.getInt("total_count");
            if (rootObject.has("restaurant_name")) {
                JSONObject nameObject = rootObject.getJSONObject("restaurant_name");
                JSONArray nameArray = nameObject.getJSONArray("results");
                if(nameArray.length() <= 0){
                    showNoResult();
                    mRestaurantRecyclerView.setAdapter(null);
                    return;
                }else{
                    showResultView();
                    ArrayList <RestaurantSearchResultModel> restaurantList = new ArrayList <>();
                    String restaurantIDs = "";
                    for (int i = 0; i < nameArray.length(); i ++) {
                        JSONObject obj =  nameArray.getJSONObject(i);
                        String id = obj.getString("id");
                        String name = obj.getString("name");
                        String type = obj.getString("type");
                        Double latitude = obj.getDouble("latitude");
                        Double longitude = obj.getDouble("longitude");
                        String url = obj.getString("url");
                        String fax = obj.getString("fax");
                        String email = obj.getString("email");
                        String postcode = obj.getString("postcode");
                        String address = obj.getString("address");
                        String country = obj.getString("country");
                        String state = obj.getString("state");
                        String city = obj.getString("city");
                        String imagepath = "";
                        if (obj.has("images")) {
                            JSONArray images = obj.getJSONArray("images");
                            if (images.length() > 0) {
                                imagepath = images.getString(0);
                                Utils.setDebug("Restaurant ImagePath", imagepath);
                            }
                        }
                        RestaurantSearchResultModel aRestaurant = new RestaurantSearchResultModel(
                                id, name, type, latitude, longitude, url, fax, email, postcode,
                                address, country, state, city, imagepath);
                        restaurantList.add(aRestaurant);
                    }

                    setSingleAdapterRecyclerView(restaurantList, Latitude, Longitude);
                }
            }

        } catch (JSONException e) {
            showNoResult();
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

    }

    private void setSingleAdapterRecyclerView(ArrayList<RestaurantSearchResultModel> details, Double latitude, Double longitude) {
        showResultView();
        mAdapter = new CardViewRestaurantResultAdapter(getActivity(), details, latitude, longitude);
        mRestaurantRecyclerView.setAdapter(mAdapter);
    }

    private void doMultipleSearch(String sRestaurantIDs) {
        allAsyncTask.add(new RestaurantMultiDetailAPI(getActivity(), sRestaurantIDs).execute());
        showDownloadingAnimation();
    }

    public void setMultipleRestaurantResult(String jsonString) {
        try {
            JSONObject objAll = new JSONObject(jsonString);
            JSONArray list = objAll.getJSONArray("Results");
            if (list != null && list.length() > 0) {
                showResultView();
                ArrayList<RestaurantMultiDetailsModel> restaurantMultiDetailsModelArrayList = new ArrayList<>();
                for (int i = 0; i < list.length(); i++) {
                    JSONObject aRestaurant = list.getJSONObject(i);
                    JSONObject res_type = aRestaurant.getJSONObject("restaurant_types");
                    Integer type_id = res_type.getInt("id");
                    String type_name = res_type.getString("restaurant_type_name");
                    Double latitude = aRestaurant.getDouble("latitude");
                    Double longitude = aRestaurant.getDouble("longitude");
                    Integer postcode = aRestaurant.getInt("postcode");
                    String url = aRestaurant.getString("url");
                    String email = aRestaurant.getString("email");
                    String phone = aRestaurant.getString("phone");
                    String fax = aRestaurant.getString("fax");
                    Integer res_id = aRestaurant.getInt("restaurant_id");
                    String owner_id = aRestaurant.getString("restaurant_owner_id");
                    String res_name = aRestaurant.getString("name");
                    String color_title = aRestaurant.getString("color_title");
                    String address = aRestaurant.getString("address");
                    String city1 = aRestaurant.getString("city1");
                    String city2 = aRestaurant.getString("city2");
                    String state = aRestaurant.getString("state");
                    String country = aRestaurant.getString("country");
                    String openinghour = aRestaurant.getString("openinghour");
                    String opentime = aRestaurant.getString("opentime");
                    String closetime = aRestaurant.getString("closetime");
                    String imagepath = aRestaurant.getString("img_path");
                    RestaurantMultiDetailsModel aDetail = new RestaurantMultiDetailsModel(res_id, res_name,
                            url, email, phone, fax, latitude, longitude, postcode, address, city1, city2,
                            state, country, openinghour, opentime, closetime, color_title, owner_id, imagepath, type_id, type_name);
                    restaurantMultiDetailsModelArrayList.add(aDetail);
                }
                setAdapterRecyclerView(restaurantMultiDetailsModelArrayList, Latitude, Longitude);
            }
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void setAdapterRecyclerView(ArrayList<RestaurantMultiDetailsModel> details, Double latitude, Double longitude) {
//        mAdapter = new CardViewRestaurantResultAdapter(getActivity(), details, latitude, longitude);
//        mRestaurantRecyclerView.setAdapter(mAdapter);
        hideDownloadingAnimation();
    }

    public void notifyDataSetChangedAdapter() {
        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();
    }

    private void showResultView() {
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        noResult.setVisibility(View.GONE);
    }

    private void showNoResult() {
        searchLang = Utils.getCurrentLanguage(getActivity());
        //String detail = "No results for \"" + searchText + "\" in radius of "+radius+" km and language "+searchLang.toUpperCase()+".";
        String detail = this.getResources().getString(R.string.No_Search_Result);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        noResult.setVisibility(View.VISIBLE);
        noResult.setText(detail);
    }

    private void showConnectionError(){
        mSwipeRefreshLayout.setVisibility(View.GONE);
        noResult.setVisibility(View.VISIBLE);
        noResult.setText(getResources().getString(R.string.Connection_Problem) + "\n" + getResources().getString(R.string.Connection_Problem_Detail));
    }


    private void showDownloadingAnimation() {
        showResultView();
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

    private void initListener() {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showDownloadingAnimation();
                downloadData();
            }
        });
    }

    @Override
    public void onResume() {
        mActivity.currentFragment = this;
        setTitleActionbar();
        searchLang = Utils.getCurrentLanguage(getActivity());
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){  // cancel all asynctask before close activity
        try {
            for (AsyncTask asyncItem : allAsyncTask) {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

//        searchView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence text, int start, int before, int count) {
//
//                searchText = text.toString().trim();
//                searchView.dismissDropDown();
//                searchView.setAdapter(null);
//                removeAllAutocompleteAsyncTask();
//
//                if (searchView.isPerformingCompletion()) {
//                    return;
//                }
//
//                if (searchText.length() > 3) {
//                    showDownloadingAnimation();
//                    allAsyncTask.add(new AutocompleteFoodAPI(getActivity(), searchText, Latitude, Longitude, radius).execute());
//
//                    //String suggestUrl = new ConfigURL(mContext).autocompleteSearch;
//                    //DownloadAutocompleteNew downloadSuggest = new DownloadAutocompleteNew();
//                    //downloadSuggest.execute(suggestUrl);
//
//                } else {
//                    hideDownloadingAnimation();
//                }
//
//                /*searchText = text.toString().trim();
//                if (searchText.length() > 3) {
//                    String suggestUrl = new ConfigURL(mContext).searchRestaurantURL;
//                    DownloadSuggest downloadSuggest = new DownloadSuggest();
//                    downloadSuggest.execute(suggestUrl);
//                } else {
//                    searchView.setAdapter(null);
//                }*/
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                if (!checkShowDropdown) {
//                    searchView.dismissDropDown();
//                    checkShowDropdown = true;
//                }
//
//                if (editable.length() > 0)
//                    clearButton.setVisibility(View.VISIBLE);
//                else
//                    clearButton.setVisibility(View.GONE);
//            }
//        });
//
//        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    doSearch();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                doSearch();
//            }
//        });
//
//        clearButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchView.setText("");
//            }
//        });
//
//        closeTagButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                searchView.setText("");
//                //searchType = "";
//                searchText = "";
//                topCategoryID = "";
//                topCategoryName = "";
//
//                //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.Search) + " \"" + searchText + "\"");
//
//                setTitleActionbar();
//
//                Utils.setUseRestaurantFilterTemp(getActivity(), false);
//                Utils.setUseFoodFilter(getActivity(), false);
//                Utils.setUseFoodFilterTemp(getActivity(), false);
//
//                setTag();
//                showDownloadingAnimation();
//                doMultipleSearch();
//            }
//        });
//
//    }


//    private void doSearch() {
//
//        searchText = searchView.getText().toString().trim();
//        checkShowDropdown = false;
//        setTag();
//        Utils.hideSoftKeyboard(getActivity(), searchView);
//        searchRestaurantDownload();
//    }
//
//    public void doRefresh() {
//
//        setTitleActionbar();
//        setTag();
//
//        searchLang = Utils.getCurrentLanguage(getActivity());
//        searchLangTemp = searchLang;
//
//        searchView.setHint(this.getResources().getString(R.string.search_hint));
//        searchView.requestLayout();
//
//        boolean checkUseRestaurantFilter = Utils.getUseRestaurantFilterTemp(getActivity());
//        boolean checkUseFoodFilter = Utils.getUseFoodFilter(getActivity());
//
//        //if((searchText != null && tagType!=null && searchText.length() > 0 && tagType.length() > 0)){
//
//        if(checkUseRestaurantFilter)
//            doSearch();
//        else if(checkUseFoodFilter){
//            foodFilterCallback.callbackFoodFilter(searchText,searchType);
//        }else
//            doMultipleSearch();
//
//
//    }
//
//
//
//    private void setSuggestAdapter(final ArrayList<String> keyArray,final ArrayList <String>  keyValue){
//        SuggestSearchAdapter adapter = new SuggestSearchAdapter(getActivity(), R.layout.item_suggest_layout, keyArray, keyValue);
//
//        searchView.setAdapter(adapter);
//        searchView.showDropDown();
//        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                Utils.hideSoftKeyboard(getActivity(), searchView);
//
//                //searchText = searchView.getText().toString();
//                searchText = searchView.getText().toString().replace(getString(R.string.contain) + " \"", "").replace("\"", "");
//
//                tagSearch.setVisibility(View.VISIBLE);
//                searchType = keyArray.get(position);
//
//                Utils.setTopCategoryOrAutocompleteFilterMode(getActivity(), Utils.AUTOCOMPLETE_FILTER_MODE);
//                Utils.checkAndSetFilter(getActivity(), searchType);
//                doRefresh();
//                setTag();
//            }
//        });
//
//    }


//    public  void setAutoComplete(String jsonString){
//        ArrayList <String>  valueArray = new ArrayList <> ();
//        ArrayList <String>  keyArray = new ArrayList <> ();
//
//        try {
//
//            JSONObject objResult = new JSONObject(jsonString);
//            JSONObject obj = objResult.getJSONObject("results");
//
//            Iterator keys = obj.keys();
//            while( keys.hasNext() ){
//
//                String key = keys.next().toString();
//                if(!key.contains("possible_keys")){
//                    JSONObject data = obj.getJSONObject(key);
//                    JSONArray list = data.getJSONArray("results");
//
//                    for(int i=0;i<list.length();i++){
//                        JSONObject tempData = list.getJSONObject(i);
//
//                        int id = tempData.getInt("id");
//                        String dat= tempData.getString("name");
//                        if(id == 0){
//                            dat = getString(R.string.contain)+" \""+dat+"\"";
//                        }
//                        keyArray.add(key);
//                        valueArray.add(dat);
//                    }
//                }
//
//
//            }
//
//            if((keyArray == null || keyArray.size() <= 0) && searchText.length() > 3){
//                toast.makeText(getActivity(), getString(R.string.No_Search_Result), Toast.LENGTH_SHORT).show();
//
//            }else{
//                if(toast!=null)
//                    toast.cancel();
//                setSuggestAdapter(keyArray, valueArray);
//            }
//
//
//
//        } catch (JSONException e) {
//            toast.makeText(getActivity(), getString(R.string.No_Search_Result), Toast.LENGTH_SHORT).show();
//            Utils.setDebug("crash", e.getLocalizedMessage());
//        }
//
//        hideDownloadingAnimation();
//    }
//
//    private void removeAllAutocompleteAsyncTask(){
//        try{
//            for (AsyncTask asyncItem : allAsyncTask ){
//                if(asyncItem != null && !asyncItem.isCancelled()
//                        && asyncItem instanceof AutocompleteFoodAPI){
//                    asyncItem.cancel(true);
//                }
//            }
//        }catch (Exception e){
//            Utils.setDebug("crash", e.getLocalizedMessage());
//        }
//    }

}
