package com.alrosyid.notula.models;

public class Attendances {
    private int id, user_id, meetings_id;
    private String name, position;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getMeetings_id() {
        return meetings_id;
    }

    public void setMeetings_id(int meetings_id) {
        this.meetings_id = meetings_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


}
