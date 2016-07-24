package com.perplelab.google;

import java.io.IOException;
import java.nio.charset.Charset;

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
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestUpdateListener;
import com.google.android.gms.games.quest.Quests;
import com.perplelab.PerpleSDK;
import com.perplelab.PerpleSDKCallback;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class PerpleGoogle implements ConnectionCallbacks, OnConnectionFailedListener, QuestUpdateListener {
    private static final String LOG_TAG = "PerpleSDK Google";

    private static Activity sMainActivity;
    private static String sWebClientId;

    private boolean mIsInit;
    private boolean mUseGoogleSignInApi;

    private GoogleApiClient mGoogleApiClient;

    private PerpleSDKCallback mLoginCallback;
    private PerpleSDKCallback mPlayServicesCallback;
    private PerpleSDKCallback mPlayServicesQuestsCallback;

    private boolean mRequestedSignIn;
    private boolean mResolvingError;

    public PerpleGoogle(Activity activity) {
        sMainActivity = activity;
    }

    public boolean init(String webClientId) {
        Log.d(LOG_TAG, "Initializing Google.");

        sWebClientId = webClientId;

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();

        if (sMainActivity instanceof FragmentActivity) {
            // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
            mGoogleApiClient = new GoogleApiClient.Builder(sMainActivity)
                .enableAutoManage((FragmentActivity)sMainActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        } else {
            Log.e(LOG_TAG, "Main Activity must be FragmentActivity.");
            return false;
        }

        mUseGoogleSignInApi = true;
        mIsInit = true;

        return true;
    }

    public boolean init(String webClientId, PerpleBuildGoogleApiClient buildClient) {
        Log.d(LOG_TAG, "Initializing Google.");

        sWebClientId = webClientId;

        Builder builder = new GoogleApiClient.Builder(sMainActivity);
        //builder.addApi(Games.API).addScope(Games.SCOPE_GAMES);

        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);

        buildClient.onBuild(builder);
        mGoogleApiClient = builder.build();

        mUseGoogleSignInApi = false;
        mIsInit = true;

        return true;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void onStart() {
        if (mRequestedSignIn && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void onStop() {
        if (mRequestedSignIn && mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @SuppressWarnings("deprecation")
    public void onResume() {
        if (mIsInit) {
            int ret = GooglePlayServicesUtil.isGooglePlayServicesAvailable(sMainActivity);
            if (ret != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(ret, sMainActivity, PerpleSDK.RC_GOGLEPLAYSERVICE_NOTAVAILABLE).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mIsInit) {
            return;
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == PerpleSDK.RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful
                GoogleSignInAccount account = result.getSignInAccount();
                if (mLoginCallback != null) {
                    mLoginCallback.onSuccess(account.getIdToken());
                } else {
                    Log.e(LOG_TAG, "Login callback is not set.");
                }
            }
            /*
            else {
                if (resultCode == Activity.RESULT_CANCELED) {
                    if (mLoginCallback != null) {
                        mLoginCallback.onFail("cancel");
                    } else {
                        Log.e(LOG_TAG, "Login callback is not set.");
                    }
                } else {
                    // Google Sign In failed
                    if (mLoginCallback != null) {
                        mLoginCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_LOGIN, result.toString()));
                    } else {
                        Log.e(LOG_TAG, "Login callback is not set.");
                    }
                }
            }
            */
        } else if (requestCode == PerpleSDK.RC_GOOGLE_SIGNIN_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (mLoginCallback != null) {
                    mLoginCallback.onFail("cancel");
                } else {
                    Log.e(LOG_TAG, "Login callback is not set.");
                }
            } else {
                if (mLoginCallback != null) {
                    String info = "ResultCode:" + String.valueOf(resultCode);
                    mLoginCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_LOGIN, info));
                } else {
                    Log.e(LOG_TAG, "Login callback is not set.");
                }
            }
        } else if (requestCode == PerpleSDK.RC_GOGLEPLAYSERVICE_NOTAVAILABLE) {
            if (mLoginCallback != null) {
                String info = "ResultCode:" + String.valueOf(resultCode);
                mLoginCallback.onFail(info);PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTAVAILABLEPLAYSERVICES, info);
            } else {
                Log.e(LOG_TAG, "Login callback is not set.");
            }
        } else if (requestCode == PerpleSDK.RC_GOOGLE_ACHIEVEMENTS) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                if (mPlayServicesCallback != null) {
                    mPlayServicesCallback.onSuccess("");
                } else {
                    Log.e(LOG_TAG, "Play services callback is not set.");
                }
            } else {
                mGoogleApiClient.disconnect();
                if (mPlayServicesCallback != null) {
                    String info = "ResultCode:" + String.valueOf(resultCode);
                    mPlayServicesCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_ACHIEVEMENTS, info));
                } else {
                    Log.e(LOG_TAG, "Play services callback is not set.");
                }
            }
        } else if (requestCode == PerpleSDK.RC_GOOGLE_LEADERBOARDS) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                if (mPlayServicesCallback != null) {
                    mPlayServicesCallback.onSuccess("");
                } else {
                    Log.e(LOG_TAG, "Play services callback is not set.");
                }
            } else {
                mGoogleApiClient.disconnect();
                if (mPlayServicesCallback != null) {
                    String info = "ResultCode:" + String.valueOf(resultCode);
                    mPlayServicesCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_LEADERBOARDS, info));
                } else {
                    Log.e(LOG_TAG, "Play services callback is not set.");
                }
            }
        } else if (requestCode == PerpleSDK.RC_GOOGLE_QUESTS) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                if (mPlayServicesCallback != null) {
                    mPlayServicesCallback.onSuccess("");
                } else {
                    Log.e(LOG_TAG, "Play services callback is not set.");
                }
            } else {
                mGoogleApiClient.disconnect();
                if (mPlayServicesCallback != null) {
                    String info = "ResultCode:" + String.valueOf(resultCode);
                    mPlayServicesCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_QUESTS, info));
                } else {
                    Log.e(LOG_TAG, "Play services callback is not set.");
                }
            }
        }
    }

    public void login(PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Google is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTINITIALIZED, "Google is not initialized."));
            return;
        }

        mLoginCallback = callback;

        if (mUseGoogleSignInApi) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            sMainActivity.startActivityForResult(signInIntent, PerpleSDK.RC_GOOGLE_SIGN_IN);
        } else {
            mRequestedSignIn = true;
            mGoogleApiClient.connect();
        }
    }

    public void logout() {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Google is not initialized.");
            return;
        }

        if (mUseGoogleSignInApi) {
            // @todo
        } else {
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    public void showAchievements(PerpleSDKCallback callback) {
        if (isSignedIn()) {
            mPlayServicesCallback = callback;
            sMainActivity.startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), PerpleSDK.RC_GOOGLE_ACHIEVEMENTS);
        } else {
            Log.e(LOG_TAG, "Google signe-in is not available.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTSIGNEDIN, "Google signe-in is not available."));
        }
    }

    public void showLeaderboards(PerpleSDKCallback callback) {
        if (isSignedIn()) {
            mPlayServicesCallback = callback;
            sMainActivity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), PerpleSDK.RC_GOOGLE_LEADERBOARDS);
        } else {
            Log.e(LOG_TAG, "Google signe-in is not available.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTSIGNEDIN, "Google signe-in is not available."));
        }
    }

    public void showQuests(PerpleSDKCallback callback) {
        if (isSignedIn()) {
            mPlayServicesCallback = callback;
            sMainActivity.startActivityForResult(Games.Quests.getQuestsIntent(mGoogleApiClient, Quests.SELECT_ALL_QUESTS), PerpleSDK.RC_GOOGLE_QUESTS);
        } else {
            Log.e(LOG_TAG, "Google signe-in is not available.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTSIGNEDIN, "Google signe-in is not available."));
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
            Log.e(LOG_TAG, "Google signe-in is not available.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTSIGNEDIN, "Google signe-in is not available."));
        }
    }

    public void updateLeaderboards(String leaderboardId, int finalScore, PerpleSDKCallback callback) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardId, finalScore);
            callback.onSuccess("");
        } else {
            Log.e(LOG_TAG, "Google signe-in is not available.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTSIGNEDIN, "Google signe-in is not available."));
        }
    }

    public void updateQuestEvents(String eventId, int incrementCount, PerpleSDKCallback callback) {
        mPlayServicesQuestsCallback = callback;

        if (isSignedIn()) {
            Games.Events.increment(mGoogleApiClient, eventId, incrementCount);
            mPlayServicesQuestsCallback.onSuccess("success");
        } else {
            Log.e(LOG_TAG, "Google signe-in is not available.");
            mPlayServicesQuestsCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_NOTSIGNEDIN, "Google signe-in is not available."));
        }
    }

    // This snippet takes the simple approach of using the first returned Google account,
    // but you can pick any Google account on the device.
    private Account getAccount(String name) {
        Account[] accounts = AccountManager.get(sMainActivity).getAccountsByType("com.google");
        if (accounts.length == 0) {
            return null;
        }

        for (Account account : accounts) {
            if (name.equals(account.name)) {
                return account;
            }
        }

        return accounts[0];
    }

    private class GetIdTokenTask extends AsyncTask<String, Void, String> {
        private PerpleSDKCallback mCallback;
        private String mMessage;

        public GetIdTokenTask(PerpleSDKCallback callback) {
            mCallback = callback;
            mMessage = "";
        }

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String idToken = "";
            Account account = getAccount(accountName);
            if (account != null) {
                // Initialize the scope using the client ID you got from the Console.
                final String scope = "audience:server:client_id:" + sWebClientId;
                try {
                    idToken = GoogleAuthUtil.getToken(sMainActivity, account, scope);
                } catch (UserRecoverableAuthException e) {
                    e.printStackTrace();
                    mMessage = e.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    mMessage = e.toString();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                    mMessage = e.toString();
                }
            } else {
                mMessage = "Google Account(" + accountName + ") is invalid.";
            }
            return idToken;
        }

        @Override
        protected void onPostExecute(String idToken) {
            if (mCallback != null) {
                if (idToken.isEmpty()) {
                    Log.e(LOG_TAG, "Getting idToken fail - msg:" + mMessage);
                    mCallback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_GOOGLE_LOGIN, mMessage));
                } else {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Getting idToken success - idToken:" + idToken);
                    }
                    mCallback.onSuccess(idToken);
                }
            } else {
                Log.e(LOG_TAG, "Login callback is not set.");
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        String accountName = Games.getCurrentAccountName(mGoogleApiClient);
        new GetIdTokenTask(mLoginCallback).execute(accountName);
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
                result.startResolutionForResult(sMainActivity, PerpleSDK.RC_GOOGLE_SIGNIN_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            if (mLoginCallback != null) {
                mLoginCallback.onFail(PerpleSDK.getErrorInfo(String.valueOf(result.getErrorCode()), result.getErrorMessage()));
            } else {
                Log.e(LOG_TAG, "Login callback is not set.");
            }
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
            mPlayServicesQuestsCallback.onSuccess(reward);
        } else {
            Log.e(LOG_TAG, "Quests callback is not set.");
        }
    }
}
