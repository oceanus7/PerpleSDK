#include "lua_perplesdk.h"
#include "tolua_fix.h"
#include "PerpleCore.h"
#include "lua_perplesdk_macro.h"

#define LOG_TAG "PerpleSDKLua"

#if defined(__ANDROID__)
#include <android/log.h>
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#else
#define LOG(...)
#endif

void executeLuaFunction(lua_State* L, int funcID, int numArgs)
{
    int funcIdx = -(numArgs + 1);

    toluafix_get_function_by_refid(L, funcID);
    if (!lua_isfunction(L, -1))
    {
        LOG("lua callback function id is invalid");
        lua_pop(L, numArgs + 1);
        return;
    }

    lua_insert(L, funcIdx);

    int traceback = 0;

    lua_getglobal(L, "__G__TRACKBACK__");
    if (!lua_isfunction(L, -1))
    {
        lua_pop(L, 1);
    }
    else
    {
        lua_insert(L, funcIdx - 1);
        traceback = funcIdx - 1;
    }

    int error = lua_pcall(L, numArgs, 1, traceback);
    if (error)
    {
        LOG("lua_pcall is return error : %d", error);
        if (traceback == 0)
        {
            lua_pop(L, 1);
        }
        else
        {
            lua_pop(L, 2);
        }
        return;
    }

    lua_pop(L, 1);

    if (traceback)
    {
        lua_pop(L, 1);
    }
}

void onSdkResult(lua_State* L, const int funcID, const std::string result, const std::string info)
{
    LOG("Lua callback, result - funcID:%d, ret:%s, info:%s", funcID, result.c_str(), info.c_str());

    lua_pushstring(L, result.c_str());
    lua_pushstring(L, info.c_str());

    executeLuaFunction(L, funcID, 2);

    lua_settop(L, 0);
}

IMPL_LUABINDING_FUNC(updateLuaCallbacks)
IMPL_LUABINDING_FUNC_I(getVersion)
IMPL_LUABINDING_FUNC_S(getVersionString)

IMPL_LUABINDING_FUNC_V_I(setFCMPushOnForeground)
IMPL_LUABINDING_FUNC_V_V(setFCMTokenRefresh)
IMPL_LUABINDING_FUNC_V_V(getFCMToken)
IMPL_LUABINDING_FUNC_V_S(sendFCMPushMessage)
IMPL_LUABINDING_FUNC_V_SS(sendFCMPushMessageToGroup)

IMPL_LUABINDING_FUNC_V_V(autoLogin)
IMPL_LUABINDING_FUNC_V_V(loginAnonymously)
IMPL_LUABINDING_FUNC_V_V(loginWithGoogle)
IMPL_LUABINDING_FUNC_V_V(loginWithFacebook)
IMPL_LUABINDING_FUNC_V_SS(loginWithEmail)
IMPL_LUABINDING_FUNC_V_V(linkWithGoogle)
IMPL_LUABINDING_FUNC_V_V(linkWithFacebook)
IMPL_LUABINDING_FUNC_V_SS(linkWithEmail)
IMPL_LUABINDING_FUNC_V_V(unlinkWithGoogle)
IMPL_LUABINDING_FUNC_V_V(unlinkWithFacebook)
IMPL_LUABINDING_FUNC_V_SS(unlinkWithEmail)
IMPL_LUABINDING_FUNC_V_SS(createUserWithEmail)
IMPL_LUABINDING_FUNC_V_V(logout)
IMPL_LUABINDING_FUNC_V_V(deleteUser)

IMPL_LUABINDING_FUNC_V_V(facebookLogin)
IMPL_LUABINDING_FUNC_V_V(facebookLogout)
IMPL_LUABINDING_FUNC_V_V(facebookGetFriends)
IMPL_LUABINDING_FUNC_V_V(facebookGetInvitableFriends)
IMPL_LUABINDING_FUNC_V_S(facebookSendRequest)
IMPL_LUABINDING_FUNC_Z_S(facebookIsGrantedPermission)
IMPL_LUABINDING_FUNC_V_S(facebookAskPermission)

