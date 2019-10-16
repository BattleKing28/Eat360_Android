package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import th.co.apps360.eat360.Model.DetailChildItem;
import th.co.apps360.eat360.Model.DetailChildItemViewHolder;
import th.co.apps360.eat360.Model.DetailParentItem;
import th.co.apps360.eat360.Model.DetailParentItemViewHolder;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.Model.MoreMenuModel;
import th.co.apps360.eat360.Model.RestaurantClosedayModel;
import th.co.apps360.eat360.R;

/**
 * Created by dan on 12/19/16.
 */

public class DetailParentInfoAdapter extends ExpandableRecyclerAdapter<DetailParentItem, DetailChildItem,
        DetailParentItemViewHolder, DetailChildItemViewHolder> {

    private static final int PARENT_VEGETARIAN = 0;
    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_CONTACT = 2;
    private static final int CHILD_DESC = 3;
    private static final int CHILD_OPENTIME = 4;
    private static final int CHILD_FACILITIES = 5;
    private static final int CHILD_ADDITIONALINFO = 6;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    private LayoutInflater mInflater;
    private List<DetailParentItem> mParentItems;
    private Context mContext;

    /**
     * Primary constructor. Sets up {@link #mParentList} and {@link #mFlatItemList}.
     * <p>
     * Any changes to {@link #mParentList} should be made on the original instance, and notified via
     * {@link #notifyParentInserted(int)}
     * {@link #notifyParentRemoved(int)}
     * {@link #notifyParentChanged(int)}
     * {@link #notifyParentRangeInserted(int, int)}
     * {@link #notifyChildInserted(int, int)}
     * {@link #notifyChildRemoved(int, int)}
     * {@link #notifyChildChanged(int, int)}
     * methods and not the notify methods of RecyclerView.Adapter.
     *
     * @param parentList List of all parents to be displayed in the RecyclerView that this
     *                   adapter is linked to
     */
    public DetailParentInfoAdapter(Context context, @NonNull List<DetailParentItem> parentList) {
        super(parentList);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mParentItems = parentList;
    }

    @NonNull
    @Override
    public DetailParentItemViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View parentView = mInflater.inflate(R.layout.item_detail_parentinfo, parentViewGroup, false);
        return new DetailParentItemViewHolder(parentView);
    }

    @NonNull
    @Override
    public DetailChildItemViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {

        View childView = mInflater.inflate(R.layout.item_detail_childinfo, childViewGroup, false);
        return new DetailChildItemViewHolder(childView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull DetailParentItemViewHolder parentViewHolder, int parentPosition,
                                       @NonNull DetailParentItem parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull DetailChildItemViewHolder childViewHolder, int parentPosition,
                                      int childPosition, @NonNull DetailChildItem child) {
        LinearLayout contactView = (LinearLayout) childViewHolder.itemView.findViewById(R.id.detail_info_contact);
        LinearLayout descView = (LinearLayout) childViewHolder.itemView.findViewById(R.id.detail_info_desc);
        LinearLayout opentimesView = (LinearLayout) childViewHolder.itemView.findViewById(R.id.detail_info_opentimes);
        LinearLayout facilitiesView = (LinearLayout) childViewHolder.itemView.findViewById(R.id.detail_info_facilities);
        LinearLayout additionalinfoView = (LinearLayout) childViewHolder.itemView.findViewById(R.id.detail_info_additionalinfo);

        switch (getChildViewType(parentPosition, childPosition)) {
            case CHILD_CONTACT:
                contactView.setVisibility(View.VISIBLE);
                descView.setVisibility(View.GONE);
                opentimesView.setVisibility(View.GONE);
                facilitiesView.setVisibility(View.GONE);
                additionalinfoView.setVisibility(View.GONE);
                TextView addressBodyView = (TextView) childViewHolder.itemView.findViewById(R.id.detail_contact_address_body);
                String address = String.format("%s %s %s %s, %s", MoreMenuModel.restaurantInfo.getAddress(),
                        MoreMenuModel.restaurantInfo.getCity(), MoreMenuModel.restaurantInfo.getState(),
                        MoreMenuModel.restaurantInfo.getPostcode(), MoreMenuModel.restaurantInfo.getCountry());
                addressBodyView.setText(address);
                TextView emailView = (TextView) childViewHolder.itemView.findViewById(R.id.detail_contact_email);
                emailView.setText(MoreMenuModel.restaurantInfo.getEmail());
                TextView weburlView = (TextView) childViewHolder.itemView.findViewById(R.id.detail_contact_website);
                weburlView.setText(MoreMenuModel.restaurantInfo.getUrl());
                ImageView callButton = (ImageView) childViewHolder.itemView.findViewById(R.id.detail_contact_call);
                callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent dial = new Intent();
                        dial.setAction(Intent.ACTION_DIAL);
                        dial.setData(Uri.parse("tel:" + MoreMenuModel.restaurantInfo.getPhone()));
                        mContext.startActivity(dial);
                    }
                });
                break;

            case CHILD_DESC:
                contactView.setVisibility(View.GONE);
                descView.setVisibility(View.VISIBLE);
                opentimesView.setVisibility(View.GONE);
                facilitiesView.setVisibility(View.GONE);
                additionalinfoView.setVisibility(View.GONE);
                TextView descText = (TextView) childViewHolder.itemView.findViewById(R.id.item_detail_desc_text);
                descText.setText(MoreMenuModel.restaurantInfo.getDesc());
                break;

            case CHILD_OPENTIME:
                contactView.setVisibility(View.GONE);
                descView.setVisibility(View.GONE);
                opentimesView.setVisibility(View.VISIBLE);
                facilitiesView.setVisibility(View.GONE);
                additionalinfoView.setVisibility(View.GONE);
                ListView opentimesList = (ListView) childViewHolder.itemView.findViewById(R.id.detail_opentime_timelist);
                opentimesList.setAdapter(new DetailsOpentimeListAdapter(mContext, MoreMenuModel.restaurantInfo.getOpeninghours()));

                final ViewPager weekendsPager = (ViewPager) childViewHolder.itemView.findViewById(R.id.closeday_pager);
                ArrayList<RestaurantClosedayModel> weekends = MoreMenuModel.restaurantInfo.getWeekenddates();
                weekendsPager.setAdapter(new DetailsSlidingCloseDayAdapter(mContext, weekends));
                CirclePageIndicator indicator = (CirclePageIndicator) childViewHolder.itemView.findViewById(R.id.closeday_indicator);
                indicator.setViewPager(weekendsPager);
                final float density = mContext.getResources().getDisplayMetrics().density;
                indicator.setRadius(5 * density);
                NUM_PAGES = weekends.size();
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == NUM_PAGES) {
                            currentPage = 0;
                        }
                        weekendsPager.setCurrentItem(currentPage++, true);
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
                break;

            case CHILD_FACILITIES:
                contactView.setVisibility(View.GONE);
                descView.setVisibility(View.GONE);
                opentimesView.setVisibility(View.GONE);
                facilitiesView.setVisibility(View.VISIBLE);
                additionalinfoView.setVisibility(View.GONE);
                GridView facilitiesGrid = (GridView) childViewHolder.itemView.findViewById(R.id.item_detail_facility_gridview);
                facilitiesGrid.setAdapter(new DetailsFacilityGridAdapter(mContext, MoreMenuModel.restaurantInfo.getFacilities()));
                break;

            case CHILD_ADDITIONALINFO:
                contactView.setVisibility(View.GONE);
                descView.setVisibility(View.GONE);
                opentimesView.setVisibility(View.GONE);
                facilitiesView.setVisibility(View.GONE);
                additionalinfoView.setVisibility(View.VISIBLE);
                TextView additionalText = (TextView) childViewHolder.itemView.findViewById(R.id.item_detail_additionalinfo_text);
                String additionalData = String.format("paid to: %s", MoreMenuModel.restaurantInfo.getPaid_to());
                additionalText.setText(additionalData);
                break;

            default:
                contactView.setVisibility(View.GONE);
                descView.setVisibility(View.VISIBLE);
                opentimesView.setVisibility(View.GONE);
                facilitiesView.setVisibility(View.GONE);
                additionalinfoView.setVisibility(View.GONE);
                descText = (TextView) childViewHolder.itemView.findViewById(R.id.item_detail_desc_text);
                descText.setText(MoreMenuModel.restaurantInfo.getDesc());
        }
    }

    @Override
    public int getParentViewType(int parentPosition) {
        return PARENT_VEGETARIAN;
    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        switch (parentPosition) {
            case 0:
                return CHILD_CONTACT;
            case 1:
                return CHILD_DESC;
            case 2:
                return CHILD_OPENTIME;
            case 3:
                return CHILD_FACILITIES;
            case 4:
                return CHILD_ADDITIONALINFO;
            default:
                return CHILD_DESC;
        }
    }

    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT_VEGETARIAN || viewType == PARENT_NORMAL;
    }

}
