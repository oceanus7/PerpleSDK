package com.perplelab.tapjoy;

public interface PerpleTapjoyPlacementCallback {
    public void onRequestSuccess();
    public void onRequestFailure(String info);
    public void onContentReady();
    public void onShow();
    public void onWait();
    public void onDismiss();
}
