package com.sagar;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

public class AppManager extends Application {

    private static AppModels appModels;

    @Override
    public void onCreate() {
        super.onCreate();
        initModels();
        initStetho();
    }

    private void initModels() {
        appModels = new AppModels(this);
    }

    private void initStetho() {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);
    }

    public static AppModels getAppModels() {
        return appModels;
    }
}
