#ifndef __LUA_PERPLESDK_H__
#define __LUA_PERPLESDK_H__

#include <string>

struct lua_State;

int registerAllPerpleSdk(lua_State* L);
void onSdkResult(lua_State* L, const int funcID, const std::string result, const std::string info);

#endif // #ifndef __LUA_PERPLESDK_H__
