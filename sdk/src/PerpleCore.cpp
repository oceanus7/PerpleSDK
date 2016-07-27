#include "PerpleCore.h"
#include "lua_perplesdk.h"
#include <sstream>

////////////////////////////////////////////////////////////////////////////////////////////////////

int updateLuaCallbacks(lua_State* L)
{
    PerpleCore::UpdateLuaCallbacks();
    return 0;
}

int getVersion(lua_State* L)
{
   return PerpleCore::GetVersion();
}

std::string getVersionString(lua_State* L)
{
    return PerpleCore::GetVersionString();
}

////////////////////////////////////////////////////////////////////////////////////////////////////

lua_State* PerpleCore::mLuaState;

std::vector<std::function<void()>> PerpleCore::mFunctionsToPerform;
std::mutex PerpleCore::mPerformMutex;

int PerpleCore::InitSDK()
{
    return 0;
}

int PerpleCore::GetVersion()
{
    return PERPLESDK_VERSION;
}

std::string PerpleCore::GetVersionString()
{
    int major = PERPLESDK_VERSION_MAJOR(PERPLESDK_VERSION);
    int minor = PERPLESDK_VERSION_MINOR(PERPLESDK_VERSION);
    int patch = PERPLESDK_VERSION_PATCH(PERPLESDK_VERSION);

    std::ostringstream oss;
    oss << major << "." << minor << "." << patch;
    return oss.str();
}

void PerpleCore::LuaOpenPerpleSDK(lua_State* L)
{
    mFunctionsToPerform.reserve(30);

    mLuaState = L;
    registerAllPerpleSdk(mLuaState);
}

lua_State* PerpleCore::GetLuaState()
{
    return mLuaState;
}

void PerpleCore::OnSDKResult(const int funcID, const char* result, const char* info)
{
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

void PerpleCore::PerformFunctionInLuaThread(const std::function<void()>& function)
{
    mPerformMutex.lock();
    mFunctionsToPerform.push_back(function);
    mPerformMutex.unlock();
}

void PerpleCore::UpdateLuaCallbacks()
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
