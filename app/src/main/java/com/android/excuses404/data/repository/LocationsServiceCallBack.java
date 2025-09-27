package com.android.excuses404.data.repository;

import com.android.excuses404.data.api.model.DisciplinesResponse;

public interface LocationsServiceCallBack {
    void onSuccess(DisciplinesResponse response);
    void onError(Throwable error);
}
