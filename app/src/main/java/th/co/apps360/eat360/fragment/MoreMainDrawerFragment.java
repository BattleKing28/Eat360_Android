package th.co.apps360.eat360.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.co.apps360.eat360.Model.CategoryModel;
import th.co.apps360.eat360.Model.FacilityModel;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.Model.RCategory;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.Model.RSubCategory;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.adapter.FoodMultiAdapter;
import th.co.apps360.eat360.adapter.RestaurantMultiAdapter;
import th.co.apps360.eat360.adapter.RestaurantSingleAdapter;


public class MoreMainDrawerFragment extends Fragment
        implements DrawerLayout.DrawerListener {

    public MoreDataModel.Search_Tab mMainPageIndex = MoreDataModel.Search_Tab.FOOD;
    private OnDrawerActionListener mListener;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View rootView;
    private Button mGoButton;
    private Button mContinueButton;
    private RelativeLayout foodLayout;
    private LinearLayout restaurantLayout;

    // food vars
    private TabHost mFoodTabHost;
    private String mCategoryTabString;
    private String mCuisineTabString;
    private String mFacilityTabString;
    private String mOpentimeTabString;

    // restaurant vars
    private RecyclerView mRecyclerFacilitiesView;
    private RecyclerView mRecyclerCuisinesView;
    private RecyclerView mRecyclerTimesView;

    private RestaurantMultiAdapter mRFacilityAdapter;
    private RestaurantSingleAdapter mRCuisineAdapter;
    private RestaurantMultiAdapter mRTimerAdapter;
    private RCategory facilityCategory;
    private RCategory cuisineCategory;
    private RCategory timerCategory;
    private List<RCategory> mRFacilities;
    private List<RCategory> mRCuisines;
    private List<RCategory> mRTimers;
    TabHost.TabSpec mCategoryTab;
    TabHost.TabSpec mCuisineTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_more_drawer, container, false);

        foodLayout = (RelativeLayout) rootView.findViewById(R.id.f_more_drawer);
        restaurantLayout = (LinearLayout) rootView.findViewById(R.id.r_more_drawer);

        mGoButton = (Button) rootView.findViewById(R.id.go_button);
        mContinueButton = (Button) rootView.findViewById(R.id.continue_button);
        initFoodView();
        initRestaurantView();
        if (mMainPageIndex == MoreDataModel.Search_Tab.FOOD) {
            loadDrawerFoodData();
        } else {
            loadDrawerRestaurantData();
        }

        return rootView;
    }

    // TODO: for food tab
    private void initFoodView() {
        // tab host
        mFoodTabHost = (TabHost) rootView.findViewById(R.id.tab_host);
        mFoodTabHost.setup();

        mCategoryTab = mFoodTabHost.newTabSpec("Category");
        mCategoryTab.setIndicator(getString(R.string.category));
        mCategoryTab.setContent(R.id.category_container);
        mFoodTabHost.addTab(mCategoryTab);

        mCuisineTab = mFoodTabHost.newTabSpec("Cuisine");
        mCuisineTab.setIndicator(getString(R.string.cuisine));
        mCuisineTab.setContent(R.id.cuisine_container);
        mFoodTabHost.addTab(mCuisineTab);

//        TabHost.TabSpec mTimeTab = mFoodTabHost.newTabSpec("Time");
//        mTimeTab.setIndicator("Time");
//        mTimeTab.setContent(R.id.time_container);
//        mFoodTabHost.addTab(mTimeTab);
    }

    public void changeLanguage() {
        mCategoryTabString = getString(R.string.category);
        mCuisineTabString = getString(R.string.cuisine);
        mFacilityTabString = getString(R.string.facilities);
        mOpentimeTabString = getString(R.string.open_time);
//        initFoodView();
        mCategoryTab.setIndicator(mCategoryTabString);
        mCuisineTab.setIndicator(mCuisineTabString);
        mFoodTabHost.refreshDrawableState();
        loadDrawerRestaurantData();
    }

    public void loadDrawerFoodData() {
        foodLayout.setVisibility(View.VISIBLE);
        restaurantLayout.setVisibility(View.GONE);

        MoreFoodDataModel.foodSelCuisine = null;
        MoreFoodDataModel.foodSelCategoryIndexList.clear();
        MoreFoodDataModel.foodCategorySelectedCount = 0;
        for (int i = 0; i < MoreFoodDataModel.foodCategoryList.size(); i++) {
            MoreFoodDataModel.foodCategoryList.get(i).setChecked(false);
        }
        for (int i = 0; i < MoreFoodDataModel.foodCuisineList.size(); i++) {
            MoreFoodDataModel.foodCuisineList.get(i).setChecked(false);
        }

        final GridView foodCategoryGridView = (GridView) rootView.findViewById(R.id.category_gridview);
        foodCategoryGridView.setAdapter(new FoodMultiAdapter(getActivity(), MoreFoodDataModel.foodCategoryList));

        ArrayList<String> cuisineNameList  = new ArrayList<>();
        for (int i = 0; i < MoreFoodDataModel.foodCuisineList.size(); i++) {
            cuisineNameList.add(MoreFoodDataModel.foodCuisineList.get(i).getName());
        }
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(),
                R.layout.list_cuisine_item,
                R.id.item,
                cuisineNameList);
        ListView listCuisine = (ListView) rootView.findViewById(R.id.cuisine_listview);
        listCuisine.setAdapter(adapter);
        listCuisine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TextView tv = (TextView) view.findViewById(R.id.item);
                for (int i = 0; i < adapterView.getChildCount(); i ++) {
                    adapterView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white));
                    TextView tvi = (TextView) adapterView.getChildAt(i).findViewById(R.id.item);
                    tvi.setTextColor(getResources().getColor(R.color.blue));
                }
                tv.setBackgroundColor(getResources().getColor(R.color.blue));
                tv.setTextColor(getResources().getColor(R.color.white));
                MoreFoodDataModel.foodSelCuisine = MoreFoodDataModel.foodCuisineList.get(position);
            }
        });

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goButtonPrevActionFoodCallback();
                setSearchKeyword();
                mListener.goButtonPostActionFoodCallback();
                closeDrawer();
            }
        });

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goButtonPrevActionFoodCallback();
                setSearchKeyword();
                mListener.continueButtonPostActionFoodCallback();
                closeDrawer();
            }
        });
    }

    private void setSearchKeyword() {
        for (int i = 0; i < MoreFoodDataModel.foodCategoryList.size(); i++) {
            if (MoreFoodDataModel.foodCategoryList.get(i).isChecked()) {
                MoreFoodDataModel.foodSelCategoryIndexList.add(i);
            }
        }
        if (MoreFoodDataModel.foodSelCategoryIndexList.size() > 0) {
            MoreFoodDataModel.searchText = "";
            MoreFoodDataModel.searchType = "food_category";
            MoreFoodDataModel.tabNo = 0;
        }
        if (MoreFoodDataModel.foodSelCuisine != null) {
            MoreFoodDataModel.searchText = "";
            MoreFoodDataModel.searchType = "food_cuisine";
            MoreFoodDataModel.tabNo = 1;
        }
    }

    // TODO: for restaurant tab
    private void initRestaurantView() {
        mRecyclerFacilitiesView = (RecyclerView) rootView.findViewById(R.id.recycler_facilities_view);
        mRecyclerCuisinesView = (RecyclerView) rootView.findViewById(R.id.recycler_cuisines_view);
        mRecyclerTimesView = (RecyclerView) rootView.findViewById(R.id.recycler_times_view);
        mFacilityTabString = getString(R.string.facilities);
        mCuisineTabString = getString(R.string.cuisine);
        mOpentimeTabString = getString(R.string.open_time);
    }

    public void loadDrawerRestaurantData() {
        foodLayout.setVisibility(View.GONE);
        restaurantLayout.setVisibility(View.VISIBLE);

        MoreRestaurantDataModel.restaurantFacilitySelectedCount = 0;
        MoreRestaurantDataModel.restaurantSelFacilityIndexList.clear();
        for (int i = 0; i < MoreRestaurantDataModel.restaurantFacilityList.size(); i++) {
            MoreRestaurantDataModel.restaurantFacilityList.get(i).setChecked(false);
        }
        MoreRestaurantDataModel.restaurantSelCuisine = null;
        for (int i = 0; i < MoreRestaurantDataModel.restaurantCuisineList.size(); i++) {
            MoreRestaurantDataModel.restaurantCuisineList.get(i).setChecked(false);
        }

        ArrayList<RSubCategory> subCategoryListF = new ArrayList<>();
        for (FacilityModel aFacility : MoreRestaurantDataModel.restaurantFacilityList) {
            aFacility.setChecked(false);
            RSubCategory subCategory = new RSubCategory(aFacility.getName(), false);
            subCategoryListF.add(subCategory);
        }
        facilityCategory = new RCategory(mFacilityTabString, subCategoryListF);
        mRFacilities = Arrays.asList(facilityCategory);
        mRFacilityAdapter = new RestaurantMultiAdapter(getActivity(), mRFacilities);
        mRFacilityAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                RCategory expandedCategory = mRFacilities.get(parentPosition);
                String toastMessage = getActivity().getResources().getString(R.string.expanded, expandedCategory.getName());
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {
                RCategory collapsedCategory = mRFacilities.get(parentPosition);
                String toastMessage = getActivity().getResources().getString(R.string.expanded, collapsedCategory.getName());
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }

        });
        mRecyclerFacilitiesView.setAdapter(mRFacilityAdapter);
        mRecyclerFacilitiesView.setLayoutManager(new LinearLayoutManager(getActivity()));


        ArrayList<RSubCategory> subCategoryListC = new ArrayList<>();
        for (CategoryModel aCuisine :
                MoreRestaurantDataModel.restaurantCuisineList) {
            aCuisine.setChecked(false);
            RSubCategory subCategory = new RSubCategory(aCuisine.getName(), false);
            subCategoryListC.add(subCategory);
        }
        cuisineCategory = new RCategory(mCuisineTabString, subCategoryListC);
        mRCuisines = Arrays.asList(cuisineCategory);
        mRCuisineAdapter = new RestaurantSingleAdapter(getActivity(), mRCuisines);
        mRCuisineAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                RCategory expandedCategory = mRCuisines.get(parentPosition);
                String toastMessage = getActivity().getResources().getString(R.string.expanded, expandedCategory.getName());
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {
                RCategory collapsedCategory = mRCuisines.get(parentPosition);
                String toastMessage = getActivity().getResources().getString(R.string.expanded, collapsedCategory.getName());
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }

        });
        mRecyclerCuisinesView.setAdapter(mRCuisineAdapter);
        mRecyclerCuisinesView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<RSubCategory> subCategoryListT = new ArrayList<>();
        timerCategory = new RCategory(mOpentimeTabString, subCategoryListT);
        mRTimers = Arrays.asList(timerCategory);
        mRTimerAdapter = new RestaurantMultiAdapter(getActivity(), mRTimers);
        mRTimerAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                RCategory expandedCategory = mRTimers.get(parentPosition);
                String toastMessage = getActivity().getResources().getString(R.string.expanded, expandedCategory.getName());
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {
                RCategory collapsedCategory = mRTimers.get(parentPosition);
                String toastMessage = getActivity().getResources().getString(R.string.expanded, collapsedCategory.getName());
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerTimesView.setAdapter(mRTimerAdapter);
        mRecyclerTimesView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goButtonPrevActionRestaurantCallback();
                showDrawerRestaurantData();
                mListener.goButtonPostActionRestaurantCallback();
                closeDrawer();
            }
        });

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goButtonPrevActionRestaurantCallback();
                showDrawerRestaurantData();
                mListener.continueButtonPostActionRestaurantCallback();
                closeDrawer();
            }
        });
    }

    public void showDrawerRestaurantData() {
        // facilities
        for (int i = 0; i < MoreRestaurantDataModel.restaurantFacilityList.size(); i++) {
            if (MoreRestaurantDataModel.restaurantFacilityList.get(i).isChecked()) {
                MoreRestaurantDataModel.restaurantSelFacilityIndexList.add(i);
            }
        }
    }

    // init drawer layout
    public void setUp(int fragmentId, DrawerLayout drawerLayout, MoreDataModel.Search_Tab mainpageIndex) {
        rootView = getActivity().findViewById(fragmentId);
        mMainPageIndex = mainpageIndex;
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.app_name, R.string.app_name);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.addDrawerListener(this);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(rootView);
    }

    public void openDrawer(){
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(rootView);
        }
    }

    public void closeDrawer(){
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(rootView);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        if (mMainPageIndex == MoreDataModel.Search_Tab.FOOD)
            mListener.openFoodMoreButtonStateCallback();
        else
            mListener.openRestaurantMoreButtonStateCallback();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (mMainPageIndex == MoreDataModel.Search_Tab.FOOD)
            mListener.closeFoodMoreButtonStateCallback();
        else
            mListener.closeRestaurantMoreButtonStateCallback();
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        switch (newState) {
            case DrawerLayout.STATE_DRAGGING:
                break;
            case DrawerLayout.STATE_IDLE:
                break;
            case DrawerLayout.STATE_SETTLING:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDrawerActionListener) {
            mListener = (OnDrawerActionListener) context;
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

    public interface OnDrawerActionListener {
        void openFoodMoreButtonStateCallback();
        void closeFoodMoreButtonStateCallback();
        void goButtonPrevActionFoodCallback();
        void goButtonPostActionFoodCallback();
        void continueButtonPostActionFoodCallback();

        void openRestaurantMoreButtonStateCallback();
        void closeRestaurantMoreButtonStateCallback();
        void goButtonPrevActionRestaurantCallback();
        void goButtonPostActionRestaurantCallback();
        void continueButtonPostActionRestaurantCallback();
    }
}
