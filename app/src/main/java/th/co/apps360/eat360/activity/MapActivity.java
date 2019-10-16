package th.co.apps360.eat360.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;

import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by jakkrit.p on 24/07/2015.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private SupportMapFragment mapView;
    private String restaurantId;
    private String restaurantName;
    private String restaurantType;
    private String imagePath;
    private boolean checkFirstShowPopup;
    private boolean fromDetail;
    private GoogleMap map;
    private LatLng latLong;
    private boolean googleMapInstalled;
    private ImageButton directions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        directions = (ImageButton) findViewById(R.id.directions);
        initData();
        initMap();
        setMarker();
        initListener();

    }


    private void initListener() {
        directions.setOnClickListener(new View.OnClickListener() {  //use this if Google Map not installed or disabled
            @Override
            public void onClick(View view) {

                try {
                    String url = "https://www.google.com/maps/dir//" + latLong.latitude + "," + latLong.longitude + "/@" + latLong.latitude + "," + latLong.longitude + ",5z";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                } catch (Exception e) {
                    ;
                }
            }
        });
    }


    private void initData() {

        googleMapInstalled = Utils.isGoogleMapsInstalled(getApplication());
        restaurantId = getIntent().getStringExtra("restaurantId");
        restaurantName = getIntent().getStringExtra("restaurantName");
        restaurantType = getIntent().getStringExtra("restaurantType");
        imagePath = getIntent().getStringExtra("imagePath");
        Double latitude = getIntent().getDoubleExtra("latitude", 0);
        Double longitude = getIntent().getDoubleExtra("longitude", 0);
        latLong = new LatLng(latitude, longitude);
        fromDetail = getIntent().getBooleanExtra("fromDetail", false);
        checkFirstShowPopup = true;
    }


    private void initMap() {

        mapView = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapView.getMapAsync(this);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new MyInfoWindowAdapter());

        //LatLng defaultLocation = new LatLng(-18.341336,  135.750510);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5));
        map.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {

                    boolean doNotMoveCameraToCenterMarker = false;

                    public boolean onMarkerClick(final Marker marker) {

                        marker.showInfoWindow();
                        if (!googleMapInstalled) {
                            directions.setVisibility(View.VISIBLE);
                        }else{
                            directions.setVisibility(View.GONE);
                        }

                        return doNotMoveCameraToCenterMarker;
                    }


                });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (fromDetail) {
                    finish();
                } else {
                    Intent intent = new Intent(MapActivity.this, MenuActivity.class);
                    intent.putExtra("restaurantId", restaurantId);
                    intent.putExtra("restaurantName", restaurantName);
                    intent.putExtra("fromMap", true);
                    startActivity(intent);
                }


            }
        });

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (!Utils.checkGPSEnabled(MapActivity.this)) {
                    Utils.showEnableLocationDialog(MapActivity.this, getResources().getString(R.string.enable_location_title), getResources().getString(R.string.enable_location_detail));

                }

                return false;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                directions.setVisibility(View.GONE);
            }
        });

        getSupportActionBar().setTitle(restaurantName);

        if(!googleMapInstalled)
            map.getUiSettings().setMapToolbarEnabled(false);


    }


    private void setMarker(){

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)) ;
        map.addMarker(marker);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 10));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }


    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


        @Override
        public View getInfoContents(final Marker marker) {
            return null;
        }


        @Override
        public View getInfoWindow(Marker marker) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myContentsView  = inflater.inflate(R.layout.infowindow, null);

            ImageView restaurantImage = (ImageView)myContentsView.findViewById(R.id.image_restaurant);


            if(!checkFirstShowPopup)
                Utils.setImageWithPicasso(getApplication(),imagePath, restaurantImage);
            else
                Utils.setImageWithPicassoCallback(getApplication(), imagePath, restaurantImage,new  InfoWindowRefresher(marker));


            TextView restaurantNameView = (TextView)myContentsView.findViewById(R.id.restaurant_name);
            TextView restaurantTypeView = (TextView)myContentsView.findViewById(R.id.restaurant_type);

            restaurantNameView.setText(restaurantName);
            restaurantTypeView.setText(restaurantType);

            TextView gotoMenu  = (TextView) myContentsView.findViewById(R.id.goto_menu);
            gotoMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MapActivity.this, MenuActivity.class);
                    intent.putExtra("restaurantId", restaurantId);
                    intent.putExtra("restaurantName", restaurantName);
                    startActivity(intent);

                }
            });

            return myContentsView;

        }


        // For fix image not show at first time
        private class InfoWindowRefresher implements Callback {
            private Marker markerToRefresh;

            private InfoWindowRefresher(Marker markerToRefresh) {
                this.markerToRefresh = markerToRefresh;
            }

            @Override
            public void onSuccess() {
                checkFirstShowPopup = false;
                markerToRefresh.showInfoWindow();
            }

            @Override
            public void onError() {}
        }

    }




    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {

        mapView.onPause();
        super.onPause();
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}