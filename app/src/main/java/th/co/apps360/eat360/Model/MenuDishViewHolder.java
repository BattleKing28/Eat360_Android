package th.co.apps360.eat360.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.joooonho.SelectableRoundedImageView;

import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 12/14/16.
 */

public class MenuDishViewHolder extends ChildViewHolder {

    private TextView mDishNameView;
    private SelectableRoundedImageView mDishImageView;
    private TextView mDishPriceView;

    public MenuDishViewHolder(@NonNull View itemView) {
        super(itemView);
        mDishNameView = (TextView) itemView.findViewById(R.id.rest_dish_name);
        mDishPriceView = (TextView) itemView.findViewById(R.id.rest_dish_price);
        mDishImageView = (SelectableRoundedImageView) itemView.findViewById(R.id.rest_dish_image);
    }

    public void bind(@NonNull MenuDish dish, Context context) {
        mDishNameView.setText(dish.getName());
        String sPrice = String.format("%s %s", dish.getPrice(), dish.getFoodCurrency().toUpperCase());
        mDishPriceView.setText(sPrice);
        Utils.setImageWithPicasso(context, dish.getImageUrl(), mDishImageView);
    }

}
