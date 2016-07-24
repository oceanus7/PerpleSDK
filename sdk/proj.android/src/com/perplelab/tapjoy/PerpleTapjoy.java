package com.perplelab.tapjoy;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.perplelab.PerpleSDK;
import com.perplelab.PerpleSDKCallback;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJAwardCurrencyListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJSpendCurrencyListener;
import com.tapjoy.Tapjoy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

public class PerpleTapjoy implements TJPlacementListener {
    private static final String LOG_TAG = "PerpleSDK Tapjoy";

    private static Activity sMainActivity;

    private boolean mIsInit;

    private boolean mIsTrackPurchase;

    private Handler mAppHandler;
    private HashMap<String, TJPlacement> mPlacement;
    private HashMap<TJPlacement, PerpleTapjoyPlacementCallback> mSetPlacementCallback;
    private HashMap<TJPlacement, PerpleTapjoyPlacementCallback> mShowPlacementCallback;

    public PerpleTapjoy(Activity activity) {
        sMainActivity = activity;
    }

    public void init(String appKey, String senderId, boolean isDebug) {
        Log.d(LOG_TAG, "Initializing Tapjoy.");

        Tapjoy.connect(sMainActivity.getApplicationContext(), appKey);

        if (isDebug) {
            Tapjoy.setDebugEnabled(true);
        }

        if (!senderId.isEmpty()) {
            Tapjoy.setGcmSender(senderId);
        }

        mAppHandler = new Handler();

        mPlacement = new HashMap<String, TJPlacement>();
        mSetPlacementCallback = new HashMap<TJPlacement, PerpleTapjoyPlacementCallback>();
        mShowPlacementCallback = new HashMap<TJPlacement, PerpleTapjoyPlacementCallback>();

        mIsInit = true;
    }

    public void onStart() {
        if (mIsInit) {
            Tapjoy.onActivityStart(sMainActivity);
        }
    }

    public void onStop() {
        if (mIsInit) {
            Tapjoy.onActivityStop(sMainActivity);
        }
    }

