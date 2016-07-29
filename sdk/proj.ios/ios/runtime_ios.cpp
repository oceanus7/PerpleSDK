//
//  runtime_ios.cpp
//  PerpleSDK
//
//  Created by PerpleLab on 2016. 7. 27..
//  Copyright © 2016년 PerpleLab. All rights reserved.
//

#include "PerpleCore.h"
#include "PerpleFirebaseCpp.h"

void setFCMPushOnForeground(int funcID, int isReceive) {

}

void setFCMTokenRefresh(int funcID) {
    PerpleCore::OnSDKResult(funcID, "refresh", "");
    PerpleCore::OnSDKResult(funcID, "error", "");
}

void getFCMToken(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void sendFCMPushMessage(int funcID, const char* data) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void sendFCMPushMessageToGroup(int funcID, const char* groupKey, const char* data) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void autoLogin(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void loginAnonymously(int funcID) {
    PerpleFirebaseCpp::GetInstance()->SignInAnonymously([&funcID](const char* result, const char* info) {
        // "success" :
        //{
        //    "profile":
        //    {
        //        "uid":"@uid",
        //        "name":"@name",
        //        "email":"@email",
        //        "photoUrl":"@photoUrl",
        //        "providerId":"@providerId"
        //    },
        //    "providerData":
        //    [
        //        {
        //            "uid":"@uid",
        //            "name":"@name",
        //            "email":"@email",
        //            "photoUrl":"@photoUrl",
        //            "providerId":"@providerId"
        //        }
        //    ]
        //    "pushToken":
        //    {
        //        "iid":"@iid",
        //        "token":"@token"
        //    }
        //}
        // "fail" : {"code":"-999","subcode":"0","msg":"Unknown error"}
        PerpleCore::OnSDKResult(funcID, result, info);
    });
}

void loginGoogle(int funcID) {
    const char* google_id_token = "";

    PerpleFirebaseCpp::GetInstance()->SignInWithGoogleLogin(google_id_token, [&funcID](const char* result, const char* info) {
        // "success" :
        //{
        //    "profile":
        //    {
        //        "uid":"@uid",
        //        "name":"@name",
        //        "email":"@email",
        //        "photoUrl":"@photoUrl",
        //        "providerId":"@providerId"
        //    },
        //    "providerData":
        //    [
        //        {
        //            "uid":"@uid",
        //            "name":"@name",
        //            "email":"@email",
        //            "photoUrl":"@photoUrl",
        //            "providerId":"@providerId"
        //        }
        //    ]
        //    "pushToken":
        //    {
        //        "iid":"@iid",
        //        "token":"@token"
        //    }
        //}
        // "fail" : {"code":"-999","subcode":"0","msg":"Unknown error"}
        // "cancel" : ""
        PerpleCore::OnSDKResult(funcID, result, info);
    });
}

void loginFacebook(int funcID) {
    const char* facebook_token = "";

    PerpleFirebaseCpp::GetInstance()->SignInWithFacebookLogin(facebook_token, [&funcID](const char* result, const char* info) {
        // "success" :
        //{
        //    "profile":
        //    {
        //        "uid":"@uid",
        //        "name":"@name",
        //        "email":"@email",
        //        "photoUrl":"@photoUrl",
        //        "providerId":"@providerId"
        //    },
        //    "providerData":
        //    [
        //        {
        //            "uid":"@uid",
        //            "name":"@name",
        //            "email":"@email",
        //            "photoUrl":"@photoUrl",
        //            "providerId":"@providerId"
        //        }
        //    ]
        //    "pushToken":
        //    {
        //        "iid":"@iid",
        //        "token":"@token"
        //    }
        //}
        // "fail" : {"code":"-999","subcode":"0","msg":"Unknown error"}
        // "cancel" : ""
        PerpleCore::OnSDKResult(funcID, result, info);
    });
}

void loginEmail(int funcID, const char* email, const char* password) {
    PerpleFirebaseCpp::GetInstance()->SignInWithEmailAndPassword(email, password, [&funcID](const char* result, const char* info) {
        // "success" :
        //{
        //    "profile":
        //    {
        //        "uid":"@uid",
        //        "name":"@name",
        //        "email":"@email",
        //        "photoUrl":"@photoUrl",
        //        "providerId":"@providerId"
        //    },
        //    "providerData":
        //    [
        //        {
        //            "uid":"@uid",
        //            "name":"@name",
        //            "email":"@email",
        //            "photoUrl":"@photoUrl",
        //            "providerId":"@providerId"
        //        }
        //    ]
        //    "pushToken":
        //    {
        //        "iid":"@iid",
        //        "token":"@token"
        //    }
        //}
        // "fail" : {"code":"-999","subcode":"0","msg":"Unknown error"}
        PerpleCore::OnSDKResult(funcID, result, info);
    });
}

void linkWithGoogle(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
    PerpleCore::OnSDKResult(funcID, "cancel", "");
}

void linkWithFacebook(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
    PerpleCore::OnSDKResult(funcID, "cancel", "");
}

void linkWithEmail(int funcID, const char* email, const char* password) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void unlinkWithGoogle(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void unlinkWithFacebook(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void unlinkWithEmail(int funcID, const char* email, const char* password) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void createUserWithEmail(int funcID, const char* email, const char* password) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void logout(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void deleteUser(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void facebookLogin(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
    PerpleCore::OnSDKResult(funcID, "cancel", "");
}

void facebookGetFriends(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void facebookGetInvitableFriends(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void facebookSendRequest(int funcID, const char* data) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
    PerpleCore::OnSDKResult(funcID, "cancel", "");
}

bool facebookIsGrantedPermission(int funcID, const char* permission) {
    return false;
}

void facebookAskPermission(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void adbrixEvent(int funcID, const char* cmd, const char* arg0, const char* arg1) {

}

void adbrixStartSession(int funcID) {

}

void adbrixEndSession(int funcID) {

}

void tapjoyEvent(int funcID, const char* cmd, const char* arg0, const char* arg1) {

}

void tapjoySetTrackPurchase(int funcID, int isTrackPurchase) {

}

void tapjoySetPlacement(int funcID, const char* placementName) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
    PerpleCore::OnSDKResult(funcID, "ready", "");
    PerpleCore::OnSDKResult(funcID, "purchase", "");
    PerpleCore::OnSDKResult(funcID, "reward", "");
    PerpleCore::OnSDKResult(funcID, "error", "");
}

void tapjoyShowPlacement(int funcID, const char* placementName) {
    PerpleCore::OnSDKResult(funcID, "show", "");
    PerpleCore::OnSDKResult(funcID, "wait", "");
    PerpleCore::OnSDKResult(funcID, "dismiss", "");
    PerpleCore::OnSDKResult(funcID, "error", "");
}

void tapjoyGetCurrency(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void tapjoySetEarnedCurrencyCallback(int funcID) {
    PerpleCore::OnSDKResult(funcID, "earn", "");
    PerpleCore::OnSDKResult(funcID, "error", "");
}

void tapjoySpendCurrency(int funcID, int amount) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void tapjoyAwardCurrency(int funcID, int amount) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void naverLogin(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void naverLogout(int funcID, int isDeleteToken) {

}

void naverRequestApi(int funcID, const char* url) {

}

bool naverCafeIsShowGlink(int funcID) {
    return false;
}

void naverCafeShowWidgetWhenUnloadSdk(int funcID, int isShowWidget) {

}

void naverCafeStopWidget(int funcID) {

}

void naverCafeStart(int funcID, int tapNumber) {

}

void naverCafeStop(int funcID) {

}

void naverCafePopBackStack(int funcID) {

}

void naverCafeStartWrite(int funcID, int menuId, const char* subject, const char* text) {

}

void naverCafeStartImageWrite(int funcID, int menuId, const char* subject, const char* text, const char* imageUrl) {

}

void naverCafeStartVideoWrite(int funcID, int menuId, const char* subject, const char* text, const char* videoUrl) {

}

void naverCafeSyncGameUserId(int funcID, const char* gameUserId) {

}

void naverCafeSetUseVideoRecord(int funcID, int isSetUseVideoRecord) {

}

void naverCafeSetCallback(int funcID) {
    PerpleCore::OnSDKResult(funcID, "start", "");
    PerpleCore::OnSDKResult(funcID, "stop", "");
    PerpleCore::OnSDKResult(funcID, "scheme", "");
    PerpleCore::OnSDKResult(funcID, "join", "");
    PerpleCore::OnSDKResult(funcID, "article", "");
    PerpleCore::OnSDKResult(funcID, "comment", "");
    PerpleCore::OnSDKResult(funcID, "vote", "");
    PerpleCore::OnSDKResult(funcID, "screenshot", "");
    PerpleCore::OnSDKResult(funcID, "record", "");
    PerpleCore::OnSDKResult(funcID, "error", "");
}

void googleLogin(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
    PerpleCore::OnSDKResult(funcID, "cancel", "");
}

void googleShowAchievements(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void googleShowLeaderboards(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void googleShowQuests(int funcID) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void googleUpdateAchievements(int funcID, const char* id, const char* steps) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void googleUpdateLeaderboards(int funcID, const char* id, const char* score) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void googleUpdateQuests(int funcID, const char* id, const char* count) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
    PerpleCore::OnSDKResult(funcID, "complete", "");
}

void setBilling(int funcID, const char* checkReceiptServerUrl) {
    PerpleCore::OnSDKResult(funcID, "purchase", "");
    PerpleCore::OnSDKResult(funcID, "error", "");
}

void confirmPurchase(int funcID, const char* orderIds) {
}

void purchase(int funcID, const char* sku, const char* payload) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}

void subscription(int funcID, const char* sku, const char* payload) {
    PerpleCore::OnSDKResult(funcID, "success", "");
    PerpleCore::OnSDKResult(funcID, "fail", "");
}
