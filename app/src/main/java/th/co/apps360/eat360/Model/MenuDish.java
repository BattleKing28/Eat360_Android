package th.co.apps360.eat360.Model;

/**
 * Created by dan on 12/14/16.
 */

public class MenuDish {

    private String mName;
    private String mImageUrl;
    private String mPrice;

    private String mId;
    private String mEngName;
    private String mDesc;
    private String mSampleImageUrl;
    private String mLangCode;
    private String mCurrency;
    private String mFoodCurrency;

    private String mCategoryId;
    private String mCategoryDesc;
    private String mCategoryIconUrl;
    private String mCategoryType;
    private String mCategoryGroup;

    private String mCuisineId;
    private String mCuisineDesc;

    private String mRestaurantId;
    private String mRestaurantName;


    public MenuDish(String name, String imageUrl, String price,
                    String id, String engname, String desc, String sampleimageurl,
                    String langcode, String currency, String foodcurrency,
                    String categoryid, String categorydesc, String categoryiconurl,
                    String categorytype, String categorygroup, String cuisineid, String cuisinedesc,
                    String restaurantid, String restaurantname) {
        mName = name;
        mImageUrl = imageUrl;
        mPrice = price;

        mId = id;
        mEngName = engname;
        mDesc = desc;
        mSampleImageUrl = sampleimageurl;
        mLangCode = langcode;
        mCurrency = currency;
        mFoodCurrency = foodcurrency;
        mCategoryId = categoryid;
        mCategoryDesc = categorydesc;
        mCategoryIconUrl = categoryiconurl;
        mCategoryType = categorytype;
        mCategoryGroup = categorygroup;
        mCuisineId = cuisineid;
        mCuisineDesc = cuisinedesc;
        mRestaurantId = restaurantid;
        mRestaurantName = restaurantname;
    }

    public String getName() {
        return mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getId() {
        return mId;
    }

    public String getEngName() {
        return mEngName;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getSampleImageUrl() {
        return mSampleImageUrl;
    }

    public String getLangCode() {
        return mLangCode;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getFoodCurrency() {
        return mFoodCurrency;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public String getCategoryDesc() {
        return mCategoryDesc;
    }

    public String getCategoryIconUrl() {
        return mCategoryIconUrl;
    }

    public String getCategoryType() {
        return mCategoryType;
    }

    public String getCategoryGroup() {
        return mCategoryGroup;
    }

    public String getCuisineId() {
        return mCuisineId;
    }

    public String getCuisineDesc() {
        return mCuisineDesc;
    }

    public String getRestaurantId() {
        return mRestaurantId;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

}
