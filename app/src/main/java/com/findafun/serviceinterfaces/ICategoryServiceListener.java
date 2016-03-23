package com.findafun.serviceinterfaces;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by zahid.r on 10/30/2015.
 */
public interface ICategoryServiceListener {
    public void onCategoryResponse(JSONArray response);
    public void onCategoryError(String error);

    public void onSetCategoryResponse(JSONObject response);
    public void onSetCategoryError(String error);
}
