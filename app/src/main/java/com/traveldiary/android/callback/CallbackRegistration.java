package com.traveldiary.android.callback;

import com.traveldiary.android.model.RegistrationResponse;


public interface CallbackRegistration {
    void response(RegistrationResponse registrationResponse);
    void fail(Throwable t);
}
