package th.co.apps360.eat360.Model;

import android.support.annotation.NonNull;

/**
 * Created by dan on 11/13/16.
 */

public class CurrencyModel implements Comparable<CurrencyModel> {

    public String currency;
    public int id;
    public double rate;
    public int is_based;
    public boolean isSelected;

    public CurrencyModel(String currency, int id, double rate, int is_based) {
        this.currency = currency;
        this.id = id;
        this.rate = rate;
        this.is_based = is_based;
        this.isSelected = false;
    }

    public String toString() {
        return ("(" + currency + ", " + id + ", " + rate + ", " + is_based + ")");
    }

    @Override
    public int compareTo(@NonNull CurrencyModel aModel) {
        return toString().compareTo(aModel.toString());
    }

}
