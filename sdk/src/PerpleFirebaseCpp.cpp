#include "PerpleFirebaseCpp.h"
#include "PerpleCore.h"
#include <sstream>
#include <stdarg.h>

////////////////////////////////////////////////////////////////////////////////////////////////////

void Log(const char* format, ...)
{
#if defined(__ANDROID__)
    // Log a message that can be viewed in "adb logcat".
    static const int kLineBufferSize = 100;
    char buffer[kLineBufferSize + 2];

    va_list list;
    va_start(list, format);

    int string_len = vsnprintf(buffer, kLineBufferSize, format, list);

    // append a linebreak to the buffer:
    string_len = string_len < kLineBufferSize ? string_len : kLineBufferSize;
    buffer[string_len] = '\n';
    buffer[string_len + 1] = '\0';

    __android_log_vprint(ANDROID_LOG_INFO, "PerpleFirebaseCpp, format, list);
    va_end(list);
}
/*
#elif defined(__APPLE__)
    // Log a message that can be viewed in the console.
    va_list args;
    NSString *formatString = @(format);

    va_start(args, format);
    NSString *message = [[NSString alloc] initWithFormat:formatString arguments:args];
    va_end(args);

    NSLog(@"%@", message);
}
*/
#else
    va_list list;
    va_start(list, format);
    vprintf(format, list);
    va_end(list);
    printf("\n");
    fflush(stdout);
}
#endif  // __ANDROID__

std::string IntToString(int arg)
{
    std::ostringstream oss;
    oss << arg;
    return oss.str();
}

////////////////////////////////////////////////////////////////////////////////////////////////////

/*
class PerpleMessageListener : public firebase::messaging::Listener
{
public:
    virtual void OnMessage(const ::firebase::messaging::Message& message)
    {
        // When messages are received by the server, they are placed into an
        // internal queue, waiting to be consumed. When ProcessMessages is called,
        // this OnMessage function is called once for each queued message.
        Log("Recieved a new message");
        if (!message.from.empty()) Log("from: %s", message.from.c_str());
        if (!message.data.empty())
        {
            Log("data:");
            typedef std::map<std::string, std::string>::const_iterator MapIter;
            for (MapIter it = message.data.begin(); it != message.data.end(); ++it)
            {
                Log("  %s: %s", it->first.c_str(), it->second.c_str());
            }
        }
    }

    virtual void OnTokenReceived(const char* token)
    {
        // To send a message to a specific instance of your app a registration token
        // is required. These tokens are unique for each instance of the app. When
        // messaging::Initialize is called, a request is sent to the Firebase Cloud
        // Messaging server to generate a token. When that token is ready,
        // OnTokenReceived will be called. The token should be cached locally so
        // that a request doesn't need to be generated each time the app is started.
        //
        // Once a token is generated is should be sent to your app server, which can
        // then use it to send messages to users.
        Log("Recieved Registration Token: %s", token);
    }
};

PerpleMessageListener glistener;
*/

////////////////////////////////////////////////////////////////////////////////////////////////////

PerpleFirebaseCpp* PerpleFirebaseCpp::sMyInstance = nullptr;

PerpleFirebaseCpp::PerpleFirebaseCpp()
{

}

PerpleFirebaseCpp::~PerpleFirebaseCpp()
{

}

#if defined(__ANDROID__)
void PerpleFirebaseCpp::Init(JNIEnv* env, jobject activity)
#else
void PerpleFirebaseCpp::Init()
#endif
{
#if defined(__ANDROID__)
    mApp = firebase::App::Create(firebase::AppOptions(), env, activity);
#else
    mApp = firebase::App::Create(firebase::AppOptions());
#endif  // defined(__ANDROID__)

    //firebase::messaging::Initialize(*mApp, &glistener);

    mAuth = firebase::auth::Auth::GetAuth(mApp);
}

void PerpleFirebaseCpp::SignInAnonymously(const TypePerpleFirebaseCppCallback& callback)
{
    firebase::Future<firebase::auth::User*> result = mAuth->SignInAnonymously();

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            auto callback = *(TypePerpleFirebaseCppCallback*)user_data;

            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    std::string info = GetLoginInfo(user);
#ifndef NDEBUG
                    Log("Firebase SignInAnonymously success - info:%s", info.c_str());
#endif
                    callback("success", info.c_str());
                }
                else
                {
                    std::string info = PerpleCore::GetErrorInfo(ERROR_FIREBASE_LOGIN, IntToString(result.Error()), result.ErrorMessage());
#ifndef NDEBUG
                    Log("Firebase SignInAnonymously fail - info:%s", info.c_str());
#endif
                    callback("fail", info.c_str());
                }
            }
        },
        (void*)(&callback));
}

