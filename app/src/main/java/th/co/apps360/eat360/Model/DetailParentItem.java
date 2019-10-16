package th.co.apps360.eat360.Model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by dan on 12/19/16.
 */

public class DetailParentItem implements Parent<DetailChildItem> {

    private String mTitle;
    private String mSubTitle;
    private List<DetailChildItem> mChildItems;

    public DetailParentItem(String title, String subtitle, List<DetailChildItem> childItems) {
        mTitle = title;
        mSubTitle = subtitle;
        mChildItems = childItems;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    @Override
    public List<DetailChildItem> getChildList() {
        return mChildItems;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

}
