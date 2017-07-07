package com.example.udacity.surfconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonu on 3/7/17.
 */
public class GitHubJSON {

    public static List<GithubField> parse(String content){
        try{
            JSONArray array = new JSONArray(content);
            List<GithubField> result = new ArrayList<>();

            for(int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                GithubField githubField = new GithubField();

                githubField.setId(object.getInt("id"));
                githubField.setName(object.getString("name"));
                githubField.setCreated(object.getString("created_at"));
                githubField.setUpdated(object.getString("updated_at"));
                githubField.setPushed(object.getString("pushed_at"));
                githubField.setHtmlUrl(object.getString("html_url"));

                result.add(githubField);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
