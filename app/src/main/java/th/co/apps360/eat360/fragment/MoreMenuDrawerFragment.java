package th.co.apps360.eat360.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import th.co.apps360.eat360.Model.MenuCategory;
import th.co.apps360.eat360.Model.MoreMenuModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.activity.MenuActivity;
import th.co.apps360.eat360.adapter.MenuDrawerAdapter;

public class MoreMenuDrawerFragment extends Fragment
        implements DrawerLayout.DrawerListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public MoreMenuModel.Menu_Tab mMenuPageIndex = MoreMenuModel.Menu_Tab.FOOD;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private MenuActivity mActivity;
    private View rootView;
    private TextView mGoButton;
    private LinearLayout mFoodLayout;
    private GridView mFoodGridView;
    private LinearLayout mFoodAvailableTimeView;

    private LinearLayout mDrinkLayout;
    private GridView mDrinkGridView;
    private GridView mAlcoholGridView;

    public MoreMenuDrawerFragment() {
    }

    public static MoreMenuDrawerFragment newInstance(String param1, String param2) {
        MoreMenuDrawerFragment fragment = new MoreMenuDrawerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_more_menu_drawer, container, false);

        mActivity = (MenuActivity)getActivity();
        mGoButton = (TextView) rootView.findViewById(R.id.more_menu_go_button);
        mFoodLayout = (LinearLayout) rootView.findViewById(R.id.menu_food_layout);
        mFoodGridView = (GridView) rootView.findViewById(R.id.more_menu_gridview_food);
        mFoodAvailableTimeView = (LinearLayout) rootView.findViewById(R.id.more_available);

        mDrinkLayout = (LinearLayout) rootView.findViewById(R.id.menu_drink_layout);
        mDrinkGridView = (GridView) rootView.findViewById(R.id.more_menu_gridview_drink);
        mAlcoholGridView = (GridView) rootView.findViewById(R.id.more_menu_gridview_alcohol);

        return rootView;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, MoreMenuModel.Menu_Tab menupageIndex) {
        rootView = getActivity().findViewById(fragmentId);
        mMenuPageIndex = menupageIndex;
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

    public void loadMenuDrawerFoodData() {
        mFoodLayout.setVisibility(View.VISIBLE);
        mDrinkLayout.setVisibility(View.GONE);
        for (MenuCategory aCategory : MoreMenuModel.foodCategories) {
            aCategory.setChecked(false);
        }
        mFoodGridView.setAdapter(new MenuDrawerAdapter(mActivity, MoreMenuModel.foodCategories));
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                mListener.goButtonActionFoodCallback();
            }
        });
    }

    public void loadMenuDrawerDrinkData() {
        mFoodLayout.setVisibility(View.GONE);
        mDrinkLayout.setVisibility(View.VISIBLE);
        for (MenuCategory aCategory : MoreMenuModel.drinkCategories) {
            aCategory.setChecked(false);
        }
        mDrinkGridView.setAdapter(new MenuDrawerAdapter(mActivity, MoreMenuModel.drinkCategories));
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                mListener.goButtonActionDrinkCallback();
            }
        });
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(rootView);
    }

    public void openDrawer(){
        if (mDrawerLayout != null) {
            if (mActivity.menupageIndex == MoreMenuModel.Menu_Tab.FOOD) {
                loadMenuDrawerFoodData();
            }
            else {
                loadMenuDrawerDrinkData();
            }
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
        mListener.openMoreButtonStateCallback();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        mListener.closeMoreButtonStateCallback();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

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
        void openMoreButtonStateCallback();
        void closeMoreButtonStateCallback();
        void goButtonActionFoodCallback();
        void goButtonActionDrinkCallback();
    }
}
