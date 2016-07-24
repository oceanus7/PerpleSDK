Firebase C++ SDK
================

The Firebase C++ SDK provides C++ interfaces for the following Firebase
services on *iOS* and *Android*:

* AdMob
* Firebase Analytics
* Firebase App Invites
* Firebase Authentication
* Firebase Cloud Messaging
* Firebase Dynamic Links
* Firebase Remote Config

## Stub Implementations

Stub (non-functional) implementations are provided for convenience when
building for Windows, OSX and Linux so the developer does not need to
conditionally compile code when also targeting the desktop.

Directory Structure
-------------------

The following table provides an overview of the Firebase C++ SDK directory
structure.

| Directories                   | Contents                                    |
|-------------------------------|---------------------------------------------|
| include                       | C++ headers                                 |
| frameworks/ios/ARCH           | iOS frameworks (compiled against libc++)    |
|                               | A multi-architecture framework is \         |
|                               | provided in the *universal* directory.      |
| libs/ios/ARCH                 | OSX static libraries (compiled against \    |
|                               | libc++)                                     |
|                               | Multi-architecture libraries are  \         |
|                               | provided in the *universal* directory.      |
| libs/android/ARCH/STL         | Android (GCC 4.8+ compatible) static \      |
|                               | libraries for each architecture and STL \   |
|                               | variant.                                    |
| *Stub Implementations*        |                                             |
| libs/darwin                   | OSX static libraries (stub \                |
|                               | implementation, compiled against libc++)    |
| frameworks/darwin             | OSX frameworks (stub implementations, \     |
|                               | compiled against libc++)                    |
| libs/linux                    | Linux static libraries (GCC 4.7+, libc++).  |
| libs/windows                  | Windows static libraries \                  |
|                               | (stub implementation, MSVC 2015+)           |

Library / Framework Dependencies
--------------------------------

