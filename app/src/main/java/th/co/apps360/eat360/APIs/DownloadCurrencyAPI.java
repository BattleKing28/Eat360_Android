package th.co.apps360.eat360.APIs;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Utils;

/**
 * Created by lafong on 6/9/2558.
 */


public class DownloadCurrencyAPI extends AsyncTask<String, Void, String> {


    private Context mContext;
    private ResultCallback resultCallback;


    public  interface ResultCallback{
        public void downloadCurrencyResultCallback(String jsonStringResult);
    }

    public DownloadCurrencyAPI(Context context){
        mContext = context;
        resultCallback =  (ResultCallback) mContext;
    }

    @Override
    protected String doInBackground(String... url) {
        StringBuilder stringResult = new StringBuilder();
        try{
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
                    apiUrl = new ConfigURL(mContext).listCurrencyURL;
                else
                    apiUrl = url[0];

            }else
                apiUrl = new ConfigURL(mContext).listCurrencyURL;

            HttpGet request = new HttpGet();
            URI website = new URI(apiUrl);
            request.setURI(website);
            HttpResponse response = httpClient.execute(request);

            //response.getStatusLine().getStatusCode() == HttpStatus.SC_OK

            BufferedReader buffer = new BufferedReader(new InputStreamReader( response.getEntity().getContent()));
            String line;
            while ((line = buffer.readLine()) != null) {
                stringResult.append(line);
            }


        } catch (ConnectTimeoutException e) {
            stringResult.append(Utils.TIMEOUT_ERROR);
        } catch (IOException e) {
            stringResult.append(Utils.CONNECTION_ERROR);
        } catch (URISyntaxException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

        return stringResult.toString();
    }

    @Override
    protected void onProgressUpdate(Void... values) {}


    @Override
    protected void onPostExecute(String jsonString) {

        resultCallback.downloadCurrencyResultCallback(jsonString);
        Utils.setDebug("json DownloadCurrencyAPI", jsonString);

    }

}
