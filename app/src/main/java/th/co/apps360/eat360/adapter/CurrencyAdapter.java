package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import th.co.apps360.eat360.Model.CurrencyModel;
import th.co.apps360.eat360.R;

/**
 * Created by dan on 2/16/17.
 */

public class CurrencyAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CurrencyModel> mCurrencies = new ArrayList<>();

    public CurrencyAdapter(Context context, ArrayList<CurrencyModel> currencies) {
        mContext = context;
        mCurrencies = currencies;
    }

    @Override
    public int getCount() {
        return mCurrencies.size();
    }

    @Override
    public Object getItem(int position) {
        return mCurrencies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mCurrencies.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_currency, null);
            viewHolder = new ViewHolder();
            viewHolder.layoutView = (LinearLayout) convertView.findViewById(R.id.item_currency_layout);
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.item_currency_name);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder  = (ViewHolder) convertView.getTag();
        }

        viewHolder.id = position;
        viewHolder.layoutView.setId(position);
        viewHolder.nameView.setId(position);

        if (mCurrencies.get(position).isSelected) {
            viewHolder.layoutView.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
            viewHolder.nameView.setTextColor(mContext.getResources().getColor(R.color.white));
        }
        else {
            viewHolder.layoutView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            viewHolder.nameView.setTextColor(mContext.getResources().getColor(R.color.grey));
        }
        viewHolder.nameView.setText(mCurrencies.get(position).currency.toUpperCase());

        return convertView;
    }

    private class ViewHolder {
        LinearLayout layoutView;
        TextView nameView;
        Integer id;
    }

}
