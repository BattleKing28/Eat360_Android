package th.co.apps360.eat360.GCM;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import th.co.apps360.backend.registration.Registration;
import th.co.apps360.eat360.Utils;

/**
 * Created by jakkrit.p on 17/09/2015.
 */


public class  GcmRegistration extends AsyncTask<Context, Void, String> {

    private GoogleCloudMessaging mGcm;
    private Context mContext;
    private Registration mRegister;

//    private static final String SENDER_ID = "244438615534";
    private static final String SENDER_ID = "766692983724";

    public GcmRegistration() {
        Registration.Builder builder =
                new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://menu360-994.appspot.com/_ah/api/") ;

        mRegister = builder.build();
    }

    @Override
    protected String doInBackground(Context... params) {
        mContext = params[0];

        String msg = "";
        try {
            if (mGcm == null) {
                mGcm = GoogleCloudMessaging.getInstance(mContext);
            }
            String regId = mGcm.register(SENDER_ID);
            msg = "Registration ID : " + regId;

            mRegister.register(regId).execute();

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        //Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }
}