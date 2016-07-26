package com.perplelab;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.facebook.FacebookException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuthException;
import com.perplelab.adbrix.PerpleAdbrix;
import com.perplelab.billing.PerpleBilling;
import com.perplelab.facebook.PerpleFacebook;
import com.perplelab.firebase.PerpleFirebase;
import com.perplelab.google.PerpleBuildGoogleApiClient;
import com.perplelab.google.PerpleGoogle;
import com.perplelab.naver.PerpleNaver;
import com.perplelab.tapjoy.PerpleTapjoy;

import org.json.JSONException;
import org.json.JSONObject;

public class PerpleSDK {
    private static final String LOG_TAG = "PerpleSDK";

    public static final String ERROR_UNKNOWN                            = "-999";
    public static final String ERROR_IOEXCEPTION                        = "-998";
    public static final String ERROR_JSONEXCEPTION                      = "-997";
    public static final String ERROR_FIREBASE_NOTINITIALIZED            = "-1000";
    public static final String ERROR_FIREBASE_SENDPUSHMESSAGE           = "-1001";
    public static final String ERROR_GOOGLE_NOTINITIALIZED              = "-1200";
    public static final String ERROR_GOOGLE_LOGIN                       = "-1201";
    public static final String ERROR_GOOGLE_NOTSIGNEDIN                 = "-1202";
    public static final String ERROR_GOOGLE_ACHIEVEMENTS                = "-1203";
    public static final String ERROR_GOOGLE_LEADERBOARDS                = "-1204";
    public static final String ERROR_GOOGLE_QUESTS                      = "-1205";
    public static final String ERROR_GOOGLE_NOTSETLOGINCALLBACK         = "-1206";
    public static final String ERROR_GOOGLE_NOTSETPLAYSERVICESCALLBACK  = "-1207";
    public static final String ERROR_GOOGLE_NOTSETQUESTSCALLBACK        = "-1208";
    public static final String ERROR_GOOGLE_NOTAVAILABLEPLAYSERVICES    = "-1209";
    public static final String ERROR_GOOGLE_LOGOUT                      = "-1210";
    public static final String ERROR_FACEBOOK_NOTINITIALIZED            = "-1300";
    public static final String ERROR_FACEBOOK_FACEBOOKEXCEPTION         = "-1301";
    public static final String ERROR_FACEBOOK_REQUESTERROR              = "-1302";
    public static final String ERROR_NAVER_NOTINITIALIZED               = "-1400";
    public static final String ERROR_NAVER_CAFENOTINITIALIZED           = "-1401";
    public static final String ERROR_BILLING_NOTINITIALIZED             = "-1500";
    public static final String ERROR_BILLING_INITFAILED                 = "-1501";
    public static final String ERROR_TAPJOY_NOTINITIALIZED              = "-1600";
    public static final String ERROR_TAPJOY_NOTSETPLACEMENT             = "-1601";
    public static final String ERROR_TAPJOY_GETCURRENCY                 = "-1602";
    public static final String ERROR_TAPJOY_SPENDCURRENCY               = "-1603";
    public static final String ERROR_TAPJOY_AWARDCURRENCY               = "-1604";

    public static final int RC_GOOGLE_SIGN_IN                   = 9001;
    public static final int RC_GOOGLE_ACHIEVEMENTS              = 9002;
    public static final int RC_GOOGLE_LEADERBOARDS              = 9003;
    public static final int RC_GOOGLE_QUESTS                    = 9004;
    public static final int RC_GOOGLE_SIGNIN_RESOLVE_ERROR      = 9005;
    public static final int RC_GOGLEPLAYSERVICE_NOTAVAILABLE    = 9006;
    public static final int RC_GOOGLE_PURCHASE_REQUEST          = 10001;
    public static final int RC_GOOGLE_SUBSCRIPTION_REQUEST      = 10002;

    public static Activity MainActivity;
    public static boolean IsDebug;
    public static boolean IsReceivePushOnForeground;

    private PerpleFirebase mFirebase;
    private PerpleBilling mBilling;
    private PerpleGoogle mGoogle;
    private PerpleFacebook mFacebook;
    private PerpleNaver mNaver;
    private PerpleAdbrix mAdbrix;
    private PerpleTapjoy mTapjoy;

    //--------------------------------------------------------------------------------
    // PerpleSDK is a singleton class
    //--------------------------------------------------------------------------------

    private static PerpleSDK sMyInstance;

    public static void createInstance(Activity activity) {
        sMyInstance = new PerpleSDK(activity);
    };

    public static PerpleSDK getInstance() {
        if (sMyInstance == null) {
            Log.e(LOG_TAG, "PerpleSDK.createInstance() must be called first.");
        }
        return sMyInstance;
    }

    private PerpleSDK(Activity activity) {
        MainActivity = activity;
        nativeInitJNI(activity);
    }

    //--------------------------------------------------------------------------------
    // PerpleSDK initializing functions
    //--------------------------------------------------------------------------------

