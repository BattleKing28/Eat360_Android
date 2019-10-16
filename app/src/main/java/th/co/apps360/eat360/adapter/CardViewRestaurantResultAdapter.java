package th.co.apps360.eat360.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
//import com.facebook.CallbackManager;
//import com.facebook.share.widget.ShareDialog;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import th.co.apps360.eat360.Model.RestaurantMultiDetailsModel;
import th.co.apps360.eat360.Model.RestaurantSearchResultModel;
import th.co.apps360.eat360.activity.MenuActivity;
//import th.co.apps360.eat360.activity.RestaurantActivity;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.MapActivity;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;


/**
 * Created by jakkrit.p on 7/07/2015.
 */

public class CardViewRestaurantResultAdapter extends RecyclerView.Adapter<CardViewRestaurantResultAdapter.ViewHolder> {

    public ArrayList<RestaurantSearchResultModel> details = new ArrayList<>();
    private Double userLatitude;
    private Double userLongitude;

    private String imageURL;
    public Context context;


    public CardViewRestaurantResultAdapter(Context context, ArrayList<RestaurantSearchResultModel> myDataset,
                                           Double userLatitude, Double userLongitude) {
        this.context = context;
        this.details = myDataset;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    private String getAddress(Integer index) {
        String address = details.get(index).getAddress();
        String country = details.get(index).getCountry();
        String state = details.get(index).getState();
        String city = details.get(index).getCity();
        String postcode = details.get(index).getPostcode();
        String fulladdress = String.format("%s\n%s %s %s\n%s",
                address, city, state, postcode, country);
        return fulladdress;
    }

//    private String convertTime(String openTime,String closeTime) {
//        // change  time to am:pm
//        String openType = "AM";
//        String closeType = "AM";
//        int openHourPM = Integer.parseInt(openTime.split(":")[0]);
//        if(openHourPM > 12 && openHourPM != 0){
//            openHourPM = openHourPM -12;
//            openType = "PM";
//        }
//        int openHinutePM = Integer.parseInt(openTime.split(":")[1]);
//        int closeHourPM = Integer.parseInt(closeTime.split(":")[0]);
//        if(closeHourPM > 12 && closeHourPM!= 0){
//            closeHourPM = closeHourPM -12;
//            closeType = "PM";
//        }
//        int closeHinutePM = Integer.parseInt(closeTime.split(":")[1]);
//        String timeAM = String.format("%02d",openHourPM)+":"+String.format("%02d",openHinutePM);
//        String timePM = String.format("%02d",closeHourPM)+":"+String.format("%02d",closeHinutePM);
//        return timeAM + openType+" - "+timePM+closeType;
//    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardViewRestaurantResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate( R.layout.cardview_restaurant_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        viewHolder.title.setSelected(true);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        try {
            viewHolder.title.setText(details.get(position).getName());
            viewHolder.title.setSelected(true);
            viewHolder.address.setText(getAddress(position));
            String distance = Utils.getDistance(details.get(position).getLatitude(),
                    details.get(position).getLongitude(),
                    userLatitude,
                    userLongitude) + " km";
            viewHolder.radius.setText(distance);
            String imageURL = details.get(position).getImagePath();
            Utils.setDebug("image path", imageURL);
            Utils.setImageWithPicasso(context, imageURL, viewHolder.image);
        }
        catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
            viewHolder.image.setImageResource(R.drawable.no_image_small);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return details.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public  class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        public TextView title;
        public ImageButton mapicon;
        public SelectableRoundedImageView image;
        public TextView radius;
        public TextView address;
        public TextView menu;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);

            title = (TextView) itemLayoutView.findViewById(R.id.res_card_title);
            mapicon = (ImageButton)itemLayoutView.findViewById(R.id.res_card_mapicon);
            image = (SelectableRoundedImageView)itemLayoutView.findViewById(R.id.res_card_image);
            radius = (TextView) itemLayoutView.findViewById(R.id.res_card_radius);
            address = (TextView) itemLayoutView.findViewById(R.id.res_card_address);
            menu = (TextView) itemLayoutView.findViewById(R.id.res_card_menu);

            image.setOnClickListener(this);
            mapicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intentMap = new Intent(context, MapActivity.class);
//                    intentMap.putExtra("restaurantId",details.get(getAdapterPosition()).getId());
//                    intentMap.putExtra("restaurantName",details.get(getAdapterPosition()).getName());
//                    intentMap.putExtra("restaurantType", details.get(getAdapterPosition()).getType());
//                    intentMap.putExtra("imagePath",details.get(getAdapterPosition()).getImagePath());
//                    intentMap.putExtra("latitude",details.get(getAdapterPosition()).getLatitude());
//                    intentMap.putExtra("longitude", details.get(getAdapterPosition()).getLongitude());
//                    context.startActivity(intentMap);
                }
            });
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // restaurant menu
//                    Intent resultIntent = new Intent(context,RestaurantActivity.class);
                    Intent resultIntent = new Intent(context,MenuActivity.class);
                    resultIntent.putExtra("target", "menu");
                    resultIntent.putExtra("restaurantId",details.get(getAdapterPosition()).getId());
                    resultIntent.putExtra("restaurantName",details.get(getAdapterPosition()).getName());
                    resultIntent.putExtra("restaurantImagePath", details.get(getAdapterPosition()).getImagePath());
                    resultIntent.putExtra("restaurantAddress", getAddress(getAdapterPosition()));
                    resultIntent.putExtra("userLatitude", userLatitude);
                    resultIntent.putExtra("userLongitude", userLongitude);
//                    resultIntent.putExtra("restaurantDistance", getDistance(details.get(getAdapterPosition()).getLatitude(),
//                            details.get(getAdapterPosition()).getLongitude(), userLatitude, userLongitude) + " km");
                    context.startActivity(resultIntent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            // restaurant info
            Intent resultIntent = new Intent(context,MenuActivity.class);
            resultIntent.putExtra("target", "info");
            resultIntent.putExtra("restaurantId",details.get(getAdapterPosition()).getId());
            resultIntent.putExtra("restaurantName",details.get(getAdapterPosition()).getName());
            resultIntent.putExtra("restaurantImagePath", details.get(getAdapterPosition()).getImagePath());
            resultIntent.putExtra("restaurantAddress", getAddress(getAdapterPosition()));
            resultIntent.putExtra("userLatitude", userLatitude);
            resultIntent.putExtra("userLongitude", userLongitude);
//            resultIntent.putExtra("restaurantDistance", getDistance(details.get(getAdapterPosition()).getLatitude(),
//                    details.get(getAdapterPosition()).getLongitude(), userLatitude, userLongitude) + " km");
            context.startActivity(resultIntent);
        }

    }

}