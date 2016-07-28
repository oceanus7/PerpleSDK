#include "lua_perplesdk.h"
#include "tolua_fix.h"
#include "PerpleSDK.h"

#define LOG_TAG "PerpleSDKLua"

#if defined(__ANDROID__)
#include <android/log.h>
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#else
#define LOG(...)
#endif

extern void jniFuncV_V(const char* funcName, int funcID);
extern void jniFuncV_S(const char* funcName, int funcID, const char* arg0);
extern void jniFuncV_SS(const char* funcName, int funcID, const char* arg0, const char* arg1);
extern void jniFuncV_SSS(const char* funcName, int funcID, const char* arg0, const char* arg1, const char* arg2);
extern void jniFuncV_I(const char* funcName, int funcID, int arg0);
extern void jniFuncV_ISS(const char* funcName, int funcID, int arg0, const char* arg1, const char* arg2);
extern void jniFuncV_ISSS(const char* funcName, int funcID, int arg0, const char* arg1, const char* arg2, const char* arg3);
extern bool jniFuncZ_V(const char* funcName, int funcID);
extern bool jniFuncZ_S(const char* funcName, int funcID, const char* arg0);

extern void updateLuaCallbacks(lua_State* L);

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
            LOG("[LUA ERROR] %s", lua_tostring(L, -1));
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
    LOG("onSdkResult : %d, %s, %s", funcID, result.c_str(), info.c_str());

    lua_pushstring(L, result.c_str());
    lua_pushstring(L, info.c_str());

    executeLuaFunction(L, funcID, 2);

    lua_settop(L, 0);
}

int tolua_PerpleSDK_updateLuaCallbacks(lua_State* tolua_S)
{
    updateLuaCallbacks(tolua_S);
    return 0;
}

