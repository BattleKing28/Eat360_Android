package th.co.apps360.eat360.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.MapActivity;


/**
 * Created by jakkrit.p on 7/07/2015.
 */

public class CardViewFoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>   {


    private ArrayList<String> foodId   = new ArrayList <>();
    private ArrayList<String> price = new ArrayList <>();
    private ArrayList<String> foodName = new ArrayList <>();
    private ArrayList<String> foodImage = new ArrayList <>();
    private ArrayList<String> currency = new ArrayList <>();
    public Context context;
    private String imagePath;
    private String typeFoodId;
    private Callback callback;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private HashMap<String,String> headerData;
    private ArrayList<HashMap<String,String>> currencyList = new ArrayList <>();


    public CardViewFoodAdapter(Context context, ArrayList<String> listName, String jsonString, HashMap<String,String> headerData,String typeFoodId) {

        foodName = listName;
        this.typeFoodId =typeFoodId;

        if(jsonString != null)
            setArrayData(jsonString);
        this.context = context;
        this.headerData = headerData;
        callback = (Callback)context;
    }

    public interface Callback {
        void callbackFoodDetail(String foodId,String id);
        void callbackCloseFilter();
    }



    private void setArrayData(String jsonString){

        try {

            JSONArray foodArray = new JSONArray(jsonString);

            for(int i=0;i<foodArray.length();i++){
                JSONObject foodDetail = foodArray.getJSONObject(i);
                setDataToVariable(foodDetail);
            }


        } catch (JSONException e) {

            Utils.setDebug("crash", e.getLocalizedMessage());
        }

    }



