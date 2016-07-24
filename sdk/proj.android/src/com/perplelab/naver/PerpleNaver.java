package com.perplelab.naver;

import org.json.JSONException;
import org.json.JSONObject;

import com.naver.glink.android.sdk.Glink;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.perplelab.PerpleSDK;
import com.perplelab.PerpleSDKCallback;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class PerpleNaver {
    private static final String LOG_TAG = "PerpleSDK Naver";

    public static final int CAFE_HOME = 0;
    public static final int CAFE_NOTICE = 1;
    public static final int CAFE_EVENT = 2;
    public static final int CAFE_MENU = 3;
    public static final int CAFE_PROFILE = 4;

    private static Activity sMainActivity;
    private boolean mIsInitLogin;
    private boolean mUseCafe;

    private OAuthLogin mOAuthLoginModule;
    private String mAccessToken;

    public PerpleNaver(Activity activity) {
        sMainActivity = activity;
    }

    public void init(String clientId, String clientSecret, String clientName, boolean isDebug) {
        Log.d(LOG_TAG, "Initializing Naver.");

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(sMainActivity, clientId, clientSecret, clientName);

        if (isDebug) {
            OAuthLoginDefine.DEVELOPER_VERSION = true;
        }

        mIsInitLogin = true;
    }

    public void initCafe(String clientId, String clientSecret, int cafeId) {
        if (cafeId > 0) {
            Log.d(LOG_TAG, "Initializing Naver CafeSDK.");

            // "네아로 개발자 센터"에서 받은 정보로 SDK를 초기화 합니다.
            // Glink의 다른 메소드를 호출하기 전에 반드시 초기화를 먼저해야 합니다.
            // 개발자 센터 주소: https://nid.naver.com/devcenter/main.nhn
            Glink.init(clientId, clientSecret, cafeId);
            mUseCafe = true;
        }
    }

    public void login(final PerpleSDKCallback callback) {
        if (!mIsInitLogin) {
            Log.e(LOG_TAG, "Naver is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_NAVER_NOTINITIALIZED, "Naver is not initialized."));
            return;
        }

        mOAuthLoginModule.startOauthLoginActivity(sMainActivity, new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    String accessToken = mOAuthLoginModule.getAccessToken(sMainActivity);
                    String refreshToken = mOAuthLoginModule.getRefreshToken(sMainActivity);
                    long expiresAt = mOAuthLoginModule.getExpiresAt(sMainActivity);
                    String tokenType = mOAuthLoginModule.getTokenType(sMainActivity);

                    mAccessToken = accessToken;

                    try {
                        JSONObject info = new JSONObject();
                        info.put("accessToken", accessToken);
                        info.put("refreshToken", refreshToken);
                        info.put("expiresAt", String.valueOf(expiresAt));
                        info.put("tokenType", tokenType);

                        callback.onSuccess(info.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();

                        callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_JSONEXCEPTION, e.toString()));
                    }

                 } else {
                    String code = mOAuthLoginModule.getLastErrorCode(sMainActivity).getCode();
                    String msg = mOAuthLoginModule.getLastErrorDesc(sMainActivity);

                    callback.onFail(PerpleSDK.getErrorInfo(code, msg));
                 }
            }
        });
    }

    public void logout() {
        if (!mIsInitLogin) {
            Log.e(LOG_TAG, "Naver is not initialized.");
            return;
        }

        mOAuthLoginModule.logout(sMainActivity);

        // @todo, logout callback 처리
    }

    public void logoutAndDeleteToken() {
        if (!mIsInitLogin) {
            Log.e(LOG_TAG, "Naver is not initialized.");
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(sMainActivity);
                if (!isSuccessDeleteToken) {
                    // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                    // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                    String code = mOAuthLoginModule.getLastErrorCode(sMainActivity).getCode();
                    String desc = mOAuthLoginModule.getLastErrorDesc(sMainActivity);
                    Log.e(LOG_TAG, "Naver, logoutAndDeleteToken fail - code:" + code + ", desc:" + desc);
                }

                // @todo, logout callback 처리
            }
        });
    }

    public void requestApi(final String url) {
        if (!mIsInitLogin) {
            Log.e(LOG_TAG, "Naver is not initialized.");
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mOAuthLoginModule.requestApi(sMainActivity, mAccessToken, url);
            }
        });
    }

    public boolean cafeIsShowGlink() {
        boolean ret = false;
        if (mUseCafe) {
            // 네이버 카페 SDK 화면이 열려 있는지 확인한다.
            // 반환값이 true이면 열려 있는 상태고, 반환값이 false이면 열려 있지 않은 상태다.
            ret = Glink.isShowGlink(sMainActivity);
        }
        return ret;
    }

    public void cafeStart(int tapNumber) {
        if (mUseCafe) {
            switch (tapNumber) {
            case CAFE_NOTICE:
                // 공지 사항 탭으로 네이버 카페 SDK 화면을 연다.
                Glink.startNotice(sMainActivity);
                break;
            case CAFE_EVENT:
                // 이벤트 탭으로 네이버 카페 SDK를 시작한다.
                Glink.startEvent(sMainActivity);
                break;
            case CAFE_MENU:
                // 게시판 탭으로 네이버 카페 SDK 화면을 연다.
                Glink.startMenu(sMainActivity);
                break;
            case CAFE_PROFILE:
                // 프로필 탭으로 네이버 카페 SDK 화면을 연다.
                Glink.startProfile(sMainActivity);
                break;
            default:
                // 홈 탭으로 네이버 카페 SDK 화면을 연다.
                Glink.startHome(sMainActivity);
                break;
            }
        }
    }

    public void cafeStop() {
        if (mUseCafe) {
            // 종료하기
            Glink.stop(sMainActivity);
        }
    }

    public void cafePopBackStack() {
        if (mUseCafe) {
            // 네이버 카페 SDK 화면을 하나씩 닫는다.
            Glink.popBackStack(sMainActivity);
        }
    }

    public void cafeStartWrite(int menuId, String subject, String text) {
        if (mUseCafe) {
            // menuId : 0이면 메뉴를 선택하지 않는다.
            // subject : 기본 제목을 넣어서 글쓰기 화면을 시작합니다.
            // text : 기본 본문을 넣어서 글쓰기 화면을 시작합니다.
            Glink.startWrite(sMainActivity, menuId, subject, text);
        }
    }

    public void cafeStartImageWrite(int menuId, String subject, String text, String imageUri) {
        if (mUseCafe) {
            // menuId : 0이면 메뉴를 선택하지 않는다.
            // subject : 기본 제목을 넣어서 글쓰기 화면을 시작합니다.
            // text : 기본 본문을 넣어서 글쓰기 화면을 시작합니다.
            // imageUri : 이미지 경로는 URI 형식으로 넣어주시면 됩니다.
            Glink.startImageWrite(sMainActivity, menuId, subject, text, imageUri);
        }
    }

    public void cafeStartVideoWrite(int menuId, String subject, String text, String videoUri) {
        if (mUseCafe) {
            // menuId : 0이면 메뉴를 선택하지 않는다.
            // subject : 기본 제목을 넣어서 글쓰기 화면을 시작합니다.
            // text : 기본 본문을 넣어서 글쓰기 화면을 시작합니다.
            // videoUri : 동영상 파일의 경로는 URI 형식으로 넣어주시면 됩니다.
            Glink.startVideoWrite(sMainActivity, menuId, subject, text, videoUri);
        }
    }

    public void cafeSyncGameUserId(String gameUserId) {
        if (mUseCafe) {
            // 게임 아이디와 카페 아이디를 매핑합니다.
            Glink.syncGameUserId(sMainActivity, gameUserId);
        }
    }

    public void cafeSetUseVideoRecord(boolean isSetUseVideoRecord) {
        if (mUseCafe) {
            Glink.setUseVideoRecord(sMainActivity, isSetUseVideoRecord);
        }
    }

    public void cafeSetCallback(final PerpleNaverCafeCallback callback) {
        if (mUseCafe) {
            // SDK 시작 리스너 설정.
            Glink.setOnSdkStartedListener(new Glink.OnSdkStartedListener() {
                @Override
                public void onSdkStarted() {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onSdkStarted");
                    }
                    callback.onSdkStarted();
                }
            });
            // SDK 종료 리스너 설정.
            Glink.setOnSdkStoppedListener(new Glink.OnSdkStoppedListener() {
                @Override
                public void onSdkStopped() {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onSdkStopped");
                    }
                    callback.onSdkStopped();
                }
            });
            // 앱스킴 터치 리스너 설정.
            Glink.setOnClickAppSchemeBannerListener(new Glink.OnClickAppSchemeBannerListener() {
                @Override
                public void onClickAppSchemeBanner(String appScheme) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onClickAppSchemeBanner - appScheme:" + appScheme);
                    }
                    // 카페 관리에서 설정한 appScheme 문자열을 SDK에서 넘겨줍니다.
                    // 각 appScheme 처리를 이곳에서 하시면 됩니다.
                    callback.onClickAppSchemeBanner(appScheme);
                }
            });
            // 카페 가입 리스너를 설정.
            Glink.setOnJoinedListener(new Glink.OnJoinedListener() {
                @Override
                public void onJoined() {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onJoined");
                    }
                    callback.onJoined();
                }
            });
            /** 게시글 등록 리스너를 설정.
             * @param menuId 게시글이 등록된 menuId
             * @param imageCount 첨부한 image 갯수
             * @param videoCount 첨부한 video 갯수
             **/
            Glink.setOnPostedArticleListener(new Glink.OnPostedArticleListener() {
                @Override public void onPostedArticle(int menuId, int imageCount, int videoCount) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onPostedArticle - menuId:" + String.valueOf(menuId) +
                                ", imageCount:" + String.valueOf(imageCount) +
                                ", videoCount:" + String.valueOf(videoCount));
                    }

                    JSONObject info = new JSONObject();
                    try {
                        info.put("menuId", String.valueOf(menuId));
                        info.put("imageCount", String.valueOf(imageCount));
                        info.put("videoCount", String.valueOf(videoCount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callback.onPostedArticle(info.toString());
                }
            });
            // 댓글 등록 리스너를 설정.
            Glink.setOnPostedCommentListener(new Glink.OnPostedCommentListener() {
                @Override
                public void onPostedComment(int articleId) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onPostedComment - articleId:" + String.valueOf(articleId));
                    }
                    callback.onPostedComment(articleId);
                }
            });
            // 투표 완료 리스너를 설정.
            Glink.setOnVotedListener(new Glink.OnVotedListener() {
                @Override
                public void onVoted(int articleId) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onVoted - articleId:" + String.valueOf(articleId));
                    }
                    callback.onVoted(articleId);
                }
            });
            //위젯 스크린샷 버튼 클릭 리스너 설정.
            Glink.setOnWidgetScreenshotClickListener(new Glink.OnWidgetScreenshotClickListener() {
                @Override
                public void onScreenshotClick() {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onScreenshotClick");
                    }
                    callback.onScreenshotClick();
                }
            });
            //동영상 녹화 완료 리스너 설정.
            Glink.setOnRecordFinishListener(new Glink.OnRecordFinishListener() {
                @Override
                public void onRecordFinished(String uri) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Naver CafeSDK, onRecordFinished - uri:" + uri);
                    }
                    callback.onRecordFinished(uri);
                }
            });
        } else {
            callback.onError(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_NAVER_CAFENOTINITIALIZED, "Naver CafeSDK is not initialized."));
        }
    }
}
