package th.co.apps360.eat360.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;

import th.co.apps360.eat360.Model.AutocompleteResultModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by jakkrit.p on 11/08/2015.
 */
public class SuggestSearchAdapter extends ArrayAdapter  implements Filterable {

    private Context context;
    private ArrayList<String> dataList;
//    private ArrayList<String> keyList;
    private ArrayList<AutocompleteResultModel> resultList;
    private LocationFilter mFilter;
    private int layoutResourceId;
    private Callback moreCallback;

    public interface Callback{
        void moreSuggestCallback();
    }

    public SuggestSearchAdapter(Activity activity, int resource, ArrayList<AutocompleteResultModel> results) {
        super(activity, resource, results);
        this.resultList = results;
        this.context = activity;
        layoutResourceId = resource;
        moreCallback = (Callback)activity;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(position != resultList.size()){
            ViewHolder viewHolder = null;
            if(view == null || view.getTag() == null){
                viewHolder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(layoutResourceId, viewGroup, false);
                viewHolder.type = (TextView)view.findViewById(R.id.type_suggest);
                viewHolder.data = (TextView)view.findViewById(R.id.data_suggest);
                view.setTag(viewHolder);
            }else if(view.getTag() != null) {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.type.setText(resultList.get(position).getType());
            viewHolder.data.setText(resultList.get(position).getName());
        }else{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.footer_suggest_layout, viewGroup, false);
            Button moreButton = (Button) view.findViewById(R.id.more_button);
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moreCallback.moreSuggestCallback();
                }
            });
        }
        return view;
    }

    @Override
    public int getCount() {
        return resultList.size();  // add 1 for footer
    }

    @Override
    public String getItem(int i) {
        if( i < resultList.size())
            return resultList.get(i).getName();
        else
            return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder {
        TextView type;
        TextView data;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new LocationFilter();
        }
        return mFilter;
    }

    private class LocationFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();
            if (charSequence == null || charSequence.length() == 0) {
                notifyDataSetChanged();
            } else {

                ArrayList <String> temp = new ArrayList <>();
                for(int i=0 ; i<resultList.size() ;i++ ){

                    if(resultList.get(i).getName().contains(charSequence))
                        temp.add(resultList.get(i).getName());
                }
                results.values = temp;
                results.count = temp.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            if (filterResults.count == 0) {
                notifyDataSetInvalidated();
            } else {
                dataList = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        }
    }

}
