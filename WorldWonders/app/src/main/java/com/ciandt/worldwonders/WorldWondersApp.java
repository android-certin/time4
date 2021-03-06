package com.ciandt.worldwonders;

import android.app.Application;
import android.util.Log;

import com.ciandt.worldwonders.database.WondersSQLiteHelper;
import com.facebook.stetho.Stetho;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by jfranco on 8/24/15.
 */
public class WorldWondersApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initDatabase();

        initStetho();

        initCalligraphy();

    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Thin.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    private void initDatabase() {
        if (!WondersSQLiteHelper.checkOpenDatabase()) {
            try {
                WondersSQLiteHelper.createDatabase(this);
            } catch (IOException e) {
                Log.i("WorldWondersApp", "Falha ao criar base de dados", e);
            }
        }
    }
}
