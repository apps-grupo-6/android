package com.android.excuses404.data.repository;

import com.android.excuses404.data.api.model.UserLoginResponse;
import com.android.excuses404.models.User;

import java.util.List;

public interface UserServiceCallBack {
    void onSuccess(UserLoginResponse response);
    void onError(Throwable error);
}
