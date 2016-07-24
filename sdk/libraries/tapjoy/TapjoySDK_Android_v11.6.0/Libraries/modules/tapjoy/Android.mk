##
## Copyright (c) 2014-2015 Tapjoy
## All rights reserved.
##

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE            := tapjoy_shared
LOCAL_SRC_FILES         := shared/$(TARGET_ARCH_ABI)/libtapjoy.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_EXPORT_CFLAGS     := -DTAPJOY_SHARED=1
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := tapjoy_static
LOCAL_SRC_FILES         := static/$(TARGET_ARCH_ABI)/libtapjoy.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_EXPORT_CFLAGS     := -DTAPJOY_STATIC=1
include $(PREBUILT_STATIC_LIBRARY)
