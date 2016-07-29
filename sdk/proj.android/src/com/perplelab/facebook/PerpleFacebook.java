package com.perplelab.facebook;

import java.util.Arrays;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.GameRequestDialog.Result;
import com.perplelab.PerpleSDK;
import com.perplelab.PerpleSDKCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PerpleFacebook {
    private static final String LOG_TAG = "PerpleSDK Facebook";

    private static Activity sMainActivity;
    private boolean mIsInit;

    private CallbackManager mCallbackManager;

    public PerpleFacebook(Activity activity) {
        sMainActivity = activity;
    }

    public void init(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Initializing Facebook.");

        FacebookSdk.sdkInitialize(sMainActivity.getApplicationContext());
        AppEventsLogger.activateApp(sMainActivity.getApplication());

        mCallbackManager = CallbackManager.Factory.create();

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

        return AccessToken.getCurrentAccessToken();
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

    public void logout() {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            return;
        }

        LoginManager.getInstance().logOut();
    }

    public void sendRequest(String info, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        try {

            JSONObject obj = new JSONObject(info);
            String title = obj.getString("title");
            String message = obj.getString("message");
            String to = obj.getString("to");

            @SuppressWarnings("deprecation")
            GameRequestContent content = new GameRequestContent.Builder()
                .setTitle(title)
                .setMessage(message)
                .setTo(to)
                .build();

            GameRequestDialog dialog = new GameRequestDialog(sMainActivity);
            dialog.registerCallback(mCallbackManager, new FacebookCallback<GameRequestDialog.Result>() {
                @Override
                public void onCancel() {
                    callback.onFail("cancel");
                }
                @Override
                public void onError(FacebookException error) {
                    callback.onFail(PerpleSDK.getErrorInfoFromFacebookException(error));
                }
                @Override
                public void onSuccess(Result result) {
                    callback.onSuccess(result.getRequestId());
                }
            });

            dialog.show(content);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_REQUEST, PerpleSDK.ERROR_JSONEXCEPTION, e.toString()));
        }
    }

    public boolean isGrantedPermission(String permission) {
        Set<String> Permissions = AccessToken.getCurrentAccessToken().getPermissions();
        boolean status = Permissions.contains(permission);
        return status;
    }

    public void askPermission(String permission, final PerpleSDKCallback callback) {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onCancel() {
                        callback.onFail("cancel");
                    }
                    @Override
                    public void onError(FacebookException error) {
                        Log.e(LOG_TAG, "Facebook askPermission error - desc:" + error.toString());
                        callback.onFail(PerpleSDK.getErrorInfoFromFacebookException(error));
                    }
                    @Override
                    public void onSuccess(LoginResult result) {
                        callback.onSuccess(result.getAccessToken().getToken());
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
            "/me/friends",
            args,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Facebook friends - response:" + response.getJSONObject().toString());
                    }

                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_GRAPHAPI, error.getRequestResultBody().toString()));
                    } else {
                        callback.onSuccess(convertFriendsListFormat(response.getJSONObject()));
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
            "/me/invitable_friends",
            args,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Facebook invitable_friends - response:" + response.getJSONObject().toString());
                    }

                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_GRAPHAPI, error.getRequestResultBody().toString()));
                    } else {
                        callback.onSuccess(convertInvitableFriendsListFormat(response.getJSONObject()));
                    }
                }
            }
        ).executeAsync();
    }

    public void getPicture(final String facebookId, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Facebook is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_NOTINITIALIZED, "Facebook is not initialized."));
            return;
        }

        String userId = "/" + facebookId + "/";

        new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            userId + "picture",
            null,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Facebook picture - fid:" + facebookId + ", response:" + response.getJSONObject().toString());
                    }

                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FACEBOOK_GRAPHAPI, error.getRequestResultBody().toString()));
                    } else {
                        callback.onSuccess(response.getJSONObject().toString());
                    }
                }
            }
        ).executeAsync();
    }

    public JSONObject getProfileData() {
        Profile profile = getProfile();
        if (profile != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", profile.getId());
                obj.put("name", profile.getName());
                obj.put("photoUrl", profile.getProfilePictureUri(64, 64));
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String convertFriendsListFormat(JSONObject obj) {
        try {
            JSONArray outArray = new JSONArray();
            JSONArray inArray = obj.getJSONArray("data");
            for (int i = 0; i < inArray.length(); i++) {
                // parsing original json
                JSONObject friendObj = (JSONObject)inArray.get(i);
                String id = friendObj.getString("id");
                String name = friendObj.getString("name");

                // make new json
                JSONObject outObj = new JSONObject();
                outObj.put("id", id);
                outObj.put("name", name);
                outArray.put(outObj);
            }
            return outArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String convertInvitableFriendsListFormat(JSONObject obj) {
        try {
            JSONArray outArray = new JSONArray();
            JSONArray inArray = obj.getJSONArray("data");
            for (int i = 0; i < inArray.length(); i++) {
                // parsing original json
                JSONObject friendObj = (JSONObject)inArray.get(i);
                String id = friendObj.getString("id");
                String name = friendObj.getString("name");
                JSONObject pictureObj = (JSONObject)friendObj.get("picture");
                JSONObject pictureDataObj = (JSONObject)pictureObj.get("data");
                String photoUrl = pictureDataObj.getString("url");

                // make new json
                JSONObject outObj = new JSONObject();
                outObj.put("id", id);
                outObj.put("name", name);
                outObj.put("photoUrl", photoUrl);
                outArray.put(outObj);
            }
            return outArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}
