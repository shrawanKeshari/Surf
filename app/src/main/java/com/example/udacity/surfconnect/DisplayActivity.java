package com.example.udacity.surfconnect;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DisplayActivity extends AppCompatActivity {

    EditText et;
    TextView tv;
    List<MyTask> tasks;
    List<GithubField> outputList;
    List<FacebookField> fbresult;
    Button ok;
    ProgressBar pb;
    String sb, check_permission, display_permission,check;
    CallbackManager callbackManager;
    ArrayList<String> fieldList;

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
                        requestData("https://api.github.com/users/" + sb + "/repos");
                    }
                } else {
                    Toast.makeText(DisplayActivity.this, "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();

        Intent intent = getIntent();
        check = intent.getStringExtra(AccountActivity.EXTRA_MESSAGE);

        if (!check.equals("GITHUB REPOS")) {
            ok.setVisibility(View.INVISIBLE);
            if (AccessToken.getCurrentAccessToken() == null){
                //Facebook login access token is required
                finish();
                return;
            }

            et.setText(check);
            et.setFocusable(false);
            tv.setText("");
            fieldList = new ArrayList<>();
            if(check.equals("FAMILY")){
                check_permission = "user_relationships";
                display_permission = "Family Relationship";
                fieldList.add("family");
                fieldList.add("name");
                fieldList.add("relationship");
            }

            if(check.equals("TAGGED PLACES")){
                check_permission = "user_tagged_places";
                display_permission = "Tagged Places";
                fieldList.add("tagged_places");
                fieldList.add("place");
            }
            //check for required permissions
            Log.d("message",fieldList.get(0)+" "+fieldList.get(1));
            Set permissions = AccessToken.getCurrentAccessToken().getPermissions();
            if(permissions.contains(check_permission)){
                fetchData();
            }else{
                //prompt user to grant permissions
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        fetchData();
                    }

                    @Override
                    public void onCancel() {
                        //inform user that permission is required
                        String permissionMsg = "Surf requires permission to access your"+
                                display_permission;
                        Toast.makeText(DisplayActivity.this, permissionMsg, Toast.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

                loginManager.logInWithReadPermissions(this, Arrays.asList(check_permission));
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Forward result to the callback manager for Login Button
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fetchData() {
        //make the API call to fetch family relationship list
        StringBuilder field_parameter = new StringBuilder("");
        int n = fieldList.size();
        for(int i = 1; i < n; i++){
            field_parameter.append(fieldList.get(i));
            if(i != (n-1)){
                field_parameter.append(",");
            }
        }

        final String check_permission = fieldList.get(0);

        Bundle parameters = new Bundle();
        parameters.putString("fields",field_parameter.toString());
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/"+check_permission,
                parameters, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(response.getError() != null){
                            //display error message
                            Toast.makeText(DisplayActivity.this,
                                    response.getError().getErrorMessage(), Toast.LENGTH_LONG)
                                    .show();
                        }

                        fbresult = new ArrayList<>();
                        //parse json data
                        JSONObject jsonResponse = response.getJSONObject();
                        try{
                            JSONArray jsonData = jsonResponse.getJSONArray("data");
                            for(int i = 0; i < jsonData.length(); i++){
                                JSONObject jsonUser = jsonData.getJSONObject(i);
                                FacebookField ff = new FacebookField();

                                if(check_permission.equals("family")){
                                    ff.setName(jsonUser.getString("name"));
                                    ff.setRelationship(jsonUser.getString("relationship"));
                                }else if(check_permission.equals("tagged_places")){
                                    ff.setName(jsonUser.getJSONObject("place").getString("name"));
                                }

                                fbresult.add(ff);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        updateFBDisplay();

                    }
                }).executeAsync();
    }

    private void updateFBDisplay() {
        if(fbresult != null){
            for (FacebookField ff : fbresult){
                tv.append("Name: " + ff.getName() + "\n");
                if(fieldList.get(0).equals("family")){
                    tv.append("Relationship: " + ff.getRelationship() + "\n");
                }
                tv.append("\n");
            }
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
        if (outputList != null) {
            for (GithubField str : outputList) {
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

            outputList = GitHubJSON.parse(result);
            updateDisplay();
        }
    }
}
