package com.android.excuses404.core.di;

import android.content.Context;

import com.android.excuses404.core.repository.TokenRepository;
import com.android.excuses404.core.repository.TokenRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class TokenModule {

    @Provides
    @Singleton
    public TokenRepository provideTokenRepository(@ApplicationContext Context context) {
        return new TokenRepositoryImpl(context);
    }
}
