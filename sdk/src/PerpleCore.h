#pragma once

#include <functional>
#include <vector>
#include <mutex>

#define PERPLESDK_VERSION_MAJOR(version)                ((uint32_t)(version) >> 24)
#define PERPLESDK_VERSION_MINOR(version)                (((uint32_t)(version) >> 16) & 0xff)
#define PERPLESDK_VERSION_PATCH(version)                ((uint32_t)(version) & 0xffff)
#define PERPLESDK_MAKE_VERSION(major, minor, patch)     (((major) << 24) | ((minor) << 16) | (patch))
#define PERPLESDK_VERSION                               PERPLESDK_MAKE_VERSION(0, 9, 18)

#define ERROR_UNKNOWN                           "-999"
#define ERROR_IOEXCEPTION                       "-998"
#define ERROR_JSONEXCEPTION                     "-997"
#define ERROR_USERRECOVERABLEAUTHEXCEPTION      "-996"
#define ERROR_GOOGLEAUTHEXCEPTION               "-995"

#define ERROR_FIREBASE_NOTINITIALIZED           "-1000"
#define ERROR_FIREBASE_SENDPUSHMESSAGE          "-1001"
#define ERROR_FIREBASE_LOGIN                    "-1002"
#define ERROR_FIREBASE_GETPUSHTOKEN             "-1003"

#define ERROR_GOOGLE_NOTINITIALIZED             "-1200"
#define ERROR_GOOGLE_LOGIN                      "-1201"
#define ERROR_GOOGLE_NOTSIGNEDIN                "-1202"
#define ERROR_GOOGLE_ACHIEVEMENTS               "-1203"
#define ERROR_GOOGLE_LEADERBOARDS               "-1204"
#define ERROR_GOOGLE_QUESTS                     "-1205"
#define ERROR_GOOGLE_NOTSETLOGINCALLBACK        "-1206"
#define ERROR_GOOGLE_NOTSETPLAYSERVICESCALLBACK "-1207"
#define ERROR_GOOGLE_NOTSETQUESTSCALLBACK       "-1208"
#define ERROR_GOOGLE_NOTAVAILABLEPLAYSERVICES   "-1209"
#define ERROR_GOOGLE_LOGOUT                     "-1210"
#define ERROR_GOOGLE_PERMISSIONDENIED           "-1211"

#define ERROR_FACEBOOK_NOTINITIALIZED           "-1300"
#define ERROR_FACEBOOK_FACEBOOKEXCEPTION        "-1301"
#define ERROR_FACEBOOK_GRAPHAPI                 "-1302"
#define ERROR_FACEBOOK_REQUEST                  "-1303"

#define ERROR_NAVER_NOTINITIALIZED              "-1400"
#define ERROR_NAVER_CAFENOTINITIALIZED          "-1401"
#define ERROR_NAVER_LOGIN                       "-1402"

#define ERROR_BILLING_NOTINITIALIZED            "-1500"
#define ERROR_BILLING_INITFAILED                "-1501"
#define ERROR_BILLING_SETUP                     "-1502"
#define ERROR_BILLING_CHECKRECEIPT              "-1503"
#define ERROR_BILLING_QUARYINVECTORY            "-1504"
#define ERROR_BILLING_PURCHASEFINISH            "-1505"

#define ERROR_TAPJOY_NOTINITIALIZED             "-1600"
#define ERROR_TAPJOY_NOTSETPLACEMENT            "-1601"
#define ERROR_TAPJOY_GETCURRENCY                "-1602"
#define ERROR_TAPJOY_SPENDCURRENCY              "-1603"
#define ERROR_TAPJOY_AWARDCURRENCY              "-1604"

struct lua_State;

class PerpleCore
{
public:
    static int InitSDK();
    static int GetVersion();
    static std::string GetVersionString();
    static void LuaOpenPerpleSDK(lua_State* L);
    static lua_State* GetLuaState();
    static void OnSDKResult(int funcID, const char* result, const char* info);
    static void PerformFunctionInLuaThread(const std::function<void()>& function);
    static void UpdateLuaCallbacks();

#ifndef __ANDROID__
    static std::string GetErrorInfo(std::string code, std::string subcode, std::string message);
#endif

private:
    static lua_State* mLuaState;

    static std::vector<std::function<void()>> mFunctionsToPerform;
    static std::mutex mPerformMutex;
};
