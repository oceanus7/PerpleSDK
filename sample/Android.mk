LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := cocos2dlua_shared

LOCAL_MODULE_FILENAME := libcocos2dlua

LOCAL_SRC_FILES := lua/main.cpp \
                   lua/Runtime_android.cpp \
                   ../../Classes/VisibleRect.cpp \
                   ../../Classes/AppDelegate.cpp \
                   ../../Classes/ConfigParser.cpp \
                   ../../Classes/Runtime.cpp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../Classes \
                    $(LOCAL_PATH)/../../../cocos2d-x/cocos/network \
					$(LOCAL_PATH)/../../../libraries/perplesdk/include

LOCAL_STATIC_LIBRARIES := curl_static_prebuilt

#perplesdk
LOCAL_STATIC_LIBRARIES += perplesdklua_static

#perplesdk
LOCAL_SHARED_LIBRARIES := perplesdk_shared

LOCAL_WHOLE_STATIC_LIBRARIES := cocos_lua_static

include $(BUILD_SHARED_LIBRARY)

#perplesdk
include $(LOCAL_PATH)/../../../libraries/perplesdk/prebuilt/android/Android.mk

$(call import-module,scripting/lua-bindings)
