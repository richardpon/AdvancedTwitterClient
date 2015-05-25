package com.codepath.apps.simpletwitterclient.lib;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    public static void create(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