IMPL_LUABINDING_FUNC_V_SSS(adbrixEvent)
IMPL_LUABINDING_FUNC_V_V(adbrixStartSession)
IMPL_LUABINDING_FUNC_V_V(adbrixEndSession)

IMPL_LUABINDING_FUNC_V_SSS(tapjoyEvent)
IMPL_LUABINDING_FUNC_V_I(tapjoySetTrackPurchase)
IMPL_LUABINDING_FUNC_V_S(tapjoySetPlacement)
IMPL_LUABINDING_FUNC_V_S(tapjoyShowPlacement)
IMPL_LUABINDING_FUNC_V_V(tapjoyGetCurrency)
IMPL_LUABINDING_FUNC_V_V(tapjoySetEarnedCurrencyCallback)
IMPL_LUABINDING_FUNC_V_I(tapjoySpendCurrency)
IMPL_LUABINDING_FUNC_V_I(tapjoyAwardCurrency)

IMPL_LUABINDING_FUNC_V_V(naverLogin)
IMPL_LUABINDING_FUNC_V_I(naverLogout)
IMPL_LUABINDING_FUNC_V_S(naverRequestApi)
IMPL_LUABINDING_FUNC_Z_V(naverCafeIsShowGlink)
IMPL_LUABINDING_FUNC_V_I(naverCafeShowWidgetWhenUnloadSdk)
IMPL_LUABINDING_FUNC_V_V(naverCafeStopWidget)
IMPL_LUABINDING_FUNC_V_I(naverCafeStart)
IMPL_LUABINDING_FUNC_V_V(naverCafeStop)
IMPL_LUABINDING_FUNC_V_V(naverCafePopBackStack)
IMPL_LUABINDING_FUNC_V_ISS(naverCafeStartWrite)
IMPL_LUABINDING_FUNC_V_ISSS(naverCafeStartImageWrite)
IMPL_LUABINDING_FUNC_V_ISSS(naverCafeStartVideoWrite)
IMPL_LUABINDING_FUNC_V_S(naverCafeSyncGameUserId)
IMPL_LUABINDING_FUNC_V_I(naverCafeSetUseVideoRecord)
IMPL_LUABINDING_FUNC_V_V(naverCafeSetCallback)

IMPL_LUABINDING_FUNC_V_V(googleLogin)
IMPL_LUABINDING_FUNC_V_V(googleLogout)
IMPL_LUABINDING_FUNC_V_V(googleShowAchievements)
IMPL_LUABINDING_FUNC_V_V(googleShowLeaderboards)
IMPL_LUABINDING_FUNC_V_V(googleShowQuests)
IMPL_LUABINDING_FUNC_V_SS(googleUpdateAchievements)
IMPL_LUABINDING_FUNC_V_SS(googleUpdateLeaderboards)
IMPL_LUABINDING_FUNC_V_SS(googleUpdateQuests)

IMPL_LUABINDING_FUNC_V_S(billingSetup)
IMPL_LUABINDING_FUNC_V_S(billingConfirm)
IMPL_LUABINDING_FUNC_V_SS(billingPurchase)
IMPL_LUABINDING_FUNC_V_SS(billingSubscription)

