package com.araditc.uploader;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.araditc.uploader.data.databse.UploadModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppConfig {
    private static AppConfig instance;
    public static Application application;

    public static volatile Handler applicationHandler;
    public static Realm farmRealm;

    public static synchronized void start(Application application) {
        AppConfig.application = application;
    }

    public static synchronized AppConfig newInstance() {
        if (instance == null) instance = new AppConfig();

        return instance;
    }

    private AppConfig() {
//        configRealm();
        applicationHandler = new Handler(application.getMainLooper());
    }

    private void configRealm() {
        Realm.init(application);

        RealmConfiguration farmAnimalsConfig = new RealmConfiguration.Builder()
                .name(BuildConfig.DB_NAME)
                .schemaVersion(BuildConfig.SCHEMA_VERSION)
                .modules(Realm.getDefaultModule(), new UploadModule())
                .build();

        farmRealm = Realm.getInstance(farmAnimalsConfig);

//        Realm.init(application);
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name(BuildConfig.DB_NAME)
//                .schemaVersion(BuildConfig.SCHEMA_VERSION)
//                .addModule(new uploader())
//                .deleteRealmIfMigrationNeeded()
//                .build();
//
//        Realm.setDefaultConfiguration(config);
    }

    public void configRealm(Context context) {
        Realm.init(context);

        RealmConfiguration farmAnimalsConfig = new RealmConfiguration.Builder()
                .name(BuildConfig.DB_NAME)
                .schemaVersion(BuildConfig.SCHEMA_VERSION)
                .modules(Realm.getDefaultModule(), new UploadModule())
                .build();

        farmRealm = Realm.getInstance(farmAnimalsConfig);
    }

    public String getApplicationName() {
        return application.getApplicationInfo().loadLabel(application.getPackageManager()).toString();
    }

    public Drawable getApplicationIcon() {

        return application.getPackageManager().getApplicationIcon(application.getApplicationInfo());

    }
}
