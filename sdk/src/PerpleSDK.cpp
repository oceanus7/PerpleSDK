#include "PerpleSDK.h"
#include "lua_perplesdk.h"
#include <sstream>

std::map<std::string, int> PerpleSDK::mLuaFuncID;
lua_State* PerpleSDK::mLuaState;

std::vector<std::function<void()>> PerpleSDK::mFunctionsToPerform;
std::mutex PerpleSDK::mPerformMutex;

void updateLuaCallbacks(lua_State* L)
{
    PerpleSDK::UpdateLuaCallbacks(L);
}

int PerpleSDK::InitSDK()
{
    return 0;
}

int PerpleSDK::GetVersion()
{
    return PERPLESDK_VERSION;
}

std::string PerpleSDK::GetVersionString()
{
    int major = PERPLESDK_VERSION_MAJOR(PERPLESDK_VERSION);
    int minor = PERPLESDK_VERSION_MINOR(PERPLESDK_VERSION);
    int patch = PERPLESDK_VERSION_PATCH(PERPLESDK_VERSION);

    std::ostringstream oss;
    oss << major << "." << minor << "." << patch;
    return oss.str();
}

void PerpleSDK::LuaOpenPerpleSDK(lua_State* L)
{
    mFunctionsToPerform.reserve(30);

    mLuaState = L;
    registerAllPerpleSdk(mLuaState);
}

lua_State* PerpleSDK::GetLuaState()
{
    return mLuaState;
}

void PerpleSDK::RegisterLuaCallbacks(const char* funcName, int funcID)
{
    mLuaFuncID[funcName] = funcID;
}

void PerpleSDK::OnSDKResult(const char* funcName, const char* result, const char* info)
{
    const int funcID = mLuaFuncID[funcName];
    const std::string result_ = result;
    const std::string info_ = info;

    if (funcID > 0)
    {
        PerformFunctionInLuaThread([=]()
        {
            onSdkResult(mLuaState, funcID, result_, info_);
        });
    }
}

void PerpleSDK::PerformFunctionInLuaThread(const std::function<void()>& function)
{
    mPerformMutex.lock();
    mFunctionsToPerform.push_back(function);
    mPerformMutex.unlock();
}

void PerpleSDK::UpdateLuaCallbacks(lua_State* L)
{
    if (!mFunctionsToPerform.empty())
    {
        mPerformMutex.lock();
        auto temp = mFunctionsToPerform;
        mFunctionsToPerform.clear();
        mPerformMutex.unlock();

        for (const auto &function : temp)
        {
            function();
        }
    }
}
