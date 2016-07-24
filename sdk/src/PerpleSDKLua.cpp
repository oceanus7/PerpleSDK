#include "PerpleSDKLua.h"

#ifdef __cplusplus
extern "C" {
#endif
#include "lua.h"
#ifdef __cplusplus
}
#endif

#include "PerpleSDK.h"

void luaopen_perplesdk(lua_State* L)
{
    PerpleSDK::LuaOpenPerpleSDK(L);
}
