package com.android.excuses404.services;

import com.android.excuses404.data.api.model.UpdateUserRequest;
import com.android.excuses404.data.repository.UserServiceCallBack;

public interface UserService {
    void login(UserServiceCallBack callBack);

    void register(UserServiceCallBack callBack);

    void loginOtp(UserServiceCallBack callBack);

    void updateUser(String token, UpdateUserRequest updateRequest, UserServiceCallBack callBack);

    void getUser(String token, UserServiceCallBack callBack);
}
