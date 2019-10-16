package th.co.apps360.eat360.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import th.co.apps360.eat360.APIs.ReverseGeoCodingAPI;
import th.co.apps360.eat360.APIs.SearchFoodAPI;
import th.co.apps360.eat360.Callback;
import th.co.apps360.eat360.Model.FoodSearchResultModel;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.activity.MainActivity;
import th.co.apps360.eat360.adapter.CardViewFoodResultAdapter;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.adapter.MarkedFIconsAdapter;

/**
 * Created by jakkrit.p on 28/07/2015.
 */
public class FragmentFoodResult extends Fragment {

    private static FragmentFoodResult mInstance;

    public static FragmentFoodResult getInstance() {
        if (mInstance == null)
            mInstance = new FragmentFoodResult();
        return mInstance;
    }

    private RecyclerView mFoodRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mTimerImageView;
    private TextView mLocationTextView;
    private TextView mRadiusTextView;
    private RecyclerView mCategoryRecyclerView;
    private TextView mCuisineTextView;
    private TextView noResult;
    private ImageView mBackButton;

    private MainActivity mActivity;

    private String searchText;
    private String searchLang;
    private String latitude;
    private String longitude;
    private String radius;
    private String selCategoryIDs;
    private String selMealIDs;
    private String selCuisineIDs;
    private String selIngredientIDs;
    private String selCurrency;
    private String topCategoryNames;

    private CardViewFoodResultAdapter mAdapter;
    private ArrayList<AsyncTask> allAsyncTask = new ArrayList<>();
//    private Callback.CallbackJsonResult callback;
//    private String possibleKey;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food_result, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh_layout);
        mFoodRecyclerView = (RecyclerView)rootView.findViewById(R.id.my_recycler_view);

        mLocationTextView = (TextView)rootView.findViewById(R.id.location);
        mRadiusTextView = (TextView)rootView.findViewById(R.id.radius);
        mTimerImageView = (ImageView)rootView.findViewById(R.id.image_timer);

        mCategoryRecyclerView = (RecyclerView)rootView.findViewById(R.id.sel_categories);
        mCuisineTextView = (TextView)rootView.findViewById(R.id.sel_cuisine);
        noResult = (TextView)rootView.findViewById(R.id.no_result);

        mActivity = (MainActivity)getActivity();
        receiveParamsFromFood();
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


    private void receiveParamsFromFood() {
        searchText = this.getArguments().getString("SearchText");
        searchLang = this.getArguments().getString("SearchLang");
        latitude = String.valueOf(this.getArguments().getDouble("Latitude"));
        longitude= String.valueOf(this.getArguments().getDouble("Longitude"));
        radius = String.valueOf(this.getArguments().getDouble("Radius"));
        selCategoryIDs = this.getArguments().getString("CategoryIDs") == null
                ? "" : this.getArguments().getString("CategoryIDs");
        selMealIDs = this.getArguments().getString("MealIDs") == null
                ? "" : this.getArguments().getString("MealIDs");
        selCuisineIDs = this.getArguments().getString("CuisineIDs") == null
                ? "" : this.getArguments().getString("CuisineIDs");
        selIngredientIDs = this.getArguments().getString("IngredientIDs") == null
                ? "" : this.getArguments().getString("IngredientIDs");
        selCurrency = Utils.getCurrentCurrency(getActivity());
        topCategoryNames = this.getArguments().getString("TopCategoryName");

        allAsyncTask.add(new ReverseGeoCodingAPI(getActivity(), Double.parseDouble(latitude), Double.parseDouble(longitude)).execute());
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager  = new LinearLayoutManager(this.getActivity());
        mFoodRecyclerView.setLayoutManager(mLayoutManager);
        mFoodRecyclerView.setHasFixedSize(true);
    }

