package com.perplelab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestUpdateListener;
import com.google.android.gms.games.quest.Quests;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class PerpleGoogle implements ConnectionCallbacks, OnConnectionFailedListener, QuestUpdateListener {
    private static final String LOG_TAG = "PerpleSDK";

    private static Activity sMainActivity;
    private boolean mIsInit;
    private boolean mUseGoogleSignInApi;

    private GoogleApiClient mGoogleApiClient;
    private PerpleGoogleLoginCallback mLoginCallback;
    private PerpleSDKCallback mPlayServicesCallback;
    private PerpleGooglePlayServicesQuestsCallback mPlayServicesQuestsCallback;

    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final int RC_GOOGLE_ACHIEVEMENTS = 9002;
    private static final int RC_GOOGLE_LEADERBOARDS = 9003;
    private static final int RC_GOOGLE_QUESTS = 9004;
    private static final int RC_GOOGLE_SIGNIN_RESOLVE_ERROR = 9005;

    private static final int RC_GOGLEPLAYSERVICE_NOTAVAILABLE = 10001;

    private String mWebClientId;

    private boolean mRequestedSignIn;
    private boolean mRequestedShowAchievements;
    private boolean mRequestedShowLeaderboards;
    private boolean mRequestedShowQuests;
    private boolean mResolvingError;

    public PerpleGoogle(Activity activity) {
        sMainActivity = activity;
        mIsInit = false;
        mUseGoogleSignInApi = false;
        mRequestedSignIn = false;
        mRequestedShowAchievements = false;
        mRequestedShowLeaderboards = false;
        mRequestedShowQuests = false;
        mResolvingError = false;
    }

    public boolean init(String web_client_id, final PerpleGoogleLoginCallback callback) {
        mWebClientId = web_client_id;

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(web_client_id)
                .requestEmail()
                .build();

        if (sMainActivity instanceof FragmentActivity) {

            // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
            mGoogleApiClient = new GoogleApiClient.Builder(sMainActivity)
                .enableAutoManage((FragmentActivity)sMainActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

            mLoginCallback = callback;

        } else {
            // @error
            Log.e(LOG_TAG, "Main Activity must be FragmentActivity.");
            return false;
        }

        mUseGoogleSignInApi = true;
        mIsInit = true;

        return true;
    }

    public boolean init(String web_client_id, PerpleBuildGoogleApiClient buildClient, final PerpleGoogleLoginCallback callback) {
        mWebClientId = web_client_id;

        Builder builder = new GoogleApiClient.Builder(sMainActivity);

        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);

        buildClient.onBuild(builder);
        mGoogleApiClient = builder.build();

        mLoginCallback = callback;

        mUseGoogleSignInApi = false;
        mIsInit = true;

        return true;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void onStart() {
        if (mRequestedSignIn) {
            mGoogleApiClient.connect();
        }
    }

    public void onStop() {
        if (mRequestedSignIn) {
            mGoogleApiClient.disconnect();
        }
    }

    @SuppressWarnings("deprecation")
    public void onResume() {
        if (mIsInit) {
            int ret = GooglePlayServicesUtil.isGooglePlayServicesAvailable(sMainActivity);
            if (ret != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(ret, sMainActivity, RC_GOGLEPLAYSERVICE_NOTAVAILABLE).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mIsInit) {
            return;
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful
                GoogleSignInAccount account = result.getSignInAccount();
                if (mLoginCallback != null) {
                    mLoginCallback.onSuccess(account.getIdToken());
                }
            }
            /*
            else {
                if (resultCode == Activity.RESULT_CANCELED) {
                    if (mLoginCallback != null) {
                        mLoginCallback.onCancel();
                    }
                } else {
                    // Google Sign In failed
                    if (mLoginCallback != null) {
                        mLoginCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_LOGIN, "Google sing-in fail."));
                    }
                }
            }
            */
        } else if (requestCode == RC_GOOGLE_SIGNIN_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mLoginCallback.onCancel();
            } else {
                String info = "resultCode:" + String.valueOf(resultCode);
                mLoginCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_LOGIN, info));
            }
        } else if (requestCode == RC_GOGLEPLAYSERVICE_NOTAVAILABLE) {
            if (mLoginCallback != null) {
                mLoginCallback.onGooglePlayServicesNotAvailable(resultCode, data);
            }
        } else if (requestCode == RC_GOOGLE_ACHIEVEMENTS) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                if (mRequestedShowAchievements) {
                    mRequestedShowAchievements = false;
                    mPlayServicesCallback.onSuccess("");
                }
            } else if (resultCode == RC_GOGLEPLAYSERVICE_NOTAVAILABLE) { // 업적창에서 로그 아웃시 이쪽으로 10001 으로 들어온다.
                mGoogleApiClient.disconnect();
                mPlayServicesCallback.onFail("logout");
            } else {
                if (mRequestedShowAchievements) {
                    mGoogleApiClient.disconnect();
                    mRequestedShowAchievements = false;
                    mPlayServicesCallback.onFail(String.valueOf(resultCode));
                }
            }
        } else if (requestCode == RC_GOOGLE_LEADERBOARDS) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                if (mRequestedShowLeaderboards) {
                    mRequestedShowLeaderboards = false;
                    mPlayServicesCallback.onSuccess("");
                }
            } else {
                if (mRequestedShowLeaderboards) {
                    mGoogleApiClient.disconnect();
                    mRequestedShowLeaderboards = false;
                    mPlayServicesCallback.onFail(String.valueOf(resultCode));
                }
            }
        } else if (requestCode == RC_GOOGLE_QUESTS) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                if (mRequestedShowQuests) {
                    mRequestedShowQuests = false;
                    mPlayServicesCallback.onSuccess("");
                }
            } else {
                if (mRequestedShowQuests) {
                    mGoogleApiClient.disconnect();
                    mRequestedShowQuests = false;
                    mPlayServicesCallback.onFail(String.valueOf(resultCode));
                }
            }
        }
    }

    public void login() {
        if (!mIsInit) {
            // @error
            Log.e(LOG_TAG, "Google is not initialized.");
            mLoginCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        if (mUseGoogleSignInApi) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            sMainActivity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        } else {
            mRequestedSignIn = true;
            mGoogleApiClient.connect();
        }
    }

    public void logout() {
        if (mUseGoogleSignInApi) {
            // @todo
        } else {
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    public void showAchievements(PerpleSDKCallback callback) {
        if (isSignedIn()) {
            mPlayServicesCallback = callback;
            sMainActivity.startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), RC_GOOGLE_ACHIEVEMENTS);
        } else {
            if (mRequestedShowAchievements) {
                mRequestedShowAchievements = false;
                callback.onFail("Google not signed-in.");
            }
        }
    }

    public void showLeaderboards(PerpleSDKCallback callback) {
        if (isSignedIn()) {
            mPlayServicesCallback = callback;
            sMainActivity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), RC_GOOGLE_LEADERBOARDS);
        } else {
            if (mRequestedShowLeaderboards) {
                mRequestedShowLeaderboards = false;
                callback.onFail("Google not signed-in.");
            }
        }
    }

    public void showQuests(PerpleSDKCallback callback) {
        if (isSignedIn()) {
            mPlayServicesCallback = callback;
            sMainActivity.startActivityForResult(Games.Quests.getQuestsIntent(mGoogleApiClient, Quests.SELECT_ALL_QUESTS), RC_GOOGLE_QUESTS);
        } else {
            if (mRequestedShowQuests) {
                mRequestedShowQuests = false;
                callback.onFail("Google not signed-in.");
            }
        }
    }

    public void updateAchievements(String achievementId, int numSteps, PerpleSDKCallback callback) {
        if (isSignedIn()) {
            if (numSteps > 0) {
                Games.Achievements.setSteps(mGoogleApiClient, achievementId, numSteps);
                callback.onSuccess("");
            } else if (numSteps == 0) {
                Games.Achievements.unlock(mGoogleApiClient, achievementId);
                callback.onSuccess("");
            }
        } else {
            callback.onFail("Google not signed-in.");
        }
    }

    public void updateLeaderboards(String leaderboardId, int finalScore, PerpleSDKCallback callback) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardId, finalScore);
            callback.onSuccess("");
        } else {
            callback.onFail("Google not signed-in.");
        }
    }

    public void updateQuestEvents(String eventId, int incrementCount, PerpleGooglePlayServicesQuestsCallback callback) {
        mPlayServicesQuestsCallback = callback;
        if (isSignedIn()) {
            Games.Events.increment(mGoogleApiClient, eventId, incrementCount);
            mPlayServicesQuestsCallback.onSuccess();
        } else {
            mPlayServicesQuestsCallback.onFail("Google not signed-in.");
        }
    }

    public String addNotificationKey(
            String senderId, String userEmail, String registrationId, String idToken)
            throws IOException, JSONException {
        URL url = new URL("https://android.googleapis.com/gcm/googlenotification");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("project_id", senderId);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "add");
        data.put("notification_key_name", userEmail);
        data.put("registration_ids", new JSONArray(Arrays.asList(registrationId)));
        data.put("id_token", idToken);

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        InputStream is = con.getInputStream();
        @SuppressWarnings("resource")
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        return response.getString("notification_key");
    }

    public String removeNotificationKey(
            String senderId, String userEmail, String registrationId, String idToken)
            throws IOException, JSONException {
        URL url = new URL("https://android.googleapis.com/gcm/googlenotification");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("project_id", senderId);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "remove");
        data.put("notification_key_name", userEmail);
        data.put("registration_ids", new JSONArray(Arrays.asList(registrationId)));
        data.put("id_token", idToken);

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        InputStream is = con.getInputStream();
        @SuppressWarnings("resource")
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        return response.getString("notification_key");
    }

    // This snippet takes the simple approach of using the first returned Google account,
    // but you can pick any Google account on the device.
    private Account getAccount(String name) {
        Account[] accounts = AccountManager.get(sMainActivity).getAccountsByType("com.google");
        if (accounts.length == 0) {
            return null;
        }

        for (Account account : accounts) {
            if (account.name.equals(name)) {
                return account;
            }
        }

        return accounts[0];
    }

    private class GetIdTokenTask extends AsyncTask<String, Void, String> {
        private PerpleGoogleLoginCallback mCallback;
        private String mErrorMsg;

        public GetIdTokenTask(PerpleGoogleLoginCallback callback) {
            mCallback = callback;
            mErrorMsg = "";
        }

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String webClientId = params[1];

            String idToken = "";

            Account account = getAccount(accountName);
            if (account != null) {
                // Initialize the scope using the client ID you got from the Console.
                final String scope = "audience:server:client_id:" + webClientId;
                try {
                    idToken = GoogleAuthUtil.getToken(sMainActivity, account, scope);
                } catch (UserRecoverableAuthException e) {
                    e.printStackTrace();
                    mErrorMsg = e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    mErrorMsg = e.getMessage();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                    mErrorMsg = e.getMessage();
                }
            } else {
                mErrorMsg = "Google Account of " + accountName + " is invalid.";
            }

            return idToken;
        }

        @Override
        protected void onPostExecute(String idToken) {
            if (idToken.isEmpty()) {
                Log.e(LOG_TAG, "Error in getting idToken : " + mErrorMsg);
                String info = PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_LOGIN, mErrorMsg);
                mCallback.onFail(info);
            } else {
                Log.d(LOG_TAG, "Getting idToken success : " + idToken);
                mCallback.onSuccess(idToken);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        String accountName = Games.getCurrentAccountName(mGoogleApiClient);
        new GetIdTokenTask(mLoginCallback).execute(accountName, mWebClientId);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        if (mRequestedSignIn) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mResolvingError && result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(sMainActivity, RC_GOOGLE_SIGNIN_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            String info = "errorCode:" + result.getErrorCode() + ", errorMsg:" + result.getErrorMessage();
            mLoginCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_LOGIN, info));
        }
    }

    @Override
    public void onQuestCompleted(Quest quest) {
        // Claim the quest reward.
        Games.Quests.claim(mGoogleApiClient, quest.getQuestId(), quest.getCurrentMilestone().getMilestoneId());

        // Process the RewardData to provision a specific reward.
        String reward = new String(quest.getCurrentMilestone().getCompletionRewardData(), Charset.forName("UTF-8"));

        // Provision the reward; this is specific to your game. Your game
        // should also let the player know the quest was completed and
        // the reward was claimed
        if (mPlayServicesQuestsCallback != null) {
            mPlayServicesQuestsCallback.onComplete(reward);
        }
    }

    public JSONObject getPlayerProfile() {
        JSONObject outData = new JSONObject();

        Player player = Games.Players.getCurrentPlayer(mGoogleApiClient);
        if (player != null) {
            String id = player.getPlayerId();
            String name = player.getDisplayName();
            Uri photoUrl = player.getIconImageUri();
            String photoUrlScheme = "";
            if (photoUrl != null) {
                photoUrlScheme = photoUrl.getScheme();
            }

            try {
                outData.put("id", id);
                outData.put("name", name);
                if (player.hasIconImage() && (photoUrlScheme.equals("http") || photoUrlScheme.equals("https"))) {
                    outData.put("photoUrl", photoUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return outData;
    }
}
