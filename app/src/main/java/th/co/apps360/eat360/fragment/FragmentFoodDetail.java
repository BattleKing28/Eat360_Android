package th.co.apps360.eat360.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.ConfigURLs;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.Utils;
import th.co.apps360.eat360.activity.MainActivity;
import th.co.apps360.eat360.activity.MenuActivity;

/**
 * Created by jakkrit.p on 2/07/2015.
 */
public class FragmentFoodDetail extends Fragment {

    private String TAG = "FragmentFoodDetail";

    private ImageView foodImage;
    private TextView restNameView;
    private TextView description;
    private TextView ingredient;
    private ImageView translate;
    private TextView descriptionTitle;
    private TextView ingredientTitle;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView price;
    private TextView noResult;
    private ImageView mBackButton;
    private TextView mTitleTextView;

    private String mFoodName;
    private String mFoodDescription;
    private String mFoodPrice;
    private String mFoodImagePath;
    private String mFoodId;
    private String mSourceActivty;
    private String mNativeRestLanguage;
    private boolean mTranslateNative;

    private ImageView categoryImageView;
    private String mRestaurantId;
    private String mRestaurantName;
//    private ShareButton shareFacebook;
    private String imageUrl;
    private ScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food_detail, container, false);
        foodImage = (ImageView) rootView.findViewById(R.id.image_food);
        restNameView = (TextView) rootView.findViewById(R.id.rest_name);
        description = (TextView) rootView.findViewById(R.id.description);
        ingredient = (TextView) rootView.findViewById(R.id.ingredients);
        price = (TextView) rootView.findViewById(R.id.price);
        categoryImageView = (ImageView) rootView.findViewById(R.id.category_iv);
//        shareFacebook  = (ShareButton) view.findViewById(R.id.share_facebook);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh_layout);
        descriptionTitle = (TextView) rootView.findViewById(R.id.description_title);
        ingredientTitle = (TextView) rootView.findViewById(R.id.ingredients_title);
        translate  = (ImageView) rootView.findViewById(R.id.translate);
        noResult = (TextView) rootView.findViewById(R.id.no_result);
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);

