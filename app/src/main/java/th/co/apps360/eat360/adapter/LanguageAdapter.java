package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import th.co.apps360.eat360.Model.LanguageModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 2/16/17.
 */

public class LanguageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LanguageModel> mLanguages = new ArrayList<>();

    public LanguageAdapter(Context context, ArrayList<LanguageModel> languages) {
        mContext = context;
        mLanguages = languages;
    }

    @Override
    public int getCount() {
        return mLanguages.size();
    }

    @Override
    public Object getItem(int position) {
        return mLanguages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mLanguages.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_language, null);
            viewHolder = new ViewHolder();
            viewHolder.layoutView = (LinearLayout) convertView.findViewById(R.id.item_language_layout);
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.item_language_name);
            viewHolder.iconView = (ImageView) convertView.findViewById(R.id.item_language_icon);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder  = (ViewHolder) convertView.getTag();
        }

        viewHolder.id = position;
        viewHolder.layoutView.setId(position);
        viewHolder.nameView.setId(position);
        viewHolder.iconView.setId(position);

        if (mLanguages.get(position).isSelected) {
            viewHolder.layoutView.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
            viewHolder.nameView.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            viewHolder.layoutView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            viewHolder.nameView.setTextColor(mContext.getResources().getColor(R.color.grey));
        }
        viewHolder.nameView.setText(mLanguages.get(position).name);
        if (!"".equals(mLanguages.get(position).icon)) {
            Utils.setImageWithPicasso(mContext, mLanguages.get(position).icon, viewHolder.iconView);
        } else {
            viewHolder.iconView.setImageResource(R.drawable.lang_icon);
        }

        return convertView;
    }

    private class ViewHolder {
        LinearLayout layoutView;
        ImageView iconView;
        TextView nameView;
        Integer id;
    }

}
