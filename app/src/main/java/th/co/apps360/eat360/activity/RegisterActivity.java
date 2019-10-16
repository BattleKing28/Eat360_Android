package th.co.apps360.eat360.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import th.co.apps360.eat360.APIs.RegisterAPI;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity extends Activity
        implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;
    private RegisterAPI mRegisterTask = null;

    // UI references.
    private EditText mFirstnameEdit;
    private EditText mLastnameEdit;
    private AutoCompleteTextView mEmailEdit;
    private EditText mPhoneEdit;
    private EditText mCityEdit;
    private EditText mCountryEdit;
    private EditText mPasswordEdit;
    private RelativeLayout mRegisterButton;
    private TextView mSkipRegisterButton;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstnameEdit = (EditText) findViewById(R.id.register_name_edit);
        mLastnameEdit = (EditText) findViewById(R.id.register_surname_edit);
        mEmailEdit = (AutoCompleteTextView) findViewById(R.id.register_email_edit);
        populateAutoComplete();
        mPhoneEdit = (EditText) findViewById(R.id.register_phone_edit);
        mCityEdit = (EditText) findViewById(R.id.register_city_edit);
        mCountryEdit = (EditText) findViewById(R.id.register_country_edit);
        mPasswordEdit = (EditText) findViewById(R.id.register_password_edit);
        mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                if (id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        mRegisterButton = (RelativeLayout) findViewById(R.id.register_signup_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
        showProgress(false);

        mSkipRegisterButton = (TextView) findViewById(R.id.register_skip);
        mSkipRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.saveToken(RegisterActivity.this, null);
                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(mainIntent);
                RegisterActivity.this.finish();
            }
        });

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailEdit, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mFirstnameEdit.setError(null);
        mLastnameEdit.setError(null);
        mEmailEdit.setError(null);
        mPhoneEdit.setError(null);
        mCityEdit.setError(null);
        mCountryEdit.setError(null);
        mPasswordEdit.setError(null);

        // Store values at the time of the login attempt.
        String firstname = mFirstnameEdit.getText().toString();
        String lastname = mLastnameEdit.getText().toString();
        String email = mEmailEdit.getText().toString();
        String phone = mPhoneEdit.getText().toString();
        String city = mCityEdit.getText().toString();
        String country = mCountryEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Utils.isPasswordValid(password)) {
            mPasswordEdit.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEdit;
            cancel = true;
        }

        if (TextUtils.isEmpty(country)) {
            mCountryEdit.setError(getString(R.string.error_field_required));
            focusView = mCountryEdit;
            cancel = true;
        }

        if (TextUtils.isEmpty(city)) {
            mCityEdit.setError(getString(R.string.error_field_required));
            focusView = mCityEdit;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneEdit.setError(getString(R.string.error_field_required));
            focusView = mPhoneEdit;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailEdit.setError(getString(R.string.error_field_required));
            focusView = mEmailEdit;
            cancel = true;
        } else if (!Utils.isEmailValid(email)) {
            mEmailEdit.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEdit;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastname)) {
            mLastnameEdit.setError(getString(R.string.error_field_required));
            focusView = mLastnameEdit;
            cancel = true;
        } else if (!Utils.isUsernameValid(lastname)) {
            mLastnameEdit.setError(getString(R.string.error_invalid_name));
            focusView = mLastnameEdit;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstname)) {
            mFirstnameEdit.setError(getString(R.string.error_field_required));
            focusView = mFirstnameEdit;
            cancel = true;
        } else if (!Utils.isUsernameValid(firstname)) {
            mFirstnameEdit.setError(getString(R.string.error_invalid_name));
            focusView = mFirstnameEdit;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
//            mRegisterTask = new RegisterAPI(this, firstname, lastname, email,
//                    phone, city, country, password);
//            mRegisterTask.execute();

            String url = ConfigURL.server1 + "/register";
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            RequestParams params = new RequestParams();
            params.put("FirstName", firstname);
            params.put("LastName", lastname);
            params.put("Email", email);
            params.put("Phone", phone);
            params.put("City", city);
            params.put("Country", country);
            params.put("Password", password);

            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    showProgress(false);
                    try {
                        JSONObject mainObject = response;
                        if (mainObject.has("status")) {
                            String status = mainObject.getString("status");
                            if (status.equals("false")) {
                                String message = mainObject.getString("message");
                                Utils.showDialog(RegisterActivity.this, "Failed to Register", message);
                            } else {
                                String userid = mainObject.getString("user_id");
                                Utils.setDebug("register", "userid: " + userid);
                                Intent confirmIntent = new Intent(RegisterActivity.this, ConfirmActivity.class);
                                startActivity(confirmIntent);
                                finish();
                            }
                        } else if (mainObject.has("StatusCode")){
                            String status = mainObject.getString("StatusCode");
                            String message = mainObject.getString("StatusMsg");
                            Utils.showDialog(RegisterActivity.this, "Failed to Register", message);
                        }
                    } catch (JSONException e) {
                        Utils.setDebug("crash", e.getLocalizedMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                    showProgress(false);
                    Utils.showDialog(RegisterActivity.this, "Failed", "Invalid register info.");
                }
            });
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailEdit.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

