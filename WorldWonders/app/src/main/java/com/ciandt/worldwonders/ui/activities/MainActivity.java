/**
 * Created by pmachado on 8/20/15.
 */
package com.ciandt.worldwonders.ui.activities;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.ciandt.worldwonders.R;
import com.ciandt.worldwonders.helpers.Helpers;
import com.ciandt.worldwonders.ui.fragments.LoginFragment;
import com.ciandt.worldwonders.ui.fragments.WorldWondersFragment;
import com.ciandt.worldwonders.model.Wonder;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addLoginFragment();

        if (Helpers.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void addLoginFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginFragment loginFragment =  new LoginFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_login, loginFragment, "login")
                .commit();

        loginFragment.setOnLoginListener(new LoginFragment.OnLoginListener() {
            @Override
            public void onLogin(Wonder wonder) {
                addWondersFragment();
            }
        });
    }

    private void addWondersFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        WorldWondersFragment worldWondersFragment =  WorldWondersFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_login, worldWondersFragment, "login")
                .commit();
    }
}
