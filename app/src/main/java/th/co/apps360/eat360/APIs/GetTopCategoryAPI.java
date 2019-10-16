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
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.Utils;

/**
 * Created by lafong on 6/9/2558.
 */


public class GetTopCategoryAPI extends AsyncTask<String, Void, String> {


    private Context mContext;
    private MoreDataModel.Search_Tab mTab;
    private ResultCallback resultCallback;

    public  interface ResultCallback {
        public void getTopCategoryAPIResultCallback(String jsonStringResult);
        public void getTopCuisineAPIResultCallback(String jsonStringResult);
    }

    public GetTopCategoryAPI(Context context, MoreDataModel.Search_Tab nTab){
        mContext = context;
        mTab = nTab;
        resultCallback =  (ResultCallback) mContext;
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
                    apiUrl = new ConfigURL(mContext).topCategoryURL;
                else
                    apiUrl = url[0];
            }else
                apiUrl = new ConfigURL(mContext).topCategoryURL;

            HttpPost httppost = new HttpPost(apiUrl);

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            String language = Utils.getCurrentLanguage(mContext);
//            language = "en" ; //fix "en" for test
            nameValuePairs.add(new BasicNameValuePair("LanguageID", language));
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

        if (mTab.equals(MoreDataModel.Search_Tab.FOOD)) {
            resultCallback.getTopCategoryAPIResultCallback(jsonString);
            Utils.setDebug("json GetTopCategoryAPI", jsonString);
        }
        else {
            resultCallback.getTopCuisineAPIResultCallback(jsonString);
            Utils.setDebug("json GetTopCuisineAPI", jsonString);
        }
    }
}


