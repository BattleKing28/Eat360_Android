package th.co.apps360.eat360.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import th.co.apps360.eat360.APIs.DownloadCurrencyAPI;
import th.co.apps360.eat360.APIs.DownloadLanguagesAPI;
import th.co.apps360.eat360.APIs.GetUserInfoAPI;
import th.co.apps360.eat360.APIs.LogoutAPI;
import th.co.apps360.eat360.APIs.UpdateUsernameAPI;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.Model.CurrencyModel;
import th.co.apps360.eat360.Model.LanguageModel;
import th.co.apps360.eat360.Model.LocationModel;
import th.co.apps360.eat360.Model.MoreDataModel;
import th.co.apps360.eat360.Model.MoreFoodDataModel;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.AboutActivity;
import th.co.apps360.eat360.activity.AvatarActivity;
import th.co.apps360.eat360.activity.HelpActivity;
import th.co.apps360.eat360.activity.LoginActivity;
import th.co.apps360.eat360.activity.MainActivity;
import th.co.apps360.eat360.activity.MoreAppActivity;
import th.co.apps360.eat360.activity.RegisterActivity;
import th.co.apps360.eat360.adapter.CurrencyAdapter;
import th.co.apps360.eat360.adapter.LanguageAdapter;

public class NavigationDrawerFragment extends Fragment
        implements DrawerLayout.DrawerListener {

    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View rootView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;

    private RelativeLayout profileContainer;
    private RelativeLayout loginContainer;
    private LinearLayout languageContainer;
    private LinearLayout currencyContainer;
    private LinearLayout helpContainer;
    private LinearLayout aboutContainer;
    private LinearLayout logoutContainer;

    private ImageView avatarImageView;
    private TextView usernameLabel;
    private EditText usernameEdit;
    private Button usernameButton;
    private TextView emailLabel;

    private TextView signinButton;
    private TextView signupButton;

    private ImageView languageIconView;
    private TextView languageLabel;
    private TextView currencyLabel;
    private TextView helpLabel;
    private TextView aboutLabel;
    private TextView logoutLabel;
    private TextView locationLabel;
    private TextView moreappsPrefixLabel;
    private TextView moreappsLabel;

    private ImageView facebookIconView;
    private ImageView linkedinIconView;
    private ImageView twitterIconView;

    private ListView languageListView;
    private ListView currencyListView;
    private ImageView selectOKButton;
    private ImageView selectCancelButton;
    private TextView selectTextView;
    private LinearLayout blurView;
    private LinearLayout menuView;

    private HashMap<String,String> languageList;
    private HashMap<String,String> currencyList;
    private String[] languageArray;
    private String[] currencyArray;
    private String languageName;
    private String currencyName;
    private String defaultLanguage;
    private String defaultCurrency;
    private String tempLanguageName;
    private String tempCurrencyName;
    private String avatarPath;
    private int tempLanguageIdx = 2;
    private int tempCurrencyIdx = 7;

    private ArrayList<AsyncTask> allAsyncTask = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =   inflater.inflate( R.layout.fragment_navigation_drawer, container, false);

        profileContainer = (RelativeLayout) rootView.findViewById(R.id.profile_container);
        loginContainer = (RelativeLayout) rootView.findViewById(R.id.login_container);
        languageContainer = (LinearLayout) rootView.findViewById(R.id.language_container);
        currencyContainer = (LinearLayout) rootView.findViewById(R.id.currency_container);
        helpContainer = (LinearLayout) rootView.findViewById(R.id.help_container);
        aboutContainer = (LinearLayout) rootView.findViewById(R.id.about_container);
        logoutContainer = (LinearLayout) rootView.findViewById(R.id.logout_container);

        avatarImageView = (ImageView) rootView.findViewById(R.id.slide_avatar_image);
        usernameLabel = (TextView) rootView.findViewById(R.id.slide_username_label);
        usernameEdit = (EditText) rootView.findViewById(R.id.slide_username_edit);
        usernameButton = (Button) rootView.findViewById(R.id.slide_username_button);
        emailLabel = (TextView) rootView.findViewById(R.id.slide_email_label);

        signinButton = (TextView) rootView.findViewById(R.id.slide_signin_button);
        signupButton = (TextView) rootView.findViewById(R.id.slide_signup_button);

        languageIconView = (ImageView) rootView.findViewById(R.id.slide_language_icon);
        languageLabel = (TextView) rootView.findViewById(R.id.slide_language_label);
        currencyLabel = (TextView) rootView.findViewById(R.id.slide_currency_label);
        helpLabel = (TextView) rootView.findViewById(R.id.slide_help_label);
        aboutLabel = (TextView) rootView.findViewById(R.id.slide_about_label);
        logoutLabel = (TextView) rootView.findViewById(R.id.slide_logout_label);
        locationLabel = (TextView) rootView.findViewById(R.id.slide_location_label);
        facebookIconView = (ImageView) rootView.findViewById(R.id.slide_facebook_icon);
        linkedinIconView = (ImageView) rootView.findViewById(R.id.slide_linkedin_icon);
        twitterIconView = (ImageView) rootView.findViewById(R.id.slide_twitter_icon);
        moreappsPrefixLabel = (TextView) rootView.findViewById(R.id.slide_moreapps_prefix);
        moreappsLabel = (TextView) rootView.findViewById(R.id.slide_moreapps_label);

        languageListView = (ListView) rootView.findViewById(R.id.slide_language_list);
        currencyListView = (ListView) rootView.findViewById(R.id.slide_currency_list);
        selectTextView = (TextView) rootView.findViewById(R.id.slide_select_text);
        selectOKButton = (ImageView) rootView.findViewById(R.id.slide_select_ok);
        selectCancelButton = (ImageView) rootView.findViewById(R.id.slide_select_cancel);
        menuView = (LinearLayout) rootView.findViewById(R.id.slide_menu_view);
        blurView = (LinearLayout) rootView.findViewById(R.id.slide_blur_view);

        menuView.setVisibility(View.VISIBLE);
        blurView.setVisibility(View.INVISIBLE);

        if (null == Utils.loadToken(getActivity())) {
            profileContainer.setVisibility(View.INVISIBLE);
            logoutContainer.setVisibility(View.GONE);
            loginContainer.setVisibility(View.VISIBLE);
            signinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.initAllGlobalData();
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                    getActivity().finish();
                }
            });
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.initAllGlobalData();
                    Intent loginIntent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(loginIntent);
                    getActivity().finish();
                }
            });
        } else {
            profileContainer.setVisibility(View.VISIBLE);
            logoutContainer.setVisibility(View.VISIBLE);
            loginContainer.setVisibility(View.INVISIBLE);
        }

        languageList = new HashMap<>();
        currencyList  = new HashMap<>();

        languageName = "";
        currencyName = "";


        initHashMap();
        initDefaultValues();
        setLanguageByLocalData();
        setTextLanguage();
        setCurrency();
        setClickListener();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void initHashMap() {
        languageList.put("English", defaultLanguage);
        currencyList.put("AUD", defaultCurrency);
    }

    private void initDefaultValues() {
        defaultLanguage = "en";
        defaultCurrency = "aud";
        setPreferenceLanguage(defaultLanguage, false);
        setPreferenceCurrency(defaultCurrency, false);
    }

    private void setClickListener() {
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent avatarIntent = new Intent(getActivity(), AvatarActivity.class);
                startActivity(avatarIntent);
            }
        });
        usernameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditUsername();
            }
        });
        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpdateUsername();
            }
        });
        languageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blurView.setVisibility(View.VISIBLE);
                languageListView.setVisibility(View.VISIBLE);
                currencyListView.setVisibility(View.GONE);
                loadDataInLanguageListView();
