1. firebase와 라이브러리는 android min sdk version 으로 10 이상을 요구하며, facebook의 경우에는 15 이상을 요구함

2. sdk/bin/prebuilt/android/libs 파일 아래 있는 모든 jar 파일을 해당 프로젝트 libs 폴더 아래로 복사

3. sdk/bin/prebuilt/android 파일 아래 perplesdk.jar 파일도 해당 프로젝트 libs 폴더 아래로 복사

4. 예시로 첨부된 Android.mk 파일을 참조하여 프로젝트의 jni 폴더 아래 Android.mk 파일 수정 (@perplesdk 주석 참고)

5. AndroidManifest.xml, strings.xml, AppActivity.java, AppDelegate.cpp 파일을 참고(@perplesdk 주석 참고)하여 루아바인딩 코드 추가

6. luacode.lua 파일을 참고하여 루아 소스 코드내 라이브러리 관련 코드 추가하고 라이브러리 api 함수를 사용
