package com.perplelab;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import android.util.Log;

public class PerpleFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "PerpleSDK";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String instanceId = FirebaseInstanceId.getInstance().getId();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Instance id: " + instanceId);
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(instanceId, refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String iid, String token) {
        // Add custom implementation, as needed.
        PerpleSDK.onFCMTokenRefresh(iid, token);
    }
}
