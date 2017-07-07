package com.example.udacity.surfconnect;

/**
 * Created by sonu on 4/7/17.
 */
public class FacebookField {

    private String name;
    private String relationship;
    private String time;
    private String pic;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getRelationship(){
        return relationship;
    }

    public void setRelationship(String relationship){
        this.relationship = relationship;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getPic(){
        return pic;
    }

    public void setPic(String pic){
        this.pic = pic;
    }
}
