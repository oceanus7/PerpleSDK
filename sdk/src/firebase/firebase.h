#pragma once

#if defined(__ANDROID__)
#include <android/native_activity.h>
#include <jni.h>
#elif defined(__APPLE__)
extern "C" {
#include <objc/objc.h>
}  // extern "C"
#endif  // __ANDROID__

#include "firebase/app.h"
#include "firebase/auth.h"

class PerpleFirebase
{
public:
    static PerpleFirebase* MyInstance;

    static void CreateInstance()
    {
        MyInstance = new PerpleFirebase();
    }

    static PerpleFirebase* GetInstance()
    {
        return MyInstance;
    }

#if defined(__ANDROID__)
    void InitFirebase(JNIEnv* env, jobject activity);
#else
    void InitFirebase();
#endif
    void AuthWithFirebaseAnonymously();
    void AuthCreateWithFirebaseEmailPassword(const char* email, const char* password);
    void AuthSignInWithFirebaseEmailPassword(const char* email, const char* password);
    void AuthWithGoogleSignIn(const char* google_id_token);
    void AuthWithFacebookLogin(const char* facebook_access_token);
    void AuthWithTwitterLogin(const char* token, const char* secret);
    void AuthWithCustomAuthSystem(const char* custom_token);
    void CurrentUserInfo();

private:
    PerpleFirebase();
    ~PerpleFirebase();

    firebase::App* m_App;
    firebase::auth::Auth* m_Auth;
    firebase::auth::User* m_User;

    std::string m_Email;
    std::string m_Password;
};