int registerAllPerpleSdk(lua_State* L)
{
    if (nullptr == L)
    {
        return 0;
    }

    tolua_open(L);
    toluafix_open(L);

    tolua_module(L, NULL, 0);
    tolua_beginmodule(L, NULL);
        tolua_usertype(L, "PerpleSDK");
        tolua_cclass(L, "PerpleSDK", "PerpleSDK", "", NULL);
        tolua_beginmodule(L,"PerpleSDK");

            DECL_LUABINDING_FUNC(updateLuaCallbacks)
            DECL_LUABINDING_FUNC(getVersion)
            DECL_LUABINDING_FUNC(getVersionString)

            DECL_LUABINDING_FUNC(setFCMPushOnForeground)
            DECL_LUABINDING_FUNC(setFCMTokenRefresh)
            DECL_LUABINDING_FUNC(getFCMToken)
            DECL_LUABINDING_FUNC(sendFCMPushMessage)
            DECL_LUABINDING_FUNC(sendFCMPushMessageToGroup)

            DECL_LUABINDING_FUNC(autoLogin)
            DECL_LUABINDING_FUNC(loginAnonymously)
            DECL_LUABINDING_FUNC(loginWithGoogle)
            DECL_LUABINDING_FUNC(loginWithFacebook)
            DECL_LUABINDING_FUNC(loginWithEmail)
            DECL_LUABINDING_FUNC(linkWithGoogle)
            DECL_LUABINDING_FUNC(linkWithFacebook)
            DECL_LUABINDING_FUNC(linkWithEmail)
            DECL_LUABINDING_FUNC(unlinkWithGoogle)
            DECL_LUABINDING_FUNC(unlinkWithFacebook)
            DECL_LUABINDING_FUNC(unlinkWithEmail)
            DECL_LUABINDING_FUNC(createUserWithEmail)
            DECL_LUABINDING_FUNC(logout)
            DECL_LUABINDING_FUNC(deleteUser)

            DECL_LUABINDING_FUNC(facebookLogin)
            DECL_LUABINDING_FUNC(facebookLogout)
            DECL_LUABINDING_FUNC(facebookGetFriends)
            DECL_LUABINDING_FUNC(facebookGetInvitableFriends)
            DECL_LUABINDING_FUNC(facebookSendRequest)
            DECL_LUABINDING_FUNC(facebookIsGrantedPermission)
            DECL_LUABINDING_FUNC(facebookAskPermission)

            DECL_LUABINDING_FUNC(adbrixEvent)
            DECL_LUABINDING_FUNC(adbrixStartSession)
            DECL_LUABINDING_FUNC(adbrixEndSession)

            DECL_LUABINDING_FUNC(tapjoyEvent)
            DECL_LUABINDING_FUNC(tapjoySetTrackPurchase)
            DECL_LUABINDING_FUNC(tapjoySetPlacement)
            DECL_LUABINDING_FUNC(tapjoyShowPlacement)
            DECL_LUABINDING_FUNC(tapjoyGetCurrency)
            DECL_LUABINDING_FUNC(tapjoySetEarnedCurrencyCallback)
            DECL_LUABINDING_FUNC(tapjoySpendCurrency)
            DECL_LUABINDING_FUNC(tapjoyAwardCurrency)

            DECL_LUABINDING_FUNC(naverLogin)
            DECL_LUABINDING_FUNC(naverLogout)
            DECL_LUABINDING_FUNC(naverRequestApi)
            DECL_LUABINDING_FUNC(naverCafeIsShowGlink)
            DECL_LUABINDING_FUNC(naverCafeShowWidgetWhenUnloadSdk)
            DECL_LUABINDING_FUNC(naverCafeStopWidget)
            DECL_LUABINDING_FUNC(naverCafeStart)
            DECL_LUABINDING_FUNC(naverCafeStop)
            DECL_LUABINDING_FUNC(naverCafePopBackStack)
            DECL_LUABINDING_FUNC(naverCafeStartWrite)
            DECL_LUABINDING_FUNC(naverCafeStartImageWrite)
            DECL_LUABINDING_FUNC(naverCafeStartVideoWrite)
            DECL_LUABINDING_FUNC(naverCafeSyncGameUserId)
            DECL_LUABINDING_FUNC(naverCafeSetUseVideoRecord)
            DECL_LUABINDING_FUNC(naverCafeSetCallback)

            DECL_LUABINDING_FUNC(googleLogin)
            DECL_LUABINDING_FUNC(googleLogout)
            DECL_LUABINDING_FUNC(googleShowAchievements)
            DECL_LUABINDING_FUNC(googleShowLeaderboards)
            DECL_LUABINDING_FUNC(googleShowQuests)
            DECL_LUABINDING_FUNC(googleUpdateAchievements)
            DECL_LUABINDING_FUNC(googleUpdateLeaderboards)
            DECL_LUABINDING_FUNC(googleUpdateQuests)

            DECL_LUABINDING_FUNC(billingSetup)
            DECL_LUABINDING_FUNC(billingConfirm)
            DECL_LUABINDING_FUNC(billingPurchase)
            DECL_LUABINDING_FUNC(billingSubscription)

        tolua_endmodule(L);
    tolua_endmodule(L);

    return 0;
}
