LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := perplesdk_shared

LOCAL_MODULE_FILENAME := libperplesdk

LOCAL_SRC_FILES := main.cpp \
                   runtime_android.cpp \
                   jni/JniHelper.cpp \
                   ../../src/PerpleCore.cpp \
                   ../../src/PerpleSDKLua.cpp

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../src

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../src

LOCAL_STATIC_LIBRARIES := perplesdklua_static

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

include $(LOCAL_PATH)/../../lua-bindings/Android.mk
