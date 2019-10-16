package th.co.apps360.eat360.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import th.co.apps360.eat360.APIs.SearchByQrAPI;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

public class QRActivity extends AppCompatActivity
        implements SearchByQrAPI.ResultCallback {

    private ImageView mCloseButton;
    private TextView mScanResults;
    private Button mScanButton;
    private String webUrl;
    private ArrayList<AsyncTask> allAsyncTask = new ArrayList<>();
    private AlertDialog alert;

    private Double userLatitude;
    private Double userLongitude;
    private boolean bSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        MoreDataModel.qrscancode = "";

        mCloseButton = (ImageView) findViewById(R.id.qr_close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mScanResults = (TextView) findViewById(R.id.qr_scan_result);
        mScanResults.setPaintFlags(mScanResults.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mScanResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rest_url = mScanResults.getText().toString();

                if (rest_url != null && !rest_url.isEmpty() && !rest_url.equals("null")) {
                    allAsyncTask.add(new SearchByQrAPI(QRActivity.this, rest_url).execute());
                }
            }
        });

        mScanButton = (Button) findViewById(R.id.qr_scan_button);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), QRScanActivity.class);
                startActivity(intent);
            }
        });

        receiveParams();
    }

    private void receiveParams() {
        userLatitude = this.getIntent().getDoubleExtra("Latitude", 0.0);
        userLongitude= this.getIntent().getDoubleExtra("Longitude", 0.0);
    }

    @Override
    public void onResume() {
        super.onResume();

        mScanResults.setText(MoreDataModel.qrscancode);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){  // cancel all asynctask before close activity
        for (AsyncTask asyncItem : allAsyncTask ){
            if(asyncItem != null && !asyncItem.isCancelled()){
                asyncItem.cancel(true);
            }
        }
        super.onDestroy();
    }

    @Override
    public void searchByQrResultCallback(String jsonStringResult) {
        getRestaurantDetail(jsonStringResult);
    }


    public void getRestaurantDetail(String jsonString){
        try {
            JSONObject objResult = new JSONObject(jsonString);
            JSONArray array = objResult.getJSONArray("Results");
            if(array.length() <= 0){
                showDialog(this, getString(R.string.No_Search_Result), getString(R.string.Please_Try_again));
                return;
            }
            JSONObject obj = array.getJSONObject(0);
            String name = obj.getString("name");
            String restaurantID = obj.getString("restaurant_id");
            Intent resultIntent = new Intent(this, MenuActivity.class);
            resultIntent.putExtra("target", "menu");
            resultIntent.putExtra("restaurantId",restaurantID);
            resultIntent.putExtra("restaurantName",name);
            resultIntent.putExtra("restaurantImagePath", "");
            resultIntent.putExtra("restaurantAddress", "");
            resultIntent.putExtra("userLatitude", userLatitude);
            resultIntent.putExtra("userLongitude", userLongitude);
            startActivity(resultIntent);
        } catch (JSONException e) {
            showDialog(this, getString(R.string.No_Search_Result), getString(R.string.Please_Try_again));
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void showDownloadingDialog(Context context, String title, String detail){
        try{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View languageLayout = inflater.inflate(R.layout.dialog_downloading_layout, null);
            TextView detailView = (TextView) languageLayout.findViewById(R.id.detail);
            detailView.setText(detail);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(languageLayout);
            builder.setTitle(title);
            builder.setCancelable(false);
            builder.setNegativeButton(context.getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for (AsyncTask asyncItem : allAsyncTask) {
                                if (asyncItem != null && !asyncItem.isCancelled()) {
                                    asyncItem.cancel(true);
                                }
                            }
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();
        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void showDialog(Context context,String title,String detail){
        try{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View languageLayout = inflater.inflate(R.layout.dialog_layout, null);
            TextView detailView = (TextView) languageLayout.findViewById(R.id.detail);
            detailView.setText(detail);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(languageLayout);
            builder.setTitle(title);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }catch(Exception e){
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }
}
