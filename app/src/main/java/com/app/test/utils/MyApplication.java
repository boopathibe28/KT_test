package com.app.test.utils;

import android.app.Application;
import android.content.Context;
import com.facebook.drawee.backends.pipeline.Fresco;
import androidx.multidex.MultiDex;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("DB.realm")
                .schemaVersion(2) // Must be bumped when the schema changes
                .migration(new MyMigration()) // Migration to run
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        Fresco.initialize(this);
    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }



}
