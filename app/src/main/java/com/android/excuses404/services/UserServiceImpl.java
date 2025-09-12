package com.android.excuses404.services;

import com.android.excuses404.data.repository.UserRepository;
import com.android.excuses404.data.repository.UserServiceCallBack;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Inject
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void login(UserServiceCallBack callBack){
        userRepository.login(callBack);
    }

    @Override
    public void register(UserServiceCallBack callBack){
        //userRepository.register(callBack);
    }

    @Override
    public void loginOtp(UserServiceCallBack callBack){
        //userRepository.loginOtp(callBack);
    }

}
