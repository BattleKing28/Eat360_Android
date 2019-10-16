package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

import java.util.ArrayList;

import th.co.apps360.eat360.Model.FoodSearchResultModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.MenuActivity;


/**
 * Created by jakkrit.p on 7/07/2015.
 */

public class CardViewFoodResultAdapter extends RecyclerView.Adapter<CardViewFoodResultAdapter.ViewHolder>   {

    private Context mContext;
    private ArrayList<FoodSearchResultModel> foodList = new ArrayList<>();
    OnFoodInfoActionListener mListener;
    private Double userLatitude;
    private Double userLongitude;

    public interface OnFoodInfoActionListener {
        void passParamsToFoodInfoCallback(Bundle bundle);
    }

    public CardViewFoodResultAdapter(Context context, ArrayList<FoodSearchResultModel> foods,
                                     Double userLatitude, Double userLongitude) {
        this.mContext = context;
        this.foodList = foods;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.mListener = (OnFoodInfoActionListener)context;
    }

    @Override
    public CardViewFoodResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate( R.layout.cardview_food_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        try {
            viewHolder.restaurantNameTextView.setText(foodList.get(position).getRestaurant_name());
            viewHolder.foodNameTextView.setText(foodList.get(position).getFood_name());
            viewHolder.priceTextView.setText(foodList.get(position).getPrice() + " " + foodList.get(position).getCurrency().toUpperCase());

            String imageURL = foodList.get(position).getImg_path();
            Utils.setImageWithPicasso(mContext, imageURL, viewHolder.foodImageView);
        } catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView restaurantNameTextView;
        public TextView foodNameTextView;
        public TextView priceTextView;
        public SelectableRoundedImageView foodImageView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);

            restaurantNameTextView = (TextView) itemLayoutView.findViewById(R.id.restaurant);
            restaurantNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent resultIntent = new Intent(mContext, MenuActivity.class);
                    resultIntent.putExtra("target", "menu");
                    resultIntent.putExtra("restaurantId", foodList.get(getAdapterPosition()).getRestaurant_id());
                    resultIntent.putExtra("restaurantName",foodList.get(getAdapterPosition()).getRestaurant_name());
                    resultIntent.putExtra("restaurantImagePath", "");
                    resultIntent.putExtra("restaurantAddress", "");
//                    resultIntent.putExtra("restaurantDistance", "");
                    resultIntent.putExtra("userLatitude", userLatitude);
                    resultIntent.putExtra("userLongitude", userLongitude);
                    mContext.startActivity(resultIntent);
                }
            });

            foodNameTextView = (TextView) itemLayoutView.findViewById(R.id.food_name);
            priceTextView = (TextView) itemLayoutView.findViewById(R.id.price);
            foodImageView = (SelectableRoundedImageView)itemLayoutView.findViewById(R.id.image_food);
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("foodId",foodList.get(getAdapterPosition()).getFood_id());
            bundle.putString("restaurantId", foodList.get(getAdapterPosition()).getRestaurant_id());
            bundle.putString("restaurantName", foodList.get(getAdapterPosition()).getRestaurant_name());
            bundle.putString("foodName", foodList.get(getAdapterPosition()).getFood_name());
            bundle.putString("price", foodList.get(getAdapterPosition()).getPrice());
            bundle.putString("foodImage", foodList.get(getAdapterPosition()).getImg_path());
            bundle.putString("sourceActivity", "MainActivity");
            mListener.passParamsToFoodInfoCallback(bundle);
        }

    }

}