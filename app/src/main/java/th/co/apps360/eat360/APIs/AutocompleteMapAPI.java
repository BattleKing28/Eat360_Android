package th.co.apps360.eat360.APIs;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 2/21/17.
 */

public class AutocompleteMapAPI extends AsyncTask<String, Void, String> {

    private String searchText;
    private String latitude;
    private String longitude;
    private String radius;
    private String facilityIDs;
    private String cuisineIDs;

    private Context mContext;
    private ResultCallback resultCallback;


    public  interface ResultCallback{
        void autocompleteResultCallback(String jsonStringResult);
    }

    public AutocompleteMapAPI(Context context, String searchText, String latitude, String longitude, String radius, String facilityIDs, String cuisineIDs){
        mContext = context;
        resultCallback =  (ResultCallback) mContext;

        this.searchText = searchText;
        this.latitude  =latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.facilityIDs = facilityIDs;
        this.cuisineIDs = cuisineIDs;
    }

    @Override
    protected String doInBackground(String... url) {

        StringBuilder stringResult = new StringBuilder();

        try {
            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

            String apiUrl = "";
            if(url != null && url.length>0){
                if(url[0] == null)
                    apiUrl = new ConfigURL(mContext).autocompleteSearch_restaurant;
                else
                    apiUrl = url[0];
            } else
                apiUrl = new ConfigURL(mContext).autocompleteSearch_restaurant;

            HttpPost httppost = new HttpPost(apiUrl);

            String language = Utils.getCurrentLanguage(mContext);
            //language = "en"; // fix en for test
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("SearchText", searchText));
            nameValuePairs.add(new BasicNameValuePair("SearchLang", language));
            nameValuePairs.add(new BasicNameValuePair("Latitude", latitude));
            nameValuePairs.add(new BasicNameValuePair("Longitude", longitude));
            nameValuePairs.add(new BasicNameValuePair("Radius", radius));
            nameValuePairs.add(new BasicNameValuePair("CategoryIDs", ""));
            nameValuePairs.add(new BasicNameValuePair("MealIDs", ""));
            nameValuePairs.add(new BasicNameValuePair("CuisineIDs", cuisineIDs));
            nameValuePairs.add(new BasicNameValuePair("OnlyIDs", "true"));
            nameValuePairs.add(new BasicNameValuePair("PageID", "1"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httppost);
            BufferedReader buffer = new BufferedReader(new InputStreamReader( response.getEntity().getContent()));

            String line;
            while ((line = buffer.readLine()) != null) {
                stringResult.append(line);
            }
        } catch (ConnectTimeoutException e) {
            stringResult.append(Utils.TIMEOUT_ERROR);
        } catch (IOException e) {
            stringResult.append(Utils.CONNECTION_ERROR);
        }
        return  stringResult.toString();
    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(String jsonString) {
        resultCallback.autocompleteResultCallback(jsonString);
        Utils.setDebug("json AutocompleteMapAPI", jsonString);
    }

}
