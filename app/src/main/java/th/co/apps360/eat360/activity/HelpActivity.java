package th.co.apps360.eat360.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;

public class HelpActivity extends AppCompatActivity {

    private ImageView helpImageView;
    private TextView helpLoadingView;
    private WebView helpWebView;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initActionbar();
        helpImageView = (ImageView)findViewById(R.id.help_imageview);
        helpLoadingView = (TextView)findViewById(R.id.help_loading_text);
        helpWebView = (WebView)findViewById(R.id.help_webview);
        helpWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                helpWebView.setVisibility(View.VISIBLE);
                helpLoadingView.setVisibility(View.GONE);
                helpImageView.setVisibility(View.GONE);
            }
        });
        String url = ConfigURL.shareFacebookUrl + "help";

        helpWebView.getSettings().setLoadsImagesAutomatically(true);
        helpWebView.getSettings().setJavaScriptEnabled(true);
        helpWebView.getSettings().setAppCacheEnabled(true);
        helpWebView.getSettings().setDomStorageEnabled(true);
        helpWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        helpWebView.loadUrl(url);

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
