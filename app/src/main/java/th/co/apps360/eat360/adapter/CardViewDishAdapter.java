package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

import java.util.ArrayList;

import th.co.apps360.eat360.Model.MenuDish;
import th.co.apps360.eat360.Model.RestaurantDishModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 12/12/16.
 */

public class CardViewDishAdapter extends RecyclerView.Adapter<CardViewDishAdapter.ViewHolder> {

    private ArrayList<MenuDish> mDishes = new ArrayList<>();
//    private String mRestaurantName;
    private Context mContext;
//    public OnDishInfoActionListener mListener;

    public interface OnDishInfoActionListener {
        void passParamsToDishInfoCallback(Bundle bundle);
    }

    public CardViewDishAdapter(Context context, ArrayList<MenuDish> dishes) {
        this.mContext = context;
        this.mDishes = dishes;
//        this.mRestaurantName = restaurantName;
//        this.mListener = (OnDishInfoActionListener) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_menu_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            holder.dishNameTextView.setText(mDishes.get(position).getName());
            holder.dishPriceTextView.setText(mDishes.get(position).getPrice() + " "
                    + mDishes.get(position).getCurrency());
            holder.dishCategoryTextView.setText(mDishes.get(position).getCategoryDesc());
            String imageURL = mDishes.get(position).getImageUrl();
            Utils.setImageWithPicasso(mContext, imageURL, holder.dishImageView);
        } catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mDishes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private SelectableRoundedImageView dishImageView;
        private TextView dishNameTextView;
        private TextView dishPriceTextView;
        private TextView dishCategoryTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            dishImageView = (SelectableRoundedImageView) itemView.findViewById(R.id.rest_dish_image);
            dishNameTextView = (TextView) itemView.findViewById(R.id.rest_dish_name);
            dishPriceTextView = (TextView) itemView.findViewById(R.id.rest_dish_price);
//            dishCategoryTextView = (TextView) itemView.findViewById(R.id.rest_dish_category);
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("foodId",mDishes.get(getAdapterPosition()).getId());
            bundle.putString("restaurantId", mDishes.get(getAdapterPosition()).getRestaurantId());
            bundle.putString("restaurantName", mDishes.get(getAdapterPosition()).getRestaurantName());
            bundle.putString("foodName", mDishes.get(getAdapterPosition()).getName());
            bundle.putString("price", mDishes.get(getAdapterPosition()).getPrice());
            bundle.putString("foodImage", mDishes.get(getAdapterPosition()).getImageUrl());
//            mListener.passParamsToDishInfoCallback(bundle);
        }
    }
}
