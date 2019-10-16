package th.co.apps360.eat360.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import th.co.apps360.eat360.ConfigURL;
import th.co.apps360.eat360.R;
import th.co.apps360.eat360.SetDefaultFont;
import th.co.apps360.eat360.Utils;

/**
 * Created by jakkrit.p on 16/09/2015.
 */
public class AboutActivity extends AppCompatActivity{

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetDefaultFont.setDefaultFont(this, "MONOSPACE", "OpenSans-Regular.ttf");
        setContentView(R.layout.activity_about);

        initActionbar();
        initData();
    }

    private void initData() {
        try {
            PackageInfo pInfo  = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
//            version.setText(getString(R.string.version)+" "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Utils.setDebug("crash", e.getLocalizedMessage());
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
