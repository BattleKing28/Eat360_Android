package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.FacilityModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 12/18/16.
 */

public class DetailsFacilityGridAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<FacilityModel> mFacilities;

    public DetailsFacilityGridAdapter(Context context, ArrayList<FacilityModel> facilities) {
        this.mContext = context;
        this.mFacilities = facilities;
    }

    @Override
    public int getCount() {
        return mFacilities.size();
    }

    @Override
    public Object getItem(int position) {
        return mFacilities.get(position);
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
            convertView = inflater.inflate(R.layout.item_detail_facility_for_gridview, null);
            viewHolder = new ViewHolder();
            viewHolder.iconView = (ImageView) convertView.findViewById(R.id.detail_facility_griditem_image);
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.detail_facility_griditem_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.id = position;
        viewHolder.iconView.setId(position);
        viewHolder.nameView.setId(position);

        viewHolder.nameView.setText(mFacilities.get(position).getName());
        String url = "";
        String selCategoryIcon = mFacilities.get(position).getIcon();
        if (selCategoryIcon.contains("http"))
            url = selCategoryIcon;
        else
            url = new ConfigURL(mContext).defaultURL + "/" + selCategoryIcon;
        Utils.setImageWithPicasso(mContext, url, viewHolder.iconView);
        return convertView;
    }

    private class ViewHolder {
        Integer id;
        ImageView iconView;
        TextView nameView;
    }

}
