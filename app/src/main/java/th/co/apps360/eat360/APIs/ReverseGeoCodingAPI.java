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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

/**
 * Created by dan on 11/8/16.
 */

public class ReverseGeoCodingAPI extends AsyncTask<String, Void, String> {

    private Context mContext;
    private Double latitude;
    private Double longitude;
    private ResultCallback resultCallback;

    public  interface ResultCallback {
        void locationResultCallback(String jsonStringResult);
        void locationResponseCallback(String jsonStringResult);
    }

    public  ReverseGeoCodingAPI(Context context , Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        mContext = context;
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
            if (url != null && url.length>0) {
                if (url[0] == null)
                    apiUrl = ConfigURL.reverseGeoCodingURL;
                else
                    apiUrl = url[0];
            }
            else
                apiUrl = ConfigURL.reverseGeoCodingURL;

            String latlngParam = "latlng=" + this.latitude + "," + this.longitude;
            String keyParam =  "key=" + mContext.getString(R.string.googlemap_geocoding_apikey);
            apiUrl += "?" + latlngParam + "&" + keyParam;

            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(apiUrl));

            HttpResponse response = httpClient.execute(httpGet);
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
        resultCallback.locationResponseCallback(jsonString);
        Utils.setDebug("json ReverseGeoCodingAPI", jsonString);

        String address = "";
        try {
            String country = "", state = "", county = "", city = "", town = "", postal_code = "";
            JSONObject mainObject = new JSONObject(jsonString);
            JSONArray resultArray = mainObject.getJSONArray("results");
            if (resultArray.length() > 0) {
                JSONObject aResult = resultArray.getJSONObject(0);
                JSONArray address_componentsArray = aResult.getJSONArray("address_components");
                for (int i = 0; i < address_componentsArray.length(); i ++) {
                    JSONObject aComponent = address_componentsArray.getJSONObject(i);
                    String longname = aComponent.getString("long_name");
                    String shortname = aComponent.getString("short_name");
                    JSONArray typesArray = aComponent.getJSONArray("types");
                    String type1 = typesArray.getString(0);

                    if ("sublocality".equals(type1)) {
                        town = shortname;
                    }
                    else if ("locality".equals(type1)) {
                        city = shortname;
                    }
                    else if ("administrative_area_level_2".equals(type1)) {
                        county = shortname;
                    }
                    else if ("administrative_area_level_1".equals(type1)) {
                        state = shortname;
                    }
                    else if ("country".equals(type1)) {
                        country = longname;
                    }
                    else if ("postal_code".equals(type1)) {
                        postal_code = shortname;
                    }
                }
            }

            if (!"".equals(city)) {
                address = city;
            }
            else if (!"".equals(town)) {
                address = town;
            }
            address += ", ";
            if (!"".equals(state)) {
                address += state;
            }
            else if (!"".equals(county)) {
                address += county;
            }
            address += " ";
            if (!"".equals(postal_code)) {
                address += postal_code;
            }
            address += "\n";
            if (!"".equals(country)) {
                address += country;
            }

        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }

        resultCallback.locationResultCallback(address);
        Utils.setDebug("address ReverseGeoCodingAPI", address);
    }

}
