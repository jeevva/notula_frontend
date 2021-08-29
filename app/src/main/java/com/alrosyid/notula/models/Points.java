package com.alrosyid.notula.models;

public class Points {
    private int id, user_id, notulas_id;
    private String points;

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

    public int getNotulas_id() {
        return notulas_id;
    }

    public void setNotulas_id(int notulas_id) {
        this.notulas_id = notulas_id;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }


}
