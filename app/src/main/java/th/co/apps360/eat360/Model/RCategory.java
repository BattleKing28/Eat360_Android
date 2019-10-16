package th.co.apps360.eat360.Model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by dan on 11/23/16.
 */

public class RCategory implements Parent<RSubCategory> {

    private String mName;
    private List<RSubCategory> mRSubCategories;
    public RCategory(String name, List<RSubCategory> rSubCategories) {
        mName = name;
        mRSubCategories = rSubCategories;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List<RSubCategory> getChildList() {
        return mRSubCategories;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public RSubCategory getRSubCategory(int position) {
        return mRSubCategories.get(position);
    }

    public boolean hasImage() {
        for (RSubCategory rSubCategory : mRSubCategories) {
            if (!rSubCategory.isIcon()) {
                return false;
            }
        }
        return true;
    }
}
