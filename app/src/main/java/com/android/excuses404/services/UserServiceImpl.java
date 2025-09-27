package com.android.excuses404.services;

import com.android.excuses404.data.api.model.UpdateUserRequest;
import com.android.excuses404.data.repository.UserRepository;
import com.android.excuses404.data.repository.UserServiceCallBack;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Inject
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void login(UserServiceCallBack callBack) {
    }

    @Override
    public void register(UserServiceCallBack callBack) {
    }

    @Override
    public void loginOtp(UserServiceCallBack callBack) {
    }

    @Override
    public void updateUser(String token, UpdateUserRequest updateRequest, UserServiceCallBack callBack) {
        userRepository.updateUser(token, updateRequest, callBack);
    }

    @Override
    public void getUser(String token, UserServiceCallBack callBack) {
        userRepository.getUser(token, callBack);
    }

}
