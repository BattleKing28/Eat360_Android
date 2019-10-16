package th.co.apps360.eat360.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.SetDefaultFont;

/**
 * Created by jakkrit.p on 16/09/2015.
 */
public class MoreAppActivity extends AppCompatActivity {

    private ImageView moreImageView;
    private TextView moreLoadingView;
    private WebView moreWebView;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        SetDefaultFont.setDefaultFont(this, "MONOSPACE", "OpenSans-Regular.ttf");
        setContentView(R.layout.activity_moreapp);

        initActionbar();
        moreImageView = (ImageView)findViewById(R.id.moreapp_imageview);
        moreLoadingView = (TextView)findViewById(R.id.moreapp_loading_text);
        moreWebView = (WebView)findViewById(R.id.moreapp_webview);
        moreWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                moreImageView.setVisibility(View.GONE);
                moreLoadingView.setVisibility(View.GONE);
                moreWebView.setVisibility(View.VISIBLE);
            }
        });
        String url = ConfigURL.shareFacebookUrl;

        moreWebView.getSettings().setLoadsImagesAutomatically(true);
        moreWebView.getSettings().setJavaScriptEnabled(true);
        moreWebView.getSettings().setAppCacheEnabled(true);
        moreWebView.getSettings().setDomStorageEnabled(true);
        moreWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        moreWebView.loadUrl(url);
    }

    private void initActionbar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setCustomView(R.layout.actionbar);
        }
        ImageView logoImageView = (ImageView) mActionBar.getCustomView().findViewById(R.id.nav_logo);
        logoImageView.setVisibility(View.VISIBLE);

        TextView titleTextView = (TextView) mActionBar.getCustomView().findViewById(R.id.nav_title);
        titleTextView.setVisibility(View.GONE);

        ImageButton nav_menu = (ImageButton) findViewById(R.id.nav_menu);
        nav_menu.setVisibility(View.GONE);

        ImageView backButton = (ImageView) mActionBar.getCustomView().findViewById(R.id.nav_back);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}
