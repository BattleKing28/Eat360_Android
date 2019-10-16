package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.Model.MoreRestaurantDataModel;
import th.co.apps360.eat360.Model.RCategory;
import th.co.apps360.eat360.Model.RCategoryViewHolder;
import th.co.apps360.eat360.Model.RSubCategory;
import th.co.apps360.eat360.Model.RSubMultiViewHolder;
import th.co.apps360.eat360.Model.RSubSingleViewHolder;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 11/28/16.
 */

public class RestaurantMultiAdapter extends ExpandableRecyclerAdapter<RCategory, RSubCategory,
        RCategoryViewHolder, RSubMultiViewHolder> {

    private static final int PARENT_VEGETARIAN = 0;
    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_VEGETARIAN = 2;
    private static final int CHILD_NORMAL = 3;

    private LayoutInflater mInflater;
    private List<RCategory> mRCategoryList;
    private Context mContext;

    public RestaurantMultiAdapter(Context context, @NonNull List<RCategory> parentList) {
        super(parentList);
        mContext = context;
        mRCategoryList = parentList;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RCategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View rCategoryView;
        rCategoryView = mInflater.inflate(R.layout.rcategory_view, parentViewGroup, false);
        return new RCategoryViewHolder(rCategoryView);
    }

    @NonNull
    @Override
    public RSubMultiViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View rSubCategoryView;
        rSubCategoryView = mInflater.inflate(R.layout.rsubcategory_multichecked_view, childViewGroup, false);
        return new RSubMultiViewHolder(rSubCategoryView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull RCategoryViewHolder parentViewHolder, int parentPosition, @NonNull RCategory parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull RSubMultiViewHolder childViewHolder, int parentPosition,
                                      final int childPosition, @NonNull final RSubCategory child) {
        childViewHolder.bind(child);
        childViewHolder.mRSubCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (MoreRestaurantDataModel.restaurantFacilitySelectionList.get(childPosition)) {
//                    MoreRestaurantDataModel.restaurantFacilitySelectionList.set(childPosition, false);
//                }
//                else {
//                    MoreRestaurantDataModel.restaurantFacilitySelectionList.set(childPosition, true);
//                }

                if (child.isChecked()) {
                    child.setChecked(false);
                    MoreRestaurantDataModel.restaurantFacilityList.get(childPosition).setChecked(false);
                    MoreRestaurantDataModel.restaurantFacilitySelectedCount --;
                } else {
                    if (MoreRestaurantDataModel.restaurantFacilitySelectedCount >= 5) {
                        Utils.showToast(mContext, "max is 5!");
                    } else {
                        child.setChecked(true);
                        MoreRestaurantDataModel.restaurantFacilityList.get(childPosition).setChecked(true);
                        MoreRestaurantDataModel.restaurantFacilitySelectedCount ++;
                    }
                }

                notifyDataSetChanged();
            }
        });
        if (MoreRestaurantDataModel.restaurantFacilityList.get(childPosition).isChecked()) {
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
