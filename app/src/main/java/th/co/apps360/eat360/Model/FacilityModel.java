package th.co.apps360.eat360.Model;

/**
 * Created by dan on 11/24/16.
 */

public class FacilityModel {
    private Integer mId;
    private String mLang_code;
    private String mName;
    private String mIcon;
    private String mDesc;
    private Boolean mChecked = false;

    public FacilityModel(Integer id, String lang_code, String name, String icon, String desc) {
        mId = id;
        mLang_code = lang_code;
        mName = name;
        mIcon = icon;
        mDesc = desc;
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

    public String getDesc() {
        return mDesc;
    }

    public Boolean isChecked() {
        return mChecked;
    }

    public void  setChecked(Boolean checked) {
        mChecked = checked;
    }

}
