package com.perplelab.naver;

public interface PerpleNaverCafeCallback {
    public void onSdkStarted();
    public void onSdkStopped();
    public void onClickAppSchemeBanner(String appScheme);
    public void onJoined();
    public void onPostedArticle(String info);
    public void onPostedComment(int articleId);
    public void onVoted(int articleId);
    public void onScreenshotClick();
    public void onRecordFinished(String uri);
    public void onError(String info);
}
