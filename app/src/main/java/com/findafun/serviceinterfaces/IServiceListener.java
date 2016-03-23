package com.findafun.serviceinterfaces;

/**
 * Created by BXDC46 on 2/1/2016.
 */
public interface IServiceListener {
    public void onSuccess(int resultCode,Object result);
    public void onError(String erorr);
}
