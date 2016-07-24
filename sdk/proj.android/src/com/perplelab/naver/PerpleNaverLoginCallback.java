package com.perplelab.naver;

public interface PerpleNaverLoginCallback {
    public void onSuccess(String accessToken, String refreshToken, long expiresAt, String tokenType);
    public void onFail(String errorCode, String errorDesc);
}
