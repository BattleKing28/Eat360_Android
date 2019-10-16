package th.co.apps360.eat360.Model;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import th.co.apps360.eat360.R;

/**
 * Created by dan on 12/19/16.
 */

public class DetailParentItemViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private TextView mTitleTextView;
    private TextView mSubTitleTextView;
    private ImageView mArrowIconView;

    public DetailParentItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mTitleTextView = (TextView) itemView.findViewById(R.id.detail_info_title);
        mSubTitleTextView = (TextView) itemView.findViewById(R.id.detail_info_subtitle);
        mArrowIconView = (ImageView) itemView.findViewById(R.id.detail_info_arrowicon);

    }

    public void bind(@NonNull DetailParentItem parentItem) {
        mTitleTextView.setText(parentItem.getTitle());
        mSubTitleTextView.setText(parentItem.getSubTitle());
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                mArrowIconView.setRotation(ROTATED_POSITION);
                mSubTitleTextView.setVisibility(View.VISIBLE);
            } else {
                mArrowIconView.setRotation(INITIAL_POSITION);
                mSubTitleTextView.setVisibility(View.INVISIBLE);
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
                mArrowIconView.setImageResource(R.drawable.selector_arrow_down);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                mArrowIconView.setImageResource(R.drawable.selector_arrow_right);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mArrowIconView.startAnimation(rotateAnimation);
        }
    }

}
