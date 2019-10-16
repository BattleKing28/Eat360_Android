package th.co.apps360.eat360.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import th.co.apps360.eat360.APIs.GetUserInfoAPI;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private GetUserInfoAPI mUserInfoTask = null;
    private String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        token = Utils.loadToken(this);
        if (null == token) {
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        finish();
        } else {
            getUserInfo(token);
        }
    }

    private void getUserInfo(String token) {
        String url = ConfigURL.server1 + "/get_user_info";
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        params.put("Token", token);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (!response.has("Status")) {
                        String message = response.getString("StatusMsg");
                        Utils.setDebug("user info", message);
                        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        String status = response.getString("Status");
                        if (status.equals("false")) {
                            String message = response.getString("Message");
                            Utils.setDebug("user info", message);
                            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                            finish();
                        } else if (status.equals("true")) {
                            JSONObject resultObject = response.getJSONObject("Result");
                            String email = resultObject.getString("email");
                            String firstname = resultObject.getString("first_name");
                            String lastname = resultObject.getString("last_name");
                            String apitoken = resultObject.getString("api_token");
                            String avatarurl = resultObject.getString("avatar");
                            MoreDataModel.userAvatarUrl = avatarurl;
                            MoreDataModel.userEmail = email;
                            MoreDataModel.userFirstname = firstname;
                            MoreDataModel.userLastname = lastname;
                            Utils.saveToken(SplashActivity.this, apitoken);
                            Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Utils.setDebug("user info", "unknown status");
                            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    Utils.setDebug("crash", e.getLocalizedMessage());
                    Utils.setDebug("user info", "json crash");
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
//                Utils.showDialog(SplashActivity.this, "Failed", "Invalid login info.");
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }

        });
    }

}