Each feature has dependencies upon libraries in this SDK and components
distributed as part of the core Firebase
[iOS SDK](https://firebase.google.com/docs/ios/setup) and
[Android SDK](https://firebase.google.com/docs/android/setup).

### Android Dependencies

| Feature                  | Required Libraries and Gradle Packages           |
|--------------------------|--------------------------------------------------|
| Firebase AdMob           | libadmob.a<bt> \                                 |
|                          | libapp.a<br> \                                   |
|                          | com.google.android.gms:play-services-ads \       |
|                          | Maven package                                    |
| Firebase Analytics       | libanalytics.a<br> \                             |
|                          | libapp.a<br> \                                   |
|                          | com.google.android.gms:firebase-analytics \      |
|                          | Maven package                                    |
| Firebase Authentication  | libauth.a<br>  \                                 |
|                          | libapp.a<br> \                                   |
|                          | com.google.android.gms:firebase-auth \           |
|                          | Maven package                                    |
| Firebase Invites and \   | libinvites.a<br> \                               |
| Dynamic Links            | libapp.a<br> \                                   |
|                          | com.google.android.gms:firebase-invites \        |
|                          | Maven package                                    |
| Firebase Messaging       | libmessaging.a<br> \                             |
|                          | libapp.a<br> \                                   |
|                          | com.google.android.gms:firebase-messaging \      |
|                          | (Maven package)<br> \                            |
|                          | libmessaging_java.jar (Android service)          |
| Firebase Remote Config   | libremote_config.a<br> \                         |
|                          | libapp.a<br> \                                   |
|                          | com.google.android.gms:firebase-config \         |
|                          | (Maven package)                                  |

### iOS Dependencies

iOS users can include either frameworks or static libraries depending upon
their preferred build environment.

#### Frameworks

| Feature                      | Required Frameworks and Cocoapods            |
|------------------------------|----------------------------------------------|
| Firebase AdMob               | firebase_admob.framework<br> \               |
|                              | firebase.framework<br> \                     |
|                              | Firebase/Core Cocoapod<br> \                 |
|                              | Google-Mobile-Ads-SDK Cocoapod               |
| Firebase Analytics           | firebase_analytics.framework<br> \           |
|                              | firebase.framework<br> \                     |
|                              | Firebase/Core Cocoapod                       |
| Firebase Authentication      | firebase_auth.framework<br>  \               |
|                              | firebase.framework<br> \                     |
|                              | Firebase/Auth Cocoapod                       |
| Firebase Invites and \       | firebase_invites.framework<br> \             |
| Dynamic Links                | firebase.framework<br> \                     |
|                              | Firebase/Invites Cocoapod                    |
| Firebase Cloud Messaging     | firebase_messaging.framework<br> \           |
|                              | firebase.framework<br> \                     |
|                              | Firebase/Messaging Cocoapod                  |
| Firebase Remote Config       | firebase_remote_config.framework<br> \       |
|                              | firebase.framework<br> \                     |
|                              | Firebase/RemoteConfig Cocoapod               |


#### Libraries

If you prefer to link against static libraries instead of frameworks (see the
previous section) the following table describes the libraries and Cocoapods
required for each SDK feature.

| Feature                      | Required Libraries and Cocoapods             |
|------------------------------|----------------------------------------------|
| Firebase AdMob               | libadmob.a<br> \                             |
|                              | libapp.a<br> \                               |
|                              | Firebase/Core Cocoapod<br> \                 |
|                              | Google-Mobile-Ads-SDK Cocoapod               |
| Firebase Analytics           | libanalytics.a<br> \                         |
|                              | libapp.a<br> \                               |
|                              | Firebase/Core Cocoapod                       |
| Firebase Authentication      | libauth.a<br>  \                             |
|                              | libapp.a<br> \                               |
|                              | FirebaseAuth Cocoapod                        |
| Firebase Invites and \       | libinvites.a<br> \                           |
| Dynamic Links                | libapp.a<br> \                               |
|                              | Firebase/AppInvites Cocoapod                 |
| Firebase Cloud Messaging     | libmessaging.a<br> \                         |
|                              | libapp.a<br> \                               |
|                              | Firebase/CloudMessaging Cocoapod             |
| Firebase Remote Config       | libremote_config.a<br> \                     |
|                              | libapp.a<br> \                               |
|                              | Firebase/RemoteConfig Cocoapod               |


### Stub Implementation Dependencies

| Feature                      | Required Libraries and Gradle Packages       |
|------------------------------|----------------------------------------------|
| Firebase AdMob               | libadmob.a<br> \                             |
|                              | libapp.a<br>                                 |
| Firebase Analytics           | libanalytics.a<br> \                         |
|                              | libapp.a<br>                                 |
| Firebase Authentication      | libauth.a<br>  \                             |
|                              | libapp.a<br>                                 |
| Firebase Invites and \       | libinvites.a<br> \                           |
| Dynamic Links                | libapp.a<br>                                 |
| Firebase Cloud Messaging     | libmessaging.a<br> \                         |
|                              | libapp.a<br>                                 |
| Firebase Remote Config       | libremote_config.a<br> \                     |
|                              | libapp.a<br>                                 |

Getting Started
---------------

See our [setup guide](https://firebase.google.com/docs/cpp/setup) to get
started.

### Custom Android Build Systems

We currently provide generate\_xml\_from\_google\_services\_json.py to convert
google-services.json to .xml resources to be included in an application.  This
script applies the same transformation that the Google Play Services Gradle
plug-in performs when building Android applications.  Users who don't use
Gradle (e.g ndk-build, makefiles, Visual Studio etc.) can use this script
to automate the generation of string resources.

### ProGuard on Android ###

Many Android build systems use
[ProGuard](https://developer.android.com/studio/build/shrink-code.html) for
builds in Release mode to shrink application sizes and protect Java source code.
If you use ProGuard, you will need to add the files in libs/android/*.pro
corresponding to the Firebase C++ libraries you are using to your ProGuard
configuration.

For example, with Gradle, build.gradle would contain:
~~~
  android {
    [...other stuff...]
    buildTypes {
      release {
        minifyEnabled true
        proguardFile getDefaultProguardFile('your-project-proguard-config.txt')
        proguardFile file(project.ext.firebase_cpp_sdk_dir + "/libs/android/app.pro")
        proguardFile file(project.ext.firebase_cpp_sdk_dir + "/libs/android/analytics.pro")
        [...and so on, for each Firebase C++ library you are using.]
      }
    }
  }
~~~


Release Notes
-------------

### 1.0.0
  - Overview
     - First public release.
       See our [setup guide](https://firebase.google.com/docs/cpp/setup) to get
       started.
  - Known Issues
     - Android armeabi libraries must be linked with gcc 4.8.
