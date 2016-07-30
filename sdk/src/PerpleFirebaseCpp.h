#pragma once

#if defined(__ANDROID__)
#include <android/native_activity.h>
#include <jni.h>
#include <android/log.h>
#elif defined(__APPLE__)
extern "C" {
#include <objc/objc.h>
}  // extern "C"
#endif  // __ANDROID__

#include "firebase/app.h"
#include "firebase/auth.h"
#include "firebase/messaging.h"
#include "jsoncpp/json.h"
#include <functional>

#define TypePerpleFirebaseCppCallback std::function<void(const char*,const char*)>

class PerpleFirebaseCpp
{
public:
    static PerpleFirebaseCpp* sMyInstance;

    static void CreateInstance()
    {
        sMyInstance = new PerpleFirebaseCpp();
    }

    static PerpleFirebaseCpp* GetInstance()
    {
        if (sMyInstance == nullptr)
        {
            CreateInstance();
        }
        return sMyInstance;
    }

#if defined(__ANDROID__)
    void Init(JNIEnv* env, jobject activity);
#else
    void Init();
#endif

    void SignInAnonymously(const TypePerpleFirebaseCppCallback& callback);
    void SignInWithEmailAndPassword(const char* email, const char* password, const TypePerpleFirebaseCppCallback& callback);
    void SignInWithGoogleLogin(const char* google_id_token, const TypePerpleFirebaseCppCallback& callback);
    void SignInWithFacebookLogin(const char* facebook_access_token, const TypePerpleFirebaseCppCallback& callback);
    void SignInWithTwitterLogin(const char* twitter_token, const char* secret, const TypePerpleFirebaseCppCallback& callback);
    void SignInWithCustomToken(const char* custom_token, const TypePerpleFirebaseCppCallback& callback);
    void CreateUserWithEmailAndPassword(const char* email, const char* password, const TypePerpleFirebaseCppCallback& callback);

private:
    PerpleFirebaseCpp();
    ~PerpleFirebaseCpp();

    static std::string GetLoginInfo(firebase::auth::User* user);
    static Json::Value GetUserProfile(firebase::auth::User* user);
    static Json::Value GetProviderData(firebase::auth::User* user);
    static Json::Value GetPushToken();

    firebase::App* mApp;
    firebase::auth::Auth* mAuth;
};
