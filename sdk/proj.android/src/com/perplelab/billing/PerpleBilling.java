package com.perplelab.billing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.perplelab.PerpleSDK;
import com.perplelab.PerpleSDKCallback;
import com.perplelab.billing.util.IabHelper;
import com.perplelab.billing.util.IabResult;
import com.perplelab.billing.util.Inventory;
import com.perplelab.billing.util.Purchase;
import com.perplelab.billing.util.IabHelper.OnConsumeMultiFinishedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class PerpleBilling {
    private static final String LOG_TAG = "PerpleSDK Billing";

    private static Activity sMainActivity;

    private IabHelper mHelper;
    private String mUrl;
    private PerpleBillingCallback mSetupCallback;
    private PerpleBillingPurchaseCallback mPurchaseCallback;
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    private boolean mIsSetupCompleted;

    private int mIncompletedPurchasesCount;
    private Map<Purchase, Boolean> mIncompletedPurchases;

    private Map<String, Purchase> mPurchases;

    private static final int RC_GOOGLE_PURCHASE_REQUEST = 10001;
    private static final int RC_GOOGLE_SUBSCRIPTION_REQUEST = 10002;

    public PerpleBilling(Activity activity) {
        sMainActivity = activity;
        mIsSetupCompleted = false;
    }

    /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    public void init(String base64EncodedPublicKey, boolean isDebug) {

        mIncompletedPurchases = new HashMap<Purchase, Boolean>();
        mPurchases = new HashMap<String, Purchase>();

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(LOG_TAG, "Creating IAB helper.");
        mHelper = new IabHelper(sMainActivity, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(isDebug);
    }

    public void startSetup(String url, PerpleBillingCallback callback) {
        // 영수증 검증 플랫폼 서버 API 주소
        // ex) http://platform.perplelab.com/@gameId/payment/receiptValidation
        mUrl = url;

        mSetupCallback = callback;

        if (mIsSetupCompleted) {
            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(LOG_TAG, "Setup is already completed. Querying inventory.");
            mSetupCallback.onPurchase("");
            return;
        }

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(LOG_TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(LOG_TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.e(LOG_TAG, "PerpleBilling Error: Problem setting up in-app billing: " + result);
                    String info = PerpleSDK.getErrorInfo(String.valueOf(result.getResponse()), result.getMessage());
                    mSetupCallback.onError(info);
                    return;
                }

                setQueryInventoryFinishedListener();
                setPurchaseFinishedListener();

                mIsSetupCompleted = true;

                Log.d(LOG_TAG, "Setup successful.");

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(LOG_TAG, "Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    public void onDestroy() {
        // very important:
        Log.d(LOG_TAG, "Destroying helper.");
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (Exception e) {
                // IllegalArgumentException
                e.printStackTrace();
            }
            mHelper = null;
            mIsSetupCompleted = false;
        }

        if (mIncompletedPurchases != null) {
            mIncompletedPurchases.clear();
        }

        if (mPurchases != null) {
            mPurchases.clear();
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return false;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            return false;
        }
        else {
            Log.d(LOG_TAG, "onActivityResult handled by IABUtil.");
            return true;
        }
    }

    public void purchase(String sku, String payload, final PerpleBillingPurchaseCallback callback) {
        Log.d(LOG_TAG, "Purchase, sku: " + sku);
        if (mHelper == null || !mIsSetupCompleted) {
            callback.onFail("Error purchasing: in-app module is not initialized.");
            return;
        }

        mPurchaseCallback = callback;

        mHelper.launchPurchaseFlow(sMainActivity, sku, RC_GOOGLE_PURCHASE_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    public void subscription(String sku, String payload, final PerpleBillingPurchaseCallback callback) {
        Log.d(LOG_TAG, "Subscription, sku: " + sku);
        if (mHelper == null || !mIsSetupCompleted) {
            callback.onFail("Error purchasing: in-app module is not initialized.");
            return;
        }

        mPurchaseCallback = callback;

        mHelper.launchSubscriptionPurchaseFlow(sMainActivity, sku, RC_GOOGLE_SUBSCRIPTION_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    public void consume(String orderIds) {
        List<String> orderIdList = getOrderIdList(orderIds);

        if (orderIdList.size() == 0) {
            return;
        }

        List<Purchase> purchases = new ArrayList<Purchase>();
        for (int i = 0; i < orderIdList.size(); i++) {
            Purchase p = mPurchases.get(orderIdList.get(i));
            if (p != null) {
                purchases.add(p);
            }
        }

        if (purchases.size() > 0) {
            mHelper.consumeAsync(purchases, new OnConsumeMultiFinishedListener() {
                @Override
                public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
                    for (int i = 0; i < purchases.size(); i++) {
                        if (results.get(i).isSuccess()) {
                            mPurchases.remove(purchases.get(i).getOrderId());
                        }
                    }
                }
            });
        }
    }

    /** Verifies the developer payload of a purchase.
     * @throws IOException, JSONException */
    private String verifyDeveloperPayload(Purchase p) throws IOException, JSONException {
        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        URL url = new URL(mUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setConnectTimeout(10000);
        con.setReadTimeout(15000);

        // HTTP request header
        con.setRequestProperty("Cache-Control", "no-cache");    //optional
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("platform", "google");
        data.put("receipt", p.getOriginalJson());
        data.put("signature", p.getSignature());

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        InputStream is = con.getInputStream();
        @SuppressWarnings("resource")
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return it.
        JSONObject response = new JSONObject(responseString);
        return response.getString("status");
    }

    private class CheckReceiptTask extends AsyncTask<Purchase, Void, Integer> {
        private String mMsg;
        private PerpleSDKCallback mCallback;

        public CheckReceiptTask(PerpleSDKCallback callback) {
            mCallback = callback;
        }

        @Override
        protected Integer doInBackground(Purchase... params) {
            int ret = -1;
            try {
                Purchase p = params[0];
                ret = 0;
                mMsg = verifyDeveloperPayload(p);
            } catch (IOException e) {
                e.printStackTrace();
                mMsg = e.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                mMsg = e.toString();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Integer ret) {
            if (mCallback != null) {
                if (ret != 0) {
                    mCallback.onFail(PerpleSDK.getErrorInfo(String.valueOf(ret), mMsg));
                } else {
                    mCallback.onSuccess(mMsg);
                }
            }
        }
    }

    private void checkReceipt(Purchase p, PerpleSDKCallback callback) {
        new CheckReceiptTask(callback).execute(p);
    }

    private void setQueryInventoryFinishedListener() {
        // Listener that's called when we finish querying the items and subscriptions we own
        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                Log.d(LOG_TAG, "Query inventory finished.");

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Is it a failure?
                if (result.isFailure()) {
                    Log.e(LOG_TAG, "Failed to query inventory: " + result);
                    String info = PerpleSDK.getErrorInfo(String.valueOf(result.getResponse()), result.getMessage());
                    mSetupCallback.onError(info);
                    return;
                }

                Log.d(LOG_TAG, "Query inventory was successful.");

                List<Purchase> purchases = inventory.getAllPurchases();

                mIncompletedPurchases.clear();
                mIncompletedPurchasesCount = purchases.size();

                if (mIncompletedPurchasesCount > 0) {
                    for (final Purchase p : purchases) {
                        checkReceipt(p, new PerpleSDKCallback() {
                            @Override
                            public void onSuccess(String info) {
                                processCheckReceiptResultImcompletedPurchases(p, info, true);
                            }
                            @Override
                            public void onFail(String info) {
                                processCheckReceiptResultImcompletedPurchases(p, info, false);
                            }
                        });
                    }
                } else {
                    mSetupCallback.onPurchase("");
                }
            }
        };
    }

    private void setPurchaseFinishedListener() {
        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, final Purchase purchase) {
                Log.d(LOG_TAG, "Purchasing finished: " + result + ", purchase: " + purchase);

                // if we were disposed of in the meantime, quit.
                if (mHelper == null) {
                    mPurchaseCallback.onFail("Error purchasing: in-app module is not initialized.");
                    return;
                }

                if (result.isFailure()) {
                    if (result.getResponse() == -1005) {
                        mPurchaseCallback.onCancel("");
                    } else {
                        String info = PerpleSDK.getErrorInfo(String.valueOf(result.getResponse()), result.getMessage());
                        mPurchaseCallback.onFail(info);
                    }
                    return;
                }

                checkReceipt(purchase, new PerpleSDKCallback() {
                    @Override
                    public void onSuccess(String info) {
                        Log.d(LOG_TAG, "Purchasing successful.");
                        processCheckReceiptResult(purchase, info, true);
                    }
                    @Override
                    public void onFail(String info) {
                        processCheckReceiptResult(purchase, info, false);
                    }
                });
            }
        };
    }

    private void processCheckReceiptResult(Purchase p, String info, boolean isCheckReceiptSuccess) {
        if (isCheckReceiptSuccess) {
            if (getRetcode(info) == 0) {
                mPurchases.put(p.getOrderId(), p);
                mPurchaseCallback.onSuccess(getPurchaseResult(p).toString());
            } else {
                mPurchaseCallback.onFail(info);
                mHelper.consumeAsync(p, new IabHelper.OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        // Do noting
                    }
                });
            }
        } else {
            mPurchaseCallback.onFail(info);
        }
    }

    private void processCheckReceiptResultImcompletedPurchases(Purchase p, String info, boolean isCheckReceiptSuccess) {
        if (isCheckReceiptSuccess) {
            if (getRetcode(info) == 0) {
                mPurchases.put(p.getOrderId(), p);
                mIncompletedPurchases.put(p, true);
            } else {
                mIncompletedPurchases.put(p, false);
            }
        }

        mIncompletedPurchasesCount--;
        if (mIncompletedPurchasesCount == 0) {

            mSetupCallback.onPurchase(getPurchaseResultArray(getPurchasesList(mIncompletedPurchases, true)));

            List<Purchase> invalidList = getPurchasesList(mIncompletedPurchases, false);
            if (invalidList.size() > 0) {
                mHelper.consumeAsync(invalidList, new OnConsumeMultiFinishedListener() {
                    @Override
                    public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
                    }
                });
            }
        }
    }

    private int getRetcode(String info) {
        try {
            JSONObject obj = new JSONObject(info);
            int retcode = Integer.parseInt(obj.getString("retcode"));
            return retcode;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private List<Purchase> getPurchasesList(Map<Purchase, Boolean> purchasesMap, boolean valid) {
        List<Purchase> list = new ArrayList<Purchase>();
        for (Map.Entry<Purchase, Boolean> entry : purchasesMap.entrySet()) {
            if (entry.getValue() == valid) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    private String getPurchaseResultArray(List<Purchase> purchases) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < purchases.size(); i++) {
            array.put(getPurchaseResult(purchases.get(i)));
        }

        if (array.length() > 0) {
            return array.toString();
        }
        return "";
    }

    private JSONObject getPurchaseResult(Purchase p) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("orderId", p.getOrderId());
            obj.put("payload", p.getDeveloperPayload());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private List<String> getOrderIdList(String data) {
        List<String> list = new ArrayList<String>();
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
