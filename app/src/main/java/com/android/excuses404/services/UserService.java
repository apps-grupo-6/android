package com.android.excuses404.services;

import com.android.excuses404.data.repository.UserServiceCallBack;

public interface UserService {
    void login(UserServiceCallBack callBack);
    void register(UserServiceCallBack callBack);
    void loginOtp(UserServiceCallBack callBack);
}
