package com.example.udacity.surfconnect;

/**
 * Created by sonu on 3/7/17.
 */
public class GithubField {

    private int id;
    private String name;
    private String created;
    private String updated;
    private String pushed;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getCreated(){
        return created;
    }

    public void setCreated(String created){
        this.created = created;
    }

    public String getUpdated(){
        return updated;
    }

    public void setUpdated(String updated){
        this.updated = updated;
    }

    public String getPushed(){
        return pushed;
    }

    public void setPushed(String pushed){
        this.pushed = pushed;
    }
}
