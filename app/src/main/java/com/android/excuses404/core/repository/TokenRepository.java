package com.android.excuses404.core.repository;

public interface TokenRepository {
    void saveToken(String token);

    String getToken();

    void clearToken();

    boolean hasToken();

    void saveUserId(int userId);

    int getUserId();

    void saveLoginStatus(boolean isLoggedIn);

    boolean isLoggedIn();

    void clearAll();
}