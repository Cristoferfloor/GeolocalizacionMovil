package com.cristofer2023.myapplication.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import com.cristofer2023.myapplication.R;

public class Mytolback {

    public static void show(AppCompatActivity activity,String title,boolean upButton ){
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
