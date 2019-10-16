package th.co.apps360.eat360.fragment;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import th.co.apps360.eat360.Model.DetailChildItem;
import th.co.apps360.eat360.Model.DetailParentItem;
import th.co.apps360.eat360.Model.MoreMenuModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.MenuActivity;
import th.co.apps360.eat360.adapter.DetailParentInfoAdapter;
import th.co.apps360.eat360.adapter.DetailsSlidingImageAdapter;

public class FragmentRestaurantDetail extends Fragment {

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static Integer[] IMAGES= {R.drawable.rest_menu_splashimage};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();

    private MenuActivity mActivity;
    private ImageView mFacebookLinkButton;
    private ImageView mInstagramLinkButton;
    private ImageView mTwitterLinkButton;
    private ImageView mGoogleLinkButton;
    private ImageView mMapPinLinkButton;
    private TextView mRestaurantNameView;
    private TextView mDistanceView;
//    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mInfoRecyclerView;
    private TextView mNoResultView;
    private ImageView mBackButton;

    private String mRestaurantId;
    private String mRestaurantName;
    private String mRestaurantImagePath;
    private String mRestaurantAddress;
    private String mRestaurantDistance;
    private Double mUserLatitude;
    private Double mUserLongitude;


    public FragmentRestaurantDetail() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (View) inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        mActivity = (MenuActivity) getActivity();
        mFacebookLinkButton = (ImageView) rootView.findViewById(R.id.detail_social_facebook);
        mInstagramLinkButton = (ImageView) rootView.findViewById(R.id.detail_social_instagram);
        mTwitterLinkButton = (ImageView) rootView.findViewById(R.id.detail_social_twitter);
        mGoogleLinkButton = (ImageView) rootView.findViewById(R.id.detail_social_google);
        mMapPinLinkButton = (ImageView) rootView.findViewById(R.id.detail_rest_mappin);
        mRestaurantNameView = (TextView) rootView.findViewById(R.id.detail_rest_name);
        mDistanceView = (TextView) rootView.findViewById(R.id.detail_rest_distance);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_layout);
        mInfoRecyclerView = (RecyclerView) rootView.findViewById(R.id.detail_recycler_view);
        mNoResultView = (TextView) rootView.findViewById(R.id.no_result);

//        rootView.setFocusableInTouchMode(true);
//        rootView.requestFocus();

        initSplashView();
        receiveParams();
        initRecyclerView();
        setTitleActionbar();
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

//        rootView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    onBackPressed();
//                    return true;
//                }
//                return false;
//            }
//        });

    }

    private void initSplashView() {
        for(int i=0;i<IMAGES.length;i++)
            ImagesArray.add(IMAGES[i]);

        mPager = (ViewPager) rootView.findViewById(R.id.image_pager);
        mPager.setAdapter(new DetailsSlidingImageAdapter(getActivity(), ImagesArray));
        CirclePageIndicator indicator = (CirclePageIndicator)rootView.findViewById(R.id.image_indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        NUM_PAGES =IMAGES.length;

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });
    }

    public void receiveParams() {
        mRestaurantId = mActivity.getRestaurantId();
        mRestaurantName = mActivity.getRestaurantName();
        mRestaurantImagePath = mActivity.getRestaurantImagePath();
        mRestaurantAddress = mActivity.getRestaurantAddress();
        mUserLatitude = mActivity.getUserLatitude();
        mUserLongitude = mActivity.getUserLongitude();
        showRestaurantInfo();
//        mSwipeRefreshLayout.setColorSchemeResources(
//                R.color.refresh_progress_1,
//                R.color.refresh_progress_2,
//                R.color.refresh_progress_3);
//        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager  = new LinearLayoutManager(this.getActivity());
        mInfoRecyclerView.setLayoutManager(mLayoutManager);
        mInfoRecyclerView.setHasFixedSize(true);
    }

    public void setTitleActionbar() {
        TextView mTitleTextView;
        mTitleTextView = ((MenuActivity) getActivity()).showTitle();
        String sTitle = "";
        if (null != mRestaurantName && !"".equals(mRestaurantName)) {
            sTitle = mRestaurantName;
        }
        mTitleTextView.setText(sTitle);
    }

    private void onBackPressed() {
        int backStackEntryCount = mActivity.getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount <= 1) {
            mActivity.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void showRestaurantInfo() {
        if (null != MoreMenuModel.restaurantInfo) {
            mRestaurantName = MoreMenuModel.restaurantInfo.getName();
            mRestaurantNameView.setText(mRestaurantName);

            mRestaurantDistance = Utils.getDistance(MoreMenuModel.restaurantInfo.getLatitude(),
                    MoreMenuModel.restaurantInfo.getLongitude(),
                    mUserLatitude,
                    mUserLongitude) + " km";
            mDistanceView.setText(mRestaurantDistance);

            mRestaurantAddress = String.format("%s %s %s %s\n%s",
                    MoreMenuModel.restaurantInfo.getAddress(),
                    MoreMenuModel.restaurantInfo.getCity(),
                    MoreMenuModel.restaurantInfo.getState(),
                    MoreMenuModel.restaurantInfo.getPostcode(),
                    MoreMenuModel.restaurantInfo.getCountry());

            DetailChildItem childItem1 = new DetailChildItem();

            DetailParentItem parentItem1 = new DetailParentItem("Contact info", "", Arrays.asList(childItem1));
            DetailParentItem parentItem2 = new DetailParentItem("Description", "", Arrays.asList(childItem1));
            DetailParentItem parentItem3 = new DetailParentItem("Open time", "Break time", Arrays.asList(childItem1));
            DetailParentItem parentItem4 = new DetailParentItem("Facilities", "", Arrays.asList(childItem1));
            DetailParentItem parentItem5 = new DetailParentItem("Additional info", "", Arrays.asList(childItem1));
            final List<DetailParentItem> parents = Arrays.asList(parentItem1, parentItem2, parentItem3,
                    parentItem4, parentItem5);
            DetailParentInfoAdapter adapter = new DetailParentInfoAdapter(mActivity, parents);
            adapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                @UiThread
                @Override
                public void onParentExpanded(int parentPosition) {
                    DetailParentItem expandedRecipe = parents.get(parentPosition);

                    String toastMsg = getResources().getString(R.string.expanded, expandedRecipe.getTitle());
                    Toast.makeText(mActivity,
                            toastMsg,
                            Toast.LENGTH_SHORT)
                            .show();
                }

                @UiThread
                @Override
                public void onParentCollapsed(int parentPosition) {
                    DetailParentItem collapsedRecipe = parents.get(parentPosition);

                    String toastMsg = getResources().getString(R.string.collapsed, collapsedRecipe.getTitle());
                    Toast.makeText(mActivity,
                            toastMsg,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
            mInfoRecyclerView.setAdapter(adapter);
            mInfoRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        }
    }

    private void reshowSplashView() {
        ArrayList<String> images = new ArrayList<>();
        for(int i=0; i<MoreMenuModel.restaurantInfo.getImages().size(); i++)
            images.add(MoreMenuModel.restaurantInfo.getImages().get(i));

        mPager = (ViewPager) rootView.findViewById(R.id.image_pager);
//        mPager.setAdapter(new DetailsSlidingImageAdapter(getActivity(), images));
        CirclePageIndicator indicator = (CirclePageIndicator)rootView.findViewById(R.id.image_indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        NUM_PAGES =IMAGES.length;

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });
    }

    @Override
    public void onResume() {
        mActivity.currentFragment = this;
        super.onResume();
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
        void onFragmentInteraction(Uri uri);
    }
}