    public boolean initSDK(String gameId, String gcmSenderId, String base64EncodedPublicKey, boolean isDebug) {
        IsDebug = isDebug;
        if (isDebug) {
            Log.d(LOG_TAG, "PerpleSDK, Enabled debug mode");
        }

        // @firebase
        mFirebase = new PerpleFirebase(MainActivity);
        mFirebase.init(gcmSenderId);

        // @billing
        if (mBilling == null) {
            initBilling(gameId, base64EncodedPublicKey, isDebug);
        }

        int ret = nativeInitSDK();
        if (ret < 0) {
            Log.e(LOG_TAG, "Initializing PerpleSDK fail - code:" + String.valueOf(ret));

            mFirebase = null;
            mBilling = null;

            return false;
        }

        String version = nativeGetSDKVersionString();
        Log.d(LOG_TAG, "Initializing PerpleSDK Success - version:" + version);

        return true;
    }

    // @billing
    public void initBilling(String gameId, String base64EncodedPublicKey, boolean isDebug) {
        if (mBilling == null) {
            mBilling = new PerpleBilling(MainActivity);
            mBilling.init(gameId, base64EncodedPublicKey, isDebug);
        }
    }

    // @google
    public boolean initGoogle(String webClientId) {
        mGoogle = new PerpleGoogle(MainActivity);
        if (mGoogle.init(webClientId)) {
            return true;
        } else {
            mGoogle = null;
        }

        return false;
    }

    // @google
    public boolean initGoogle(String webClientId, PerpleBuildGoogleApiClient build) {
        mGoogle = new PerpleGoogle(MainActivity);
        if (mGoogle.init(webClientId, build)) {
            return true;
        } else {
            mGoogle = null;
        }

        return false;
    }

    // @facebook
    public void initFacebook(Bundle savedInstanceState) {
        mFacebook = new PerpleFacebook(MainActivity);
        mFacebook.init(savedInstanceState);
    }

    // @naver
    public void initNaver(String clientId, String clientSecret, String clientName, boolean isDebug) {
        if (mNaver == null) {
            mNaver = new PerpleNaver(MainActivity);
        }
        mNaver.init(clientId, clientSecret, clientName, isDebug);
    }

    // @naver cafe
    public void initNaverCafe(String clientId, String clientSecret, int cafeId) {
        if (mNaver == null) {
            mNaver = new PerpleNaver(MainActivity);
        }
        mNaver.initCafe(clientId, clientSecret, cafeId);
    }

    // @adbrix
    public void initAdbrix() {
        mAdbrix = new PerpleAdbrix(MainActivity);
        mAdbrix.init();
    }

    // @tapjoy
    public void initTapjoy(String appKey, String senderId, boolean isDebug) {
        mTapjoy = new PerpleTapjoy(MainActivity);
        mTapjoy.init(appKey, senderId, isDebug);
    }

    public void onStart() {
        // @tapjoy
        if (mTapjoy != null) {
            mTapjoy.onStart();
        }

        // @firebase
        if (mFirebase != null) {
            mFirebase.onStart();
        }

        // @google
        if (mGoogle != null) {
            mGoogle.onStart();
        }
    }

    public void onStop() {
        // @tapjoy
        if (mTapjoy != null) {
            mTapjoy.onStop();
        }

        // @firebase
        if (mFirebase != null) {
            mFirebase.onStop();
        }

        // @google
        if (mGoogle != null) {
            mGoogle.onStop();
        }
    }

    public void onResume() {
        // @adbrix
        if (mAdbrix != null) {
            mAdbrix.onResume();
        }

        // @google
        if (mGoogle != null) {
            mGoogle.onResume();
        }

        // @facebook
        if (mFacebook != null) {
            mFacebook.onResume();
        }
    }

    public void onPause() {
        // @adbrix
        if (mAdbrix != null) {
            mAdbrix.onPause();
        }

        // @facebook
        if (mFacebook != null) {
            mFacebook.onPause();
        }
    }

