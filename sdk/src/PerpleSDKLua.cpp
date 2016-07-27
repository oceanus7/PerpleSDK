#include "PerpleSDKLua.h"

#ifdef __cplusplus
extern "C" {
#endif
#include "lua.h"
#ifdef __cplusplus
}
#endif

#include "PerpleCore.h"

void luaopen_perplesdk(lua_State* L)
{
    PerpleCore::LuaOpenPerpleSDK(L);
}
