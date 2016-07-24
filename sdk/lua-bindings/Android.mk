LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := perplesdklua_static

LOCAL_MODULE_FILENAME := libperplesdklua

LOCAL_SRC_FILES := lua/tolua/tolua_event.c \
                   lua/tolua/tolua_is.c \
                   lua/tolua/tolua_map.c \
                   lua/tolua/tolua_push.c \
                   lua/tolua/tolua_to.c \
                   lua-bindings/lua_perplesdk.cpp \
                   lua-bindings/tolua_fix.cpp \

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/lua-bindings \

LOCAL_C_INCLUDES := $(LOCAL_PATH)/lua-bindings \
                    $(LOCAL_PATH)/lua/tolua \
                    $(LOCAL_PATH)/lua/lua \
                    $(LOCAL_PATH)/lua/luajit/include \
                    $(LOCAL_PATH)/../src

LOCAL_STATIC_LIBRARIES := luajit_static

include $(BUILD_STATIC_LIBRARY)

include $(LOCAL_PATH)/lua/luajit/prebuilt/android/Android.mk
