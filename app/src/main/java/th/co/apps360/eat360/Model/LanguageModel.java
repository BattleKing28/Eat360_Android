package th.co.apps360.eat360.Model;

import android.support.annotation.NonNull;

/**
 * Created by dan on 11/24/16.
 */

public class LanguageModel implements Comparable<LanguageModel> {

    public String name;
    public int id;
    public String short_name;
    public String lang_code;
    public String icon;
    public boolean isSelected;

    public LanguageModel(String name, int id, String short_name, String lang_code, String icon) {
        this.name = name;
        this.id = id;
        this.short_name = short_name;
        this.lang_code = lang_code;
        this.icon = icon;
        this.isSelected = false;
    }

    public String toString() {
        return ("(" + name + ", " + id + ", " + short_name + ", " + lang_code + ", " + icon + ")");
    }

    @Override
    public int compareTo(@NonNull LanguageModel aModel) {
        return toString().compareTo(aModel.toString());
    }

}
