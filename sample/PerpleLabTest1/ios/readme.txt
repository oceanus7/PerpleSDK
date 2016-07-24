1. Google 서비스는 CocoaPods를 사용하여 종속 항목을 설치하고 관리합니다. 터미널 창을 열어 앱용 Xcode 프로젝트가 있는 위치로 이동하세요.

    1. Podfile이 없는 경우 다음 명령어로 새로 만드세요.

        $ pod init

    2. Podfile을 열고 다음 코드를 추가하세요.(기본적으로 Firebase Analytics 포함됨)

        $ pod 'Firebase'

    3. 파일을 저장하고 다음 명령어를 실행하세요.

        $ pod install

앱에 사용할 .xcworkspace 파일이 생성됩니다. 향후 애플리케이션의 모든 개발 작업에 이 파일을 사용하세요.

2. 앱을 시작할 때 Firebase에 연결하려면 기본 AppDelegate 클래스에 아래의 초기화 코드를 추가하세요.

[Objective-C]

@import UIKit;
@import Firebase;

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  [FIRApp configure];
  return YES;
}

[Swift]

import UIKit
import Firebase

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

  var window: UIWindow?

  func application(application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?)
    -> Bool {
    FIRApp.configure()
    return true
  }
}
