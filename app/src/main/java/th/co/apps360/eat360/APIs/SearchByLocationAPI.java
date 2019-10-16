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


public class SearchByLocationAPI extends AsyncTask<String, Void, String> {


    private Context mContext;
    private ResultCallback resultCallback;
    private  String minLatitude;
    private  String minLongitude;
    private  String maxLatitude;
    private  String maxLongitude;

    public interface ResultCallback {
        void SearchByLocationResultCallback(String jsonStringResult);
    }

    public SearchByLocationAPI(Context context,String minLatitude,String minLongitude,
                               String maxLatitude,String maxLongitude){
        mContext = context;
        resultCallback =  (ResultCallback) mContext;
        this.minLatitude = minLatitude;
        this.minLongitude = minLongitude;
        this.maxLatitude = maxLatitude;
        this.maxLongitude = maxLongitude;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
                    apiUrl = new ConfigURL(mContext).searchByLocationURL;
                else
                    apiUrl = url[0];
            } else
                apiUrl = new ConfigURL(mContext).searchByLocationURL;

            HttpPost httppost = new HttpPost(apiUrl);

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            String language = Utils.getCurrentLanguage(mContext);
            nameValuePairs.add(new BasicNameValuePair("LanguageID", language));
            nameValuePairs.add(new BasicNameValuePair("MinLatitude", minLatitude));
            nameValuePairs.add(new BasicNameValuePair("MinLongitude", minLongitude));
            nameValuePairs.add(new BasicNameValuePair("MaxLatitude", maxLatitude));
            nameValuePairs.add(new BasicNameValuePair("MaxLongitude", maxLongitude));
            nameValuePairs.add(new BasicNameValuePair("PageID", "1"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
            resultCallback.SearchByLocationResultCallback(jsonString);
            Utils.setDebug("json SearchByLocationAPI", jsonString);
        } catch (Exception e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }
}

