package com.sagar.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

public class UIUtil {

    public static void showMessage(Activity activity, String msg) {
        View contentView = activity.findViewById(android.R.id.content);
        Snackbar.make(contentView, msg, Snackbar.LENGTH_LONG).show();
    }

}
