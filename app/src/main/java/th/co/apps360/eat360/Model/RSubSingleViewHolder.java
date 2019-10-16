package th.co.apps360.eat360.Model;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import th.co.apps360.eat360.R;

/**
 * Created by dan on 11/23/16.
 */

public class RSubSingleViewHolder extends ChildViewHolder {

    public TextView mRSubCategoryTextView;

    public RSubSingleViewHolder(@NonNull View itemView) {
        super(itemView);
        mRSubCategoryTextView = (TextView)itemView.findViewById(R.id.rsub_single_tv);
    }

    public void bind(@NonNull RSubCategory rSubCategory) {
        mRSubCategoryTextView.setText(rSubCategory.getName());
    }
}
