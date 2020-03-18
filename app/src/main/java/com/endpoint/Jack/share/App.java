package com.endpoint.Jack.share;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.endpoint.Jack.language.Language_Helper;

public class App extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base,Language_Helper.getLanguage(base)));
    }
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(this, "SERIF", "font/Ara Hamah Kilania_0.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        TypefaceUtil.overrideFont(this, "DEFAULT", "font/Ara Hamah Kilania_0.ttf");
        TypefaceUtil.overrideFont(this, "MONOSPACE", "font/Ara Hamah Kilania_0.ttf");
        TypefaceUtil.overrideFont(this, "SANS_SERIF", "font/Ara Hamah Kilania_0.ttf");
    }

}