//                setDialogLanguage();
            }
        });
        currencyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blurView.setVisibility(View.VISIBLE);
                languageListView.setVisibility(View.GONE);
                currencyListView.setVisibility(View.VISIBLE);
                loadDataInCurrencyListView();
//                setDialogCurrency();
            }
        });
        helpContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent helpIntent = new Intent(getActivity(), HelpActivity.class);
                startActivity(helpIntent);
            }
        });
        aboutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                startActivity(aboutIntent);
            }
        });
        moreappsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moreappsIntent = new Intent(getActivity(), MoreAppActivity.class);
                startActivity(moreappsIntent);
            }
        });
        logoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogout();
            }
        });
        selectOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languageListView.getVisibility() == View.VISIBLE) {
                    Utils.setCurrentLanguageIdx(getActivity(), tempLanguageIdx);
                    if ("".equals(MoreDataModel.languageObjectList.get(tempLanguageIdx).icon)) {
                        languageIconView.setImageResource(R.drawable.lang_icon);
                    } else {
                        Utils.setImageWithPicasso(getActivity(), MoreDataModel.languageObjectList.get(tempLanguageIdx).icon, languageIconView);
                    }
                    languageLabel.setText(MoreDataModel.languageObjectList.get(tempLanguageIdx).name);

                    Utils.setCurrentLanguage(getActivity(), MoreDataModel.languageObjectList.get(tempLanguageIdx).lang_code);
                    Utils.setCurrentLanguageIcon(getActivity(), MoreDataModel.languageObjectList.get(tempLanguageIdx).icon);
                    changeLanguage();
                    mCallbacks.onLanguageSelected();
                } else if (currencyListView.getVisibility() == View.VISIBLE) {
                    Utils.setCurrentCurrencyIdx(getActivity(), tempCurrencyIdx);
                    currencyLabel.setText(MoreDataModel.currencyObjectList.get(tempCurrencyIdx).currency.toUpperCase());
                    Utils.setCurrentCurrency(getActivity(), MoreDataModel.currencyObjectList.get(tempCurrencyIdx).currency);
                    mCallbacks.onCurrencySelected();
                }
                blurView.setVisibility(View.INVISIBLE);
            }
        });
        selectCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languageListView.getVisibility() == View.VISIBLE) {
                    int tempLanguageIndex = Utils.getCurrentLanguageIdx(getActivity());
                    if ("".equals(MoreDataModel.languageObjectList.get(tempLanguageIndex).icon)) {
                        languageIconView.setImageResource(R.drawable.lang_icon);
                    } else {
                        Utils.setImageWithPicasso(getActivity(), MoreDataModel.languageObjectList.get(tempLanguageIndex).icon, languageIconView);
                    }
                    languageLabel.setText(MoreDataModel.languageObjectList.get(tempLanguageIndex).name);
                } else if (currencyListView.getVisibility() == View.VISIBLE) {
                    int tempCurrencyIndex = Utils.getCurrentCurrencyIdx(getActivity());
                    currencyLabel.setText(MoreDataModel.currencyObjectList.get(tempCurrencyIndex).currency.toUpperCase());
                }
                blurView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getUserInfo() {
        usernameLabel.setText(MoreDataModel.userFirstname + " " + MoreDataModel.userLastname);
        emailLabel.setText(MoreDataModel.userEmail);
        setPreviewAvatar();

        String token = Utils.loadToken(getActivity());
        allAsyncTask.add(new GetUserInfoAPI(getActivity(), token).execute());
    }

    public void getUserInfoResult(String jsonString) {
        try {
            JSONObject mainObject = new JSONObject(jsonString);
            if (!mainObject.has("Status")) {
                String message = mainObject.getString("StatusMsg");
                Utils.setDebug("user info", message);
            } else {
                String status = mainObject.getString("Status");
                if (status.equals("false")) {
                    String message = mainObject.getString("Message");
                    Utils.setDebug("user info", message);
                } else if (status.equals("true")) {
                    JSONObject resultObject = mainObject.getJSONObject("Result");
                    String email = resultObject.getString("email");
                    String firstname = resultObject.getString("first_name");
                    String lastname = resultObject.getString("last_name");
                    String apitoken = resultObject.getString("api_token");
                    String avatarurl = resultObject.getString("avatar");
                    MoreDataModel.userAvatarUrl = avatarurl;
                    MoreDataModel.userEmail = email;
                    MoreDataModel.userFirstname = firstname;
                    MoreDataModel.userLastname = lastname;
                    Utils.saveToken(getActivity(), apitoken);

                    setPreviewAvatar();
                    usernameLabel.setText(MoreDataModel.userFirstname + " " + MoreDataModel.userLastname);
                    emailLabel.setText(MoreDataModel.userEmail);
                } else {
                    Utils.setDebug("user info", "unknown status");
                }
            }
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
            Utils.setDebug("user info", "json crash");
        }

    }

    private void setPreviewAvatar() {
        if ("".equals(MoreDataModel.userAvatarUrl)) {
            avatarImageView.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.with(getActivity()).load(MoreDataModel.userAvatarUrl)
                    .placeholder(R.drawable.profile_image)
                    .error(R.drawable.profile_image)
                    .into(avatarImageView);
        }
    }

    private void onEditUsername() {
        usernameLabel.setVisibility(View.INVISIBLE);
        usernameEdit.setVisibility(View.VISIBLE);
        usernameButton.setVisibility(View.VISIBLE);
    }

    private void onUpdateUsername() {
        String username = usernameEdit.getText().toString();
        String[] usernameArray = username.split(" ");
        String firstname = "";
        String lastname = "";
        if (usernameArray.length > 0) {
            firstname = usernameArray[0];
            if (usernameArray.length > 1) {
                for (int i = 1; i < usernameArray.length; i ++) {
                    String unitUsername = usernameArray[i];
                    lastname += unitUsername;
                    lastname += " ";
                }
                lastname = lastname.substring(0, lastname.length() - 1);
            }
        }
        String token = Utils.loadToken(getActivity());
        allAsyncTask.add(new UpdateUsernameAPI(getActivity(), token, firstname, lastname).execute());
    }

    public void replaceUsername(String jsonString) {
        try {
            JSONObject mainObject = new JSONObject(jsonString);
            String statusCode = mainObject.getString("StatusCode");
            String statusMsg = mainObject.getString("StatusMsg");
            if (statusCode.equals("200")) {
                if (statusMsg.equals("success")) {
                    usernameLabel.setText(usernameEdit.getText().toString());
                    usernameLabel.setVisibility(View.VISIBLE);
                    usernameEdit.setVisibility(View.INVISIBLE);
                    usernameButton.setVisibility(View.INVISIBLE);
                    return;
                }
            }
            Utils.setDebug("update username", "failed:" + statusMsg);
            Utils.showDialog(getActivity(), "Update username", "failed: " + statusMsg);
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void onLogout() {
        String token = Utils.loadToken(getActivity());
        allAsyncTask.add(new LogoutAPI(getActivity(), token).execute());
//        FirebaseAuth.getInstance().signOut();
        closeDrawer();
    }

    // TODO: Language
    public void downloadLanguage() {
        allAsyncTask.add( new DownloadLanguagesAPI(getActivity()).execute(new ConfigURL(getActivity()).listLanguageURL));
    }

    public void setStringLanguageToVariable(String jsonString) {
        try {
            JSONObject jObjectResult = new JSONObject(jsonString);
            JSONObject jObject = jObjectResult.getJSONObject("Results");
            Iterator<String> keys = jObject.keys();
            int size = jObject.length();
            MoreDataModel.languageObjectList.clear();
            for(int i=0;i<size;i++) {
                String object = keys.next();
                JSONObject langObject = jObject.getJSONObject(object);
                int id = langObject.getInt("id");
                String lang_code = langObject.getString("lang_code");
                String keyName  = langObject.getString("name");
                keyName =  keyName.substring(0,1).toUpperCase()+ keyName.substring(1).toLowerCase(); //capital first char
                String name = keyName;
                String short_name = langObject.getString("short_name");
                String icon = langObject.getString("icon");
                LanguageModel aLanguageObject = new LanguageModel(name, id, short_name, lang_code, icon);
                MoreDataModel.languageObjectList.add(aLanguageObject);
            }
            Collections.sort(MoreDataModel.languageObjectList);
        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void loadDataInLanguageListView() {
        final LanguageAdapter adapter = new LanguageAdapter(getActivity(), MoreDataModel.languageObjectList);
        languageListView.setAdapter(adapter);
        languageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < MoreDataModel.languageObjectList.size(); i++) {
                    if (i == position) {
                        MoreDataModel.languageObjectList.get(i).isSelected = true;
                        tempLanguageIdx = i;
                    } else {
                        MoreDataModel.languageObjectList.get(i).isSelected = false;
                    }
                }
                adapter.notifyDataSetChanged();
                selectTextView.setText(MoreDataModel.languageObjectList.get(position).name);
            }
        });
        int tempidx = Utils.getCurrentLanguageIdx(getActivity());
        selectTextView.setText(MoreDataModel.languageObjectList.get(tempidx).name);
    }

    private void setPreferenceLanguage(String languageCode,boolean forceSet) {
        String language = Utils.getCurrentLanguage(this.getActivity());
        if(forceSet || language == null) {
            Utils.setCurrentLanguage(this.getActivity(), languageCode);
        }
    }


    private void setDialogLanguage() {
        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View languageLayout = inflater.inflate(R.layout.language_layout, null);
        languageListView = (ListView) languageLayout.findViewById(R.id.language_list);
        languageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if(languageArray == null)
            languageArray = Arrays.copyOf(languageList.keySet().toArray(), languageList.keySet().toArray().length, String[].class);
        setListLanguage(languageArray);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setView(languageLayout);
        builder.setTitle(getResources().getString(R.string.Select_language));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        languageName = tempLanguageName;
//                        language.setText(" " + languageName);
                        languageLabel.setText(languageName);
                        setPreferenceLanguage(languageList.get(languageName), true);
                        changeLanguage();
                        dialog.cancel();
                        mCallbacks.onLanguageSelected();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog languageDialog = builder.create();
        languageDialog.show();
    }

    private void setListLanguage(String []listLanguage) {
        final ArrayAdapter adapter = new ArrayAdapter(this.getActivity(),R.layout.list_item,R.id.item,listLanguage);
        languageListView.setAdapter(adapter);
        languageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RadioButton radioButton = (RadioButton) view.findViewById(R.id.item);
                tempLanguageName = radioButton.getText().toString();
            }
        });

        for(int i=0;i<listLanguage.length;i++) {
            if(listLanguage[i].equals(languageName)) {
                languageListView.setItemChecked(i,true);
                tempLanguageName = languageName;
                break;
            }
        }
        adapter.notifyDataSetChanged();
        //languageListView.deferNotifyDataSetChanged();
    }


    private void changeLanguage() {
        helpLabel.setText(getActivity().getResources().getString(R.string.help));
        aboutLabel.setText(getActivity().getResources().getString(R.string.about));
        logoutLabel.setText(getActivity().getResources().getString(R.string.log_out));
        moreappsPrefixLabel.setText(getActivity().getResources().getString(R.string.moreapps));
    }

    public void setLanguage() {
        setTextLanguage();
    }

    private  void setLanguageByLocalData() {
        languageList = new HashMap<>() ;
        languageList = Utils.getHashMapLanguage(getActivity());
        languageArray = new String[languageList.size()];
        languageArray = Arrays.copyOf(languageList.keySet().toArray(), languageList.keySet().toArray().length, String[].class);
        Arrays.sort(languageArray);
    }


    public  void setTextLanguage() {
        //SharedPreferences prefs = this.getActivity().getSharedPreferences(idPreference, this.getActivity().MODE_PRIVATE);
        String value = Utils.getCurrentLanguage(this.getActivity());
        languageName = getKeyFromValueLanguage(value);
        tempLanguageName = languageName;
        if(languageName == null) {
            setPreferenceLanguage(defaultLanguage, true);
            languageName = getKeyFromValueLanguage(defaultLanguage);
        }
        Utils.setLanguageApp(getActivity(),Utils.getCurrentLanguage(this.getActivity()));
        changeLanguage();
        languageLabel.setText(languageName);

        String icon = Utils.getCurrentLanguageIcon(getActivity());
        if (null == icon || "".equals(icon)) {
            languageIconView.setImageResource(R.drawable.lang_icon);
        } else {
            Utils.setImageWithPicasso(getActivity(), icon, languageIconView);
        }
    }

    private String getKeyFromValueLanguage(String value) {
        for( String key : languageList.keySet() ) {
            if(languageList.get(key).equals(value))
                return key;
        }
        return null;
    }

    // TODO: Currency
    public void downloadCurrency() {
        allAsyncTask.add(new DownloadCurrencyAPI(getActivity()).execute());
    }

    public void setStringCurrencyToVariable(String jsonString) {
        try {
            JSONObject jObjectResult = new JSONObject(jsonString);
            JSONObject jObject = jObjectResult.getJSONObject("results");
            Iterator<String> keys = jObject.keys();
            currencyArray = new String[jObject.length()];
            int size = jObject.length();
            MoreDataModel.currencyObjectList.clear();

            for(int i=0;i<size;i++) {
                String object = keys.next();
                JSONObject langObject  = jObject.getJSONObject(object);
                String keyName  = langObject.getString("currency");
                currencyList.put(keyName.toUpperCase(), object);
                String key = keyName.toUpperCase();
                currencyArray[i] = key;

                String currency = keyName;
                int id = langObject.getInt("id");
                double rate = langObject.getDouble("rate");
                int is_based = langObject.getInt("is_based");
                CurrencyModel aCurrencyObject = new CurrencyModel(currency, id, rate, is_based);
                MoreDataModel.currencyObjectList.add(aCurrencyObject);
            }

            Collections.sort(MoreDataModel.currencyObjectList);
            Arrays.sort(currencyArray);
            Utils.setStringCurrency(getActivity(), jsonString);
            setTextCurrency();

        } catch (JSONException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
    }

    private void loadDataInCurrencyListView() {
        final CurrencyAdapter adapter = new CurrencyAdapter(getActivity(), MoreDataModel.currencyObjectList);
        currencyListView.setAdapter(adapter);
        currencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < MoreDataModel.currencyObjectList.size(); i++) {
                    if (i == position) {
                        MoreDataModel.currencyObjectList.get(i).isSelected = true;
                        tempCurrencyIdx = i;
                    } else {
                        MoreDataModel.currencyObjectList.get(i).isSelected = false;
                    }
                }
                adapter.notifyDataSetChanged();
                selectTextView.setText(MoreDataModel.currencyObjectList.get(position).currency.toUpperCase());
            }
        });
        int tempidx = Utils.getCurrentCurrencyIdx(getActivity());
        selectTextView.setText(MoreDataModel.currencyObjectList.get(tempidx).currency.toUpperCase());
    }

    private void setPreferenceCurrency(String currencyCode,boolean forceSet) {
        String currency = Utils.getCurrentCurrency(this.getActivity());
        if(forceSet || currency == null){
            Utils.setCurrentCurrency(this.getActivity(), currencyCode);
        }
    }



    private void setDialogCurrency() {
        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View languageLayout = inflater.inflate(R.layout.currency_layout, null);
        currencyListView = (ListView) languageLayout.findViewById(R.id.currency_list);
        currencyListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if(currencyArray == null)
            currencyArray = Arrays.copyOf(currencyList.keySet().toArray(), currencyList.keySet().toArray().length, String[].class);
        setListCurrency(currencyArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setView(languageLayout);
        builder.setTitle(getResources().getString(R.string.Select_currency));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currencyName = tempCurrencyName;
//                        currency.setText(" "+currencyName);
                        currencyLabel.setText(currencyName);
                        setPreferenceCurrency(currencyList.get(currencyName), true);
                        dialog.cancel();
                        mCallbacks.onCurrencySelected();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog currencyDialog = builder.create();
        currencyDialog.show();
    }

    private void setListCurrency(String []listCurrency) {
        final ArrayAdapter adapter = new ArrayAdapter(this.getActivity(),R.layout.list_item,R.id.item,listCurrency);
        currencyListView.setAdapter(adapter);
        currencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RadioButton radioButton = (RadioButton) view.findViewById(R.id.item);
                tempCurrencyName = radioButton.getText().toString();
//                currencyLabel.setText(tempCurrencyName);
            }
        });

        for(int i=0;i<listCurrency.length;i++) {
            if(listCurrency[i].equals(currencyName)){
                currencyListView.setItemChecked(i,true);
                tempCurrencyName = currencyName;
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }


    public void setCurrency() {
        String jsonStringCurrency = Utils.getStringCurrency(getActivity());
        if(jsonStringCurrency != null) {
            setStringCurrencyToVariable(jsonStringCurrency);
        }
    }

    private  void setTextCurrency() {
        //SharedPreferences prefs = this.getActivity().getSharedPreferences(idPreference, this.getActivity().MODE_PRIVATE);
        String value = Utils.getCurrentCurrency(this.getActivity());
        currencyName = getKeyFromValueCurrency(value);
        tempCurrencyName = currencyName;
        if(currencyName == null) {
            setPreferenceCurrency(defaultCurrency, true);
            currencyName = getKeyFromValueCurrency(defaultCurrency);
        }
        currencyLabel.setText(currencyName);
    }

    private String getKeyFromValueCurrency(String value) {
        for( String key : currencyList.keySet() ){
            if(currencyList.get(key).equals(value))
                return key;
        }
        return null;
    }


    // TODO: drawer menu
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.app_name, R.string.app_name);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.addDrawerListener(this);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (rootView != null) {
//            rootView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
//        if (mCallbacks != null) {
//            mCallbacks.onNavigationDrawerItemSelected(position);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        LocationModel locationModel = Utils.getLocationDetails();
        if (locationModel != null) {
            locationLabel.setText(locationModel.getCity() + ", " + locationModel.getCountry());
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        Log.d("Navigation Drawer", "onDrawerClosed");
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    public  interface NavigationDrawerCallbacks {
        void onLanguageSelected();
        void onCurrencySelected();
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void toggleDrawerLayout() {
        if(mDrawerLayout.isDrawerOpen(mFragmentContainerView))
            closeDrawer();
        else
            openDrawer();
    }

    private void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    private void openDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onDestroy() {  // cancel all asynctask before close activity
        for (AsyncTask asyncItem : allAsyncTask) {
            if(asyncItem != null && !asyncItem.isCancelled()) {
                asyncItem.cancel(true);
            }
        }
        super.onDestroy();
    }



    ///////////////////////////////////////////////////////////////////////////////
    // unused
    private void setDialogServers() {
        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View serverLayout = inflater.inflate(R.layout.help_layout, null);

        final ListView  serverListView= (ListView) serverLayout.findViewById(R.id.server_list);
        serverListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        String []serverArray = getResources().getStringArray(R.array.servers);
        ArrayAdapter<String> serverAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item,serverArray);

        serverListView.setAdapter(serverAdapter);

        int currentServer = Utils.getCurrentServer(getContext());
        serverListView.setItemChecked(currentServer,true);


        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setView(serverLayout);

        builder.setTitle(getResources().getString(R.string.select_server));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utils.setCurrentServer(getContext(),serverListView.getCheckedItemPosition());
                        dialog.cancel();
                        closeDrawer();

                    }
                });

        builder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }
}
