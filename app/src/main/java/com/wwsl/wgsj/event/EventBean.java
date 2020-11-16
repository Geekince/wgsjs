package com.wwsl.wgsj.event;

import org.json.JSONObject;

public class EventBean {

    public final String BOTTOM_HIDE = "bottom_hide";
    public final String BOTTOM_SHOW = "bottom_show";

    private String from;
    private String to;
    private String action;
    private JSONObject data;

    public EventBean(String action) {
        this.action = action;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
