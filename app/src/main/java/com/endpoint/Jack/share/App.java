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


}
