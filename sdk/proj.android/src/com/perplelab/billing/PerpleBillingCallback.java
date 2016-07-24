package com.perplelab.billing;

public interface PerpleBillingCallback {
    public void onPurchase(String info);
    public void onError(String info);
}
