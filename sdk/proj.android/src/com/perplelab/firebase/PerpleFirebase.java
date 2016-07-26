package com.perplelab.firebase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
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
import com.perplelab.PerpleSDK;
import com.perplelab.PerpleSDKCallback;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class PerpleFirebase {
    private static final String LOG_TAG = "PerpleSDK Firebase";

    private static Activity sMainActivity;
    private static String mGCMSenderId;
    private static AtomicInteger sMsgId;

    private boolean mIsInit;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean mSignedIn;

    private PerpleSDKCallback mLogoutCallback;

    public PerpleFirebase(Activity activity) {
        sMainActivity = activity;
    }

    public void init(String gcmSenderId) {
        Log.d(LOG_TAG, "Initializing Firebase.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (PerpleSDK.IsDebug) {
                    Log.d(LOG_TAG, "Firebase onAuthStateChanged - user:" + getUserProfile(user).toString());
                }

                if (user != null) {
                    // User is signed in
                    mSignedIn = true;
                } else {
                    // User is signed out
                    mSignedIn = false;

                    // Custom logout callback
                    if (mLogoutCallback != null) {
                        mLogoutCallback.onSuccess("");
                        mLogoutCallback = null;
                    }
                }
            }
        };

        mGCMSenderId = gcmSenderId;
        sMsgId = new AtomicInteger();

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
            Log.e(LOG_TAG, "Firebase is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        if (mSignedIn) {
            String info = getLoginInfo(mAuth.getCurrentUser());
            if (PerpleSDK.IsDebug) {
                Log.d(LOG_TAG, "Firebase autoLogin success - info:" + info);
            }
            callback.onSuccess(info);
        } else {
            if (PerpleSDK.IsDebug) {
                Log.d(LOG_TAG, "Firebase autoLogin fail");
            }
            callback.onFail("");
        }
    }

    public void loginAnonymously(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
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
                        Log.e(LOG_TAG, "Firebase loginAnonymously fail - info:" + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase loginAnonymously success - info:" + info);
                        }
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void loginEmail(String email, String password, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
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
                        Log.e(LOG_TAG, "Firebase loginEmail fail - info:" + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase loginEmail success - info:" + info);
                        }
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void logout(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
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
            Log.e(LOG_TAG, "Firebase is not initialized.");
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
                        Log.e(LOG_TAG, "Firebase createUserWithEmail fail - info:" + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase createUserWithEmail success - info:" + info);
                        }
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void deleteUser(final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        // Important: To delete a user, the user must have signed in recently. See Re-authenticate a user.
        mAuth.getCurrentUser().delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase deleteUser success");
                        }
                        callback.onSuccess("");
                    } else {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.e(LOG_TAG, "Firebase deleteUser fail - info:" + info);
                        callback.onFail(info);
                    }
                }
            });
    }

    public void signInWithCredential(AuthCredential credential, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
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
                        Log.e(LOG_TAG, "Firebase signInWithCredential fail - info:" + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase signInWithCredential success - info:" + info);
                        }
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void linkWithCredential(AuthCredential credential, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
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
                        Log.e(LOG_TAG, "Firebase linkWithCredential fail - info:" + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase linkWithCredential success - info:" + info);
                        }
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void unlink(String providerId, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
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
                        Log.e(LOG_TAG, "Firebase unlink fail - info:" + info);
                        callback.onFail(info);
                    } else {
                        String info = getLoginInfo(mAuth.getCurrentUser());
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase unlink success - info:" + info);
                        }
                        callback.onSuccess(info);
                    }
                }
            });
    }

    public void reauthenticate(AuthCredential credential, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        // Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (PerpleSDK.IsDebug) {
                        Log.d(LOG_TAG, "Firebase reauthenticate success");
                    }
                    callback.onSuccess("");
                }
            });
    }

    public void updateProfile(String displayName, String photoUri, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
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
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase updateProfile success");
                        }
                        callback.onSuccess("");
                    } else {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.e(LOG_TAG, "Firebase updateProfile fail - info:" + info);
                        callback.onFail(info);
                    }
                }
            });
    }

    public void updateEmail(String newEmail, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        // Important: To set a user's email address, the user must have signed in recently. See Re-authenticate a user.
        mAuth.getCurrentUser().updateEmail("user@example.com")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase updateEmail success");
                        }
                        callback.onSuccess("");
                    } else {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.e(LOG_TAG, "Firebase updateEmail fail - info:" + info);
                        callback.onFail(info);
                    }
                }
            });
    }

    public void updatePassword(String newPassword, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        // Important: To set a user's password, the user must have signed in recently. See Re-authenticate a user.
        mAuth.getCurrentUser().updatePassword(newPassword)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase updatePassword success");
                        }
                        callback.onSuccess("");
                    } else {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.e(LOG_TAG, "Firebase updatePassword fail - info:" + info);
                        callback.onFail(info);
                    }
                }
            });
    }

    public void sendPasswordResetEmail(String emailAddress, final PerpleSDKCallback callback) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
            callback.onFail(PerpleSDK.getErrorInfo(PerpleSDK.ERROR_FIREBASE_NOTINITIALIZED, "Firebase is not initialized."));
            return;
        }

        mAuth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (PerpleSDK.IsDebug) {
                            Log.d(LOG_TAG, "Firebase sendPasswordResetEmail success");
                        }
                        callback.onSuccess("");
                    } else {
                        String info = PerpleSDK.getErrorInfoFromFirebaseException(task.getException());
                        Log.e(LOG_TAG, "Firebase sendPasswordResetEmail fail - info:" + info);
                        callback.onFail(info);
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

    public void subscribeToTopic(String topic) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Firebase, Subscribe to news topic: " + topic);
        }
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public void unsubscribeFromTopic(String topic) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "Firebase, Unsubscribe to news topic: " + topic);
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    public void sendUpstreamMessage(String data){
        if (!mIsInit) {
            Log.e(LOG_TAG, "Firebase is not initialized.");
            return;
        }

        RemoteMessage.Builder build = new RemoteMessage.Builder(mGCMSenderId + "@gcm.googleapis.com");
        build.setMessageId(Integer.toString(sMsgId.incrementAndGet()));

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
            Log.e(LOG_TAG, "Firebase is not initialized.");
            return;
        }

        RemoteMessage.Builder build = new RemoteMessage.Builder(uniqueKey);
        build.setMessageId(Integer.toString(sMsgId.incrementAndGet()));

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

    private String getLoginInfo(FirebaseUser user) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("profile", getUserProfile(user));
            obj.put("prividerData", getPrividerSpecificInfo(user));
            obj.put("pushToken", getPushToken());

            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static JSONObject getUserProfile(FirebaseUser user) {
        JSONObject obj = new JSONObject();

        if (user != null) {
            try {

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getToken() instead.
                obj.put("uid", user.getUid());

                obj.put("name", user.getDisplayName());
                obj.put("email", user.getEmail());
                obj.put("photoUrl", user.getPhotoUrl());
                obj.put("providerId", user.getProviderId());
                //obj.put("providers", new JSONArray(user.getProviders()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    private static JSONArray getPrividerSpecificInfo(FirebaseUser user) {
        JSONArray array = new JSONArray();

        if (user != null) {
            try {
                for (UserInfo profile : user.getProviderData()) {
                    JSONObject obj = new JSONObject();

                    // Id of the provider (ex: google.com, facebook.com, firebase, email)
                    obj.put("providerId", profile.getProviderId());

                    obj.put("uid", profile.getUid());
                    obj.put("name", profile.getDisplayName());
                    obj.put("email", profile.getEmail());
                    obj.put("photoUrl", profile.getPhotoUrl());

                    array.put(obj);
                };
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;
    }

    public static boolean isLinkedSpecificProvider(String info, String provider) {
        try {
            JSONObject obj = new JSONObject(info);
            JSONArray array = (JSONArray)obj.get("prividerData");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject profile = (JSONObject)array.get(i);
                    if (profile != null && provider.equals(profile.getString("providerId"))) {
                        return true;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String addGoogleLoginInfo(String loginInfo) {
        try {
            JSONObject obj = new JSONObject(loginInfo);
            if (!obj.has("google")) {
                obj.put("google", PerpleSDK.getGoogle().getProfileData());
                return obj.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loginInfo;
    }

    public static String addFacebookLoginInfo(String loginInfo) {
        try {
            JSONObject obj = new JSONObject(loginInfo);
            if (!obj.has("facebook")) {
                obj.put("facebook", PerpleSDK.getFacebook().getProfileData());
                return obj.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loginInfo;
    }

    public JSONObject getPushToken() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("iid", getFCMiid());
            obj.put("token", getFCMtoken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public String getFCMiid() {
        return FirebaseInstanceId.getInstance().getId();
    }

    public String getFCMtoken() {
        return FirebaseInstanceId.getInstance().getToken();
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
}
