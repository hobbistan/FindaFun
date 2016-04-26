package com.findafun.serviceinterfaces;

import org.json.JSONObject;

/**
 * Created by Data Crawl 6 on 25-04-2016.
 */
public interface IForgotPasswordServiceListener {

    public void onForgotPassword(JSONObject response);

    public void onForgotPasswordError(String error);
}
