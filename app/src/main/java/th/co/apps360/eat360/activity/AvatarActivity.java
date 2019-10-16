package th.co.apps360.eat360.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Service.AndroidMultiPartEntity;
import th.co.apps360.eat360.Utils;

public class AvatarActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    private ImageView avatarImageView;
    private ImageView cameraButton;
    private ImageView galleryButton;
    private ImageView backButton;
    private ImageView checkButton;
    private ProgressBar progressBar;
    private TextView progressLabel;

    private String mCurrentPhotoPath = "";
    private Bitmap mImageBitmap;
    long totalSize = 0;

    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        if (savedInstanceState != null) {
            String sFileUri = savedInstanceState.getString("file-uri");
            if (!sFileUri.equals("")) fileUri = Uri.parse(sFileUri);
        }

        getSupportActionBar().hide();
        avatarImageView = (ImageView) findViewById(R.id.avatar_prfile_image);
        cameraButton = (ImageView) findViewById(R.id.avatar_camera_button);
        galleryButton = (ImageView) findViewById(R.id.avatar_gallery_button);
        backButton = (ImageView) findViewById(R.id.avatar_back);
        checkButton = (ImageView) findViewById(R.id.avatar_check);
        progressBar = (ProgressBar) findViewById(R.id.avatar_progress_bar);
        progressLabel = (TextView) findViewById(R.id.avatar_progress_label);

        Picasso.with(this).load(MoreDataModel.userAvatarUrl)
                .placeholder(R.drawable.profile_image)
                .error(R.drawable.profile_image)
                .into(avatarImageView);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraButton.setBackgroundColor(Color.parseColor("#0189ca"));
                galleryButton.setBackgroundColor(Color.TRANSPARENT);
                Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                File filePhoto = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
                fileUri = Uri.fromFile(filePhoto);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intentCamera.putExtra("return-data", false);
                startActivityForResult(intentCamera, CAMERA_REQUEST);
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraButton.setBackgroundColor(Color.TRANSPARENT);
                galleryButton.setBackgroundColor(Color.parseColor("#0189ca"));

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);               // file uri to save the crop image
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadFileToServer().execute();
            }
        });

        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras.containsKey("data")) {
                        mImageBitmap = (Bitmap) extras.get("data");
                    }
                    else {
                        mImageBitmap = getBitmapFromUri();
                    }
                }
                else {
                    mImageBitmap = getBitmapFromUri();
                }
                // imageView.setImageBitmap(bitmap);
                avatarImageView.setImageURI(fileUri);
            } else if (requestCode == GALLERY_REQUEST) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                mCurrentPhotoPath = c.getString(columnIndex);
                c.close();
                mImageBitmap = (BitmapFactory.decodeFile(mCurrentPhotoPath));
                Utils.setDebug("imagepath from gallery", mCurrentPhotoPath);
                avatarImageView.setImageBitmap(mImageBitmap);
            }
        }
    }

    public Bitmap getBitmapFromUri() {
        getContentResolver().notifyChange(fileUri, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;

        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, fileUri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable("file_uri", fileUri);
        if (fileUri == null) {
            outState.putString("file-uri", "");
        } else {
            outState.putString("file-uri", fileUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        fileUri = savedInstanceState.getParcelable("file-uri");
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            progressLabel.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

            HttpPost httppost = new HttpPost(new ConfigURL().uploadAvatarURL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(mCurrentPhotoPath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                String token = Utils.loadToken(AvatarActivity.this);
                entity.addPart("Token",
                        new StringBody(token));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpClient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e1) {
                responseString = e1.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Utils.setDebug("avatar upload", "Response from server: " + result);
//            Utils.showDialog(AvatarActivity.this, "avatar upload", result);
            finish();
            super.onPostExecute(result);
        }

    }

}