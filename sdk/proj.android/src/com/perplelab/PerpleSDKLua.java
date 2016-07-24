package com.perplelab;

import org.json.JSONObject;

import com.perplelab.PerpleSDK;
import com.perplelab.firebase.PerpleFirebase;
import com.perplelab.naver.PerpleNaverCafeCallback;
import com.perplelab.tapjoy.PerpleTapjoyPlacementCallback;

public class PerpleSDKLua {

    // @firebase fcm
    public static void setFCMPushOnForeground(final int funcID, int isReceive) {
        PerpleSDK.IsReceivePushOnForeground = (isReceive == 0 ? false : true);
    }

    // @firebase fcm
    public static void setFCMTokenRefresh(final int funcID) {
        PerpleSDK.setFCMTokenRefreshCallback(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "refresh", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "error", info);
            }
        });
    }

    // @firebase fcm
    public static void getFCMToken(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            String info = PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized.");
            PerpleSDK.callSDKResult(funcID, "fail", info);
            return;
        }

        JSONObject obj = PerpleSDK.getFirebase().getPushToken();
        if (obj != null) {
            PerpleSDK.callSDKResult(funcID, "success", obj.toString());
        } else {
            PerpleSDK.callSDKResult(funcID, "fail", PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, "JSON exception"));
        }
    }

    // @firebase fcm
    public static void sendPushMessage(final int funcID, String data) {
        PerpleSDK.setSendPushMessageCallback(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });

        PerpleSDK.getFirebase().sendUpstreamMessage(data);
    }

    // @firebase fcm
    public static void sendPushMessageToGroup(final int funcID, String groupKey, String data) {
        PerpleSDK.setSendPushMessageCallback(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });

        PerpleSDK.getFirebase().sendUpstreamMessage(groupKey, data);
    }

    // @firebase
    public static void autoLogin(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().autoLogin(
            new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    PerpleSDK.callSDKResult(funcID, "success", info);
                }
                @Override
                public void onFail(String info) {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            });
    }

    // @firebase
    public static void loginAnonymously(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().loginAnonymously(
            new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    PerpleSDK.callSDKResult(funcID, "success", info);
                }
                @Override
                public void onFail(String info) {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            });
    }

    // @firebase, @google
    public static void loginGoogle(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        if (PerpleSDK.getGoogle() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        PerpleSDK.getGoogle().login(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String idToken) {
                PerpleSDK.getFirebase().signInWithCredential(
                        PerpleFirebase.getGoogleCredential(idToken),
                        new PerpleSDKCallback() {
                            @Override
                            public void onSuccess(String info) {
                                PerpleSDK.callSDKResult(funcID, "success", info);
                            }
                            @Override
                            public void onFail(String info) {
                                PerpleSDK.callSDKResult(funcID, "fail", info);
                            }
                        });
            }
            @Override
            public void onFail(String info) {
                if (info.equals("cancel")) {
                    PerpleSDK.callSDKResult(funcID, "cancel", "");
                } else {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            }
        });
    }

    // @firebase, @facebook
    public static void loginFacebook(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        if (PerpleSDK.getFacebook() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        PerpleSDK.getFacebook().login(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String token) {
                PerpleSDK.getFirebase().signInWithCredential(
                        PerpleFirebase.getFacebookCredential(token),
                        new PerpleSDKCallback() {
                            @Override
                            public void onSuccess(String info) {
                                PerpleSDK.callSDKResult(funcID, "success", info);
                            }
                            @Override
                            public void onFail(String info) {
                                PerpleSDK.callSDKResult(funcID, "fail", info);
                            }
                        });
            }
            @Override
            public void onFail(String info) {
                if (info.equals("cancel")) {
                    PerpleSDK.callSDKResult(funcID, "cancel", "");
                } else {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            }
        });
    }

    // @firebase
    public static void loginEmail(final int funcID, String email, String password) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().loginEmail(
            email,
            password,
            new PerpleSDKCallback() {
                @Override
                public void onSuccess(String info) {
                    PerpleSDK.callSDKResult(funcID, "success", info);
                }
                @Override
                public void onFail(String info) {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            });
    }

    // @firebase, @google
    public static void linkWithGoogle(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        if (PerpleSDK.getGoogle() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        PerpleSDK.getGoogle().login(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String idToken) {
                PerpleSDK.getFirebase().linkWithCredential(
                        PerpleFirebase.getGoogleCredential(idToken),
                        new PerpleSDKCallback() {
                            @Override
                            public void onSuccess(String info) {
                                PerpleSDK.callSDKResult(funcID, "success", info);
                            }
                            @Override
                            public void onFail(String info) {
                                PerpleSDK.callSDKResult(funcID, "fail", info);
                            }
                        });
            }
            @Override
            public void onFail(String info) {
                if (info.equals("cancel")) {
                    PerpleSDK.callSDKResult(funcID, "cancel", "");
                } else {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            }
        });
    }

    // @firebase, @facebook
    public static void linkWithFacebook(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        if (PerpleSDK.getFacebook() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        PerpleSDK.getFacebook().login(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String token) {
                PerpleSDK.getFirebase().linkWithCredential(
                        PerpleFirebase.getFacebookCredential(token),
                        new PerpleSDKCallback() {
                            @Override
                            public void onSuccess(String info) {
                                PerpleSDK.callSDKResult(funcID, "success", info);
                            }
                            @Override
                            public void onFail(String info) {
                                PerpleSDK.callSDKResult(funcID, "fail", info);
                            }
                        });
            }
            @Override
            public void onFail(String info) {
                if (info.equals("cancel")) {
                    PerpleSDK.callSDKResult(funcID, "cancel", "");
                } else {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            }
        });
    }

    // @firebase
    public static void linkWithEmail(final int funcID, String email, String password) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().linkWithCredential(
                PerpleFirebase.getEmailCredential(email, password),
                new PerpleSDKCallback() {
                    @Override
                    public void onSuccess(String info) {
                        PerpleSDK.callSDKResult(funcID, "success", info);
                    }
                    @Override
                    public void onFail(String info) {
                        PerpleSDK.callSDKResult(funcID, "fail", info);
                    }
                });
    }

    // @firebase, @google
    public static void unlinkWithGoogle(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().unlink("google.com", new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @firebase, @facebook
    public static void unlinkWithFacebook(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().unlink("facebook.com", new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @firebase
    public static void unlinkWithEmail(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().unlink("email", new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @firebase
    public static void createUserWithEmail(final int funcID, String email, String password) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().createUserWithEmail(email, password, new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @firebase
    public static void logout(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().logout(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @firebase
    public static void deleteUser(final int funcID) {
        if (PerpleSDK.getFirebase() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        PerpleSDK.getFirebase().deleteUser(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @facebook
    public static void facebookGetFriends(final int funcID) {
        if (PerpleSDK.getFacebook() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        PerpleSDK.getFacebook().getFriends(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @facebook
    public static void facebookGetInvitableFriends(final int funcID) {
        if (PerpleSDK.getFacebook() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        PerpleSDK.getFacebook().getInvitableFriends(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @facebook
    public static void facebookSendRequest(final int funcID, String info) {
        if (PerpleSDK.getFacebook() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        PerpleSDK.getFacebook().sendRequest(info, new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                if (info.equals("cancel")) {
                    PerpleSDK.callSDKResult(funcID, "cancel", "");
                } else {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            }
        });
    }

    // @facebook
    public static boolean facebookIsGrantedPermission(final int funcID, String permission) {
        boolean ret = false;
        if (PerpleSDK.getFacebook() != null) {
            ret = PerpleSDK.getFacebook().isGrantedPermission(permission);
        }
        return ret;
    }

    // @facebook
    public static void facebookAskPermission(final int funcID, String permission) {
        if (PerpleSDK.getFacebook() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        PerpleSDK.getFacebook().askPermission(permission, new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @adbrix
    public static void adbrixEvent(final int funcID, String id, String arg0, String arg1) {
        if (PerpleSDK.getAdbrix() != null) {
            PerpleSDK.getAdbrix().setEvent(id, arg0, arg1);
        }
    }

    // @adbrix
    public static void adbrixStartSession(final int funcID) {
        if (PerpleSDK.getAdbrix() != null) {
            PerpleSDK.getAdbrix().StartSession();
        }
    }

    // @adbrix
    public static void adbrixEndSession(final int funcID) {
        if (PerpleSDK.getAdbrix() != null) {
            PerpleSDK.getAdbrix().EndSession();
        }
    }

    // @tapjoy
    public static void tapjoyEvent(final int funcID, String id, String arg0, String arg1) {
        if (PerpleSDK.getTapjoy() != null) {
            PerpleSDK.getTapjoy().setEvent(id, arg0, arg1);
        }
    }

    // @tapjoy
    public static void tapjoySetTrackPurchase(final int funcID, int isTrackPurchase) {
        if (PerpleSDK.getTapjoy() != null) {
            PerpleSDK.getTapjoy().setTrackPurchase(isTrackPurchase == 0 ? false : true);
        }
    }

    // @tapjoy
    public static void tapjoySetPlacement(final int funcID, String placementName) {
        if (PerpleSDK.getTapjoy() == null) {
            PerpleSDK.callSDKResult(funcID, "error",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        PerpleSDK.getTapjoy().setPlacement(placementName, new PerpleTapjoyPlacementCallback() {
            @Override
            public void onRequestSuccess() {
                PerpleSDK.callSDKResult(funcID, "success", "");
            }
            @Override
            public void onRequestFailure(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
            @Override
            public void onContentReady() {
                PerpleSDK.callSDKResult(funcID, "ready", "");
            }
            @Override
            public void onPurchaseRequest(String info) {
                PerpleSDK.callSDKResult(funcID, "purchase", info);
            }
            @Override
            public void onRewardRequest(String info) {
                PerpleSDK.callSDKResult(funcID, "reward", info);
            }
            @Override
            public void onError(String info) {
                PerpleSDK.callSDKResult(funcID, "error", info);
            }
            @Override
            public void onShow() {}
            @Override
            public void onWait() {}
            @Override
            public void onDismiss() {}
        });
    }

    // @tapjoy
    public static void tapjoyShowPlacement(final int funcID, String placementName) {
        if (PerpleSDK.getTapjoy() == null) {
            PerpleSDK.callSDKResult(funcID, "error",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        PerpleSDK.getTapjoy().showPlacement(placementName, new PerpleTapjoyPlacementCallback() {
            @Override
            public void onShow() {
                PerpleSDK.callSDKResult(funcID, "show", "");
            }
            @Override
            public void onWait() {
                PerpleSDK.callSDKResult(funcID, "wait", "");
            }
            @Override
            public void onDismiss() {
                PerpleSDK.callSDKResult(funcID, "dismiss", "");
            }
            @Override
            public void onError(String info) {
                PerpleSDK.callSDKResult(funcID, "error", info);
            }
            @Override
            public void onRequestSuccess() {}
            @Override
            public void onRequestFailure(String info) {}
            @Override
            public void onContentReady() {}
            @Override
            public void onPurchaseRequest(String info) {}
            @Override
            public void onRewardRequest(String info) {}
        });
    }

    // @tapjoy
    public static void tapjoyGetCurrency(final int funcID) {
        if (PerpleSDK.getTapjoy() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        PerpleSDK.getTapjoy().getCurrency(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @tapjoy
    public static void tapjoySetEarnedCurrencyCallback(final int funcID) {
        if (PerpleSDK.getTapjoy() == null) {
            PerpleSDK.callSDKResult(funcID, "error",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        PerpleSDK.getTapjoy().setEarnedCurrencyCallback(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "earn", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "error", info);
            }
        });
    }

    // @tapjoy
    public static void tapjoySpendCurrency(final int funcID, int amount) {
        if (PerpleSDK.getTapjoy() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        PerpleSDK.getTapjoy().spendCurrency(amount, new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @tapjoy
    public static void tapjoyAwardCurrency(final int funcID, int amount) {
        if (PerpleSDK.getTapjoy() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_TAPJOY_NOTINITIALIZED, "Tapjoy is not initialized."));
            return;
        }

        PerpleSDK.getTapjoy().awardCurrency(amount, new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @naver
    public static void naverLogin(final int funcID) {
        if (PerpleSDK.getNaver() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_NAVER_NOTINITIALIZED, "Naver is not initialized."));
            return;
        }

        PerpleSDK.getNaver().login(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }

            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @naver
    public static void naverLogout(final int funcID, int isDeleteToken) {
        if (PerpleSDK.getNaver() != null) {
            if (isDeleteToken == 0) {
                PerpleSDK.getNaver().logout();
            } else {
                PerpleSDK.getNaver().logoutAndDeleteToken();
            }
        }
    }

    // @naver
    public static void naverRequestApi(final int funcID, String url) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().requestApi(url);
        }
    }

    // @naver
    public static boolean naverCafeIsShowGlink(final int funcID) {
        boolean ret = false;
        if (PerpleSDK.getNaver() != null) {
            ret = PerpleSDK.getNaver().cafeIsShowGlink();
        }
        return ret;
    }

    // @naver
    public static void naverCafeStart(final int funcID, int tapNumber) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().cafeStart(tapNumber);
        }
    }

    // @naver
    public static void naverCafeStop(final int funcID) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().cafeStop();
        }
    }

    // @naver
    public static void naverCafePopBackStack(final int funcID) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().cafePopBackStack();
        }
    }

    // @naver
    public static void naverCafeStartWrite(final int funcID, int menuId, String subject, String text) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().cafeStartWrite(menuId, subject, text);
        }
    }

    // @naver
    public static void naverCafeStartImageWrite(final int funcID, int menuId, String subject, String text, String imageUrl) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().cafeStartImageWrite(menuId, subject, text, imageUrl);
        }
    }

    // @naver
    public static void naverCafeStartVideoWrite(final int funcID, int menuId, String subject, String text, String videoUrl) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().cafeStartVideoWrite(menuId, subject, text, videoUrl);
        }
    }

    // @naver
    public static void naverCafeSyncGameUserId(final int funcID, String gameUserId) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().cafeSyncGameUserId(gameUserId);
        }
    }

    // @naver
    public static void naverCafeSetUseVideoRecord(final int funcID, int isSetUseVideoRacord) {
        if (PerpleSDK.getNaver() != null) {
            PerpleSDK.getNaver().cafeSetUseVideoRecord(isSetUseVideoRacord == 0 ? false : true);
        }
    }

    // @naver
    public static void naverCafeSetCallback(final int funcID) {
        if (PerpleSDK.getNaver() == null) {
            PerpleSDK.callSDKResult(funcID, "error",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_NAVER_NOTINITIALIZED, "Naver is not initialized."));
            return;
        }

        PerpleSDK.getNaver().cafeSetCallback(new PerpleNaverCafeCallback() {
            @Override
            public void onSdkStarted() {
                PerpleSDK.callSDKResult(funcID, "start", "");
            }
            @Override
            public void onSdkStopped() {
                PerpleSDK.callSDKResult(funcID, "stop", "");
            }
            @Override
            public void onClickAppSchemeBanner(String appScheme) {
                PerpleSDK.callSDKResult(funcID, "scheme", appScheme);
            }
            @Override
            public void onJoined() {
                PerpleSDK.callSDKResult(funcID, "join", "");
            }
            @Override
            public void onPostedArticle(String info) {
                PerpleSDK.callSDKResult(funcID, "article", info);
            }
            @Override
            public void onPostedComment(int articleId) {
                PerpleSDK.callSDKResult(funcID, "comment", String.valueOf(articleId));
            }
            @Override
            public void onVoted(int articleId) {
                PerpleSDK.callSDKResult(funcID, "vote", String.valueOf(articleId));
            }
            @Override
            public void onScreenshotClick() {
                PerpleSDK.callSDKResult(funcID, "screenshot", "");
            }
            @Override
            public void onRecordFinished(String uri) {
                PerpleSDK.callSDKResult(funcID, "record", uri);
            }
            @Override
            public void onError(String info) {
                PerpleSDK.callSDKResult(funcID, "error", info);
            }
        });
    }

    // @google
    public static void googleShowAchievements(final int funcID) {
        if (PerpleSDK.getGoogle() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        PerpleSDK.getGoogle().showAchievements(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @google
    public static void googleShowLeaderboards(final int funcID) {
        if (PerpleSDK.getGoogle() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        PerpleSDK.getGoogle().showLeaderboards(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @google
    public static void googleShowQuests(final int funcID) {
        if (PerpleSDK.getGoogle() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        PerpleSDK.getGoogle().showQuests(new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @google
    public static void googleUpdateAchievements(final int funcID, String id, String steps) {
        if (PerpleSDK.getGoogle() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        PerpleSDK.getGoogle().updateAchievements(id, Integer.parseInt(steps), new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @google
    public static void googleUpdateLeaderboards(final int funcID, String id, String score) {
        if (PerpleSDK.getGoogle() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        PerpleSDK.getGoogle().updateLeaderboards(id, Integer.parseInt(score), new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @google
    public static void googleUpdateQuests(final int funcID, String id, String count) {
        if (PerpleSDK.getGoogle() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        PerpleSDK.getGoogle().updateQuestEvents(id, Integer.parseInt(count), new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                if (info.equals("success")) {
                    PerpleSDK.callSDKResult(funcID, "success", "");
                } else {
                    PerpleSDK.callSDKResult(funcID, "complete", info);
                }
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "fail", info);
            }
        });
    }

    // @billing
    public static void setBilling(final int funcID, String checkReceiptServerUrl) {
        if (PerpleSDK.getBilling() == null) {
            PerpleSDK.callSDKResult(funcID, "error",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_BILLING_NOTINITIALIZED, "Billing is not initialized."));
            return;
        }

        PerpleSDK.getBilling().startSetup(checkReceiptServerUrl, new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "purchase", info);
            }
            @Override
            public void onFail(String info) {
                PerpleSDK.callSDKResult(funcID, "error", info);
            }
        });
    }

    // @billing
    public static void purchase(final int funcID, String sku, String payload) {
        if (PerpleSDK.getBilling() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_BILLING_NOTINITIALIZED, "Billing is not initialized."));
            return;
        }

        PerpleSDK.getBilling().purchase(sku, payload, new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                if (info.equals("cancel")) {
                    PerpleSDK.callSDKResult(funcID, "cancel", "");
                } else {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            }
        });
    }

    // @billing
    public static void subscription(final int funcID, String sku, String payload) {
        if (PerpleSDK.getBilling() == null) {
            PerpleSDK.callSDKResult(funcID, "fail",
                    PerpleSDK.getErrorInfo(PerpleSDK.ERROR_BILLING_NOTINITIALIZED, "Billing is not initialized."));
            return;
        }

        PerpleSDK.getBilling().subscription(sku, payload, new PerpleSDKCallback() {
            @Override
            public void onSuccess(String info) {
                PerpleSDK.callSDKResult(funcID, "success", info);
            }
            @Override
            public void onFail(String info) {
                if (info.equals("cancel")) {
                    PerpleSDK.callSDKResult(funcID, "cancel", "");
                } else {
                    PerpleSDK.callSDKResult(funcID, "fail", info);
                }
            }
        });
    }
}

