package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import th.co.apps360.eat360.Model.MenuCategory;
import th.co.apps360.eat360.Model.MenuCategoryViewHolder;
import th.co.apps360.eat360.Model.MenuDish;
import th.co.apps360.eat360.Model.MenuDishViewHolder;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.activity.MenuActivity;

/**
 * Created by dan on 12/12/16.
 */

public class MenuCategoryAdapter extends ExpandableRecyclerAdapter<MenuCategory, MenuDish,
        MenuCategoryViewHolder, MenuDishViewHolder> {

    private static final int PARENT_VEGETARIAN = 0;
    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_VEGETARIAN = 2;
    private static final int CHILD_NORMAL = 3;

    private LayoutInflater mInflater;
    private List<MenuCategory> mMenuCategories;
    private Context mContext;

    public OnFoodInfoActionListener mListener;

    public interface OnFoodInfoActionListener {
        void passParamsToFoodInfoCallback(Bundle bundle);
    }

    public MenuCategoryAdapter(Context context, @NonNull List<MenuCategory> parentList) {
        super(parentList);
        mContext = context;
        mListener = (OnFoodInfoActionListener) context;
        mMenuCategories = parentList;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MenuCategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View categoryView = mInflater.inflate(R.layout.item_menu_category, parentViewGroup, false);
        return new MenuCategoryViewHolder(categoryView);
    }

    @NonNull
    @Override
    public MenuDishViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View dishView = mInflater.inflate(R.layout.cardview_menu_row, childViewGroup, false);
        return new MenuDishViewHolder(dishView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull MenuCategoryViewHolder parentViewHolder,
                                       int parentPosition, @NonNull MenuCategory parent) {
        parentViewHolder.bind(parent, mContext);
    }

    @Override
    public void onBindChildViewHolder(@NonNull MenuDishViewHolder childViewHolder,
                                      final int parentPosition, final int childPosition, @NonNull final MenuDish child) {
        childViewHolder.bind(child, mContext);

        childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("foodId",child.getId());
                bundle.putString("restaurantId", child.getRestaurantId());
                bundle.putString("restaurantName", child.getRestaurantName());
                bundle.putString("foodName", child.getName());
                bundle.putString("price", child.getPrice());
                bundle.putString("foodImage", child.getImageUrl());
                bundle.putString("sourceActivity", "MenuActivity");
                mListener.passParamsToFoodInfoCallback(bundle);
            }
        });
    }

    @Override
    public int getParentViewType(int parentPosition) {
        return PARENT_VEGETARIAN;
    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        return CHILD_VEGETARIAN;
    }

    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT_VEGETARIAN || viewType == PARENT_NORMAL;
    }

}
