package com.perplelab;

import android.content.Intent;

public interface PerpleGoogleLoginCallback {
    public void onSuccess(String idToken);
    public void onFail(String info);
    public void onCancel();
    public void onGooglePlayServicesNotAvailable(int resultCode, Intent data);
}
