
package com.findafun.bean.categories;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetCategory {

    @SerializedName("func_name")
    @Expose
    private String funcName;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("preferences")
    @Expose
    private List<Preference> preferences = new ArrayList<Preference>();

    /**
     * 
     * @return
     *     The funcName
     */
    public String getFuncName() {
        return funcName;
    }

    /**
     * 
     * @param funcName
     *     The func_name
     */
    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    /**
     * 
     * @return
     *     The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     *     The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     * @return
     *     The preferences
     */
    public List<Preference> getPreferences() {
        return preferences;
    }

    /**
     * 
     * @param preferences
     *     The preferences
     */
    public void setPreferences(List<Preference> preferences) {
        this.preferences = preferences;
    }

}