    public void onDestroy() {
        // @billing
        if (mBilling != null) {
            mBilling.onDestroy();
        }

        // @tapjoy
        if (mTapjoy != null) {
            mTapjoy.onDestroy();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (IsDebug) {
            Log.d(LOG_TAG, "onActivityResult - request:" + String.valueOf(requestCode) + ", result:" + String.valueOf(resultCode));
        }

        // @billing
        // tapjoy 의 track purchase 기능을 이용하기 위해서는 billing 에서 처리가 된 activity result 라 할지라도
        // tapjoy 의 onActivityRersult 로 bypass 해 주어야 하므로, billing 의 onActivityResult 의 true/false 결과에
        // 따른 리턴 처리를 하지 않도록 한다.
        if (mBilling != null) {
            mBilling.onActivityResult(requestCode, resultCode, data);
        }

        // @tapjoy
        if (mTapjoy != null) {
            mTapjoy.onActivityResult(requestCode, resultCode, data);
        }

        // @google
        if (mGoogle != null) {
            mGoogle.onActivityResult(requestCode, resultCode, data);
        }

        // @facebook
        if (mFacebook != null) {
            mFacebook.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setReceivePushOnForeground(boolean isReceive) {
        IsReceivePushOnForeground = isReceive;
    }

    //--------------------------------------------------------------------------------
    // External public static functions
    //--------------------------------------------------------------------------------

    // @firebase
    public static PerpleFirebase getFirebase() {
        return getInstance().mFirebase;
    }

    // @billing
    public static PerpleBilling getBilling() {
        return getInstance().mBilling;
    }

    // @billing
    public static IInAppBillingService getBillingService() {
        PerpleBilling billing = getInstance().mBilling;
        if (billing != null) {
            return billing.getBillingService();
        } else {
            return null;
        }
    }

    // @google
    public static PerpleGoogle getGoogle() {
        return getInstance().mGoogle;
    }

    // @google
    public static GoogleApiClient getGoogleApiClient() {
        if (getGoogle() != null) {
            return getGoogle().getGoogleApiClient();
        } else {
            return null;
        }
    }

    // @facebook
    public static PerpleFacebook getFacebook() {
        return getInstance().mFacebook;
    }

    // @adbrix
    public static PerpleAdbrix getAdbrix() {
        return getInstance().mAdbrix;
    }

    // @tapjoy
    public static PerpleTapjoy getTapjoy() {
        return getInstance().mTapjoy;
    }

    public static PerpleNaver getNaver() {
        return getInstance().mNaver;
    }

    //--------------------------------------------------------------------------------
    // Internal utility functions
    //--------------------------------------------------------------------------------

    // @firebase fcm
    private static PerpleSDKCallback sTokenRefreshCallback;
    public static void setFCMTokenRefreshCallback(PerpleSDKCallback callback) {
        if (getInstance().mFirebase == null) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        sTokenRefreshCallback = callback;

        JSONObject obj = getInstance().mFirebase.getPushToken();
        if (obj != null) {
            callback.onSuccess(obj.toString());
        } else {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, "JSON exception"));
        }

    }

    // @firebase fcm, callback from PerpleFirebaseInstanceIdService
    public static void onFCMTokenRefresh(String iid, String token) {
        if (sTokenRefreshCallback != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("iid", iid);
                obj.put("token", token);
                sTokenRefreshCallback.onSuccess(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                sTokenRefreshCallback.onFail(getErrorInfo(ERROR_JSONEXCEPTION, e.toString()));
            }
        }
    }

    // @firebase fcm
    private static PerpleSDKCallback sSendPushMessageCallback;
    public static void setSendPushMessageCallback(PerpleSDKCallback callback) {
        if (getInstance().mFirebase == null) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        sSendPushMessageCallback = callback;
    }

    // @firebase fcm, callback from PerpleFirebaseMessagingService
    public static void onMessageSent(String msgId) {
        if (sSendPushMessageCallback != null) {
            sSendPushMessageCallback.onSuccess(msgId);
        }
    }

    // @firebase fcm, callback from PerpleFirebaseMessagingService
    public static void onSendError(String msgId, Exception exception) {
        String code = ERROR_FIREBASE_SENDPUSHMESSAGE;
        String message = "Error occurred in sending push message.";
        try {
            JSONObject obj = new JSONObject();
            obj.put("msgId", msgId);
            obj.put("desc", exception.toString());
            message = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            message = e.toString();
        }

        if (sSendPushMessageCallback != null) {
            sSendPushMessageCallback.onFail(getErrorInfo(code, message));
        }
    }

    public static String getErrorInfo(String code, String msg) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("code", code);
            obj.put("msg", msg);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getErrorInfoFromFirebaseException(Exception error) {
        if (error instanceof FirebaseAuthException) {
            FirebaseAuthException e = (FirebaseAuthException)error;
            return PerpleSDK.getErrorInfo(e.getErrorCode(), e.getMessage());
        } else {
            return PerpleSDK.getErrorInfo(ERROR_UNKNOWN, error.toString());
        }
    }

    public static String getErrorInfoFromFacebookException(Exception error) {
        if (error instanceof FacebookException) {
            FacebookException e = (FacebookException)error;
            return PerpleSDK.getErrorInfo(ERROR_FACEBOOK_FACEBOOKEXCEPTION, e.toString());
        } else {
            return PerpleSDK.getErrorInfo(ERROR_UNKNOWN, error.toString());
        }
    }

    private static GLSurfaceView sGLSurfaceView;
    public static void setGLSurfaceView(GLSurfaceView view) {
        sGLSurfaceView = view;
    }

    public static void callSDKResult(final int id, final String result, final String info) {
        if (IsDebug) {
            Log.d(LOG_TAG, "callSDKResult - " + "funcID:" + String.valueOf(id) + ", ret:" + result + ",info:" + info);
        }

        if (sGLSurfaceView != null) {
            sGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    nativeSDKResult(id, result, info);
                }
            });
        } else {
            nativeSDKResult(id, result, info);
        }
    }

    //--------------------------------------------------------------------------------

    private static native int nativeInitJNI(Activity activity);
    private static native int nativeInitSDK();
    private static native int nativeGetSDKVersion();
    private static native String nativeGetSDKVersionString();
    private static native int nativeSDKResult(int id, String result, String info);
}
