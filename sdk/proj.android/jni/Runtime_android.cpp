#include <jni.h>
#include <android/log.h>
#include "jni/JniHelper.h"

#define LOG_TAG "PerpleSDKLua"

#if defined(__ANDROID__)
#include <android/log.h>
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#else
#define LOG(...)
#endif

#define PACKAGE_NAME "com/perplelab/PerpleSDKLua"

void jniFuncV_V(const char* funcName, int funcID)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d", funcName, funcID);

	JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "(I)V"))
    {
        t.env->CallStaticVoidMethod(t.classID, t.methodID, funcID);
        t.env->DeleteLocalRef(t.classID);
    }
}

void jniFuncV_S(const char* funcName, int funcID, const char* arg0)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d, arg0:%s", funcName, funcID, arg0);

	JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "(ILjava/lang/String;)V"))
    {
        jstring arg0_ = t.env->NewStringUTF(arg0);

        t.env->CallStaticVoidMethod(t.classID, t.methodID, funcID, arg0_);
        t.env->DeleteLocalRef(t.classID);

        t.env->DeleteLocalRef(arg0_);
    }
}

void jniFuncV_SS(const char* funcName, int funcID, const char* arg0, const char* arg1)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d, arg0:%s, arg1:%s", funcName, funcID, arg0, arg1);

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "(ILjava/lang/String;Ljava/lang/String;)V"))
    {
        jstring arg0_ = t.env->NewStringUTF(arg0);
        jstring arg1_ = t.env->NewStringUTF(arg1);

        t.env->CallStaticVoidMethod(t.classID, t.methodID, funcID, arg0_, arg1_);
        t.env->DeleteLocalRef(t.classID);

        t.env->DeleteLocalRef(arg0_);
        t.env->DeleteLocalRef(arg1_);
    }
}

void jniFuncV_SSS(const char* funcName, int funcID, const char* arg0, const char* arg1, const char* arg2)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d, arg0:%s, arg1:%s, arg2:%s", funcName, funcID, arg0, arg1, arg2);

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "I(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
    {
        jstring arg0_ = t.env->NewStringUTF(arg0);
        jstring arg1_ = t.env->NewStringUTF(arg1);
        jstring arg2_ = t.env->NewStringUTF(arg2);

        t.env->CallStaticVoidMethod(t.classID, t.methodID, funcID, arg0_, arg1_, arg2_);
        t.env->DeleteLocalRef(t.classID);

        t.env->DeleteLocalRef(arg0_);
        t.env->DeleteLocalRef(arg1_);
        t.env->DeleteLocalRef(arg2_);
    }
}

void jniFuncV_I(const char* funcName, int funcID, int arg0)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d, arg0:%d", funcName, funcID, arg0);

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "(II)V"))
    {
        t.env->CallStaticVoidMethod(t.classID, t.methodID, funcID, arg0);
        t.env->DeleteLocalRef(t.classID);
    }
}

void jniFuncV_ISS(const char* funcName, int funcID, int arg0, const char* arg1, const char* arg2)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d, arg0:%d, arg1:%s, arg2:%s", funcName, funcID, arg0, arg1, arg2);

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "(IILjava/lang/String;Ljava/lang/String;)V"))
    {
        jstring arg1_ = t.env->NewStringUTF(arg1);
        jstring arg2_ = t.env->NewStringUTF(arg2);

        t.env->CallStaticVoidMethod(t.classID, t.methodID, funcID, arg0, arg1_, arg2_);
        t.env->DeleteLocalRef(t.classID);

        t.env->DeleteLocalRef(arg1_);
        t.env->DeleteLocalRef(arg2_);
    }
}

void jniFuncV_ISSS(const char* funcName, int funcID, int arg0, const char* arg1, const char* arg2, const char* arg3)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d, arg0:%d, arg1:%s, arg2:%s, arg3:%s", funcName, funcID, arg0, arg1, arg2, arg3);

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
    {
        jstring arg1_ = t.env->NewStringUTF(arg1);
        jstring arg2_ = t.env->NewStringUTF(arg2);
        jstring arg3_ = t.env->NewStringUTF(arg3);

        t.env->CallStaticVoidMethod(t.classID, t.methodID, funcID, arg0, arg1_, arg2_, arg3_);
        t.env->DeleteLocalRef(t.classID);

        t.env->DeleteLocalRef(arg1_);
        t.env->DeleteLocalRef(arg2_);
        t.env->DeleteLocalRef(arg3_);
    }

}

bool jniFuncZ_V(const char* funcName, int funcID)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d", funcName, funcID);

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "(I)Z"))
    {
        jboolean ret = t.env->CallStaticBooleanMethod(t.classID, t.methodID, funcID);
        t.env->DeleteLocalRef(t.classID);
        return (ret == JNI_TRUE);
    }

    return false;
}

bool jniFuncZ_S(const char* funcName, int funcID, const char* arg0)
{
    LOG("Lua callback, calling - funcName:%s, funcID:%d, arg0:%s", funcName, funcID, arg0);

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, PACKAGE_NAME, funcName, "(ILjava/lang/String;)Z"))
    {
        jstring arg0_ = t.env->NewStringUTF(arg0);

        jboolean ret = t.env->CallStaticBooleanMethod(t.classID, t.methodID, funcID, arg0_);

        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(arg0_);

        return (ret == JNI_TRUE);
    }

    return false;
}
