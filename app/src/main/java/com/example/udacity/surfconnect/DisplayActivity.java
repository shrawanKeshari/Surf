package com.example.udacity.surfconnect;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    EditText et;
    Button ok;
    View lay;
    ProgressBar pb;
    private static String webUrl = "https://api.github.com/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et = (EditText) findViewById(R.id.edit_text);
        ok = (Button) findViewById(R.id.ok_b);
        lay = findViewById(R.id.l1);

        pb = (ProgressBar) findViewById(R.id.progress_bar1);
        pb.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        String check = intent.getStringExtra(AccountActivity.EXTRA_MESSAGE);

        if(!check.equals("GITHUB REPOS")){
            et.setVisibility(View.INVISIBLE);
            ok.setVisibility(View.INVISIBLE);
            lay.setVisibility(View.INVISIBLE);
        }
    }
}
