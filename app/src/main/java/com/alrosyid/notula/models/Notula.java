package com.alrosyid.notula.models;

public class Notula {

    private String title, meetings_title, date, summary;
    private User user;
    private Meetings meetings;

    private int id, meetings_id, user_id;


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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMeetings_id() {
        return meetings_id;
    }

    public void setMeetings_id(int meetings_id) {
        this.meetings_id = meetings_id;
    }

    public String getMeetings_title() {
        return meetings_title;
    }

    public void setMeetings_title(String meets_title) {
        this.meetings_title = meets_title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Meetings getMeetings() {
        return meetings;
    }

    public void setMeetings(Meetings meetings) {
        this.meetings = meetings;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
