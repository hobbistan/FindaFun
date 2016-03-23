package com.findafun.serviceinterfaces;

/**
 * Created by BXDC46 on 1/22/2016.
 */
public interface IGamificationServiceListener {
    public void onSuccess(int resultCode,Object result);
    public void onError(String erorr);
}
