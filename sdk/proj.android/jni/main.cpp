#include <jni.h>
#include <android/log.h>
#include "jni/JniHelper.h"
#include "PerpleCore.h"

extern "C"
{
    jint JNI_OnLoad(JavaVM* vm, void* reserved)
    {
        JniHelper::setJavaVM(vm);
        return JNI_VERSION_1_4;
    }

    JNIEXPORT jboolean JNICALL Java_com_perplelab_PerpleSDK_nativeInitJNI(JNIEnv* env, jobject obj, jobject activity)
    {
        bool ret = JniHelper::setClassLoaderFrom(activity);
        return (ret ? JNI_TRUE : JNI_FALSE);
    }

    JNIEXPORT jint JNICALL Java_com_perplelab_PerpleSDK_nativeInitSDK(JNIEnv* env, jobject obj)
    {
        int ret = PerpleCore::InitSDK();
        return ret;
    }

    JNIEXPORT jint JNICALL Java_com_perplelab_PerpleSDK_nativeGetSDKVersion(JNIEnv* env, jobject obj)
    {
        int version = PerpleCore::GetVersion();
        return version;
    }

    JNIEXPORT jstring JNICALL Java_com_perplelab_PerpleSDK_nativeGetSDKVersionString(JNIEnv* env, jobject obj)
    {
        std::string ret = PerpleCore::GetVersionString();
        jstring version = env->NewStringUTF(ret.c_str());
        return version;
    }

    JNIEXPORT jint JNICALL Java_com_perplelab_PerpleSDK_nativeSDKResult(JNIEnv* env, jobject obj, jstring id, jstring result, jstring info)
    {
        jboolean isCopy0;
        jboolean isCopy1;
        jboolean isCopy2;

        const char* id_ = env->GetStringUTFChars(id, &isCopy0);
        const char* result_ = env->GetStringUTFChars(result, &isCopy1);
        const char* info_ = env->GetStringUTFChars(info, &isCopy2);

        if (id_ != NULL)
        {
            PerpleCore::OnSDKResult(id_, result_, info_);
        }

        if (isCopy0 == JNI_TRUE) { env->ReleaseStringUTFChars(id, id_); }
        if (isCopy1 == JNI_TRUE) { env->ReleaseStringUTFChars(result, result_); }
        if (isCopy2 == JNI_TRUE) { env->ReleaseStringUTFChars(info, info_); }

        return 0;
    }
}
