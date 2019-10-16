package th.co.apps360.eat360.Model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dan on 12/15/16.
 */

public class RestaurantTimeModel {

    private Boolean isActive;
    private String weekday;
    private String timeFrom;
    private String timeTill;
    private String breakFrom;
    private String breakTill;

    private ArrayList<String> WEEKDAYS = new ArrayList<String>(
            Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));

    public RestaurantTimeModel(Boolean isActive, Integer nWeekday, String timeFrom, String timeTill, String breakFrom, String breakTill) {
        this.isActive = isActive;
        this.weekday = WEEKDAYS.get(nWeekday);
        this.timeFrom = timeFrom;
        this.timeTill = timeTill;
        this.breakFrom = breakFrom;
        this.breakTill = breakTill;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getWeekday() {
        return weekday;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public String getTimeTill() {
        return timeTill;
    }

    public String getBreakFrom() {
        return breakFrom;
    }

    public String getBreakTill() {
        return breakTill;
    }

}
