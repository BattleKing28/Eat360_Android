package th.co.apps360.eat360.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

//import th.co.apps360.eat360.activity.RestaurantActivity;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.MenuActivity;

/**
 * Created by jakkrit.p on 20/08/2015.
 */

public class FoodCategoryAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<Integer,String>>  foodType = new ArrayList <>();
    private HashMap<Integer,String> mealIconList = new HashMap <>();
    private String typeAdapter;
    private int idSelected;


    public FoodCategoryAdapter(Context context,ArrayList<HashMap<Integer,String>>  catType,String typeAdapter,int idSelected) {
        mContext = context ;
        this.foodType = catType;
        this.typeAdapter = typeAdapter;
        this.idSelected = idSelected;

        if(typeAdapter.equals(MenuActivity.MEAL_TYPE)){

            for(int i=0;i< foodType.size();i++){

                HashMap<Integer,String> temp =  foodType.get(i);
                Iterator keys = temp.keySet().iterator();
                int id = Integer.parseInt(keys.next().toString());
                String mealName = temp.get(id);

                String iconName = "";
                if (mealName.equals("breakfast"))
                    iconName ="breakfast";
                else if(mealName.equals("maindish"))
                    iconName ="maindish";
                else if(mealName.equals("light meal"))
                    iconName ="lightmeal";
                else if(mealName.equals("lunch"))
                    iconName ="lunch";
                else if(mealName.equals("supper"))
                    iconName ="supper";
                else
                    iconName ="food";

                mealIconList.put(id,iconName);

            }

        }

    }

    public int getCount() {
        return foodType.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }




    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_category_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageButton = (ImageView) convertView.findViewById(R.id.category);
            viewHolder.itemContainer  = (LinearLayout) convertView.findViewById(R.id.category_container);

            convertView.setTag(viewHolder);
        } else {
            viewHolder  = (ViewHolder) convertView.getTag();
        }


        if(typeAdapter.equals(MenuActivity.MEAL_TYPE)){
            String nameIcon = mealIconList.get(position+1);
            int drawable = mContext.getResources().getIdentifier(nameIcon, "drawable", mContext.getPackageName()) ;
            viewHolder.imageButton.setImageResource(drawable);
        }else{

            HashMap<Integer,String> categoryData = foodType.get(position);
            Set key = categoryData.keySet();
            int keyName = Integer.parseInt(key.toArray()[0].toString());
            String url = new ConfigURL(mContext).defaultURL+"/"+categoryData.get(keyName) ;
            Utils.setImageWithPicasso(mContext, url, viewHolder.imageButton);

        }

        if(idSelected == position){
            viewHolder.itemContainer.setBackgroundColor(mContext.getResources().getColor(R.color.light_blue_transparent));
        }


        return convertView;
    }




    private class ViewHolder{
        ImageView imageButton;
        LinearLayout itemContainer;

    }


}