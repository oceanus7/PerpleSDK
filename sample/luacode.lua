-- Lua 함수 호출 시 콜백 처리를 위해서 필수적으로 PerpleSDK:updateLuaCallbacks() 를 스케줄러에 등록해야 함.
-- 아래의 예제 코드를 참고할 것.

gPerpleSDKSchedulerID = 0

-- Call this function in entry point (ex. main())
function StartPerpleSDKScheduler()
    gPerpleSDKSchedulerID = scheduler.scheduleUpdateGlobal(function()
        PerpleSDK:updateLuaCallbacks()
    end)
end

-- Call this function in closing application
function EndPerpleSDKScheduler()
    scheduler.unscheduleGlobal(gPerpleSDKSchedulerID)
    gPerpleSDKSchedulerID = 0
end



-- Lua 함수 호출 예제

-- 앱이 포그라운드 상태에서 firebase 푸시를 수신할 것인지 설정
-- @receive : 1(수신) or 0(비수신)
PerpleSDK:setFCMPushOnForeground(@receive)

-- @token : FCM registration token
PerpleSDK:setFCMTokenRefresh(function(ret, info)
    if ret == 'refresh' then
        -- info : "@token"
    elseif ret == 'error' then
    end
end)

-- @token : FCM registration token
PerpleSDK:getFCMToken(function(ret, info)
    if ret == 'success' then
        -- info : "@token"
    elseif ret == 'fail' then
    end
end)

-- @data : key/value 짝으로 구성된 JSON 포맷 문자열
PerpleSDK:sendFCMPushMessage('@data', function(ret, info)
    if ret == 'success' then
    elseif ret = 'fail' then
    end
end)

