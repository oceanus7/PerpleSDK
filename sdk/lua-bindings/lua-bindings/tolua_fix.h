#ifndef __TOLUA_FIX_H__
#define __TOLUA_FIX_H__

#include "tolua++.h"

#define TOLUA_REFID_PTR_MAPPING "perplesdk_toluafix_refid_ptr_mapping"
#define TOLUA_REFID_TYPE_MAPPING "perplesdk_toluafix_refid_type_mapping"
#define TOLUA_REFID_FUNCTION_MAPPING "perplesdk_toluafix_refid_function_mapping"

TOLUA_API void toluafix_open(lua_State* L);
TOLUA_API int  toluafix_ref_function(lua_State* L, int lo, int def);
TOLUA_API void toluafix_get_function_by_refid(lua_State* L, int refid);
TOLUA_API void toluafix_remove_function_by_refid(lua_State* L, int refid);
TOLUA_API int  toluafix_isfunction(lua_State* L, int lo, const char* type, int def, tolua_Error* err);
TOLUA_API int  toluafix_totable(lua_State* L, int lo, int def);
TOLUA_API int  toluafix_istable(lua_State* L, int lo, const char* type, int def, tolua_Error* err);
TOLUA_API void toluafix_stack_dump(lua_State* L, const char* label);

#endif // #ifndef __TOLUA_FIX_H__
