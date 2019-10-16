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
 * Created by dan on 1/18/17.
 */

public class RegisterAPI extends AsyncTask<String, Void, String> {

    private Context mContext;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mPhone;
    private String mCity;
    private String mCountry;
    private String mPassword;
    private ResultCallback resultCallback;

    public interface ResultCallback {
        void registerCallback(String jsonStringResult);
    }

    public RegisterAPI(Context context, String firstname, String lastname, String email,
                       String phone, String city, String country, String password) {
        mContext = context;
        mFirstName = firstname;
        mLastName = lastname;
        mEmail = email;
        mPhone = phone;
        mCity = city;
        mCountry = country;
        mPassword = password;
        resultCallback = (ResultCallback) mContext;
    }

    @Override
    protected String doInBackground(String... params) {
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

            String apiUrl = new ConfigURL().registerURL;
            HttpPost httppost = new HttpPost(apiUrl);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("FirstName", mFirstName));
            nameValuePairs.add(new BasicNameValuePair("LastName", mLastName));
            nameValuePairs.add(new BasicNameValuePair("Email", mEmail));
            nameValuePairs.add(new BasicNameValuePair("Password", mPassword));
            nameValuePairs.add(new BasicNameValuePair("Phone", mPhone));
            nameValuePairs.add(new BasicNameValuePair("City", mCity));
            nameValuePairs.add(new BasicNameValuePair("Country", mCountry));
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
    protected void onPostExecute(String jsonString) {
        resultCallback.registerCallback(jsonString);
        Utils.setDebug("json RegisterAPI", jsonString);
    }

}
