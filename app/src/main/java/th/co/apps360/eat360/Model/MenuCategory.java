package th.co.apps360.eat360.Model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by dan on 12/14/16.
 */

public class MenuCategory implements Parent<MenuDish> {

    private String mTitle;
    private String mIconUrl;
    private List<MenuDish> mDishes;
    private String mId;
    private String mLangCode;
    private String mType;
    private Boolean mChecked = false;

    public MenuCategory(String title, String iconUrl,
                        String id, String langcode, String type, List<MenuDish> dishes) {
        mTitle = title;
        mIconUrl = iconUrl;
        mId = id;
        mLangCode = langcode;
        mType = type;
        mDishes = dishes;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public String getId() {
        return mId;
    }

    public String getLangCode() {
        return mLangCode;
    }

    public String getType() {
        return mType;
    }

    public MenuDish getDish(int position) {
        return mDishes.get(position);
    }

    public void setDishes(List<MenuDish> dishes) {
        mDishes = dishes;
    }

    public Boolean isChecked() {
        return mChecked;
    }

    public void  setChecked(Boolean checked) {
        mChecked = checked;
    }

    @Override
    public List<MenuDish> getChildList() {
        return mDishes;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }

}
