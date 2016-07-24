// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_ADMOB_CLIENT_CPP_INCLUDE_FIREBASE_ADMOB_H_
#define FIREBASE_ADMOB_CLIENT_CPP_INCLUDE_FIREBASE_ADMOB_H_

#ifdef __ANDROID__
#include <jni.h>
#endif  // __ANDROID__

#include "firebase/admob/banner_view.h"
#include "firebase/admob/interstitial_ad.h"
#include "firebase/admob/types.h"
#include "firebase/app.h"

namespace firebase {

/// @brief API for AdMob with Firebase.
///
/// This AdMob API allows you to load and display AdMob banners
/// (@ref BannerView) and interstitials (@ref InterstitialAd). Each instance of
/// BannerView or InterstitialAd will create and control a corresponding
/// platform-specific mobile ad view class.
namespace admob {

/// Initializes AdMob via Firebase.
///
/// @param app The Firebase app for which to initialize mobile ads.
void Initialize(const ::firebase::App& app);

#ifdef __ANDROID__
/// Initialize AdMob without Firebase.
///
/// The arguments to @ref Initialize are platform specific so the caller must
/// do something like this:
/// @code
/// #if defined(__ANDROID__)
/// firebase::admob::Initialize(jni_env, activity);
/// #else
/// firebase::admob::Initialize();
/// #endif
/// @endcode
///
/// @param[in] jni_env JNIEnv pointer.
/// @param[in] activity Activity used to start the application.
void Initialize(JNIEnv* jni_env, jobject activity);
#else
/// Initialize AdMob without Firebase.
void Initialize();
#endif  // __ANDROID__

/// @brief Terminate AdMob.
///
/// Frees resources associated with AdMob that were allocated during
/// @ref firebase::admob::Initialize().
void Terminate();

}  // namespace admob
}  // namespace firebase

#endif  // FIREBASE_ADMOB_CLIENT_CPP_INCLUDE_FIREBASE_ADMOB_H_
