#include "firebase.h"

PerpleFirebase* PerpleFirebase::MyInstance = nullptr;

PerpleFirebase::PerpleFirebase()
{

}

PerpleFirebase::~PerpleFirebase()
{

}

#if defined(__ANDROID__)
void PerpleFirebase::InitFirebase(JNIEnv* env, jobject activity)
#else
void PerpleFirebase::InitFirebase()
#endif
{
#if defined(__ANDROID__)
    m_App = firebase::App::Create(firebase::AppOptions(), env, activity);
#else
    m_App = firebase::App::Create(firebase::AppOptions());
#endif  // defined(__ANDROID__)

    m_Auth = firebase::auth::Auth::GetAuth(m_App);
}

void PerpleFirebase::AuthWithFirebaseAnonymously()
{
    firebase::Future<firebase::auth::User*> result = m_Auth->SignInAnonymously();

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    printf("Sign in succeeded for '%s'\n", user->DisplayName().c_str());
                }
                else
                {
                    printf("Sign in failed with error '%s'\n", result.ErrorMessage());
                }
            }
        },
        nullptr);
}

void PerpleFirebase::AuthCreateWithFirebaseEmailPassword(const char* email, const char* password)
{
    firebase::Future<firebase::auth::User*> result = m_Auth->CreateUserWithEmailAndPassword(email, password);

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    printf("Create user succeeded for email '%s'\n", user->Email().c_str());
                }
                else
                {
                    printf("Created user failed with error '%s'\n", result.ErrorMessage());
                }
            }
        },
        nullptr);
}

void PerpleFirebase::AuthSignInWithFirebaseEmailPassword(const char* email, const char* password)
{
    firebase::Future<firebase::auth::User*> result = m_Auth->SignInWithEmailAndPassword(email, password);

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    printf("Sign in succeeded for email '%s'\n", user->Email().c_str());
                }
                else
                {
                    printf("Sign in failed with error '%s'\n", result.ErrorMessage());
                }
            }
        },
        nullptr);
}

void PerpleFirebase::AuthWithGoogleSignIn(const char* google_id_token)
{
    firebase::auth::Credential credential = firebase::auth::GoogleAuthProvider::GetCredential(google_id_token, nullptr);
    firebase::Future<firebase::auth::User*> result = m_Auth->SignInWithCredential(credential);

    result.OnCompletion(
    [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
    {
        if (result.Status() == firebase::kFutureStatusComplete)
        {
            if (result.Error() == firebase::auth::kAuthErrorNone)
            {
                firebase::auth::User* user = *result.Result();
                printf("Sign in succeeded for '%s'\n", user->DisplayName().c_str());
            }
            else
            {
                printf("Sign in failed with error '%s'\n", result.ErrorMessage());
            }
        }
    },
    nullptr);
}

void PerpleFirebase::AuthWithFacebookLogin(const char* facebook_access_token)
{
    firebase::auth::Credential credential = firebase::auth::FacebookAuthProvider::GetCredential(facebook_access_token);
    firebase::Future<firebase::auth::User*> result = m_Auth->SignInWithCredential(credential);

    result.OnCompletion(
    [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
    {
        if (result.Status() == firebase::kFutureStatusComplete)
        {
            if (result.Error() == firebase::auth::kAuthErrorNone)
            {
                firebase::auth::User* user = *result.Result();
                printf("Sign in succeeded for '%s'\n", user->DisplayName().c_str());
            }
            else
            {
                printf("Sign in failed with error '%s'\n", result.ErrorMessage());
            }
        }
    },
    nullptr);
}

void PerpleFirebase::AuthWithTwitterLogin(const char* token, const char* secret)
{
    firebase::auth::Credential credential = firebase::auth::TwitterAuthProvider::GetCredential(token, secret);
    firebase::Future<firebase::auth::User*> result = m_Auth->SignInWithCredential(credential);

    result.OnCompletion(
    [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
    {
        if (result.Status() == firebase::kFutureStatusComplete)
        {
            if (result.Error() == firebase::auth::kAuthErrorNone)
            {
                firebase::auth::User* user = *result.Result();
                printf("Sign in succeeded for '%s'\n", user->DisplayName().c_str());
            }
            else
            {
                printf("Sign in failed with error '%s'\n", result.ErrorMessage());
            }
        }
    },
    nullptr);
}

void PerpleFirebase::AuthWithCustomAuthSystem(const char* custom_token)
{
    firebase::Future<firebase::auth::User*> result = m_Auth->SignInWithCustomToken(custom_token);

    result.OnCompletion(
    [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
    {
        if (result.Status() == firebase::kFutureStatusComplete)
        {
            if (result.Error() == firebase::auth::kAuthErrorNone)
            {
                firebase::auth::User* user = *result.Result();
                printf("Sign in succeeded for '%s'\n", user->DisplayName().c_str());
            }
            else
            {
                printf("Sign in failed with error '%s'\n", result.ErrorMessage());
            }
        }
    },
    nullptr);
}

void PerpleFirebase::CurrentUserInfo()
{
    m_User = m_Auth->CurrentUser();
    if (m_User != nullptr)
    {
        std::string name = m_User->DisplayName();
        std::string email = m_User->Email();
        std::string photo_url = m_User->PhotoUrl();

        // The user's ID, unique to the Firebase project.
        // Do NOT use this value to authenticate with your backend server,
        // if you have one. Use User::Token() instead.
        std::string uid = m_User->UID();

        printf("Display Name : %s\n", name.c_str());
        printf("Email : %s\n", email.c_str());
        printf("Photo URL : %s\n", photo_url.c_str());
        printf("UID : %s\n", uid.c_str());
    }
}
