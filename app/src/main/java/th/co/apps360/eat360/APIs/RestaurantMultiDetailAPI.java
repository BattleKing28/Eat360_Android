package th.co.apps360.eat360.APIs;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
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
 * Created by lafong on 6/9/2558.
 */



public class RestaurantMultiDetailAPI extends AsyncTask<String, Void, String> {



    private Context mContext;
    private ResultCallback resultCallback;
    private String possibleKey;


    public interface ResultCallback {
        void multipleRestaurantDetailsResultCallback(String jsonStringResult);
    }

    public RestaurantMultiDetailAPI(Context context, String possibleKey){
        mContext = context;
        resultCallback = (ResultCallback) mContext;
        this.possibleKey = possibleKey;
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
            if (url != null && url.length > 0) {
                if(url[0] == null)
                    apiUrl = new ConfigURL(mContext).restaurantMultiDetailURL;
                else
                    apiUrl = url[0];
            } else
                apiUrl = new ConfigURL(mContext).restaurantMultiDetailURL;

            HttpPost httppost = new HttpPost(apiUrl);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            String searchLang = Utils.getCurrentLanguage(mContext);
            nameValuePairs.add(new BasicNameValuePair("LanguageID", searchLang));
            nameValuePairs.add(new BasicNameValuePair("RestaurantIDs", possibleKey));

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


        return stringResult.toString();
    }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected void onPostExecute(String jsonString) {
        try {
            resultCallback.multipleRestaurantDetailsResultCallback(jsonString);
            Utils.setDebug("json RestaurantMultiDetailAPI", jsonString);
        } catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

}