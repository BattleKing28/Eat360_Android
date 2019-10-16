package th.co.apps360.eat360.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 12/14/16.
 */

public class MenuCategoryViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180.0f;

    @NonNull
    private ImageView mCategoryIconView;
    private TextView mCategoryTitleView;
    private ImageView mArrowImageView;

    public MenuCategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        mCategoryIconView = (ImageView) itemView.findViewById(R.id.rest_menu_category_icon);
        mCategoryTitleView = (TextView) itemView.findViewById(R.id.rest_menu_category_title);
        mArrowImageView = (ImageView) itemView.findViewById(R.id.rest_menu_arrow_icon);
    }

    public void bind(@NonNull MenuCategory category, Context context) {
        mCategoryTitleView.setText(category.getTitle());
        Utils.setImageWithPicasso(context, category.getIconUrl(), mCategoryIconView);
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                mArrowImageView.setRotation(ROTATED_POSITION);
            } else {
                mArrowImageView.setRotation(INITIAL_POSITION);
            }
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                mArrowImageView.setImageResource(R.drawable.selector_arrow_down);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                mArrowImageView.setImageResource(R.drawable.selector_arrow_right);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mArrowImageView.startAnimation(rotateAnimation);
        }
    }
}
