package com.findafun.serviceinterfaces;

import org.json.JSONObject;

/**
 * Created by Data Crawl 6 on 18-05-2016.
 */
public interface IStaticEventServiceListener {

    public void onEventResponse(JSONObject response);

    public void onEventError(String error);
}
