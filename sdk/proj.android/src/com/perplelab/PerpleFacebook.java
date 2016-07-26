package com.perplelab;

import java.util.Arrays;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.GameRequestDialog.Result;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PerpleFacebook {
    private static final String LOG_TAG = "PerpleSDK";

    private static Activity sMainActivity;
    private boolean mIsInit;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    AccessToken mAccessToken;
    private GameRequestDialog mRequestDialog;

    private PerpleSDKCallback mRequestGameCallback;

    public PerpleFacebook(Activity activity) {
        sMainActivity = activity;
        mIsInit = false;
    }

    public void init(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(sMainActivity.getApplicationContext());

        AppEventsLogger.activateApp(sMainActivity.getApplication());

        mCallbackManager = CallbackManager.Factory.create();

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                AccessToken oldAccessToken,
                AccessToken currentAccessToken) {
                mAccessToken = currentAccessToken;
                // @todo
            }
        };

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                Profile oldProfile,
                Profile currentProfile) {
                // @todo
            }
        };

        mRequestDialog = new GameRequestDialog(sMainActivity);
        mRequestDialog.registerCallback(mCallbackManager, new FacebookCallback<GameRequestDialog.Result>() {
            @Override
            public void onCancel() {
                if (mRequestGameCallback != null) {
                    mRequestGameCallback.onFail("cancel");
                }
            }
            @Override
            public void onError(FacebookException error) {
                if (mRequestGameCallback != null) {
                    mRequestGameCallback.onFail(error.toString());
                }
            }
            @Override
            public void onSuccess(Result result) {
                if (mRequestGameCallback != null) {
                    String id = result.getRequestId();
                    JSONObject info = new JSONObject();
                    try {
                        info.put("requestId", id);
                        mRequestGameCallback.onSuccess(info.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mRequestGameCallback.onFail(e.toString());
                    }
                }
            }
        });

        mIsInit = true;
    }

    public void onResume() {
        if (mIsInit) {
            AppEventsLogger.activateApp(sMainActivity.getApplication());
        }
    }

    @SuppressWarnings("deprecation")
    public void onPause() {
        if (mIsInit) {
            AppEventsLogger.deactivateApp(sMainActivity);
        }
    }

    public void onDestroy() {
        if (mIsInit) {
            mAccessTokenTracker.stopTracking();
            mProfileTracker.stopTracking();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mIsInit) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public AccessToken getAccessToken() {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            return null;
        }

        return mAccessToken;
    }

    public Profile getProfile() {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            return null;
        }

        return Profile.getCurrentProfile();
    }

    public void login(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onCancel() {
                        callback.onFail("cancel");
                    }
                    @Override
                    public void onError(FacebookException error) {
                        Log.e(LOG_TAG, "Facebook login error - desc:" + error.toString());
                        callback.onFail(PerpleSDK.getErrorInfoFromFacebookException(error));
                    }
                    @Override
                    public void onSuccess(LoginResult result) {
                        callback.onSuccess(result.getAccessToken().getToken());
                    }
        });

        LoginManager.getInstance().logInWithReadPermissions(sMainActivity, Arrays.asList("public_profile", "email", "user_friends"));
    }

    public void sendRequest(String info, PerpleSDKCallback callback) {
        mRequestGameCallback = callback;

        JSONObject jsonInfo;
        String title = "";
        String message = "";
        String to = "";
        try {
            jsonInfo = new JSONObject(info);
            title = jsonInfo.getString("title");
            message = jsonInfo.getString("message");
            to = jsonInfo.getString("to");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        @SuppressWarnings("deprecation")
        GameRequestContent content = new GameRequestContent.Builder()
            .setTitle(title)
            .setMessage(message)
            .setTo(to)
            .build();

        mRequestDialog.show(content);
    }

    public boolean isGrantedPermission(String permission) {
        Set<String> Permissions = AccessToken.getCurrentAccessToken().getPermissions();
        boolean status = Permissions.contains(permission);
        return status;
    }

    public void askPermission(String permission, final PerpleSDKCallback callback) {
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onCancel() {
                callback.onFail("cancel");
            }
            @Override
            public void onError(FacebookException e) {
                String info = PerpleSDK.getErrorInfoFromFacebookException(e);
                callback.onFail(info);
            }
            @Override
            public void onSuccess(LoginResult arg0) {
                callback.onSuccess("");
            }
        });

        LoginManager.getInstance().logInWithReadPermissions(sMainActivity, Arrays.asList(permission));
    }

    public void getFriends(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        Bundle args = new Bundle();
        args.putInt("limit", 5000);
        new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            //myFacebookId + "friends",
            "/me/friends",
            args,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    Log.w(LOG_TAG, "Facebook friends - " + response.toString());
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        String info = PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_REQUESTERROR, error.getRequestResultBody().toString());
                        callback.onFail(info);
                    } else {
                        JSONObject outData = convertFriendsListFormat(response.getJSONObject());
                        callback.onSuccess(outData.toString());
                    }
                }
            }
        ).executeAsync();
    }

    public void getInvitableFriends(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        Bundle args = new Bundle();
        args.putInt("limit", 5000);
        new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            //myFacebookId + "invitable_friends",
            "/me/invitable_friends",
            args,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    Log.w(LOG_TAG, "Facebook invitable_friends - " + response.toString());
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        String info = PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_REQUESTERROR, error.getRequestResultBody().toString());
                        callback.onFail(info);
                    } else {
                        JSONObject outData = convertInvitableFriendsListFormat(response.getJSONObject());
                        callback.onSuccess(outData.toString());
                    }
                }
            }
        ).executeAsync();
    }

    public void getPictureUrl(final String facebookId, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        String facebookUserId = "/" + facebookId + "/";

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                facebookUserId + "picture",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.w(LOG_TAG, "Facebook picture info - fid:" + facebookId + ", response:" + response.toString());
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            String info = PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_REQUESTERROR, error.getRequestResultBody().toString());
                            callback.onFail(info);
                        } else {
                            String info = response.getJSONObject().toString();
                            callback.onSuccess(info);
                        }
                    }
                }
            ).executeAsync();
    }

    private static JSONObject convertFriendsListFormat(JSONObject inData) {
        JSONObject outData = new JSONObject();
        try {
            JSONObject paging = inData.getJSONObject("paging");
            outData.put("paging", paging);

            JSONArray ids = new JSONArray();
            outData.put("ids", ids);
            JSONArray list = inData.getJSONArray("data");
            for (int i = 0; i < list.length(); i++) {
                JSONObject data = (JSONObject)list.get(i);
                String id = data.getString("id");
                ids.put(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return outData;
    }

    private static JSONObject convertInvitableFriendsListFormat(JSONObject inData) {
        JSONObject outData = new JSONObject();
        try {
            JSONObject paging = inData.getJSONObject("paging");
            outData.put("paging", paging);

            JSONArray friends = new JSONArray();
            outData.put("friends", friends);
            JSONArray list = inData.getJSONArray("data");
            for (int i = 0; i < list.length(); i++) {
                JSONObject data = (JSONObject)list.get(i);
                String id = data.getString("id");
                String name = data.getString("name");
                JSONObject picture = (JSONObject)data.get("picture");
                JSONObject pictureData = (JSONObject)picture.get("data");
                String photoUrl = pictureData.getString("url");

                JSONObject outItem = new JSONObject();
                outItem.put("id", id);
                outItem.put("name", name);
                outItem.put("photoUrl", photoUrl);
                friends.put(outItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return outData;
    }
}
