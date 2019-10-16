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
import th.co.apps360.eat360.Model.MenuCategory;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 12/16/16.
 */

public class MenuDrawerAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MenuCategory> mCategories = new ArrayList<>();

    public MenuDrawerAdapter(Context context, ArrayList<MenuCategory> categories) {
        mContext = context;
        mCategories = categories;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Object getItem(int i) {
        return mCategories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_popup_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.imageLayout = (RelativeLayout) view.findViewById(R.id.category_layout);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.category_image);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.category_checkbox);
            view.setTag(viewHolder);
        }
        else {
            viewHolder  = (ViewHolder) view.getTag();
        }

        viewHolder.id = i;
        viewHolder.imageLayout.setId(i);
        viewHolder.imageView.setId(i);
        viewHolder.checkbox.setId(i);

        String categoryIconUrl = mCategories.get(i).getIconUrl();
        Utils.setImageWithPicasso(mContext, categoryIconUrl, viewHolder.imageView);
        viewHolder.checkbox.setChecked(mCategories.get(i).isChecked());

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv = (ImageView)view;
                int pos = iv.getId();
                if (viewHolder.checkbox.isChecked()) {
                    viewHolder.checkbox.setChecked(false);
                    viewHolder.checkbox.setVisibility(View.INVISIBLE);
                    viewHolder.imageView.getDrawable().setColorFilter(new LightingColorFilter(Color.WHITE, Color.BLUE));
                    viewHolder.imageLayout.setBackgroundColor(Color.WHITE);
                    mCategories.get(pos).setChecked(false);
                }
                else {
                    viewHolder.checkbox.setChecked(true);
                    viewHolder.checkbox.setVisibility(View.VISIBLE);
                    viewHolder.imageView.getDrawable().setColorFilter(new LightingColorFilter(Color.BLUE, Color.WHITE));
                    viewHolder.imageLayout.setBackgroundColor(0xff0189ca);
                    mCategories.get(pos).setChecked(true);
                }
            }
        });

        return view;
    }


    private class ViewHolder {
        RelativeLayout imageLayout;
        ImageView imageView;
        CheckBox checkbox;
        Integer id;
    }

}
