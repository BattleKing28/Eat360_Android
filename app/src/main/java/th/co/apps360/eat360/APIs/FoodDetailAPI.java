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


// get food detail
public class FoodDetailAPI extends AsyncTask<String, Void, String> {


    private Context mContext;
    private ResultCallback resultCallback;
    private String foodId;
    private String selLanguage = null;
    private String selCurrency = null;


    public  interface ResultCallback {
        void foodResultCallback(String jsonStringResult);
    }

    public FoodDetailAPI(Context context, String foodId){
        mContext = context;
        resultCallback =  (ResultCallback) mContext;
        this.foodId =foodId;
        this.selCurrency = Utils.getCurrentCurrency(mContext);
    }

    public FoodDetailAPI(Context context, String foodId, String language){
        mContext = context;
        resultCallback =  (ResultCallback) mContext;
        this.foodId =foodId;
        this.selLanguage = language;
        this.selCurrency = Utils.getCurrentCurrency(mContext);
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
                    apiUrl = new ConfigURL(mContext).foodDetailURL ;
                else
                    apiUrl = url[0];
            }else
                apiUrl = new ConfigURL(mContext).foodDetailURL;

            HttpPost httppost = new HttpPost(apiUrl);
            String language = "";
            if(selLanguage != null){
                language = selLanguage;
            }else{
                language = Utils.getCurrentLanguage(mContext);
            }

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("FoodID", foodId));
            nameValuePairs.add(new BasicNameValuePair("LanguageID", language));
            nameValuePairs.add(new BasicNameValuePair("FoodCurrency", selCurrency));
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

        resultCallback.foodResultCallback(jsonString);
        Utils.setDebug("json FoodDetailAPI",jsonString);
    }

}

