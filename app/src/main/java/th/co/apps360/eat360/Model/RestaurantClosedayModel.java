package th.co.apps360.eat360.Model;

/**
 * Created by dan on 12/18/16.
 */

public class RestaurantClosedayModel {

    private String closeday_date;
    private String closeday_month;
    private String closeday_title;

    public RestaurantClosedayModel(String date, String month, String title) {
        this.closeday_date = date;
        this.closeday_month = month;
        this.closeday_title = title;
    }

    public String getCloseday_date() {
        return closeday_date;
    }

    public String getCloseday_month() {
        return closeday_month;
    }

    public String getCloseday_title() {
        return closeday_title;
    }

}
