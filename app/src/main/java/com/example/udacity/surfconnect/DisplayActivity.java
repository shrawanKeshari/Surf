package com.example.udacity.surfconnect;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    EditText et;
    TextView tv;
    List<MyTask> tasks;
    List<GithubField> ouputList;
    Button ok;
    View lay;
    ProgressBar pb;
    String sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv = (TextView) findViewById(R.id.text_view);
        et = (EditText) findViewById(R.id.edit_text);
        ok = (Button) findViewById(R.id.ok_b);
        lay = findViewById(R.id.l1);

        pb = (ProgressBar) findViewById(R.id.progress_bar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("");
                if (isOnline()) {
                    sb = et.getText().toString();
                    if (check_username(sb)) {
                        Toast.makeText(DisplayActivity.this, "Enter valid username",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("message", sb);
                        requestData("https://api.github.com/users/" + sb + "/repos");
                    }
                } else {
                    Toast.makeText(DisplayActivity.this, "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent intent = getIntent();
        String check = intent.getStringExtra(AccountActivity.EXTRA_MESSAGE);

        if (!check.equals("GITHUB REPOS")) {
            et.setVisibility(View.INVISIBLE);
            ok.setVisibility(View.INVISIBLE);
            lay.setVisibility(View.INVISIBLE);
        }
    }

    private boolean check_username(String sb) {
        if (sb.equals("")) {
            return true;
        } else if (sb.charAt(0) == '-' || sb.charAt(sb.length() - 1) == '-') {
            return true;
        } else if (!sb.matches("^[a-zA-Z0-9-]*$")) {
            return true;
        }
        return false;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        tv.append("UserName: " + sb + "\n");
        if (ouputList != null) {
            for (GithubField str : ouputList) {
                tv.append("Name: " + str.getName() + "\n");
                tv.append("Id: " + str.getId() + "\n");
                tv.append("Created At: " + str.getCreated() + "\n");
                tv.append("Updated At: " + str.getUpdated() + "\n");
                tv.append("Pushed At: " + str.getPushed() + "\n");
                tv.append("\n");
            }
        }
        et.setText("");
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... params) {
            String content = HTTPManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(DisplayActivity.this, "Repository Not Exist or " +
                        "Check Your Internet Access", Toast.LENGTH_LONG).show();
                return;
            }
            if (result.equals("")) {
                Toast.makeText(DisplayActivity.this, "Repository Is Empty", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            ouputList = GitHubJSON.parse(result);
            updateDisplay();
        }

//        @Override
//        protected void onProgressUpdate(String... values) {
//            updateDisplay(values[0]);
//        }
    }
}
