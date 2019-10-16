package th.co.apps360.eat360.Model;

/**
 * Created by dan on 11/23/16.
 */

public class RSubCategory {

    private String mName;
    private boolean mIsIcon;
    private boolean mChecked = false;

    public RSubCategory(String name, boolean isIcon) {
        mName = name;
        mIsIcon = isIcon;
    }

    public String getName() {
        return mName;
    }

    public boolean isIcon() {
        return mIsIcon;
    }

    public Boolean isChecked() {
        return mChecked;
    }

    public void  setChecked(Boolean checked) {
        mChecked = checked;
    }

}
