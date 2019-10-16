package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import th.co.apps360.eat360.Model.RestaurantTimeModel;
import th.co.apps360.eat360.R;

/**
 * Created by dan on 12/18/16.
 */

public class DetailsOpentimeListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<RestaurantTimeModel> mOpentimes;

    public DetailsOpentimeListAdapter(Context context, ArrayList<RestaurantTimeModel> opentimes) {
        mContext = context;
        mOpentimes = opentimes;
    }

    @Override
    public int getCount() {
        return mOpentimes.size();
    }

    @Override
    public Object getItem(int position) {
        return mOpentimes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_detail_opentime_item, null);
            viewHolder = new ViewHolder();
            viewHolder.activeIconView = (ImageView) convertView.findViewById(R.id.item_detail_opentime_isactive);
            viewHolder.weekdayTextView = (TextView) convertView.findViewById(R.id.item_detail_opentime_weekday);
            viewHolder.opentimeFromTextView = (TextView) convertView.findViewById(R.id.item_detail_opentime_from);
            viewHolder.opentimeTillTextView = (TextView) convertView.findViewById(R.id.item_detail_opentime_till);
            viewHolder.breaktimeFromTextView = (TextView) convertView.findViewById(R.id.item_detail_breaktime_from);
            viewHolder.breaktimeBetweenTextView = (TextView) convertView.findViewById(R.id.item_detail_breaktime_between);
            viewHolder.breaktimeTillTextView = (TextView) convertView.findViewById(R.id.item_detail_breaktime_till);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.id = position;
        viewHolder.weekdayTextView.setId(position);
        viewHolder.opentimeFromTextView.setId(position);
        viewHolder.opentimeTillTextView.setId(position);
        viewHolder.breaktimeFromTextView.setId(position);
        viewHolder.breaktimeBetweenTextView.setId(position);
        viewHolder.breaktimeTillTextView.setId(position);

        viewHolder.weekdayTextView.setText(mOpentimes.get(position).getWeekday());
        viewHolder.opentimeFromTextView.setText(mOpentimes.get(position).getTimeFrom());
        viewHolder.opentimeTillTextView.setText(mOpentimes.get(position).getTimeTill());
        viewHolder.breaktimeFromTextView.setText(mOpentimes.get(position).getBreakFrom());
        viewHolder.breaktimeTillTextView.setText(mOpentimes.get(position).getBreakTill());

        if (mOpentimes.get(position).getActive()) {
            viewHolder.activeIconView.setImageResource(R.drawable.opentime_active);
            viewHolder.weekdayTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
            viewHolder.opentimeFromTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
            viewHolder.opentimeTillTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
            viewHolder.breaktimeFromTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
            viewHolder.breaktimeTillTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
        } else {
            viewHolder.activeIconView.setImageResource(R.drawable.opentime_inactive);
            viewHolder.weekdayTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_icon));
            viewHolder.opentimeFromTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_icon));
            viewHolder.opentimeTillTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_icon));
            viewHolder.breaktimeFromTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_icon));
            viewHolder.breaktimeTillTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_icon));
        }

        return convertView;
    }

    private class ViewHolder {
        Integer id;
        ImageView activeIconView;
        TextView weekdayTextView;
        TextView opentimeFromTextView;
        TextView opentimeTillTextView;
        TextView breaktimeFromTextView;
        TextView breaktimeBetweenTextView;
        TextView breaktimeTillTextView;
    }

}
