package th.co.apps360.eat360.Model;

/**
 * Created by dan on 11/24/16.
 */

public class CategoryModel {

    private Integer mId;
    private String mLang_code;
    private String mName;
    private String mIcon;
    private String mType;
    private Boolean mChecked = false;

    public CategoryModel(Integer id, String lang_code, String name, String icon, String type) {
        mId = id;
        mLang_code = lang_code;
        mName = name;
        mIcon = icon;
        mType = type;
    }

    public Integer getId() {
        return mId;
    }

    public String getLang_code() {
        return mLang_code;
    }

    public String getName() {
        return mName;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getType() {
        return mType;
    }

    public Boolean isChecked() {
        return mChecked;
    }

    public void  setChecked(Boolean checked) {
        mChecked = checked;
    }


}
