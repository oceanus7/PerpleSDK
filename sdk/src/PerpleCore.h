#pragma once

#include <vector>
#include <mutex>

#define PERPLESDK_VERSION_MAJOR(version)                ((uint32_t)(version) >> 24)
#define PERPLESDK_VERSION_MINOR(version)                (((uint32_t)(version) >> 16) & 0xff)
#define PERPLESDK_VERSION_PATCH(version)                ((uint32_t)(version) & 0xffff)
#define PERPLESDK_MAKE_VERSION(major, minor, patch)     (((major) << 24) | ((minor) << 16) | (patch))
#define PERPLESDK_VERSION                               PERPLESDK_MAKE_VERSION(0, 9, 18)

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

private:
    static lua_State* mLuaState;

    static std::vector<std::function<void()>> mFunctionsToPerform;
    static std::mutex mPerformMutex;
};
