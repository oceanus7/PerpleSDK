// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_APP_CLIENT_CPP_SRC_VERSION_H_
#define FIREBASE_APP_CLIENT_CPP_SRC_VERSION_H_

/// @brief Namespace that encompasses all Firebase APIs.
namespace firebase {

/// @def FIREBASE_VERSION_MAJOR
/// @brief Major version number of the Firebase C++ SDK.
/// @see kFirebaseVersionString
#define FIREBASE_VERSION_MAJOR 1
/// @def FIREBASE_VERSION_MINOR
/// @brief Minor version number of the Firebase C++ SDK.
/// @see kFirebaseVersionString
#define FIREBASE_VERSION_MINOR 0
/// @def FIREBASE_VERSION_REVISION
/// @brief Revision number of the Firebase C++ SDK.
/// @see kFirebaseVersionString
#define FIREBASE_VERSION_REVISION 0

/// @cond FIREBASE_APP_INTERNAL
#define FIREBASE_STRING_EXPAND(X) #X
#define FIREBASE_STRING(X) FIREBASE_STRING_EXPAND(X)
/// @endcond

// Version number.
// clang-format off
#define FIREBASE_VERSION_NUMBER_STRING        \
  FIREBASE_STRING(FIREBASE_VERSION_MAJOR) "." \
  FIREBASE_STRING(FIREBASE_VERSION_MINOR) "." \
  FIREBASE_STRING(FIREBASE_VERSION_REVISION)
// clang-format on

// Concatenated version string.
#define FIREBASE_VERSION_STRING "Firebase C++ " FIREBASE_VERSION_NUMBER_STRING

#if !defined(_WIN32) && !defined(__CYGWIN__) && !defined(DOXYGEN)
extern volatile __attribute__((weak)) const char *kFirebaseVersionString;
volatile __attribute__((weak)) const char *kFirebaseVersionString =
    FIREBASE_VERSION_STRING;
#else
/// @brief String which identifies the current version of the Firebase C++ SDK.
///
/// @see FIREBASE_VERSION_MAJOR
/// @see FIREBASE_VERSION_MINOR
/// @see FIREBASE_VERSION_REVISION
static const char* kFirebaseVersionString = FIREBASE_VERSION_STRING;
#endif  // (!defined(_WIN32) && !defined(__CYGWIN__)) || defined(DOXYGEN)

}  // namespace firebase

#endif  // FIREBASE_APP_CLIENT_CPP_SRC_VERSION_H_