    public void onDestroy() {
        if (mIsInit) {
            mPlacement.clear();
            mSetPlacementCallback.clear();
            mShowPlacementCallback.clear();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Tapjoy is not initialized.");
            return;
        }

        if (mIsTrackPurchase && PerpleSDK.getBillingService() != null) {
            if (requestCode == PerpleSDK.RC_GOOGLE_PURCHASE_REQUEST ||
                requestCode == PerpleSDK.RC_GOOGLE_SUBSCRIPTION_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    trackPurchase(data);
                }
            }
        }
    }

    public void setTrackPurchase(boolean isTrackPurchase) {
        mIsTrackPurchase = isTrackPurchase;
    }

    private void trackPurchase(Intent data) {
        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
        String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

        // get reciept here.
        try {
            JSONObject purchaseDataJson = new JSONObject(purchaseData);
            String productId = purchaseDataJson.getString("productId");

            // getSkuDetails
            ArrayList<String> skuList = new ArrayList<String> ();
            skuList.add(productId);
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
            Bundle skuDetails = PerpleSDK.getBillingService().getSkuDetails(3, sMainActivity.getPackageName(), "inapp", querySkus);
            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

            Tapjoy.trackPurchase(responseList.get(0), purchaseData, dataSignature, null);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setPlacement(final String placementName, final PerpleTapjoyPlacementCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Tapjoy is not initialized.");
            callback.onError(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        final PerpleTapjoy myInstance = this;

        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                TJPlacement p = mPlacement.get(placementName);

                if (p == null) {
                    p = new TJPlacement(sMainActivity.getApplicationContext(), placementName, myInstance);
                    mPlacement.put(placementName, p);
                    mSetPlacementCallback.put(p, callback);
                }

                p.requestContent();
            }
        });
    }

    public void showPlacement(final String placementName, final PerpleTapjoyPlacementCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Tapjoy is not initialized.");
            callback.onError(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                TJPlacement p = mPlacement.get(placementName);

                mShowPlacementCallback.put(p, callback);

                if (p != null) {
                    if (p.isContentReady()) {
                        p.showContent();
                    } else {
                        callback.onWait();
                    }
                } else {
                    Log.e(LOG_TAG, "Tapjoy placement is not set - name:" + placementName);
                    callback.onError(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTSETPLACEMENT, "Tapjoy placement is not set."));
                }
            }
        });
    }

    public void getCurrency(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Tapjoy is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                Tapjoy.getCurrencyBalance(new TJGetCurrencyBalanceListener() {
                    @Override
                    public void onGetCurrencyBalanceResponse(String currencyName, int balance) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("currencyName", currencyName);
                            obj.put("balance", String.valueOf(balance));
                            callback.onSuccess(obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, e.toString()));
                        }
                    }
                    @Override
                    public void onGetCurrencyBalanceResponseFailure(String error) {
                        callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_GETCURRENCY, error));
                    }
                });
            }
        });
    }

    public void setEarnedCurrencyCallback(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Tapjoy is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
                    @Override
                    public void onEarnedCurrency(String currencyName, int amount) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("currencyName", currencyName);
                            obj.put("amount", String.valueOf(amount));
                            callback.onSuccess(obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, e.toString()));
                        }
                    }
                });
            }
        });
    }

    public void spendCurrency(final int amount, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Tapjoy is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                Tapjoy.spendCurrency(amount, new TJSpendCurrencyListener() {
                    @Override
                    public void onSpendCurrencyResponse(String currencyName, int balance) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("currencyName", currencyName);
                            obj.put("balance", String.valueOf(balance));
                            callback.onSuccess(obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, e.toString()));
                        }
                    }
                    @Override
                    public void onSpendCurrencyResponseFailure(String error) {
                        callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_SPENDCURRENCY, error));
                    }
                });
            }
        });
    }

    public void awardCurrency(final int amount, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Tapjoy is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                Tapjoy.awardCurrency(amount, new TJAwardCurrencyListener() {
                    @Override
                    public void onAwardCurrencyResponse(String currencyName, int balance) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("currencyName", currencyName);
                            obj.put("balance", String.valueOf(balance));
                            callback.onSuccess(obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, e.toString()));
                        }
                    }
                    @Override
                    public void onAwardCurrencyResponseFailure(String error) {
                        callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_AWARDCURRENCY, error));
                    }
                });
            }
        });
    }

    public void setEvent(final String id, final String arg0, final String arg1) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Tapjoy is not initialized.");
            return;
        }

        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                if (id.equals("userID")) {
                    Tapjoy.setUserID(arg0);
                } else if (id.equals("userLevel")) {
                    Tapjoy.setUserLevel(Integer.parseInt(arg0));
                } else if (id.equals("userFriendCount")) {
                    Tapjoy.setUserFriendCount(Integer.parseInt(arg0));
                } else if (id.equals("appDataVersion")) {
                    Tapjoy.setAppDataVersion(arg0);
                } else if (id.equals("customCohort")) {
                    Tapjoy.setUserCohortVariable(Integer.parseInt(arg0), arg1);
                } else if (id.equals("trackEvent")) {
                    if (!arg0.equals("")) {
                        String[] array = arg0.split(";");
                        if (array.length == 4) {
                            // category;name;parameter1;parameter2
                            Tapjoy.trackEvent(array[0], array[1], array[2], array[3]);
                        } else if (array.length == 5) {
                            // category;name;parameter1;parameter2;value
                            Tapjoy.trackEvent(array[0], array[1], array[2], array[3], Long.parseLong(array[4]));
                        } else if (array.length == 6) {
                            // category;name;parameter1;parameter2;valueName;value
                            Tapjoy.trackEvent(array[0], array[1], array[2], array[3], array[4], Long.parseLong(array[5]));
                        } else if (array.length == 8) {
                            // category;name;parameter1;parameter2;value1Name;value1;value2Name;value2
                            Tapjoy.trackEvent(array[0], array[1], array[2], array[3], array[4], Long.parseLong(array[5]), array[6], Long.parseLong(array[7]));
                        } else if (array.length == 10) {
                            // category;name;parameter1;parameter2;value1Name;value1;value2Name;value2;value3Name;value3
                            Tapjoy.trackEvent(array[0], array[1], array[2], array[3], array[4], Long.parseLong(array[5]), array[6], Long.parseLong(array[7]), array[8], Long.parseLong(array[9]));
                        }
                    }
                } else if (id.equals("trackPurchase")) {
                    if (!arg0.equals("")) {
                        String[] array = arg0.split(";");
                        if (array.length == 3) {
                            // productId;currencyCode;price
                            Tapjoy.trackPurchase(array[0], array[1], Double.parseDouble(array[2]), null);
                        } else if (array.length == 4) {
                            // productId;currencyCode;price;campaignId
                            Tapjoy.trackPurchase(array[0], array[1], Double.parseDouble(array[2]), array[3]);
                        }
                    } else if (!arg1.equals("")) {
                        try {
                            JSONObject obj = new JSONObject(arg1);
                            String skuDetails = obj.getString("skuDetails");
                            String purchaseData = obj.getString("purchaseData");
                            String dataSignature = obj.getString("dataSignature");
                            String campaignId = obj.getString("campaignId");
                            // skuDetails,purchaseData,dataSignature,campaignId
                            Tapjoy.trackPurchase(skuDetails, purchaseData, dataSignature, campaignId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onRequestSuccess(TJPlacement p) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Tapjoy, onRequestSuccess - placement:" + p.getName());
        }

        PerpleTapjoyPlacementCallback callback = mSetPlacementCallback.get(p);
        if (callback != null) {
            callback.onRequestSuccess();
        }
    }

    @Override
    public void onRequestFailure(TJPlacement p, TJError error) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Tapjoy, onRequestFailure - placement:" + p.getName() +
                    ", code:" + String.valueOf(error.code) +
                    ", message:" + error.message);
        }

        PerpleTapjoyPlacementCallback callback = mSetPlacementCallback.get(p);
        if (callback != null) {
            callback.onRequestFailure(PerpleSDK.getErrorInfo(String.valueOf(error.code), error.message));
        }
    }

    @Override
    public void onContentReady(TJPlacement p) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Tapjoy, onContentReady - placement:" + p.getName());
        }

        PerpleTapjoyPlacementCallback callback = mSetPlacementCallback.get(p);
        if (callback != null) {
            callback.onContentReady();
        }
    }

    @Override
    public void onPurchaseRequest(TJPlacement p, TJActionRequest request, String productId) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Tapjoy, onPurchaseRequest - placement:" + p.getName() +
                    ", request:" + request.toString() +
                    ", productId:" + productId);
        }

        PerpleTapjoyPlacementCallback callback = mSetPlacementCallback.get(p);
        if (callback != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("request", request.toString());
                obj.put("productId", productId);
                callback.onPurchaseRequest(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, e.toString()));
            }
        }
    }

    @Override
    public void onRewardRequest(TJPlacement p, TJActionRequest request, String itemId, int quantity) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Tapjoy, onRewardRequest - placement:" + p.getName() +
                    ", request:" + request.toString() +
                    ", itemId:" + itemId +
                    ", quantity:" + String.valueOf(quantity));
        }

        PerpleTapjoyPlacementCallback callback = mSetPlacementCallback.get(p);
        if (callback != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("request", request.toString());
                obj.put("itemId", itemId);
                obj.put("quantity", String.valueOf(quantity));
                callback.onRewardRequest(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onError(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, e.toString()));
            }
        }
    }

    @Override
    public void onContentShow(TJPlacement p) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Tapjoy, onContentShow - placement:" + p.getName());
        }
        PerpleTapjoyPlacementCallback callback = mShowPlacementCallback.get(p);
        if (callback != null) {
            callback.onShow();
        }
    }

    @Override
    public void onContentDismiss(TJPlacement p) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Tapjoy, onContentDismiss - placement:" + p.getName());
        }

        PerpleTapjoyPlacementCallback callback = mShowPlacementCallback.get(p);
        if (callback != null) {
            callback.onDismiss();
        }
    }
}
