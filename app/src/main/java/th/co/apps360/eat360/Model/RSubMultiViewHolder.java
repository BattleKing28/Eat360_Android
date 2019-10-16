package th.co.apps360.eat360.Model;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import th.co.apps360.eat360.R;

/**
 * Created by dan on 11/28/16.
 */

public class RSubMultiViewHolder extends ChildViewHolder {

    public TextView mRSubCategoryTextView;

    public RSubMultiViewHolder(@NonNull View itemView) {
        super(itemView);
        mRSubCategoryTextView = (TextView)itemView.findViewById(R.id.rsub_multi_tv);
    }

    public void bind(@NonNull RSubCategory rSubCategory) {
        mRSubCategoryTextView.setText(rSubCategory.getName());
    }

}
