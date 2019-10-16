package th.co.apps360.eat360.APIs;

/**
 * Created by dan on 2/10/17.
 */

import com.loopj.android.http.*;

public class Eat360RestClient {

    private static final String BASE_URL = "https://stagingapi.eat360app.com";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
