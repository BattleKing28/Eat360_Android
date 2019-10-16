package th.co.apps360.eat360.APIs;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.util.List;
import java.util.Locale;

import th.co.apps360.eat360.Utils;

/**
 * Created by lafong on 6/9/2558.
 */

public  class GetLocationNameFromLatLon extends AsyncTask<String, Void, String> {

    private  Context mContext;
    private Double latitude;
    private Double longitude;
    private ResultCallback resultCallback;

    public  interface ResultCallback{
        public void locationResultCallback(String jsonStringResult);
    }

    public  GetLocationNameFromLatLon(Context context , Double latitude, Double longitude){
        this.latitude = latitude ;
        this.longitude = longitude;
        mContext = context;
        resultCallback =  (ResultCallback) mContext;
    }


    @Override
    protected String doInBackground(String... strings) {
        String locationName = "";
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                String stateName = address.getAdminArea();
                String countryName = address.getCountryName();
                locationName =  cityName+" "+stateName+" "+countryName;
            }

        } catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

        return locationName;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        resultCallback.locationResultCallback(jsonString);
        Utils.setDebug("json GetLocationNameFromLatLon", jsonString);
    }
}
