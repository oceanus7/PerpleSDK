LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := firebase_app_static
LOCAL_MODULE_FILENAME := libfirebaseapp
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/gnustl/libapp.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := firebase_analytics_static
LOCAL_MODULE_FILENAME := libfirebaseanalytics
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/gnustl/libanalytics.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := firebase_admob_static
LOCAL_MODULE_FILENAME := libfirebaseadmob
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/gnustl/libadmob.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := firebase_messaging_static
LOCAL_MODULE_FILENAME := libfirebasemessaging
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/gnustl/libmessaging.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := firebase_auth_static
LOCAL_MODULE_FILENAME := libfirebaseauth
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/gnustl/libauth.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := firebase_invites_static
LOCAL_MODULE_FILENAME := libfirebaseinvites
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/gnustl/libinvites.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := firebase_remote_config_static
LOCAL_MODULE_FILENAME := libfirebaseremote_config
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/gnustl/libremote_config.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include
include $(PREBUILT_STATIC_LIBRARY)
