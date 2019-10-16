package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.CategoryModel;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by jakkrit.p on 20/08/2015.
 */

public class FoodMultiAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CategoryModel> mCategories = new ArrayList <>();

    public FoodMultiAdapter(Context context, ArrayList<CategoryModel> categories) {
        mContext = context;
        mCategories = categories;
    }

    public int getCount() {
        return mCategories.size();
    }

    public Object getItem(int position) {
        return mCategories.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_popup_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.imageLayout = (RelativeLayout) convertView.findViewById(R.id.category_layout);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.category_image);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.category_checkbox);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder  = (ViewHolder) convertView.getTag();
        }

        viewHolder.id = position;
        viewHolder.imageLayout.setId(position);
        viewHolder.imageView.setId(position);
        viewHolder.checkbox.setId(position);

        String selCategoryIconUrl = new ConfigURL(mContext).defaultURL + "/" + mCategories.get(position).getIcon();
        Utils.setImageWithPicasso(mContext, selCategoryIconUrl, viewHolder.imageView);
        boolean isChecked = mCategories.get(position).isChecked();
        if (isChecked) {
            viewHolder.checkbox.setChecked(true);
            viewHolder.checkbox.setVisibility(View.VISIBLE);
            viewHolder.imageView.getDrawable().setColorFilter(new LightingColorFilter(Color.BLUE, Color.WHITE));
            viewHolder.imageLayout.setBackgroundColor(0xff0189ca);

        } else {
            if (MoreFoodDataModel.foodCategorySelectedCount >= 5) {
            } else {
                viewHolder.checkbox.setChecked(false);
                viewHolder.checkbox.setVisibility(View.INVISIBLE);
                viewHolder.imageLayout.setBackgroundColor(Color.WHITE);
            }
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv = (ImageView) view;
                int pos = iv.getId();
                if (mCategories.get(pos).isChecked()) {
                    mCategories.get(pos).setChecked(false);
                    MoreFoodDataModel.foodCategorySelectedCount --;
                } else {
                    if (MoreFoodDataModel.foodCategorySelectedCount >= 5) {
                        Utils.showToast(mContext, "max is 5!");
                    } else {
                        mCategories.get(pos).setChecked(true);
                        MoreFoodDataModel.foodCategorySelectedCount ++;
                    }
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ImageView iv = (ImageView) view;
                int pos = iv.getId();
                try {
                    String selCategoryName = mCategories.get(pos).getName();
                    Utils.showToast(mContext, selCategoryName);
                } catch (Exception e) {
                    Utils.setDebug("crash", e.getLocalizedMessage());
                }
                return true;
            }
        });

        return convertView;
    }

    private class ViewHolder {
        RelativeLayout imageLayout;
        ImageView imageView;
        CheckBox checkbox;
        Integer id;
    }

}