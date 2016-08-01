//
//  lua_perplesdk_macro.h
//  PerpleSDK
//
//  Created by Yonghak on 2016. 7. 27..
//  Copyright © 2016년 PerpleLab. All rights reserved.
//

#ifndef lua_perplesdk_macro_h
#define lua_perplesdk_macro_h


#ifdef NDEBUG
#define IS_DEBUG false
#else
#define IS_DEBUG true
#endif


////////////////////////////////////////////////////////////////////////////////////////////////////

#define DECL_LUABINDING_FUNC(funcname) \
    tolua_function(L, #funcname, tolua_PerpleSDK_##funcname);

////////////////////////////////////////////////////////////////////////////////////////////////////

#define IMPL_LUABINDING_FUNC(funcname) \
    extern int funcname(lua_State* L); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isnoobj(tolua_S, 2, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        funcname(tolua_S); \
        return 0; \
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

#define IMPL_LUABINDING_FUNC_I(funcname) \
    extern int funcname(lua_State* L); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isnoobj(tolua_S, 2, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int argc = lua_gettop(tolua_S) - 1; \
        if (argc == 0) \
        { \
            tolua_pushnumber(tolua_S, (lua_Number)funcname(tolua_S)); \
            return 1; \
        } \
        return 0; \
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

#define IMPL_LUABINDING_FUNC_S(funcname) \
    extern std::string funcname(lua_State* L); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        tolua_Error tolua_err; \
        if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
            !tolua_isnoobj(tolua_S, 2, &tolua_err)) \
        { \
            tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
            return 0; \
        } \
        int argc = lua_gettop(tolua_S) - 1; \
        if (argc == 0) \
        { \
            tolua_pushstring(tolua_S, funcname(tolua_S).c_str()); \
            return 1; \
        } \
        return 0; \
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern void jniFuncV_V(const char* funcName, int funcID);
#define IMPL_LUABINDING_FUNC_V_V(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int funcID = toluafix_ref_function(tolua_S, 2, 0); \
        jniFuncV_V(#funcname, funcID); \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_V_V(funcname) \
    extern void funcname(int funcID); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int funcID = toluafix_ref_function(tolua_S, 2, 0); \
        funcname(funcID); \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern void jniFuncV_S(const char* funcName, int funcID, const char* arg0);
#define IMPL_LUABINDING_FUNC_V_S(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 2, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        const char* arg0 = tolua_tostring(tolua_S, 2, 0); \
        int funcID = toluafix_ref_function(tolua_S, 3, 0); \
        jniFuncV_S(#funcname, funcID, arg0); \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_V_S(funcname) \
    extern void funcname(int funcID, const char* arg0); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 2, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        const char* arg0 = tolua_tostring(tolua_S, 2, 0); \
        int funcID = toluafix_ref_function(tolua_S, 3, 0); \
        funcname(funcID, arg0); \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern void jniFuncV_SS(const char* funcName, int funcID, const char* arg0, const char* arg1);
#define IMPL_LUABINDING_FUNC_V_SS(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 2, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 3, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        const char* arg0 = tolua_tostring(tolua_S, 2, 0); \
        const char* arg1 = tolua_tostring(tolua_S, 3, 0); \
        int funcID = toluafix_ref_function(tolua_S, 4, 0); \
        jniFuncV_SS(#funcname, funcID, arg0, arg1); \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_V_SS(funcname) \
    extern void funcname(int funcID, const char* arg0, const char* arg1); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 2, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 3, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        const char* arg0 = tolua_tostring(tolua_S, 2, 0); \
        const char* arg1 = tolua_tostring(tolua_S, 3, 0); \
        int funcID = toluafix_ref_function(tolua_S, 4, 0); \
        funcname(funcID, arg0, arg1); \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern void jniFuncV_I(const char* funcName, int funcID, int arg0);
#define IMPL_LUABINDING_FUNC_V_I(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isnumber(tolua_S, 2, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int arg0 = (int)tolua_tonumber(tolua_S, 2, 0); \
        int funcID = toluafix_ref_function(tolua_S, 3, 0); \
        jniFuncV_I(#funcname, funcID, arg0); \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_V_I(funcname) \
    extern void funcname(int funcID, int arg0); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isnumber(tolua_S, 2, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int arg0 = (int)tolua_tonumber(tolua_S, 2, 0); \
        int funcID = toluafix_ref_function(tolua_S, 3, 0); \
        funcname(funcID, arg0); \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern void jniFuncV_SSS(const char* funcName, int funcID, const char* arg0, const char* arg1, const char* arg2);
#define IMPL_LUABINDING_FUNC_V_SSS(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 2, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 3, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 4, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        const char* arg0 = tolua_tostring(tolua_S, 2, 0); \
        const char* arg1 = tolua_tostring(tolua_S, 3, 0); \
        const char* arg2 = tolua_tostring(tolua_S, 4, 0); \
        int funcID = toluafix_ref_function(tolua_S, 5, 0); \
        jniFuncV_SSS(#funcname, funcID, arg0, arg1, arg2); \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_V_SSS(funcname) \
    extern void funcname(int funcID, const char* arg0, const char* arg1, const char* arg2); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 2, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 3, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 4, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        const char* arg0 = tolua_tostring(tolua_S, 2, 0); \
        const char* arg1 = tolua_tostring(tolua_S, 3, 0); \
        const char* arg2 = tolua_tostring(tolua_S, 4, 0); \
        int funcID = toluafix_ref_function(tolua_S, 5, 0); \
        funcname(funcID, arg0, arg1, arg2); \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern void jniFuncV_ISS(const char* funcName, int funcID, int arg0, const char* arg1, const char* arg2);
#define IMPL_LUABINDING_FUNC_V_ISS(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isnumber(tolua_S, 2, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 3, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 4, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int arg0 = (int)tolua_tonumber(tolua_S, 2, 0); \
        const char* arg1 = tolua_tostring(tolua_S, 3, 0); \
        const char* arg2 = tolua_tostring(tolua_S, 4, 0); \
        int funcID = toluafix_ref_function(tolua_S, 5, 0); \
        jniFuncV_ISS(#funcname, funcID, arg0, arg1, arg2); \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_V_ISS(funcname) \
    extern void funcname(int funcID, int arg0, const char* arg1, const char* arg2); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isnumber(tolua_S, 2, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 3, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 4, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int arg0 = (int)tolua_tonumber(tolua_S, 2, 0); \
        const char* arg1 = tolua_tostring(tolua_S, 3, 0); \
        const char* arg2 = tolua_tostring(tolua_S, 4, 0); \
        int funcID = toluafix_ref_function(tolua_S, 5, 0); \
        funcname(funcID, arg0, arg1, arg2); \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern void jniFuncV_ISSS(const char* funcName, int funcID, int arg0, const char* arg1, const char* arg2, const char* arg3);
#define IMPL_LUABINDING_FUNC_V_ISSS(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isnumber(tolua_S, 2, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 3, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 4, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 5, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int arg0 = (int)tolua_tonumber(tolua_S, 2, 0); \
        const char* arg1 = tolua_tostring(tolua_S, 3, 0); \
        const char* arg2 = tolua_tostring(tolua_S, 4, 0); \
        const char* arg3 = tolua_tostring(tolua_S, 5, 0); \
        int funcID = toluafix_ref_function(tolua_S, 6, 0); \
        jniFuncV_ISSS(#funcname, funcID, arg0, arg1, arg2, arg3); \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_V_ISSS(funcname) \
    extern void funcname(int funcID, int arg0, const char* arg1, const char* arg2, const char* arg3); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isnumber(tolua_S, 2, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 3, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 4, 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 5, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int arg0 = (int)tolua_tonumber(tolua_S, 2, 0); \
        const char* arg1 = tolua_tostring(tolua_S, 3, 0); \
        const char* arg2 = tolua_tostring(tolua_S, 4, 0); \
        const char* arg3 = tolua_tostring(tolua_S, 5, 0); \
        int funcID = toluafix_ref_function(tolua_S, 6, 0); \
        funcname(funcID, arg0, arg1, arg2, arg3); \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern bool jniFuncZ_V(const char* funcName, int funcID);
#define IMPL_LUABINDING_FUNC_Z_V(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int argc = lua_gettop(tolua_S) - 1; \
        if (argc <= 1) \
        { \
            int funcID = toluafix_ref_function(tolua_S, 2, 0); \
            bool ret = jniFuncZ_V(#funcname, funcID); \
            tolua_pushboolean(tolua_S, (ret ? 1 : 0)); \
            return 1; \
        } \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_Z_V(funcname) \
    extern bool funcname(int funcID); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int argc = lua_gettop(tolua_S) - 1; \
        if (argc <= 1) \
        { \
            int funcID = toluafix_ref_function(tolua_S, 2, 0); \
            bool ret = funcname(funcID); \
            tolua_pushboolean(tolua_S, (ret ? 1 : 0)); \
            return 1; \
        } \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#if defined(__ANDROID__)
extern bool jniFuncZ_S(const char* funcName, int funcID, const char* arg0);
#define IMPL_LUABINDING_FUNC_Z_S(funcname) \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 2, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int argc = lua_gettop(tolua_S) - 1; \
        if (argc <= 2) \
        { \
            const char* arg0 = tolua_tostring(tolua_S, 2, 0); \
            int funcID = toluafix_ref_function(tolua_S, 3, 0); \
            bool ret = jniFuncZ_S(#funcname, funcID, arg0); \
            tolua_pushboolean(tolua_S, (ret ? 1 : 0)); \
            return 1; \
        } \
        return 0; \
    }
#else
#define IMPL_LUABINDING_FUNC_Z_S(funcname) \
    extern bool funcname(int funcID, const char* arg0); \
    int tolua_PerpleSDK_##funcname(lua_State* tolua_S) \
    { \
        if (IS_DEBUG) \
        { \
            tolua_Error tolua_err; \
            if (!tolua_isusertable(tolua_S, 1, "PerpleSDK", 0, &tolua_err) || \
                !tolua_isstring(tolua_S, 2, 0, &tolua_err)) \
            { \
                tolua_error(tolua_S, "PerpleSDKLua: Error in function '" #funcname "'.", &tolua_err); \
                return 0; \
            } \
        } \
        int argc = lua_gettop(tolua_S) - 1; \
        if (argc <= 2) \
        { \
            const char* arg0 = tolua_tostring(tolua_S, 2, 0); \
            int funcID = toluafix_ref_function(tolua_S, 3, 0); \
            bool ret = funcname(funcID, arg0); \
            tolua_pushboolean(tolua_S, (ret ? 1 : 0)); \
            return 1; \
        } \
        return 0; \
    }
#endif

////////////////////////////////////////////////////////////////////////////////////////////////////

#endif /* lua_perplesdk_macro_h */