void PerpleFirebaseCpp::CreateUserWithEmailAndPassword(const char* email, const char* password, const TypePerpleFirebaseCppCallback& callback)
{
    firebase::Future<firebase::auth::User*> result = mAuth->CreateUserWithEmailAndPassword(email, password);

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            auto callback = *(TypePerpleFirebaseCppCallback*)user_data;

            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    std::string info = GetLoginInfo(user);
#ifndef NDEBUG
                    Log("Firebase CreateUserWithEmailAndPassword success - info:%s", info.c_str());
#endif
                    callback("success", info.c_str());
                }
                else
                {
                    std::string info = PerpleCore::GetErrorInfo(ERROR_FIREBASE_LOGIN, IntToString(result.Error()), result.ErrorMessage());
#ifndef NDEBUG
                    Log("Firebase CreateUserWithEmailAndPassword fail - info:%s", info.c_str());
#endif
                    callback("fail", info.c_str());
                }
            }
        },
        (void*)(&callback));
}

void PerpleFirebaseCpp::SignInWithEmailAndPassword(const char* email, const char* password, const TypePerpleFirebaseCppCallback& callback)
{
    firebase::Future<firebase::auth::User*> result = mAuth->SignInWithEmailAndPassword(email, password);

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            auto callback = *(TypePerpleFirebaseCppCallback*)user_data;

            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    std::string info = GetLoginInfo(user);
#ifndef NDEBUG
                    Log("Firebase SignInWithEmailAndPassword success - info:%s", info.c_str());
#endif
                    callback("success", info.c_str());
                }
                else
                {
                    std::string info = PerpleCore::GetErrorInfo(ERROR_FIREBASE_LOGIN, IntToString(result.Error()), result.ErrorMessage());
#ifndef NDEBUG
                    Log("Firebase SignInWithEmailAndPassword fail - info:%s", info.c_str());
#endif
                    callback("fail", info.c_str());
                }
            }
        },
        (void*)(&callback));
}

void PerpleFirebaseCpp::SignInWithGoogleLogin(const char* google_id_token, const TypePerpleFirebaseCppCallback& callback)
{
    firebase::auth::Credential credential = firebase::auth::GoogleAuthProvider::GetCredential(google_id_token, nullptr);
    firebase::Future<firebase::auth::User*> result = mAuth->SignInWithCredential(credential);

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            auto callback = *(TypePerpleFirebaseCppCallback*)user_data;

            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    std::string info = GetLoginInfo(user);
#ifndef NDEBUG
                    Log("Firebase SignInWithGoogleLogin success - info:%s", info.c_str());
#endif
                    callback("success", info.c_str());
                }
                else
                {
                    std::string info = PerpleCore::GetErrorInfo(ERROR_FIREBASE_LOGIN, IntToString(result.Error()), result.ErrorMessage());
#ifndef NDEBUG
                    Log("Firebase SignInWithGoogleLogin fail - info:%s", info.c_str());
#endif
                    callback("fail", info.c_str());
                }
            }
        },
        (void*)(&callback));
}

void PerpleFirebaseCpp::SignInWithFacebookLogin(const char* facebook_access_token, const TypePerpleFirebaseCppCallback& callback)
{
    firebase::auth::Credential credential = firebase::auth::FacebookAuthProvider::GetCredential(facebook_access_token);
    firebase::Future<firebase::auth::User*> result = mAuth->SignInWithCredential(credential);

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            auto callback = *(TypePerpleFirebaseCppCallback*)user_data;

            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    std::string info = GetLoginInfo(user);
#ifndef NDEBUG
                    Log("Firebase SignInWithFacebookLogin success - info:%s", info.c_str());
#endif
                    callback("success", info.c_str());
                }
                else
                {
                    std::string info = PerpleCore::GetErrorInfo(ERROR_FIREBASE_LOGIN, IntToString(result.Error()), result.ErrorMessage());
#ifndef NDEBUG
                    Log("Firebase SignInWithFacebookLogin fail - info:%s", info.c_str());
#endif
                    callback("fail", info.c_str());
                }
            }
        },
        (void*)(&callback));
}

