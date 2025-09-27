package com.android.excuses404.data.repository;

public interface UserServiceCallBack {
    void onSuccess(Object response);

    void onError(String error);
}