-- @groupKey : 수신자 그룹 키
-- @data : key/value 짝으로 구성된 JSON 포맷의 문자열
PerpleSDK:sendFCMPushMessageToGroup('@groupKey', '@data', function(ret, info)
    if ret == 'success' then
    elseif ret = 'fail' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:autoLogin(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --     -- 구글 로그인이 링크되어 있어 구글 로그인 처리까지 성공한 경우
        --    "google":{"id":"@id","name":"@name","photoUrl":"@photoUrl"},
        --     -- 페이스북 로그인이 링크되어 있어 페이스북 로그인 처리까지 성공한 경우
        --    "facebook":{"id":"@id","name":"@name","photoUrl":"@photoUrl"}
        --}
    elseif ret == 'fail' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:loginAnonymously(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:loginGoogle(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    elseif ret == 'cancel' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:loginFacebook(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    elseif ret == 'cancel' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:loginEmail('@email', '@password', function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:linkWithGoogle(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    elseif ret == 'cancel' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:linkWithFacebook(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    elseif ret == 'cancel' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:linkWithEmail('@email', '@password', function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:unlinkWithGoogle(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:unlinkWithFacebook(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:unlinkWithEmail(function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    end
end)

-- @providerId : 'google.com' or 'facebook.com' or 'firebase' or 'email' ...
PerpleSDK:createUserWithEmail('@email', '@password', function(ret, info)
    if ret == 'success' then
        -- info
        --{
        --    "profile":{"uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl","providerId":"@providerId"},
        --    "providerData":[{"providerId":"@providerId","uid":"@uid","name":"@name","email":"@email","photoUrl":"@photoUrl"},...],
        --    "pushToken":@token"
        --}
    elseif ret == 'fail' then
    end
end)

PerpleSDK:logout(function(ret, info)
    if ret == 'success' then
    elseif ret == 'fail' then
    end
end)

PerpleSDK:deleteUser(function(ret, info)
    if ret == 'success' then
    elseif ret == 'fail' then
    end
end)

PerpleSDK:facebookLogin(function(ret, info)
    if ret == 'success' then
        -- info : {"id":"@id","name":"@name","photoUrl":"@photoUrl"}
    elseif ret == 'fail' then
    end
end)

PerpleSDK:facebookGetFriends(function(ret, info)
    if ret == 'success' then
        -- info
        -- [
        --     {"id":"@id","name":"@name"},
        --     {"id":"@id","name":"@name"}
        -- ]
    elseif ret == 'fail' then
    end
end)

PerpleSDK:facebookGetInvitableFriends(function(ret, info)
    if ret == 'success' then
        -- info
        -- [
        --     {"id":"@id","name":"@name","photoUrl":"@photoUrl"},
        --     {"id":"@id","name":"@name","photoUrl":"@photoUrl"}
        -- ]
    elseif ret == 'fail' then
    end
end)

-- @data : {"title":"@title","message":"@message","to":"@facebookId"}
PerpleSDK:facebookSendRequest('@data', function(ret, info)
    if ret == 'success' then
        -- info : "@requestId"
    elseif ret == 'fail' then
    elseif ret == 'cancel' then
    end
end)

-- @permission : 퍼미션 이름
bool ret = PerpleSDK:facebookIsGrantedPermission('@permission')
if ret == true then
else
end

-- @permission : 퍼미션 이름
PerpleSDK:facebookAskPermission('@permission', function(ret, info)
    if ret == 'success' then
    elseif ret == 'fail' then
    end
end)

-- @uid: user id
PerpleSDK:adbrixEvent('userId', '@uid', '')

-- @age: user's age
PerpleSDK:adbrixEvent('age', '@age', '')

-- @gender: 'MAIL' or 'FEMAIL'
PerpleSDK:adbrixEvent('gender', '@gender', '')

-- @arg1 : 유저활동 이름(영어와 숫자로 된 공백없는 문자열)
PerpleSDK:adbrixEvent('firstTimeExperience', '@arg1', '')
-- @arg2 : 부가 파라미터
PerpleSDK:adbrixEvent('firstTimeExperience', '@arg1', '@arg2')

-- @arg1 : 유저활동 이름(영어와 숫자로 된 공백없는 문자열)
PerpleSDK:adbrixEvent('retention', '@arg1', '')
-- @arg2 : 부가 파라미터
PerpleSDK:adbrixEvent('retention', '@arg1', '@arg2')

-- @arg1 : 구매아이템 이름(영어와 숫자로 된 공백없는 문자열)
PerpleSDK:adbrixEvent('buy', '@arg1', '')
-- @arg2 : 부가 파라미터(ex, 아이템 가격)
PerpleSDK:adbrixEvent('buy', '@arg1', '@arg2')

-- @cohort_number: 'COHORT_1' or 'COHORT_2' or 'COHORT_3'
-- @arg1: cohort description(in app activity)
PerpleSDK:adbrixEvent('customCohort', '@cohort_number', '@arg1')

-- ProcessKill 사용시 명시적 EndSession 처리를 위함
-- 참고링크 -> http://help.igaworks.com/hc/ko/3_3/Content/Article/processkill_guide_aos
PerpleSDK:adbrixStartSession()
PerpleSDK:adbrixEndSession()

-- @userID: user id
PerpleSDK:tapjoyEvent('userID', '@userID', '')

-- @userLevel: user level
PerpleSDK:tapjoyEvent('userLevel', '@userLevel', '')

-- @userFriendCount: user's friends count
PerpleSDK:tapjoyEvent('userFriendCount', '@userFriendCount', '')

-- @appDataVersion: app data version
PerpleSDK:tapjoyEvent('appDataVersion', '@appDataVersion', '')

-- @arg1 : cohort variable index(ex, 1, 2, 3, ...)
-- @arg2 : cohort value(ex, user activity)
PerpleSDK:tapjoyEvent('userCohortVariable', '@arg1', '@arg2')

-- @arg1
-- 'category;name;parameter1;parameter2'
-- 'category;name;parameter1;parameter2;value'
-- 'category;name;parameter1;parameter2;valueName;value'
-- 'category;name;parameter1;parameter2;value1Name;value1;value2Name;value2'
-- 'category;name;parameter1;parameter2;value1Name;value1;value2Name;value2;value3Name;value3'
PerpleSDK:tapjoyEvent('trackEvent', '@arg1', '')

-- @arg1
-- 'productId;currencyCode;price'
-- 'productId;currencyCode;price;campaignId'
PerpleSDK:tapjoyEvent('trackPurchase', '@arg1', '')
-- @arg2
-- { "skuDetails":"@skuDetails", "purchaseData":"@purchaseData", "dataSignature":"@dataSignature", "campaignId":"@campaignId" }
PerpleSDK:tapjoyEvent('trackPurchase', '', '@arg2')

-- 여기서 구매 추적을 사용하면, 게임 내에서 PerpleSDK:tapjoyEvent('trackPurchase', ...) 를 별도로 호출하지 않아도 Billing 모듈에서의 구매 처리에 따라 구매기록이 트래킹 된다.
-- @isTrackPurchase : 1(구매 추적 사용) or 0(비사용)
PerpleSDK:tapjoySetTrackPurchase(@isTrackPurchase)

-- @placementName의 플레이스먼스를 초기화하고 컨텐츠 다운로드를 시작한다.
-- 컨텐츠가 준비되면 'ready' 콜백이 온다. 이 때, 아래의 tapjoyShowPlacement()를 호출하면 된다.
PerpleSDK:tapjoySetPlacement('@placementName', function(ret, info)
    if ret == 'success' then
    elseif ret == 'fail' then
    elseif ret == 'ready' then
        -- 컨텐츠 다운로드가 끝나고 보여줄 준비되었을 때
    elseif ret == 'purchase' then
        -- info : {"request":"@request","productId":"@productId"}
    elseif ret == 'reward' then
        -- info : {"request":"@request","itemId":"@itemId","quantity":@quantity}
    elseif ret == 'error' then
    end
end)

PerpleSDK:tapjoyShowPlacement('@placementName', function(ret, info)
    if ret == 'show' then
        -- 컨텐츠가 보여질 때
    elseif ret == 'wait' then
        -- 아직 컨텐츠가 준비되지 않은 상태
    elseif ret == 'dismiss' then
        -- 컨텐츠가 사라질 때
    elseif ret == 'error' then
    end
end)

-- 가상 화폐 잔액 조회
PerpleSDK:tapjoyGetCurrency(function(ret, info)
    if ret == 'success' then
        -- info : {"currencyName":"@가상화페 이름","balance":"@잔액" }
    elseif ret == 'fail' then
    end
end)

-- 가상 화폐 획득
-- 가상 화폐를 획득할 때마다 'earn' 콜백이 호출됨
PerpleSDK:tapjoySetEarnedCurrencyCallback(function(ret, info)
    if ret == 'earn' then
        -- info : {"currencyName":"@가상화페 이름", "amount":"@획득한 금액"}
    elseif ret == 'error' then
    end
end)

-- 가상 화폐 사용
PerpleSDK:tapjoySpendCurrency('@amount', function(ret, info)
    if ret == 'success' then
        -- info : {"currencyName":"@가상화페 이름", "balance":"@잔액"}
    elseif ret == 'fail' then
    end
end)

-- 가상 화폐 지급
PerpleSDK:tapjoyAwardCurrency('@amount', function(ret, info)
    if ret == 'success' then
        -- info : {"currencyName":"@가상화페 이름","balance":"@잔액"}
    elseif ret == 'fail' then
    end
end)

PerpleSDK:naverLogin(function(ret, info)
    if ret == 'success' then
        -- info : {"accessToken":"@accessToken","refreshToken":"@refreshToken","expiresAt":"@expiresAt","tokenType":"@tokenType"}
    elseif ret == 'fail' then
    end
end)

-- @isDeleteToken : 1(토큰 삭제) or 0(비삭제)
PerpleSDK:naverLogout(@isDeleteToken)

PerpleSDK:naverRequestApi('@url')

-- @isShowWidget : 1(SDK unload 시 카페 위젯 보여주기) or 0(안 보여주기)
PerpleSDK:naverCafeShowWidgetWhenUnloadSdk(@isShowWidget)

PerpleSDK:naverCafeStopWidget()

bool ret = PerpleSDK:naverCafeIsShowGlink()
if ret == true then
    -- Naver Cafe SDK UI Opened
else
    -- Naver Cafe SDK UI Closed
end

-- @tapNumber : 0(Home) or 1(Notice) or 2(Event) or 3(Menu) or 4(Profile)
PerpleSDK:naverCafeStart(@tapNumber)

PerpleSDK:naverCafeStop()

PerpleSDK:naverCafePopBackStack()

PerpleSDK:naverCafeStartWrite(menuId, '@subject', '@text')

PerpleSDK:naverCafeStartImageWrite(menuId, '@subject', '@text', '@imageUrl')

PerpleSDK:naverCafeStartVideoWrite(menuId, '@subject', '@text', '@videoUrl')

PerpleSDK:naverCafeSyncGameUserId('@gameUserId')

-- @isUseVideoRecord : 1(동영상 녹화 사용) or 0(비사용)
PerpleSDK:naverCafeSetUseVideoRecord(@isUseVideoRecord)

PerpleSDK:naverCafeSetCallback(function(ret, info)
    if ret == 'start' then
        --onSdkStarted
    elseif ret == 'stop' then
        --onSdkStopped
    elseif ret == 'scheme' then
        --onClickAppSchemeBanner
    elseif ret == 'join' then
        --onJoined
    elseif ret == 'article' then
        --onPostedArticle
    elseif ret == 'comment' then
        --onPostedComment
    elseif ret == 'vote' then
        --onVoted
    elseif ret == 'screenshot' then
        --onScreenshotClick
    elseif ret == 'record' then
        --onRecordFinished
    elseif ret == 'error' then
    end
end)

PerpleSDK:googleLogin(function(ret, info)
    if ret == 'success' then
        -- info : {"id":"@id","name":"@name","photoUrl":"@photoUrl"}
    elseif ret == 'fail' then
    end
end)

PerpleSDK:googleShowAchievements(function(ret, info)
    elseif ret == 'success' then
    elseif ret == 'fail' then
        -- info : {"code":"@code", "msg":"@msg"}
        -- @code 가 '-1210' 일 경우 로그아웃한 것임
    end
end)

PerpleSDK:googleShowLeaderboards(function(ret, info)
    elseif ret == 'success' then
    elseif ret == 'fail' then
        -- info : {"code":"@code", "msg":"@msg"}
        -- @code 가 '-1210' 일 경우 로그아웃한 것임
    end
end)

PerpleSDK:googleShowQuests(function(ret, info)
    elseif ret == 'success' then
    elseif ret == 'fail' then
        -- info : {"code":"@code", "msg":"@msg"}
        -- @code 가 '-1210' 일 경우 로그아웃한 것임
    end
end)

-- @achievementId : 업적 아이디
-- @steps : 달성스텝, 0이면 모든 스텝을 한번에 달성
PerpleSDK:googleUpdateAchievements('@achievementId', '@steps', function(ret, info)
    elseif ret == 'success' then
    elseif ret == 'fail' then
    end
end)

-- @leaderboardId : 리더보드 아이디
-- @score : 점수
PerpleSDK:googleUpdateLeaderboards('@leaderboardId', '@score', function(ret, info)
    elseif ret == 'success' then
    elseif ret == 'fail' then
    end
end)

-- @questId : 퀘스트 이벤트 아이디
-- @count : 퀘스트 이벤트 달성 카운트
-- @reward : 보상 정보을 담은 JSON 포맷 문자열
PerpleSDK:googleUpdateQuests('@questId', '@count', function(ret, info)
    elseif ret == 'success' then
    elseif ret == 'fail' then
    elseif ret == 'complete' then
        -- info : '@reward'
    end
end)

-- Billing 초기화
-- SDK 초기화 후에 이 함수를 호출해야 하며 초기화 성공 시 'purchase' 콜백이 오며, 이 때 info 에 완료되지 않은 구매 리스트가 담겨온다.
-- @url : 영수증 검증 서버 주소, 끝에 / 붙여야 함 (예:http://platform.perplelab.com/)
PerpleSDK:billingSetup('@url', function(ret, info)
    if ret == 'purchase' then
        -- info : [{"orderId":"@orderId","payload":"@payload"},...]
    elseif ret == 'error' then
    end
end)

-- 구매 완료 성공 콜백을 받은 후 게임 서버에서 정상적으로 상품 지급을 한 다음 다시 이 함수를 호출해서 구매 프로세스를 완료시킴
-- 이 함수를 호출하면 구글 결제 인벤토리에서 해당 Purchase 를 Consume 처리함.
PerpleSDK:billingConfirm("['@orderId',...]")

-- 일반형 상품 구매
-- @sku : 상품 아이디
-- @payload : 영수증 검증에 필요한 부가 정보
PerpleSDK:billingPurchase('@sku', '@payload', function(ret, info)
    if ret == 'success' then
        -- info : {"orderId":"@orderId","payload":"@payload"}
    elseif ret == 'fail' then
    elseif ret == 'cancel' then
    end
end)

-- 구독형 상품 구매
-- @sku : 상품 아이디
-- @payload : 영수증 검증에 필요한 부가 정보
PerpleSDK:billingSubscription('@sku', '@payload', function(ret, info)
    if ret == 'success' then
        -- info : {"orderId":"@orderId","payload":"@payload"}
    elseif ret == 'fail' then
    elseif ret == 'cancel' then
    end
end)