package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.Model.RCategory;
import th.co.apps360.eat360.Model.RCategoryViewHolder;
import th.co.apps360.eat360.Model.RSubCategory;
import th.co.apps360.eat360.Model.RSubSingleViewHolder;
import th.co.apps360.eat360.R;

/**
 * Created by dan on 11/23/16.
 */

public class RestaurantSingleAdapter extends ExpandableRecyclerAdapter<RCategory, RSubCategory,
        RCategoryViewHolder, RSubSingleViewHolder> {

    private static final int PARENT_VEGETARIAN = 0;
    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_VEGETARIAN = 2;
    private static final int CHILD_NORMAL = 3;

    private LayoutInflater mInflater;
    private List<RCategory> mRCategoryList;
    private Context mContext;
    public int selected_item = 0;


    public RestaurantSingleAdapter(Context context, @NonNull List<RCategory> rCategoryList) {
        super(rCategoryList);
        mContext = context;
        mRCategoryList = rCategoryList;
        mInflater = LayoutInflater.from(context);
    }

    @UiThread
    @NonNull
    @Override
    public RCategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View rCategoryView;
        rCategoryView = mInflater.inflate(R.layout.rcategory_view, parentViewGroup, false);
        return new RCategoryViewHolder(rCategoryView);
    }

    @UiThread
    @NonNull
    @Override
    public RSubSingleViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View rSubCategoryView;
        rSubCategoryView = mInflater.inflate(R.layout.rsubcategory_singlechecked_view, childViewGroup, false);
        return new RSubSingleViewHolder(rSubCategoryView);
    }

    @UiThread
    @Override
    public void onBindParentViewHolder(@NonNull RCategoryViewHolder parentViewHolder, int parentPosition, @NonNull RCategory parent) {
        parentViewHolder.bind(parent);
    }

    @UiThread
    @Override
    public void onBindChildViewHolder(@NonNull RSubSingleViewHolder childViewHolder, final int parentPosition, final int childPosition, @NonNull RSubCategory child) {
        childViewHolder.bind(child);
        childViewHolder.mRSubCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_item = childPosition;
                MoreRestaurantDataModel.restaurantSelCuisine = MoreRestaurantDataModel.restaurantCuisineList.get(childPosition);
                notifyDataSetChanged();
            }
        });

        if(childPosition == selected_item) {
            childViewHolder.mRSubCategoryTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            childViewHolder.mRSubCategoryTextView.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
        } else {
            childViewHolder.mRSubCategoryTextView.setTextColor(mContext.getResources().getColor(R.color.black_transparent));
            childViewHolder.mRSubCategoryTextView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getParentViewType(int parentPosition) {
        if (mRCategoryList.get(parentPosition).hasImage()) {
            return PARENT_VEGETARIAN;
        } else {
            return PARENT_NORMAL;
        }
    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        RSubCategory rSubCategory = mRCategoryList.get(parentPosition).getRSubCategory(childPosition);
        if (rSubCategory.isIcon()) {
            return CHILD_VEGETARIAN;
        } else {
            return CHILD_NORMAL;
        }
    }

    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT_VEGETARIAN || viewType == PARENT_NORMAL;
    }


}
