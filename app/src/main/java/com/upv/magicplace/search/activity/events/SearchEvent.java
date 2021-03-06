package com.upv.magicplace.search.activity.events;

import com.upv.magicplace.search.entities.Result;

import java.util.List;

public class SearchEvent {

    private int type;
    private List<Result> data;
    private String message;

    public static final int SUCCESS_EVENT = 0;
    public static final int ERROR = 1;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Result> getData() {
        return data;
    }

    public void setData(List<Result> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