    private void setDataToVariable(JSONObject foodDetail ){

        try {
            foodId.add(foodDetail.getString("id"));
            price.add(foodDetail.getString("price"))  ;
            foodName.add(foodDetail.getString("food_name")) ;
            currency.add(foodDetail.getString("currency").toUpperCase()) ;
            imagePath = foodDetail.getJSONObject("images").getString("img_path");

            JSONArray imgArray = foodDetail.getJSONObject("images").getJSONArray("images");

            for(int j=0 ;j < imgArray.length();j++ ){
                foodImage.add(imgArray.getString(j)) ;
                break; // for test 1 image
            }

            HashMap<String,String> currencies = new HashMap<>();
            JSONObject jObjectCurrency = foodDetail.getJSONObject("currencies");
            Iterator cur = jObjectCurrency.keys();
            String key;
            while(cur.hasNext()){
                key = cur.next().toString();
                currencies.put(key, jObjectCurrency.getString(key));
            }
            currencyList.add(currencies);

        } catch (JSONException e) {

            Utils.setDebug("crash", e.getLocalizedMessage());
        }

    }



    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if(viewType == TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.restaurant_header, parent, false);
            return  new VHHeader(view);
        }else if(viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.cardview_menu_row, parent, false);
            return new VHItem(view);
        }
        return null;

    }

    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder , int position) {

        if(holder instanceof VHHeader) {

            VHHeader VHheader = (VHHeader)holder;
            VHheader.address.setText(headerData.get("address"));
            VHheader.restaurantTypeView.setText(headerData.get("restaurantType"));
            VHheader.time.setText(headerData.get("time"));
            Utils.setImageWithPicasso(context, headerData.get("imageURL"), VHheader.restaurantImage);

            if(Utils.getUseFoodFilterTemp(context)){
                VHheader.tagSearch.setVisibility(View.VISIBLE);
                TextView  tagType = (TextView) VHheader.tagSearch.findViewById(R.id.tag_type);
                //String tagText = Utils.getTextFilter(context);
                String tagText = setTextFilter();
                tagType.setText(tagText);
                VHheader.closeTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.setUseFoodFilterTemp(context, false);
                        callback.callbackCloseFilter();
                    }
                });
            }else{
                VHheader.tagSearch.setVisibility(View.GONE);
            }


            if(foodId == null || foodId.size() <= 0)
                VHheader.noResult.setVisibility(View.VISIBLE);
            else
                VHheader.noResult.setVisibility(View.GONE);


        }else if(holder instanceof VHItem) {

            position = position-1; // delete 1 for header


            VHItem VHitem = (VHItem)holder;

            VHitem.foodName.setText(foodName.get(position));
            VHitem.foodName.setSelected(true);

            String imageURL = new ConfigURL(context).defaultURL+"/"+imagePath+foodImage.get(position) ;
            Utils.setImageWithPicasso(context, imageURL, VHitem.imageFood);

            String  currency= Utils.getCurrentCurrency(context);
            if(currencyList.size() > 0){
                HashMap<String,String> currencyListTemp = currencyList.get(position);
                String price = currencyListTemp.get(currency);
                currency = currency.toUpperCase();
                VHitem.price.setText(price+" "+currency);
            }

        }


    }


    private String setTextFilter(){

        String keywordText = context.getResources().getString(R.string.Keyword);
        String categoryText = context.getResources().getString(R.string.category);
        String radiusText = context.getResources().getString(R.string.Radius);
        String kmText  = context.getResources().getString(R.string.km);
        String locationText  = context.getResources().getString(R.string.Location);

        return keywordText+":\""+Utils.getKeyword(context)+"\" "+categoryText+":\""+Utils.getCategory(context)+"\" "+radiusText+":\""+Utils.getRadius(context)+kmText+"\" "+locationText+":\""+Utils.getLocation(context)+"\"";
    }




    @Override
    public int getItemCount() {

        if(foodImage == null)
            return 1;
        else
            return foodImage.size()+1; // plus 1 for header


    }




    public  class VHItem extends RecyclerView.ViewHolder  implements View.OnClickListener {

        public TextView price;
        public TextView foodName;
        public ImageView imageFood;

        public VHItem(View itemLayoutView) {

            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);

            price = (TextView) itemLayoutView.findViewById(R.id.price);
            foodName = (TextView) itemLayoutView.findViewById(R.id.food_name);
            imageFood = (ImageView)itemLayoutView.findViewById(R.id.image_food);

        }

        @Override
        public void onClick(View view) {
            Utils.setUseFoodListCache(context,true);
            callback.callbackFoodDetail(foodId.get(getAdapterPosition()-1),typeFoodId);
        }

    }


    class VHHeader extends RecyclerView.ViewHolder{
        private TextView address;
        private TextView restaurantTypeView;
        private TextView time;
        private ImageView restaurantImage;
        private ImageView location;
        private LinearLayout tagSearch;
        private ImageButton closeTag;
        private TextView noResult;

        public VHHeader(View itemView) {
            super(itemView);

            try{
                address = (TextView) itemView.findViewById(R.id.address);
                restaurantTypeView  = (TextView) itemView.findViewById(R.id.restaurant_type);
                time = (TextView) itemView.findViewById(R.id.time);
                restaurantImage = (ImageView) itemView.findViewById(R.id.image_restaurant);
                location = (ImageView) itemView.findViewById(R.id.marker);
                tagSearch = (LinearLayout)itemView.findViewById(R.id.tag_search);
                closeTag = (ImageButton) itemView.findViewById(R.id.close_tag);
                noResult = (TextView) itemView.findViewById(R.id.no_result);


                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity activity = (Activity) context;
                        boolean fromMap = activity.getIntent().getBooleanExtra("fromMap", false);
                        if (fromMap) {
                            activity.finish();
                        } else {
                            Intent intentMap = new Intent(activity, MapActivity.class);
                            intentMap.putExtra("restaurantId", headerData.get("restaurantId"));
                            intentMap.putExtra("restaurantName", headerData.get("restaurantName"));
                            intentMap.putExtra("restaurantType", headerData.get("restaurantType"));
                            intentMap.putExtra("imagePath", headerData.get("imageURL"));
                            intentMap.putExtra("latitude", Double.parseDouble(headerData.get("latitude")));
                            intentMap.putExtra("longitude", Double.parseDouble(headerData.get("longitude")));
                            intentMap.putExtra("fromDetail", true);
                            context.startActivity(intentMap);
                        }
                    }
                });

            }catch(Exception e){
                ;
            }
        }
    }

}