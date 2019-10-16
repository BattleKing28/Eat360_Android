package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import th.co.apps360.eat360.Model.RestaurantClosedayModel;
import th.co.apps360.eat360.R;

/**
 * Created by dan on 12/18/16.
 */

public class DetailsSlidingCloseDayAdapter extends PagerAdapter {

    private ArrayList<RestaurantClosedayModel> mClosedays;
    private LayoutInflater inflater;
    private Context mContext;


    public DetailsSlidingCloseDayAdapter(Context context, ArrayList<RestaurantClosedayModel> closedays) {
        this.mContext = context;
        this.mClosedays = closedays;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mClosedays.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View closedayLayout = inflater.inflate(R.layout.sliding_closedays_layout, view, false);

        assert closedayLayout != null;
        final TextView closeday_date = (TextView) closedayLayout.findViewById(R.id.closeday_date);
        final TextView closeday_month = (TextView) closedayLayout.findViewById(R.id.closeday_month);
        final TextView closeday_title = (TextView) closedayLayout.findViewById(R.id.closeday_title);


        closeday_date.setText(mClosedays.get(position).getCloseday_date());
        closeday_month.setText(mClosedays.get(position).getCloseday_month());
        closeday_title.setText(mClosedays.get(position).getCloseday_title());

        view.addView(closedayLayout, 0);

        return closedayLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
