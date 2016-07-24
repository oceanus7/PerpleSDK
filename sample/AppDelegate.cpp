#include "AppDelegate.h"
#include "CCLuaEngine.h"
#include "SimpleAudioEngine.h"
#include "cocos2d.h"
#include "Runtime.h"
#include "ConfigParser.h"
#include "network\HttpClient.h"

// @perplesdk
#include "PerpleSDKLua.h"

using namespace CocosDenshion;

USING_NS_CC;
using namespace std;

// Register custom lua function.
void register_custom_function(lua_State* L)
{
    // @perplesdk
    luaopen_perplesdk(L);
}

AppDelegate::AppDelegate()
{
}

AppDelegate::~AppDelegate()
{
    // HttpClient 사용시 앱 종료 때 crash나는 cocos2d 자체의 버그로 추가함(jjo)
    network::HttpClient::getInstance()->destroyInstance();
    SimpleAudioEngine::end();
}

bool AppDelegate::applicationDidFinishLaunching()
{
#if (CC_TARGET_PLATFORM != CC_PLATFORM_WIN32)
#if (COCOS2D_DEBUG>0)
    initRuntime();
#endif
#endif

    string pathRoot = "";
#if (CC_TARGET_PLATFORM == CC_PLATFORM_WIN32)
#if (COCOS2D_DEBUG>0)
    pathRoot = "../../";
#else
    pathRoot = "Resource/";
#endif
#endif
    FileUtils::getInstance()->addSearchPath(pathRoot + "src");
    FileUtils::getInstance()->addSearchPath(pathRoot + "res");

    if (!ConfigParser::getInstance()->isInit())
    {
        ConfigParser::getInstance()->readConfig();
    }

    Size viewSize = ConfigParser::getInstance()->getInitViewSize();
    if (viewSize.height > viewSize.width)
    {
        swap(viewSize.width, viewSize.height);
    }

    bool isLandscape = ConfigParser::getInstance()->isLandscape();
    if (!isLandscape)
    {
        swap(viewSize.width, viewSize.height);
    }

    // Initialize director.
    auto director = Director::getInstance();
    auto glview = director->getOpenGLView();
    if (!glview)
    {
        string title = ConfigParser::getInstance()->getInitViewName();
#if (CC_TARGET_PLATFORM == CC_PLATFORM_WIN32 || CC_TARGET_PLATFORM == CC_PLATFORM_MAC)
        extern void createSimulator(const char *viewName, float width, float height, bool isLandscape = true, float frameZoomFactor = 1.0f);
        double frameZoomFactor = ConfigParser::getInstance()->getFrameZoomFactor();
        createSimulator(title.c_str(), viewSize.width, viewSize.height, isLandscape, frameZoomFactor);
        glview = director->getOpenGLView();
#else
        glview = GLView::createWithRect(title.c_str(), Rect(0, 0, viewSize.width, viewSize.height));
        director->setOpenGLView(glview);
#endif
    }

    glview->setDesignResolutionSize(viewSize.width, viewSize.height, ResolutionPolicy::NO_BORDER);

    // Turn on display FPS.
    director->setDisplayStats(true);

    // Set FPS. The default value is 1.0/60 if you don't call this.
    director->setAnimationInterval(1.0 / 60);

    auto engine = LuaEngine::getInstance();
    ScriptEngineManager::getInstance()->setScriptEngine(engine);

    // Register custom function.
    LuaStack *stack = engine->getLuaStack();
    register_custom_function(stack->getLuaState());

#if (COCOS2D_DEBUG>0)
    bool isUseConnectLayerInDebug = ConfigParser::getInstance()->isUseConnectLayerInDebug();
    if (isUseConnectLayerInDebug)
    {
        if (startRuntime())
            return true;
    }
#endif

    engine->executeScriptFile(ConfigParser::getInstance()->getEntryFile().c_str());
    return true;
}

// This function will be called when the app is inactive. When comes a phone call, it's be invoked too.
void AppDelegate::applicationDidEnterBackground()
{
    Director::getInstance()->stopAnimation();

    SimpleAudioEngine::getInstance()->pauseBackgroundMusic();
}

// This function will be called when the app is active again.
void AppDelegate::applicationWillEnterForeground()
{
    Director::getInstance()->startAnimation();

    SimpleAudioEngine::getInstance()->resumeBackgroundMusic();
}
