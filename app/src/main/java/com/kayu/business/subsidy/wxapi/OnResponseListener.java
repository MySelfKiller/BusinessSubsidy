package com.kayu.business.subsidy.wxapi;

public interface OnResponseListener {
    void onSuccess();

    void onCancel();

    void onFail(String message);
}