//        rootView.setFocusableInTouchMode(true);
//        rootView.requestFocus();
//        rootView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    onBackPressed();
//                    return true;
//                }
//                return false;
//            }
//        });

        initData();
        downloadFoodInfo();
        initListener();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initData() {
        mNativeRestLanguage = "en";
        mTranslateNative = false;
        mFoodId = this.getArguments().getString("foodId");
        mFoodPrice = this.getArguments().getString("price");
        mFoodName = this.getArguments().getString("foodName");
        mFoodImagePath = this.getArguments().getString("foodImage");
        mRestaurantId = this.getArguments().getString("restaurantId");
        mRestaurantName = this.getArguments().getString("restaurantName");
        mSourceActivty = this.getArguments().getString("sourceActivity");

        String sPrice = mFoodPrice + " " + Utils.getCurrentCurrency(getActivity()).toUpperCase();
        price.setText(sPrice);
        restNameView.setText(mRestaurantName);
        setTitleActionbar();
        downloadFoodImage(mFoodId);

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
    }

    private void downloadFoodInfo() {
        showDownloadingAnimation();
        descriptionTitle.setText(getString(R.string.Description));
        ingredientTitle.setText(getString(R.string.ingredients));
        if (mTranslateNative) {
            downloadFoodDetail(mFoodId, mNativeRestLanguage);
            downloadFoodIngredients(mFoodId, mNativeRestLanguage);
        } else {
            downloadFoodDetail(mFoodId, Utils.getCurrentLanguage(getActivity()));
            downloadFoodIngredients(mFoodId, Utils.getCurrentLanguage(getActivity()));
        }
    }

    public void downloadFoodDetailFromLanguage() {
        mTranslateNative = false;
        downloadFoodInfo();
    }

    private void downloadFoodImage(String foodId) {
        String url = ConfigURLs.foodImageURL + "/" + foodId;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject jObject = response.getJSONObject("Results");
                    String imagePath = jObject.getString("img_path");
                    Utils.setImageWithPicasso(getActivity(), imagePath,foodImage);
                    hideDownloadingAnimation();
                } catch (JSONException e) {
                    hideDownloadingAnimation();
                    foodImage.setImageResource(R.drawable.no_image_small);
                    Utils.setDebug(TAG, "failed to get food image: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                hideDownloadingAnimation();
                foodImage.setImageResource(R.drawable.no_image_small);
                Utils.setDebug(TAG, "failed to get food image: " + res);
            }
        });

    }

    private void downloadFoodDetail(String foodId, String selLanguage) {
        String url = ConfigURLs.foodDetailURL;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        params.put("FoodID", foodId);
        params.put("LanguageID", selLanguage);
        params.put("FoodCurrency", Utils.getCurrentCurrency(getActivity()));

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject obj = response.getJSONObject("Results");
                    mFoodDescription = obj.getString("description");
                    mNativeRestLanguage = obj.getString("native_lang");
                    description.setText(mFoodDescription);
                    mFoodPrice = obj.getString("price");
                    price.setText(mFoodPrice + " " + Utils.getCurrentCurrency(getActivity()).toUpperCase());
                    hideDownloadingAnimation();
                } catch (JSONException e) {
                    mFoodDescription = "No description";
                    description.setText(mFoodDescription);
                    Utils.setDebug(TAG, "failed to get food desc: " + e.getLocalizedMessage());
                    hideDownloadingAnimation();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                mFoodDescription = "No description";
                description.setText(mFoodDescription);
                Utils.setDebug(TAG, "failed to get food desc: " + res);
                hideDownloadingAnimation();
            }
        });

    }

    private void downloadFoodIngredients(String foodId, String selLanguage) {
        String url = ConfigURLs.foodIngredientsURL + "/" + foodId;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        params.put("FoodID", foodId);
        params.put("LanguageID", selLanguage);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String ingredients = "";
                    JSONObject obj = response.getJSONObject("Results");
                    JSONArray ingredientsArray = obj.getJSONArray("ingredients");
                    for (int i = 0; i < ingredientsArray.length(); i ++) {
                        JSONObject name =ingredientsArray.getJSONObject(i);
                        ingredients += name.getString("name") ;
                        if (i !=ingredientsArray.length()-1)
                            ingredients += ", ";
                    }
                    ingredient.setText(ingredients);
                    hideDownloadingAnimation();
                } catch (JSONException e) {
                    ingredient.setText("No ingredients");
                    Utils.setDebug(TAG, "failed to get food ingredients: " + e.getLocalizedMessage());
                    hideDownloadingAnimation();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                ingredient.setText("No ingredients");
                Utils.setDebug(TAG, "failed to get food ingredients: " + res);
                hideDownloadingAnimation();
            }
        });

    }


    public void setTitleActionbar() {
        if (mSourceActivty.equals("MainActivity")) {
            mTitleTextView = ((MainActivity) getActivity()).showTitle();
            String sTitle = "";
            if (null != mFoodName && !"".equals(mFoodName)) {
                sTitle = mFoodName;
            }
            mTitleTextView.setText(sTitle);
            mBackButton = ((MainActivity) getActivity()).showBackButton();
            mBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        else if (mSourceActivty.equals("MenuActivity")) {
            mTitleTextView = ((MenuActivity) getActivity()).showTitle();
            String sTitle = "";
            if (null != mFoodName && !"".equals(mFoodName)) {
                sTitle = mFoodName;
            }
            mTitleTextView.setText(sTitle);
            mBackButton = ((MenuActivity) getActivity()).showBackButton();
            mBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    private void onBackPressed() {
        getFragmentManager().popBackStack();
    }

    private void initListener() {
//        final ShareDialog shareDialog = new ShareDialog(this.getActivity());
//        shareFacebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ShareDialog.canShow(ShareLinkContent.class)) {
//
//                    String url = new ConfigURL(getContext()).shareFacebookUrl+"index.php/restaurant/"+mRestaurantId+"/menu/"+mFoodId;
//                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                            .setContentTitle(mFoodName)
//                            .setContentDescription(mFoodDescription)
//                            .setContentUrl(Uri.parse(url))
//                            .setImageUrl(Uri.parse(imageUrl))
//                            .build();
//                    shareDialog.show(linkContent);
//                }
//
//            }
//        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadFoodInfo();
            }
        });
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTranslateNative = !mTranslateNative;
                downloadFoodInfo();
            }
        });
    }

    public void changeCurrency() {
        if (mTranslateNative) {
            downloadFoodDetail(mFoodId, mNativeRestLanguage);
        } else {
            downloadFoodDetail(mFoodId, Utils.getCurrentLanguage(getActivity()));
        }
    }

    private void showDownloadingAnimation() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void hideDownloadingAnimation() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 600);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSourceActivty.equals("MainActivity")) {
            ((MainActivity) getActivity()).currentFragment = this;
        }
        else if (mSourceActivty.equals("MenuActivity")) {
            ((MenuActivity) getActivity()).currentFragment = this;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
