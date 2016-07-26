package com.perplelab;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class PerpleFirebase {
    private static final String LOG_TAG = "PerpleSDK";

    private static Activity sMainActivity;
    private boolean mIsInit;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean mSignedIn;

    private PerpleSDKCallback mLogoutCallback;

    private static String mGCMSenderId;
    private static AtomicInteger mMsgId;

    public PerpleFirebase(Activity activity) {
        sMainActivity = activity;
        mIsInit = false;
        mSignedIn = false;
        mLogoutCallback = null;
    }

    public void init(String gcmSenderId) {
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String info = getUserProfile(user).toString();
                    Log.w(LOG_TAG, "Firebase onAuthStateChanged : Signed In - " + info);
                    mSignedIn = true;
                } else {
                    // User is signed out
                    Log.w(LOG_TAG, "Firebase onAuthStateChanged : Signed Out");
                    mSignedIn = false;

                    if (mLogoutCallback != null) {
                        mLogoutCallback.onSuccess("");
                        mLogoutCallback = null;
                    }
                }
            }
        };

        mGCMSenderId = gcmSenderId;
        mMsgId = new AtomicInteger();

        mIsInit = true;
    }

    public void onStart() {
        if (mIsInit) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    public void onStop() {
        if (mIsInit) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void autoLogin(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        if (mSignedIn) {
            String info = getLoginInfo(mAuth.getCurrentUser());
            Log.w(LOG_TAG, "Firebase autoLogin : success - " + info);
            callback.onSuccess(info);
        } else {
            String info = "";
            Log.w(LOG_TAG, "Firebase autoLogin : fail");
            callback.onFail(info);
        }
    }

    public void loginAnonymously(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        mAuth.signInAnonymously()
            .addOnCompleteListener(sMainActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.w(LOG_TAG, "Firebase loginAnonymously : fail - " + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        Log.w(LOG_TAG, "Firebase loginAnonymously : success - " + info);
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void loginEmail(String email, String password, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(sMainActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.w(LOG_TAG, "Firebase loginEmail : fail - " + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        Log.w(LOG_TAG, "Firebase loginEmail : success - " + info);
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void logout(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        if (!mSignedIn) {
            callback.onSuccess("");
            return;
        }

        mLogoutCallback = callback;
        mAuth.signOut();
    }

    public void createUserWithEmail(String email, String password, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(sMainActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.w(LOG_TAG, "Firebase createUserWithEmail : fail - " + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        Log.w(LOG_TAG, "Firebase createUserWithEmail : success - " + info);
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void deleteUser(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        // Important: To delete a user, the user must have signed in recently. See Re-authenticate a user.
        mAuth.getCurrentUser().delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.w(LOG_TAG, "Firebase deleteUser : success");
                        callback.onSuccess("");
                    } else {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.w(LOG_TAG, "Firebase deleteUser : fail - " + info);
                        callback.onFail(info);
                    }
                }
            });
    }

    public void signInWithCredential(AuthCredential credential, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(sMainActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.w(LOG_TAG, "Firebase signInWithCredential : fail - " + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        Log.w(LOG_TAG, "Firebase signInWithCredential : success - " + info);
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void linkWithCredential(AuthCredential credential, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        mAuth.getCurrentUser().linkWithCredential(credential)
            .addOnCompleteListener(sMainActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.w(LOG_TAG, "Firebase linkWithCredential : fail - " + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        Log.w(LOG_TAG, "Firebase linkWithCredential : success - " + info);
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void unlink(String providerId, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        mAuth.getCurrentUser().unlink(providerId)
            .addOnCompleteListener(sMainActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        // Auth provider unlinked from account
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.w(LOG_TAG, "Firebase unlink : fail - " + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        Log.w(LOG_TAG, "Firebase unlink : success - " + info);
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void reauthenticate(AuthCredential credential, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        // Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.w(LOG_TAG, "Firebase reauthenticate : success");
                    callback.onSuccess("");
                }
            });
    }

    public void updateProfile(String displayName, String photoUri) {
        if (!mIsInit) {
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .setPhotoUri(Uri.parse(photoUri))
            .build();

        mAuth.getCurrentUser().updateProfile(profileUpdates)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.w(LOG_TAG, "Firebase updateProfile : success");
                    } else {
                        Log.w(LOG_TAG, "Firebase updateProfile : fail");
                    }
                }
            });
    }

    public void updateEmail(String newEmail) {
        if (!mIsInit) {
            return;
        }

        // Important: To set a user's email address, the user must have signed in recently. See Re-authenticate a user.
        mAuth.getCurrentUser().updateEmail("user@example.com")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.w(LOG_TAG, "Firebase updateEmail : success");
                    } else {
                        Log.w(LOG_TAG, "Firebase updateEmail : fail");
                    }
                }
            });
    }

    public void updatePassword(String newPassword) {
        if (!mIsInit) {
            return;
        }

        // Important: To set a user's password, the user must have signed in recently. See Re-authenticate a user.
        mAuth.getCurrentUser().updatePassword(newPassword)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.w(LOG_TAG, "Firebase updatePassword : success");
                    } else {
                        Log.w(LOG_TAG, "Firebase updatePassword : fail");
                    }
                }
            });
    }

    public void sendPasswordResetEmail(String emailAddress) {
        if (!mIsInit) {
            return;
        }

        mAuth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.w(LOG_TAG, "Firebase sendPasswordResetEmail : success");
                    } else {
                        Log.w(LOG_TAG, "Firebase sendPasswordResetEmail : fail");
                    }
                }
            });
    }

    public static AuthCredential getGoogleCredential(String idToken) {
        return GoogleAuthProvider.getCredential(idToken, null);
    }

    public static AuthCredential getFacebookCredential(String token) {
        return FacebookAuthProvider.getCredential(token);
    }

    public static AuthCredential getEmailCredential(String email, String password) {
        return EmailAuthProvider.getCredential(email, password);
    }

    public String getFCMiid() {
        return FirebaseInstanceId.getInstance().getId();
    }

    public String getFCMtoken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public JSONObject getFCMPushToken() {
        String iid = getFCMiid();
        String token = getFCMtoken();

        JSONObject info = new JSONObject();
        try {
            info.put("iid", iid);
            info.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

    public void subscribeToTopic(String topic) {
        Log.d(LOG_TAG, "Firebase, Subscribed to news topic: " + topic);
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public void unsubscribeFromTopic(String topic) {
        Log.d(LOG_TAG, "Firebase, Unsubscribed to news topic: " + topic);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    public void sendUpstreamMessage(String data){
        if (!mIsInit) {
            return;
        }

        RemoteMessage.Builder build = new RemoteMessage.Builder(mGCMSenderId + "@gcm.googleapis.com");
        build.setMessageId(Integer.toString(mMsgId.incrementAndGet()));

        try {
            JSONObject jsonObj = new JSONObject(data);
            Iterator<String> it = jsonObj.keys();

            while (it.hasNext()) {
                String key = it.next();
                Object value = jsonObj.get(key);
                build.addData(key, (String)value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(build.build());
    }

    public void sendUpstreamMessage(String uniqueKey, String data) {
        if (!mIsInit) {
            return;
        }

        RemoteMessage.Builder build = new RemoteMessage.Builder(uniqueKey);
        build.setMessageId(Integer.toString(mMsgId.incrementAndGet()));

        try {
            JSONObject jsonObj = new JSONObject(data);
            Iterator<String> it = jsonObj.keys();

            while (it.hasNext()) {
                String key = it.next();
                Object value = jsonObj.get(key);
                build.addData(key, (String)value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(build.build());
    }

    public boolean isLinkedProvider(String info, String provider) {
        try {
            JSONObject jsonObj = new JSONObject(info);
            JSONObject prividerSpecificInfo = (JSONObject)jsonObj.get("prividerSpecificInfo");
            if (prividerSpecificInfo != null) {
                JSONArray data = (JSONArray) prividerSpecificInfo.get("data");
                int l = data.length();
                for (int i = 0; i < l; i ++) {
                    String providerId = ((JSONObject)(data.get(i))).get("providerId").toString();
                    if (providerId.equals(provider)) {
                        return true;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getLoginInfo(FirebaseUser user) {
        JSONObject userProfile = getUserProfile(user);
        JSONObject prividerSpecificInfo = getPrividerSpecificInfo(user);
        JSONObject pushToken = getFCMPushToken();

        JSONObject data = new JSONObject();
        try {
            data.put("userProfile", userProfile);
            data.put("prividerSpecificInfo", prividerSpecificInfo);
            data.put("pushToken", pushToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data.toString();
    }

    private static JSONObject getUserProfile(FirebaseUser user) {
        JSONObject outData = new JSONObject();

        if (user != null) {
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String providerId = user.getProviderId();

            JSONObject providers = new JSONObject();
            int i = 1;
            for (String provider : user.getProviders()) {
                String key = "provider-" + Integer.valueOf(i);
                try {
                    providers.put(key, provider);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                outData.put("uid", uid);
                outData.put("name", name);
                outData.put("email", email);
                outData.put("photoUrl", photoUrl);
                outData.put("providerId", providerId);
                outData.put("providers", providers);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return outData;
    }

    private static JSONObject getPrividerSpecificInfo(FirebaseUser user) {
        JSONObject outData = new JSONObject();

        if (user != null) {
            try {
                outData.put("uid", user.getUid());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            JSONArray arrayList = new JSONArray();
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String puid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();

                JSONObject outItem = new JSONObject();
                try {
                    outItem.put("providerId", providerId);
                    outItem.put("puid", puid);
                    outItem.put("name", name);
                    outItem.put("email", email);
                    outItem.put("photoUrl", photoUrl);

                    arrayList.put(outItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };

            try {
                outData.put("data",  arrayList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return outData;
    }
}
