LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := perplesdklua_static
LOCAL_MODULE_FILENAME := libperplesdklua
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libperplesdklua.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := perplesdk_shared
LOCAL_MODULE_FILENAME := libperplesdk
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libperplesdk.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_SHARED_LIBRARY)