    private void initMarkedView() {
        if (MoreFoodDataModel.foodSelCuisine != null) {
            mCuisineTextView.setText(MoreFoodDataModel.foodSelCuisine.getName());
            mCuisineTextView.setVisibility(View.VISIBLE);
            mCuisineTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    MoreFoodDataModel.foodSelCuisine = null;
                }
            });
        }
        if (MoreFoodDataModel.foodSelCategoryIndexList.size() > 0) {
            ArrayList<String> markedCategoryIconList = new ArrayList<>();
            ArrayList<Integer> markedCategoryPosList = new ArrayList<>();
            for (int i = 0; i < MoreFoodDataModel.foodSelCategoryIndexList.size(); i ++) {
                int idx = MoreFoodDataModel.foodSelCategoryIndexList.get(i);
                markedCategoryIconList.add(MoreFoodDataModel.foodCategoryList.get(idx).getIcon());
                markedCategoryPosList.add(i);
            }
            mCategoryRecyclerView.setAdapter(new MarkedFIconsAdapter(getActivity(), markedCategoryIconList, markedCategoryPosList));
        }
        mTimerImageView.setVisibility(View.VISIBLE);
    }

    public void downloadData() {
        setTitleActionbar();
        showResultView();
        showDownloadingAnimation();
        if (mFoodRecyclerView!=null) {
            mFoodRecyclerView.setAdapter(null);
        }
        searchFoodDownload();
    }

    public void setTitleActionbar() {
        TextView mTitleTextView = mActivity.showTitle();
        String sTitle = "";
        if (null != searchText && !"".equals(searchText)) {
            sTitle = this.getResources().getString(R.string.Menu) + ": " + searchText;
        }
        else if (!"".equals(topCategoryNames)) {
            sTitle = this.getResources().getString(R.string.Menu) + ": " + topCategoryNames;
        }
        mTitleTextView.setText(sTitle);
    }

    private void onBackPressed() {
        MoreFoodDataModel.foodSelCuisine = null;
        MoreFoodDataModel.foodSelCategoryIndexList.clear();
        getFragmentManager().popBackStack();
        mActivity.showLogo();
        mActivity.hideBackButton();
    }

    private void searchFoodDownload() {
        allAsyncTask.add(new SearchFoodAPI(getActivity(), searchText, searchLang, latitude, longitude,
                radius, selCategoryIDs, selMealIDs, selCuisineIDs, selIngredientIDs, selCurrency)
                .execute(new ConfigURL(getContext()).searchFoodURL));
    }

    public void setLocationName(String locationName) {
        if (locationName != null && locationName.length() > 0) {
            mLocationTextView.setText(locationName);
            String sRadius = "< " + radius + " km";
            mRadiusTextView.setText(sRadius);
        }
    }

    public void setFoodResult(String jsonString) {
        if (jsonString.equals(Utils.TIMEOUT_ERROR) || jsonString.equals(Utils.CONNECTION_ERROR)) {
            Utils.showConnectionProblemDialog(getActivity());
            showConnectionError();
        } else {
            if (jsonString.length() > 0) {
                loadFoodSearchResult(jsonString);
            } else {
                showNoResult();
            }
        }
        hideDownloadingAnimation();
    }

    private void loadFoodSearchResult(String jsonString) {
        try {
            JSONObject objAll = new JSONObject(jsonString);
            Boolean notfound = objAll.getBoolean("NotFound");
            if (notfound) {
                showNoResult();
                return;
            }
            JSONArray list = objAll.getJSONArray("Results");

            if (list.length() <= 0) {
                showNoResult();
                mFoodRecyclerView.setAdapter(null);
                return;
            }

            ArrayList <FoodSearchResultModel> foodList = new ArrayList <>();

            for (int i = 0; i < list.length(); i ++) {
                JSONObject obj =  list.getJSONObject(i);
                String food_id = obj.getString("id");
                String food_name = obj.getString("food_name");
                String restaurant_id = obj.getString("restaurant_id");
                String restaurant_name = obj.getString("restaurant_name");
                String lang_code = obj.getString("lang_code");
                String food_desc = obj.getString("description");
                String price = obj.getString("price");
                String currency = obj.getString("currency");
                JSONObject images = obj.getJSONObject("images");
                String img_path = images.getString("img_path");
                FoodSearchResultModel aFood = new FoodSearchResultModel(food_id, food_name, food_desc,
                        restaurant_id, restaurant_name, lang_code, price, currency, img_path);
                foodList.add(aFood);
            }

            setAdapterRecyclerView(foodList);

        } catch (JSONException e) {
            showNoResult();
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void setAdapterRecyclerView(ArrayList <FoodSearchResultModel> foodList) {
        showResultView();
        mAdapter = new CardViewFoodResultAdapter(mActivity, foodList, Double.valueOf(latitude), Double.valueOf(longitude));
        mFoodRecyclerView.setAdapter(mAdapter);
    }

    public void notifyDataSetChangedAdapter() {
        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();
    }

    private void showResultView() {
        noResult.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void showNoResult() {
        noResult.setVisibility(View.VISIBLE);
        noResult.setText(getActivity().getResources().getString(R.string.No_Search_Result));
        mSwipeRefreshLayout.setVisibility(View.GONE);
    }

    private void showConnectionError(){
        mSwipeRefreshLayout.setVisibility(View.GONE);
        noResult.setVisibility(View.VISIBLE);
        noResult.setText(getResources().getString(R.string.Connection_Problem) + "\n" + getResources().getString(R.string.Connection_Problem_Detail));
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
    public void onDestroy() {  // cancel all asynctask before close activity
        for (AsyncTask asyncItem : allAsyncTask ) {
            if (asyncItem != null && !asyncItem.isCancelled()) {
                asyncItem.cancel(true);
            }
        }
        super.onDestroy();
    }

}