void PerpleFirebaseCpp::SignInWithTwitterLogin(const char* twitter_token, const char* secret, const TypePerpleFirebaseCppCallback& callback)
{
    firebase::auth::Credential credential = firebase::auth::TwitterAuthProvider::GetCredential(twitter_token, secret);
    firebase::Future<firebase::auth::User*> result = mAuth->SignInWithCredential(credential);

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            auto callback = *(TypePerpleFirebaseCppCallback*)user_data;

            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    std::string info = GetLoginInfo(user);
#ifndef NDEBUG
                    Log("Firebase SignInWithTwitterLogin success - info:%s", info.c_str());
#endif
                    callback("success", info.c_str());
                }
                else
                {
                    std::string info = PerpleCore::GetErrorInfo(ERROR_FIREBASE_LOGIN, IntToString(result.Error()), result.ErrorMessage());
#ifndef NDEBUG
                    Log("Firebase SignInWithTwitterLogin fail - info:%s", info.c_str());
#endif
                    callback("fail", info.c_str());
                }
            }
        },
        (void*)(&callback));
}

void PerpleFirebaseCpp::SignInWithCustomToken(const char* custom_token, const TypePerpleFirebaseCppCallback& callback)
{
    firebase::Future<firebase::auth::User*> result = mAuth->SignInWithCustomToken(custom_token);

    result.OnCompletion(
        [](const firebase::Future<firebase::auth::User*>& result, void* user_data)
        {
            auto callback = *(TypePerpleFirebaseCppCallback*)user_data;

            if (result.Status() == firebase::kFutureStatusComplete)
            {
                if (result.Error() == firebase::auth::kAuthErrorNone)
                {
                    firebase::auth::User* user = *result.Result();
                    std::string info = GetLoginInfo(user);
#ifndef NDEBUG
                    Log("Firebase SignInWithCustomToken success - info:%s", info.c_str());
#endif
                    callback("success", info.c_str());
                }
                else
                {
                    std::string info = PerpleCore::GetErrorInfo(ERROR_FIREBASE_LOGIN, IntToString(result.Error()), result.ErrorMessage());
#ifndef NDEBUG
                    Log("Firebase SignInWithCustomToken fail - info:%s", info.c_str());
#endif
                    callback("fail", info.c_str());
                }
            }
        },
        (void*)(&callback));
}

std::string PerpleFirebaseCpp::GetLoginInfo(firebase::auth::User* user)
{
    Json::Value root;

    if (user != nullptr)
    {
        root["profile"] = GetUserProfile(user);
        root["providerData"] = GetPrividerSpecificInfo(user);
    }

    Json::StyledWriter writer;
    return writer.write(root);
}

Json::Value PerpleFirebaseCpp::GetUserProfile(firebase::auth::User* user)
{
    Json::Value root;

    if (user != nullptr)
    {
        // The user's ID, unique to the Firebase project.
        // Do NOT use this value to authenticate with your backend server,
        // if you have one. Use User::Token() instead.
        root["uid"] = user->UID();

        root["name"] = user->DisplayName();
        root["email"] = user->Email();
        root["photoUrl"] = user->PhotoUrl();
        root["providerId"] = user->ProviderId();
    }

    return root;
}

Json::Value PerpleFirebaseCpp::GetPrividerSpecificInfo(firebase::auth::User* user)
{
    Json::Value root(Json::arrayValue);

    auto provider_data = user->ProviderData();
    for (int i = 0; i < provider_data.size(); i++)
    {
        auto user_info = provider_data.at(i);

        Json::Value json_value;

        // Id of the provider (ex: google.com, facebook.com, firebase, email)
        json_value["providerId"] = user_info->ProviderId();

        json_value["uid"] = user_info->UID();
        json_value["name"] = user_info->DisplayName();
        json_value["email"] = user_info->Email();
        json_value["photoUrl"] = user_info->PhotoUrl();

        root.append(json_value);
    }

    return root;
}

////////////////////////////////////////////////////////////////////////////////////////////////////
