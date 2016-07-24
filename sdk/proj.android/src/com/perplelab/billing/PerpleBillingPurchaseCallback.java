package com.perplelab.billing;

public interface PerpleBillingPurchaseCallback {
    public void onSuccess(String info);
    public void onFail(String info);
    public void onCancel(String info);
}
