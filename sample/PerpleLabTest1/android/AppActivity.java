/****************************************************************************
Copyright (c) 2008-2010 Ricardo Quesada
Copyright (c) 2010-2012 cocos2d-x.org
Copyright (c) 2011      Zynga Inc.
Copyright (c) 2013-2014 Chukong Technologies Inc.

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.lua;

import org.cocos2dx.lib.Cocos2dxActivity;
import com.perplelab.perplelabtest1.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

// @perplesdk
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

// @perplesdk
import com.perplelab.PerpleSDK;

// The name of .so is specified in AndroidMenifest.xml. NativityActivity will load it automatically for you.
// You can use "System.loadLibrary()" to load other .so files.

// @perplesdk
// Cocos2dxActivity is must be a child class of FragmentActivity !!!
public class AppActivity extends Cocos2dxActivity {

    // @perplesdk
    static {
        System.loadLibrary("perplesdk");
    }

    static String hostIPAdress = "0.0.0.0";

    // @billing
    static final String gameId = "5000";
    static final String billingBase64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA+jphc66lLw/3ZHdFuRnAXCNVaRbwvFMPAFoAmNMA5lvHfDIdCrBJL4g+VNXARsFqeW2sg0g4V7fzn4xsEQIu0bGVoyPEk7Hbw2YanlFSV9Ivqso1YiygbLS7uAdOCvTfqS0jqLYSbbT44fr9Nj+nBBcMcAlHSIw8wF6/PJh1BxEvMPpHM+V9wnN8wpJ8nq1JnoztpTt3jhxghwDSlnhf1n/JbNJb0LfwoPyhqnstNBTF3DEJCI74mq+ikhVn08F+Xd06lS57YQdkwQYFlyHty3r+JleJCffhtxFCE3LkshoH6FJE2uqJlogPVWkgWpwELE+IyECQqIkEb5XN1c8eFQIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (nativeIsLandScape()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        // Check the wifi is opened when the native is debug.
        if (nativeIsDebug())
        {
            if (!isWifiConnected())
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Warning");
                builder.setMessage("Open Wifi for debuging...");
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        finish();
                        System.exit(0);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        }

        hostIPAdress = getHostIpAddress();

        // @perplesdk
        PerpleSDK.createInstance(this);

        // @perplesdk, Lua 콜백을 GL Thread 에서 실행하고자 할 경우 설정한다.
        PerpleSDK.setGLSurfaceView(mGLSurfaceView);

        // 디버그 메시지 출력
        boolean isDebug = false;

        // @perplesdk
        if (PerpleSDK.getInstance().initSDK(gameId, getString(R.string.gcm_defaultSenderId), billingBase64PublicKey, isDebug)) {

            // firebase FCM 알림을 포그라운드 상태에서도 받고자 할 경우 true로 설정
            PerpleSDK.getInstance().setReceivePushOnForeground(false);

            // @google, Google 로그인만 사용할 경우
            //PerpleSDK.getInstance().initGoogle(getString(R.string.default_web_client_id));

            // @google, Google Play Services 기능(업적, 리더보드, 퀘스트)을 사용하고자 할 경우
            PerpleSDK.getInstance().initGoogle(getString(R.string.default_web_client_id), new PerpleBuildGoogleApiClient() {
                @Override
                public void onBuild(Builder builder) {
                    builder.addApi(Games.API).addScope(Games.SCOPE_GAMES);
                }
            });

            // @facebook
            PerpleSDK.getInstance().initFacebook(savedInstanceState);
        }

        // @naver, '네이버 아이디로 로그인'을 사용하고자 할 경우
        //PerpleSDK.getInstance().initNaver(getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name), isDebug);

        // @naver, '네이버카페SDK' 만 사용하고자 할 경우
        PerpleSDK.getInstance().initNaverCafe(getString(R.string.naver_client_id), getString(R.string.naver_client_secret), Integer.parseInt(getString(R.string.naver_cafe_id)));

        // @adbrix
        PerpleSDK.getInstance().initAdbrix();

        // @tapjoy, push 기능을 사용하고자 할 경우
        //PerpleSDK.getInstance().initTapjoy(getString(R.string.tapjoy_app_key), getString(R.string.gcm_defaultSenderId), isDebug);

        // @tapjoy
        PerpleSDK.getInstance().initTapjoy(getString(R.string.tapjoy_app_key), "", isDebug);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // @perplesdk
        PerpleSDK.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // @perplesdk
        PerpleSDK.getInstance().onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // @perplesdk
        PerpleSDK.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // @perplesdk
        PerpleSDK.getInstance().onPause();
    }

    @Override
    protected void onDestroy() {
        // @perplesdk
        PerpleSDK.getInstance().onDestroy();

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // @perplesdk
        PerpleSDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public String getHostIpAddress() {
        WifiManager wifiMgr = (WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return ((ip & 0xFF) + "." + ((ip >>>= 8) & 0xFF) + "." + ((ip >>>= 8) & 0xFF) + "." + ((ip >>>= 8) & 0xFF));
    }

    public static String getLocalIpAddress() {
        return hostIPAdress;
    }

    public static String getSDCardPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String strSDCardPathString = Environment.getExternalStorageDirectory().getPath();
            return strSDCardPathString;
        }
        return null;
    }

    private static native boolean nativeIsLandScape();
    private static native boolean nativeIsDebug();
}