int tolua_PerpleSDK_getVersion(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 2, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int argc = lua_gettop(tolua_S) - 1;
        if (argc == 0)
        {
            int version = PERPLESDK_VERSION;
            tolua_pushnumber(tolua_S, (lua_Number)version);
            return 1;
        }
        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'getVersion'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_setFCMTokenRefresh(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("setFCMTokenRefresh", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'setFCMTokenRefresh'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_getFCMToken(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("getFCMToken", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'getFCMToken'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_sendFCMPushMessage(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 4, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* data = tolua_tostring(tolua_S, 2, 0);

        int funcID = toluafix_ref_function(tolua_S, 3, 0);

        jniFuncV_S("sendFCMPushMessage", funcID, data);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'sendFCMPushMessage'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_sendFCMPushMessageToGroup(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* groupKey = tolua_tostring(tolua_S, 2, 0);
        const char* data = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("sendFCMPushMessageToGroup", funcID, groupKey, data);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'sendFCMPushMessageToGroup'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_autoLogin(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("autoLogin", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'autoLogin'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_loginAnonymously(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("loginAnonymously", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'loginAnonymously'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_loginGoogle(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("loginGoogle", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'loginGoogle'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_loginFacebook(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("loginFacebook", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'loginFacebook'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_loginEmail(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* email = tolua_tostring(tolua_S, 2, 0);
        const char* password = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("loginEmail", funcID, email, password);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'loginEmail'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_linkWithGoogle(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("linkWithGoogle", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'linkWithGoogle'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_linkWithFacebook(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("linkWithFacebook", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'linkWithFacebook'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_linkWithEmail(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* email = tolua_tostring(tolua_S, 2, 0);
        const char* password = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("linkWithEmail", funcID, email, password);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'linkWithEmail'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_unlinkWithGoogle(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("unlinkWithGoogle", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'unlinkWithGoogle'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_unlinkWithFacebook(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("unlinkWithFacebook", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'unlinkWithFacebook'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_unlinkWithEmail(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* email = tolua_tostring(tolua_S, 2, 0);
        const char* password = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("unlinkWithEmail", funcID, email, password);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'unlinkWithEmail'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_logout(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("logout", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'logout'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_deleteUser(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("deleteUser", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'deleteUser'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_createUserWithEmail(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* email = tolua_tostring(tolua_S, 2, 0);
        const char* password = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("createUserWithEmail", funcID, email, password);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'createUserWithEmail'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_facebookGetFriends(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("facebookGetFriends", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'facebookGetFriends'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_facebookGetInvitableFriends(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("facebookGetInvitableFriends", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'facebookGetInvitableFriends'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_facebookSendRequest(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 4, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* info = tolua_tostring(tolua_S, 2, 0);
        int funcID = toluafix_ref_function(tolua_S, 3, 0);

        jniFuncV_S("facebookSendRequest", funcID, info);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'facebookSendRequest'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_facebookIsGrantedPermission(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int argc = lua_gettop(tolua_S) - 1;
        if (argc == 1)
        {
            const char* permission = tolua_tostring(tolua_S, 2, 0);
            bool ret = jniFuncZ_S("facebookIsGrantedPermission", -1, permission);
            int ret_ = ret ? 1 : 0;

            tolua_pushboolean(tolua_S, ret_);
            return 1;
        }
        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'facebookIsGrantedPermission'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_facebookAskPermission(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 4, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* permission = tolua_tostring(tolua_S, 2, 0);
        int funcID = toluafix_ref_function(tolua_S, 3, 0);

        jniFuncV_S("facebookAskPermission", funcID, permission);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'facebookAskPermission'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_adbrixEvent(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 4, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* id = tolua_tostring(tolua_S, 2, 0);
        const char* arg0 = tolua_tostring(tolua_S, 3, 0);
        const char* arg1 = tolua_tostring(tolua_S, 4, 0);

        jniFuncV_SSS("adbrixEvent", -1, id, arg0, arg1);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'adbrixEvent'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_adbrixStartSession(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 2, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        jniFuncV_V("adbrixStartSession", -1);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'adbrixStartSession'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_adbrixEndSession(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 2, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        jniFuncV_V("adbrixEndSession", -1);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'adbrixEndSession'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_tapjoyEvent(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 4, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* id = tolua_tostring(tolua_S, 2, 0);
        const char* arg0 = tolua_tostring(tolua_S, 3, 0);
        const char* arg1 = tolua_tostring(tolua_S, 4, 0);

        jniFuncV_SSS("tapjoyEvent", -1, id, arg0, arg1);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'tapjoyEvent'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_tapjoySetPlacement(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 4, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* placement = tolua_tostring(tolua_S, 2, 0);

        int funcID = toluafix_ref_function(tolua_S, 3, 0);

        jniFuncV_S("tapjoySetPlacement", funcID, placement);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'tapjoySetPlacement'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_tapjoyShowPlacement(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 4, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* placement = tolua_tostring(tolua_S, 2, 0);

        int funcID = toluafix_ref_function(tolua_S, 3, 0);

        jniFuncV_S("tapjoyShowPlacement", funcID, placement);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'tapjoyShowPlacement'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_tapjoyGetCurrency(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("tapjoyGetCurrency", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'tapjoyGetCurrency'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_tapjoySetEarnedCurrencyCallback(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("tapjoySetEarnedCurrencyCallback", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'tapjoySetEarnedCurrencyCallback'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_tapjoySpendCurrency(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 4, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int amount = (int)tolua_tonumber(tolua_S, 2, 0);
        int funcID = toluafix_ref_function(tolua_S, 3, 0);

        jniFuncV_I("tapjoySpendCurrency", funcID, amount);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'tapjoySpendCurrency'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_tapjoyAwardCurrency(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 4, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int amount = (int)tolua_tonumber(tolua_S, 2, 0);
        int funcID = toluafix_ref_function(tolua_S, 3, 0);

        jniFuncV_I("tapjoyAwardCurrency", funcID, amount);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'tapjoyAwardCurrency'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverLogin(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("naverLogin", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverLogin'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverLogout(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int deleteToken = (int)tolua_tonumber(tolua_S, 2, 0);

        jniFuncV_I("naverLogout", -1, deleteToken);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverLogout'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverRequestApi(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* url = tolua_tostring(tolua_S, 2, 0);

        jniFuncV_S("naverRequestApi", -1, url);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverRequestApi'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeIsShowGlink(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 2, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int argc = lua_gettop(tolua_S) - 1;
        if (argc == 0)
        {
            bool ret = jniFuncZ_V("naverCafeIsShowGlink", -1);
            int ret_ = ret ? 1 : 0;

            tolua_pushboolean(tolua_S, ret_);
            return 1;
        }
        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeIsShowGlink'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeStart(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int type = (int)tolua_tonumber(tolua_S, 2, 0);

        jniFuncV_I("naverCafeStart", -1, type);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeStart'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeStop(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 2, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        jniFuncV_V("naverCafeStop", -1);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeStop'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafePopBackStack(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 2, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        jniFuncV_V("naverCafePopBackStack", -1);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafePopBackStack'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeStartWrite(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 4, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int menuId = (int)tolua_tonumber(tolua_S, 2, 0);
        const char* subject = tolua_tostring(tolua_S, 3, 0);
        const char* text = tolua_tostring(tolua_S, 4, 0);

        jniFuncV_ISS("naverCafeStartWrite", -1, menuId, subject, text);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeStartWrite'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeStartImageWrite(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 4, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 5, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 6, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int menuId = (int)tolua_tonumber(tolua_S, 2, 0);
        const char* subject = tolua_tostring(tolua_S, 3, 0);
        const char* text = tolua_tostring(tolua_S, 4, 0);
        const char* imageUri = tolua_tostring(tolua_S, 5, 0);

        jniFuncV_ISSS("naverCafeStartImageWrite", -1, menuId, subject, text, imageUri);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeStartImageWrite'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeStartVideoWrite(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 4, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 5, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 6, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int menuId = (int)tolua_tonumber(tolua_S, 2, 0);
        const char* subject = tolua_tostring(tolua_S, 3, 0);
        const char* text = tolua_tostring(tolua_S, 4, 0);
        const char* videoUri = tolua_tostring(tolua_S, 5, 0);

        jniFuncV_ISSS("naverCafeStartVideoWrite", -1, menuId, subject, text, videoUri);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeStartVideoWrite'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeSyncGameUserId(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* gameUserId = tolua_tostring(tolua_S, 2, 0);

        jniFuncV_S("naverCafeSyncGameUserId", -1, gameUserId);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeSyncGameUserId'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeSetUseVideoRecord(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int flag = (int)tolua_tonumber(tolua_S, 2, 0);

        jniFuncV_I("naverCafeSetUseVideoRecord", -1, flag);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeSetUseVideoRecord'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_naverCafeSetCallback(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

		jniFuncV_V("naverCafeSetCallback", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'naverCafeSetCallback'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_googleShowAchievements(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("googleShowAchievements", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'googleShowAchievements'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_googleShowLeaderboards(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("googleShowLeaderboards", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'googleShowLeaderboards'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_googleShowQuests(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 2, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        int funcID = toluafix_ref_function(tolua_S, 2, 0);

        jniFuncV_V("googleShowQuests", funcID);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'googleShowQuests'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_googleUpdateAchievements(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* id = tolua_tostring(tolua_S, 2, 0);
        const char* steps = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("googleUpdateAchievements", funcID, id, steps);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'googleUpdateAchievements'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_googleUpdateLeaderboards(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* id = tolua_tostring(tolua_S, 2, 0);
        const char* score = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("googleUpdateLeaderboards", funcID, id, score);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'googleUpdateLeaderboards'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_googleUpdateQuests(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* id = tolua_tostring(tolua_S, 2, 0);
        const char* count = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("googleUpdateQuests", funcID, id, count);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'googleUpdateQuests'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_setBilling(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 4, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* url = tolua_tostring(tolua_S, 2, 0);

        int funcID = toluafix_ref_function(tolua_S, 3, 0);

        jniFuncV_S("setBilling", funcID, url);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'setBilling'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_confirmPurchase(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 3, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* orderIds = tolua_tostring(tolua_S, 2, 0);

        jniFuncV_S("confirmPurchase", -1, orderIds);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'confirmPurchase'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_purchase(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* sku = tolua_tostring(tolua_S, 2, 0);
        const char* payload = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("purchase", funcID, sku, payload);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'purchase'.", &tolua_err);
    return 0;
#endif
}

int tolua_PerpleSDK_subscription(lua_State* tolua_S)
{
#ifndef TOLUA_RELEASE
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4, "", 0, &tolua_err) ||
        !tolua_isnoobj(tolua_S, 5, &tolua_err))
    {
        goto tolua_lerror;
    }
    else
#endif
    {
        const char* sku = tolua_tostring(tolua_S, 2, 0);
        const char* payload = tolua_tostring(tolua_S, 3, 0);

        int funcID = toluafix_ref_function(tolua_S, 4, 0);

        jniFuncV_SS("subscription", funcID, sku, payload);

        return 0;
    }

#ifndef TOLUA_RELEASE
tolua_lerror :
    tolua_error(tolua_S, "PerpleSDKLua: Error in function 'subscription'.", &tolua_err);
    return 0;
#endif
}

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
            tolua_function(L, "updateLuaCallbacks", tolua_PerpleSDK_updateLuaCallbacks);
            tolua_function(L, "getVersion", tolua_PerpleSDK_getVersion);
            tolua_function(L, "setFCMTokenRefresh", tolua_PerpleSDK_setFCMTokenRefresh);
            tolua_function(L, "getFCMToken", tolua_PerpleSDK_getFCMToken);
            tolua_function(L, "sendFCMPushMessage", tolua_PerpleSDK_sendFCMPushMessage);
            tolua_function(L, "sendFCMPushMessageToGroup", tolua_PerpleSDK_sendFCMPushMessageToGroup);
            tolua_function(L, "autoLogin", tolua_PerpleSDK_autoLogin);
            tolua_function(L, "loginAnonymously", tolua_PerpleSDK_loginAnonymously);
            tolua_function(L, "loginGoogle", tolua_PerpleSDK_loginGoogle);
            tolua_function(L, "loginFacebook", tolua_PerpleSDK_loginFacebook);
            tolua_function(L, "loginEmail", tolua_PerpleSDK_loginEmail);
            tolua_function(L, "linkWithGoogle", tolua_PerpleSDK_linkWithGoogle);
            tolua_function(L, "linkWithFacebook", tolua_PerpleSDK_linkWithFacebook);
            tolua_function(L, "linkWithEmail", tolua_PerpleSDK_linkWithEmail);
            tolua_function(L, "unlinkWithGoogle", tolua_PerpleSDK_unlinkWithGoogle);
            tolua_function(L, "unlinkWithFacebook", tolua_PerpleSDK_unlinkWithFacebook);
            tolua_function(L, "unlinkWithEmail", tolua_PerpleSDK_unlinkWithEmail);
            tolua_function(L, "logout", tolua_PerpleSDK_logout);
            tolua_function(L, "deleteUser", tolua_PerpleSDK_deleteUser);
            tolua_function(L, "createUserWithEmail", tolua_PerpleSDK_createUserWithEmail);
            tolua_function(L, "facebookGetFriends", tolua_PerpleSDK_facebookGetFriends);
            tolua_function(L, "facebookGetInvitableFriends", tolua_PerpleSDK_facebookGetInvitableFriends);
            tolua_function(L, "facebookSendRequest", tolua_PerpleSDK_facebookSendRequest);
            tolua_function(L, "facebookIsGrantedPermission", tolua_PerpleSDK_facebookIsGrantedPermission);
            tolua_function(L, "facebookAskPermission", tolua_PerpleSDK_facebookAskPermission);
            tolua_function(L, "adbrixEvent", tolua_PerpleSDK_adbrixEvent);
            tolua_function(L, "adbrixStartSession", tolua_PerpleSDK_adbrixStartSession);
            tolua_function(L, "adbrixEndSession", tolua_PerpleSDK_adbrixEndSession);
            tolua_function(L, "tapjoyEvent", tolua_PerpleSDK_tapjoyEvent);
            tolua_function(L, "tapjoySetPlacement", tolua_PerpleSDK_tapjoySetPlacement);
            tolua_function(L, "tapjoyShowPlacement", tolua_PerpleSDK_tapjoyShowPlacement);
            tolua_function(L, "tapjoyGetCurrency", tolua_PerpleSDK_tapjoyGetCurrency);
            tolua_function(L, "tapjoySetEarnedCurrencyCallback", tolua_PerpleSDK_tapjoySetEarnedCurrencyCallback);
            tolua_function(L, "tapjoySpendCurrency", tolua_PerpleSDK_tapjoySpendCurrency);
            tolua_function(L, "tapjoyAwardCurrency", tolua_PerpleSDK_tapjoyAwardCurrency);
            tolua_function(L, "naverLogin", tolua_PerpleSDK_naverLogin);
            tolua_function(L, "naverLogout", tolua_PerpleSDK_naverLogout);
            tolua_function(L, "naverRequestApi", tolua_PerpleSDK_naverRequestApi);
            tolua_function(L, "naverCafeIsShowGlink", tolua_PerpleSDK_naverCafeIsShowGlink);
            tolua_function(L, "naverCafeStart", tolua_PerpleSDK_naverCafeStart);
            tolua_function(L, "naverCafeStop", tolua_PerpleSDK_naverCafeStop);
            tolua_function(L, "naverCafePopBackStack", tolua_PerpleSDK_naverCafePopBackStack);
            tolua_function(L, "naverCafeStartWrite", tolua_PerpleSDK_naverCafeStartWrite);
            tolua_function(L, "naverCafeStartImageWrite", tolua_PerpleSDK_naverCafeStartImageWrite);
            tolua_function(L, "naverCafeStartVideoWrite", tolua_PerpleSDK_naverCafeStartVideoWrite);
            tolua_function(L, "naverCafeSyncGameUserId", tolua_PerpleSDK_naverCafeSyncGameUserId);
            tolua_function(L, "naverCafeSetUseVideoRecord", tolua_PerpleSDK_naverCafeSetUseVideoRecord);
            tolua_function(L, "naverCafeSetCallback", tolua_PerpleSDK_naverCafeSetCallback);
            tolua_function(L, "googleShowAchievements", tolua_PerpleSDK_googleShowAchievements);
            tolua_function(L, "googleShowLeaderboards", tolua_PerpleSDK_googleShowLeaderboards);
            tolua_function(L, "googleShowQuests", tolua_PerpleSDK_googleShowQuests);
            tolua_function(L, "googleUpdateAchievements", tolua_PerpleSDK_googleUpdateAchievements);
            tolua_function(L, "googleUpdateLeaderboards", tolua_PerpleSDK_googleUpdateLeaderboards);
            tolua_function(L, "googleUpdateQuests", tolua_PerpleSDK_googleUpdateQuests);
            tolua_function(L, "setBilling", tolua_PerpleSDK_setBilling);
            tolua_function(L, "confirmPurchase", tolua_PerpleSDK_confirmPurchase);
            tolua_function(L, "purchase", tolua_PerpleSDK_purchase);
            tolua_function(L, "subscription", tolua_PerpleSDK_subscription);
        tolua_endmodule(L);
    tolua_endmodule(L);

    return 0;
}
