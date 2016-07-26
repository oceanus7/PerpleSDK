package com.perplelab;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuthException;
import com.perplelab.billing.PerpleBilling;
import com.perplelab.billing.PerpleBillingCallback;
import com.perplelab.billing.PerpleBillingPurchaseCallback;
import com.perplelab.naver.PerpleNaver;
import com.perplelab.naver.PerpleNaverCafeCallback;
import com.perplelab.naver.PerpleNaverLoginCallback;
import com.perplelab.tapjoy.PerpleTapjoy;
import com.perplelab.tapjoy.PerpleTapjoyPlacementCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PerpleSDK {
    private static final String LOG_TAG = "PerpleSDK";

    public static final String ERROR_UNKNOWN = "-999";
    public static final String ERROR_FIREBASE_NOTINITIALIZED = "-100";
    public static final String ERROR_GOOGLE_NOTINITIALIZED = "-200";
    public static final String ERROR_GOOGLE_LOGIN = "-201";
    public static final String ERROR_FACEBOOK_NOTINITIALIZED = "-300";
    public static final String ERROR_FACEBOOK_LOGIN = "-301";
    public static final String ERROR_FACEBOOK_REQUESTERROR = "-302";
    public static final String ERROR_NAVER_NOTINITIALIZED = "-400";
    public static final String ERROR_BILLING_NOTINITIALIZED = "-500";
    public static final String ERROR_BILLING_INITFAILED = "-501";

    private static PerpleSDK sMyInstance;

    public static void createInstance(Activity activity) {
        sMyInstance = new PerpleSDK(activity);
    };

    public static PerpleSDK getInstance() {
        if (sMyInstance == null) {
            // @error
            Log.e(LOG_TAG, "PerpleSDK.createInstance() must be called first.");
        }
        return sMyInstance;
    }

    private static Activity sMainActivity;
    private boolean mIsInit;

    private static GLSurfaceView sGLSurfaceView;

    // @firebase
    private PerpleFirebase mFirebase;
    private boolean mUseFirebase;

    // @firebase, FCM
    private static boolean sFCMIsSetFromLua;
    private static String sFCMiid = "";
    private static String sFCMtoken = "";

    // @billing
    private PerpleBilling mBilling;
    private boolean mUseBilling;

    // @google
    private PerpleGoogle mGoogle;
    private boolean mUseGoogle;

    // @facebook
    private PerpleFacebook mFacebook;
    private boolean mUseFacebook;

    // @naver
    private PerpleNaver mNaver;
    private boolean mUseNaver;
    private boolean mUseNaverCafe;

    // @adbrix
    private PerpleAdbrix mAdbrix;
    private boolean mUseAdbrix;

    // @tapjoy
    private PerpleTapjoy mTapjoy;
    private boolean mUseTapjoy;

    private boolean mRequestSendPushMessage;
    private boolean mRequestSendPushMessageToGroup;

    private PerpleSDK(Activity activity) {
        mRequestSendPushMessage = false;
        mRequestSendPushMessageToGroup = false;

        mUseFirebase = false;
        mUseGoogle = false;
        mUseFacebook = false;
        mUseNaver = false;
        mUseNaverCafe = false;
        mUseAdbrix = false;
        mUseTapjoy = false;

        mUseBilling = false;

        sMainActivity = activity;
        mIsInit = false;

        nativeInitJNI(sMainActivity);
    }

    public boolean initSDK(String gameId, String gcmSenderId, String base64EncodedPublicKey) {
        // @firebase
        mFirebase = new PerpleFirebase(sMainActivity);
        mFirebase.init(gcmSenderId);
        mUseFirebase = true;

        // @billing
        mBilling = new PerpleBilling(sMainActivity);
        mBilling.init(gameId, base64EncodedPublicKey, false);
        mUseBilling = true;

        int ret = nativeInitSDK();
        if (ret < 0) {
            // Init Failed!!
            Log.e(LOG_TAG, "PerplsSDK Init Fail!!! - ErrorCode:" + String.valueOf(ret));
            return false;
        }

        String version = nativeGetSDKVersionString();
        Log.d(LOG_TAG, "PerplsSDK Init Success!!! - Version:" + version);

        mIsInit = true;

        return true;
    }

    public static void setGLSurfaceView(GLSurfaceView view) {
        sGLSurfaceView = view;
    }

    public static Activity getMainActivity() {
        return sMainActivity;
    }

    // @firebase, FCM
    public static void setFCMTokenRefresh() {
        sFCMIsSetFromLua = true;

        if (!getInstance().mUseFirebase) {
            callSDKResult("setFCMTokenRefresh", "error", getErrorInfo(ERROR_FIREBASE_NOTINITIALIZED,"Firebase is not initialized."));
            return;
        }

        if (!sFCMiid.isEmpty() && !sFCMtoken.isEmpty()) {
            JSONObject info = new JSONObject();
            try {
                info.put("iid", sFCMiid);
                info.put("token", sFCMtoken);
                callSDKResult("setFCMTokenRefresh", "refresh", info.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                callSDKResult("setFCMTokenRefresh", "error", e.toString());
            }
        } else {
            String info = getInstance().mFirebase.getFCMPushToken().toString();
            callSDKResult("setFCMTokenRefresh", "refresh", info);
        }
    }

    // @firebase, FCM
    public static void onFCMTokenRefresh(String iid, String token) {
        if (!getInstance().mUseFirebase) {
            return;
        }

        sFCMiid = iid;
        sFCMtoken = token;

        if (sFCMIsSetFromLua) {
            JSONObject info = new JSONObject();
            try {
                info.put("iid", sFCMiid);
                info.put("token", sFCMtoken);
                callSDKResult("setFCMTokenRefresh", "refresh", info.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                callSDKResult("setFCMTokenRefresh", "error", e.toString());
            }
        }
    }

    // @firebase, FCM
    public static void getFCMToken() {
        if (!getInstance().mUseFirebase) {
            String info = PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized.");
            callSDKResult("getFCMToken", "fail", info);
            return;
        }

        String info = getInstance().mFirebase.getFCMPushToken().toString();
        callSDKResult("getFCMToken", "success", info);
    }

    // @firebase, FCM
    public static void sendFCMPushMessage(String data) {
        if (!getInstance().mUseFirebase) {
            return;
        }

        getInstance().mRequestSendPushMessage = true;
        getInstance().mFirebase.sendUpstreamMessage(data);
    }

    // @firebase, FCM
    public static void sendFCMPushMessageToGroup(String groupKey, String data) {
        if (!getInstance().mUseFirebase) {
            return;
        }

        getInstance().mRequestSendPushMessageToGroup = true;
        getInstance().mFirebase.sendUpstreamMessage(groupKey, data);
    }

    // @firebase, FCM
    public static void onMessageSent(String msgId) {
        if (getInstance().mRequestSendPushMessage) {
            getInstance().mRequestSendPushMessage = false;
            callSDKResult("sendFCMPushMessage", "success", msgId);
        } else if (getInstance().mRequestSendPushMessageToGroup) {
            getInstance().mRequestSendPushMessageToGroup = false;
            callSDKResult("sendFCMPushMessageToGroup", "success", msgId);
        }
    }

    // @firebase, FCM
    public static void onSendError(String msgId, Exception exception) {
        JSONObject info = new JSONObject();
        try {
            info.put("msgId", msgId);
            info.put("desc", exception.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (getInstance().mRequestSendPushMessage) {
            getInstance().mRequestSendPushMessage = false;
            callSDKResult("sendFCMPushMessage", "fail", info.toString());
        } else if (getInstance().mRequestSendPushMessageToGroup) {
            getInstance().mRequestSendPushMessageToGroup = false;
            callSDKResult("sendFCMPushMessageToGroup", "fail", info.toString());
        }
    }

    // @billing
    public static void setBilling(String url) {
        getInstance().mBilling.startSetup(url, new PerpleBillingCallback() {
            @Override
            public void onError(String info) {
                callSDKResult("setBilling", "error", info);
            }

            @Override
            public void onPurchase(String info) {
                callSDKResult("setBilling", "purchase", info);
            }
        });
    }

    // @billing
    public static void purchase(String sku, String payload) {
        if (!getInstance().mUseBilling) {
            String info = PerpleSDK.getErrorInfo(PerpleSDK.ERROR_BILLING_NOTINITIALIZED, "Billing is not initialized.");
            callSDKResult("purchase", "fail", info);
            return;
        }

        getInstance().mBilling.purchase(sku, payload, new PerpleBillingPurchaseCallback() {
            @Override
            public void onSuccess(String info) {
                callSDKResult("purchase", "success", info);
            }

            @Override
            public void onFail(String info) {
                callSDKResult("purchase", "fail", info);
            }

            @Override
            public void onCancel(String info) {
                callSDKResult("purchase", "cancel", info);
            }
        });
    }

    // @billing
    public static void subscription(String sku, String payload) {
        if (!getInstance().mUseBilling) {
            String info = PerpleSDK.getErrorInfo(PerpleSDK.ERROR_BILLING_NOTINITIALIZED, "Billing is not initialized.");
            callSDKResult("purchase", "fail", info);
            return;
        }

        getInstance().mBilling.subscription(sku, payload, new PerpleBillingPurchaseCallback() {
            @Override
            public void onSuccess(String info) {
                callSDKResult("subscription", "success", info);
            }

            @Override
            public void onFail(String info) {
                callSDKResult("subscription", "fail", info);
            }

            @Override
            public void onCancel(String info) {
                callSDKResult("subscription", "cancel", info);
            }
        });
    }

    // @google
    public boolean initGoogle(String web_client_id) {
        if (!mIsInit) {
            return false;
        }

        mGoogle = new PerpleGoogle(sMainActivity);
        if (mGoogle.init(web_client_id)) {
            mUseGoogle = true;
            return true;
        }

        return false;
    }

    // @google
    public boolean initGoogle(String web_client_id, PerpleBuildGoogleApiClient build) {
        if (!mIsInit) {
            return false;
        }

        mGoogle = new PerpleGoogle(sMainActivity);
        if (mGoogle.init(web_client_id, build)) {
            mUseGoogle = true;
            return true;
        }

        return false;
    }

    // @google
    public GoogleApiClient getGoogleApiClient() {
        if (mUseGoogle) {
            return mGoogle.getGoogleApiClient();
        } else {
            return null;
        }
    }

    // @facebook
    public boolean initFacebook(Bundle savedInstanceState) {
        if (!mIsInit) {
            return false;
        }

        mFacebook = new PerpleFacebook(sMainActivity);
        mFacebook.init(savedInstanceState);

        mUseFacebook = true;

        return true;
    }

    // @naver
    public void initNaver(String clientId, String clientSecret, String clientName, boolean isDebug) {
        if (mNaver == null) {
            mNaver = new PerpleNaver(sMainActivity);
        }
        mNaver.init(clientId, clientSecret, clientName, isDebug);
        mUseNaver = true;
    }

    // @naver
    public void initNaverCafe(String clientId, String clientSecret, int cafeId) {
        if (mNaver == null) {
            mNaver = new PerpleNaver(sMainActivity);
        }
        mNaver.initCafe(clientId, clientSecret, cafeId);
        mUseNaverCafe = true;
    }

    // @adbrix
    public void initAdbrix() {
        mAdbrix = new PerpleAdbrix(sMainActivity);
        mAdbrix.init();
        mUseAdbrix = true;
    }

    // @tapjoy
    public void initTapjoy(String appKey, String senderId, boolean isDebug) {
        mTapjoy = new PerpleTapjoy(sMainActivity);
        mTapjoy.init(appKey, senderId, isDebug);
        mUseTapjoy = true;
    }

    public void onStart() {
        // @firebase
        if (mUseFirebase) {
            mFirebase.onStart();
        }

        // @google
        if (mUseGoogle) {
            mGoogle.onStart();
        }

        // @tapjoy
        if (mUseTapjoy) {
            mTapjoy.onStart();
        }
    }

    public void onStop() {
        // @firebase
        if (mUseFirebase) {
            mFirebase.onStop();
        }

        // @google
        if (mUseGoogle) {
            mGoogle.onStop();
        }

        // @tapjoy
        if (mUseTapjoy) {
            mTapjoy.onStop();
        }
    }

    public void onResume() {
        // @adbrix
        if (mUseAdbrix) {
            mAdbrix.onResume();
        }

        // @google
        if (mUseGoogle) {
            mGoogle.onResume();
        }

        // @facebook
        if (mUseFacebook) {
            mFacebook.onResume();
        }
    }

    public void onPause() {
        // @adbrix
        if (mUseAdbrix) {
            mAdbrix.onPause();
        }

        // @facebook
        if (mUseFacebook) {
            mFacebook.onPause();
        }
    }

    public void onDestroy() {
        // @facebook
        if (mUseFacebook) {
            mFacebook.onDestroy();
        }

        // @tapjoy
        if (mUseTapjoy) {
            mTapjoy.onDestroy();
        }

        // @billing
        if (mUseBilling) {
            mBilling.onDestroy();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult - request:" + String.valueOf(requestCode) + ", result:" + String.valueOf(resultCode));

        // @billing
        if (mUseBilling) {
            if (mBilling.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }

        // @google
        if (mUseGoogle) {
            mGoogle.onActivityResult(requestCode, resultCode, data);
        }

        // @facebook
        if (mUseFacebook) {
            mFacebook.onActivityResult(requestCode, resultCode, data);
        }

        /*
        // @tapjoy
        if (mUseTapjoy) {
            mTapjoy.onActivityResult(requestCode, resultCode, data);
        }
        */
    }

    // @firebase
    public static void autoLogin() {
        if (!getInstance().mUseFirebase) {
            callSDKResult("autoLogin", "fail", getErrorInfo(ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        if (!getInstance().mUseGoogle) {
            callSDKResult("autoLogin", "fail", getErrorInfo(ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        if (!getInstance().mUseFacebook) {
            callSDKResult("autoLogin", "fail", getErrorInfo(ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        // 구글 로그인은 필수이므로 오토로그인 시 무조건 구글 로그인을 처리함
        getInstance().mGoogle.login(new PerpleGoogleLoginCallback() {

            @Override
            public void onSuccess(String idToken) {

                getInstance().mFirebase.signInWithCredential(
                        PerpleFirebase.getGoogleCredential(idToken),
                        new PerpleSDKCallback() {

                            @Override
                            public void onSuccess(String info) {
                                // 로그인 정보에 구글 로그인 정보 추가
                                final String loginInfo = addGoogleLoginInfo(info);

                                // 로그인 정보에 facebook.com 이 있으면 페이스북 로그인 처리
                                if (getInstance().mFirebase.isLinkedProvider(info, "facebook.com")) {
                                   getInstance().mFacebook.login(new PerpleSDKCallback() {
                                        @Override
                                        public void onSuccess(String token) {
                                            callSDKResult("autoLogin", "success", loginInfo);
                                        }
                                        @Override
                                        public void onFail(String info) {
                                            callSDKResult("autoLogin", "success", loginInfo);
                                        }
                                    });
                                } else {
                                    callSDKResult("autoLogin", "success", loginInfo);
                                }
                            }

                            @Override
                            public void onFail(String info) {
                                callSDKResult("autoLogin", "fail", info);
                            }

                        });

            }

            @Override
            public void onFail(String info) {
                callSDKResult("autoLogin", "fail", info);
            }

            @Override
            public void onCancel() {
                callSDKResult("autoLogin", "cancel", "");
            }

            @Override
            public void onGooglePlayServicesNotAvailable(int resultCode, Intent data) {
                // @todo
            }

        });

    }

    // @firebase
    public static void loginAnonymously() {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.loginAnonymously(
                new PerpleSDKCallback() {
                    @Override
                    public void onSuccess(String info) {
                        callSDKResult("loginAnonymously", "success", info);
                    }
                    @Override
                    public void onFail(String info) {
                        callSDKResult("loginAnonymously", "fail", info);
                    }
                });
        }
    }

    // @google
    public static void loginGoogle() {
        if (getInstance().mUseGoogle) {
            getInstance().mGoogle.login(new PerpleGoogleLoginCallback() {
                @Override
                public void onSuccess(String idToken) {
                    getInstance().mFirebase.signInWithCredential(
                            PerpleFirebase.getGoogleCredential(idToken),
                            new PerpleSDKCallback() {
                                @Override
                                public void onSuccess(String info) {
                                    callSDKResult("loginGoogle", "success", addGoogleLoginInfo(info));
                                }
                                @Override
                                public void onFail(String info) {
                                    callSDKResult("loginGoogle", "fail", info);
                                }
                            });
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("loginGoogle", "fail", info);
                }
                @Override
                public void onCancel() {
                    callSDKResult("loginGoogle", "cancel", "");
                }
                @Override
                public void onGooglePlayServicesNotAvailable(int resultCode, Intent data) {
                    // @todo
                }
            });
        }
    }

    // @facebook
    public static void loginFacebook() {
        if (getInstance().mUseFacebook) {
            getInstance().mFacebook.login(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String token) {
                    getInstance().mFirebase.signInWithCredential(
                            PerpleFirebase.getFacebookCredential(token),
                            new PerpleSDKCallback() {
                                @Override
                                public void onSuccess(String info) {
                                    callSDKResult("loginFacebook", "success", info);
                                }
                                @Override
                                public void onFail(String info) {
                                    callSDKResult("loginFacebook", "fail", info);
                                }
                            });
                }
                @Override
                public void onFail(String info) {
                    if (info.equals("cancel")) {
                    } else {
                        callSDKResult("loginFacebook", "fail", info);
                    }
                }
            });
        }
    }

    // @firebase
    public static void loginEmail(String email, String password) {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.loginEmail(
                email,
                password,
                new PerpleSDKCallback() {
                    @Override
                    public void onSuccess(String info) {
                        callSDKResult("loginEmail", "success", info);
                    }
                    @Override
                    public void onFail(String info) {
                        callSDKResult("loginEmail", "fail", info);
                    }
                });
        }
    }

    // @firebase
    public static void linkWithGoogle() {
        if (getInstance().mUseGoogle) {
            getInstance().mGoogle.login(new PerpleGoogleLoginCallback() {
                @Override
                public void onSuccess(String idToken) {
                    getInstance().mFirebase.linkWithCredential(
                            PerpleFirebase.getGoogleCredential(idToken),
                            new PerpleSDKCallback() {
                                @Override
                                public void onSuccess(String info) {
                                    callSDKResult("linkWithGoogle", "success", addGoogleLoginInfo(info));
                                }
                                @Override
                                public void onFail(String info) {
                                    callSDKResult("linkWithGoogle", "fail", info);
                                }
                            });
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("linkWithGoogle", "fail", info);
                }
                @Override
                public void onCancel() {
                    callSDKResult("linkWithGoogle", "cancel", "");
                }
                @Override
                public void onGooglePlayServicesNotAvailable(int resultCode, Intent data) {
                    // @todo
                }
            });
        }
    }

    // @firebase
    public static void linkWithFacebook() {
        if (getInstance().mUseFacebook) {
            getInstance().mFacebook.login(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String token) {
                    getInstance().mFirebase.linkWithCredential(
                            PerpleFirebase.getFacebookCredential(token),
                            new PerpleSDKCallback() {
                                @Override
                                public void onSuccess(String info) {
                                    callSDKResult("linkWithFacebook", "success", info);
                                }
                                @Override
                                public void onFail(String info) {
                                    callSDKResult("linkWithFacebook", "fail", info);
                                }
                            });
                }
                @Override
                public void onFail(String info) {
                    if (info.equals("cancel")) {
                        callSDKResult("linkWithFacebook", "cancel", "");
                    } else {
                        callSDKResult("linkWithFacebook", "fail", info);
                    }
                }
            });
        }
    }

    // @firebase
    public static void linkWithEmail(String email, String password) {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.linkWithCredential(
                PerpleFirebase.getEmailCredential(email, password),
                new PerpleSDKCallback() {
                    @Override
                    public void onSuccess(String info) {
                        callSDKResult("linkWithEmail", "success", info);
                    }
                    @Override
                    public void onFail(String info) {
                        callSDKResult("linkWithEmail", "fail", info);
                    }
                });
        }
    }

    // @firebase
    public static void unlinkWithGoogle() {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.unlink("google.com", new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    getInstance().mGoogle.getGoogleApiClient().disconnect();
                    callSDKResult("unlinkWithGoogle", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("unlinkWithGoogle", "fail", info);
                }
            });
        }
    }

    // @firebase
    public static void unlinkWithFacebook() {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.unlink("facebook.com", new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("unlinkWithFacebook", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("unlinkWithFacebook", "fail", info);
                }
            });
        }
    }

    // @firebase
    public static void unlinkWithEmail() {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.unlink("email", new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("unlinkWithEmail", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("unlinkWithEmail", "fail", info);
                }
            });
        }
    }

    // @firebase
    public static void logout() {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.logout(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {

                    // 구글 로그아웃 처리
                    if (getInstance().mUseGoogle) {
                        if (getInstance().mGoogle.isSignedIn()) {
                            getInstance().mGoogle.logout();
                        }
                    }

                    callSDKResult("logout", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("logout", "fail", info);
                }
            });
        }
    }

    // @firebase
    public static void deleteUser() {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.deleteUser(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("deleteUser", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("deleteUser", "fail", info);
                }
            });
        }
    }

    // @firebase
    public static void createUserWithEmail(String email, String password) {
        if (getInstance().mUseFirebase) {
            getInstance().mFirebase.createUserWithEmail(email, password, new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("createUserWithEmail", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("createUserWithEmail", "fail", info);
                }
            });
        }
    }

    // @facebook
    public static void facebookGetFriends() {
        if (getInstance().mUseFacebook) {
            getInstance().mFacebook.getFriends(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("facebookGetFriends", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("facebookGetFriends", "fail", info);
                }
            });
        }
    }

    // @facebook
    public static void facebookGetInvitableFriends() {
        if (getInstance().mUseFacebook) {
            getInstance().mFacebook.getInvitableFriends(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("facebookGetInvitableFriends", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("facebookGetInvitableFriends", "fail", info);
                }
            });
        }
    }

    // @facebook
    public static void facebookSendRequest(String info) {
        if (getInstance().mUseFacebook) {
            getInstance().mFacebook.sendRequest(info, new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("facebookSendRequest", "success", info);
                }
                @Override
                public void onFail(String info) {
                    if (info.equals("cancel")) {
                        callSDKResult("facebookSendRequest", "cancel", "");
                    } else {
                        callSDKResult("facebookSendRequest", "fail", info);
                    }
                }
            });
        }
    }

    // @facebook
    public static boolean facebookIsGrantedPermission(String permission) {
        boolean ret = false;
        if (getInstance().mUseFacebook) {
            ret = getInstance().mFacebook.isGrantedPermission(permission);
        }
        return ret;
    }

    // @facebook
    public static void facebookAskPermission(String permission) {
        if (getInstance().mUseFacebook) {
            getInstance().mFacebook.askPermission(permission, new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("facebookAskPermission", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("facebookAskPermission", "fail", info);
                }
            });
        }
    }

    public static void googleShowAchievements() {
        if (getInstance().mUseGoogle) {
            getInstance().mGoogle.showAchievements(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("googleShowAchievements", "success", info);
                }

                @Override
                public void onFail(String info) {
                    callSDKResult("googleShowAchievements", "fail", info);
                }
            });
        }
    }
    public static void googleShowLeaderboards() {
        if (getInstance().mUseGoogle) {
            getInstance().mGoogle.showLeaderboards(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("googleShowLeaderboards", "success", info);
                }

                @Override
                public void onFail(String info) {
                    callSDKResult("googleShowLeaderboards", "fail", info);
                }
            });
        }
    }

    // @google
    public static void googleShowQuests() {
        if (getInstance().mUseGoogle) {
            getInstance().mGoogle.showQuests(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("googleShowQuests", "success", info);
                }

                @Override
                public void onFail(String info) {
                    callSDKResult("googleShowQuests", "fail", info);
                }
            });
        }
    }

    // @google
    public static void googleUpdateAchievements(String id, String steps) {
        if (getInstance().mUseGoogle) {
            getInstance().mGoogle.updateAchievements(id, Integer.parseInt(steps), new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("googleUpdateAchievements", "success", info);
                }

                @Override
                public void onFail(String info) {
                    callSDKResult("googleUpdateAchievements", "fail", info);
                }
            });
        }
    }

    // @google
    public static void googleUpdateLeaderboards(String id, String score) {
        if (getInstance().mUseGoogle) {
            getInstance().mGoogle.updateLeaderboards(id, Integer.parseInt(score), new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("googleUpdateLeaderboards", "success", info);
                }

                @Override
                public void onFail(String info) {
                    callSDKResult("googleUpdateLeaderboards", "fail", info);
                }
            });
        }
    }

    // @google
    public static void googleUpdateQuests(String id, String count) {
        if (getInstance().mUseGoogle) {
            getInstance().mGoogle.updateQuestEvents(id, Integer.parseInt(count), new PerpleGooglePlayServicesQuestsCallback() {
                @Override
                public void onSuccess() {
                    callSDKResult("googleUpdateQuests", "success", "");
                }

                @Override
                public void onFail(String info) {
                    callSDKResult("googleUpdateQuests", "fail", info);
                }

                @Override
                public void onComplete(String info) {
                    callSDKResult("googleUpdateQuests", "complete", info);
                }
            });
        }
    }

    // @adbrix
    public static void adbrixEvent(String id, String arg0, String arg1) {
        if (getInstance().mUseAdbrix) {
            getInstance().mAdbrix.setEvent(id, arg0, arg1);
        }
    }

    // @adbrix
    public static void adbrixStartSession() {
        if (getInstance().mUseAdbrix) {
            getInstance().mAdbrix.StartSession();
        }
    }

    // @adbrix
    public static void adbrixEndSession() {
        if (getInstance().mUseAdbrix) {
            getInstance().mAdbrix.EndSession();
        }
    }

    // @tapjoy
    public static void tapjoyEvent(String id, String arg0, String arg1) {
        if (getInstance().mUseTapjoy) {
            getInstance().mTapjoy.setEvent(id, arg0, arg1);
        }
    }

    // @tapjoy
    public static void tapjoySetPlacement(String placementName) {
        if (getInstance().mUseTapjoy) {
            getInstance().mTapjoy.setPlacement(placementName, new PerpleTapjoyPlacementCallback() {
                @Override
                public void onRequestSuccess() {
                    callSDKResult("tapjoySetPlacement", "success", "");
                }
                @Override
                public void onRequestFailure(String info) {
                    callSDKResult("tapjoySetPlacement", "fail", info);
                }
                @Override
                public void onContentReady() {
                    callSDKResult("tapjoySetPlacement", "ready", "");
                }
                @Override
                public void onShow() {
                    // not use
                }
                @Override
                public void onWait() {
                    // not use
                }
                @Override
                public void onDismiss() {
                    // not use
                }
            });
        }
    }

    // @tapjoy
    public static void tapjoyShowPlacement(String placementName) {
        if (getInstance().mUseTapjoy) {
            getInstance().mTapjoy.showPlacement(placementName, new PerpleTapjoyPlacementCallback() {
                @Override
                public void onShow() {
                    callSDKResult("tapjoyShowPlacement", "show", "");
                }
                @Override
                public void onWait() {
                    callSDKResult("tapjoyShowPlacement", "wait", "");
                }
                @Override
                public void onDismiss() {
                    callSDKResult("tapjoyShowPlacement", "dismiss", "");
                }
                @Override
                public void onRequestSuccess() {
                    // not use
                }
                @Override
                public void onRequestFailure(String info) {
                    // not use
                }
                @Override
                public void onContentReady() {
                    // not use
                }
            });
        }
    }

    // @tapjoy
    public static void tapjoyGetCurrency() {
        if (getInstance().mUseTapjoy) {
            getInstance().mTapjoy.getCurrency(new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("tapjoyGetCurrency", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("tapjoyGetCurrency", "fail", info);
                }
            });
        }
    }

    // @tapjoy
    public static void tapjoySetEarnedCurrencyCallback() {
        if (getInstance().mUseTapjoy) {
            getInstance().mTapjoy.setEarnedCurrencyCallback(new PerpleSDKUpdateCallback() {
                @Override
                public void onUpdate(String info) {
                    callSDKResult("tapjoySetEarnedCurrencyCallback", "earn", info);
                }
                @Override
                public void onError(String info) {
                    callSDKResult("tapjoySetEarnedCurrencyCallback", "error", info);
                }
            });
        }
    }

    // @tapjoy
    public static void tapjoySpendCurrency(int amount) {
        if (getInstance().mUseTapjoy) {
            getInstance().mTapjoy.spendCurrency(amount, new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("tapjoySpendCurrency", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("tapjoySpendCurrency", "fail", info);
                }
            });
        }
    }

    // @tapjoy
    public static void tapjoyAwardCurrency(int amount) {
        if (getInstance().mUseTapjoy) {
            getInstance().mTapjoy.awardCurrency(amount, new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    callSDKResult("tapjoyAwardCurrency", "success", info);
                }
                @Override
                public void onFail(String info) {
                    callSDKResult("tapjoyAwardCurrency", "fail", info);
                }
            });
        }
    }

    // @naver
    public static void naverLogin() {
        if (getInstance().mUseNaver) {
            getInstance().mNaver.login(new PerpleNaverLoginCallback() {
                @Override
                public void onSuccess(String accessToken, String refreshToken, long expiresAt, String tokenType) {
                    JSONObject info = new JSONObject();
                    try {
                        info.put("accessToken", accessToken);
                        info.put("refreshToken", refreshToken);
                        info.put("expiresAt", String.valueOf(expiresAt));
                        info.put("tokenType", tokenType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callSDKResult("naverLogin", "success", info.toString());
                }

                @Override
                public void onFail(String errorCode, String errorDesc) {
                    JSONObject info = new JSONObject();
                    try {
                        info.put("errorCode", errorCode);
                        info.put("errorDesc", errorDesc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callSDKResult("naverLogin", "fail", info.toString());
                }
            });
        }
    }

    // @naver
    public static void naverLogout(int deleteToken) {
        if (getInstance().mUseNaver) {
            if (deleteToken == 0) {
                getInstance().mNaver.logout();
            } else {
                getInstance().mNaver.logoutAndDeleteToken();
            }
        }
    }

    // @naver
    public static void naverRequestApi(String url) {
        if (getInstance().mUseNaver) {
            getInstance().mNaver.requestApi(url);
        }
    }

    // @naver
    public static boolean naverCafeIsShowGlink() {
        boolean ret = false;
        if (getInstance().mUseNaverCafe) {
            ret = getInstance().mNaver.isShowGlink();
        }
        return ret;
    }

    // @naver
    public static void naverCafeStart(int tapNumber) {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafeStart(tapNumber);
        }
    }

    // @naver
    public static void naverCafeStop() {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafeStop();
        }
    }

    // @naver
    public static void naverCafePopBackStack() {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafePopBackStack();
        }
    }

    // @naver
    public static void naverCafeStartWrite(int menuId, String subject, String text) {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafeStartWrite(menuId, subject, text);
        }
    }

    // @naver
    public static void naverCafeStartImageWrite(int menuId, String subject, String text, String imageUrl) {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafeStartImageWrite(menuId, subject, text, imageUrl);
        }
    }

    // @naver
    public static void naverCafeStartVideoWrite(int menuId, String subject, String text, String videoUrl) {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafeStartVideoWrite(menuId, subject, text, videoUrl);
        }
    }

    // @naver
    public static void naverCafeSyncGameUserId(String gameUserId) {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafeSyncGameUserId(gameUserId);
        }
    }

    // @naver
    public static void naverCafeSetUseVideoRecord(int flag) {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafeSetUseVideoRecord(flag);
        }
    }

    // @naver
    public static void naverCafeSetCallback() {
        if (getInstance().mUseNaverCafe) {
            getInstance().mNaver.cafeSetCallback(new PerpleNaverCafeCallback() {
                @Override
                public void onSdkStarted() {
                    callSDKResult("naverCafeSetCallback", "start", "");
                }
                @Override
                public void onSdkStopped() {
                    callSDKResult("naverCafeSetCallback", "stop", "");
                }
                @Override
                public void onClickAppSchemeBanner(String appScheme) {
                    callSDKResult("naverCafeSetCallback", "scheme", appScheme);
                }
                @Override
                public void onJoined() {
                    callSDKResult("naverCafeSetCallback", "join", "");
                }
                @Override
                public void onPostedArticle(int menuId, int imageCount, int videoCount) {
                    JSONObject info = new JSONObject();
                    try {
                        info.put("menuId", String.valueOf(menuId));
                        info.put("imageCount", String.valueOf(imageCount));
                        info.put("videoCount", String.valueOf(videoCount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callSDKResult("naverCafeSetCallback", "article", info.toString());
                }
                @Override
                public void onPostedComment(int articleId) {
                    callSDKResult("naverCafeSetCallback", "comment", String.valueOf(articleId));
                }
                @Override
                public void onVoted(int articleId) {
                    callSDKResult("naverCafeSetCallback", "vote", String.valueOf(articleId));
                }
                @Override
                public void onScreenshotClick() {
                    callSDKResult("naverCafeSetCallback", "screenshot", "");
                }
                @Override
                public void onRecordFinished(String uri) {
                    callSDKResult("naverCafeSetCallback", "record", uri);
                }
            });
        }
    }

    // 구글 플레이 게임 플레이어 정보 삽입
    public static String addGoogleLoginInfo(String info) {
        try {
            JSONObject json_info = new JSONObject(info);
            if (json_info.has("player") == false) {
                JSONObject player = getInstance().mGoogle.getPlayerProfile();
                json_info.put("player", player);
                return json_info.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static String getErrorInfo(String code, String msg) {
        JSONObject outData = new JSONObject();
        try {
            outData.put("code", code);
            outData.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outData.toString();
    }

    public static String getErrorInfoFromFirebaseException(Exception e) {
        if (e instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException)e;
            return PerpleSDK.getErrorInfo(authException.getErrorCode(), authException.getMessage());
        } else {
            return PerpleSDK.getErrorInfo(ERROR_UNKNOWN, e.toString());
        }
    }

    public static String getErrorInfoFromFacebookException(Exception e) {
        if (e instanceof FacebookException) {
            FacebookException authException = (FacebookException)e;
            return PerpleSDK.getErrorInfo(ERROR_FACEBOOK_LOGIN, authException.getMessage());
        } else {
            return PerpleSDK.getErrorInfo(ERROR_UNKNOWN, e.toString());
        }
    }

    private static void callSDKResult(final String id, final String result, final String info) {
        Log.d(LOG_TAG, "callSDKResult - " + "id:" + id + ", ret:" + result + ",info:" + info);

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

    private static native int nativeInitJNI(Activity activity);
    private static native int nativeInitSDK();
    private static native int nativeGetSDKVersion();
    private static native String nativeGetSDKVersionString();
    private static native int nativeSDKResult(String id, String result, String info);
}
