package th.co.apps360.eat360.Model;

/**
 * Created by dan on 12/9/16.
 */

public class AutocompleteResultModel {

    private Integer mId;
    private String mName;
    private String mType;

    public AutocompleteResultModel(Integer id, String name, String type) {
        mId = id;
        mName = name;
        mType = type;
    }

    public Integer getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

}
